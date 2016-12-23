/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import pl.ipebk.tabi.infrastructure.base.Table;
import pl.ipebk.tabi.infrastructure.models.PlateModel;

public class PlatesTable extends Table<PlateModel> {
    public static final String COLUMN_PLACE_ID = "place_id";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_PLATE_END = "plate_end";

    public static final String TABLE_NAME = "additional_plates";

    private static final String[] TABLE_COLUMNS = {
            BaseColumns._ID,
            COLUMN_PLACE_ID,
            COLUMN_PLATE,
            COLUMN_PLATE_END
    };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + TABLE_COLUMNS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_COLUMNS[1] + " INTEGER NOT NULL, "
            + TABLE_COLUMNS[2] + " TEXT, "
            + TABLE_COLUMNS[3] + " TEXT );";

    @Override public String getName() {
        return TABLE_NAME;
    }

    @Override protected String[] getColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public PlateModel cursorToModel(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        long placeId = cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_ID));
        String pattern = cursor.getString(cursor.getColumnIndex(COLUMN_PLATE));
        String end = cursor.getString(cursor.getColumnIndex(COLUMN_PLATE_END));

        return PlateModel.create(id, placeId, pattern, end);
    }

    @Override public ContentValues modelToContentValues(PlateModel model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, model.placeId());
        values.put(COLUMN_PLATE, model.getDto().pattern());
        values.put(COLUMN_PLATE_END, model.getDto().end());
        return values;
    }
}
