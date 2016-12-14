package pl.ipebk.tabi.domain.place;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PlaceTest {
    @Test public void testGetMainPlateForNoPlates() throws Exception {
        Place malbork = new Place();
        malbork.setName("Malbork");

        LicensePlate plate = malbork.getMainPlate();

        assertNull(plate);
    }

    @Test public void testGetMainPlate() throws Exception {
        String mainPlatePattern = "TAB";
        Place malbork = createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        LicensePlate mainPlate = malbork.getMainPlate();

        assertEquals(mainPlatePattern, mainPlate.getPattern());
    }


    @Test public void testGetPlateMatchingPatternFullPlate() {
        String plateToFind = "KK";
        Place malbork = createPlaceWithPlates("KR", plateToFind, "KM");

        LicensePlate plate = malbork.getPlateMatchingPattern(plateToFind);

        assertEquals(plateToFind, plate.getPattern());
    }

    @Test public void testGetPlateMatchingPatternOneLetter() {
        String plateToFind = "KR";
        Place malbork = createPlaceWithPlates(plateToFind, "KK", "KM");

        LicensePlate plate = malbork.getPlateMatchingPattern("K");

        assertEquals(plateToFind, plate.getPattern());
    }

    @Test public void testGetPlateMatchingPatternNull() {
        String mainPlatePattern = "TAB";
        Place malbork = createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        LicensePlate plate = malbork.getPlateMatchingPattern(null);

        assertEquals(mainPlatePattern, plate.getPattern());
    }

    @Test public void testPlatesToString() {
        String plateToFind = "KR";
        Place malbork = createPlaceWithPlates(plateToFind, "KK");

        String expected = "KR, KK";
        String actual = malbork.platesToString();

        assertEquals(expected, actual);
    }

    @Test public void testPlatesToStringExceptMatching() {
        String plateToFind = "KR";
        Place malbork = createPlaceWithPlates(plateToFind, "KK", "KM");

        String expected = "KR, KK";
        String actual = malbork.platesToStringExceptMatchingPattern("KM");

        assertEquals(expected, actual);
    }

    private Place createPlaceWithPlates(String... patterns) {
        Place malbork = new Place();
        malbork.setName("Malbork");
        malbork.setPlates(getListOfPlates(patterns));

        return malbork;
    }

    private static List<LicensePlate> getListOfPlates(String... patterns) {
        List<LicensePlate> plates = new ArrayList<>();
        for (int i = 0; i < patterns.length; i++) {
            plates.add(createPlate(patterns[i]));
        }
        return plates;
    }

    public static LicensePlate createPlate(String pattern) {
        LicensePlate plate = new LicensePlate();
        plate.setPattern(pattern);
        return plate;
    }
}