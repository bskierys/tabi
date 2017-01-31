/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback.events;

import com.google.auto.value.AutoValue;

@AutoValue public abstract class SuccessfullySentEvent {
    public abstract String response();

    public static SuccessfullySentEvent create(String response){
        return new AutoValue_SuccessfullySentEvent(response);
    }
}
