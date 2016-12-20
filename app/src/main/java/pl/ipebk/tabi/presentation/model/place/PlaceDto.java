/*
* author: Bartlomiej Kierys
* date: 2016-12-16
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.place;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.List;

import pl.ipebk.tabi.readmodel.PlaceType;

@AutoValue
public abstract class PlaceDto {
    public abstract String name();
    public abstract PlaceType placeType();
    @Nullable public abstract String voivodeship();
    @Nullable public abstract String powiat();
    @Nullable public abstract String gmina();
    public abstract List<LicensePlateDto> plates();

    public static PlaceDto create(String name, PlaceType placeType, String voivodeship,
                                  String powiat, String gmina, List<LicensePlateDto> plates) {
        return new AutoValue_PlaceDto(name, placeType, voivodeship, powiat, gmina, plates);
    }
}
