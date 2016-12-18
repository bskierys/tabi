package pl.ipebk.tabi.readmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LicensePlateDtoTest {
    @Test public void testToString() throws Exception {
        String pattern = "TAB";
        String end = "I";
        LicensePlateDto plate = LicensePlateDto.create(pattern, end);

        String expected = pattern + "..." + "I";
        String actual = plate.toString();

        assertEquals(expected, actual);
    }

    @Test public void testToStringWhenEndNull() throws Exception {
        String pattern = "TAB";
        LicensePlateDto plate = LicensePlateDto.create(pattern, null);

        String actual = plate.toString();

        assertEquals(pattern, actual);
    }
}