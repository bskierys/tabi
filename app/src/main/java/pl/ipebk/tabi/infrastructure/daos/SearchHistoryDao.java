/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.ipebk.tabi.infrastructure.base.Dao;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.infrastructure.tables.SearchHistoryTable;
import pl.ipebk.tabi.infrastructure.views.DatabaseViewPlaceAndPlateDtoFactory;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import rx.Observable;
import timber.log.Timber;

public class SearchHistoryDao extends Dao<SearchHistoryModel> {
    private int standardPlacesWithPlateCount;
    private int placesCount;
    private PlacesTable placesTable;
    private PlaceAndPlateDtoFactory itemFactory;

    public SearchHistoryDao(BriteDatabase database) {
        super(SearchHistoryModel.class, database);
        table = new SearchHistoryTable();
        placesTable = new PlacesTable();
        itemFactory = new DatabaseViewPlaceAndPlateDtoFactory();
        placesCount = getPlacesCount();
        standardPlacesWithPlateCount = getStandardPlacesWithPlateCount();
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

    /**
     * Gets history for places and one random place. Type of the place is dependant of type of search. If place is
     * searched by places, it just returns random place, if place is searched by plates it returns random standard place
     * that has own plate
     *
     * @param limit number of rows returned by this query. Thus means this will return limit - 1 places from history and
     * one random place. To not limit query pass 0 or null here
     * @param type Type of search
     */
    public Observable<Cursor> getHistoryPlaces(Integer limit, int type) {
        Pair<String, String[]> sql = getHistoryPlacesSql(limit, type);
        return db.createQuery(table.getName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * Internal method for tests
     */
    public List<PlaceAndPlateDto> getHistoryPlacesList(Integer limit, int type) {
        Pair<String, String[]> sql = getHistoryPlacesSql(limit, type);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfDtoForCursor(cursor);
    }

    @NonNull private List<PlaceAndPlateDto> getListOfDtoForCursor(Cursor cursor) {
        List<PlaceAndPlateDto> models = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PlaceAndPlateDto model = itemFactory.createFromCursor(cursor);
                models.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        models.removeAll(Collections.singleton(null));
        return models;
    }

    private Pair<String, String[]> getHistoryPlacesSql(Integer limit, int type) {
        String columnsF1 = getAliasedColumnsForPlaceListItems("f1");
        String columnsF2 = getAliasedColumnsForPlaceListItems("f2")
                .replace("f2." + PlacesTable.COLUMN_PLACE_TYPE, "f2.type");

        String plateChangeAlias = PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_PLATE;
        String columnsF1Inside = placesTable.getQualifiedColumnsCommaSeparated("p")
                                      .replace("p." + plateChangeAlias, "s." + plateChangeAlias)
                                      .replace("p." + PlacesTable.COLUMN_PLATE_END, "null");
        String columnsF2Inside = placesTable.getQualifiedColumnsCommaSeparated(null)
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

        String selectPlacesFromHistory = " SELECT " + columnsF1Inside + " FROM " + SearchHistoryTable.TABLE_NAME
                + " s left join " + PlacesTable.TABLE_NAME + " p on s." + SearchHistoryTable.COLUMN_PLACE_ID
                + " = p." + BaseColumns._ID + " WHERE s." + SearchHistoryTable.COLUMN_SEARCH_TYPE + " = "
                + Integer.toString(type) + " ORDER BY s." + SearchHistoryTable.COLUMN_TIME_SEARCHED
                + " DESC " + limitSql;

        String selectRandom = " SELECT " + columnsF2Inside + " FROM " + PlacesTable.TABLE_NAME;
        String limitRandomTemplate = " LIMIT 1 OFFSET ABS(RANDOM() %% %d";
        if (type == SearchType.LICENSE_PLATE.ordinal()) {
            String whereClause = " WHERE " + PlacesTable.COLUMN_PLACE_TYPE + " < %s AND "
                    + PlacesTable.COLUMN_HAS_OWN_PLATE + " = %s ";
            String[] whereArgs = {Integer.toString(PlaceType.SPECIAL.ordinal()), Integer.toString(1)};
            selectRandom += String.format(whereClause, whereArgs)
                    + String.format(limitRandomTemplate, standardPlacesWithPlateCount);
        } else if (type == SearchType.PLACE.ordinal()) {
            selectRandom += String.format(limitRandomTemplate, placesCount);
        }

        String sql = " SELECT " + columnsF1 + " FROM (" + selectPlacesFromHistory + ") AS f1 " +
                " UNION ALL " +
                " SELECT " + columnsF2 + " FROM (" + selectRandom + ")) as f2";

        return new Pair<>(sql, null);
    }

    private String getAliasedColumnsForPlaceListItems(String alias) {
        String columns = changeAliasName(
                placesTable.getQualifiedColumnsCommaSeparated(alias), alias,
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
    int getStandardPlacesWithPlateCount() {
        String[] columns = {"count(1)"};
        String whereClause = PlacesTable.COLUMN_PLACE_TYPE + " < ? AND "
                + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? ";
        String[] whereArgs = {Integer.toString(PlaceType.SPECIAL.ordinal()), Integer.toString(1)};

        String sql = SQLiteQueryBuilder.buildQueryString(
                false, PlacesTable.TABLE_NAME, columns, whereClause, null, null, null, null);
        Cursor cursor = db.query(sql, whereArgs);

        return getSimpleInt(cursor);
    }

    /**
     * Gets count of places that are not special
     */
    int getPlacesCount() {
        String[] columns = {"count(1)"};
        String whereClause = PlacesTable.COLUMN_PLACE_TYPE + " < ? ";
        String[] whereArgs = {Integer.toString(PlaceType.SPECIAL.ordinal())};
        String sql = SQLiteQueryBuilder.buildQueryString(
                false, PlacesTable.TABLE_NAME, columns, whereClause, null, null, null, null);
        Cursor cursor = db.query(sql, whereArgs);

        return getSimpleInt(cursor);
    }
}
