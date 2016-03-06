/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.SqlBrite;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.base.BasePresenter;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.utils.Stopwatch;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SearchPresenter extends BasePresenter<SearchMvpView> {
    private static final long QUICK_LIST_RENDERING_DELAY = 300;
    private static final long FULL_LIST_RENDERING_DELAY = 0;
    private static final int FULLY_LOADED_VIEW_CONST = 2;

    private final DataManager dataManager;
    private final SpellCorrector spellCorrector;
    private Subscription searchSubscription;
    private Stopwatch stopwatch = new Stopwatch();
    private String lastSearched;

    @Inject public SearchPresenter(DataManager dataManager, SpellCorrector spellCorrector) {
        this.dataManager = dataManager;
        this.spellCorrector = spellCorrector;
    }

    @Override public void attachView(SearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }
        super.detachView();
    }

    //region public methods
    public void placeSelected(long placeId, String searchedPlate) {
        getMvpView().goToPlaceDetails(placeId, searchedPlate);
    }

    public void startInitialSearchForText(String searchText) {
        getMvpView().setSearchText(searchText);
        deepSearchForText(searchText);
    }

    public void loadInitialStateForPlaces() {
            getMvpView().showEmptyStateInPlacesSection();
    }

    public void loadInitialStateForPlates() {
            getMvpView().showEmptyStateInPlatesSection();
    }

    public void quickSearchForText(String rawPhrase) {
        searchForRawTextWithLimit(rawPhrase, 3, QUICK_LIST_RENDERING_DELAY);
    }

    public void deepSearchForText(String rawPhrase) {
        this.lastSearched = rawPhrase;
        searchForRawTextWithLimit(rawPhrase, null, FULL_LIST_RENDERING_DELAY);
        getMvpView().hideKeyboard();
    }
    //endregion

    //region Search
    private void searchForRawTextWithLimit(String rawPhrase, Integer limit, long delay) {
        checkViewAttached();
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }

        searchSubscription = Observable.just(rawPhrase)
                .subscribeOn(Schedulers.computation())
                .delay(delay, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(spellCorrector::cleanForSearch)
                .subscribe(s -> beginSearchForCleaned(limit, s),
                        e -> Timber.e("Error during searching for places", e));
    }

    private void beginSearchForCleaned(Integer limit, String s) {
        if (s == null || s.equals("")) {
            loadInitialStateForPlaces();
            loadInitialStateForPlates();
        } else {
            getMvpView().hideEmptyStateInPlacesSection();
            getMvpView().hideEmptyStateInPlatesSection();
            stopwatch.reset();

            getObservableForSearchWithinTwoQueries(s, limit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showSearchResults);
        }
    }

    private Observable<Pair<Cursor, Cursor>> getObservableForSearchWithinTwoQueries(String phrase, Integer limit) {
        Observable<Cursor> platesCursorObservable = dataManager.getDatabaseHelper()
                .getPlaceDao().getPlacesForPlateStart(phrase, limit);

        Observable<Cursor> placesCursorObservable = dataManager.getDatabaseHelper()
                .getPlaceDao().getPlacesByName(phrase, limit);

        return Observable.zip(platesCursorObservable,
                placesCursorObservable, Pair<Cursor, Cursor>::new);
    }

    private void showSearchResults(Pair<Cursor, Cursor> cursorCursorPair) {
        Cursor platesCursor = cursorCursorPair.first;
        Cursor placesCursor = cursorCursorPair.second;

        Timber.d("Search query took: %s", stopwatch.getElapsedTimeString());

        stopwatch.reset();
        if (platesCursor.getCount() > 0) {
            getMvpView().showPlacesInPlatesSection(platesCursor);
        } else {
            getMvpView().showEmptyStateInPlatesSection();
        }

        if (placesCursor.getCount() > 0) {
            getMvpView().showPlacesInPlacesSection(placesCursor);
        } else {
            getMvpView().showEmptyStateInPlacesSection();
        }

        Timber.d("Rendering layout took: %s", stopwatch.getElapsedTimeString());
    }
    //endregion
}
