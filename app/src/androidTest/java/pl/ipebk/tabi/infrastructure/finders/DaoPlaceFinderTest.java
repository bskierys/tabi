package pl.ipebk.tabi.infrastructure.finders;

import android.test.suitebuilder.annotation.MediumTest;

import java.text.CollationKey;
import java.text.Collator;
import java.util.List;

import pl.ipebk.tabi.database.DatabaseTest;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.test.common.TestModelFactory;

import static org.junit.Assert.*;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceFinderTest extends FinderTest {
    private DaoPlaceFinder finder;
    private TestModelFactory as;
    private DaoLicensePlateFinderTest.DatabaseTestModelFactory is;

    // TODO: 2016-12-07 write finder descriptions based on that tests
    @Override public void setUp() throws Exception {
        super.setUp();
        finder = new DaoPlaceFinder(databaseHelper.getPlacesToSearchDao());
        as = new DaoLicensePlateFinderTest.DatabaseTestModelFactory();
        is = (DaoLicensePlateFinderTest.DatabaseTestModelFactory) as;
    }

    @MediumTest public void test_shouldFindPlace_whenHasPolishDiacritics() throws Exception {
        as.givenPlace().withName("świdnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swirzyce");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("świd", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFindPlace_whenSearchedWithoutPolishDiacritics() throws Exception {
        as.givenPlace().withName("świdnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swirzyce");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swid", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldOrderAlphabetically_whenHasPolishDiacritics() throws Exception {
        as.givenPlace().withName("świdnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swirzyce");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        thenFoundPlaces().hasCount(2).and().searchedPlaceThatIs(FIRST).hasName("swirzyce"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldLimitResults_whenAsked() throws Exception {
        as.givenPlace().withName("swidnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnico");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnice");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", 2));
        thenFoundPlaces().hasCount(2);
    }

    @MediumTest public void test_shouldBiggerCityBeFirst_whenSearched() throws Exception {
        as.givenPlace().withName("swidnica").ofType(PlaceType.TOWN);
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica").ofType(PlaceType.VOIVODE_CITY);
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        then().searchedPlaceThatIs(FIRST).isType(PlaceType.VOIVODE_CITY); // and
        then().searchedPlaceThatIs(SECOND).isType(PlaceType.TOWN); // and
        then().searchedPlaceThatIs(THIRD).isType(PlaceType.VILLAGE);
    }

    @MediumTest public void test_shouldCityBeFirst_whenHasOwnPlate() throws Exception {
        as.givenPlace().withName("swidnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnice").withOwnPlate();
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));
        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
    }

    @MediumTest public void test_shouldBeSortedAlphabetically_whenUsingPolishDiacritics() throws Exception {
        as.givenPlace().withName("śwarądz");
        is.addedToDatabase(); // and
        as.givenPlace().withName("świnoujście");
        is.addedToDatabase(); // and
        as.givenPlace().withName("śokołów");
        is.addedToDatabase(); // and
        as.givenPlace().withName("śókołów");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("ś", null));

        then().searchedPlaceThatIs(FIRST).hasName("śokołów"); // and
        then().searchedPlaceThatIs(SECOND).hasName("śókołów"); // and
        then().searchedPlaceThatIs(THIRD).hasName("śwarądz"); // and
        then().searchedPlaceThatIs(FOURTH).hasName("świnoujście");
    }

    @MediumTest public void test_shouldFavorNoDiacritics_whenSearchedWithoutDiacritics() throws Exception {
        as.givenPlace().withName("świdnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica"); // and
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorDiacritics_whenSearchedWithDiacritics() throws Exception {
        as.givenPlace().withName("świdnica");
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("świ", null));
        thenFoundPlaces().hasCount(1).and().searchedPlaceThatIs(FIRST).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorOwningPlate_whenCanFavorByDiacritics() throws Exception {
        as.givenPlace().withName("świdnica").withOwnPlate();
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica");
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("świdnica");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }

    // TODO: 2016-12-07 should it be that way?
    @MediumTest public void test_shouldFavorDiacritics_whenCanFavorBigCity() throws Exception {
        as.givenPlace().withName("świdnica").ofType(PlaceType.TOWN);
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnica");
        then().searchedPlaceThatIs(SECOND).hasName("świdnica");
    }

    @MediumTest public void test_shouldFavorBigCity_whenCanFavorSortingAlphabetically() throws Exception {
        as.givenPlace().withName("swidnica").ofType(PlaceType.VILLAGE);
        is.addedToDatabase(); // and
        as.givenPlace().withName("swidnice").ofType(PlaceType.TOWN);
        is.addedToDatabase();

        whenSearched(finder.findPlacesListByName("swi", null));

        then().searchedPlaceThatIs(FIRST).hasName("swidnice");
        then().searchedPlaceThatIs(SECOND).hasName("swidnica");
    }
}