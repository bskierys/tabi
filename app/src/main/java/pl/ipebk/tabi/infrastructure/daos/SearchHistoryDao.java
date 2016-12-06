/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.content.ContentValues;

import com.squareup.sqlbrite.BriteDatabase;

import pl.ipebk.tabi.infrastructure.base.Dao;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.infrastructure.tables.SearchHistoryTable;
import timber.log.Timber;

public class SearchHistoryDao extends Dao<SearchHistoryModel> {
    public SearchHistoryDao(BriteDatabase database) {
        super(SearchHistoryModel.class, database);
        table = new SearchHistoryTable();
    }

    /**
     * Adds or updates existing history. History is updated if same place is searched in same SearchType. Updated fields
     * are: plate and time searched, so place may pop in as recent searched.
     *
     * @param history Place to add or update in history.
     */
    public void updateOrAdd(SearchHistoryModel history) {
        ContentValues values = table.modelToContentValues(history);
        String updateQuery = SearchHistoryTable.COLUMN_PLACE_ID + " = ? AND "
                + SearchHistoryTable.COLUMN_SEARCH_TYPE + " = ? ";
        String[] updateQueryArgs = {Long.toString(history.placeId()),
                Integer.toString(history.searchType())};

        int rowsAffected = db.update(table.getName(), values, updateQuery, updateQueryArgs);

        if (rowsAffected <= 0) {
            Timber.d("Inserting new Search history for placeId %d", history.placeId());
            Long id = db.insert(table.getName(), values);
            history.setId(id);
            if (id < 0) {
                Timber.e("Unable to insert entity %s", type.toString());
            } else {
                Timber.d("Inserted SearchHistory with placeId: %d", history.placeId());
            }
        } else {
            Timber.d("Search history for placeId %d updated", history.placeId());
        }
    }
}
