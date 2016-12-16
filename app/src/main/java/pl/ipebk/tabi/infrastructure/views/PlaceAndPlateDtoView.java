/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

import android.database.Cursor;

import pl.ipebk.tabi.infrastructure.base.View;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;

/**
 * Common View implementation for all views that are based on {@link PlaceAndPlateDto} model
 */
abstract class PlaceAndPlateDtoView extends View<PlaceAndPlateDto> {
    @Override public PlaceAndPlateDto cursorToModel(Cursor cursor) {
        return (new DatabaseViewPlaceAndPlateFactory()).createFromCursor(cursor);
    }
}
