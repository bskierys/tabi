/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database;

import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.MediumTest;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;

public class PlateDaoTest extends DatabaseTest {
    private static final String PLATE1 = "TAB";
    private static final String PLATE2 = "BAT";
    private static final String PLACE1 = "Name";
    private static final String PLACE2 = "Eman";

    @NonNull private List<Plate> addTwoPlacesAndPlatesForThem() {
        Place place1 = new Place();
        place1.setName(PLACE1);
        Place place2 = new Place();
        place2.setName(PLACE2);

        databaseHelper.getPlaceDao().add(place1);
        databaseHelper.getPlaceDao().add(place2);

        return addTwoPlatesForTwoPlaces(place1.getId(), place2.getId());
    }

    @NonNull private List<Plate> addTwoPlatesForTwoPlaces(long place1Id, long place2Id) {
        Plate plate1 = new Plate();
        plate1.setPattern(PLATE1);
        plate1.setPlaceId(place1Id);
        Plate plate2 = new Plate();
        plate2.setPattern(PLATE2);
        plate2.setPlaceId(place2Id);
        List<Plate> platesToAdd = new ArrayList<>();
        platesToAdd.add(plate1);
        platesToAdd.add(plate2);
        return platesToAdd;
    }

    @MediumTest public void testGetPlatesByPlaceId() {
        List<Plate> platesToAdd = addTwoPlacesAndPlatesForThem();
        Plate plateToFind = platesToAdd.get(0);
        Plate plateNotToFind = platesToAdd.get(1);

        long placeToFindId = plateToFind.getPlaceId();

        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateNotToFind);

        List<Plate> plates = databaseHelper.getPlateDao().getPlatesForPlaceId(placeToFindId);

        assertNotNull(plates);
        assertTrue(!plates.isEmpty());
        assertEquals(2, plates.size());
        assertEquals(PLATE1, plates.get(0).getPattern());
    }

    @MediumTest public void testUpdateOrAddTest() {
        List<Plate> platesToAdd = addTwoPlacesAndPlatesForThem();
        databaseHelper.getPlateDao().updateOrAdd(platesToAdd);

        List<Place> places = databaseHelper.getPlaceDao().getAll();
        long place1Id = places.get(0).getId();
        long place2Id = places.get(1).getId();

        List<Plate> all = databaseHelper.getPlateDao().getAll();
        assertEquals(2, all.size());

        List<Plate> platesToUpdate = addTwoPlatesForTwoPlaces(place1Id, place2Id);
        databaseHelper.getPlateDao().updateOrAdd(platesToUpdate);

        all = databaseHelper.getPlateDao().getAll();
        assertEquals(2, all.size());
    }
}
