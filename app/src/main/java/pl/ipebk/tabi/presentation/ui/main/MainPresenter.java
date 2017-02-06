/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import javax.inject.Inject;

import pl.ipebk.tabi.BuildConfig;
import com.suredigit.inappfeedback.FeedbackClient;
import pl.ipebk.tabi.presentation.DatabaseLoader;
import pl.ipebk.tabi.presentation.localization.DemoGreetingPredicate;
import pl.ipebk.tabi.presentation.ui.base.BasePresenter;
import pl.ipebk.tabi.presentation.utils.Stopwatch;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;
import pl.ipebk.tabi.utils.PreferenceHelper;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter<MainMvpView> {
    private final DatabaseLoader databaseLoader;
    private Subscription loadSubscription;
    private Subscription responseSubscription;
    private Stopwatch stopwatch;
    private PreferenceHelper preferenceHelper;
    private DemoGreetingPredicate greetingPredicate;
    private FeedbackClient feedbackClient;

    @Inject public MainPresenter(DatabaseLoader databaseLoader, StopwatchManager stopwatchManager,
                                 PreferenceHelper preferenceHelper, DemoGreetingPredicate greetingPredicate,
                                 FeedbackClient feedbackClient) {
        this.databaseLoader = databaseLoader;
        this.stopwatch = stopwatchManager.getDefaultStopwatch();
        this.preferenceHelper = preferenceHelper;
        this.greetingPredicate = greetingPredicate;
        this.feedbackClient = feedbackClient;
    }

    @Override public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);

        getMvpView().showLoading();
        loadDatabase();
        feedbackClient.sendUnsentFeedback()
                      .subscribe(v -> {
                      }, error -> {
                          Timber.w(error, "Could not send feedback. Postponing");
                      }, () -> {
                          Timber.d("Success. Previously unsent feedback was just sent");
                      });
        responseSubscription = feedbackClient.getPendingResponses().subscribe(response -> {
            Timber.d("Response from developer: %s", response);
            getMvpView().showResponseToFeedback(response);
        }, error -> {
            Timber.e(error, "Problem getting pending responses");
        });
    }

    @Override public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(loadSubscription);
        RxUtil.unsubscribe(responseSubscription);
    }

    public void refreshView() {
        preferenceHelper.increaseMainScreenVisited();
        int mainScreenVisitedNumber = preferenceHelper.howManyTimesMainScreenVisited();

        if (greetingPredicate.shouldShowDemoGreeting()) {
            getMvpView().showDemoGreeting();
            greetingPredicate.markDemoGreetingShown();
        }

        // TODO: 2016-06-14 use better method than 1/3 entrances
        if (mainScreenVisitedNumber % 3 == 0) {
            getMvpView().showFeedbackCaption();
            Timber.d("Show feedback caption");
        } else {
            Timber.d("Show standard caption");
            getMvpView().showGreetingCaption();
        }
        getMvpView().showVersion(BuildConfig.VERSION_NAME);
    }

    private void loadDatabase() {
        stopwatch.reset();
        loadSubscription = databaseLoader
                .initDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                }, e -> {
                    Timber.e("Error initializing database");
                }, () -> {
                    getMvpView().hideLoading();
                    Timber.d("Initializing time: %s", stopwatch.getElapsedTimeString());
                });
    }

    public void goToSearch() {
        getMvpView().goToSearch(null);
    }
}
