/*
* author: Bartlomiej Kierys
* date: 2017-02-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Factory class to produce and hold instance of {@link FeedbackClient} class. Be sure to call {@link #init(Context, String)} before any other method.
 */
public class Feedback {
    private static final String PREF_NAME = "com.suredigit.inappfeedback";
    private static FeedbackClient instance;

    /**
     * Initializes feedback client. Use application Id from the webpage to instantiate item
     *
     * @param context Instance of {@link Context} to plant client
     * @param apiKey Application key registered in webpage to associate your account with application
     */
    public static void init(Context context, String apiKey) {
        if (instance == null) {
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(FeedbackTypeAdapterFactory.create()).create();
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            FeedbackStorage storage = new FeedbackStorage(sharedPreferences, gson);
            DeviceInfoProvider infoProvider = new DeviceInfoProvider(context);
            FeedbackRestClient restClient = new FeedbackRestClient();
            instance = new FeedbackClient(infoProvider, restClient, storage, gson);
        }

        instance.init(apiKey);
    }

    /**
     * @return Instance of {@link FeedbackClient}. Be sure to call {@link #init(Context, String)} first. Will throw {@link RuntimeException} if not initialised
     */
    public static FeedbackClient getClient() {
        if (instance == null) {
            throw new RuntimeException("Feedback was not initialised. Could not get client");
        }
        return instance;
    }
}
