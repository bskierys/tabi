/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

import android.provider.BaseColumns;

import pl.ipebk.tabi.infrastructure.tables.PlacesTable;

public class PlatesToSearchView extends PlaceAndPlateDtoView {
    public static final String COLUMN_PLATE_PRIORITY = "plate_priority";

    public static final String VIEW_NAME = "plates_to_search";

    private static final String[] VIEW_COLUMNS = {
            BaseColumns._ID,
            PlacesTable.COLUMN_NAME,
            PlacesTable.COLUMN_PLACE_TYPE,
            PlacesTable.COLUMN_VOIVODESHIP,
            PlacesTable.COLUMN_POWIAT,
            PlacesTable.COLUMN_SEARCHED_PLATE,
            PlacesTable.COLUMN_SEARCHED_PLATE_END,
            COLUMN_PLATE_PRIORITY
    };

    /**
     * Database create statement too complicated -omitted on purpose
     */
    // TODO: 2016-12-15 further improvement of query
    @Override protected String getDatabaseCreateStatement() {
        String columns = getQualifiedColumnsCommaSeparated("places");
        columns = columns.replace("places.searched_plate", "k.searched_plate");
        columns = columns.replace("places.searched_plate_end", "k.searched_plate_end");
        columns = columns.replace("places.plate_priority", "k.plate_priority");

        String selectMainPlates = "SELECT _id AS ID,  plate,  plate_end, 2 AS plate_priority FROM places WHERE " +
                "has_own_plate = 1";

        String selectAdditionalPlaces = "SELECT w._id AS ID,  w.plate_b AS plate,  w.plate_end AS plate_end, 1 AS " +
                "plate_priority FROM (\n" +
                "\t\t\tSELECT p._id,  a.plate AS plate_b,  a.plate_end AS plate_end, p.has_own_plate FROM places p " +
                "JOIN additional_plates a ON  p._id = a.place_id \n" +
                "\t\t) AS w WHERE w.has_own_plate = 1";

        String selectAllPlates = "SELECT m.ID AS ID,  m.plate AS searched_plate,  m.plate_end AS searched_plate_end, " +
                "m.plate_priority AS plate_priority FROM ( " + selectMainPlates + " UNION "
                + selectAdditionalPlaces + " ) AS m";

        String selectViewStatement = "SELECT " + columns + " FROM (" + selectAllPlates + ") "
                + "AS k LEFT JOIN places ON k.ID = places._id ORDER BY plate_priority;";

        String createViewStatement = "CREATE VIEW " + VIEW_NAME + " AS " + selectViewStatement;

        return createViewStatement;
    }

    @Override public String getName() {
        return VIEW_NAME;
    }

    @Override protected String[] getColumns() {
        return VIEW_COLUMNS;
    }
}
