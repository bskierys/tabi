package pl.ipebk.tabi.presentation.ui.details;

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

import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.place.LicensePlateDto;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.presentation.model.place.PlaceDto;
import pl.ipebk.tabi.presentation.model.place.PlaceFactory;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.utils.RxSchedulersOverrideRule;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.utils.TestPlaceLocalizationHelper;
import pl.ipebk.tabi.utils.AggregateIdMatcher;
import rx.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {
    @Rule public final RxSchedulersOverrideRule overrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock DetailsMvpView mockMvpView;
    @Mock PlaceRepository placeRepo;
    @Mock Activity mockContext;
    @Mock ClipboardCopyMachine mockClipboardCopyMachine;
    @Mock MapScaleCalculator mockMapScaleCalculator;
    @Mock PlaceLocalizationHelper placeLocalizationHelper;
    private PlaceFactory mockFactory;
    private DetailsPresenter detailsPresenter;

    // TODO: 2016-12-14 given-when-then test scheme
    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockMvpView.getMapHeightStream()).thenReturn(Observable.just(1));
        when(mockMvpView.getMapWidthStream()).thenReturn(Observable.just(1));
        mockFactory = new PlaceFactory(placeLocalizationHelper);

        detailsPresenter = new DetailsPresenter(placeRepo, mockClipboardCopyMachine,
                                                mockMapScaleCalculator, mockFactory);
        detailsPresenter.attachView(mockMvpView);
    }

    @After public void tearDown() throws Exception {
        detailsPresenter.detachView();
    }

    // TODO: 2016-12-14 get rid of mock-driven tests
    @Test public void testStandardPlaceIsLoaded() {
        String name = "Malbork";
        List<LicensePlateDto> plates = new ArrayList<>();
        plates.add(LicensePlateDto.create("TAB", null));
        PlaceDto malbork = PlaceDto.create(name, PlaceType.TOWN, name + "1", name + "2", name + "3", plates);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));
        when(placeLocalizationHelper.formatVoivodeship(anyString())).thenReturn(malbork.voivodeship());
        when(placeLocalizationHelper.formatPowiat(anyString())).thenReturn(malbork.powiat());
        when(placeLocalizationHelper.formatGmina(anyString())).thenReturn(malbork.gmina());
        when(placeLocalizationHelper.formatAdditionalInfo(any(), anyString())).thenReturn(malbork.gmina());

        detailsPresenter.loadPlace(1L, "k", SearchType.LICENSE_PLATE, PlaceListItemType.SEARCH);

        verify(mockMvpView).showPlaceName(name);
        verify(mockMvpView).showPlate("TAB");
        verify(mockMvpView).showVoivodeship(contains(malbork.voivodeship()));
        verify(mockMvpView).showPowiat(contains(malbork.powiat()));
        verify(mockMvpView).showGmina(contains(malbork.gmina()));
        verify(mockMvpView).showAdditionalInfo(malbork.gmina());
        verify(mockMvpView, atMost(2)).showMap(any());
    }

    static AggregateId agIdEq(AggregateId expected) {
        return argThat(new AggregateIdMatcher(expected));
    }

    @Test public void testSpecialPlaceIsLoaded() {
        String name = "Malbork";
        List<LicensePlateDto> plates = new ArrayList<>();
        plates.add(LicensePlateDto.create("TAB", null));
        PlaceDto malbork = PlaceDto.create(name, PlaceType.SPECIAL, name + "1", name + "2", name + "3", plates);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));

        detailsPresenter.loadPlace(1L, null, SearchType.LICENSE_PLATE, PlaceListItemType.SEARCH);

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
        List<LicensePlateDto> plates = new ArrayList<>();
        plates.add(LicensePlateDto.create("TAB", null));
        PlaceDto malbork = PlaceDto.create(name, PlaceType.SPECIAL, name + "1", name + "2", name + "3", plates);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));
        detailsPresenter.loadPlace(1L, null, SearchType.LICENSE_PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.showOnMap();

        verify(mockMvpView).startMapApp(any());
    }

    @Test public void testSearchInGoogle() {
        String name = "Malbork";
        List<LicensePlateDto> plates = new ArrayList<>();
        plates.add(LicensePlateDto.create("TAB", null));
        PlaceDto malbork = PlaceDto.create(name, PlaceType.SPECIAL, name + "1", name + "2", name + "3", plates);

        when(placeRepo.loadByIdObservable(agIdEq(new AggregateId(1L)))).thenReturn(Observable.just(malbork));
        when(placeLocalizationHelper.formatPlaceToSearch(any())).thenReturn(name);
        detailsPresenter.loadPlace(1L, null, SearchType.LICENSE_PLATE, PlaceListItemType.SEARCH);

        detailsPresenter.searchInGoogle();

        Place malborkPlace = mockFactory.createFromDto(malbork);
        verify(mockMvpView).startWebSearch(malborkPlace.getSearchPhrase());
    }
}