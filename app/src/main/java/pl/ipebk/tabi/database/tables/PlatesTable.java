/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.tables;

import android.content.ContentValues;
import android.database.Cursor;

import pl.ipebk.tabi.database.base.Table;
import pl.ipebk.tabi.database.models.Plate;

public class PlatesTable extends Table<Plate> {
    public static final String COLUMN_PLACE_ID = "place_id";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_PLATE_END = "plate_end";

    public static final String TABLE_NAME = "additional_plates";

    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID,
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

    @Override public String getTableName() {
        return TABLE_NAME;
    }

    @Override protected String[] getTableColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public Plate cursorToModel(Cursor cursor) {
        Plate plate = new Plate();
        plate.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        plate.setPlaceId(cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_ID)));
        plate.setPattern(cursor.getString(cursor.getColumnIndex(COLUMN_PLATE)));
        plate.setEnd(cursor.getString(cursor.getColumnIndex(COLUMN_PLATE_END)));
        return plate;
    }

    @Override public ContentValues modelToContentValues(Plate model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLACE_ID, model.getPlaceId());
        values.put(COLUMN_PLATE, model.getPattern());
        values.put(COLUMN_PLATE_END, model.getEnd());
        return values;
    }
}
