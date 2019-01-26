/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.net.Uri;

import java.util.Locale;

import javax.inject.Inject;

import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.place.LicensePlateDto;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.presentation.model.place.PlaceFactory;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BasePresenter;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private static final String POLISH_LOCALE_CODE = "pl";
    private static final String GOOGLE_CLOUD_API_KEY = BuildConfig.GCApiKey;

    private PlaceRepository repository;
    private Observable<Place> placeOnce;
    private BehaviorSubject<Place> placeSubject;
    private String searchedPlate;

    private PlaceFactory placeFactory;
    private ClipboardCopyMachine clipboardCopyMachine;
    private MapScaleCalculator mapScaleCalculator;

    private Subscription loadMapSubscription;
    private Subscription loadPlaceSubscription;

    @Inject public DetailsPresenter(PlaceRepository repository, ClipboardCopyMachine clipboardCopyMachine,
                                    MapScaleCalculator mapScaleCalculator, PlaceFactory placeFactory) {
        this.repository = repository;
        this.clipboardCopyMachine = clipboardCopyMachine;
        this.placeFactory = placeFactory;
        this.mapScaleCalculator = mapScaleCalculator;
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
        showPlaceIconBasedOnItemType(itemType);

        if (searchedPlate != null && searchType == SearchType.LICENSE_PLATE) {
            this.searchedPlate = searchedPlate.toUpperCase();
        }

        loadPlaceSubscription = repository.loadByIdObservable(new AggregateId(id))
                                          .map(placeFactory::createFromDto)
                                          .subscribe(placeSubject::onNext,
                                                     error -> Timber.e("Could not load place in details"));

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
        LicensePlateDto plate = place.getPlateMatchingPattern(searchedPlate);
        if (plate != null) {
            getMvpView().showPlate(plate.toString());
        }

        getMvpView().preloadWebSearch(place.getSearchPhrase());
        getMvpView().enableActionButtons();
        getMvpView().showAdditionalInfo(place.getAdditionalInfo(searchedPlate));
        getMvpView().showPlaceName(place.getName());
        getMvpView().showVoivodeship(place.getVoivodeship());
        getMvpView().showPowiat(place.getPowiat());
        getMvpView().showGmina(place.getGmina());
    }

    private void showSpecialPlace(Place place) {
        LicensePlateDto plate = place.getPlateMatchingPattern(searchedPlate);
        if (plate != null) {
            getMvpView().showPlate(plate.toString());
        }

        getMvpView().showPlaceName(place.getName());
        getMvpView().showPlaceHolder();
    }

    public void showOnMap() {
        placeOnce.map(Place::getSearchPhrase)
                 .map(placeName -> "geo:0,0?q=" + placeName)
                 .map(Uri::parse)
                 .subscribe(uri -> getMvpView().startMapApp(uri)
                         , error -> Timber.e(error, "Problem processing map uri"));
    }

    public void searchInGoogle() {
        placeOnce.map(Place::getSearchPhrase)
                 .subscribe(searchData -> getMvpView().startWebSearch(searchData),
                            error -> Timber.e(error, "Error loading google search"));
    }

    public void copyToClipboard() {
        placeOnce.map(Place::getFullInfo)
                 .subscribe(placeInfo -> {
                     clipboardCopyMachine.copyToClipBoard(placeInfo);
                     getMvpView().showInfoMessageCopied();
                 }, ex -> Timber.e("Could not copy to clipboard"));
    }

    private Uri getMapUrl(Place place, int width, int height) {
        int scale = mapScaleCalculator.getMapScale();

        String size = String.format(Locale.getDefault(), "%dx%d", width, height);
        String placeName = place.getSearchPhrase();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("maps.googleapis.com");
        builder.appendPath("maps");
        builder.appendPath("api");
        builder.appendPath("staticmap");
        builder.appendQueryParameter("center", placeName);
        builder.appendQueryParameter("zoom", Integer.toString(9));
        builder.appendQueryParameter("scale", Integer.toString(scale));
        builder.appendQueryParameter("size", size);
        builder.appendQueryParameter("maptype", "roadmap");
        builder.appendQueryParameter("language", POLISH_LOCALE_CODE);
        builder.appendQueryParameter("key", GOOGLE_CLOUD_API_KEY);

        return builder.build();
    }
}
