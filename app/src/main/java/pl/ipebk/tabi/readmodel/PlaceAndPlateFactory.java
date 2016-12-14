/*
* author: Bartlomiej Kierys
* date: 2016-12-12
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * TODO: Generic description. Replace with real one.
 */
public abstract class PlaceAndPlateFactory {
    public abstract PlaceAndPlateDto createFromCursor(Cursor cursor);

    public PlaceAndPlateDto create(long placeId, String placeName, String plateStart, String plateEnd,
                                   String voivodeship, String powiat, PlaceType placeType) {
        return new AutoValue_PlaceAndPlateDto(new AggregateId(placeId), placeName, plateStart,
                                              plateEnd, voivodeship, powiat, placeType);
    }
}
