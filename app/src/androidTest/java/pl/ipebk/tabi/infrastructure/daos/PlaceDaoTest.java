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

    private PlaceModelAssembler2 placeModelAssembler;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
    }

    // TODO: 2016-12-10 better naming/usage for that class
    // TODO: 2016-12-10 add place to database in assemble method
    public PlaceModelAssembler2 givenPlace() {
        placeModelAssembler = new PlaceModelAssembler2();
        return placeModelAssembler;
    }

    @MediumTest public void testGetNextRowId() throws Exception {
        int nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(1, nextRowId);

        givenPlace().assemble();
        nextRowId = databaseHelper.getPlaceDao().getNextRowId();

        assertEquals(2, nextRowId);
    }

    // TODO: 2016-12-10 test second method with count
    @MediumTest public void testGetNotSpecialPowiatPlacesCount() throws Exception {
        givenPlace().ofType(PlaceType.TOWN).withOwnPlate().assemble();
        givenPlace().ofType(PlaceType.VILLAGE).withOwnPlate().assemble();
        givenPlace().ofType(PlaceType.POWIAT_CITY).withOwnPlate().assemble();
        givenPlace().ofType(PlaceType.VOIVODE_CITY).withOwnPlate().assemble();

        givenPlace().ofType(PlaceType.TOWN).assemble();
        givenPlace().ofType(PlaceType.VILLAGE).assemble();
        givenPlace().ofType(PlaceType.POWIAT_CITY).assemble();
        givenPlace().ofType(PlaceType.VOIVODE_CITY).assemble();

        givenPlace().ofType(PlaceType.RANDOM).withOwnPlate().assemble();
        givenPlace().ofType(PlaceType.SPECIAL).withOwnPlate().assemble();

        int expected = 4;
        int actual = databaseHelper.getPlaceDao().getStandardPlacesWithPlateCount();

        assertEquals(expected, actual);
    }

    // TODO: 2016-12-10 better naming
    class PlaceModelAssembler2 extends PlaceModelAssembler {
        @Override public PlaceModel assemble() {
            PlaceModel model = super.assemble();
            databaseHelper.getPlaceDao().add(model);
            return model;
        }
    }
}
