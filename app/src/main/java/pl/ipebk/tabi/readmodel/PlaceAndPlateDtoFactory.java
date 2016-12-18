/*
* author: Bartlomiej Kierys
* date: 2016-12-12
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * Factory class to produce dto objects. It was created to separate database logic from model
 */
public abstract class PlaceAndPlateDtoFactory {
    public abstract PlaceAndPlateDto createFromCursor(Cursor cursor);

    public PlaceAndPlateDto create(long placeId, String placeName, String plateStart, String plateEnd,
                                   String voivodeship, String powiat, PlaceType placeType) {
        return new AutoValue_PlaceAndPlateDto(new AggregateId(placeId), placeName, plateStart,
                                              plateEnd, voivodeship, powiat, placeType);
    }
}
