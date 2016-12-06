/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import java.util.List;

import pl.ipebk.tabi.infrastructure.daos.PlatesToSearchDao;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoLicensePlateFinder implements LicensePlateFinder {
    private PlatesToSearchDao dao;

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
