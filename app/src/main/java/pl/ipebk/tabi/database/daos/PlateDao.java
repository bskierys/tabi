/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.tables.PlatesTable;

public class PlateDao extends Dao<Plate> {
    public PlateDao(SQLiteDatabase database) {
        super(Plate.class, database);
        table = new PlatesTable();
    }

    public List<Plate> getPlatesForPlaceId(long placeId) {
        List<Plate> plates = new ArrayList<>();
        String selection = PlatesTable.COLUMN_PLACE_ID + " = ?";
        String[] selectionArgs = {Long.toString(placeId)};

        Cursor cursor = db.query(table.getTableName(), table.getQualifiedColumns(),
                selection, selectionArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Plate model = table.cursorToModel(cursor);
                plates.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return plates;
    }

    public void updateOrAdd(Plate plate) {
        String whereClause = String.format(" %s = ? ", PlatesTable.COLUMN_PLATE);
        List<String> whereParams = new ArrayList<>();
        whereParams.add(plate.getPattern());

        if (plate.getEnd() == null) {
            whereClause += PlatesTable.COLUMN_PLATE_END + " is null ";
        } else {
            whereClause += String.format(" %s = ? ", PlatesTable.COLUMN_PLATE_END);
            whereParams.add(plate.getEnd());
        }

        if (db.update(table.getTableName(), table.modelToContentValues(plate),
                whereClause, whereParams.toArray(new String[whereParams.size()])) == 0) {
            add(plate);
        }
    }

    public void updateOrAdd(List<Plate> plates) {
        for (Plate plate : plates) {
            updateOrAdd(plate);
        }
    }
}
