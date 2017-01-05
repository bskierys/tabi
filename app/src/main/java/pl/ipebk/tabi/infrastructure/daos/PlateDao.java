/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.infrastructure.base.Dao;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.infrastructure.tables.PlatesTable;

public class PlateDao extends Dao<PlateModel> {
    public PlateDao(BriteDatabase database) {
        super(PlateModel.class, database);
        table = new PlatesTable();
    }

    public List<PlateModel> getPlatesForPlaceId(long placeId) {
        String selection = PlatesTable.COLUMN_PLACE_ID + " = ?";
        String[] selectionArgs = {Long.toString(placeId)};

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getName(),
                                                         table.getQualifiedColumns(), selection, null, null, null,
                                                         null);

        Cursor cursor = db.query(sql, selectionArgs);
        return getListOfModelsForCursor(cursor);
    }

    /**
     * Checks if given plate is already in database. If so, updates it, if no, adds new one.
     *
     * @param plate Instance of {@link PlateModel} to add or update
     */
    public void updateOrAdd(PlateModel plate) {
        String whereClause = String.format(" %s = ? AND %s = ? AND ",
                                           PlatesTable.COLUMN_PLATE, PlatesTable.COLUMN_PLACE_ID);
        List<String> whereParams = new ArrayList<>();
        whereParams.add(plate.getDto().pattern());
        whereParams.add(Long.toString(plate.placeId()));

        if (plate.getDto().end() == null) {
            whereClause += PlatesTable.COLUMN_PLATE_END + " IS NULL ";
        } else {
            whereClause += String.format(" %s = ? ", PlatesTable.COLUMN_PLATE_END);
            whereParams.add(plate.getDto().end());
        }

        int rowsUpdated = db.update(table.getName(), table.modelToContentValues(plate),
                                    whereClause, whereParams.toArray(new String[whereParams.size()]));

        if (rowsUpdated == 0) {
            add(plate);
        }
    }

    /**
     * Checks if given plates are already in database. If so, updates it, if no, adds new ones.
     *
     * @param plates List of {@link PlateModel} to add or update
     */
    public void updateOrAdd(List<PlateModel> plates) {
        for (PlateModel plate : plates) {
            updateOrAdd(plate);
        }
    }
}
