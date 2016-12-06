/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.database.Cursor;

import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public interface SearchHistoryFinder {
    Observable<Cursor> findHistoryPlaces(Integer limit, SearchType type);
}
