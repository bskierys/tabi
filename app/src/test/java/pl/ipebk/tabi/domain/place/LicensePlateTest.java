package pl.ipebk.tabi.domain.place;

import org.junit.Test;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

import static org.junit.Assert.*;

public class LicensePlateTest {
    @Test public void testToString() throws Exception {
        String pattern = "TAB";
        String end = "I";
        LicensePlate plate = new LicensePlate(new AggregateId(0), pattern, end);

        String expected = pattern + "..." + "I";
        String actual = plate.toString();

        assertEquals(expected, actual);
    }

    @Test public void testToStringWhenEndNull() throws Exception {
        String pattern = "TAB";
        LicensePlate plate = new LicensePlate(new AggregateId(0), pattern, null);

        String actual = plate.toString();

        assertEquals(pattern, actual);
    }
}