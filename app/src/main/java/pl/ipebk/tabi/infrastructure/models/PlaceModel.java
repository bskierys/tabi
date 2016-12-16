/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.models;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.List;

import pl.ipebk.tabi.infrastructure.base.Model;
import pl.ipebk.tabi.readmodel.PlaceType;

@AutoValue
public abstract class PlaceModel implements Model {
    private long id;
    // TODO: 2016-12-06 implement full model and set correct fields in some kind of converter

    public abstract String name();
    public abstract PlaceType type();
    @Nullable public abstract String voivodeship();
    @Nullable public abstract String powiat();
    @Nullable public abstract String gmina();
    public abstract List<PlateModel> plates();
    public abstract boolean hasOwnPlate();

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public static PlaceModel create(long id, String name, PlaceType type, String voivodeship, String powiat, String gmina,
                                    List<PlateModel> plates, boolean hasOwnPlate) {
        PlaceModel place = new AutoValue_PlaceModel(name, type, voivodeship, powiat, gmina, plates, hasOwnPlate);
        place.setId(id);
        return place;
    }

    public static PlaceModel create(String name, PlaceType type, String voivodeship, String powiat, String gmina,
                                    List<PlateModel> plates, boolean hasOwnPlate) {
        return new AutoValue_PlaceModel(name, type, voivodeship, powiat, gmina, plates, hasOwnPlate);
    }
}
