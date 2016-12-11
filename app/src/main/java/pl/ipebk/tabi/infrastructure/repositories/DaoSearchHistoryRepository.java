/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import javax.inject.Inject;

import pl.ipebk.tabi.domain.searchhistory.SearchHistory;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoSearchHistoryRepository implements SearchHistoryRepository {
    private SearchHistoryDao dao;

    @Inject public DaoSearchHistoryRepository(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getSearchHistoryDao();
    }

    // TODO: 2016-12-10 ??
    public DaoSearchHistoryRepository(SearchHistoryDao dao) {
        this.dao = dao;
    }

    @Override public void save(SearchHistory history) {
        dao.updateOrAdd(SearchHistoryModel.create(0, history.getPlaceId(),
                                                  history.getPlate(),
                                                  history.getTimeSearched(),
                                                  history.getSearchType().ordinal()));
    }
}
