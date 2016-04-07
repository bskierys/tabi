/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import pl.ipebk.tabi.database.base.Dao;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;
import rx.Observable;

public class SearchHistoryDao extends Dao<SearchHistory> {
    public SearchHistoryDao(BriteDatabase database, PlaceDao placeDao) {
        super(SearchHistory.class, database);
        table = new SearchHistoryTable();
        ((SearchHistoryTable) table).setPlaceDao(placeDao);
    }

    /**
     * @param type {@link pl.ipebk.tabi.database.models.SearchType} to filter history
     * @param limit nummber of history rows to return. Null to ignore
     * @return Search history for given type ordered be time descending.
     */
    public Observable<List<SearchHistory>> getHistoryForType(SearchType type, Integer limit) {
        Pair<String, String[]> sql = getHistoryListForTypeSql(type, limit);
        return db.createQuery(table.getTableName(), sql.first, sql.second)
                 .mapToList(cursor -> table.cursorToModel(cursor));
    }

    /**
     * @param type {@link pl.ipebk.tabi.database.models.SearchType} to filter history
     * @param limit nummber of history rows to return. Null to ignore
     * @return Search history for given type ordered be time descending.
     */
    public List<SearchHistory> getHistoryListForType(SearchType type, Integer limit) {
        Pair<String, String[]> sql = getHistoryListForTypeSql(type, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getHistoryListForTypeSql(SearchType type, Integer limit) {
        String selection = SearchHistoryTable.COLUMN_SEARCH_TYPE + " = ?";
        String[] selectionArgs = {Integer.toString(type.ordinal())};

        String limited = null;
        if (limit != null) {
            limited = Integer.toString(limit);
        }

        String orderBy = SearchHistoryTable.COLUMN_TIME_SEARCHED + " DESC";
        String groupBy = SearchHistoryTable.COLUMN_PLACE_ID;
        String having = String.format(" %1$s = max( %1$s ) ", SearchHistoryTable.COLUMN_TIME_SEARCHED);

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                                                         table.getQualifiedColumns(), selection, groupBy, having,
                                                         orderBy, limited);

        return new Pair<>(sql, selectionArgs);
    }
}
