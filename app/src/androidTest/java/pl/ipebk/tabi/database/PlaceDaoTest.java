/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.test.suitebuilder.annotation.MediumTest;

import java.text.CollationKey;
import java.text.Collator;
import java.util.List;


import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.ui.search.PlaceListItem;

public class PlaceDaoTest extends DatabaseTest {
    /*@MediumTest public void testGetPlaceForOnlyOnePlate() throws Exception {
        String plateStartToFind = "TAB";
        Place placeToFind = TestDataFactory.createStandardPlace(plateStartToFind, plateStartToFind, Place.Type
                .POWIAT_CITY);
        databaseHelper.getPlaceDao().add(placeToFind);

        String plateStartNotToFind = "BAT";
        Place placeNotToFind = TestDataFactory.createStandardPlace(plateStartNotToFind, plateStartNotToFind, Place
                .Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(placeNotToFind);

        List<Place> foundPlates = databaseHelper.getPlaceDao()
                                                .getPlaceListForPlateStart(Character.toString(plateStartToFind.charAt
                                                        (0)), null);

        assertTrue(1 == foundPlates.size());

        Place foundPlace = foundPlates.get(0);

        assertNotNull(foundPlace);
        assertEquals(foundPlace.getName(), plateStartToFind);
        assertEquals(foundPlace.getPlates().get(0).getPattern(), plateStartToFind);
    }

    @MediumTest public void testGetNextRowId() throws Exception {
        int nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(1, nextRowId);

        String plateStartToFind = "TAB";
        Place place = TestDataFactory.createStandardPlace(plateStartToFind, plateStartToFind, Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(place);
        nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(2, nextRowId);
    }

    @MediumTest public void testGetNotSpecialPowiatPlacesCount() throws Exception {
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.TOWN, true));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.VILLAGE, true));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.POWIAT_CITY, true));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.VOIVODE_CITY, true));

        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.TOWN, false));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.VILLAGE, false));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.POWIAT_CITY, false));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.VOIVODE_CITY, false));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.RANDOM, true));
        databaseHelper.getPlaceDao().add(TestDataFactory.createPlaceForType(Place.Type.SPECIAL, true));

        int expected = 4;
        int actual = databaseHelper.getPlaceDao().getStandardPlacesWithPlateCount();

        assertEquals(expected, actual);
    }*/
}
