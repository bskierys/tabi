/*
* author: Bartlomiej Kierys
* date: 2016-06-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Class designed for easy shared preferences access. Instantiate it for access to all essential settings.
 */
public class PreferenceHelper {
    // constants for preferences
    private static final String MAIN_SCREEN_VIEWS = "main_screen_views_number";
    private static final String DEMO_GREETING_WAS_SHOWN = "demo_greeting_was_shown";

    private SharedPreferences sharedPreferences;

    @Inject public PreferenceHelper(SharedPreferences preferences) {
        this.sharedPreferences = preferences;
    }

    public void increaseMainScreenVisited() {
        int alreadyVisited = howManyTimesMainScreenVisited();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MAIN_SCREEN_VIEWS, alreadyVisited + 1);
        editor.apply();
    }

    public int howManyTimesMainScreenVisited() {
        return sharedPreferences.getInt(MAIN_SCREEN_VIEWS, 0);
    }

    public void demoGreetingShown() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DEMO_GREETING_WAS_SHOWN, true);
        editor.apply();
    }

    public boolean wasDemoGreetingShown() {
        return sharedPreferences.getBoolean(DEMO_GREETING_WAS_SHOWN, false);
    }
}
