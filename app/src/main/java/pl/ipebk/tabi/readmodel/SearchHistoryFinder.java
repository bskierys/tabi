/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import rx.Observable;

/**
 * <p>Finder that handles searching for places that were recently put into search history. Rules of finding places:</p>
 * <ul> <li>if we search for the same place, it is updated not added. So if we search for same place with different
 * plate system will forget that we searched by the other plate first</li> <li>search history for places and license
 * plates section are stored separately</li> <li>random place/license plate is always the last one so if you want to
 * limit 3 places, you get 2 historical and one random</li> <li>random plate is always picked from places that has own
 * plate, and are not special</li> <li>random place is always picked from places that are not special</li> </ul>
 */
public interface SearchHistoryFinder {
    Observable<Cursor> findHistoryPlaces(Integer limit, SearchType type);
}
