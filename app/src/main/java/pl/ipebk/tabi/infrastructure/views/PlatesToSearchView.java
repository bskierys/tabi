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
    // TODO: 2016-12-06 format this to proper code dependant on constants
    @Override protected String getDatabaseCreateStatement() {
        return "CREATE VIEW plates_to_search AS\n" +
                "SELECT places._id as _id, places.place_name as place_name, places.place_type as place_type, places" +
                ".voivodeship as voivodeship, places.powiat as powiat, \n" +
                "k.searched_plate AS searched_plate,  k.searched_plate_end AS searched_plate_end, k.plate_priority AS" +
                " plate_priority FROM (\n" +
                "\n" +
                "\tSELECT m.ID AS ID,  m.plate AS searched_plate,  m.plate_end AS searched_plate_end, m" +
                ".plate_priority AS plate_priority FROM (\n" +
                "\t\n" +
                "\t\tSELECT _id AS ID,  plate,  plate_end, 2 AS plate_priority FROM places WHERE has_own_plate = 1 \n" +
                "\t\t\tUNION  \n" +
                "\t\tSELECT w._id AS ID,  w.plate_b AS plate,  w.plate_end AS plate_end, 1 AS plate_priority FROM (\n" +
                "\t\t\tSELECT p._id,  a.plate AS plate_b,  a.plate_end AS plate_end, p.has_own_plate FROM places p " +
                "JOIN additional_plates a ON  p._id = a.place_id \n" +
                "\t\t) AS w WHERE w.has_own_plate = 1\n" +
                "\t\t\n" +
                "\t) AS m\n" +
                "\t\n" +
                ") AS k LEFT JOIN places ON k.ID = places._id ORDER BY plate_priority;";
    }

    @Override public String getName() {
        return VIEW_NAME;
    }

    @Override protected String[] getColumns() {
        return VIEW_COLUMNS;
    }
}
