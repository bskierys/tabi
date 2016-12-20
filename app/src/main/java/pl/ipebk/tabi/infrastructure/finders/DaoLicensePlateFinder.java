/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.infrastructure.daos.PlatesToSearchDao;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import rx.Observable;

/**
 * Implementation of {@link LicensePlateFinder} that bases on apps Dao
 */
public class DaoLicensePlateFinder implements LicensePlateFinder {
    private PlatesToSearchDao dao;

    @Inject DaoLicensePlateFinder(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getPlatesToSearchDao();
    }

    public DaoLicensePlateFinder(PlatesToSearchDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Cursor> findPlacesForPlateStart(String plateStart, Integer limit) {
        return dao.getPlacesForPlateStart(plateStart, limit);
    }

    List<PlaceAndPlateDto> findPlaceListForPlateStart(String plateStart, Integer limit){
        return dao.getPlaceListForPlateStart(plateStart, limit);
    }
}
