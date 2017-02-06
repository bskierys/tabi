/*
* author: Bartlomiej Kierys
* date: 2017-02-05
* email: bskierys@gmail.com
*/
package com.suredigit.inappfeedback;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class FeedbackItem {
    abstract String comment();
    abstract FeedbackType type();
    @SerializedName("ts")abstract long timeCreated();
    @SerializedName("model") abstract String deviceModel();
    @SerializedName("manufacturer") abstract String deviceManufacturer();
    @SerializedName("sdk") abstract int sdkVersion();
    @SerializedName("pname") abstract String packageName();
    @SerializedName("UUID") abstract String installId();
    @SerializedName("libver") abstract String libraryVersion();
    @SerializedName("versionname") abstract String versionName();
    @SerializedName("versioncode") abstract int versionCode();
    @SerializedName("custommessage") abstract String customMessage();

    public static FeedbackItem create(String comment, FeedbackType type, long time, String model, String manufact,
                                      int sdkVersion, String packageName, String UUID, String libVersion, String versionName,
                                      int versionCode, String customMessage) {
        return new AutoValue_FeedbackItem(comment, type, time, model, manufact, sdkVersion, packageName, UUID, libVersion,
                                          versionName, versionCode, customMessage);
    }

    public static TypeAdapter<FeedbackItem> typeAdapter(Gson gson) {
        return new AutoValue_FeedbackItem.GsonTypeAdapter(gson);
    }
}
