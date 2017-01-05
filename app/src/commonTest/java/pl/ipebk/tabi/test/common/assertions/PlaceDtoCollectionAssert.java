/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assertions;

import java.util.List;

import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static pl.ipebk.tabi.test.common.assertions.Order.LAST;

/**
 * Assert helper for list of {@link PlaceAndPlateDto} objects
 */
public class PlaceDtoCollectionAssert {
    private List<PlaceAndPlateDto> places;

    public PlaceDtoCollectionAssert(List<PlaceAndPlateDto> place) {
        this.places = place;
    }

    public PlaceDtoCollectionAssert hasCount(int count) {
        assertEquals(count, places.size());
        return this;
    }

    public PlaceDtoCollectionAssert areNone() {
        return this.hasCount(0);
    }

    public PlaceDtoCollectionAssert and() {
        return this;
    }

    public PlaceAndPlateDtoAssert searchedPlaceThatIs(int placeNumber) {
        PlaceAndPlateDto place;
        if (placeNumber == LAST) {
            place = places.get(places.size() - 1);
        } else {
            assertTrue(places.size() > placeNumber);
            place = places.get(placeNumber);
        }
        assertNotNull(place);

        return new PlaceAndPlateDtoAssert(place);
    }
}
