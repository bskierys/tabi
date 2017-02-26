package pl.ipebk.tabi.presentation.ui.search;

import android.app.Activity;
import android.database.Cursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.utils.RxSchedulersOverrideRule;
import pl.ipebk.tabi.utils.AggregateIdMatcher;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.presentation.utils.Stopwatch;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock SearchMvpView mockMvpView;
    @Mock SearchHistoryRepository searchRepository;
    @Mock SearchHistoryFinder historyFinder;
    @Mock PlaceFinder placeFinder;
    @Mock LicensePlateFinder plateFinder;
    @Mock SearchTimeProvider timeProvider;
    @Mock Activity mockContext;
    private SearchPresenter searchPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Stopwatch stopwatch = mock(Stopwatch.class);
        StopwatchManager mockManager = mock(StopwatchManager.class);
        when(mockManager.getStopwatch()).thenReturn(stopwatch);

        SearchHistoryFactory searchHistoryFactory = new SearchHistoryFactory(timeProvider);

        searchPresenter = new SearchPresenter(searchRepository, historyFinder,
                                              placeFinder, plateFinder, new SpellCorrector(),
                                              mockManager, searchHistoryFactory);
        searchPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        searchPresenter.detachView();
    }

    @Test public void testRefreshSearch() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(historyFinder.findHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.quickSearchForText("");

        searchPresenter.refreshSearch();
        verify(mockMvpView, atLeastOnce()).showInitialSearchInPlacesSection(any(Cursor.class));
        verify(mockMvpView, atLeastOnce()).showInitialSearchInPlatesSection(any(Cursor.class));
    }

    @Test public void testClearSearch() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(historyFinder.findHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.clearSearch();
        verify(mockMvpView).showInitialSearchInPlatesSection(cursor);
        verify(mockMvpView).showInitialSearchInPlacesSection(cursor);
    }

    @Test public void testLoadInitialStateForPlaces() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(historyFinder.findHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.loadInitialStateForPlates();
        verify(mockMvpView).showInitialSearchInPlatesSection(any(Cursor.class));
    }

    @Test public void testLoadInitialStateForPlates() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(historyFinder.findHistoryPlaces(anyInt(),any(SearchType.class))).thenReturn(rx.Observable.just(cursor));

        searchPresenter.loadInitialStateForPlaces();
        verify(mockMvpView).showInitialSearchInPlacesSection(any(Cursor.class));
    }
}