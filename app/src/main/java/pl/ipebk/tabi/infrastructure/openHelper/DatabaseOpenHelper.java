/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.openHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.daos.PlacesToSearchDao;
import pl.ipebk.tabi.infrastructure.daos.PlateDao;
import pl.ipebk.tabi.infrastructure.daos.PlatesToSearchDao;
import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.infrastructure.tables.PlatesTable;
import pl.ipebk.tabi.infrastructure.tables.SearchHistoryTable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Implementation of {@link android.database.sqlite.SQLiteOpenHelper} Responsible for opening and managing database.
 */
@Singleton
public class DatabaseOpenHelper extends SQLiteAssetHelper implements DatabaseHelperInterface {
    private static final String DATABASE_NAME = "tabi.db";
    private static final int DATABASE_VERSION = 2;

    protected static SQLiteDatabase db;
    protected BriteDatabase briteDb;
    protected PlaceDao placeDao;
    protected PlateDao plateDao;
    protected SearchHistoryDao searchHistoryDao;
    protected PlacesToSearchDao placesToSearchDao;
    protected PlatesToSearchDao platesToSearchDao;
    private boolean setupDone;

    @Inject public DatabaseOpenHelper(@ApplicationContext Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
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

    @Override public PlacesToSearchDao getPlacesToSearchDao() {
        return placesToSearchDao;
    }

    @Override public PlatesToSearchDao getPlatesToSearchDao() {
        return platesToSearchDao;
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
            searchHistoryDao = new SearchHistoryDao(briteDb);
        }

        if (placesToSearchDao == null) {
            placesToSearchDao = new PlacesToSearchDao(briteDb);
        }

        if (platesToSearchDao == null) {
            platesToSearchDao = new PlatesToSearchDao(briteDb);
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
