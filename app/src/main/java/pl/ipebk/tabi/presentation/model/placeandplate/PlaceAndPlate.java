/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.placeandplate;

import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.place.LicensePlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Model representing place with single plate. This is simplified object to display in single place row
 */
public class PlaceAndPlate {
    private PlaceAndPlateDto dto;
    private PlaceLocalizationHelper localizationHelper;

    PlaceAndPlate(PlaceAndPlateDto dto, PlaceLocalizationHelper localizationHelper) {
        this.dto = dto;
        this.localizationHelper = localizationHelper;
    }

    public AggregateId id(){
        return dto.id();
    }

    public String plateString() {
        LicensePlateDto plate = LicensePlateDto.create(dto.plateStart(), dto.plateEnd());
        return plate.toString();
    }

    public PlaceType placeType() {
        return dto.placeType();
    }

    public String name() {
        return dto.name();
    }

    public String voivodeship(){
        return localizationHelper.formatVoivodeship(dto.voivodeship());
    }

    public String powiat() {
        return localizationHelper.formatPowiat(dto.powiat());
    }
}
