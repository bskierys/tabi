/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.placeandplate;

import android.database.Cursor;

import javax.inject.Inject;

import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.utils.NameFormatHelper;

/**
 * TODO: Generic description. Replace with real one.
 */
public class PlaceAndPlateFactory {
    private PlaceAndPlateDtoFactory dtoFactory;
    private NameFormatHelper formatHelper;

    @Inject public PlaceAndPlateFactory(PlaceAndPlateDtoFactory dtoFactory, NameFormatHelper formatHelper) {
        this.dtoFactory = dtoFactory;
        this.formatHelper = formatHelper;
    }

    public PlaceAndPlate createFromCursor(Cursor cursor) {
        return new PlaceAndPlate(dtoFactory.createFromCursor(cursor), formatHelper);
    }

    public PlaceAndPlate createFromDto(PlaceAndPlateDto dto) {
        return new PlaceAndPlate(dto, formatHelper);
    }
}
