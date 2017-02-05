/*
* author: Bartlomiej Kierys
* date: 2017-02-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.utils;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class that helps to mock shared preferences for test purposes
 */
public class ShadowSharedPreferences implements SharedPreferences {
    private HashMap<String, Object> mockedPreferences = new HashMap<>();

    @Override public Map<String, ?> getAll() {
        return mockedPreferences;
    }

    @Nullable @Override public String getString(String s, String s1) {
        Object stored = mockedPreferences.get(s);
        if(stored !=null) {
            return (String) stored;
        } else {
            return s1;
        }
    }

    @Nullable @Override public Set<String> getStringSet(String s, Set<String> set) {
        return null;
    }

    @Override public int getInt(String s, int i) {
        Object stored = mockedPreferences.get(s);
        if(stored !=null) {
            return (int) stored;
        } else {
            return i;
        }
    }

    @Override public long getLong(String s, long l) {
        return 0;
    }

    @Override public float getFloat(String s, float v) {
        return 0;
    }

    @Override public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override public boolean contains(String s) {
        return false;
    }

    @Override public Editor edit() {
        return new Editor();
    }

    @Override public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    private class Editor implements SharedPreferences.Editor {
        @Override public SharedPreferences.Editor putString(String s, String s1) {
            mockedPreferences.put(s, s1);
            return this;
        }

        @Override public SharedPreferences.Editor putStringSet(String s, Set<String> set) {
            mockedPreferences.put(s, set);
            return this;
        }

        @Override public SharedPreferences.Editor putInt(String s, int i) {
            mockedPreferences.put(s, i);
            return this;
        }

        @Override public SharedPreferences.Editor putLong(String s, long l) {
            mockedPreferences.put(s, l);
            return this;
        }

        @Override public SharedPreferences.Editor putFloat(String s, float v) {
            mockedPreferences.put(s, v);
            return this;
        }

        @Override public SharedPreferences.Editor putBoolean(String s, boolean b) {
            mockedPreferences.put(s, b);
            return this;
        }

        @Override public SharedPreferences.Editor remove(String s) {
            mockedPreferences.remove(s);
            return this;
        }

        @Override public SharedPreferences.Editor clear() {
            return this;
        }

        @Override public boolean commit() {
            return true;
        }

        @Override public void apply() {
            // do nothing
        }
    }
}
