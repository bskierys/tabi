/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

import android.provider.BaseColumns;

import pl.ipebk.tabi.infrastructure.tables.PlacesTable;

public class PlacesToSearchView extends PlaceAndPlateDtoView {
    public static final String VIEW_NAME = "places_to_search";

    private static final String[] VIEW_COLUMNS = {
            BaseColumns._ID,
            PlacesTable.COLUMN_NAME,
            PlacesTable.COLUMN_PLACE_TYPE,
            PlacesTable.COLUMN_VOIVODESHIP,
            PlacesTable.COLUMN_POWIAT,
            PlacesTable.COLUMN_SEARCHED_PLATE,
            PlacesTable.COLUMN_SEARCHED_PLATE_END
    };

    /**
     * Database create statement too complicated -omitted on purpose
     */
    // TODO: 2016-12-06 format this to proper code dependant on constants
    @Override protected String getDatabaseCreateStatement() {
        return "CREATE VIEW places_to_search AS\n" +
                "SELECT _id, place_name, place_type, voivodeship, powiat, plate, plate_end, place_name_to_lower, " +
                "place_name_to_lower_no_diacritics, has_own_plate FROM places WHERE place_type < 5;";
    }

    @Override public String getName() {
        return VIEW_NAME;
    }

    @Override protected String[] getColumns() {
        return VIEW_COLUMNS;
    }
}
