/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.net.Uri;

import java.util.Locale;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.place.LicensePlate;
import pl.ipebk.tabi.domain.place.Place;
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.ui.base.BasePresenter;
import pl.ipebk.tabi.ui.search.PlaceListItemType;
import pl.ipebk.tabi.utils.DeviceHelper;
import pl.ipebk.tabi.utils.NameFormatHelper;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private PlaceRepository repository;
    // TODO: 2016-12-06 should not use domain model? rather view dto
    private Observable<Place> placeOnce;
    private BehaviorSubject<Place> placeSubject;
    private String searchedPlate;

    private NameFormatHelper nameFormatHelper;
    private DeviceHelper deviceHelper;

    private Subscription loadMapSubscription;
    private Subscription loadPlaceSubscription;

    @Inject public DetailsPresenter(PlaceRepository repository, DeviceHelper deviceHelper, NameFormatHelper
            nameFormatHelper) {
        this.repository = repository;
        this.deviceHelper = deviceHelper;
        this.nameFormatHelper = nameFormatHelper;
    }

    @Override public void attachView(DetailsMvpView mvpView) {
        super.attachView(mvpView);
        this.placeSubject = BehaviorSubject.create();
        this.placeOnce = placeSubject.asObservable();
    }

    @Override public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(loadMapSubscription);
        RxUtil.unsubscribe(loadPlaceSubscription);
    }

    public void loadPlace(long id, String searchedPlate, SearchType searchType, PlaceListItemType itemType) {
        getMvpView().disableActionButtons();
        getMvpView().showSearchedText(searchedPlate);
        showPlaceIconBasedOnItemType(itemType);

        if (searchedPlate != null && searchType == SearchType.LICENSE_PLATE) {
            this.searchedPlate = searchedPlate.toUpperCase();
        }

        // TODO: 2016-12-10 onError here
        loadPlaceSubscription = repository.loadByIdObservable(new AggregateId(id))
                                          .subscribe(placeSubject::onNext);

        Observable<Place> standardPlaceStream = placeOnce.filter(p -> p.getType() != PlaceType.SPECIAL);
        standardPlaceStream.subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(this::showStandardPlace, error -> {
                               Timber.e(error, "Could not load place in details");
                           });

        loadMapSubscription = Observable.combineLatest(
                standardPlaceStream,
                getMvpView().getMapWidthStream().filter(w -> w > 0),
                getMvpView().getMapHeightStream().filter(h -> h > 0),
                this::getMapUrl).subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(uri -> getMvpView().showMap(uri),
                                                   error -> {
                                                       Timber.e(error, "Could not load map in details");
                                                       getMvpView().showMapError();
                                                   });

        Observable<Place> specialPlaceStream = placeOnce.filter(p -> p.getType() == PlaceType.SPECIAL);
        specialPlaceStream.subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(this::showSpecialPlace, error -> {
                              Timber.e(error, "Could not show special place in details");
                          });
    }

    private void showPlaceIconBasedOnItemType(PlaceListItemType itemType) {
        int iconResId = R.drawable.ic_doodle_search;

        if (itemType == PlaceListItemType.HISTORICAL) {
            iconResId = R.drawable.ic_doodle_history;
        } else if (itemType == PlaceListItemType.RANDOM) {
            iconResId = R.drawable.ic_doodle_random;
        }

        getMvpView().showPlaceIcon(iconResId);
    }

    private void showStandardPlace(Place place) {
        LicensePlate plate = place.getPlateMatchingPattern(searchedPlate);
        if (plate != null) {
            getMvpView().showPlate(plate.toString());
        }

        getMvpView().enableActionButtons();
        getMvpView().showAdditionalInfo(nameFormatHelper.formatAdditionalInfo(place, searchedPlate));
        getMvpView().showPlaceName(place.getName());
        getMvpView().showVoivodeship(nameFormatHelper.formatVoivodeship(place.getVoivodeship()));
        getMvpView().showPowiat(nameFormatHelper.formatPowiat(place.getPowiat()));
        getMvpView().showGmina(nameFormatHelper.formatGmina(place.getGmina()));
    }

    private void showSpecialPlace(Place place) {
        LicensePlate plate = place.getPlateMatchingPattern(searchedPlate);
        if (plate != null) {
            getMvpView().showPlate(plate.toString());
        }

        getMvpView().showPlaceName(place.getName());
        getMvpView().showPlaceHolder();
    }

    public void showOnMap() {
        placeOnce.map(place -> nameFormatHelper.formatPlaceToSearch(place))
                 .map(placeName -> "geo:0,0?q=" + placeName)
                 .map(Uri::parse)
                 .subscribe(uri -> getMvpView().startMapApp(uri)
                         , error -> Timber.e(error, "Problem processing map uri"));
    }

    public void searchInGoogle() {
        placeOnce.map(place -> nameFormatHelper.formatPlaceToSearch(place))
                 .subscribe(searchData -> getMvpView().startWebSearch(searchData),
                            error -> Timber.e(error, "Error loading google search"));
    }

    public void copyToClipboard() {
        placeOnce.map(place -> nameFormatHelper.formatPlaceInfo(place))
                 .subscribe(placeInfo -> {
                     deviceHelper.copyToClipBoard(placeInfo);
                     getMvpView().showInfoMessageCopied();
                 }, ex -> Timber.e("Could not copy to clipboard"));
    }

    private Uri getMapUrl(Place place, int width, int height) {
        int scale = deviceHelper.getMapScale();

        String size = String.format(Locale.getDefault(), "%dx%d", width, height);
        String language = Locale.getDefault().getLanguage();
        String placeName = place + "," + getMvpView().getLocalizedPoland();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("maps.googleapis.com");
        builder.appendPath("maps");
        builder.appendPath("api");
        builder.appendPath("staticmap");
        builder.appendQueryParameter("center", placeName);
        builder.appendQueryParameter("zoom", Integer.toString(9));
        builder.appendQueryParameter("scale", Integer.toString(scale));
        builder.appendQueryParameter("size", size);
        builder.appendQueryParameter("maptype", "roadmap");
        builder.appendQueryParameter("language", language);

        return builder.build();
    }
}
