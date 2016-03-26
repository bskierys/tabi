package pl.ipebk.tabi.database.models;

import org.junit.Test;

import pl.ipebk.tabi.test.common.TestDataFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PlaceTest {
    @Test public void testGetMainPlateForNoPlates() throws Exception {
        Place malbork = new Place();
        malbork.setName("Malbork");

        Plate plate = malbork.getMainPlate();

        assertNull(plate);
    }

    @Test public void testGetMainPlate() throws Exception {
        String mainPlatePattern = "TAB";
        Place malbork = TestDataFactory.createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        Plate mainPlate = malbork.getMainPlate();

        assertEquals(mainPlatePattern, mainPlate.getPattern());
    }


    @Test public void testGetPlateMatchingPatternFullPlate() {
        String plateToFind = "KK";
        Place malbork = TestDataFactory.createPlaceWithPlates("KR", plateToFind, "KM");

        Plate plate = malbork.getPlateMatchingPattern(plateToFind);

        assertEquals(plateToFind, plate.getPattern());
    }

    @Test public void testGetPlateMatchingPatternOneLetter() {
        String plateToFind = "KR";
        Place malbork = TestDataFactory.createPlaceWithPlates(plateToFind, "KK", "KM");

        Plate plate = malbork.getPlateMatchingPattern("K");

        assertEquals(plateToFind, plate.getPattern());
    }

    @Test public void testGetPlateMatchingPatternNull() {
        String mainPlatePattern = "TAB";
        Place malbork = TestDataFactory.createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        Plate plate = malbork.getPlateMatchingPattern(null);

        assertEquals(mainPlatePattern, plate.getPattern());
    }

    @Test public void testPlatesToString() {
        String plateToFind = "KR";
        Place malbork = TestDataFactory.createPlaceWithPlates(plateToFind, "KK");

        String expected = "KR, KK";
        String actual = malbork.platesToString();

        assertEquals(expected, actual);
    }

    @Test public void testPlatesToStringExceptMatching() {
        String plateToFind = "KR";
        Place malbork = TestDataFactory.createPlaceWithPlates(plateToFind, "KK", "KM");

        String expected = "KR, KK";
        String actual = malbork.platesToStringExceptMatchingPattern("KM");

        assertEquals(expected, actual);
    }


}