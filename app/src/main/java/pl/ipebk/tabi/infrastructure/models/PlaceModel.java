/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.infrastructure.base.Model;
import pl.ipebk.tabi.presentation.model.place.LicensePlateDto;
import pl.ipebk.tabi.presentation.model.place.PlaceDto;
import pl.ipebk.tabi.readmodel.PlaceType;

@AutoValue
public abstract class PlaceModel implements Model {
    private long id;
    private PlaceDto dto;

    public abstract List<PlateModel> plates();
    public abstract boolean hasOwnPlate();

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public PlaceDto dto() {
        List<LicensePlateDto> licensePlates = new ArrayList<>();
        for (PlateModel plate : plates()) {
            licensePlates.add(plate.getDto());
        }
        return PlaceDto.create(dto.name(), dto.placeType(), dto.voivodeship(),
                               dto.powiat(), dto.gmina(), licensePlates);
    }

    public static PlaceModel create(long id, String name, PlaceType type, String voivodeship, String powiat, String
            gmina,
                                    List<PlateModel> plates, boolean hasOwnPlate) {
        PlaceModel place = PlaceModel.create(name, type, voivodeship, powiat, gmina, plates, hasOwnPlate);
        place.setId(id);
        return place;
    }

    public static PlaceModel create(String name, PlaceType type, String voivodeship, String powiat, String gmina,
                                    List<PlateModel> plates, boolean hasOwnPlate) {
        PlaceDto placeDto = PlaceDto.create(name, type, voivodeship, powiat, gmina, new ArrayList<>());
        PlaceModel place = new AutoValue_PlaceModel(plates, hasOwnPlate);
        place.dto = placeDto;
        return place;
    }
}
