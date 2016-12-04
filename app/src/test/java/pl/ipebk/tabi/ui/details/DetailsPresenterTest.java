package pl.ipebk.tabi.ui.details;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.ui.search.PlaceListItemType;
import pl.ipebk.tabi.ui.utils.RxSchedulersOverrideRule;
import pl.ipebk.tabi.utils.DeviceHelper;
import pl.ipebk.tabi.utils.NameFormatHelper;
import rx.Observable;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock DetailsMvpView mockMvpView;
    @Mock PlaceDao mockPlaceDao;
    @Mock Activity mockContext;
    @Mock DeviceHelper mockDeviceHelper;
    @Mock NameFormatHelper mockNameHelper;
    private DetailsPresenter detailsPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DatabaseOpenHelper mockOpenHelper = mock(DatabaseOpenHelper.class);
        when(mockOpenHelper.getPlaceDao()).thenReturn(mockPlaceDao);
        DataManager mockDataManager = mock(DataManager.class);
        when(mockDataManager.getDatabaseHelper()).thenReturn(mockOpenHelper);

        when(mockDeviceHelper.getMapScale()).thenReturn(2);
        when(mockMvpView.getMapHeightStream()).thenReturn(Observable.just(1));
        when(mockMvpView.getMapWidthStream()).thenReturn(Observable.just(1));

        detailsPresenter = new DetailsPresenter(mockDataManager, mockDeviceHelper, mockNameHelper);
        detailsPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        detailsPresenter.detachView();
    }

    @Test public void testStandardPlaceIsLoaded() {
        /*String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);
        malbork.setVoivodeship(name + "1");
        malbork.setPowiat(name + "2");
        malbork.setGmina(name + "3");

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, "k", SearchType.PLATE, PlaceListItemType.SEARCH,
                                   Observable.just(1), Observable.just(1));

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showPlate(anyString());
        verify(mockMvpView).showVoivodeship(Mockito.contains(name + "1"));
        verify(mockMvpView).showPowiat(Mockito.contains(name + "2"));
        verify(mockMvpView).showGmina(Mockito.contains(name + "3"));
        verify(mockMvpView).showSearchedText("k");
        verify(mockMvpView).showAdditionalInfo(anyString());
        verify(mockMvpView, atMost(2)).showMap(any());*/
    }

    // TODO: 2016-06-09 uncomment these tests
    @Test public void testSpecialPlaceIsLoaded() {
        /*String name = "Malbork";
        Place malbork = TestDataFactory.createSpecialPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH,
                                   Observable.just(1), Observable.just(1));

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showPlate(anyString());
        verify(mockMvpView).showPlaceHolder();
        verify(mockMvpView, never()).showVoivodeship(anyString());
        verify(mockMvpView, never()).showPowiat(anyString());
        verify(mockMvpView, never()).showGmina(anyString());
        verify(mockMvpView, never()).showAdditionalInfo(anyString());
        verify(mockMvpView, never()).showMap(any());*/
    }

    @Test public void testShowOnMap() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.showOnMap();

        verify(mockMvpView).startMapApp(any());
    }

    @Test public void testSearchInGoogle() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        when(mockNameHelper.formatPlaceToSearch(malbork)).thenReturn(name);
        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.searchInGoogle();

        verify(mockMvpView).startWebSearch(name);
    }
}