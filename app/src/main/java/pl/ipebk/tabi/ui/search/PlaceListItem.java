/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bartlomiej.kierys@imed24.pl
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;

import pl.ipebk.tabi.database.tables.PlacesTable;

/**
 * Class representing list item on place list.
 * It has only necessary information to show.
 */
public class PlaceListItem {
    private long placeId;
    private String placeName;
    private String plateStart;
    private String plateEnd;
    private String voivodeship;
    private String powiat;

    public PlaceListItem(Cursor cursor) {
        setPlaceId(cursor.getLong(cursor.getColumnIndex(PlacesTable.COLUMN_ID)));
        setPlaceName(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_NAME)));
        setVoivodeship(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_VOIVODESHIP)));
        setPowiat(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_POWIAT)));
        setPlateStart(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_SEARCHED_PLATE)));
        setPlateEnd(cursor.getString(cursor.getColumnIndex(PlacesTable.COLUMN_SEARCHED_PLATE_END)));
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlateStart() {
        return plateStart;
    }

    public void setPlateStart(String plateStart) {
        this.plateStart = plateStart;
    }

    public String getPlateEnd() {
        return plateEnd;
    }

    public void setPlateEnd(String plateEnd) {
        this.plateEnd = plateEnd;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public String getPowiat() {
        return powiat;
    }

    public void setPowiat(String powiat) {
        this.powiat = powiat;
    }
}
