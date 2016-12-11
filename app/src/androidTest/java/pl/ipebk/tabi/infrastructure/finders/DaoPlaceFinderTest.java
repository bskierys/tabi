package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceFinderTest extends FinderTest {
    private DaoPlaceFinder finder;

    // TODO: 2016-12-07 write finder descriptions based on that tests
    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoPlaceFinder(databaseHelper.getPlacesToSearchDao());
    }

    @MediumTest public void test_shouldFindPlace_whenHasPolishDiacritics() throws Exception {
        givenPlace().withName("świdnica");
        addedToDatabase(); // and
        givenPlace().withName("swirzyce");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("świd", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFindPlace_whenSearchedWithoutPolishDiacritics() throws Exception {
        givenPlace().withName("świdnica");
        addedToDatabase(); // and
        givenPlace().withName("swirzyce");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swid", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldOrderAlphabetically_whenHasPolishDiacritics() throws Exception {
        givenPlace().withName("świdnica");
        addedToDatabase(); // and
        givenPlace().withName("swirzyce");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        thenFoundPlaces().hasCount(2).and().searchedPlaceThatIs(FIRST).hasName("swirzyce"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldLimitResults_whenAsked() throws Exception {
        givenPlace().withName("swidnica");
        addedToDatabase(); // and
        givenPlace().withName("swidnico");
        addedToDatabase(); // and
        givenPlace().withName("swidnice");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", 2));
        thenFoundPlaces().hasCount(2);
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSearched() throws Exception {
        givenPlace().withName("swidnica").ofType(PlaceType.TOWN);
        addedToDatabase(); // and
        givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        addedToDatabase(); // and
        givenPlace().withName("swidnica").ofType(PlaceType.VOIVODE_CITY);
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); // and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.TOWN); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldCityBeFirst_whenHasOwnPlate() throws Exception {
        givenPlace().withName("swidnica");
        addedToDatabase(); // and
        givenPlace().withName("swidnice").withOwnPlate();
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenUsingPolishDiacritics() throws Exception {
        givenPlace().withName("śwarądz");
        addedToDatabase(); // and
        givenPlace().withName("świnoujście");
        addedToDatabase(); // and
        givenPlace().withName("śokołów");
        addedToDatabase(); // and
        givenPlace().withName("śókołów");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("ś", null));

        then().searchedPlaceThatIs(FIRST).hasName("śokołów"); // and
        then().searchedPlaceThatIs(SECOND).hasName("śókołów"); // and
        then().searchedPlaceThatIs(THIRD).hasName("śwarądz"); // and
        then().searchedPlaceThatIs(FOURTH).hasName("świnoujście");
    }

    @MediumTest public void test_shouldFavorNoDiacritics_whenSearchedWithoutDiacritics() throws Exception {
        givenPlace().withName("świdnica");
        addedToDatabase(); // and
        givenPlace().withName("swidnica");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorDiacritics_whenSearchedWithDiacritics() throws Exception {
        givenPlace().withName("świdnica");
        addedToDatabase(); // and
        givenPlace().withName("swidnica");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("świ", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorOwningPlate_whenCanFavorByDiacritics() throws Exception {
        givenPlace().withName("świdnica").withOwnPlate();
        addedToDatabase(); // and
        givenPlace().withName("swidnica");
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("świdnica");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }

    // TODO: 2016-12-07 should it be that way?
    @MediumTest public void test_shouldFavorDiacritics_whenCanFavorBigCity() throws Exception {
        givenPlace().withName("świdnica").ofType(PlaceType.TOWN);
        addedToDatabase(); // and
        givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica");
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorBigCity_whenCanFavorSortingAlphabetically() throws Exception {
        givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        addedToDatabase(); // and
        givenPlace().withName("swidnice").ofType(PlaceType.TOWN);
        addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }
}