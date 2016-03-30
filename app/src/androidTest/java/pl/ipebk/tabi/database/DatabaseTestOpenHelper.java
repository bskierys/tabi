/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;
import pl.ipebk.tabi.database.openHelper.DatabaseHelperInterface;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * This is test open helper. It is used to mock original helper in database tests
 * and to recreate database from SQL instead of copying it from assets.
 * It's code should be almost identical to original helper (exception:
 * onCreate and onUpgrade methods).
 */
public class DatabaseTestOpenHelper extends SQLiteOpenHelper implements DatabaseHelperInterface {
    private static final String DATABASE_NAME = "tabi.db";
    private static final int DATABASE_VERSION = 1;

    protected SQLiteDatabase db;
    protected BriteDatabase briteDb;
    protected PlaceDao placeDao;
    protected PlateDao plateDao;
    protected SearchHistoryDao searchHistoryDao;
    private boolean setupDone;

    public DatabaseTestOpenHelper(Context context) {
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

        SqlBrite sqlBrite = SqlBrite.create();
        briteDb = sqlBrite.wrapDatabaseHelper(this, Schedulers.io());

        if (plateDao == null) {
            plateDao = new PlateDao(briteDb);
        }

        if (placeDao == null) {
            placeDao = new PlaceDao(briteDb, plateDao);
        }

        if (searchHistoryDao == null) {
            searchHistoryDao = new SearchHistoryDao(briteDb, placeDao);
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

    @Override public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override public synchronized void init() {
        if (!setupDone) {
            Timber.d("Setting up database");
            setupDao();
            setupDone = true;
        } else {
            Timber.d("Database already opened");
        }
    }

    @Override public void destroy() {
        if (setupDone) {
            Timber.d("Closing database");
            db.close();
            setupDone = false;
        } else {
            Timber.d("Database already closed");
        }
    }

    @Override public void purge() {
        if (setupDone) {
            Timber.d("Clearing all tables");
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