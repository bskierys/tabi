/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;

public class PlacesTable extends Table<Place> {
    public static final String COLUMN_NAME = "place_name";
    public static final String COLUMN_PLACE_TYPE = "place_type";
    public static final String COLUMN_VOIVODESHIP = "voivodeship";
    public static final String COLUMN_POWIAT = "powiat";
    public static final String COLUMN_GMINA = "gmina";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_PLATE_END = "plate_end";
    public static final String COLUMN_HAS_OWN_PLATE = "has_own_plate";

    public static final String TABLE_NAME = "places";

    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PLACE_TYPE,
            COLUMN_VOIVODESHIP,
            COLUMN_POWIAT,
            COLUMN_GMINA,
            COLUMN_PLATE,
            COLUMN_PLATE_END,
            COLUMN_HAS_OWN_PLATE
    };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + TABLE_COLUMNS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TABLE_COLUMNS[1] + " TEXT NOT NULL, "
            + TABLE_COLUMNS[2] + " INTEGER DEFAULT " + Integer.toString(Place.Type.UNSPECIFIED.ordinal()) + ", "
            + TABLE_COLUMNS[3] + " TEXT, "
            + TABLE_COLUMNS[4] + " TEXT, "
            + TABLE_COLUMNS[5] + " TEXT, "
            + TABLE_COLUMNS[6] + " TEXT, "
            + TABLE_COLUMNS[7] + " TEXT, "
            + TABLE_COLUMNS[8] + " INTEGER DEFAULT 0"
            + ");";

    private PlateDao plateDao;

    public void setPlateDao(PlateDao plateDao) {
        this.plateDao = plateDao;
    }

    @Override public String getTableName() {
        return TABLE_NAME;
    }

    @Override protected String[] getTableColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public Place cursorToModel(Cursor cursor) {
        Place place = new Place();
        place.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        place.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        int placeTypeIndex = cursor.getColumnIndex(COLUMN_PLACE_TYPE);
        if (!cursor.isNull(placeTypeIndex))
            place.setType(Place.Type.values()[cursor.getInt(placeTypeIndex)]);
        else place.setType(Place.Type.UNSPECIFIED);
        place.setVoivodeship(cursor.getString(cursor.getColumnIndex(COLUMN_VOIVODESHIP)));
        place.setPowiat(cursor.getString(cursor.getColumnIndex(COLUMN_POWIAT)));
        place.setGmina(cursor.getString(cursor.getColumnIndex(COLUMN_GMINA)));
        if (plateDao != null) {
            List<Plate> plates = plateDao.getPlatesForPlaceId(place.getId());
            Plate mainPlate = new Plate();
            mainPlate.setPattern(cursor.getString(cursor.getColumnIndex(COLUMN_PLATE)));
            mainPlate.setEnd(cursor.getString(cursor.getColumnIndex(COLUMN_PLATE_END)));
            plates.add(0, mainPlate);
            place.setPlates(plates);
        } else Log.e(TAG, "Plate dao is not set");
        place.setHasOwnPlate(cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_OWN_PLATE)) == 0);
        return place;
    }

    @Override public ContentValues modelToContentValues(Place model) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, model.getName());
        if (model.getType() != null) {
            values.put(COLUMN_PLACE_TYPE, model.getType().ordinal());
        } else {
            values.putNull(COLUMN_PLACE_TYPE);
        }
        values.put(COLUMN_VOIVODESHIP, model.getVoivodeship());
        values.put(COLUMN_POWIAT, model.getPowiat());
        values.put(COLUMN_GMINA, model.getGmina());
        if (model.getPlates() != null && model.getPlates().size() > 0) {
            Plate mainPlate = model.getPlates().get(0);
            model.getPlates().remove(0);
            plateDao.updateOrAdd(model.getPlates());

            values.put(COLUMN_PLATE, mainPlate.getPattern());
            values.put(COLUMN_PLATE_END, mainPlate.getEnd());
        }
        values.put(COLUMN_HAS_OWN_PLATE, model.isHasOwnPlate());
        return values;
    }
}
