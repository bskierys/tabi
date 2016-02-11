/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;

import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;

public class DatabaseTests extends AndroidTestCase {
    private static final String PLATE1 = "TAB";
    private static final String PLATE2 = "BAT";
    private static final String PLACE1 = "Name";
    private static final String PLACE2 = "Eman";
    private static DatabaseTestOpenHelper databaseHelper;

    @Override public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        databaseHelper = new DatabaseTestOpenHelper(context);
        databaseHelper.init();
    }

    @Override public void tearDown() throws Exception {
        databaseHelper.purge();
    }

    @MediumTest public void testGetPlatesByPlaceId() {
        Place placeToFind = new Place();
        placeToFind.setName(PLACE1);
        Place placeNotToFind = new Place();
        placeNotToFind.setName(PLACE2);

        databaseHelper.getPlaceDao().add(placeToFind);
        databaseHelper.getPlaceDao().add(placeNotToFind);

        Plate plateToFind = new Plate();
        plateToFind.setPattern(PLATE1);
        plateToFind.setPlaceId(placeToFind.getId());
        Plate plateNotToFind = new Plate();
        plateNotToFind.setPattern(PLATE2);
        plateNotToFind.setPlaceId(placeNotToFind.getId());

        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateNotToFind);

        List<Plate> plates = databaseHelper.getPlateDao().getPlatesForPlaceId(placeToFind.getId());

        assertNotNull(plates);
        assertTrue(plates.size() > 0);
        assertEquals(2, plates.size());
        assertEquals(PLATE1, plates.get(0).getPattern());
    }
}
