/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.DatabaseLoader;
import pl.ipebk.tabi.presentation.ui.base.BasePresenter;
import pl.ipebk.tabi.utils.PreferenceHelper;
import pl.ipebk.tabi.utils.RxUtil;
import pl.ipebk.tabi.presentation.utils.Stopwatch;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter<MainMvpView> {
    private static final String ACTION_SHOW_LICENSES = "licenses";
    private static final String ACTION_GIVE_FEEDBACK = "rate";

    private final DatabaseLoader databaseLoader;
    private Subscription loadSubscription;
    private Stopwatch stopwatch;
    private PreferenceHelper preferenceHelper;
    private Context context;

    @Inject public MainPresenter(DatabaseLoader databaseLoader, StopwatchManager stopwatchManager,
                                 PreferenceHelper preferenceHelper, Context context) {
        this.databaseLoader = databaseLoader;
        this.stopwatch = stopwatchManager.getDefaultStopwatch();
        this.preferenceHelper = preferenceHelper;
        this.context = context;
    }

    @Override public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);

        getMvpView().showLoading();
        loadDatabase();
    }

    @Override public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(loadSubscription);
    }

    public void refreshView() {
        preferenceHelper.increaseMainScreenVisited();

        int mainScreenVisitedNumber = preferenceHelper.howManyTimesMainScreenVisited();

        // TODO: 2016-06-14 use better method than 1/3 entrances
        String caption;
        if (mainScreenVisitedNumber % 3 == 0) {
            caption = context.getString(R.string.main_doodle_caption_feedback);
            Timber.d("Show feedback caption");
        } else {
            Timber.d("Show standard caption");
            caption = context.getString(R.string.main_doodle_caption);
        }
        getMvpView().showCaption(caption);
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
                    loadCategories();
                    getMvpView().hideLoading();
                    Timber.d("Initializing time: %s", stopwatch.getElapsedTimeString());
                });
    }

    /**
     * Be careful. Main screen resources are loaded by reflection. Be sure tu name your new resources appropriately.
     * Check {@link MainScreenResourceFinder} and {@link MainListItem} for more info.
     */
    private void loadCategories() {
        List<MainListItem> items = new ArrayList<>();

        items.add(new MainListHeaderItem("browse"));

        items.add(new MainListElementItem("dolnoslaskie", "d"));
        items.add(new MainListElementItem("kujawskopomorskie", "c"));
        items.add(new MainListElementItem("lodzkie", "e"));
        items.add(new MainListElementItem("lubelskie", "l"));
        items.add(new MainListElementItem("lubuskie", "f"));
        items.add(new MainListElementItem("malopolskie", "k"));
        items.add(new MainListElementItem("mazowieckie", "w"));
        items.add(new MainListElementItem("opolskie", "o"));
        items.add(new MainListElementItem("podkarpackie", "r"));
        items.add(new MainListElementItem("podlaskie", "b"));
        items.add(new MainListElementItem("pomorskie", "g"));
        items.add(new MainListElementItem("slaskie", "s"));
        items.add(new MainListElementItem("swietokrzyskie", "t"));
        items.add(new MainListElementItem("warminskomazurskie", "n"));
        items.add(new MainListElementItem("wielkopolskie", "p"));
        items.add(new MainListElementItem("zachodniopomorskie", "z"));
        items.add(new MainListElementItem("sluzbybezpieczenstwa", "h"));
        items.add(new MainListElementItem("tablicewojskowe", "u"));

        items.add(new MainListHeaderItem("about_app"));

        items.add(new MainListElementItem("licenses", ACTION_SHOW_LICENSES));
        items.add(new MainListElementItem("rate", ACTION_GIVE_FEEDBACK));

        getMvpView().showCategories(items);
    }

    public void goToSearch() {
        getMvpView().goToSearch(null);
    }

    public void showCategoryForAction(String action) {
        if(ACTION_SHOW_LICENSES.equals(action)) {
            getMvpView().goToAboutAppPage();
        } else if(ACTION_GIVE_FEEDBACK.equals(action)) {
            getMvpView().showFeedbackDialog();
        } else {
            Timber.d("Menu item clicked has literal as action");
            getMvpView().goToSearch(action);
        }
    }
}
