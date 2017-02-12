/*
* author: Bartlomiej Kierys
* date: 2017-02-05
* email: bskierys@gmail.com
*/
package com.suredigit.inappfeedback;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

@GsonTypeAdapterFactory
public abstract class FeedbackTypeAdapterFactory implements TypeAdapterFactory {
    public static FeedbackTypeAdapterFactory create() {
        return new AutoValueGson_FeedbackTypeAdapterFactory();
    }
}
