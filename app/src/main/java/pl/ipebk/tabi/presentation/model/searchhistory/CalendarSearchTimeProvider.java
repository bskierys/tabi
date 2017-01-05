/*
* author: Bartlomiej Kierys
* date: 2016-12-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.searchhistory;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Implementation of {@link SearchTimeProvider} that uses {@link Calendar} to acquire current time.
 */
public class CalendarSearchTimeProvider implements SearchTimeProvider {
    @Inject public CalendarSearchTimeProvider() {}

    @Override public Date now() {
        return Calendar.getInstance().getTime();
    }
}
