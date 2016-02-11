/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.sqlite.SQLiteDatabase;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.tables.PlacesTable;

public class PlaceDao extends Dao<Place> {
    private PlateDao plateDao;

    public PlaceDao(SQLiteDatabase database, PlateDao plateDao) {
        super(Place.class, database);
        this.plateDao = plateDao;
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
    }


}
