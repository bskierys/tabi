/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.placeandplate;

import android.database.Cursor;

import javax.inject.Inject;

import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;

/**
 * This class exists to inject {@link PlaceLocalizationHelper} to {@link PlaceAndPlate} object.
 */
public class PlaceAndPlateFactory {
    private PlaceAndPlateDtoFactory dtoFactory;
    private PlaceLocalizationHelper localizationHelper;

    @Inject public PlaceAndPlateFactory(PlaceAndPlateDtoFactory dtoFactory, PlaceLocalizationHelper
            localizationHelper) {
        this.dtoFactory = dtoFactory;
        this.localizationHelper = localizationHelper;
    }

    public PlaceAndPlate createFromCursor(Cursor cursor) {
        return new PlaceAndPlate(dtoFactory.createFromCursor(cursor), localizationHelper);
    }

    public PlaceAndPlate createFromDto(PlaceAndPlateDto dto) {
        return new PlaceAndPlate(dto, localizationHelper);
    }
}
