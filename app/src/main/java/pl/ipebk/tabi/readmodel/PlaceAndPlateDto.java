/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import pl.ipebk.tabi.infrastructure.tables.PlacesTable;

/**
 * TODO: Generic description. Replace with real one.
 */
@AutoValue
public abstract class PlaceAndPlateDto {
    // TODO: 2016-12-04 aggregateId
    public abstract long placeId();
    public abstract String placeName();
    public abstract String plateStart();
    @Nullable public abstract String plateEnd();
    public abstract String voivodeship();
    public abstract String powiat();
    public abstract PlaceType placeType();

    public static PlaceAndPlateDto create(long placeId, String placeName, String plateStart,
                                          String plateEnd, String voivodeship, String powiat, PlaceType placeType) {
        return new AutoValue_PlaceAndPlateDto(placeId, placeName, plateStart, plateEnd, voivodeship, powiat, placeType);
    }

    // TODO: 2016-12-10 readmodel should not depend on concrete implementation of database
    public static PlaceAndPlateDto create(Cursor cursor) {
        long placeId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        String placeName = cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_NAME));
        String voivodeship = cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_VOIVODESHIP));
        String powiat = cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_POWIAT));
        String plateStart = cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_SEARCHED_PLATE));
        String plateEnd = cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_SEARCHED_PLATE_END));

        PlaceType type = PlaceType.UNSPECIFIED;
        int placeTypeIndex = cursor.getColumnIndex(PlacesTable.COLUMN_PLACE_TYPE);
        if (!cursor.isNull(placeTypeIndex)) {
            type = PlaceType.values()[cursor.getInt(placeTypeIndex)];
        }

        return new AutoValue_PlaceAndPlateDto(placeId, placeName, plateStart, plateEnd, voivodeship, powiat, type);
    }
}
