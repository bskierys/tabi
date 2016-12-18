package pl.ipebk.tabi.presentation.model.place;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.readmodel.LicensePlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;

import static org.junit.Assert.*;

public class PlaceTest {
    @Test public void testGetMainPlateForNoPlates() throws Exception {
        Place malbork = new Place("Malbork", PlaceType.TOWN, null, null, null, new ArrayList<>());

        LicensePlateDto plate = malbork.getMainPlate();

        assertNull(plate);
    }

    @Test public void testGetMainPlate() throws Exception {
        String mainPlatePattern = "TAB";
        Place malbork = createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        LicensePlateDto mainPlate = malbork.getMainPlate();

        assertEquals(mainPlatePattern, mainPlate.pattern());
    }


    @Test public void testGetPlateMatchingPatternFullPlate() {
        String plateToFind = "KK";
        Place malbork = createPlaceWithPlates("KR", plateToFind, "KM");

        LicensePlateDto plate = malbork.getPlateMatchingPattern(plateToFind);

        assertEquals(plateToFind, plate.pattern());
    }

    @Test public void testGetPlateMatchingPatternOneLetter() {
        String plateToFind = "KR";
        Place malbork = createPlaceWithPlates(plateToFind, "KK", "KM");

        LicensePlateDto plate = malbork.getPlateMatchingPattern("K");

        assertEquals(plateToFind, plate.pattern());
    }

    @Test public void testGetPlateMatchingPatternNull() {
        String mainPlatePattern = "TAB";
        Place malbork = createPlaceWithPlates(mainPlatePattern, "BAT", "GAP");

        LicensePlateDto plate = malbork.getPlateMatchingPattern(null);

        assertEquals(mainPlatePattern, plate.pattern());
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
        return new Place("Malbork", PlaceType.TOWN, null, null, null, getListOfPlates(patterns));
    }

    private List<LicensePlateDto> getListOfPlates(String... patterns) {
        List<LicensePlateDto> plates = new ArrayList<>();
        for (int i = 0; i < patterns.length; i++) {
            plates.add(createPlate(patterns[i]));
        }
        return plates;
    }

    public LicensePlateDto createPlate(String pattern) {
        return LicensePlateDto.create(pattern, null);
    }
}