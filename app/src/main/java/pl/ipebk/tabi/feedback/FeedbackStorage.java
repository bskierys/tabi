/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import android.content.SharedPreferences;

import com.suredigit.inappfeedback.FeedBackItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * TODO: Generic description. Replace with real one.
 */
public class FeedbackStorage {
    private static final String PREF_UNSENT_ITEMS_COUNT = "com.suredigit.feedbackdialog.pending_size";
    private static final String PREF_UNSENT_ITEM_ELEMENT = "com.suredigit.feedbackdialog.pending_item_";
    private static final int MAX_ITEMS_COUNT = 20;

    private SharedPreferences sharedPreferences;

    @Inject public FeedbackStorage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public int getUnsentItemsCount() {
        return sharedPreferences.getInt(PREF_UNSENT_ITEMS_COUNT, 0);
    }

    public List<FeedBackItem> getUnsentItems() {
        int size = getUnsentItemsCount();
        if (size < 0) {
            return new ArrayList<>();
        } else {
            List<FeedBackItem> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String jsonTxt = sharedPreferences.getString(getElementPrefKey(i), null);
                FeedBackItem item = parseJsonToItem(jsonTxt);
                if(item!=null) {
                    items.add(item);
                }
            }
            return items;
        }
    }

    private FeedBackItem parseJsonToItem(String jsonTxt) {
        if (jsonTxt != null) {
            JSONObject json = null;

            try {
                json = new JSONObject(jsonTxt);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            try {
                if (json != null) {
                    return new FeedBackItem(json.get("comment").toString(),
                                            json.get("type").toString(),
                                            json.get("ts").toString(),
                                            json.get("model").toString(),
                                            json.get("manufacturer").toString(),
                                            json.get("sdk").toString(),
                                            json.get("pname").toString(),
                                            json.get("UUID").toString(),
                                            json.get("libver").toString(),
                                            json.get("versionname").toString(),
                                            json.get("versioncode").toString(),
                                            json.get("custommessage").toString());
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private String getElementPrefKey(int i) {
        return PREF_UNSENT_ITEM_ELEMENT + Integer.toString(i);
    }

    public void putUnsentItems(List<FeedBackItem> items) {
        List<FeedBackItem> itemsStored = getUnsentItems();
        itemsStored.addAll(items);
        if(itemsStored.size() > MAX_ITEMS_COUNT) {
            int difference = MAX_ITEMS_COUNT - itemsStored.size();
            for(int i = 0; i< difference; i++) {
                itemsStored.remove(0);
            }
        }
        saveAll(itemsStored);
    }

    private void saveAll(List<FeedBackItem> items) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0; i< items.size(); i++) {
            editor.putString(getElementPrefKey(i), items.get(i).toString());
        }
        editor.apply();
    }

    public void clearUnsentItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0; i< MAX_ITEMS_COUNT; i++) {
            editor.remove(getElementPrefKey(i));
        }
        editor.apply();
    }
}
