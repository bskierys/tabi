/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.Calendar;

import javax.inject.Inject;

import pl.ipebk.tabi.domain.searchhistory.SearchHistory;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.ui.base.BasePresenter;
import pl.ipebk.tabi.utils.RxUtil;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.utils.Stopwatch;
import pl.ipebk.tabi.utils.StopwatchManager;
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

    private final SpellCorrector spellCorrector;
    private StopwatchManager stopwatchManager;
    private Stopwatch stopwatch;
    private Subscription searchSubscription;
    private String lastSearched;
    private SearchHistoryRepository historyRepository;
    private SearchHistoryFinder historyFinder;
    private PlaceFinder placeFinder;
    private LicensePlateFinder plateFinder;
    private SearchHistoryFactory historyFactory;

    @Inject public SearchPresenter(SearchHistoryRepository searchRepository, SearchHistoryFinder historyFinder,
                                   PlaceFinder placeFinder, LicensePlateFinder plateFinder,
                                   SpellCorrector spellCorrector, StopwatchManager stopwatchManager,
                                   SearchHistoryFactory factory) {
        this.historyRepository = searchRepository;
        this.placeFinder = placeFinder;
        this.historyFinder = historyFinder;
        this.plateFinder = plateFinder;
        this.spellCorrector = spellCorrector;
        this.stopwatchManager = stopwatchManager;
        this.stopwatch = stopwatchManager.getStopwatch();
        this.historyFactory = factory;
    }

    @Override public void attachView(SearchMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        RxUtil.unsubscribe(searchSubscription);
        super.detachView();
    }

    //region public methods
    public void placeSelected(long placeId, String searchedPlate, String plateClicked,
                              SearchType searchType, PlaceListItemType itemType) {
        getMvpView().goToPlaceDetails(placeId, searchedPlate, searchType, itemType);

        Observable.just(historyFactory.create(placeId, plateClicked, searchType))
                  .observeOn(Schedulers.io())
                  .subscribe(historyRepository::save, ex -> {
                      Timber.e(ex, "Problem saving history to database");
                  });
    }

    public void startInitialSearchForText(String searchText) {
        getMvpView().setSearchText(searchText);
        deepSearchForText(searchText);
    }

    public void refreshSearch() {
        if (lastSearched == null || lastSearched.equals("")) {
            loadInitialStateForPlaces();
            loadInitialStateForPlates();
        }
    }

    public void clearSearch() {
        lastSearched = null;
        getMvpView().setSearchText(null);
        loadInitialStateForPlaces();
        loadInitialStateForPlates();
    }

    public void loadInitialStateForPlaces() {
        Stopwatch historyWatch = stopwatchManager.getStopwatch();
        historyWatch.reset();
        historyFinder.findHistoryPlaces(HISTORY_SEARCH_NUMBER, SearchType.PLACE)
                   .filter(cursor -> cursor != null).first()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .doOnNext(cursor -> getMvpView().hideEmptyStateInPlacesSection())
                   .subscribe(cursor -> {
                       getMvpView().showInitialSearchInPlacesSection(cursor);
                       Timber.d("Loading search history for places took: %s", historyWatch.getElapsedTimeString());
                   }, ex -> Timber.e("Initial places not loaded correctly: " + ex.getMessage()));
    }

    public void loadInitialStateForPlates() {
        Stopwatch historyWatch = stopwatchManager.getStopwatch();
        historyWatch.reset();
        historyFinder.findHistoryPlaces(HISTORY_SEARCH_NUMBER, SearchType.PLATE)
                   .filter(cursor -> cursor != null).first()
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .doOnNext(cursor -> getMvpView().hideEmptyStateInPlatesSection())
                   .subscribe(cursor -> {
                       getMvpView().showInitialSearchInPlatesSection(cursor);
                       Timber.d("Loading search history for plates took: %s", historyWatch.getElapsedTimeString());
                   }, ex -> Timber.e("Initial plates not loaded correctly: " + ex.getMessage()));
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
        RxUtil.unsubscribe(searchSubscription);

        searchSubscription = Observable
                .just(rawPhrase)
                .subscribeOn(Schedulers.computation())
                .map(spellCorrector::cleanForSearch)
                .doOnNext(cleanedText -> lastSearched = cleanedText)
                .flatMap(text -> beginSearchForCleaned(limit, text))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> showSearchResults(results, searchType),
                           e -> Timber.e(e, "Error during searching for places", e));
    }

    // TODO: 2016-12-02 primitive obsession - can be an object
    private Observable<Pair<Cursor, Cursor>> beginSearchForCleaned(Integer limit, String s) {
        return Observable.create(subscriber -> {
            if (s == null || s.equals("")) {
                loadInitialStateForPlaces();
                loadInitialStateForPlates();
            } else {
                stopwatch.reset();

                if (!subscriber.isUnsubscribed()) {
                    getObservableForSearchWithinTwoQueries(s, limit)
                            .subscribe(subscriber::onNext, subscriber::onError, subscriber::onCompleted);
                }
            }
        });
    }

    @NonNull private Observable<Pair<Cursor, Cursor>> getObservableForSearchWithinTwoQueries(String phrase, Integer
            limit) {
        // TODO: 2016-12-10 refactor
        Observable<Cursor> platesCursorObservable = plateFinder.findPlacesForPlateStart(phrase, limit);
        Observable<Cursor> placesCursorObservable = placeFinder.findPlacesByName(phrase, limit);

        return Observable.zip(platesCursorObservable, placesCursorObservable, Pair<Cursor, Cursor>::new);
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
