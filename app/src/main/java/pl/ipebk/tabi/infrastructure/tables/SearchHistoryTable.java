/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Date;

import pl.ipebk.tabi.infrastructure.base.Table;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import timber.log.Timber;

public class SearchHistoryTable extends Table<SearchHistoryModel> {
    public static final String COLUMN_PLACE_ID = "place_id";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_TIME_SEARCHED = "time_searched";
    public static final String COLUMN_SEARCH_TYPE = "search_type";

    public static final String TABLE_NAME = "search_history";

    private static final String[] TABLE_COLUMNS = {
            BaseColumns._ID,
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
            + "FOREIGN KEY (" + COLUMN_PLACE_ID + ") REFERENCES " + PlacesTable.TABLE_NAME + "(" + BaseColumns._ID + ")"
            + " ON DELETE CASCADE );";

    @Override public String getName() {
        return TABLE_NAME;
    }

    @Override protected String[] getColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public SearchHistoryModel cursorToModel(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        long placeId = cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_ID));
        String plate = cursor.getString(cursor.getColumnIndex(COLUMN_PLATE));
        Date timeSearched = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_SEARCHED)));
        int type = cursor.getInt(cursor.getColumnIndex(COLUMN_SEARCH_TYPE));

        return SearchHistoryModel.create(id, placeId, plate, timeSearched, type);
    }

    @Override public ContentValues modelToContentValues(SearchHistoryModel model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, model.placeId());

        if (model.timeSearched() != null) {
            values.put(COLUMN_TIME_SEARCHED, model.timeSearched().getTime());
        } else {
            Timber.e("time searched is not set in history");
        }
        values.put(COLUMN_PLATE, model.plate());
        values.put(COLUMN_SEARCH_TYPE, model.searchType());

        return values;
    }
}