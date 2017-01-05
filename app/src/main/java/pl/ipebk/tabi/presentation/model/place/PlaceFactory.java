/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.place;

import javax.inject.Inject;

import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;

/**
 * This class exists to inject {@link PlaceLocalizationHelper} to {@link Place} object.
 */
public class PlaceFactory {
    private PlaceLocalizationHelper localizationHelper;

    @Inject public PlaceFactory(PlaceLocalizationHelper localizationHelper) {
        this.localizationHelper = localizationHelper;
    }

    public Place createFromDto(PlaceDto dto) {
        return new Place(dto, localizationHelper);
    }
}
