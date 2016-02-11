/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;

/**
 * This is test open helper. It is used to mock original helper in database tests
 * and to recreate database from SQL instead of copying it from assets.
 * It's code should be almost identical to original helper (exception:
 * onCreate and onUpgrade methods).
 */
public class DatabaseTestOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "TABI.Dtbs.Test";
    private static final String DATABASE_NAME = "tabi.db";
    private static final int DATABASE_VERSION = 1;

    protected static SQLiteDatabase db;
    protected PlaceDao placeDao;
    protected PlateDao plateDao;
    protected SearchHistoryDao searchHistoryDao;
    private boolean setupDone;

    public DatabaseTestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public PlaceDao getPlaceDao() {
        return placeDao;
    }

    public PlateDao getPlateDao() {
        return plateDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    private void setupDao() {
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

    @Override public void onCreate(SQLiteDatabase database) {
        PlatesTable platesTable = new PlatesTable();
        PlacesTable placesTable = new PlacesTable();
        SearchHistoryTable searchHistoryTable = new SearchHistoryTable();
        placesTable.create(database);
        platesTable.create(database);
        searchHistoryTable.create(database);
    }

    @Override public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        PlatesTable platesTable = new PlatesTable();
        PlacesTable placesTable = new PlacesTable();
        SearchHistoryTable searchHistoryTable = new SearchHistoryTable();
        placesTable.upgrade(database, oldVersion, newVersion);
        platesTable.upgrade(database, oldVersion, newVersion);
        searchHistoryTable.upgrade(database, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
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
