/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.squareup.sqlbrite.BriteDatabase;

import pl.ipebk.tabi.infrastructure.base.Dao;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;

public class PlaceDao extends Dao<PlaceModel> {
    public PlaceDao(BriteDatabase database, PlateDao plateDao) {
        super(PlaceModel.class, database);
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
        ((PlacesTable) table).setPlaceDao(this);
    }

    public int getNextRowId() {
        String query = "SELECT MAX(" + BaseColumns._ID + ") FROM " + table.getName();
        Cursor cursor = db.query(query);

        return getSimpleInt(cursor) + 1;
    }
}
