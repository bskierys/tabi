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
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.ui.main.CategoryListItem;
import rx.Observable;
import timber.log.Timber;

public class PlaceDao extends Dao<Place> {
    public PlaceDao(BriteDatabase database, PlateDao plateDao) {
        super(Place.class, database);
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
        ((PlacesTable) table).setPlaceDao(this);
    }

    /**
     * @return RxJava query observable that should be mapped to {@link CategoryListItem}. Outcome consists of
     * voivodeships and special categories sorted by type (voivodeships first) and
     * by localized name.
     */
    public Observable<List<CategoryListItem>> getVoivodeshipsObservable() {
        Pair<String, String[]> sql = getVoivodeshipsSql();
        return db.createQuery(table.getTableName(), sql.first, sql.second)
                .mapToList(CategoryListItem::new);
    }

    private Pair<String, String[]> getVoivodeshipsSql() {
        String[] columns = {PlacesTable.COLUMN_VOIVODESHIP,
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLACE_TYPE};

        String selection = String.format(" %1$s = ? OR %1$s = ? ", PlacesTable.COLUMN_PLACE_TYPE);
        String[] selectionArgs = {Integer.toString(Place.Type.VOIVODE_CITY.ordinal()),
                Integer.toString(Place.Type.SPECIAL.ordinal())};

        String groupBy = PlacesTable.COLUMN_VOIVODESHIP + ", " + PlacesTable.COLUMN_PLACE_TYPE;
        String orderBy = String.format(" %s ASC, %s COLLATE LOCALIZED ASC ",
                PlacesTable.COLUMN_PLACE_TYPE, PlacesTable.COLUMN_VOIVODESHIP);

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                columns, selection, groupBy, null, orderBy, null);

        return new Pair<>(sql, selectionArgs);
    }

    public int getNextRowId() {
        String query = "SELECT MAX(" + PlacesTable.COLUMN_ID + ") FROM " + table.getTableName();
        Cursor cursor = db.query(query, null);
        int maxNumber = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                maxNumber = cursor.getInt(0);
            }
            cursor.close();
        }

        return maxNumber + 1;
    }

    /**
     * @param plateStart Plate start to search in plates. Plates are searched in main,
     *                   or additional plates of model
     * @param limit      Additional parameter. You can limit number of returned rows
     * @return List of places that plate starts with given letters. Outcome can be
     * limited and is sorted firstly by plate length (two letter plates are more important
     * for user and comes first) and then alphabetically.
     */
    public Observable<Cursor> getPlacesForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        return db.createQuery(table.getTableName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * @param plateStart Plate start to search in plates. Plates are searched in main,
     *                   or additional plates of model
     * @param limit      Additional parameter. You can limit number of returned rows
     * @return List of places that plate starts with given letters. Outcome can be
     * limited and is sorted firstly by plate length (two letter plates are more important
     * for user and comes first) and then alphabetically.
     */
    public List<Place> getPlaceListForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlacesForPlateStartSql(String plateStart, Integer limit) {
        Timber.d("Searching through plates for: %s with limit %d", plateStart, limit);
        PlatesTable platesTable = new PlatesTable();

        String selectAllAdditionalPlatesWithPlaceId = String.format(" SELECT p.%1$s, a.%2$s AS plate_b, a.%3$s AS %4$s, p.%5$s",
                PlacesTable.COLUMN_ID, PlatesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END, PlacesTable.COLUMN_PLATE_END, PlacesTable.COLUMN_HAS_OWN_PLATE)
                + " FROM " + table.getTableName() + " p JOIN " + platesTable.getTableName()
                + " a ON " + String.format(" p.%s = a.%s ", PlacesTable.COLUMN_ID, PlatesTable.COLUMN_PLACE_ID);

        String selectAdditionalPlatesInCitiesWithOwnPlates = String.format(" SELECT w.%1$s AS ID, w.plate_b AS %2$s, w.plate_end AS %3$s ",
                PlacesTable.COLUMN_ID, PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END)
                + " FROM " + "(" + selectAllAdditionalPlatesWithPlaceId + ")"
                + " AS w WHERE w." + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? ";

        String selectAllPlatesFromPlacesWithOwnPlate = String.format(" SELECT %1$s AS ID, %2$s, %3$s ",
                PlacesTable.COLUMN_ID, PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END)
                + " FROM " + table.getTableName() + " WHERE " + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? "
                + " UNION " + selectAdditionalPlatesInCitiesWithOwnPlates;

        String selectPlacesIdsWithPlatesStartingWithPlateStart = " SELECT m.ID AS ID"
                + ", m." + PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE
                + ", m." + PlacesTable.COLUMN_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END
                + " FROM (" + selectAllPlatesFromPlacesWithOwnPlate
                + ") AS m WHERE m." + PlacesTable.COLUMN_PLATE + " LIKE %s";

        String orderCorrectPlacesByImportanceAndPlatesAlphabetically = selectPlacesIdsWithPlatesStartingWithPlateStart
                + " GROUP BY m.ID " + " ORDER BY " + String.format("length(m.%1$s) ASC, m.%1$s ASC, m.%2$s ASC",
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END);

        String selectPlacesFromIdsButKeepTheOrder = "SELECT " + getQualifiedColumnsCommaSeparated()
                + ", k." + PlacesTable.COLUMN_SEARCHED_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE
                + ", k." + PlacesTable.COLUMN_SEARCHED_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END
                + " FROM (" + orderCorrectPlacesByImportanceAndPlatesAlphabetically + ") AS k LEFT JOIN "
                + String.format("%1$s ON k.ID = %1$s.%2$s", table.getTableName(), PlacesTable.COLUMN_ID);

        String selectPlacesThatHaveOwnPlateStartingWith = String.format(selectPlacesFromIdsButKeepTheOrder,
                "\'" + plateStart + "%\'");

        if (limit != null && limit > 0) {
            selectPlacesThatHaveOwnPlateStartingWith += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            selectPlacesThatHaveOwnPlateStartingWith += ";";
        }

        String[] queryParams = {Integer.toString(1), Integer.toString(1)};

        return new Pair<>(selectPlacesThatHaveOwnPlateStartingWith, queryParams);
    }

    /**
     * Searches database for places that starts with given letters. All place names are searched
     * by lower case so make sure your text is lower case, and without special signs before you
     * pass it to this method
     *
     * @param nameStart Text to search in the beginning of name of place. Should be lower case and
     *                  without special characters
     * @param limit     Places returned can be limited - use this parameter. Null to ignore.
     * @return Places found in table. Outcome is sorted. if you type with diacritics,
     * outcome with diacritics will be higher on a list. List is sorted also by place type (larger
     * cities tend to be searched more often) and alphabetically.
     */
    public Observable<Cursor> getPlacesByName(String nameStart, Integer limit) {
        Pair<String, String[]> sql = getPlaceListByNameSql(nameStart, limit);
        return db.createQuery(table.getTableName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * Searches database for places that starts with given letters. All place names are searched
     * by lower case so make sure your text is lower case, and without special signs before you
     * pass it to this method
     *
     * @param nameStart Text to search in the beginning of name of place. Should be lower case and
     *                  without special characters
     * @param limit     Places returned can be limited - use this parameter. Null to ignore.
     * @return Places found in table. Outcome is sorted. if you type with diacritics,
     * outcome with diacritics will be higher on a list. List is sorted also by place type (larger
     * cities tend to be searched more often) and alphabetically.
     */
    public List<Place> getPlaceListByName(String nameStart, Integer limit) {
        Pair<String, String[]> sql = getPlaceListByNameSql(nameStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlaceListByNameSql(String nameStart, Integer limit) {
        Timber.d("Searching through places name for: %s with limit %d", nameStart, limit);

        String alias = "t";
        String columns = getQualifiedColumnsCommaSeparated().replace(table.getTableName(), alias);
        columns += ", " + alias + "." + PlacesTable.COLUMN_PLATE + " as " + PlacesTable.COLUMN_SEARCHED_PLATE;
        columns += ", " + alias + "." + PlacesTable.COLUMN_PLATE_END + " as " + PlacesTable.COLUMN_SEARCHED_PLATE_END;

        String selectPlaceTemplate = " SELECT *, %d as grp FROM %s WHERE %s LIKE %s ";
        String likeArg = "\"" + nameStart + "%\"";

        // TODO: 2016-02-27 locale
        String selectWithDiacritics = String.format(selectPlaceTemplate, 1, table.getTableName(),
                PlacesTable.COLUMN_NAME_LOWER, likeArg);
        String selectNoDiacritics = String.format(selectPlaceTemplate, 2, table.getTableName(),
                PlacesTable.COLUMN_SEARCH_PHRASE, likeArg);

        String getPlacesByName = "SELECT " + columns + ", MIN(grp) AS source_group FROM ("
                + selectWithDiacritics + " UNION ALL " + selectNoDiacritics + ") AS " + alias + " GROUP BY "
                + PlacesTable.COLUMN_ID + " ORDER BY MIN(grp) ASC, " + PlacesTable.COLUMN_PLACE_TYPE
                + " ASC, " + PlacesTable.COLUMN_NAME + " COLLATE LOCALIZED ASC ";

        if (limit != null && limit > 0) {
            getPlacesByName += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            getPlacesByName += ";";
        }

        return new Pair<>(getPlacesByName, null);
    }
}
