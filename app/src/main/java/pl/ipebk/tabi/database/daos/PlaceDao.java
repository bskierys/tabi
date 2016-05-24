/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import pl.ipebk.tabi.database.base.Dao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;
import pl.ipebk.tabi.database.tables.SearchHistoryTable;
import pl.ipebk.tabi.ui.main.CategoryListItem;
import pl.ipebk.tabi.ui.search.PlaceListItem;
import rx.Observable;
import timber.log.Timber;

public class PlaceDao extends Dao<Place> {
    private int standardPlacesWithPlateCount;
    private int placesCount;

    public PlaceDao(BriteDatabase database, PlateDao plateDao) {
        super(Place.class, database);
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
        ((PlacesTable) table).setPlaceDao(this);
    }

    /**
     * @return RxJava query observable that should be mapped to {@link CategoryListItem}. Outcome consists of
     * voivodeships and special categories sorted by type (voivodeships first) and by localized name.
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
        String query = "SELECT MAX(" + BaseColumns._ID + ") FROM " + table.getTableName();
        Cursor cursor = db.query(query);

        return getSimpleInt(cursor) + 1;
    }

    /**
     * @param plateStart Plate start to search in plates. Plates are searched in main, or additional plates of model
     * @param limit Additional parameter. You can limit number of returned rows
     * @return List of places that plate starts with given letters. Outcome can be limited and is sorted firstly by
     * plate length (two letter plates are more important for user and comes first) and then alphabetically.
     */
    public Observable<Cursor> getPlacesForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        return db.createQuery(table.getTableName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * @param plateStart Plate start to search in plates. Plates are searched in main, or additional plates of model
     * @param limit Additional parameter. You can limit number of returned rows
     * @return List of places that plate starts with given letters. Outcome can be limited and is sorted firstly by
     * plate length (two letter plates are more important for user and comes first) and then alphabetically.
     */
    public List<Place> getPlaceListForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlacesForPlateStartSql(String plateStart, Integer limit) {
        Timber.d("Searching through plates for: %s with limit %d", plateStart, limit);
        PlatesTable platesTable = new PlatesTable();

        String selectAllAdditionalPlatesWithPlaceId = String.format(
                " SELECT p.%1$s, a.%2$s AS plate_b, a.%3$s AS %4$s, p.%5$s", BaseColumns._ID,
                PlatesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END, PlacesTable.COLUMN_PLATE_END,
                PlacesTable.COLUMN_HAS_OWN_PLATE)
                + " FROM " + table.getTableName() + " p JOIN " + platesTable.getTableName()
                + " a ON " + String.format(" p.%s = a.%s ", BaseColumns._ID, PlatesTable.COLUMN_PLACE_ID);

        String selectAdditionalPlatesInCitiesWithOwnPlates = String.format(
                " SELECT w.%1$s AS ID, w.plate_b AS %2$s, w.plate_end AS %3$s ",
                BaseColumns._ID, PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END)
                + " FROM " + "(" + selectAllAdditionalPlatesWithPlaceId + ")"
                + " AS w WHERE w." + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? ";

        String selectAllPlatesFromPlacesWithOwnPlate = String.format(
                " SELECT %1$s AS ID, %2$s, %3$s ", BaseColumns._ID,
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END)
                + " FROM " + table.getTableName() + " WHERE " + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? "
                + " UNION " + selectAdditionalPlatesInCitiesWithOwnPlates;

        String selectPlacesIdsWithPlatesStartingWithPlateStart = " SELECT m.ID AS ID"
                + ", m." + PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE
                + ", m." + PlacesTable.COLUMN_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END
                + " FROM (" + selectAllPlatesFromPlacesWithOwnPlate
                + ") AS m WHERE m." + PlacesTable.COLUMN_PLATE + " LIKE %s";

        String orderCorrectPlacesByImportanceAndPlatesAlphabetically = selectPlacesIdsWithPlatesStartingWithPlateStart
                + " GROUP BY m.ID " + " ORDER BY " + String.format(
                "length(m.%1$s) ASC, m.%1$s ASC, m.%2$s ASC", PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END);

        String selectPlacesFromIdsButKeepTheOrder = "SELECT "
                + table.getQualifiedColumnsCommaSeparated(table.getTableName())
                + ", k." + PlacesTable.COLUMN_SEARCHED_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE
                + ", k." + PlacesTable.COLUMN_SEARCHED_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END
                + " FROM (" + orderCorrectPlacesByImportanceAndPlatesAlphabetically + ") AS k LEFT JOIN "
                + String.format("%1$s ON k.ID = %1$s.%2$s", table.getTableName(), BaseColumns._ID);

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
        return db.createQuery(table.getTableName(), sql.first, sql.second).map(SqlBrite.Query::run);
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
    public List<Place> getPlaceListByName(String nameStart, Integer limit) {
        Pair<String, String[]> sql = getPlaceListByNameSql(nameStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlaceListByNameSql(String nameStart, Integer limit) {
        Timber.d("Searching through places name for: %s with limit %d", nameStart, limit);

        String alias = "t";
        String columns = table.getQualifiedColumnsCommaSeparated(alias);
        columns += ", " + alias + "." + PlacesTable.COLUMN_PLATE + " as " + PlacesTable.COLUMN_SEARCHED_PLATE;
        columns += ", " + alias + "." + PlacesTable.COLUMN_PLATE_END + " as " + PlacesTable.COLUMN_SEARCHED_PLATE_END;

        String selectPlaceTemplate = " SELECT *, %d as grp FROM %s WHERE %s LIKE %s ";
        String likeArg = "\"" + nameStart + "%\"";

        String selectWithDiacritics = String.format(Locale.US, selectPlaceTemplate, 1, table.getTableName(),
                                                    PlacesTable.COLUMN_NAME_LOWER, likeArg);
        String selectNoDiacritics = String.format(Locale.US, selectPlaceTemplate, 2, table.getTableName(),
                                                  PlacesTable.COLUMN_SEARCH_PHRASE, likeArg);

        String getPlacesByName = "SELECT " + columns + ", MIN(grp) AS source_group FROM ("
                + selectWithDiacritics + " UNION ALL " + selectNoDiacritics + ") AS " + alias + " GROUP BY "
                + BaseColumns._ID + " ORDER BY MIN(grp) ASC, " + PlacesTable.COLUMN_PLACE_TYPE
                + " ASC, " + PlacesTable.COLUMN_NAME + " COLLATE LOCALIZED ASC ";

        if (limit != null && limit > 0) {
            getPlacesByName += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            getPlacesByName += ";";
        }

        return new Pair<>(getPlacesByName, null);
    }

    /**
     * Gets history for places and one random place. Type of the place is dependant of type of search. If place is
     * searched by places, it just returns random place, if place is searched by plates it returns random standard place
     * that has own plate
     *
     * @param limit number of rows returned by this query. Thus means this will return limit - 1 places from history and
     * one random place. To not limit query pass 0 or null here
     * @param type Type of search
     */
    public Observable<Cursor> getHistoryPlaces(Integer limit, SearchType type) {
        Pair<String, String[]> sql = getHistoryPlacesSql(limit, type);
        return db.createQuery(table.getTableName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * Gets history for places and one random place. Type of the place is dependant of type of search. If place is
     * searched by places, it just returns random place, if place is searched by plates it returns random standard place
     * that has own plate
     *
     * @param limit number of rows returned by this query. Thus means this will return limit - 1 places from history and
     * one random place. To not limit query pass 0 or null here
     * @param type Type of search
     */
    public List<PlaceListItem> getHistoryPlacesList(Integer limit, SearchType type) {
        Pair<String, String[]> sql = getHistoryPlacesSql(limit, type);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfPlaceListItemsForCursor(cursor);
    }

    private List<PlaceListItem> getListOfPlaceListItemsForCursor(Cursor cursor) {
        List<PlaceListItem> models = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PlaceListItem model = new PlaceListItem(cursor);
                models.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        models.removeAll(Collections.singleton(null));
        return models;
    }

    private Pair<String, String[]> getHistoryPlacesSql(Integer limit, SearchType type) {
        String columnsF1 = getAliasedColumnsForPlaceListItems("f1");
        String columnsF2 = getAliasedColumnsForPlaceListItems("f2")
                .replace("f2." + PlacesTable.COLUMN_PLACE_TYPE, "f2.type");

        String plateChangeAlias = PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_PLATE;
        String columnsF1Inside = table.getQualifiedColumnsCommaSeparated("p")
                                      .replace("p." + plateChangeAlias, "s." + plateChangeAlias)
                                      .replace("p." + PlacesTable.COLUMN_PLATE_END, "null");
        String columnsF2Inside = table.getQualifiedColumnsCommaSeparated(null)
                                      .replace(PlacesTable.COLUMN_PLACE_TYPE, "6 as type");

        if (standardPlacesWithPlateCount == 0) {
            standardPlacesWithPlateCount = getStandardPlacesWithPlateCount();
        }
        if (placesCount == 0) {
            placesCount = getPlacesCount();
        }

        String limitSql = "";
        if (limit != null && limit > 0) {
            limitSql = " LIMIT " + Integer.toString(limit - 1);
        }

        SearchHistoryTable historyTable = new SearchHistoryTable();

        String selectPlacesFromHistory = " SELECT " + columnsF1Inside + " FROM " + historyTable.getTableName()
                + " s left join " + table.getTableName() + " p on s." + SearchHistoryTable.COLUMN_PLACE_ID
                + " = p." + BaseColumns._ID + " WHERE s." + SearchHistoryTable.COLUMN_SEARCH_TYPE + " = "
                + Integer.toString(type.ordinal()) + " ORDER BY s." + SearchHistoryTable.COLUMN_TIME_SEARCHED
                + " DESC " + limitSql;

        String selectRandom = " SELECT " + columnsF2Inside + " FROM " + table.getTableName();
        String limitRandomTemplate = " LIMIT 1 OFFSET ABS(RANDOM() %% %d";
        if (type == SearchType.PLATE) {
            String whereClause = " WHERE " + PlacesTable.COLUMN_PLACE_TYPE + " < %s AND "
                    + PlacesTable.COLUMN_HAS_OWN_PLATE + " = %s ";
            String[] whereArgs = {Integer.toString(Place.Type.SPECIAL.ordinal()), Integer.toString(1)};
            selectRandom += String.format(whereClause, whereArgs)
                    + String.format(limitRandomTemplate, standardPlacesWithPlateCount);
        } else if (type == SearchType.PLACE) {
            selectRandom += String.format(limitRandomTemplate, placesCount);
        }

        String sql = " SELECT " + columnsF1 + " FROM (" + selectPlacesFromHistory + ") AS f1 " +
                " UNION ALL " +
                " SELECT " + columnsF2 + " FROM (" + selectRandom + ")) as f2";

        return new Pair<>(sql, null);
    }

    private String getAliasedColumnsForPlaceListItems(String alias) {
        String columns = changeAliasName(
                table.getQualifiedColumnsCommaSeparated(alias), alias,
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_SEARCHED_PLATE);
        columns = changeAliasName(columns, alias, PlacesTable.COLUMN_PLATE_END, PlacesTable.COLUMN_SEARCHED_PLATE_END);
        return columns;
    }

    private String changeAliasName(String columnsCommaSeparated, String prefix, String columnName, String alias) {
        String shouldBeReplaced = prefix + "." + columnName + " as " + columnName;
        String replaceFor = prefix + "." + columnName + " as " + alias;
        return columnsCommaSeparated.replace(shouldBeReplaced, replaceFor);
    }

    /**
     * Gets count of places that are not special and that has own plates
     */
    public int getStandardPlacesWithPlateCount() {
        String[] columns = {"count(1)"};
        String whereClause = PlacesTable.COLUMN_PLACE_TYPE + " < ? AND "
                + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? ";
        String[] whereArgs = {Integer.toString(Place.Type.SPECIAL.ordinal()), Integer.toString(1)};

        String sql = SQLiteQueryBuilder.buildQueryString(
                false, table.getTableName(), columns, whereClause, null, null, null, null);
        Cursor cursor = db.query(sql, whereArgs);

        return getSimpleInt(cursor);
    }

    public int getPlacesCount() {
        String[] columns = {"count(1)"};

        String sql = SQLiteQueryBuilder.buildQueryString(
                false, table.getTableName(), columns, null, null, null, null, null);
        Cursor cursor = db.query(sql);

        return getSimpleInt(cursor);
    }

    private int getSimpleInt(Cursor cursor) {
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
