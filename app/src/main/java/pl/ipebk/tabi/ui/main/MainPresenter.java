/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import javax.inject.Inject;

import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import pl.ipebk.tabi.utils.Stopwatch;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter<MainMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;
    private Stopwatch stopwatch = new Stopwatch();

    @Inject public MainPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().showLoading();
        loadDatabase();
    }

    @Override public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void loadDatabase() {
        stopwatch.reset();
        subscription = dataManager.initDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                }, e -> {
                    Timber.e("Error initializing database");
                    getMvpView().showError("Error initializing database" + e.getMessage());
                }, () -> {
                    getMvpView().hideLoading();
                    getMvpView().showTime(stopwatch.getElapsedTimeString());
                    Timber.d("Initializing time: %s", stopwatch.getElapsedTimeString());
                });
    }

    public void goToSearch() {
        getMvpView().goToSearch();
    }
}
