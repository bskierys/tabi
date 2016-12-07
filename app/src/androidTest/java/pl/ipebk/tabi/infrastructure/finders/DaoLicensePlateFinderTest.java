package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.TestModelFactory;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoLicensePlateFinderTest extends FinderTest {
    private DaoLicensePlateFinder finder;
    private TestModelFactory as;
    private DatabaseTestModelFactory is;

    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoLicensePlateFinder(databaseHelper.getPlatesToSearchDao());
        as = new DatabaseTestModelFactory();
        is = (DatabaseTestModelFactory) as;
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByFirstPlate() throws Exception {
        as.givenPlace().withPlate("TAB");
        is.addedToDatabase();
        // and
        as.givenPlace().withPlate("BAT");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByAdditionalPlate() throws Exception {
        as.givenPlace().withPlate("BAT").and().withPlate("TAB");
        is.addedToDatabase();
        // and
        as.givenPlace().withPlate("BAT");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldNotFindPlate_whenNoneMatches() throws Exception {
        as.givenPlace().withPlate("BRA");
        is.addedToDatabase();
        // and
        as.givenPlace().withPlate("BAT");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(0);
    }

    @MediumTest public void test_shouldTwoLatterPlateBeFirst_whenSimilarPattern() throws Exception {
        as.givenPlace().withPlate("BAT");
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BA");
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("B");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("B"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BA"); //and
        then().searchedPlaceThatIs(THIRD).hasPlate("BAT");
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSimilarPattern() throws Exception {
        as.givenPlace().withPlate("BAT").ofType(PlaceType.TOWN);
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY);
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BAT").ofType(PlaceType.POWIAT_CITY);
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.POWIAT_CITY); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.TOWN);
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenSearched() throws Exception {
        as.givenPlace().withPlate("BZ");
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BP");
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BA");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BA"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BP"); // and
        then().searchedPlaceThatIs(THIRD).hasPlate("BZ");
    }

    @MediumTest public void test_shouldBeSortedByCitySizeFirst_whenMayBeSortedByPlateLength() throws Exception {
        as.givenPlace().withPlate("BA").ofType(PlaceType.VILLAGE);
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY);
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldBeSortedByPlateLengthFirst_whenMayBeSortedAlphabetically() throws Exception {
        as.givenPlace().withPlate("BZ");
        is.addedToDatabase(); // and
        as.givenPlace().withPlate("BAT");
        is.addedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BZ"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BAT");
    }
}