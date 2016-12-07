/*
* author: Bartlomiej Kierys
* date: 2016-12-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import java.util.List;

import pl.ipebk.tabi.database.DatabaseTest;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.TestModelFactory;

/**
 * TODO: Generic description. Replace with real one.
 */
public class FinderTest extends DatabaseTest {
    private List<PlaceAndPlateDto> foundPlaces;
    protected static final int FIRST = 0;
    protected static final int SECOND = 1;
    protected static final int THIRD = 2;
    protected static final int FOURTH = 3;

    public void whenSearched(List<PlaceAndPlateDto> places) {
        this.foundPlaces = places;
    }

    public PlaceDtoCollectionAssert thenFoundPlaces() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }

    public PlaceDtoCollectionAssert then() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }

    public static class PlaceDtoCollectionAssert {
        private List<PlaceAndPlateDto> places;

        public PlaceDtoCollectionAssert(List<PlaceAndPlateDto> place) {
            this.places = place;
        }

        public PlaceDtoCollectionAssert hasCount(int count) {
            assertEquals(count, places.size());
            return this;
        }

        public PlaceDtoCollectionAssert and() {
            return this;
        }

        public PlaceAndPlateDtoAssert searchedPlaceThatIs(int placeNumber) {
            assertTrue(places.size() > placeNumber);
            PlaceAndPlateDto place = places.get(placeNumber);
            assertNotNull(place);

            return new PlaceAndPlateDtoAssert(place);
        }
    }

    public static class PlaceAndPlateDtoAssert {
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

    // TODO: 2016-12-07 what access to this classes? Should be here or separate classes?
    static class DatabaseTestModelFactory extends TestModelFactory {
        void addedToDatabase() {
            this.placeModel = placeModelAssembler.assemble();
            databaseHelper.getPlaceDao().add(this.placeModel);
        }
    }
}
