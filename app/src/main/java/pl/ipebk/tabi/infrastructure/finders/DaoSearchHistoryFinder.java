/*
* author: Bartlomiej Kierys
* date: 2016-12-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.readmodel.SearchType;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoSearchHistoryFinder implements SearchHistoryFinder {
    private PlaceDao dao;

    public DaoSearchHistoryFinder(PlaceDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Cursor> findHistoryPlaces(Integer limit, SearchType type) {
        return dao.getHistoryPlaces(limit, type.ordinal());
    }
}
