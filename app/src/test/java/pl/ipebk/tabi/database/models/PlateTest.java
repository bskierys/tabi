package pl.ipebk.tabi.database.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlateTest {
    @Test public void testToString() throws Exception {
        Plate plate = new Plate();
        String pattern = "TAB";
        String end = "I";
        plate.setPattern(pattern);
        plate.setEnd(end);

        String expected = pattern + "..." + "I";
        String actual = plate.toString();

        assertEquals(expected, actual);
    }

    @Test public void testToStringWhenEndNull() throws Exception {
        Plate plate = new Plate();
        String pattern = "TAB";
        plate.setPattern(pattern);

        String actual = plate.toString();

        assertEquals(pattern, actual);
    }
}