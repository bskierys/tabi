/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.models;

import com.google.auto.value.AutoValue;

import pl.ipebk.tabi.infrastructure.base.Model;

@AutoValue
public abstract class PlateModel implements Model {
    private long id;
    private long placeId;
    public abstract String pattern();
    public abstract String end();

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public long placeId(){
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public static PlateModel create(String pattern, String end) {
        return new AutoValue_PlateModel(pattern, end);
    }

    public static PlateModel create(long id, long placeId, String pattern, String end) {
        PlateModel plate = new AutoValue_PlateModel(pattern, end);
        plate.setId(id);
        plate.setPlaceId(placeId);
        return plate;
    }
}
