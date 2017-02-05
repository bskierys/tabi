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

@AutoValue
public abstract class FeedbackItem {
    abstract String comment();
    abstract String type();
    abstract String ts();
    @SerializedName("model") abstract String deviceModel();
    @SerializedName("manufacturer") abstract String deviceManufacturer();
    abstract String sdk();
    @SerializedName("pname") abstract String packageName();
    @SerializedName("UUID") abstract String installId();
    @SerializedName("libver") abstract String libraryVersion();
    @SerializedName("versionname") abstract String versionName();
    @SerializedName("versioncode") abstract String versionCode();
    @SerializedName("custommessage") abstract String customMessage();

    public static FeedbackItem create(String comment, String type, String ts, String model, String manufact,
                                      String sdk, String pname, String UUID, String LIBVER, String versionName,
                                      String versionCode, String customMesssage) {
        return new AutoValue_FeedbackItem(comment, type, ts, model, manufact, sdk, pname, UUID, LIBVER,
                                          versionName, versionCode, customMesssage);
    }

    public static TypeAdapter<FeedbackItem> typeAdapter(Gson gson) {
        return new AutoValue_FeedbackItem.GsonTypeAdapter(gson);
    }
}
