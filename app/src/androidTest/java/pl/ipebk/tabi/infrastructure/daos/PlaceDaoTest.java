/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.test.suitebuilder.annotation.MediumTest;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class PlaceDaoTest extends DatabaseTest {

    @Override public void setUp() throws Exception {
        super.setUp();
    }

    private PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    private void addToDatabase(PlaceModelAssembler assembler) {
        databaseHelper.getPlaceDao().add(assembler.assemble());
    }

    @MediumTest public void testGetNextRowId() throws Exception {
        int nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(1, nextRowId);

        addToDatabase(givenPlace().withName("a"));
        nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(2, nextRowId);
    }

    @MediumTest public void testGetNotSpecialPowiatPlacesCount() throws Exception {
        addToDatabase(givenPlace().ofType(PlaceType.TOWN).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY).withOwnPlate());

        addToDatabase(givenPlace().ofType(PlaceType.TOWN));
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE));
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY));
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY));

        addToDatabase(givenPlace().ofType(PlaceType.RANDOM).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.SPECIAL).withOwnPlate());

        int expected = 4;
        int actual = databaseHelper.getPlaceDao().getStandardPlacesWithPlateCount();

        assertEquals(expected, actual);
    }

    @MediumTest public void testGetCount() throws Exception {
        addToDatabase(givenPlace().ofType(PlaceType.TOWN).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.SPECIAL).withOwnPlate());

        int expected = 4;
        int actual = databaseHelper.getPlaceDao().getStandardPlacesWithPlateCount();

        assertEquals(expected, actual);
    }
}
