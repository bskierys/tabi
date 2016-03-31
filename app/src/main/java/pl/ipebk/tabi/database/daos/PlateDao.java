/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.database.base.Dao;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.tables.PlatesTable;

public class PlateDao extends Dao<Plate> {
    public PlateDao(BriteDatabase database) {
        super(Plate.class, database);
        table = new PlatesTable();
    }

    public List<Plate> getPlatesForPlaceId(long placeId) {
        String selection = PlatesTable.COLUMN_PLACE_ID + " = ?";
        String[] selectionArgs = {Long.toString(placeId)};

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), selection, null, null, null, null);

        Cursor cursor = db.query(sql, selectionArgs);
        return getListOfModelsForCursor(cursor);
    }

    /**
     * Checks if given plate is already in database. If so, updates it,
     * if no, adds new one.
     *
     * @param plate Instance of {@link Plate} to add or update
     */
    public void updateOrAdd(Plate plate) {
        String whereClause = String.format(" %s = ? AND %s = ? AND ",
                PlatesTable.COLUMN_PLATE, PlatesTable.COLUMN_PLACE_ID);
        List<String> whereParams = new ArrayList<>();
        whereParams.add(plate.getPattern());
        whereParams.add(Long.toString(plate.getPlaceId()));

        if (plate.getEnd() == null) {
            whereClause += PlatesTable.COLUMN_PLATE_END + " IS NULL ";
        } else {
            whereClause += String.format(" %s = ? ", PlatesTable.COLUMN_PLATE_END);
            whereParams.add(plate.getEnd());
        }

        int rowsUpdated = db.update(table.getTableName(), table.modelToContentValues(plate),
                whereClause, whereParams.toArray(new String[whereParams.size()]));

        if (rowsUpdated == 0) {
            add(plate);
        }
    }

    /**
     * Checks if given plates are already in database. If so, updates it,
     * if no, adds new ones.
     *
     * @param plates List of {@link Plate} to add or update
     */
    public void updateOrAdd(List<Plate> plates) {
        for (Plate plate : plates) {
            updateOrAdd(plate);
        }
    }
}
