/*
* author: Bartlomiej Kierys
* date: 2016-12-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import java.util.List;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class FinderTest extends DatabaseTest {
    private List<PlaceAndPlateDto> foundPlaces;
    static final int FIRST = 0;
    static final int SECOND = 1;
    static final int THIRD = 2;
    static final int FOURTH = 3;
    static final int LAST = -1;

    PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    void addToDatabase(PlaceModelAssembler assembler) {
        databaseHelper.getPlaceDao().add(assembler.assemble());
    }

    void whenSearched(List<PlaceAndPlateDto> places) {
        this.foundPlaces = places;
    }

    PlaceDtoCollectionAssert thenFoundPlaces() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }

    PlaceDtoCollectionAssert then() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }

    static class PlaceDtoCollectionAssert {
        private List<PlaceAndPlateDto> places;

        PlaceDtoCollectionAssert(List<PlaceAndPlateDto> place) {
            this.places = place;
        }

        PlaceDtoCollectionAssert hasCount(int count) {
            assertEquals(count, places.size());
            return this;
        }

        PlaceDtoCollectionAssert areNone() {
            return this.hasCount(0);
        }

        PlaceDtoCollectionAssert and() {
            return this;
        }

        PlaceAndPlateDtoAssert searchedPlaceThatIs(int placeNumber) {
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

    static class PlaceAndPlateDtoAssert {
        private PlaceAndPlateDto place;

        PlaceAndPlateDtoAssert(PlaceAndPlateDto place) {
            this.place = place;
        }

        PlaceAndPlateDtoAssert hasPlate(String plate) {
            assertEquals(plate, place.plateStart());
            return this;
        }

        PlaceAndPlateDtoAssert hasName(String name) {
            assertEquals(name, place.placeName());
            return this;
        }

        PlaceAndPlateDtoAssert isType(PlaceType type) {
            assertEquals(type, place.placeType());
            return this;
        }
    }
}
