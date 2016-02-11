/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.sqlite.SQLiteDatabase;

import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;

public class SearchHistoryDao extends Dao<SearchHistory> {
    public SearchHistoryDao(SQLiteDatabase database, PlaceDao placeDao) {
        super(SearchHistory.class, database);
        table = new SearchHistoryTable();
        ((SearchHistoryTable) table).setPlaceDao(placeDao);
    }
}
