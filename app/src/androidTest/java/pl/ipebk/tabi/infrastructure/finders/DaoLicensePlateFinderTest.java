package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.List;

import pl.ipebk.tabi.database.DatabaseTest;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.TestModelFactory;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoLicensePlateFinderTest extends DatabaseTest {
    private DaoLicensePlateFinder finder;
    private TestModelFactory as;
    private DatabaseTestModelFactory is;

    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoLicensePlateFinder(databaseHelper.getPlatesToSearchDao());
        as = new DatabaseTestModelFactory();
        is = (DatabaseTestModelFactory) as;
    }

    private static class DatabaseTestModelFactory extends TestModelFactory {
        private void addedToDatabase() {
            this.placeModel = placeModelAssembler.assemble();
            databaseHelper.getPlaceDao().add(this.placeModel);
        }
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByFirstPlate() throws Exception {
        as.givenPlace().withPlate("TAB").ofType(PlaceType.POWIAT_CITY);
        is.addedToDatabase();
        // and
        as.givenPlace().withPlate("BAT").ofType(PlaceType.POWIAT_CITY);
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).andFirstPlace().hasPlate("TAB");
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByAdditionalPlate() throws Exception {
        as.givenPlace().withPlate("BAT").withPlate("TAB").ofType(PlaceType.POWIAT_CITY);
        is.addedToDatabase();
        // and
        as.givenPlace().withPlate("BAT").ofType(PlaceType.POWIAT_CITY);
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).andFirstPlace().hasPlate("TAB");
    }

    private List<PlaceAndPlateDto> foundPlaces;

    public void whenSearched(List<PlaceAndPlateDto> places) {
        this.foundPlaces = places;
    }

    public PlaceDtoCollectionAssert thenFoundPlaces() {
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

        public PlaceAndPlateDtoAssert andFirstPlace() {
            assertTrue(places.size() >= 1);
            PlaceAndPlateDto place = places.get(0);
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
    }

    /*@MediumTest public void testGetByPlateIsSortedProperly() throws Exception {
        PlaceModel twoLetter1 = TestDataFactory.createStandardPlace("1", "AA", PlaceType.POWIAT_CITY);
        PlaceModel twoLetter2 = TestDataFactory.createStandardPlace("2", "AZ", PlaceType.POWIAT_CITY);
        PlaceModel threeLetter1 = TestDataFactory.createStandardPlace("3", "AAA", PlaceType.POWIAT_CITY);
        PlaceModel threeLetter2 = TestDataFactory.createStandardPlace("4", "AWW", PlaceType.POWIAT_CITY);
        PlaceModel threeLetter3 = TestDataFactory.createStandardPlace("5", "AZZ", PlaceType.POWIAT_CITY);

        databaseHelper.getPlaceDao().add(twoLetter1);
        databaseHelper.getPlaceDao().add(twoLetter2);
        databaseHelper.getPlaceDao().add(threeLetter1);
        databaseHelper.getPlaceDao().add(threeLetter2);
        databaseHelper.getPlaceDao().add(threeLetter3);

        int limit = 4;

        List<PlaceModel> places = databaseHelper.getPlaceDao().getPlaceListForPlateStart("A", limit);

        // check if limit is correct
        assertEquals(limit, places.size());

        Collator collator = Collator.getInstance();
        for (int j = 0; j <= 1; j++) {
            String lastOne = places.get(j * 2).getPlates().get(0).getPattern();
            PlateModel plate = places.get(j * 2 + 1).getPlates().get(0);
            String platePattern = plate.getPattern();

            // check for pattern length
            assertEquals(j + 2, platePattern.length());
            assertEquals(j + 2, lastOne.length());

            // check alphabetical order
            CollationKey key = collator.getCollationKey(platePattern);
            int compare = key.compareTo(collator.getCollationKey(lastOne));
            assertTrue(compare > 0);
        }
    }*/
}