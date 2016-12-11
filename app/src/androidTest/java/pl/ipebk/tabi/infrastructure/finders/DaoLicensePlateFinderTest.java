package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoLicensePlateFinderTest extends FinderTest {
    private DaoLicensePlateFinder finder;

    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoLicensePlateFinder(databaseHelper.getPlatesToSearchDao());
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByFirstPlate() throws Exception {
        givenPlace().withPlate("TAB").withOwnPlate();
        isAddedToDatabase();
        // and
        givenPlace().withPlate("BAT").withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByAdditionalPlate() throws Exception {
        givenPlace().withPlate("BAT").and().withPlate("TAB").withOwnPlate();
        isAddedToDatabase();
        // and
        givenPlace().withPlate("BAT").withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldNotFindPlate_whenNoneMatches() throws Exception {
        givenPlace().withPlate("BRA").withOwnPlate();
        isAddedToDatabase();
        // and
        givenPlace().withPlate("BAT").withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(0);
    }

    @MediumTest public void test_shouldTwoLatterPlateBeFirst_whenSimilarPattern() throws Exception {
        givenPlace().withPlate("BAT").withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BA").withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("B").withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("B"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BA"); //and
        then().searchedPlaceThatIs(THIRD).hasPlate("BAT");
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSimilarPattern() throws Exception {
        givenPlace().withPlate("BAT").ofType(PlaceType.TOWN).withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY).withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BAT").ofType(PlaceType.POWIAT_CITY).withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.POWIAT_CITY); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.TOWN);
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenSearched() throws Exception {
        givenPlace().withPlate("BZ").withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BP").withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BA").withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BA"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BP"); // and
        then().searchedPlaceThatIs(THIRD).hasPlate("BZ");
    }

    @MediumTest public void test_shouldBeSortedByCitySizeFirst_whenMayBeSortedByPlateLength() throws Exception {
        givenPlace().withPlate("BA").ofType(PlaceType.VILLAGE).withOwnPlate();
        isAddedToDatabase(); // and
        givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY).withOwnPlate();
        isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldBeSortedByPlateLengthFirst_whenMayBeSortedAlphabetically() throws Exception {
        givenPlace().withPlate("BZ").withOwnPlate(); isAddedToDatabase(); // and
        givenPlace().withPlate("BAT").withOwnPlate(); isAddedToDatabase();

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BZ"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BAT");
    }

    @MediumTest public void test_shouldNotFindPlace_whenPlaceHasNoOwnPlate() throws Exception {
        givenPlace().withPlate("BZ"); isAddedToDatabase();
        whenSearched(finder.findPlaceListForPlateStart("B", null));
        thenFoundPlaces().areNone();
    }
}