/*
* author: Bartlomiej Kierys
* date: 2016-02-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.openHelper;

import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.daos.PlacesToSearchDao;
import pl.ipebk.tabi.infrastructure.daos.PlateDao;
import pl.ipebk.tabi.infrastructure.daos.PlatesToSearchDao;
import pl.ipebk.tabi.infrastructure.daos.SearchHistoryDao;

/**
 * Interface that creates abstraction for managing database layer in app and tests.
 */
public interface DatabaseHelperInterface {
    PlaceDao getPlaceDao();

    PlateDao getPlateDao();

    SearchHistoryDao getSearchHistoryDao();

    PlacesToSearchDao getPlacesToSearchDao();

    PlatesToSearchDao getPlatesToSearchDao();

    /**
     * Opens database connection if it is closed. Does nothing if connection is already opened.
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
