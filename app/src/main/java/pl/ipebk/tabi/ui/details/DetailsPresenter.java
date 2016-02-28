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
import android.util.DisplayMetrics;

import java.util.Locale;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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

    public void loadPlace(long id, String searchedPlate) {
        if (searchedPlate != null) {
            this.searchedPlate = searchedPlate.toUpperCase();
        }

        dataManager.getDatabaseHelper().getPlaceDao()
                .getByIdObservable(id)
                .mapToOne(cursor ->
                        dataManager.getDatabaseHelper()
                                .getPlaceDao().getTable()
                                .cursorToModel(cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showPlace);
    }

    // TODO: 2016-02-27 same method as in search rows
    // TODO: 2016-02-28 refactor
    private void showPlace(Place place) {
        this.place = place;

        Plate plate = null;
        int searchedPlateIndex = 0;
        if (searchedPlate == null) {
            plate = place.getMainPlate();
        } else {
            int i = 0;
            while (plate == null && i < place.getPlates().size()) {
                if (place.getPlates().get(i).getPattern().startsWith(searchedPlate)) {
                    plate = place.getPlates().get(i);
                    searchedPlateIndex = i;
                }
                i++;
            }
        }

        // TODO: 2016-02-28 finding plate move to place model
        if (plate == null) {
            Timber.e("Cannot find plate");
            return;
        }

        String additionalText = "";
        if (place.getType().ordinal() < Place.Type.PART_OF_TOWN.ordinal()) {
            additionalText += "miasto";
        } else if (place.getType() == Place.Type.PART_OF_TOWN) {
            additionalText += "część miasta " + place.getGmina();
        } else if (place.getType() == Place.Type.VILLAGE) {
            additionalText += "wioska";
        }

        if (place.getPlates().size() > 1) {
            additionalText += ", inne tablice: ";
            for (int i = 0; i < place.getPlates().size(); i++) {
                if (i != searchedPlateIndex) {
                    additionalText += place.getPlates().get(i).toString();
                    if (i != place.getPlates().size() - 1) {
                        additionalText += ", ";
                    }
                }
            }
        }

        getMvpView().showPlaceName(place.getName());
        getMvpView().showSearchedPlate(plate.toString());
        getMvpView().showVoivodeship(place.getVoivodeship());
        getMvpView().showPowiat(place.getPowiat());
        getMvpView().showGmina(place.getGmina());
        getMvpView().showAdditionalInfo(additionalText);

        loadMap();
    }

    // TODO: 2016-02-27 move methods from presenter to dataManager
    private void loadMap() {
        String language = Locale.getDefault().getLanguage();
        String placeName = (place.toString() + "," + context.getString(R.string.maps_country))
                .replace(" ", "+");
        // TODO: 2016-02-28 find optimal image size programatically
        Uri googleApiUri = new Uri.Builder().scheme("http")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("staticmap")
                .appendQueryParameter("center", placeName)
                .appendQueryParameter("zoom", Integer.toString(9))
                .appendQueryParameter("size", "600x300")
                .appendQueryParameter("maptype", "roadmap")
                .appendQueryParameter("language",language)
                .build();
        getMvpView().showMap(googleApiUri);
    }

    public float convertPixelsToDp(float px){
        //http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
