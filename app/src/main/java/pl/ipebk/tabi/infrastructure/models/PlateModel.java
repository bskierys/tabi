/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.models;

import pl.ipebk.tabi.infrastructure.base.Model;
import pl.ipebk.tabi.presentation.model.place.LicensePlateDto;

public class PlateModel implements Model {
    private long id;
    private long placeId;
    private LicensePlateDto dto;

    private PlateModel() {}

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public long getPlaceId() {
        return placeId;
    }

    public LicensePlateDto getDto() {
        return dto;
    }

    public void setDto(LicensePlateDto dto) {
        this.dto = dto;
    }

    public long placeId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public static PlateModel create(String pattern, String end) {
        PlateModel model = new PlateModel();
        model.setDto(LicensePlateDto.create(pattern, end));
        return model;
    }

    public static PlateModel create(long id, long placeId, String pattern, String end) {
        PlateModel plate = PlateModel.create(pattern, end);
        plate.setId(id);
        plate.setPlaceId(placeId);
        return plate;
    }
}
