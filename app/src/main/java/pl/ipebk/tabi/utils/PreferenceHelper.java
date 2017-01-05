/*
* author: Bartlomiej Kierys
* date: 2016-06-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

/**
 * Class designed for easy shared preferences access. Instantiate it for access to all essential settings.
 */
public class PreferenceHelper {
    // constants for preferences
    private static final String MAIN_SCREEN_VIEWS = "main_screen_views_number";

    private SharedPreferences sharedPreferences;

    // TODO: 2016-12-19 SharedPreferences should be injected
    @Inject public PreferenceHelper(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void increaseMainScreenVisited(){
        int alreadyVisited = howManyTimesMainScreenVisited();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MAIN_SCREEN_VIEWS, alreadyVisited + 1);
        editor.apply();
    }

    public int howManyTimesMainScreenVisited() {
        return sharedPreferences.getInt(MAIN_SCREEN_VIEWS, 0);
    }

    public void setVisited(Class c, String additional, boolean isVisited) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getKey(c, additional), isVisited);
        editor.apply();
    }

    public boolean isVisited(Class c, String additional) {
        return sharedPreferences.getBoolean(getKey(c, additional), false);
    }

    private String getKey(Class c, String additional) {
        return c.getCanonicalName() + ";" + additional;
    }
}
