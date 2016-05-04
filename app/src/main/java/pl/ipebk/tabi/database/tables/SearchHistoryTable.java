/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

import pl.ipebk.tabi.database.base.Table;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
import timber.log.Timber;

public class SearchHistoryTable extends Table<SearchHistory> {
    public static final String COLUMN_PLACE_ID = "place_id";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_TIME_SEARCHED = "time_searched";
    public static final String COLUMN_SEARCH_TYPE = "search_type";

    public static final String TABLE_NAME = "search_history";

    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_PLACE_ID,
            COLUMN_PLATE,
            COLUMN_TIME_SEARCHED,
            COLUMN_SEARCH_TYPE
    };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + TABLE_COLUMNS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_COLUMNS[1] + " INTEGER NOT NULL, "
            + TABLE_COLUMNS[2] + " TEXT, "
            + TABLE_COLUMNS[3] + " INTEGER NOT NULL, "
            + TABLE_COLUMNS[4] + " INTEGER DEFAULT " + Integer.toString(SearchType.UNKNOWN.ordinal()) + ", "
            + "FOREIGN KEY (" + COLUMN_PLACE_ID + ") REFERENCES " + PlacesTable.TABLE_NAME + "(" + COLUMN_ID + ")"
            + " ON DELETE CASCADE );";

    @Override public String getTableName() {
        return TABLE_NAME;
    }

    @Override protected String[] getTableColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public SearchHistory cursorToModel(Cursor cursor) {
        SearchHistory history = new SearchHistory();
        history.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));

        history.setPlaceId(cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_ID)));
        history.setPlate(cursor.getString(cursor.getColumnIndex(COLUMN_PLATE)));
        history.setTimeSearched(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_SEARCHED))));

        int type = cursor.getInt(cursor.getColumnIndex(COLUMN_SEARCH_TYPE));
        if (type >= SearchType.values().length) {
            history.setSearchType(SearchType.UNKNOWN);
        } else {
            history.setSearchType(SearchType.values()[type]);
        }

        return history;
    }

    @Override public ContentValues modelToContentValues(SearchHistory model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, model.getPlaceId());

        if (model.getTimeSearched() != null) {
            values.put(COLUMN_TIME_SEARCHED, model.getTimeSearched().getTime());
        } else {
            Timber.e("time searched is not set in history");
        }
        values.put(COLUMN_PLATE, model.getPlate());

        if (model.getSearchType() == null) {
            values.put(COLUMN_SEARCH_TYPE, SearchType.UNKNOWN.ordinal());
        } else {
            values.put(COLUMN_SEARCH_TYPE, model.getSearchType().ordinal());
        }

        return values;
    }
}
