package com.suredigit.inappfeedback;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Class that handles sending feedback and receiving answers from the web.
 */
public class FeedbackClient {
    private static final String LIBRARY_VERSION = "6";
    private String applicationId;

    private List<FeedbackItem> feedbackItems;
    private String installationId;

    private DeviceInfoProvider deviceInfoProvider;
    private FeedbackRestClient restClient;
    private FeedbackStorage storage;
    private Gson gson;

    private FeedbackClient() {}

    /**
     * Constructor for {@link FeedbackClient}. Be sure to call {@link #init(String)} before any other method.
     */
    FeedbackClient(DeviceInfoProvider infoProvider, FeedbackRestClient restClient,
                   FeedbackStorage storage, Gson gson) {
        this.deviceInfoProvider = infoProvider;
        this.restClient = restClient;
        this.storage = storage;
        this.gson = gson;
    }

    /**
     * Initialises client for given application
     *
     * @param appId Application id from web page
     */
    void init(@NonNull String appId) {
        if (!appId.equals(applicationId)) {
            this.feedbackItems = new ArrayList<>();
            this.applicationId = appId;
            this.installationId = deviceInfoProvider.getInstallationId();
            this.loadUnsentItems();
        }
    }

    /**
     * Current version of library does not send unsent feedback by default. Use this to invoke this method when needed.
     *
     * @return Observable that completes when sending is completed
     */
    public Observable<Void> sendUnsentFeedback() {
        checkNotNull(applicationId, "Feedback client was not initialised");
        this.loadUnsentItems();
        if (this.feedbackItems.size() > 0) {
            Timber.d("Found pending feedback items");
            return this.sendFeedback(null);
        } else {
            return Observable.just(null);
        }
    }

    /**
     * Sends feedback of given type to server.
     *
     * @param comment Comment given by the user
     * @param type Type of given feedback
     * @return Observable emitting onComplete when feedback is sent
     */
    public Observable<Void> sendFeedback(String comment, FeedbackType type) {
        checkNotNull(applicationId, "Feedback client was not initialised");
        return sendFeedback(createFeedBackItem(comment, type));
    }

    private FeedbackItem createFeedBackItem(String comment, FeedbackType type) {
        long currentSeconds = System.currentTimeMillis() / 1000L;
        String model = deviceInfoProvider.getDeviceModel();
        String manufacturer = deviceInfoProvider.getDeviceManufacturer();
        int sdkVersion = deviceInfoProvider.getSdkVersion();
        String uuid = this.installationId;
        String versionName = deviceInfoProvider.getAppVersionName();
        int versionCode = deviceInfoProvider.getAppVersionCode();

        return FeedbackItem.create(comment, type, currentSeconds, model,
                                   manufacturer, sdkVersion, deviceInfoProvider.getPackageName(),
                                   uuid, LIBRARY_VERSION, versionName, versionCode, "");
    }

    private Observable<Void> sendFeedback(FeedbackItem fi) {
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

    /**
     * Current version of library does not get responses automatically. Use this to invoke this method when needed.
     *
     * @return Observable that emits received feedback
     */
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

    private void checkNotNull(Object o, String message) {
        if (o == null) {
            throw new FeedbackClientException(message);
        }
    }

    public static class FeedbackClientException extends RuntimeException {
        FeedbackClientException(String message) {
            super(message);
        }
    }
}
