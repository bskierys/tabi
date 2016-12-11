/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

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
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.infrastructure.tables.SearchHistoryTable;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.readmodel.SearchType;
import rx.Observable;

public class PlaceDao extends Dao<PlaceModel> {
    private int standardPlacesWithPlateCount;
    private int placesCount;

    public PlaceDao(BriteDatabase database, PlateDao plateDao) {
        super(PlaceModel.class, database);
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
        ((PlacesTable) table).setPlaceDao(this);
        placesCount = getPlacesCount();
        standardPlacesWithPlateCount = getStandardPlacesWithPlateCount();
    }

    public int getNextRowId() {
        String query = "SELECT MAX(" + BaseColumns._ID + ") FROM " + table.getName();
        Cursor cursor = db.query(query);

        return getSimpleInt(cursor) + 1;
    }

    // TODO: 2016-12-05 move to SearchHistoryDao

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

    @NonNull protected List<PlaceAndPlateDto> getListOfDtoForCursor(Cursor cursor) {
        // TODO: 2016-12-10 temporary method
        List<PlaceAndPlateDto> models = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PlaceAndPlateDto model = PlaceAndPlateDto.create(cursor);
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

        String selectPlacesFromHistory = " SELECT " + columnsF1Inside + " FROM " + historyTable.getName()
                + " s left join " + table.getName() + " p on s." + SearchHistoryTable.COLUMN_PLACE_ID
                + " = p." + BaseColumns._ID + " WHERE s." + SearchHistoryTable.COLUMN_SEARCH_TYPE + " = "
                + Integer.toString(type) + " ORDER BY s." + SearchHistoryTable.COLUMN_TIME_SEARCHED
                + " DESC " + limitSql;

        String selectRandom = " SELECT " + columnsF2Inside + " FROM " + table.getName();
        String limitRandomTemplate = " LIMIT 1 OFFSET ABS(RANDOM() %% %d";
        if (type == SearchType.PLATE.ordinal()) {
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
    int getStandardPlacesWithPlateCount() {
        String[] columns = {"count(1)"};
        String whereClause = PlacesTable.COLUMN_PLACE_TYPE + " < ? AND "
                + PlacesTable.COLUMN_HAS_OWN_PLATE + " = ? ";
        String[] whereArgs = {Integer.toString(PlaceType.SPECIAL.ordinal()), Integer.toString(1)};

        String sql = SQLiteQueryBuilder.buildQueryString(
                false, table.getName(), columns, whereClause, null, null, null, null);
        Cursor cursor = db.query(sql, whereArgs);

        return getSimpleInt(cursor);
    }

    private int getPlacesCount() {
        String[] columns = {"count(1)"};
        String whereClause = PlacesTable.COLUMN_PLACE_TYPE + " < ? ";
        String[] whereArgs = {Integer.toString(PlaceType.SPECIAL.ordinal())};
        String sql = SQLiteQueryBuilder.buildQueryString(
                false, table.getName(), columns, whereClause, null, null, null, null);
        Cursor cursor = db.query(sql, whereArgs);

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
