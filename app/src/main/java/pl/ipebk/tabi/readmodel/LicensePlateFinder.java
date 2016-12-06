/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import java.util.List;

import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public interface LicensePlateFinder {
    Observable<Cursor> findPlacesForPlateStart(String plateStart, Integer limit);
}
