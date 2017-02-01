package pl.ipebk.tabi.feedback;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.suredigit.inappfeedback.FeedBackItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.ipebk.tabi.feedback.events.ReplyFromDeveloperEvent;
import pl.ipebk.tabi.feedback.events.SendingPostponedEvent;
import pl.ipebk.tabi.feedback.events.SuccessfullySentEvent;
import pl.ipebk.tabi.utils.RxEventBus;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/*
* TODO: Generic description. Replace with real one.
*/
@Singleton
public class FeedbackClient {
    private static final String LIBRARY_VERSION = "6";
    private String applicationId;

    private ArrayList<FeedBackItem> feedbackItems;
    private String installationId;

    private SharedPreferences sharedPreferences;
    private DeviceInfoProvider deviceInfoProvider;
    private FeedbackRestClient restClient;
    private FeedbackStorage storage;
    private RxEventBus eventBus;

    @Inject public FeedbackClient(DeviceInfoProvider infoProvider, SharedPreferences sharedPreferences,
                                  FeedbackRestClient restClient, FeedbackStorage storage, RxEventBus eventBus) {
        this.deviceInfoProvider = infoProvider;
        this.sharedPreferences = sharedPreferences;
        this.restClient = restClient;
        this.storage = storage;
        this.eventBus = eventBus;
    }

    public void init(@NonNull String appId) {
        // TODO: 2017-01-31 better initialization
        if (appId.equals(applicationId)) {
            return;
        }

        this.feedbackItems = new ArrayList<>();
        this.applicationId = appId;
        this.installationId = deviceInfoProvider.getInstallationId();
        this.loadUnsentItems();
        if (this.feedbackItems.size() > 0) {
            Timber.d("Found pending feedback items");
            this.sendFeedback(null);
        }

        this.getPendingResponses();
    }

    // TODO: 2017-01-31 doc?
    public void sendFeedback(String comment, FeedbackType type) {
        sendFeedback(createFeedBackItem(comment, type));
    }

    private FeedBackItem createFeedBackItem(String comment, FeedbackType type) {
        String currentSeconds = Long.toString(System.currentTimeMillis() / 1000L);
        String model = deviceInfoProvider.getDeviceModel();
        String manufacturer = deviceInfoProvider.getDeviceManufacturer();
        String sdk = String.valueOf(deviceInfoProvider.getSdkVersion());
        String uuid = this.installationId;
        String versionName = deviceInfoProvider.getAppVersionName();
        String versionCode = Integer.toString(deviceInfoProvider.getAppVersionCode());

        return new FeedBackItem(comment, type.toString(), currentSeconds, model,
                                manufacturer, sdk, deviceInfoProvider.getPackageName(),
                                uuid, LIBRARY_VERSION, versionName, versionCode, "");
    }

    protected void sendFeedback(FeedBackItem fi) {
        String submitJson = getJsonForFeedbackItem(fi);
        Timber.d("Sending feedback: %s", submitJson);
        restClient.postFeedback(submitJson)
                  .subscribeOn(Schedulers.io())
                  .subscribe(response -> {
                      eventBus.post(SuccessfullySentEvent.create(response));
                      Timber.d("Successful feedback. Message from server: %s", response);
                      feedbackItems.clear();
                      saveUnsentItems();
                  }, error -> {
                      eventBus.post(new SendingPostponedEvent());
                      Timber.e(error, "Unable to send feedback. Error: %s", error.getMessage());
                      feedbackItems.add(fi);
                      saveUnsentItems();
                  });
    }

    @NonNull private String getJsonForFeedbackItem(FeedBackItem fi) {
        JSONObject submitJson = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            if (fi != null) {
                list.put(fi.toJson());
            }

            if (feedbackItems.size() > 0) {
                Collections.reverse(feedbackItems);
            }

            Iterator response = feedbackItems.iterator();

            while (response.hasNext()) {
                FeedBackItem e = (FeedBackItem) response.next();
                list.put(e.toJson());
            }

            submitJson.put("APPUID", applicationId);
            submitJson.put("feedback", list);
        } catch (JSONException var22) {
            var22.printStackTrace();
        }
        return submitJson.toString();
    }

    protected void getPendingResponses() {
        restClient.getPendingReplies(installationId)
                  .subscribeOn(Schedulers.io())
                  .subscribe(result -> {
                      Timber.d("Message from developer: %s", result);
                      eventBus.post(ReplyFromDeveloperEvent.create(result));
                  }, error -> {
                      Timber.w(error, "Could not get pending messages from server");
                  });
    }

    private boolean saveUnsentItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("com.suredigit.feedbackdialog.pending_size", this.feedbackItems.size());

        int i;
        for (i = 0; i < 20; ++i) {
            editor.remove("com.suredigit.feedbackdialog.pending_item_" + i);
        }

        if (this.feedbackItems.size() > 0) {
            for (i = 0; i < this.feedbackItems.size(); ++i) {
                editor.putString("com.suredigit.feedbackdialog.pending_item_" + i, ((FeedBackItem) this.feedbackItems.get(i)).toString());
            }
        }

        return editor.commit();
    }

    private void loadUnsentItems() {
        this.feedbackItems.clear();
        int size = sharedPreferences.getInt("com.suredigit.feedbackdialog.pending_size", 0);

        for (int i = 0; i < size; ++i) {
            String jsonTxt = sharedPreferences.getString("com.suredigit.feedbackdialog.pending_item_" + i, null);
            if (jsonTxt != null) {
                JSONObject json = null;

                try {
                    json = new JSONObject(jsonTxt);
                } catch (JSONException var9) {
                    var9.printStackTrace();
                }

                try {
                    if (json != null) {
                        this.feedbackItems.add(new FeedBackItem(json.get("comment").toString(), json.get("type").toString(), json.get("ts").toString(), json.get
                                ("model").toString(), json.get("manufacturer").toString(), json.get("sdk").toString(), json.get("pname").toString(), json.get("UUID")
                                                                                                                                                         .toString(),
                                                                json.get("libver").toString(), json.get("versionname").toString(), json.get("versioncode").toString(),
                                                                json.get("custommessage").toString()));
                    }
                } catch (JSONException var8) {
                    var8.printStackTrace();
                }
            }
        }
    }
}
