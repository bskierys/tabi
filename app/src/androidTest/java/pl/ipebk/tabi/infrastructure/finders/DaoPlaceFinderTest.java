package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import java.text.CollationKey;
import java.text.Collator;
import java.util.List;

import pl.ipebk.tabi.test.common.TestDataFactory;

import static org.junit.Assert.*;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceFinderTest {
    /*@MediumTest public void testSearchPlacesWithDiacritics() throws Exception {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type
                .POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("swirzyce", dummyPlate, Place.Type
                .POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("doboszyce", dummyPlate, Place.Type
                .POWIAT_CITY));

        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", null);
        assertTrue(!foundPlaces.isEmpty());
        assertEquals("świdnica", foundPlaces.get(0).getName());

        foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("swid", null);

        assertTrue(!foundPlaces.isEmpty());
        assertEquals("świdnica", foundPlaces.get(0).getName());
    }

    @MediumTest public void testSearchPlacesLimit() throws Exception {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type
                .POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnico", dummyPlate, Place.Type
                .POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnice", dummyPlate, Place.Type
                .POWIAT_CITY));

        int limit = 2;
        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", limit);

        assertEquals(limit, foundPlaces.size());
    }

    @MediumTest public void testSearchPlacesSortOrderByType() throws Exception {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type
                .VOIVODE_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnico", dummyPlate, Place.Type.TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnice", dummyPlate, Place.Type
                .VILLAGE));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnicy", dummyPlate, Place.Type
                .SPECIAL));

        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", null);

        assertEquals(4, foundPlaces.size());

        int lastOne = foundPlaces.get(0).getType().ordinal();
        for (int i = 1; i < 4; i++) {
            int typeOrdinal = foundPlaces.get(i).getType().ordinal();
            assertTrue(typeOrdinal >= lastOne);
        }
    }

    @MediumTest public void testSearchPlacesSortOrderByName() throws Exception {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("śwarądz", dummyPlate, Place.Type.TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świnoujście", dummyPlate, Place.Type
                .TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("śokołów", dummyPlate, Place.Type.TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("śókołów", dummyPlate, Place.Type.TOWN));

        List<Place> places = databaseHelper.getPlaceDao().getPlaceListByName("ś", null);

        Collator collator = Collator.getInstance();

        String lastOne = places.get(0).getName();
        for (int i = 1; i < 3; i++) {
            Place place = places.get(i);
            CollationKey key = collator.getCollationKey(place.getName());
            int compare = key.compareTo(collator.getCollationKey(lastOne));

            assertTrue(compare > 0);
            lastOne = place.getName();
        }
    }*/
}