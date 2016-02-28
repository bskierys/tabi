/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.app.Activity;
import android.content.Context;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private DataManager dataManager;
    private Context context;

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

    public void loadPlace(long id) {
        dataManager.getDatabaseHelper().getPlaceDao()
                .getByIdObservable(id)
                .mapToOne(cursor ->
                        dataManager.getDatabaseHelper()
                                .getPlaceDao().getTable()
                                .cursorToModel(cursor))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(place -> {
                    getMvpView().showPlace(place);
                    loadMap(place);
                });
    }

    // TODO: 2016-02-27 move methods from presenter to dataManager
    private void loadMap(Place place) {
        String googleApiUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
                + "Mielec,mielecki,podkarpackie,polska&zoom=13&size=600x300&maptype=roadmap"
                + "&key=" + context.getString(R.string.config_maps_api_key);
        String fakeUrl = "http://stressfree.pl/wp-content/uploads/2012/05/nasze_makaron.jpg";
        getMvpView().showMap(fakeUrl);
    }
}
