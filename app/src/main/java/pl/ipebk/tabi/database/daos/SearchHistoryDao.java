/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;

public class SearchHistoryDao extends Dao<SearchHistory> {
    public SearchHistoryDao(SQLiteDatabase database, PlaceDao placeDao) {
        super(SearchHistory.class, database);
        table = new SearchHistoryTable();
        ((SearchHistoryTable) table).setPlaceDao(placeDao);
    }

    /**
     * @param type  {@link pl.ipebk.tabi.database.models.SearchHistory.SearchType} to filter history
     * @param limit nummber of history rows to return. Null to ignore
     * @return Search history for given type ordered be time descending.
     */
    public Cursor getHistoryForType(SearchHistory.SearchType type, Integer limit) {
        String selection = SearchHistoryTable.COLUMN_SEARCH_TYPE + " = ?";
        String[] selectionArgs = {Integer.toString(type.ordinal())};

        String limited = null;
        if (limit != null) {
            limited = Integer.toString(limit);
        }

        String orderBy = SearchHistoryTable.COLUMN_TIME_SEARCHED + " DESC";

        Cursor cursor = db.query(table.getTableName(), table.getQualifiedColumns(),
                selection, selectionArgs, null, null, orderBy, limited);

        return cursor;
    }

    /**
     * @param type  {@link pl.ipebk.tabi.database.models.SearchHistory.SearchType} to filter history
     * @param limit nummber of history rows to return. Null to ignore
     * @return Search history for given type ordered be time descending.
     */
    public List<SearchHistory> getHistoryListForType(SearchHistory.SearchType type, Integer limit) {
        Cursor cursor = getHistoryForType(type, limit);
        return getListOfModelsForCursor(cursor);
    }
}
