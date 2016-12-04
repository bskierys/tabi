package pl.ipebk.tabi.ui.search;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.Observable;
import android.util.DisplayMetrics;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.details.DetailsPresenter;
import pl.ipebk.tabi.ui.utils.RxSchedulersOverrideRule;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.utils.Stopwatch;
import pl.ipebk.tabi.utils.StopwatchManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock SearchMvpView mockMvpView;
    @Mock PlaceDao mockPlaceDao;
    @Mock SearchHistoryDao mockHistoryDao;
    @Mock Activity mockContext;
    private SearchPresenter searchPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DatabaseOpenHelper mockOpenHelper = mock(DatabaseOpenHelper.class);
        when(mockOpenHelper.getPlaceDao()).thenReturn(mockPlaceDao);
        when(mockOpenHelper.getSearchHistoryDao()).thenReturn(mockHistoryDao);
        DataManager mockDataManager = mock(DataManager.class);
        when(mockDataManager.getDatabaseHelper()).thenReturn(mockOpenHelper);
        Stopwatch stopwatch = mock(Stopwatch.class);
        StopwatchManager mockManager = mock(StopwatchManager.class);
        when(mockManager.getStopwatch()).thenReturn(stopwatch);

        searchPresenter = new SearchPresenter(mockDataManager, new SpellCorrector(), mockManager);
        searchPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        searchPresenter.detachView();
    }

    @Test public void testPlaceSelected() throws Exception {
        searchPresenter.placeSelected(0, "TAB", "TAB", SearchType.PLACE,PlaceListItemType.SEARCH);
        verify(mockMvpView).goToPlaceDetails(0, "TAB", SearchType.PLACE,PlaceListItemType.SEARCH);
        verify(mockHistoryDao).updateOrAdd(any(SearchHistory.class));
    }

    @Test public void testRefreshSearch() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.quickSearchForText("");

        searchPresenter.refreshSearch();
        verify(mockMvpView, atLeastOnce()).showInitialSearchInPlacesSection(any(Cursor.class));
        verify(mockMvpView, atLeastOnce()).showInitialSearchInPlatesSection(any(Cursor.class));
    }

    @Test public void testClearSearch() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.clearSearch();
        verify(mockMvpView).showInitialSearchInPlatesSection(cursor);
        verify(mockMvpView).showInitialSearchInPlacesSection(cursor);
    }

    @Test public void testLoadInitialStateForPlaces() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.loadInitialStateForPlates();
        verify(mockMvpView).showInitialSearchInPlatesSection(any(Cursor.class));
    }

    @Test public void testLoadInitialStateForPlates() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.loadInitialStateForPlaces();
        verify(mockMvpView).showInitialSearchInPlacesSection(any(Cursor.class));
    }
}