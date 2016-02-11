/*
* author: Bartlomiej Kierys
* date: 2016-02-12
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;

class DatabaseHelper {
    static SQLiteDatabase db;
    PlaceDao placeDao;
    PlateDao plateDao;
    SearchHistoryDao searchHistoryDao;
    boolean setupDone;

    PlaceDao getPlaceDao() {
        return placeDao;
    }

    PlateDao getPlateDao() {
        return plateDao;
    }

    SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    void setupDao() {
        if (db == null) {
            db = getWritableDatabase();
        }

        if (plateDao == null) {
            plateDao = new PlateDao(db);
        }

        if (placeDao == null) {
            placeDao = new PlaceDao(db, plateDao);
        }

        if (searchHistoryDao == null) {
            searchHistoryDao = new SearchHistoryDao(db, placeDao);
        }
    }

    /**
     * Opens database connection if it is closed. Does nothing
     * if connection is already opened.
     */
    public synchronized void init() {
        if (!setupDone) {
            Log.d(TAG, "Setting up database.");
            setupDao();
            setupDone = true;
        } else Log.d(TAG, "Database already opened.");
    }

    /**
     * Manually closes opened database connection.
     */
    public void destroy() {
        if (setupDone) {
            Log.d(TAG, "Closing database.");
            db.close();
            setupDone = false;
        } else Log.d(TAG, "Database already closed.");
    }

    /**
     * Drops all tables and then recreates them.
     */
    public void purge() {
        if (setupDone) {
            Log.d(TAG, "Clearing all tables.");
            PlacesTable placesTable = new PlacesTable();
            PlatesTable platesTable = new PlatesTable();
            SearchHistoryTable searchHistoryTable = new SearchHistoryTable();
            platesTable.drop(db);
            searchHistoryTable.drop(db);
            placesTable.drop(db);
            placesTable.create(db);
            platesTable.create(db);
            searchHistoryTable.create(db);
        }
    }
}
