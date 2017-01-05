/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import rx.Observable;

/**
 * <p>Finder that handles searching for places start of one of its plates. Rules of finding places:</p> <ul> <li>places
 * can be searched by it's main or additional plate</li> <li>only places that has their own plates will be displayed
 * (ex. powiat city)</li> <li>ordering: <ul> <li>larger cities first</li> <li>two-letter plates before
 * three-letters</li> <li>alphabetically by searched plate</li> <li>alphabetically by searched plate's end</li>
 * </ul></li> </ul>
 */
public interface LicensePlateFinder {
    Observable<Cursor> findPlacesForPlateStart(String plateStart, Integer limit);
}
