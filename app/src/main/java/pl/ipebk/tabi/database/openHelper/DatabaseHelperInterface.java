/*
* author: Bartlomiej Kierys
* date: 2016-02-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.openHelper;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.daos.SearchHistoryDao;

/**
 * Interface that creates abstraction for managing database layer in app and tests.
 */
public interface DatabaseHelperInterface {
    PlaceDao getPlaceDao();

    PlateDao getPlateDao();

    SearchHistoryDao getSearchHistoryDao();

    /**
     * Opens database connection if it is closed. Does nothing
     * if connection is already opened.
     */
    void init();

    /**
     * Manually closes opened database connection.
     */
    void destroy();

    /**
     * Drops all tables and then recreates them.
     */
    void purge();
}
