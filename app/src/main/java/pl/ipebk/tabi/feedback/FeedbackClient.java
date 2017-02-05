package pl.ipebk.tabi.feedback;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/*
* TODO: Generic description. Replace with real one.
*/
@Singleton
public class FeedbackClient {
    private static final String LIBRARY_VERSION = "6";
    private String applicationId;

    private List<FeedbackItem> feedbackItems;
    private String installationId;

    private DeviceInfoProvider deviceInfoProvider;
    private FeedbackRestClient restClient;
    private FeedbackStorage storage;
    private Gson gson;

    @Inject public FeedbackClient(DeviceInfoProvider infoProvider, FeedbackRestClient restClient,
                                  FeedbackStorage storage, Gson gson) {
        this.deviceInfoProvider = infoProvider;
        this.restClient = restClient;
        this.storage = storage;
        this.gson = gson;
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
    }

    public Observable<Void> sendUnsentFeedback() {
        this.loadUnsentItems();
        if (this.feedbackItems.size() > 0) {
            Timber.d("Found pending feedback items");
            return this.sendFeedback(null);
        } else {
            return Observable.just(null);
        }
    }

    // TODO: 2017-01-31 doc?
    public Observable<Void> sendFeedback(String comment, FeedbackType type) {
        return sendFeedback(createFeedBackItem(comment, type));
    }

    private FeedbackItem createFeedBackItem(String comment, FeedbackType type) {
        String currentSeconds = Long.toString(System.currentTimeMillis() / 1000L);
        String model = deviceInfoProvider.getDeviceModel();
        String manufacturer = deviceInfoProvider.getDeviceManufacturer();
        String sdk = String.valueOf(deviceInfoProvider.getSdkVersion());
        String uuid = this.installationId;
        String versionName = deviceInfoProvider.getAppVersionName();
        String versionCode = Integer.toString(deviceInfoProvider.getAppVersionCode());

        return FeedbackItem.create(comment, type.toString(), currentSeconds, model,
                                   manufacturer, sdk, deviceInfoProvider.getPackageName(),
                                   uuid, LIBRARY_VERSION, versionName, versionCode, "");
    }

    protected Observable<Void> sendFeedback(FeedbackItem fi) {
        String submitJson = getJsonForFeedbackItem(fi);
        Timber.d("Sending feedback: %s", submitJson);
        return Observable.create(subscriber -> {
            restClient.postFeedback(submitJson)
                      .subscribeOn(Schedulers.io())
                      .subscribe(response -> {
                          if (response.equals("OK")) {
                              Timber.d("Successful feedback. Message from server: %s", response);
                              feedbackItems.clear();
                              saveUnsentItems();
                              if (!subscriber.isUnsubscribed()) {
                                  subscriber.onNext(null);
                                  subscriber.onCompleted();
                              }
                          } else if (!subscriber.isUnsubscribed()) {
                              subscriber.onError(new RuntimeException("Response from web: " + response));
                          }
                      }, error -> {
                          Timber.e(error, "Unable to send feedback. Error: %s", error.getMessage());
                          feedbackItems.add(fi);
                          saveUnsentItems();
                          if (!subscriber.isUnsubscribed()) {
                              subscriber.onError(error);
                          }
                      });
        });
    }

    @NonNull private String getJsonForFeedbackItem(FeedbackItem fi) {
        List<FeedbackItem> itemsToSend = new ArrayList<>();
        if (fi != null) {
            itemsToSend.add(fi);
        }
        if (feedbackItems.size() > 0) {
            Collections.reverse(feedbackItems);
            itemsToSend.addAll(feedbackItems);
        }

        FeedbackSubmit submit = FeedbackSubmit.create(applicationId, itemsToSend);
        return gson.toJson(submit);
    }

    public Observable<String> getPendingResponses() {
        return restClient.getPendingReplies(installationId)
                         .subscribeOn(Schedulers.io())
                         .filter(result -> result != null && !result.equals(""));
    }

    private void saveUnsentItems() {
        storage.clearUnsentItems();
        storage.putUnsentItems(feedbackItems);
    }

    private void loadUnsentItems() {
        this.feedbackItems.clear();
        this.feedbackItems = storage.getUnsentItems();
    }
}
