package pl.ipebk.tabi.domain.place;

import org.junit.Test;

import static org.junit.Assert.*;

public class LicensePlateTest {
    @Test public void testToString() throws Exception {
        LicensePlate plate = new LicensePlate();
        String pattern = "TAB";
        String end = "I";
        plate.setPattern(pattern);
        plate.setEnd(end);

        String expected = pattern + "..." + "I";
        String actual = plate.toString();

        assertEquals(expected, actual);
    }

    @Test public void testToStringWhenEndNull() throws Exception {
        LicensePlate plate = new LicensePlate();
        String pattern = "TAB";
        plate.setPattern(pattern);

        String actual = plate.toString();

        assertEquals(pattern, actual);
    }
}