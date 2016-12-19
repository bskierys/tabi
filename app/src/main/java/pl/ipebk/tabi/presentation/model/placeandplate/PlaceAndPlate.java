/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.placeandplate;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.readmodel.LicensePlateDto;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * TODO: Generic description. Replace with real one.
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
