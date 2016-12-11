/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

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
        return PlaceAndPlateDto.create(cursor);
    }
}
