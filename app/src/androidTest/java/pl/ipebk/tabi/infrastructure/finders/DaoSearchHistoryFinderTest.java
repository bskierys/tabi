package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.List;

import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.ui.search.PlaceListItem;

import static org.junit.Assert.*;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoSearchHistoryFinderTest extends FinderTest {
    /*@MediumTest public void testGetHistoryForType() throws Exception {
        String dummyPlate = "AAA";
        Place place1 = TestDataFactory.createStandardPlace("śwarądz", dummyPlate, Place.Type.TOWN);
        Place place2 = TestDataFactory.createStandardPlace("śokołów", dummyPlate, Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(place1);
        databaseHelper.getPlaceDao().add(place2);

        databaseHelper.getSearchHistoryDao().updateOrAdd(
                TestDataFactory.createSearchHistory(place1.getId(), SearchType.PLACE, 0));
        databaseHelper.getSearchHistoryDao().updateOrAdd(
                TestDataFactory.createSearchHistory(place2.getId(), SearchType.PLACE, 10));
        databaseHelper.getSearchHistoryDao().updateOrAdd(
                TestDataFactory.createSearchHistory(place2.getId(), SearchType.PLATE, 20));

        List<PlaceListItem> items = databaseHelper.getPlaceDao().getHistoryPlacesList(null, SearchType.PLACE);

        assertEquals(3, items.size());
        for (int i = 0; i < items.size() - 1; i++) {
            PlaceListItem item = items.get(i);
            assertEquals(Place.Type.TOWN, item.getPlaceType());
        }
    }

    @MediumTest public void testGetHistoryProperlySorted() throws Exception {
        Place first = TestDataFactory.createStandardPlace("1", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(first);
        Place second = TestDataFactory.createStandardPlace("2", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(second);
        Place third = TestDataFactory.createStandardPlace("3", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(third);
        Place forth = TestDataFactory.createStandardPlace("4", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(forth);

        databaseHelper.getSearchHistoryDao().add(
                TestDataFactory.createSearchHistory(second.getId(), SearchType.PLACE, 10));
        databaseHelper.getSearchHistoryDao().add(
                TestDataFactory.createSearchHistory(first.getId(), SearchType.PLACE, 5));
        databaseHelper.getSearchHistoryDao().add(
                TestDataFactory.createSearchHistory(forth.getId(), SearchType.PLACE, 50));
        databaseHelper.getSearchHistoryDao().add(
                TestDataFactory.createSearchHistory(third.getId(), SearchType.PLACE, 12));

        List<PlaceListItem> items = databaseHelper.getPlaceDao().getHistoryPlacesList(null, SearchType.PLACE);

        assertEquals(5, items.size());

        for (int i = 0; i < items.size() - 1; i++) {
            PlaceListItem item = items.get(i);
            String expectedPlaceName = Integer.toString(4 - i);
            assertEquals(expectedPlaceName, item.getPlaceName());
        }
    }

    @MediumTest public void testGetHistoryHasRandomForPlace() throws Exception {
        Place first = TestDataFactory.createStandardPlace("1", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(first);
        Place second = TestDataFactory.createStandardPlace("2", "AAA", Place.Type.TOWN);
        databaseHelper.getPlaceDao().add(second);

        List<PlaceListItem> items = databaseHelper.getPlaceDao().getHistoryPlacesList(null, SearchType.PLACE);
        assertEquals(1, items.size());

        PlaceListItem random = items.get(0);
        assertTrue(random.getPlaceName().equals("1") || random.getPlaceName().equals("2"));
    }

    @MediumTest public void testGetHistoryHasRandomForPlate() throws Exception {
        Place first = TestDataFactory.createStandardPlace("1", "AAA", Place.Type.TOWN);
        first.setHasOwnPlate(true);
        databaseHelper.getPlaceDao().add(first);
        Place second = TestDataFactory.createStandardPlace("2", "AAA", Place.Type.TOWN);
        second.setHasOwnPlate(false);
        databaseHelper.getPlaceDao().add(second);

        List<PlaceListItem> items = databaseHelper.getPlaceDao().getHistoryPlacesList(null, SearchType.PLATE);
        assertEquals(1, items.size());

        PlaceListItem random = items.get(0);
        assertTrue(random.getPlaceName().equals("1"));
        assertEquals(Place.Type.RANDOM, random.getPlaceType());
    }*/
}