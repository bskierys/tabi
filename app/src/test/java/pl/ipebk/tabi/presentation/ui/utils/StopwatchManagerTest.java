package pl.ipebk.tabi.presentation.ui.utils;

import org.junit.Before;
import org.junit.Test;

import pl.ipebk.tabi.presentation.utils.Stopwatch;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StopwatchManagerTest {
    private StopwatchManager manager;

    @Before public void setUp() throws Exception {
        manager = new StopwatchManager();
    }

    @Test public void testGetDefaultStopwatch() throws Exception {
        Stopwatch stopwatch = manager.getDefaultStopwatch();
        Stopwatch secondStopwatch = manager.getDefaultStopwatch();

        assertEquals(stopwatch, secondStopwatch);
    }

    @Test public void testGetStopwatchForKey() throws Exception {
        Stopwatch tab = manager.getStopwatch("tab");
        Stopwatch bat = manager.getStopwatch("bat");
        Stopwatch tab2 = manager.getStopwatch("tab");

        assertEquals(tab, tab2);
        assertNotEquals(tab, bat);
    }
}