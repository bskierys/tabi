/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assertions;

import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;

import static org.junit.Assert.assertEquals;

/**
 * Assert helper for {@link PlaceAndPlateDto} object
 */
public class PlaceAndPlateDtoAssert {
    private PlaceAndPlateDto place;

    public PlaceAndPlateDtoAssert(PlaceAndPlateDto place) {
        this.place = place;
    }

    public PlaceAndPlateDtoAssert hasPlate(String plate) {
        assertEquals(plate, place.plateStart());
        return this;
    }

    public PlaceAndPlateDtoAssert hasName(String name) {
        assertEquals(name, place.placeName());
        return this;
    }

    public PlaceAndPlateDtoAssert isType(PlaceType type) {
        assertEquals(type, place.placeType());
        return this;
    }
}
