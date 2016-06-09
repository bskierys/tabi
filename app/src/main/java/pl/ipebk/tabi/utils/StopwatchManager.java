/*
* author: Bartlomiej Kierys
* date: 2016-05-16
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Class to manage and propagate stopwatches across our app. It can provide default, singleton and new stopwatch so you
 * can start and stop watches across app to measure time.
 */
public class StopwatchManager {
    private static final String DEFAULT_KEY = "default_stopwatch";
    private static HashMap<String, Stopwatch> stopwatches;

    @Inject public StopwatchManager() {
        stopwatches = new HashMap<>();
        stopwatches.put(DEFAULT_KEY, new Stopwatch());
    }

    /**
     * @return default Stopwatch for whole application
     */
    public Stopwatch getDefaultStopwatch() {
        return stopwatches.get(DEFAULT_KEY);
    }

    /**
     * @return New instance of stopwatch for you to use
     */
    public Stopwatch getStopwatch() {
        return new Stopwatch();
    }

    /**
     * If stopwatch for key already exists returns it. If not, gets you new one and remembers it. Next time it will
     * return same stopwatch for that key.
     *
     * @param key Key to distinct your stopwatch
     */
    public Stopwatch getStopwatch(String key) {
        Stopwatch stopwatch = stopwatches.get(key);

        if (stopwatch == null) {
            stopwatch = new Stopwatch();
            stopwatches.put(key, stopwatch);
        }

        return stopwatch;
    }
}
