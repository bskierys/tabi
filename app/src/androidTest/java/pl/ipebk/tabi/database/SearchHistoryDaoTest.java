/*
* author: Bartlomiej Kierys
* date: 2016-02-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.Date;
import java.util.List;

import pl.ipebk.tabi.test.common.TestDataFactory;

public class SearchHistoryDaoTest extends DatabaseTest {
    /*@MediumTest public void testGetHistoryForPlates() throws Exception {
        Place place = TestDataFactory.createStandardPlace("PLACE", "TAB", Place.Type.POWIAT_CITY);
        Place plate = TestDataFactory.createStandardPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        Place plate2 = TestDataFactory.createStandardPlace("PLATE", "TAB2", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(place);
        databaseHelper.getPlaceDao().add(plate);
        databaseHelper.getPlaceDao().add(plate2);

        SearchHistory placeHistory = new SearchHistory();
        placeHistory.setPlaceId(place.getId());
        placeHistory.setSearchType(SearchType.PLACE);
        placeHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(placeHistory);

        SearchHistory plateHistory = new SearchHistory();
        plateHistory.setPlaceId(plate.getId());
        plateHistory.setSearchType(SearchType.PLATE);
        plateHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(plateHistory);

        SearchHistory plateHistory2 = new SearchHistory();
        plateHistory2.setPlaceId(plate2.getId());
        plateHistory2.setSearchType(SearchType.PLATE);
        plateHistory2.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(plateHistory2);

        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                                                        .getHistoryListForType(SearchType.PLATE, null);

        assertEquals(2, historyList.size());

        for (SearchHistory history : historyList) {
            assertEquals(SearchType.PLATE, history.getSearchType());
        }
    }

    @MediumTest public void testGetHistoryOrderAndLimit() throws Exception {
        for (int i = 0; i < 5; i++) {
            Place plate = TestDataFactory.createStandardPlace("PLATE_" + Integer.toString(i), "BAT", Place.Type
                    .POWIAT_CITY);
            databaseHelper.getPlaceDao().add(plate);

            SearchHistory history = new SearchHistory();
            history.setPlaceId(plate.getId());
            history.setSearchType(SearchType.PLATE);
            history.setTimeSearched(new Date(i));
            databaseHelper.getSearchHistoryDao().add(history);
        }

        int limit = 4;
        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                                                        .getHistoryListForType(SearchType.PLATE, limit);

        assertEquals(limit, historyList.size());

        Date lastOne = historyList.get(0).getTimeSearched();
        for (int i = 1; i < limit; i++) {
            Date historyDate = historyList.get(i).getTimeSearched();
            assertTrue(historyDate.before(lastOne));
            lastOne = historyDate;
        }
    }

    @MediumTest public void testOnlyOnePlaceInHistoryWithSamePlate() throws Exception {
        Place plate = TestDataFactory.createStandardPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(plate);

        for (int i = 0; i < 5; i++) {
            SearchHistory history = new SearchHistory();
            history.setPlaceId(plate.getId());
            history.setSearchType(SearchType.PLATE);
            history.setTimeSearched(new Date(i));
            databaseHelper.getSearchHistoryDao().add(history);
        }

        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                                                        .getHistoryListForType(SearchType.PLATE, null);

        assertEquals(1, historyList.size());
        assertEquals(4, historyList.get(0).getTimeSearched().getTime());
    }

    @MediumTest public void testAddOrUpdateHistory() throws Exception {
        Place place = TestDataFactory.createStandardPlace("PlaceModel");
        databaseHelper.getPlaceDao().add(place);
        long id = place.getId();

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
}
