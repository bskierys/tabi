/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import android.database.Cursor;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.tables.PlacesTable;

/**
 * List item that represents category on main screen. This means
 * voivodeship or special category
 */
public class CategoryListItem {
    private String name;
    private Character plateStart;
    private Place.Type type;

    public CategoryListItem(Cursor cursor) {
        setName(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_VOIVODESHIP)));

        int plateIndex = cursor.getColumnIndex(PlacesTable.COLUMN_PLATE);
        if (!cursor.isNull(plateIndex)) {
            String plate = cursor.getString(plateIndex);
            Character plateStart = plate.charAt(0);
            setPlateStart(plateStart);
        }

        int placeTypeIndex = cursor.getColumnIndex(PlacesTable.COLUMN_PLACE_TYPE);
        if (!cursor.isNull(placeTypeIndex)) {
            setType(Place.Type.values()[cursor.getInt(placeTypeIndex)]);
        } else {
            setType(Place.Type.UNSPECIFIED);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getPlateStart() {
        return plateStart;
    }

    public void setPlateStart(Character plateStart) {
        this.plateStart = plateStart;
    }

    public Place.Type getType() {
        return type;
    }

    public void setType(Place.Type type) {
        this.type = type;
    }
}
