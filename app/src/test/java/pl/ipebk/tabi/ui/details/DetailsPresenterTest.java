package pl.ipebk.tabi.ui.details;

import android.app.Activity;
import android.content.res.Resources;
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
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.ui.utils.RxSchedulersOverrideRule;
import rx.Observable;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new
            RxSchedulersOverrideRule();
    @Mock DetailsMvpView mockMvpView;
    @Mock PlaceDao mockPlaceDao;
    @Mock Activity mockContext;
    private DetailsPresenter detailsPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DatabaseOpenHelper mockOpenHelper = mock(DatabaseOpenHelper.class);
        when(mockOpenHelper.getPlaceDao()).thenReturn(mockPlaceDao);
        DataManager mockDataManager = mock(DataManager.class);
        when(mockDataManager.getDatabaseHelper()).thenReturn(mockOpenHelper);

        // set mocked density
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.density = 2f;

        Resources mockResources = mock(Resources.class);
        when(mockResources.getDisplayMetrics()).thenReturn(metrics);
        when(mockContext.getResources()).thenReturn(mockResources);

        detailsPresenter = new DetailsPresenter(mockDataManager, mockContext);
        detailsPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        detailsPresenter.detachView();
    }

    @Test public void testStandardPlaceIsLoaded() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, null, Observable.just(1), Observable.just(1));

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showSearchedPlate(anyString());
        verify(mockMvpView).showVoivodeship(anyString());
        verify(mockMvpView).showPowiat(anyString());
        verify(mockMvpView).showGmina(anyString());
        verify(mockMvpView).showAdditionalInfo(anyString());
        verify(mockMvpView, atMost(2)).showMap(any());
    }

    @Test public void testSpecialPlaceIsLoaded() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createSpecialPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, null, Observable.just(1), Observable.just(1));

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showSearchedPlate(anyString());
        verify(mockMvpView).showPlaceHolder();
        verify(mockMvpView, never()).showVoivodeship(anyString());
        verify(mockMvpView, never()).showPowiat(anyString());
        verify(mockMvpView, never()).showGmina(anyString());
        verify(mockMvpView, never()).showAdditionalInfo(anyString());
        verify(mockMvpView, never()).showMap(any());
    }

    @Test public void testShowOnMap() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, Observable.just(1), Observable.just(1));

        detailsPresenter.showOnMap();

        verify(mockMvpView).startMap(any());
    }

    @Test public void testSearchInGoogle() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, Observable.just(1), Observable.just(1));

        detailsPresenter.searchInGoogle();

        verify(mockMvpView).startWebSearch(anyString());
    }

    @Test public void testShowVoivodeship() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, Observable.just(1), Observable.just(1));

        detailsPresenter.showMoreForVoivodeship();
        Character searchPhrase = malbork.getMainPlate().getPattern().charAt(0);

        verify(mockMvpView).goToSearchForPhrase(Character.toString(searchPhrase));
    }
}