/*
* author: Bartlomiej Kierys
* date: 2017-02-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@AutoValue
public abstract class FeedbackSubmit {
    @SerializedName("APPUID") abstract String applicationId();
    @SerializedName("feedback") abstract List<FeedbackItem> feedbackItems();

    public static FeedbackSubmit create(String appId, List<FeedbackItem> items) {
        return new AutoValue_FeedbackSubmit(appId, items);
    }

    public static TypeAdapter<FeedbackSubmit> typeAdapter(Gson gson) {
        return new AutoValue_FeedbackSubmit.GsonTypeAdapter(gson);
    }
}
