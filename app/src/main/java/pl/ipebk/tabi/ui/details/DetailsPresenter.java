/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import javax.inject.Inject;

import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private DataManager dataManager;

    @Inject public DetailsPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
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
                .subscribe(place -> getMvpView().showPlace(place));
    }
}
