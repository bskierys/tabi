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

public class SearchHistoryDaoTest extends DatabaseTest {
    @MediumTest public void testGetHistoryForPlates() {
        Place place = constructPlace("PLACE", "TAB", Place.Type.POWIAT_CITY);
        Place plate = constructPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(place);
        databaseHelper.getPlaceDao().add(plate);

        SearchHistory placeHistory = new SearchHistory();
        placeHistory.setPlace(place);
        placeHistory.setSearchType(SearchHistory.SearchType.PLACE);
        placeHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(placeHistory);

        SearchHistory plateHistory = new SearchHistory();
        plateHistory.setPlace(plate);
        plateHistory.setSearchType(SearchHistory.SearchType.PLATE);
        plateHistory.setTimeSearched(new Date(0));
        databaseHelper.getSearchHistoryDao().add(plateHistory);
        databaseHelper.getSearchHistoryDao().add(plateHistory);

        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                .getHistoryListForType(SearchHistory.SearchType.PLATE, null);

        assertEquals(2, historyList.size());

        for (SearchHistory history : historyList) {
            assertEquals("PLATE", history.getPlace().getName());
        }
    }

    @MediumTest public void testGetHistoryOrderAndLimit() {
        Place plate = constructPlace("PLATE", "BAT", Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(plate);

        for (int i = 0; i < 5; i++) {
            SearchHistory history = new SearchHistory();
            history.setPlace(plate);
            history.setSearchType(SearchHistory.SearchType.PLATE);
            history.setTimeSearched(new Date(i));
            databaseHelper.getSearchHistoryDao().add(history);
        }

        int limit = 4;
        List<SearchHistory> historyList = databaseHelper.getSearchHistoryDao()
                .getHistoryListForType(SearchHistory.SearchType.PLATE, limit);

        assertEquals(limit, historyList.size());

        Date lastOne = historyList.get(0).getTimeSearched();
        for (int i = 1; i < limit; i++) {
            Date historyDate = historyList.get(i).getTimeSearched();
            assertTrue(historyDate.before(lastOne));
            lastOne = historyDate;
        }
    }
}
