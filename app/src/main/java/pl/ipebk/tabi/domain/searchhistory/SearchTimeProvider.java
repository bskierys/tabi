/*
* author: Bartlomiej Kierys
* date: 2016-12-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.searchhistory;

import java.util.Date;

/**
 * Provides current time. Should be injected in app, but mocked in tests
 */
public interface SearchTimeProvider {
    Date now();
}
