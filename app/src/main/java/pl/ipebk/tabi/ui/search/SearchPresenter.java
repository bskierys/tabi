/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.SqlBrite;

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
    private final DataManager dataManager;
    private final SpellCorrector spellCorrector;
    private Subscription searchSubscription;
    private Stopwatch stopwatch = new Stopwatch();

    @Inject public SearchPresenter(DataManager dataManager, SpellCorrector spellCorrector) {
        this.dataManager = dataManager;
        this.spellCorrector = spellCorrector;
    }

    @Override public void attachView(SearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        super.detachView();
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }
    }

    //region public methods
    public void placeSelected(long placeId) {
        getMvpView().goToPlaceDetails(placeId);
    }

    public void loadInitialStateForPlaces() {
        getMvpView().showEmptyStateInPlacesSection();
    }

    public void loadInitialStateForPlates() {
        getMvpView().showEmptyStateInPlatesSection();
    }

    public void quickSearchForText(String rawPhrase) {
        searchForRawTextWithLimit(rawPhrase, 3);
    }

    public void deepSearchForText(String rawPhrase) {
        searchForRawTextWithLimit(rawPhrase, null);
        getMvpView().hideKeyboard();
    }
    //endregion

    //region Search
    private void searchForRawTextWithLimit(String rawPhrase, Integer limit) {
        checkViewAttached();
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }

        searchSubscription = Observable.just(rawPhrase)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(spellCorrector::cleanForSearch)
                .doOnUnsubscribe(() -> getMvpView().hideLoading())
                .subscribe(s -> beginSearchForCleaned(limit, s));
    }

    private void beginSearchForCleaned(Integer limit, String s) {
        if (s == null || s.equals("")) {
            loadInitialStateForPlaces();
            loadInitialStateForPlates();
        } else {
            getMvpView().hideEmptyStateInPlacesSection();
            getMvpView().hideEmptyStateInPlatesSection();
            getMvpView().showLoading();
            stopwatch.reset();

            getObservableForSearchWithinTwoQueries(s, limit)
                    .subscribe(this::showSearchResults);
        }
    }

    private Observable<Pair<Cursor, Cursor>> getObservableForSearchWithinTwoQueries(String phrase, Integer limit) {
        Observable<Cursor> platesCursorObservable = dataManager.getDatabaseHelper()
                .getPlaceDao().getPlacesForPlateStart(phrase, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(SqlBrite.Query::run);

        Observable<Cursor> placesCursorObservable = dataManager.getDatabaseHelper()
                .getPlaceDao().getPlacesByName(phrase, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(SqlBrite.Query::run);

        return Observable.zip(platesCursorObservable,
                placesCursorObservable, Pair<Cursor, Cursor>::new);
    }

    private void showSearchResults(Pair<Cursor, Cursor> cursorCursorPair) {
        Cursor platesCursor = cursorCursorPair.first;
        Cursor placesCursor = cursorCursorPair.second;

        getMvpView().hideLoading();

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

        Timber.d("Search query took: %s", stopwatch.getElapsedTimeString());
    }
    //endregion
}
