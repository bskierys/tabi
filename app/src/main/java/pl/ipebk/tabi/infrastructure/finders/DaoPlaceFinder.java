/*
* author: Bartlomiej Kierys
* date: 2016-12-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import java.util.List;

import pl.ipebk.tabi.infrastructure.daos.PlacesToSearchDao;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceFinder implements PlaceFinder {
    private PlacesToSearchDao dao;

    public DaoPlaceFinder(PlacesToSearchDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Cursor> findPlacesByName(String nameStart, Integer limit) {
        return dao.getPlacesByName(nameStart, limit);
    }

    List<PlaceAndPlateDto> findPlacesListByName(String nameStart, Integer limit) {
        return dao.getPlaceListByName(nameStart, limit);
    }
}
