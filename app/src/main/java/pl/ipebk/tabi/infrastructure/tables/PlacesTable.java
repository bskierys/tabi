/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.List;

import pl.ipebk.tabi.infrastructure.base.Table;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.daos.PlateDao;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.utils.SpellCorrector;
import timber.log.Timber;

public class PlacesTable extends Table<PlaceModel> {
    public static final String COLUMN_NAME = "place_name";
    public static final String COLUMN_NAME_LOWER = "place_name_to_lower";
    public static final String COLUMN_SEARCH_PHRASE = "place_name_to_lower_no_diacritics";
    public static final String COLUMN_PLACE_TYPE = "place_type";
    public static final String COLUMN_VOIVODESHIP = "voivodeship";
    public static final String COLUMN_POWIAT = "powiat";
    public static final String COLUMN_GMINA = "gmina";
    public static final String COLUMN_PLATE = "plate";
    public static final String COLUMN_PLATE_END = "plate_end";
    public static final String COLUMN_HAS_OWN_PLATE = "has_own_plate";
    public static final String COLUMN_SEARCHED_PLATE = "searched_plate";
    public static final String COLUMN_SEARCHED_PLATE_END = "searched_plate_end";

    public static final String TABLE_NAME = "places";

    private static final String[] TABLE_COLUMNS = {
            BaseColumns._ID,
            COLUMN_NAME,
            COLUMN_NAME_LOWER,
            COLUMN_SEARCH_PHRASE,
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
            + TABLE_COLUMNS[2] + " TEXT, "
            + TABLE_COLUMNS[3] + " TEXT, "
            + TABLE_COLUMNS[4] + " INTEGER DEFAULT " + Integer.toString(PlaceType.UNSPECIFIED.ordinal()) + ", "
            + TABLE_COLUMNS[5] + " TEXT, "
            + TABLE_COLUMNS[6] + " TEXT, "
            + TABLE_COLUMNS[7] + " TEXT, "
            + TABLE_COLUMNS[8] + " TEXT, "
            + TABLE_COLUMNS[9] + " TEXT, "
            + TABLE_COLUMNS[10] + " INTEGER DEFAULT 0"
            + ");";

    private PlateDao plateDao;
    private PlaceDao placeDao;
    private SpellCorrector corrector;

    public PlacesTable() {
        corrector = new SpellCorrector();
    }

    public void setPlateDao(PlateDao plateDao) {
        this.plateDao = plateDao;
    }

    public void setPlaceDao(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    @Override public String getName() {
        return TABLE_NAME;
    }

    @Override protected String[] getColumns() {
        return TABLE_COLUMNS;
    }

    @Override protected String getDatabaseCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override public PlaceModel cursorToModel(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        PlaceType type = PlaceType.UNSPECIFIED;
        int placeTypeIndex = cursor.getColumnIndex(COLUMN_PLACE_TYPE);
        if (!cursor.isNull(placeTypeIndex)) {
            type = PlaceType.values()[cursor.getInt(placeTypeIndex)];
        }

        String voivodeship = cursor.getString(cursor.getColumnIndex(COLUMN_VOIVODESHIP));
        String powiat = cursor.getString(cursor.getColumnIndex(COLUMN_POWIAT));
        String gmina = cursor.getString(cursor.getColumnIndex(COLUMN_GMINA));

        List<PlateModel> plates = null;
        if (plateDao != null) {
            plates = plateDao.getPlatesForPlaceId(id);
            String mainPlatePattern = cursor.getString(cursor.getColumnIndex(COLUMN_PLATE));
            String mainPlateEnd = cursor.getString(cursor.getColumnIndex(COLUMN_PLATE_END));

            plates.add(0, PlateModel.create(mainPlatePattern, mainPlateEnd));
        } else {
            Timber.e("Plate dao is not set");
        }

        boolean hasOwnPlate = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_OWN_PLATE)) == 1;

        return PlaceModel.create(id, name, type, voivodeship, powiat, gmina, plates, hasOwnPlate);
    }

    @Override public ContentValues modelToContentValues(PlaceModel model) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, model.dto().name());
        values.put(COLUMN_NAME_LOWER, model.dto().name().toLowerCase());
        values.put(COLUMN_SEARCH_PHRASE, corrector.constructSearchPhrase(model.dto().name()));

        if (model.dto().placeType() != null) {
            values.put(COLUMN_PLACE_TYPE, model.dto().placeType().ordinal());
        } else {
            values.putNull(COLUMN_PLACE_TYPE);
        }

        values.put(COLUMN_VOIVODESHIP, model.dto().voivodeship());
        values.put(COLUMN_POWIAT, model.dto().powiat());
        values.put(COLUMN_GMINA, model.dto().gmina());

        if (model.plates() != null && model.plates().size() > 0) {
            int nextRowId = placeDao.getNextRowId();
            model.setId(nextRowId);

            // remove main plate so it is not treated as additional plate
            PlateModel mainPlate = model.plates().get(0);
            model.plates().remove(0);

            for (PlateModel plate : model.plates()) {
                plate.setPlaceId((long) nextRowId);
            }

            plateDao.updateOrAdd(model.plates());

            // add main plate afterwards to not harm original model
            model.plates().add(0,mainPlate);
            values.put(COLUMN_PLATE, mainPlate.getDto().pattern());
            values.put(COLUMN_PLATE_END, mainPlate.getDto().end());
        }

        values.put(COLUMN_HAS_OWN_PLATE, model.hasOwnPlate());

        return values;
    }
}
