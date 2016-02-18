/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.openHelper.DatabaseTestOpenHelper;

public class DatabaseTest extends AndroidTestCase {
    protected static DatabaseTestOpenHelper databaseHelper;

    @Override public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        databaseHelper = new DatabaseTestOpenHelper(context);
        databaseHelper.init();
    }

    @Override public void tearDown() throws Exception {
        databaseHelper.purge();
    }

    protected Place constructPlace(String name, String plateStart, Place.Type categoryType) {
        Place place = new Place();
        place.setVoivodeship(name);
        place.setName(name);
        place.setType(categoryType);
        place.setHasOwnPlate(true);
        List<Plate> plates = new ArrayList<>();
        Plate plate = new Plate();

        if (plateStart == null) {
            plate.setPattern(name + name + name);
        } else {
            plate.setPattern(plateStart);
        }

        plates.add(plate);
        place.setPlates(plates);
        return place;
    }
}
