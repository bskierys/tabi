/*
* author: Bartlomiej Kierys
* date: 2016-02-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.Date;
import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.test.common.TestDataFactory;

public class SearchHistoryDaoTest extends DatabaseTest {
    @MediumTest public void testGetHistoryForPlates() {
        Place place = TestDataFactory.createStandardPlace("PLACE", "TAB", Place.Type.POWIAT_CITY);
        Place plate = TestDataFactory.createStandardPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        Place plate2 = TestDataFactory.createStandardPlace("PLATE", "TAB2", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(place);
        databaseHelper.getPlaceDao().add(plate);
        databaseHelper.getPlaceDao().add(plate2);

        SearchHistory placeHistory = new SearchHistory();
        placeHistory.setPlace(place);
        placeHistory.setSearchType(SearchType.PLACE);
        placeHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(placeHistory);

        SearchHistory plateHistory = new SearchHistory();
        plateHistory.setPlace(plate);
        plateHistory.setSearchType(SearchType.PLATE);
        plateHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(plateHistory);

        SearchHistory plateHistory2 = new SearchHistory();
        plateHistory2.setPlace(plate2);
        plateHistory2.setSearchType(SearchType.PLATE);
        plateHistory2.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(plateHistory2);

        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                                                        .getHistoryListForType(SearchType.PLATE, null);

        assertEquals(2, historyList.size());

        for (SearchHistory history : historyList) {
            assertEquals("PLATE", history.getPlace().getName());
        }
    }

    @MediumTest public void testGetHistoryOrderAndLimit() {
        for (int i = 0; i < 5; i++) {
            Place plate = TestDataFactory.createStandardPlace("PLATE_" + Integer.toString(i), "BAT", Place.Type
                    .POWIAT_CITY);
            databaseHelper.getPlaceDao().add(plate);

            SearchHistory history = new SearchHistory();
            history.setPlace(plate);
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

    @MediumTest public void testOnlyOnePlaceInHistoryWithSamePlate() {
        Place plate = TestDataFactory.createStandardPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(plate);

        for (int i = 0; i < 5; i++) {
            SearchHistory history = new SearchHistory();
            history.setPlace(plate);
            history.setSearchType(SearchType.PLATE);
            history.setTimeSearched(new Date(i));
            databaseHelper.getSearchHistoryDao().add(history);
        }

        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                                                        .getHistoryListForType(SearchType.PLATE, null);

        assertEquals(1, historyList.size());
        assertEquals(4, historyList.get(0).getTimeSearched().getTime());
    }
}
