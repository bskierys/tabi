/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback.events;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ReplyFromDeveloperEvent {
    public abstract String message();

    public static ReplyFromDeveloperEvent create(String message) {
        return new AutoValue_ReplyFromDeveloperEvent(message);
    }
}
