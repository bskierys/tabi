/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import rx.Observable;

/**
 * <p>Finder that handles searching for places by start of it's name. Rules of finding places:</p> <ul> <li>special
 * places should not be displayed here</li> <li>all other places can e searched. regardless of size</li> <li>size of the
 * city has priority over gramatical correctness</li> <li>ordering: <ul> <li>having a plate</li> <li>gramatical
 * correctness - if you type diacritics then places with diacritics first, if you type without diacritics then places
 * without diacritics first</li> <li>two-letter plates before three-letters</li> <li>alphabetically by place name</li>
 * </ul></li> </ul>
 */
public interface PlaceFinder {
    Observable<Cursor> findPlacesByName(String nameStart, Integer limit);
}
