/*
* author: Bartlomiej Kierys
* date: 2016-02-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Voivodeship;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.database.tables.PlatesTable;

public class PlaceDao extends Dao<Place> {
    public PlaceDao(SQLiteDatabase database, PlateDao plateDao) {
        super(Place.class, database);
        table = new PlacesTable();
        ((PlacesTable) table).setPlateDao(plateDao);
        ((PlacesTable) table).setPlaceDao(this);
    }

    /**
     * @return Cursor that should be mapped to {@link Voivodeship}. Outcome consists of
     * voivodeships and special categories sorted by type (voivodeships first) and
     * by localized name.
     */
    public Cursor getVoivodeshipsCursor() {
        String[] columns = {PlacesTable.COLUMN_VOIVODESHIP,
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLACE_TYPE};

        String selection = String.format(" %1$s = ? OR %1$s = ? ", PlacesTable.COLUMN_PLACE_TYPE);
        String[] selectionArgs = {Integer.toString(Place.Type.VOIVODE_CITY.ordinal()),
                Integer.toString(Place.Type.SPECIAL.ordinal())};

        String groupBy = PlacesTable.COLUMN_VOIVODESHIP + ", " + PlacesTable.COLUMN_PLACE_TYPE;
        String orderBy = String.format(" %s ASC, %s COLLATE LOCALIZED ASC ",
                PlacesTable.COLUMN_PLACE_TYPE, PlacesTable.COLUMN_VOIVODESHIP);

        return db.query(table.getTableName(), columns, selection, selectionArgs, groupBy, null, orderBy);
    }

    /**
     * @return Outcome consists of voivodeships and special categories
     * sorted by type (voivodeships first) and by localized name.
     */
    public List<Voivodeship> getVoivodeships() {
        PlacesTable placesTable = (PlacesTable) table;

        List<Voivodeship> voivodeships = new ArrayList<>();
        Cursor cursor = getVoivodeshipsCursor();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Voivodeship model = placesTable.cursorToVoivodeship(cursor);
                voivodeships.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        voivodeships.removeAll(Collections.singleton(null));
        return voivodeships;
    }

    public int getNextRowId() {
        String query = "SELECT MAX(" + PlacesTable.COLUMN_ID + ") FROM " + table.getTableName();
        Cursor cursor = db.rawQuery(query, null);
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
    public Cursor getPlacesForPlateStart(String plateStart, Integer limit) {
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

        String selectPlacesIdsWithPlatesStartingWithPlateStart = " SELECT m.ID AS ID FROM ("
                + selectAllPlatesFromPlacesWithOwnPlate
                + ") AS m WHERE m." + PlacesTable.COLUMN_PLATE + " LIKE %s";

        String orderCorrectPlacesByImportanceAndPlatesAlphabetically = selectPlacesIdsWithPlatesStartingWithPlateStart
                + " GROUP BY m.ID " + " ORDER BY " + String.format("length(m.%1$s) ASC, m.%1$s ASC, m.%2$s ASC",
                PlacesTable.COLUMN_PLATE, PlacesTable.COLUMN_PLATE_END);

        String selectPlacesFromIdsButKeepTheOrder = "SELECT " + getQualifiedColumnsCommaSeparated() + " FROM ("
                + orderCorrectPlacesByImportanceAndPlatesAlphabetically + ") AS k LEFT JOIN "
                + String.format("%1$s ON k.ID = %1$s.%2$s", table.getTableName(), PlacesTable.COLUMN_ID);

        String selectPlacesThatHaveOwnPlateStartingWith = String.format(selectPlacesFromIdsButKeepTheOrder,
                "\'" + plateStart + "%\'");

        if (limit != null && limit > 0) {
            selectPlacesThatHaveOwnPlateStartingWith += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            selectPlacesThatHaveOwnPlateStartingWith += ";";
        }

        String[] queryParams = {Integer.toString(1), Integer.toString(1)};

        return db.rawQuery(selectPlacesThatHaveOwnPlateStartingWith, queryParams);
    }

    @NonNull private String getQualifiedColumnsCommaSeparated() {
        String commaSeparated = "";
        String[] columns = table.getQualifiedColumns();
        for (int i = 0; i < columns.length; i++) {
            commaSeparated += columns[i];
            if (i != columns.length - 1) {
                commaSeparated += ", ";
            }
        }
        return commaSeparated;
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
        Cursor cursor = getPlacesForPlateStart(plateStart, limit);
        return getListOfModelsForCursor(cursor);
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
    public Cursor getPlacesByName(String nameStart, Integer limit) {
        String alias = "t";
        String columns = getQualifiedColumnsCommaSeparated().replace(table.getTableName(), alias);

        String selectPlaceTemplate = " SELECT *, %d as grp FROM %s WHERE %s LIKE %s ";
        String likeArg = "\"" + nameStart + "%\"";

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

        return db.rawQuery(getPlacesByName, null);
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
        Cursor cursor = getPlacesByName(nameStart, limit);
        return getListOfModelsForCursor(cursor);
    }
}
