/*
* author: Bartlomiej Kierys
* date: 2016-12-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.searchhistory;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * TODO: Generic description. Replace with real one.
 */
public class CalendarSearchTimeProvider implements SearchTimeProvider {
    @Inject public CalendarSearchTimeProvider() {}

    @Override public Date now() {
        return Calendar.getInstance().getTime();
    }
}
