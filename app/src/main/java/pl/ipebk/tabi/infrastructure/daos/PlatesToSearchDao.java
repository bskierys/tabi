/*
 * author: Bartlomiej Kierys
 * date: 2016-12-04
 * email: bskierys@gmail.com
 */
package pl.ipebk.tabi.infrastructure.daos;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.util.Pair;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import pl.ipebk.tabi.infrastructure.base.ViewDao;
import pl.ipebk.tabi.infrastructure.tables.PlacesTable;
import pl.ipebk.tabi.infrastructure.views.PlatesToSearchView;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import rx.Observable;
import timber.log.Timber;

public class PlatesToSearchDao extends ViewDao<PlaceAndPlateDto> {
    public PlatesToSearchDao(BriteDatabase db) {
        super(PlaceAndPlateDto.class, db);
        this.view = new PlatesToSearchView();
    }

    /**
     * @param voivodeship Voivodeship name to look for
     * @return List of places with from given voivodeship
     */
    public Observable<Cursor> getPlacesForVoivodeshipName(String voivodeship) {
        String argument = "\'" + voivodeship + "\'";
        String projection = BaseColumns._ID + "," +
                PlacesTable.COLUMN_NAME + "," +
                PlacesTable.COLUMN_PLACE_TYPE + "," +
                PlacesTable.COLUMN_VOIVODESHIP + "," +
                PlacesTable.COLUMN_POWIAT + "," +
                PlacesTable.COLUMN_PLATE + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE + "," +
                PlacesTable.COLUMN_PLATE_END + " AS " + PlacesTable.COLUMN_SEARCHED_PLATE_END;
        String query = "SELECT " + projection + " FROM "
                + PlacesTable.TABLE_NAME + " WHERE " + PlacesTable.COLUMN_VOIVODESHIP + "=" + argument
                + " ORDER BY " + PlacesTable.COLUMN_SEARCHED_PLATE + ";";
        return db.createQuery(view.getName(), query).map(SqlBrite.Query::run);
    }

    /**
     * @param plateStart PlateModel start to search in plates. Plates are searched in main, or additional plates of model
     * @param limit Additional parameter. You can limit number of returned rows
     * @return List of places that plate starts with given letters. Outcome can be limited and is sorted firstly by plate length (two letter plates are more important for
     * user and comes first) and then alphabetically.
     */
    public Observable<Cursor> getPlacesForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        return db.createQuery(view.getName(), sql.first, sql.second).map(SqlBrite.Query::run);
    }

    /**
     * Internal test method
     */
    public List<PlaceAndPlateDto> getPlaceListForPlateStart(String plateStart, Integer limit) {
        Pair<String, String[]> sql = getPlacesForPlateStartSql(plateStart, limit);
        Cursor cursor = db.query(sql.first, sql.second);
        return getListOfModelsForCursor(cursor);
    }

    private Pair<String, String[]> getPlacesForPlateStartSql(String plateStart, Integer limit) {
        Timber.d("Searching through plates for: %s with limit %d", plateStart, limit);

        String selectIdAndPriorityForBestMatchingPlate = " SELECT " + BaseColumns._ID + " , MAX( "
                + PlatesToSearchView.COLUMN_PLATE_PRIORITY + " ) AS max_plate_priority FROM " + view.getName()
                + " WHERE " + PlacesTable.COLUMN_SEARCHED_PLATE + " LIKE %s GROUP BY " + BaseColumns._ID;

        String selectGrouped = " SELECT * FROM ( " +
                selectIdAndPriorityForBestMatchingPlate + " ) as o LEFT JOIN " + view.getName() + " p ON " +
                " o." + BaseColumns._ID + " = p." + BaseColumns._ID + " WHERE o.max_plate_priority = p." +
                PlatesToSearchView.COLUMN_PLATE_PRIORITY;

        String orderFormat = " SELECT " + view.getQualifiedColumnsCommaSeparated(null) + " FROM ( " + selectGrouped +
                " ) as w WHERE " + PlacesTable.COLUMN_SEARCHED_PLATE + " LIKE %s ORDER BY " +
                PlacesTable.COLUMN_PLACE_TYPE + " ASC, length( " + PlacesTable.COLUMN_SEARCHED_PLATE + " ) ASC, " +
                PlacesTable.COLUMN_SEARCHED_PLATE + " ASC, " + PlacesTable.COLUMN_SEARCHED_PLATE_END + " ASC ";

        String placeLikePattern = "\'" + plateStart + "%\'";

        String selectPlacesThatHaveOwnPlateStartWith = String.format(orderFormat, placeLikePattern, placeLikePattern);

        if (limit != null && limit > 0) {
            selectPlacesThatHaveOwnPlateStartWith += " LIMIT " + Integer.toString(limit) + ";";
        } else {
            selectPlacesThatHaveOwnPlateStartWith += ";";
        }

        return new Pair<>(selectPlacesThatHaveOwnPlateStartWith, null);
    }
}
