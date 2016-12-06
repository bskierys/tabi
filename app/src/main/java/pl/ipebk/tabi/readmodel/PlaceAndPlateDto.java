/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import com.google.auto.value.AutoValue;

/**
 * TODO: Generic description. Replace with real one.
 */
@AutoValue
public abstract class PlaceAndPlateDto {
    // TODO: 2016-12-04 aggregateId
    public abstract long placeId();
    public abstract String placeName();
    public abstract String plateStart();
    public abstract String plateEnd();
    public abstract String voivodeship();
    public abstract String powiat();
    public abstract PlaceType placeType();

    public static PlaceAndPlateDto create(long placeId, String placeName, String plateStart,
                                          String plateEnd, String voivodeship, String powiat, PlaceType placeType) {
        return new AutoValue_PlaceAndPlateDto(placeId, placeName, plateStart, plateEnd, voivodeship, powiat, placeType);
    }
}
