/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.MediumTest;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class PlateDaoTest extends DatabaseTest {
    private static final String PLATE1 = "TAB";
    private static final String PLATE2 = "BAT";
    private static final String PLACE1 = "Name";
    private static final String PLACE2 = "Eman";

    @NonNull private List<PlateModel> addTwoPlacesAndPlatesForThem() {
        PlaceModel place1 = (new PlaceModelAssembler()).withName(PLACE1).assemble();
        PlaceModel place2 = (new PlaceModelAssembler()).withName(PLACE2).assemble();

        databaseHelper.getPlaceDao().add(place1);
        databaseHelper.getPlaceDao().add(place2);

        return addTwoPlatesForTwoPlaces(place1.getId(), place2.getId());
    }

    @NonNull private List<PlateModel> addTwoPlatesForTwoPlaces(long place1Id, long place2Id) {
        PlateModel plate1 = PlateModel.create(PLATE1, null);
        plate1.setPlaceId(place1Id);
        PlateModel plate2 = PlateModel.create(PLATE2, null);
        plate2.setPlaceId(place2Id);
        List<PlateModel> platesToAdd = new ArrayList<>();
        platesToAdd.add(plate1);
        platesToAdd.add(plate2);
        return platesToAdd;
    }

    @MediumTest public void testGetPlatesByPlaceId() {
        List<PlateModel> platesToAdd = addTwoPlacesAndPlatesForThem();
        PlateModel plateToFind = platesToAdd.get(0);
        PlateModel plateNotToFind = platesToAdd.get(1);

        long placeToFindId = plateToFind.placeId();

        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateToFind);
        databaseHelper.getPlateDao().add(plateNotToFind);

        List<PlateModel> plates = databaseHelper.getPlateDao().getPlatesForPlaceId(placeToFindId);

        assertNotNull(plates);
        assertTrue(!plates.isEmpty());
        assertEquals(2, plates.size());
        assertEquals(PLATE1, plates.get(0).getDto().pattern());
    }

    @MediumTest public void testUpdateOrAddTest() {
        List<PlateModel> platesToAdd = addTwoPlacesAndPlatesForThem();
        databaseHelper.getPlateDao().updateOrAdd(platesToAdd);

        List<PlaceModel> places = databaseHelper.getPlaceDao().getAll();
        long place1Id = places.get(0).getId();
        long place2Id = places.get(1).getId();

        List<PlateModel> all = databaseHelper.getPlateDao().getAll();
        assertEquals(2, all.size());

        List<PlateModel> platesToUpdate = addTwoPlatesForTwoPlaces(place1Id, place2Id);
        databaseHelper.getPlateDao().updateOrAdd(platesToUpdate);

        all = databaseHelper.getPlateDao().getAll();
        assertEquals(2, all.size());
    }
}
