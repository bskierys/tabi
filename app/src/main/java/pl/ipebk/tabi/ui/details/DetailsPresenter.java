/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.util.Locale;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private DataManager dataManager;
    private Context context;
    private Place place;
    private String searchedPlate;

    @Inject public DetailsPresenter(DataManager dataManager, Activity activity) {
        this.dataManager = dataManager;
        this.context = activity;
    }

    @Override public void attachView(DetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        super.detachView();
    }

    public void loadPlace(long id, String searchedPlate, SearchType searchType,
                          Observable<Integer> mapWidthStream, Observable<Integer> mapHeightStream) {

        getMvpView().disableActionButtons();
        getMvpView().showSearchedText(searchedPlate);

        if (searchedPlate != null && searchType == SearchType.PLATE) {
            this.searchedPlate = searchedPlate.toUpperCase();
        }

        Observable<Place> placeStream = dataManager
                .getDatabaseHelper().getPlaceDao()
                .getByIdObservable(id);

        Observable<Place> standardPlaceStream = placeStream
                .filter(p -> p.getType() != Place.Type.SPECIAL);

        standardPlaceStream.subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(this::showStandardPlace);

        Observable<Uri> loadMapStream = Observable
                .combineLatest(standardPlaceStream, mapWidthStream.filter(w -> w > 0),
                               mapHeightStream.filter(h -> h > 0), this::getMapUrl);

        loadMapStream.subscribeOn(Schedulers.computation())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(uri -> getMvpView().showMap(uri));

        Observable<Place> specialPlaceStream = placeStream
                .filter(p -> p.getType() == Place.Type.SPECIAL);

        specialPlaceStream.subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(this::showSpecialPlace);
    }

    // TODO: 2016-02-27 same method as in search rows
    private void showStandardPlace(Place place) {
        this.place = place;

        getMvpView().enableActionButtons();

        Observable<Place> placeStream = Observable.just(place);

        placeStream.map(p -> p.getPlateMatchingPattern(searchedPlate))
                   .filter(p -> p != null).map(Plate::toString)
                   .subscribeOn(Schedulers.computation())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(plateText -> getMvpView().showPlate(plateText));

        placeStream.map(p -> getAdditionalInfo(place))
                   .subscribeOn(Schedulers.computation())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(additionalText -> getMvpView().showAdditionalInfo(additionalText));

        placeStream.subscribeOn(Schedulers.computation())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(p -> {
                       getMvpView().showPlaceName(p.getName());
                       getMvpView().showVoivodeship(context.getString(R.string.details_voivodeship) + " " + p
                               .getVoivodeship());
                       getMvpView().showPowiat(context.getString(R.string.details_powiat) + " " + p.getPowiat());
                       getMvpView().showGmina(context.getString(R.string.details_gmina) + " " + p.getGmina());
                   });
    }

    private void showSpecialPlace(Place place) {
        this.place = place;
        getMvpView().showPlaceName(place.getName());
        getMvpView().showPlaceHolder();

        Observable<Place> placeStream = Observable.just(place);

        placeStream.map(p -> p.getPlateMatchingPattern(searchedPlate))
                   .filter(p -> p != null).map(Plate::toString)
                   .subscribeOn(Schedulers.computation())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(plateText -> getMvpView().showPlate(plateText));
    }

    @NonNull private String getAdditionalInfo(Place place) {
        String placeType = "";
        if (place.getType().ordinal() < Place.Type.PART_OF_TOWN.ordinal()) {
            placeType = context.getString(R.string.details_additional_town);
        } else if (place.getType() == Place.Type.PART_OF_TOWN) {
            placeType = context.getString(R.string.details_additional_part_of_town) + " " + place.getGmina();
        } else if (place.getType() == Place.Type.VILLAGE) {
            placeType = context.getString(R.string.details_additional_village);
        }

        String otherPlates = "";
        if (place.getPlates().size() > 1) {
            otherPlates = ", " + context.getString(R.string.details_additional_other_plates) + ": "
                    + place.platesToStringExceptMatchingPattern(searchedPlate);
        }

        return placeType + otherPlates;
    }

    public void showMoreForVoivodeship() {
        Character firstLetter = place.getMainPlate().getPattern().charAt(0);
        getMvpView().goToSearchForPhrase(firstLetter.toString());
    }

    public void showOnMap() {
        String placeName = place + "," + context.getString(R.string.details_country);
        String rawUri = "geo:0,0?q=" + placeName;

        getMvpView().startMap(Uri.parse(rawUri));
    }

    public void searchInGoogle() {
        getMvpView().startWebSearch(place + "," + context.getString(R.string.details_country));
    }

    private Uri getMapUrl(Place place, int width, int height) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();

        int scale = getScale(metrics.density);

        int widthInDp = (int) (width / metrics.density);
        int heightInDp = (int) (height / metrics.density);

        String size = String.format(Locale.getDefault(), "%dx%d", widthInDp, heightInDp);
        String language = Locale.getDefault().getLanguage();
        String placeName = place + "," + context.getString(R.string.details_country);

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

    /**
     * Densities for android are ldpi -> 0.75, mdpi -> 1.0, hdpi -> 1.5, xhdpi -> 2.0, xxhdpi -> 3.0, xxxhdpi -> 4.0.
     * Scale for map should match these values. Unfortunately non-premium users can only scale up to 2, so we use this
     * method as computing helper.
     *
     * @param density Android pixel density
     * @return Google static maps api scale
     */
    private int getScale(float density) {
        if (density < 2.0f) {
            return 1;
        } else {
            return 2;
        }
    }
}
