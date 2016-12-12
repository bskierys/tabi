/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * TODO: Generic description. Replace with real one.
 */
@AutoValue
public abstract class PlaceAndPlateDto {
    public abstract AggregateId placeId();
    public abstract String placeName();
    public abstract String plateStart();
    @Nullable public abstract String plateEnd();
    @Nullable public abstract String voivodeship();
    @Nullable public abstract String powiat();
    public abstract PlaceType placeType();
}
