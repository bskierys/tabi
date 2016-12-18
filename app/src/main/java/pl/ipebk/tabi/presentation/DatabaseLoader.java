/*
* author: Bartlomiej Kierys
* date: 2016-12-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation;

import rx.Observable;

/**
 * Class that is responsible for preparing database
 */
public interface DatabaseLoader {
    /**
     * Load database and return observable for event when it's done
     */
    Observable<Void> initDatabase();
}
