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

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.place.LicensePlate;
import pl.ipebk.tabi.domain.place.Place;
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.test.common.utils.TestNameFormatHelper;
import pl.ipebk.tabi.ui.search.PlaceListItemType;
import pl.ipebk.tabi.ui.utils.RxSchedulersOverrideRule;
import pl.ipebk.tabi.utils.AggregateIdMatcher;
import pl.ipebk.tabi.utils.DeviceHelper;
import pl.ipebk.tabi.utils.NameFormatHelper;
import rx.Observable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock DetailsMvpView mockMvpView;
    @Mock PlaceRepository placeRepo;
    @Mock Activity mockContext;
    @Mock DeviceHelper mockDeviceHelper;
    private TestNameFormatHelper mockNameHelper;
    private DetailsPresenter detailsPresenter;

    // TODO: 2016-12-14 given-when-then test scheme
    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockDeviceHelper.getMapScale()).thenReturn(2);
        when(mockMvpView.getMapHeightStream()).thenReturn(Observable.just(1));
        when(mockMvpView.getMapWidthStream()).thenReturn(Observable.just(1));
        mockNameHelper = new TestNameFormatHelper(mockContext);

        detailsPresenter = new DetailsPresenter(placeRepo, mockDeviceHelper, mockNameHelper);
        detailsPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        detailsPresenter.detachView();
    }

    // TODO: 2016-12-14 get rid of mock-driven tests
    @Test public void testStandardPlaceIsLoaded() {
        String name = "Malbork";
        // TODO: 2016-12-14 factory class
        List<LicensePlate> plates = new ArrayList<>();
        plates.add(new LicensePlate(new AggregateId(0L), "TAB", null));
        Place malbork = new Place(name, PlaceType.TOWN,name + "1",name + "2",name + "3", plates, true);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, "k", SearchType.PLATE, PlaceListItemType.SEARCH);

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showPlate("TAB");
        verify(mockMvpView).showVoivodeship(contains(mockNameHelper.formatVoivodeship(name + "1")));
        verify(mockMvpView).showPowiat(contains(mockNameHelper.formatPowiat(name + "2")));
        verify(mockMvpView).showGmina(contains(mockNameHelper.formatGmina(name + "3")));
        verify(mockMvpView).showSearchedText("k");
        verify(mockMvpView).showAdditionalInfo(anyString());
        verify(mockMvpView, atMost(2)).showMap(any());
    }

    static AggregateId agIdEq(AggregateId expected) {
        return argThat(new AggregateIdMatcher(expected));
    }

    @Test public void testSpecialPlaceIsLoaded() {
        String name = "Malbork";
        List<LicensePlate> plates = new ArrayList<>();
        plates.add(new LicensePlate(new AggregateId(0L), "TAB", null));
        Place malbork = new Place(name, PlaceType.SPECIAL,name + "1",name + "2",name + "3", plates, true);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH);

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showPlate(anyString());
        verify(mockMvpView).showPlaceHolder();
        verify(mockMvpView, never()).showVoivodeship(anyString());
        verify(mockMvpView, never()).showPowiat(anyString());
        verify(mockMvpView, never()).showGmina(anyString());
        verify(mockMvpView, never()).showAdditionalInfo(anyString());
        verify(mockMvpView, never()).showMap(any());
    }

    @Test public void testShowOnMap() {
        String name = "Malbork";
        List<LicensePlate> plates = new ArrayList<>();
        plates.add(new LicensePlate(new AggregateId(0L), "TAB", null));
        Place malbork = new Place(name, PlaceType.SPECIAL,name + "1",name + "2",name + "3", plates, true);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.showOnMap();

        verify(mockMvpView).startMapApp(any());
    }

    @Test public void testSearchInGoogle() {
        String name = "Malbork";
        List<LicensePlate> plates = new ArrayList<>();
        plates.add(new LicensePlate(new AggregateId(0L), "TAB", null));
        Place malbork = new Place(name, PlaceType.SPECIAL,name + "1",name + "2",name + "3", plates, true);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));
        when(mockNameHelper.formatPlaceToSearch(malbork)).thenReturn(name);
        detailsPresenter.loadPlace(1L, null, SearchType.PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.searchInGoogle();

        verify(mockMvpView).startWebSearch(mockNameHelper.formatPlaceToSearch(malbork));
    }
}