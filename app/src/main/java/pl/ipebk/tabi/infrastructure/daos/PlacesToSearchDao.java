/*
* author: Bartlomiej Kierys
* date: 2016-12-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.Locale;

import pl.ipebk.tabi.infrastructure.base.ViewDao;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.infrastructure.views.PlacesToSearchView;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import rx.Observable;
import timber.log.Timber;

public class PlacesToSearchDao extends ViewDao<PlaceAndPlateDto> {
    public PlacesToSearchDao(BriteDatabase database) {
        super(PlaceAndPlateDto.class, database);
        view = new PlacesToSearchView();
    }

    /**
     * Searches database for places that starts with given letters. All place names are searched by lower case so make
     * sure your text is lower case, and without special signs before you pass it to this method
     *
     * @param nameStart Text to search in the beginning of name of place. Should be lower case and without special
     * characters
     * @param limit Places returned can be limited - use this parameter. Null to ignore.
     * @return Places found in table. Outcome is sorted. if you type with diacritics, outcome with diacritics will be
     * higher on a list. List is sorted also by place type (larger cities tend to be searched more often) and
     * alphabetically.
     */
    public Observable<Cursor> getPlacesByName(String nameStart, Integer limit) {
        Pair<String, String[]> sql = getPlaceListByNameSql(nameStart, limit);
        return db.createQuery(view.getName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * Internal method for tests
     */
    public List<PlaceAndPlateDto> getPlaceListByName(String nameStart, Integer limit) {
        Pair<String, String[]> sql = getPlaceListByNameSql(nameStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlaceListByNameSql(String nameStart, Integer limit) {
        Timber.d("Searching through places name for: %s with limit %d", nameStart, limit);

        String alias = "t";
        String columns = view.getQualifiedColumnsCommaSeparated(alias);

        String selectPlaceTemplate = " SELECT *, %d as grp FROM %s WHERE %s LIKE %s ";
        String likeArg = "\"" + nameStart + "%\"";

        String selectWithDiacritics = String.format(Locale.US, selectPlaceTemplate, 1, view.getName(),
                                                    PlacesTable.COLUMN_NAME_LOWER, likeArg);
        String selectNoDiacritics = String.format(Locale.US, selectPlaceTemplate, 2, view.getName(),
                                                  PlacesTable.COLUMN_SEARCH_PHRASE, likeArg);

        String getPlacesByName = "SELECT " + columns + ", MIN(grp) AS source_group FROM ("
                + selectWithDiacritics + " UNION ALL " + selectNoDiacritics + ") AS " + alias + " GROUP BY "
                + BaseColumns._ID + " ORDER BY " + PlacesTable.COLUMN_HAS_OWN_PLATE + " DESC ,MIN(grp) ASC, "
                + PlacesTable.COLUMN_PLACE_TYPE + " ASC, length( " + PlacesTable.COLUMN_SEARCHED_PLATE + " ) ASC, "
                + PlacesTable.COLUMN_NAME + " COLLATE LOCALIZED ASC ";

        if (limit != null && limit > 0) {
            getPlacesByName += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            getPlacesByName += ";";
        }

        return new Pair<>(getPlacesByName, null);
    }
}
