package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.readmodel.PlaceType;
import static pl.ipebk.tabi.test.common.assertions.Order.*;

public class DaoPlaceFinderTest extends FinderTest {
    private DaoPlaceFinder finder;

    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoPlaceFinder(databaseHelper.getPlacesToSearchDao());
    }

    @MediumTest public void test_shouldFindPlace_whenHasPolishDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica")); // and
        addToDatabase(givenPlace().withName("swirzyce"));

        whenSearched(finder.findPlacesListByName("świd", null));

        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFindPlace_whenSearchedWithoutPolishDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica")); // and
        addToDatabase(givenPlace().withName("swirzyce"));

        whenSearched(finder.findPlacesListByName("swid", null));

        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldOrderAlphabetically_whenHasPolishDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica")); // and
        addToDatabase(givenPlace().withName("swirzyce"));

        whenSearched(finder.findPlacesListByName("swi", null));

        thenFoundPlaces().hasCount(2).and().searchedPlaceThatIs(FIRST).hasName("swirzyce"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldLimitResults_whenAsked() throws Exception {
        addToDatabase(givenPlace().withName("swidnica")); // and
        addToDatabase(givenPlace().withName("swidnico")); // and
        addToDatabase(givenPlace().withName("swidnice"));

        whenSearched(finder.findPlacesListByName("swi", 2));

        thenFoundPlaces().hasCount(2);
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSearched() throws Exception {
        addToDatabase(givenPlace().withName("swidnica").ofType(PlaceType.TOWN)); // and
        addToDatabase(givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE)); // and
        addToDatabase(givenPlace().withName("swidnica").ofType(PlaceType.VOIVODE_CITY));

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); // and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.TOWN); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldCityBeFirst_whenHasOwnPlate() throws Exception {
        addToDatabase(givenPlace().withName("swidnica")); // and
        addToDatabase(givenPlace().withName("swidnice").withOwnPlate());

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenUsingPolishDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("śwarądz")); // and
        addToDatabase(givenPlace().withName("świnoujście")); // and
        addToDatabase(givenPlace().withName("śokołów")); // and
        addToDatabase(givenPlace().withName("śókołów"));

        whenSearched(finder.findPlacesListByName("ś", null));

        then().searchedPlaceThatIs(FIRST).hasName("śokołów"); // and
        then().searchedPlaceThatIs(SECOND).hasName("śókołów"); // and
        then().searchedPlaceThatIs(THIRD).hasName("śwarądz"); // and
        then().searchedPlaceThatIs(FOURTH).hasName("świnoujście");
    }

    @MediumTest public void test_shouldFavorNoDiacritics_whenSearchedWithoutDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica")); // and
        addToDatabase(givenPlace().withName("swidnica"));

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorDiacritics_whenSearchedWithDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica")); // and
        addToDatabase(givenPlace().withName("swidnica"));

        whenSearched(finder.findPlacesListByName("świ", null));

        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorOwningPlate_whenCanFavorByDiacritics() throws Exception {
        addToDatabase(givenPlace().withName("świdnica").withOwnPlate()); // and
        addToDatabase(givenPlace().withName("swidnica"));

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("świdnica");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }

    // TODO: 2016-12-07 should it be that way?
    @MediumTest public void test_shouldFavorDiacritics_whenCanFavorBigCity() throws Exception {
        addToDatabase(givenPlace().withName("świdnica").ofType(PlaceType.TOWN)); // and
        addToDatabase(givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE));

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica");
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorBigCity_whenCanFavorSortingAlphabetically() throws Exception {
        addToDatabase(givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE)); // and
        addToDatabase(givenPlace().withName("swidnice").ofType(PlaceType.TOWN));

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }
}