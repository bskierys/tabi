/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import pl.ipebk.tabi.infrastructure.base.View;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Common View implementation for all views that are based on {@link PlaceAndPlateDto} model
 */
abstract class PlaceAndPlateDtoView extends View<PlaceAndPlateDto> {
    @Override public PlaceAndPlateDto cursorToModel(Cursor cursor) {
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

        return PlaceAndPlateDto.create(placeId, placeName, plateStart, plateEnd, voivodeship, powiat, type);
    }
}
