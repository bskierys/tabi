/*
* author: Bartlomiej Kierys
* date: 2016-12-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.infrastructure.daos.PlacesToSearchDao;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import rx.Observable;

/**
 * Implementation of {@link PlaceFinder} that bases on apps Dao
 */
public class DaoPlaceFinder implements PlaceFinder {
    private PlacesToSearchDao dao;

    @Inject DaoPlaceFinder(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getPlacesToSearchDao();
    }

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
