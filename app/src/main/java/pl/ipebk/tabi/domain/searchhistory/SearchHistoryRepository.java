/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.searchhistory;

/**
 * This repository has only one purpose - insert search history into database
 */
public interface SearchHistoryRepository {
    /**
     * Save SearchHistory to database
     */
    void save(SearchHistory history);
}
