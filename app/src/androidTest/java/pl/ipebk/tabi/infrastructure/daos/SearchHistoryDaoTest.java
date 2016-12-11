/*
* author: Bartlomiej Kierys
* date: 2016-02-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.finders.DaoSearchHistoryFinderTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import pl.ipebk.tabi.test.common.assemblers.SearchHistoryAssembler;

public class SearchHistoryDaoTest extends DatabaseTest {
    private PlaceModelAssembler2 placeModelAssembler;

    @Override public void setUp() throws Exception {
        super.setUp();
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

    // TODO: 2016-12-10 better naming/usage for that class
    // TODO: 2016-12-10 add place to database in assemble method
    public PlaceModelAssembler2 givenPlace() {
        placeModelAssembler = new PlaceModelAssembler2();
        return placeModelAssembler;
    }

    // TODO: 2016-12-10 change names to match pattern
    /*@MediumTest public void testAddOrUpdateHistory() throws Exception {
        long id = givenPlace().withName("Place").assemble().getId();

        givenThatSearched()
        databaseHelper.getSearchHistoryDao().updateOrAdd(TestDataFactory.createSearchHistory(id, SearchType.PLACE, 0));
        assertEquals(1, databaseHelper.getSearchHistoryDao().getHistoryListForType(SearchType.PLACE, null).size());

        databaseHelper.getSearchHistoryDao().updateOrAdd(TestDataFactory.createSearchHistory(id, SearchType.PLACE, 10));
        List<SearchHistory> wholeHistory = databaseHelper.getSearchHistoryDao()
                                                         .getHistoryListForType(SearchType.PLACE, null);
        assertEquals(1, wholeHistory.size());
        assertEquals(10, wholeHistory.get(0).getTimeSearched().getTime());
    }

    @MediumTest public void testAddHistoryDoeInOtherSearchType() throws Exception {
        Place place = TestDataFactory.createStandardPlace("PlaceModel");
        databaseHelper.getPlaceDao().add(place);
        long id = place.getId();

        databaseHelper.getSearchHistoryDao().updateOrAdd(TestDataFactory.createSearchHistory(id, SearchType.PLACE, 0));
        databaseHelper.getSearchHistoryDao().updateOrAdd(TestDataFactory.createSearchHistory(id, SearchType.PLATE, 10));

        assertEquals(2, databaseHelper.getSearchHistoryDao().getAll().size());
    }*/

    // TODO: 2016-12-10 better naming
    class PlaceModelAssembler2 extends PlaceModelAssembler {
        @Override public PlaceModel assemble() {
            PlaceModel model = super.assemble();
            databaseHelper.getPlaceDao().add(model);
            return model;
        }
    }
}
