/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.openHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;
import pl.ipebk.tabi.database.openHelper.DatabaseHelperInterface;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;

/**
 * Implementation of {@link android.database.sqlite.SQLiteOpenHelper}
 * Responsible for opening and managing database.
 */
public class DatabaseOpenHelper extends SQLiteAssetHelper implements DatabaseHelperInterface {
    private static final String TAG = "TABI.Dtbs";
    private static final String DATABASE_NAME = "tabi.db";
    private static final int DATABASE_VERSION = 1;

    protected static SQLiteDatabase db;
    protected PlaceDao placeDao;
    protected PlateDao plateDao;
    protected SearchHistoryDao searchHistoryDao;
    private boolean setupDone;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public PlaceDao getPlaceDao() {
        return placeDao;
    }

    @Override public PlateDao getPlateDao() {
        return plateDao;
    }

    @Override public SearchHistoryDao getSearchHistoryDao() {
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

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override public synchronized void init() {
        if (!setupDone) {
            Log.d(TAG, "Setting up database.");
            setupDao();
            setupDone = true;
        } else Log.d(TAG, "Database already opened.");
    }

    @Override public void destroy() {
        if (setupDone) {
            Log.d(TAG, "Closing database.");
            db.close();
            setupDone = false;
        } else Log.d(TAG, "Database already closed.");
    }

    @Override public void purge() {
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
