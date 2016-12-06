/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import pl.ipebk.tabi.domain.searchhistory.SearchHistory;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoSearchHistoryRepository implements SearchHistoryRepository {
    private SearchHistoryDao dao;

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
