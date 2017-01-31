package pl.ipebk.tabi.feedback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RadioButton;

import com.suredigit.inappfeedback.FeedBackItem;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.ipebk.tabi.feedback.events.SuccessfullySentEvent;
import pl.ipebk.tabi.utils.RxEventBus;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/*
* TODO: Generic description. Replace with real one.
*/
@Singleton
public class FeedbackClient {
    public static FeedbackDialog.LogTypes LOGT;
    private static final String APIVER = "2";
    private static final String LIBVER = "6";
    private static final String POSTURL = "http://www.android-feedback.com/service/2";
    private static final String REPLIESURL = "http://www.android-feedback.com/service/2/getPending/";
    private static final String PREFS_NAME = "inappfeedback_prefs";
    private static final int MAX_PENDING_ITEMS = 20;
    private String APPUID;
    private DeviceInfoProvider deviceInfoProvider;
    private ArrayList<FeedBackItem> mFeedBackItems;
    private String UUID;

    private SharedPreferences sharedPreferences;
    private FeedbackRestClient restClient;
    private RxEventBus eventBus;

    @Inject public FeedbackClient(DeviceInfoProvider infoProvider, SharedPreferences sharedPreferences,
                                  FeedbackRestClient restClient, RxEventBus eventBus) {
        this.deviceInfoProvider = infoProvider;
        this.sharedPreferences = sharedPreferences;
        this.restClient = restClient;
        this.eventBus = eventBus;
    }

    public void initialise(@NonNull String appId) {
        // TODO: 2017-01-31 better initialization
        if(appId.equals(APPUID)) {
            return;
        }

        this.mFeedBackItems = new ArrayList();
        this.APPUID = appId;
        this.UUID = deviceInfoProvider.getInstallationId();
        this.loadUnsentItems();
        if (this.mFeedBackItems.size() > 0) {
            Timber.d("Found pending feedback items");
            this.sendFeedback((FeedBackItem) null);
        }

        //this.getPendingResponses();
    }

    // TODO: 2017-01-31 doc?
    public void sendFeedback(String comment, String type) {
        sendFeedback(createFeedBackItem(comment, type));
    }

    private FeedBackItem createFeedBackItem(String comment, String type) {
        String currentSeconds = Long.toString(System.currentTimeMillis() / 1000L);
        String model = deviceInfoProvider.getDeviceModel();
        String manufacturer = deviceInfoProvider.getDeviceManufacturer();
        String sdk = String.valueOf(deviceInfoProvider.getSdkVersion());
        String uuid = this.UUID;
        String versionName = deviceInfoProvider.getAppVersionName();
        String versionCode = Integer.toString(deviceInfoProvider.getAppVersionCode());

        FeedBackItem fItem = new FeedBackItem(comment, type, currentSeconds, model, manufacturer, sdk,
                                              deviceInfoProvider.getPackageName(), uuid, "6", versionName, versionCode,
                                              "custom message");
        return fItem;
    }

    protected void sendFeedback(FeedBackItem fi) {
        String submitJson = getJsonForFeedbackItem(fi);
        Timber.d("Sending feedback: %s", submitJson);
        restClient.postFeedback(submitJson)
                  .subscribeOn(Schedulers.io())
                  .subscribe(response -> {
            eventBus.post(SuccessfullySentEvent.create(response));
            Timber.d("Successful feedback. Message from server: %s", response);
            mFeedBackItems.clear();
            saveUnsentItems();
        }, error -> {
            Timber.e(error, "Unable to send feedback. Error: %s", error.getMessage());
            mFeedBackItems.add(fi);
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

            if (mFeedBackItems.size() > 0) {
                Collections.reverse(mFeedBackItems);
            }

            Iterator response = mFeedBackItems.iterator();

            while (response.hasNext()) {
                FeedBackItem e = (FeedBackItem) response.next();
                list.put(e.toJson());
            }

            submitJson.put("APPUID", FeedbackClient.this.APPUID);
            submitJson.put("feedback", list);
        } catch (JSONException var22) {
            var22.printStackTrace();
        }
        return submitJson.toString();
    }

    /*protected void getPendingResponses() {
        Thread thread = new Thread() {
            public void run() {
                HttpGet httpget = new HttpGet("http://www.android-feedback.com/service/2/getPending/" + FeedbackDialog.this.UUID);
                if (FeedbackDialog.LOGT == FeedbackDialog.LogTypes.DEBUG) {
                    Log.d("FeedbackDialog", httpget.getURI().toString());
                }

                BasicHttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = FeedbackDialog.CONN_TIMEOUT;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = FeedbackDialog.CONN_TIMEOUT;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
                boolean success = false;
                String result = "";

                try {
                    HttpResponse e = httpclient.execute(httpget);
                    HttpEntity entity = e.getEntity();
                    result = EntityUtils.toString(entity, "UTF-8");
                    if (FeedbackDialog.LOGT == FeedbackDialog.LogTypes.DEBUG) {
                        Log.d("FeedbackDialog", result);
                    }

                    int code = e.getStatusLine().getStatusCode();
                    if (code == 200) {
                        success = true;
                    }
                } catch (ClientProtocolException var18) {
                    var18.printStackTrace();
                    success = false;
                } catch (IOException var19) {
                    var19.printStackTrace();
                    success = false;
                } finally {
                    if (success && !result.equalsIgnoreCase("")) {
                        final String resultInterim = result.toString();
                        final boolean rate = resultInterim.contains("[RATE]");
                        if (rate) {
                            resultInterim = resultInterim.replace("[RATE]", "");
                        }

                        FeedbackDialog.this.mActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackDialog.this.mActivity);
                                builder.setTitle(FeedbackDialog.this.mSettings.getReplyTitle()).setMessage(resultInterim).setCancelable(false).setNegativeButton
                                        (FeedbackDialog.this.mSettings.getReplyCloseButtonText(), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                                if (rate) {
                                    builder.setPositiveButton(FeedbackDialog.this.mSettings.getReplyRateButtonText(), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            FeedbackDialog.this.openGooglePlay();
                                        }
                                    });
                                }

                                FeedbackDialog.this.mResponseDialog = builder.create();
                                FeedbackDialog.this.mResponseDialog.show();
                            }
                        });
                    }
                }
            }
        };
        thread.start();
    }*/

    private boolean saveUnsentItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("com.suredigit.feedbackdialog.pending_size", this.mFeedBackItems.size());

        int i;
        for (i = 0; i < 20; ++i) {
            editor.remove("com.suredigit.feedbackdialog.pending_item_" + i);
        }

        if (this.mFeedBackItems.size() > 0) {
            for (i = 0; i < this.mFeedBackItems.size(); ++i) {
                editor.putString("com.suredigit.feedbackdialog.pending_item_" + i, ((FeedBackItem) this.mFeedBackItems.get(i)).toString());
            }
        }

        return editor.commit();
    }

    private void loadUnsentItems() {
        this.mFeedBackItems.clear();
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
                        this.mFeedBackItems.add(new FeedBackItem(json.get("comment").toString(), json.get("type").toString(), json.get("ts").toString(), json.get
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
