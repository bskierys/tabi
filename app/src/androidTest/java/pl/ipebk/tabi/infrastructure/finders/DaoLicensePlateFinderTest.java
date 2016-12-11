package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class DaoLicensePlateFinderTest extends FinderTest {
    private DaoLicensePlateFinder finder;

    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoLicensePlateFinder(databaseHelper.getPlatesToSearchDao());
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByFirstPlate() throws Exception {
        addToDatabase(givenPlace().withPlate("TAB").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldFindPlate_whenSearchingByAdditionalPlate() throws Exception {
        addToDatabase(givenPlace().withPlate("BAT").and().withPlate("TAB").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasPlate("TAB");
    }

    @MediumTest public void test_shouldNotFindPlate_whenNoneMatches() throws Exception {
        addToDatabase(givenPlace().withPlate("BRA").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("T", null));
        thenFoundPlaces().areNone();
    }

    @MediumTest public void test_shouldTwoLatterPlateBeFirst_whenSimilarPattern() throws Exception {
        addToDatabase(givenPlace().withPlate("BAT").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BA").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("B").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("B"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BA"); //and
        then().searchedPlaceThatIs(THIRD).hasPlate("BAT");
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSimilarPattern() throws Exception {
        addToDatabase(givenPlace().withPlate("BAT").ofType(PlaceType.TOWN).withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY).withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").ofType(PlaceType.POWIAT_CITY).withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.POWIAT_CITY); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.TOWN);
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenSearched() throws Exception {
        addToDatabase(givenPlace().withPlate("BZ").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BP").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BA").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BA"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BP"); // and
        then().searchedPlaceThatIs(THIRD).hasPlate("BZ");
    }

    @MediumTest public void test_shouldBeSortedByCitySizeFirst_whenMayBeSortedByPlateLength() throws Exception {
        addToDatabase(givenPlace().withPlate("BA").ofType(PlaceType.VILLAGE).withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").ofType(PlaceType.VOIVODE_CITY).withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); //and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldBeSortedByPlateLengthFirst_whenMayBeSortedAlphabetically() throws Exception {
        addToDatabase(givenPlace().withPlate("BZ").withOwnPlate()); // and
        addToDatabase(givenPlace().withPlate("BAT").withOwnPlate());

        whenSearched(finder.findPlaceListForPlateStart("B", null));

        then().searchedPlaceThatIs(FIRST).hasPlate("BZ"); //and
        then().searchedPlaceThatIs(SECOND).hasPlate("BAT");
    }

    @MediumTest public void test_shouldNotFindPlace_whenPlaceHasNoOwnPlate() throws Exception {
        addToDatabase(givenPlace().withPlate("BZ"));
        whenSearched(finder.findPlaceListForPlateStart("B", null));
        thenFoundPlaces().areNone();
    }
}