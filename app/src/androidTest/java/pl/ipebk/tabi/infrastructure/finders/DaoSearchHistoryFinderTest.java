package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import pl.ipebk.tabi.test.common.assemblers.SearchHistoryAssembler;

public class DaoSearchHistoryFinderTest extends FinderTest {
    private static final int DEFAULT_SEARCH_LIMIT = 4;

    private DaoSearchHistoryFinder finder;

    // TODO: 2016-12-07 write finder descriptions based on that tests
    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoSearchHistoryFinder(databaseHelper.getSearchHistoryDao());
    }

    @MediumTest public void test_shouldFindOnlyForPlates_whenSearchedForHistoryForPlates() throws Exception {
        remember(thatISearched(forPlace().withName("a")).within(SearchType.PLACE).atTime(10));
        remember(thatISearched(forPlace().withName("b")).within(SearchType.PLACE).atTime(12));
        remember(thatISearched(forPlace().withName("b")).within(SearchType.PLATE).atTime(14));

        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLACE));

        thenFoundPlaces().hasCount(3); // one for random plate
        then().searchedPlaceThatIs(FIRST).hasName("b");
        then().searchedPlaceThatIs(SECOND).hasName("a");
    }

    @MediumTest public void test_shouldOrderPlacesByTime() throws Exception {
        remember(thatISearched(forPlace().withName("a")).atTime(14));
        remember(thatISearched(givenPlace().withName("b")).atTime(10));
        remember(thatISearched(givenPlace().withName("c")).atTime(12));

        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchHistoryAssembler.DEFAULT_SEARCH_TYPE));

        then().searchedPlaceThatIs(FIRST).hasName("a");
        then().searchedPlaceThatIs(SECOND).hasName("c");
        then().searchedPlaceThatIs(THIRD).hasName("b");
    }

    @MediumTest public void test_shouldHaveRandomPlace_whenSearchForPlace() throws Exception {
        remember(thatISearched(givenPlace().withName("a")).within(SearchType.PLACE).atTime(10));
        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLACE));
        thenFoundPlaces().hasCount(2); // one for random
        then().searchedPlaceThatIs(LAST).isType(PlaceType.RANDOM);
    }

    @MediumTest public void test_shouldHaveRandomPlace_whenSearchForLicensePlate() throws Exception {
        remember(thatISearched(givenPlace().withName("a").withOwnPlate()).within(SearchType.PLATE).atTime(10));
        whenSearched(finder.findHistoryPlacesList(DEFAULT_SEARCH_LIMIT, SearchType.PLATE));
        thenFoundPlaces().hasCount(2); // one for random
        then().searchedPlaceThatIs(LAST).isType(PlaceType.RANDOM);
    }

    private void remember(SearchHistoryAssembler assembler) {
        databaseHelper.getSearchHistoryDao().updateOrAdd(assembler.assemble());
    }

    private PlaceModelAssembler forPlace() {
        return givenPlace();
    }

    private SearchHistoryAssembler thatISearched(PlaceModelAssembler placeAssembler) {
        PlaceModel model = placeAssembler.assemble();
        databaseHelper.getPlaceDao().add(model);
        SearchHistoryAssembler searchHistoryAssembler = new SearchHistoryAssembler();
        searchHistoryAssembler = searchHistoryAssembler.searchedFor(model);
        return searchHistoryAssembler;
    }
}