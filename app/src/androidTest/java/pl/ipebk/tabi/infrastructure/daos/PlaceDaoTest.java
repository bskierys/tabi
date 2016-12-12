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
}
