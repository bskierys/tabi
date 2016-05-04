/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.content.ContentValues;
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
import timber.log.Timber;

public class SearchHistoryDao extends Dao<SearchHistory> {
    public SearchHistoryDao(BriteDatabase database) {
        super(SearchHistory.class, database);
        table = new SearchHistoryTable();
    }

    /**
     * Adds or updates existing history. History is updated if same place is searched in same SearchType. Updated fields
     * are: plate and time searched, so place may pop in as recent searched.
     *
     * @param history Place to add or update in history.
     */
    public void updateOrAdd(SearchHistory history) {
        ContentValues values = table.modelToContentValues(history);
        String updateQuery = SearchHistoryTable.COLUMN_PLACE_ID + " = ? AND "
                + SearchHistoryTable.COLUMN_SEARCH_TYPE + " = ? ";
        String[] updateQueryArgs = {Long.toString(history.getPlaceId()),
                Integer.toString(history.getSearchType().ordinal())};

        int rowsAffected = db.update(table.getTableName(), values, updateQuery, updateQueryArgs);

        if (rowsAffected <= 0) {
            Timber.d("Inserting new Search history for placeId %d", history.getPlaceId());
            Long id = db.insert(table.getTableName(), values);
            history.setId(id);
            if (id < 0) {
                Timber.e("Unable to insert entity %s", type.toString());
            } else {
                Timber.d("Inserted SearchHistory with placeId: %d", history.getPlaceId());
            }
        } else {
            Timber.d("Search history for placeId %d updated", history.getPlaceId());
        }
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
