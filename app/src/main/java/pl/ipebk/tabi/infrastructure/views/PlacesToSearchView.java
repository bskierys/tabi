/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.views;

import android.provider.BaseColumns;

import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.readmodel.PlaceType;

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
    @Override protected String getDatabaseCreateStatement() {
        return "CREATE VIEW " + VIEW_NAME + " AS " +
                " SELECT " + BaseColumns._ID + ", " + PlacesTable.COLUMN_NAME + ", "
                + PlacesTable.COLUMN_PLACE_TYPE + ", " + PlacesTable.COLUMN_VOIVODESHIP + ", "
                + PlacesTable.COLUMN_POWIAT + ", "
                + PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE + ", "
                + PlacesTable.COLUMN_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END + ", "
                + PlacesTable.COLUMN_NAME_LOWER + ", " + PlacesTable.COLUMN_SEARCH_PHRASE + ", "
                + PlacesTable.COLUMN_HAS_OWN_PLATE + " FROM " + PlacesTable.TABLE_NAME
                + " WHERE " + PlacesTable.COLUMN_PLACE_TYPE + " < " + Integer.toString(PlaceType.SPECIAL.ordinal()) +
                ";";
    }

    @Override public String getName() {
        return VIEW_NAME;
    }

    @Override protected String[] getColumns() {
        return VIEW_COLUMNS;
    }
}
