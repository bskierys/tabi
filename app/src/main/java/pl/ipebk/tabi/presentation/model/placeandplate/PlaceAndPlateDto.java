/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.placeandplate;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Simplified model of place with plate to display on search row
 */
@AutoValue
public abstract class PlaceAndPlateDto {
    public abstract AggregateId id();
    public abstract String name();
    public abstract String plateStart();
    @Nullable public abstract String plateEnd();
    @Nullable public abstract String voivodeship();
    @Nullable public abstract String powiat();
    public abstract PlaceType placeType();
}
