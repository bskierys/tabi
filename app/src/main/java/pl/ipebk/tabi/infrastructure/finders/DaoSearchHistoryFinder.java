/*
* author: Bartlomiej Kierys
* date: 2016-12-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import android.database.Cursor;

import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import rx.Observable;

/**
 * Implementation of {@link SearchHistoryFinder} that uses app's dao
 */
public class DaoSearchHistoryFinder implements SearchHistoryFinder {
    private SearchHistoryDao dao;

    @Inject public DaoSearchHistoryFinder(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getSearchHistoryDao();
    }

    /**
     * Internal constructor for tests
     */
    DaoSearchHistoryFinder(SearchHistoryDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Cursor> findHistoryPlaces(Integer limit, SearchType type) {
        return dao.getHistoryPlaces(limit, type.ordinal());
    }

    List<PlaceAndPlateDto> findHistoryPlacesList(Integer limit, SearchType type) {
        return dao.getHistoryPlacesList(limit, type.ordinal());
    }
}
