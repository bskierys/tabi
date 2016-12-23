/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import javax.inject.Inject;

import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;

/**
 * Implementation of {@link SearchHistoryRepository} with app's dao
 */
public class DaoSearchHistoryRepository implements SearchHistoryRepository {
    private SearchHistoryDao dao;

    @Inject public DaoSearchHistoryRepository(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getSearchHistoryDao();
    }

    /**
     * Internal constructor for tests
     */
    DaoSearchHistoryRepository(SearchHistoryDao dao) {
        this.dao = dao;
    }

    @Override public void save(SearchHistory history) {
        dao.updateOrAdd(SearchHistoryModel.create(0, history.placeId().getValue(),
                                                  history.plate(),
                                                  history.timeSearched(),
                                                  history.searchType().ordinal()));
    }
}
