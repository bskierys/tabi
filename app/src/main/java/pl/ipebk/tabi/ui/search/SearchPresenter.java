/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;
import android.support.v4.util.Pair;

import java.util.Calendar;

import javax.inject.Inject;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
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
    public static final Integer SEARCH_QUANTITY_QUICK = 3;
    public static final Integer SEARCH_QUANTITY_FULL = null;
    private static final int SEARCH_TYPE_QUICK = 28;
    private static final int SEARCH_TYPE_FULL = 82;
    private static final int HISTORY_SEARCH_NUMBER = 3;

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
    public void placeSelected(long placeId, String searchedPlate, String plateClicked, SearchType searchType) {
        getMvpView().goToPlaceDetails(placeId, searchedPlate, searchType);

        // TODO: 2016-04-11 unit tests

        Observable.just(new SearchHistory())
                  .doOnNext(history -> history.setPlaceId(placeId))
                  .doOnNext(history -> history.setPlate(plateClicked))
                  .doOnNext(history -> history.setSearchType(searchType))
                  .doOnNext(history -> history.setTimeSearched(Calendar.getInstance().getTime()))
                  .observeOn(Schedulers.io())
                  .subscribe(history -> dataManager.getDatabaseHelper().getSearchHistoryDao().updateOrAdd(history));
    }

    public void startInitialSearchForText(String searchText) {
        getMvpView().setSearchText(searchText);
        deepSearchForText(searchText);
    }

    public void refreshSearch(){
        if(lastSearched == null || lastSearched.equals("")){
            loadInitialStateForPlaces();
            loadInitialStateForPlates();
        }
    }

    public void loadInitialStateForPlaces() {
        Stopwatch historyWatch = new Stopwatch();
        historyWatch.reset();
        dataManager.getDatabaseHelper().getPlaceDao().getHistoryPlaces(HISTORY_SEARCH_NUMBER, SearchType.PLACE)
                   .filter(cursor -> cursor != null).first()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .doOnNext(cursor -> getMvpView().hideEmptyStateInPlacesSection())
                   .subscribe(cursor -> {
                       getMvpView().showInitialSearchInPlacesSection(cursor);
                       Timber.d("Loading search history for places took: %s", historyWatch.getElapsedTimeString());
                   });
    }

    public void loadInitialStateForPlates() {
        Stopwatch historyWatch = new Stopwatch();
        historyWatch.reset();
        dataManager.getDatabaseHelper().getPlaceDao().getHistoryPlaces(HISTORY_SEARCH_NUMBER, SearchType.PLATE)
                   .filter(cursor -> cursor != null).first()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .doOnNext(cursor -> getMvpView().hideEmptyStateInPlatesSection())
                   .subscribe(cursor -> {
                       getMvpView().showInitialSearchInPlatesSection(cursor);
                       Timber.d("Loading search history for plates took: %s", historyWatch.getElapsedTimeString());
                   });
    }

    public void quickSearchForText(String rawPhrase) {
        searchForRawTextWithLimit(rawPhrase, SEARCH_QUANTITY_QUICK, SEARCH_TYPE_QUICK);
    }

    public void deepSearchForText(String rawPhrase) {
        searchForRawTextWithLimit(rawPhrase, SEARCH_QUANTITY_FULL, SEARCH_TYPE_FULL);
        getMvpView().hideKeyboard();
    }
    //endregion

    //region Search
    private void searchForRawTextWithLimit(String rawPhrase, Integer limit, int searchType) {
        checkViewAttached();
        if (searchSubscription != null) {
            searchSubscription.unsubscribe();
        }

        searchSubscription = Observable.just(rawPhrase)
                                       .subscribeOn(Schedulers.computation())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .map(spellCorrector::cleanForSearch)
                                       .doOnNext(cleanedText->lastSearched = cleanedText)
                                       .subscribe(s -> beginSearchForCleaned(limit, s, searchType),
                                                  e -> Timber.e("Error during searching for places", e));
    }

    private void beginSearchForCleaned(Integer limit, String s, int searchType) {
        if (s == null || s.equals("")) {
            loadInitialStateForPlaces();
            loadInitialStateForPlates();
        } else {
            stopwatch.reset();

            getObservableForSearchWithinTwoQueries(s, limit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(results -> showSearchResults(results, searchType));
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

    private void showSearchResults(Pair<Cursor, Cursor> cursorCursorPair, int searchType) {
        Cursor platesCursor = cursorCursorPair.first;
        Cursor placesCursor = cursorCursorPair.second;

        Timber.d("Search query took: %s", stopwatch.getElapsedTimeString());

        stopwatch.reset();
        if (platesCursor.getCount() > 0) {
            getMvpView().hideEmptyStateInPlatesSection();
            if (searchType == SEARCH_TYPE_QUICK) {
                getMvpView().showBestSearchInPlatesSection(platesCursor);
            } else if (searchType == SEARCH_TYPE_FULL) {
                getMvpView().showFullSearchInPlatesSection(platesCursor);
            }
        } else {
            getMvpView().showEmptyStateInPlatesSection();
        }

        if (placesCursor.getCount() > 0) {
            getMvpView().hideEmptyStateInPlacesSection();
            if (searchType == SEARCH_TYPE_QUICK) {
                getMvpView().showBestSearchInPlacesSection(placesCursor);
            } else if (searchType == SEARCH_TYPE_FULL) {
                getMvpView().showFullSearchInPlacesSection(placesCursor);
            }
        } else {
            getMvpView().showEmptyStateInPlacesSection();
        }

        Timber.d("Rendering layout took: %s", stopwatch.getElapsedTimeString());
    }
    //endregion
}
