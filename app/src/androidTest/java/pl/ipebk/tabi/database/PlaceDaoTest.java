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

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.ui.main.CategoryListItem;

public class PlaceDaoTest extends DatabaseTest {
    @MediumTest public void testGetVoivodeships() {
        Collator collator = Collator.getInstance();

        String voivodeshipName1 = "A";
        String voivodeshipName2 = "Ś";
        String voivodeshipName3 = "Z";

        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName1, null, Place.Type.VOIVODE_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName2, null, Place.Type.VOIVODE_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName3, null, Place.Type.VOIVODE_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName1, null, Place.Type.SPECIAL));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName2, null, Place.Type.SPECIAL));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace(voivodeshipName3, null, Place.Type.SPECIAL));

        databaseHelper.getPlaceDao().getVoivodeshipsObservable()
                .subscribe(voivodeships -> {
                    assertEquals(6, voivodeships.size());

                    // check if voivodeships are before specials
                    for (int i = 0; i < 3; i++) {
                        assertEquals(Place.Type.VOIVODE_CITY, voivodeships.get(i).getType());
                        assertEquals(Place.Type.SPECIAL, voivodeships.get(i + 3).getType());
                    }

                    // check order of voivodeships
                    String lastOne = voivodeships.get(0).getName();
                    for (int i = 1; i < 3; i++) {
                        CategoryListItem categoryListItem = voivodeships.get(i);
                        CollationKey key = collator.getCollationKey(categoryListItem.getName());
                        int compare = key.compareTo(collator.getCollationKey(lastOne));

                        assertTrue(compare > 0);
                        lastOne = categoryListItem.getName();
                    }
                });
    }

    @MediumTest public void testGetPlaceForOnlyOnePlate() {
        String plateStartToFind = "TAB";
        Place placeToFind = TestDataFactory.createStandardPlace(plateStartToFind, plateStartToFind, Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(placeToFind);

        String plateStartNotToFind = "BAT";
        Place placeNotToFind = TestDataFactory.createStandardPlace(plateStartNotToFind, plateStartNotToFind, Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(placeNotToFind);

        List<Place> foundPlates = databaseHelper.getPlaceDao()
                .getPlaceListForPlateStart(Character.toString(plateStartToFind.charAt(0)), null);

        assertTrue(1 == foundPlates.size());

        Place foundPlace = foundPlates.get(0);

        assertNotNull(foundPlace);
        assertEquals(foundPlace.getName(), plateStartToFind);
        assertEquals(foundPlace.getPlates().get(0).getPattern(), plateStartToFind);
    }

    @MediumTest public void testGetNextRowId() {
        int nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(1, nextRowId);

        String plateStartToFind = "TAB";
        Place place = TestDataFactory.createStandardPlace(plateStartToFind, plateStartToFind, Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(place);
        nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(2, nextRowId);
    }

    @MediumTest public void testGetPlaceForAdditionalPlate() {
        String plateStartToFind = "TAB";
        String plateStartNotToFind = "BAT";
        Place placeNotToFind = TestDataFactory.createStandardPlace(plateStartNotToFind, plateStartNotToFind, Place.Type.POWIAT_CITY);
        databaseHelper.getPlaceDao().add(placeNotToFind);

        Place placeToFind = TestDataFactory.createStandardPlace(plateStartNotToFind, plateStartNotToFind, Place.Type.POWIAT_CITY);
        Plate plate = new Plate();
        plate.setPattern(plateStartToFind);
        placeToFind.getPlates().add(plate);
        databaseHelper.getPlaceDao().add(placeToFind);

        List<Place> foundPlaces = databaseHelper.getPlaceDao()
                .getPlaceListForPlateStart(Character.toString(plateStartToFind.charAt(0)), null);

        assertTrue(1 == foundPlaces.size());

        Place foundPlace = foundPlaces.get(0);

        assertNotNull(foundPlace);
        assertEquals(foundPlace.getPlates().get(1).getPattern(), plateStartToFind);
    }

    @MediumTest public void testGetByPlateIsSortedProperly() {
        Place twoLetter1 = TestDataFactory.createStandardPlace("1", "AA", Place.Type.POWIAT_CITY);
        Place twoLetter2 = TestDataFactory.createStandardPlace("2", "AZ", Place.Type.POWIAT_CITY);
        Place threeLetter1 = TestDataFactory.createStandardPlace("3", "AAA", Place.Type.POWIAT_CITY);
        Place threeLetter2 = TestDataFactory.createStandardPlace("4", "AWW", Place.Type.POWIAT_CITY);
        Place threeLetter3 = TestDataFactory.createStandardPlace("5", "AZZ", Place.Type.POWIAT_CITY);

        databaseHelper.getPlaceDao().add(twoLetter1);
        databaseHelper.getPlaceDao().add(twoLetter2);
        databaseHelper.getPlaceDao().add(threeLetter1);
        databaseHelper.getPlaceDao().add(threeLetter2);
        databaseHelper.getPlaceDao().add(threeLetter3);

        int limit = 4;

        List<Place> places = databaseHelper.getPlaceDao().getPlaceListForPlateStart("A", limit);

        // check if limit is correct
        assertEquals(limit, places.size());

        Collator collator = Collator.getInstance();
        for (int j = 0; j <= 1; j++) {
            String lastOne = places.get(j * 2).getPlates().get(0).getPattern();
            Plate plate = places.get(j * 2 + 1).getPlates().get(0);
            String platePattern = plate.getPattern();

            // check for pattern length
            assertEquals(j + 2, platePattern.length());
            assertEquals(j + 2, lastOne.length());

            // check alphabetical order
            CollationKey key = collator.getCollationKey(platePattern);
            int compare = key.compareTo(collator.getCollationKey(lastOne));
            assertTrue(compare > 0);
        }
    }

    @MediumTest public void testSearchPlacesWithDiacritics() {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type.POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("swirzyce", dummyPlate, Place.Type.POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("doboszyce", dummyPlate, Place.Type.POWIAT_CITY));

        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", null);
        assertTrue(!foundPlaces.isEmpty());
        assertEquals("świdnica", foundPlaces.get(0).getName());

        foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("swid", null);

        assertTrue(!foundPlaces.isEmpty());
        assertEquals("świdnica", foundPlaces.get(0).getName());
    }

    @MediumTest public void testSearchPlacesLimit() {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type.POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnico", dummyPlate, Place.Type.POWIAT_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnice", dummyPlate, Place.Type.POWIAT_CITY));

        int limit = 2;
        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", limit);

        assertEquals(limit, foundPlaces.size());
    }

    @MediumTest public void testSearchPlacesSortOrderByType() {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnica", dummyPlate, Place.Type.VOIVODE_CITY));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnico", dummyPlate, Place.Type.TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnice", dummyPlate, Place.Type.VILLAGE));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świdnicy", dummyPlate, Place.Type.SPECIAL));

        List<Place> foundPlaces = databaseHelper.getPlaceDao().getPlaceListByName("świ", null);

        assertEquals(4, foundPlaces.size());

        int lastOne = foundPlaces.get(0).getType().ordinal();
        for (int i = 1; i < 4; i++) {
            int typeOrdinal = foundPlaces.get(i).getType().ordinal();
            assertTrue(typeOrdinal >= lastOne);
        }
    }

    @MediumTest public void testSearchPlacesSortOrderByName() {
        String dummyPlate = "AAA";
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("śwarądz", dummyPlate, Place.Type.TOWN));
        databaseHelper.getPlaceDao().add(TestDataFactory.createStandardPlace("świnoujście", dummyPlate, Place.Type.TOWN));
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
    }
}
