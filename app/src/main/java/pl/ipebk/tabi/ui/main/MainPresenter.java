/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import pl.ipebk.tabi.utils.PreferenceHelper;
import pl.ipebk.tabi.utils.RxUtil;
import pl.ipebk.tabi.utils.Stopwatch;
import pl.ipebk.tabi.utils.StopwatchManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter<MainMvpView> {
    public static final String FEEDBACK_API_KEY = "AF-8DE6899E68E4-F6";

    private final DataManager dataManager;
    private Subscription loadSubscription;
    private Stopwatch stopwatch;
    private PreferenceHelper preferenceHelper;
    private Context context;

    @Inject public MainPresenter(DataManager dataManager, StopwatchManager stopwatchManager,
                                 PreferenceHelper preferenceHelper, Context context) {
        this.dataManager = dataManager;
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
    }

    // TODO: 2016-05-28 presenter lifecycle
    private void loadDatabase() {
        stopwatch.reset();
        loadSubscription = dataManager
                .initDatabase()
                .map(v -> preloadHistory())
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

    private Observable<Cursor> preloadHistory() {
        return dataManager.getDatabaseHelper().getPlaceDao().getHistoryPlaces(3, SearchType.PLACE);
    }

    private void loadCategories() {

        // TODO: 2016-05-26 move actions to constants
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

        items.add(new MainListElementItem("licencje", "1"));
        items.add(new MainListElementItem("ocen", "2"));

        items.add(new MainListHeaderItem("none"));

        getMvpView().showCategories(items);
    }

    public void goToSearch() {
        getMvpView().goToSearch(null);
    }

    public void menuItemClicked(String action) {
        try {
            // TODO: 2016-06-14 better action click handling
            int actionId = Integer.parseInt(action);
            Timber.d("Menu item clicked has numeral as action. This number is: %d", actionId);
            if (actionId == 2) {
                Timber.d("Showing feedback dialog for API key: %s", FEEDBACK_API_KEY);
                getMvpView().showFeedbackDialog();
            } else {
                getMvpView().prompt("Not implemented yet");
            }
        } catch (NumberFormatException e) {
            Timber.d("Menu item clicked has literal as action");
            getMvpView().goToSearch(action);
        }
    }
}
