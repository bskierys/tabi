/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.place;

import javax.inject.Inject;

import pl.ipebk.tabi.readmodel.PlaceDto;
import pl.ipebk.tabi.utils.NameFormatHelper;

/**
 * This class exists to inject {@link NameFormatHelper} to {@link Place} object.
 */
public class PlaceFactory {
    private NameFormatHelper formatHelper;

    @Inject public PlaceFactory(NameFormatHelper formatHelper) {
        this.formatHelper = formatHelper;
    }

    public Place createFromDto(PlaceDto dto) {
        return new Place(dto, formatHelper);
    }
}
