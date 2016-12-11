package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.Date;

import pl.ipebk.tabi.domain.searchhistory.SearchHistory;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import pl.ipebk.tabi.test.common.assemblers.SearchHistoryAssembler;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoSearchHistoryFinderTest extends FinderTest {
    private static final int DEFAULT_SEARCH_LIMIT = 3;

    private DaoSearchHistoryFinder finder;

    // TODO: 2016-12-07 write finder descriptions based on that tests
    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoSearchHistoryFinder(databaseHelper.getPlaceDao());
    }

    @MediumTest public void test_shouldFindOnlyForPlates_whenSearchedForHistoryForPlates() throws Exception {
        givenThatSearched(givenPlace().withName("a")).within(SearchType.PLATE).atTime(10).assemble();
        givenThatSearched(givenPlace().withName("b")).within(SearchType.PLATE).atTime(12).assemble();
        givenThatSearched(givenPlace().withName("b")).within(SearchType.PLACE).atTime(14).assemble();

        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLATE));

        thenFoundPlaces().hasCount(3); // one for random plate
        then().searchedPlaceThatIs(FIRST).hasName("a");
        then().searchedPlaceThatIs(SECOND).hasName("b");
    }

    @MediumTest public void test_shouldOrderPlacesByTime() throws Exception {
        givenThatSearched(givenPlace().withName("a")).atTime(14).assemble();
        givenThatSearched(givenPlace().withName("b")).atTime(10).assemble();
        givenThatSearched(givenPlace().withName("c")).atTime(12).assemble();

        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchHistoryAssembler.DEFAULT_SEARCH_TYPE));

        then().searchedPlaceThatIs(FIRST).hasName("b");
        then().searchedPlaceThatIs(SECOND).hasName("c");
        then().searchedPlaceThatIs(SECOND).hasName("a");
    }

    @MediumTest public void test_shouldHaveRandomPlace_whenSearchForPlace() throws Exception {
        givenThatSearched(givenPlace().withName("a")).within(SearchType.PLACE).atTime(10).assemble();
        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLACE));
        thenFoundPlaces().hasCount(2); // one for random
        then().searchedPlaceThatIs(LAST).isType(PlaceType.RANDOM);
    }

    @MediumTest public void test_shouldHaveRandomPlace_whenSearchForLicensePlate() throws Exception {
        givenThatSearched(givenPlace().withName("a")).within(SearchType.PLATE).atTime(10).assemble();
        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLATE));
        thenFoundPlaces().hasCount(2); // one for random
        then().searchedPlaceThatIs(LAST).isType(PlaceType.RANDOM);
    }

    protected SearchHistoryAssembler searchHistoryAssembler;

    private SearchHistoryAssembler2 givenThatSearched(PlaceModelAssembler placeAssembler) {
        PlaceModel model = placeAssembler.assemble();
        databaseHelper.getPlaceDao().add(model);
        searchHistoryAssembler = new SearchHistoryAssembler2();
        searchHistoryAssembler = searchHistoryAssembler.searchedFor(model);
        return (SearchHistoryAssembler2) searchHistoryAssembler;
    }

    private static class SearchHistoryAssembler2 extends SearchHistoryAssembler {
        @Override public SearchHistoryModel assemble() {
            SearchHistoryModel model = super.assemble();
            databaseHelper.getSearchHistoryDao().updateOrAdd(model);
            return model;
        }
    }
}