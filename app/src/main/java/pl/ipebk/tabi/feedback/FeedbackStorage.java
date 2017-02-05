/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Class to support storing feedback that could not be sent instantly. It is based on {@link SharedPreferences}
 */
public class FeedbackStorage {
    private static final String PREF_UNSENT_ITEMS_COUNT = "com.suredigit.feedbackdialog.pending_size";
    private static final String PREF_UNSENT_ITEM_ELEMENT = "com.suredigit.feedbackdialog.pending_item_";
    private static final int MAX_ITEMS_COUNT = 20;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    /**
     * Constructor for {@link FeedbackStorage} class
     * @param sharedPreferences Instance of {@link SharedPreferences} to store data
     * @param gson Instance of {@link Gson} to serialize objects
     */
    @Inject public FeedbackStorage(SharedPreferences sharedPreferences, Gson gson) {
        this.sharedPreferences = sharedPreferences;
        this.gson = gson;
    }

    /**
     * @return Number of items that are currently stored in storage
     */
    public int getUnsentItemsCount() {
        return sharedPreferences.getInt(PREF_UNSENT_ITEMS_COUNT, 0);
    }

    /**
     * @return List of items stored in storage
     */
    public List<FeedbackItem> getUnsentItems() {
        int size = getUnsentItemsCount();
        if (size < 0) {
            return new ArrayList<>();
        } else {
            List<FeedbackItem> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String jsonTxt = sharedPreferences.getString(getElementPrefKey(i), null);
                FeedbackItem item = gson.fromJson(jsonTxt, FeedbackItem.class);
                if (item != null) {
                    items.add(item);
                }
            }
            return items;
        }
    }

    private String getElementPrefKey(int i) {
        return PREF_UNSENT_ITEM_ELEMENT + Integer.toString(i);
    }

    /**
     * Adds new items to that stored already in storage. Max number of stored items is 20.
     */
    public void putUnsentItems(List<FeedbackItem> items) {
        List<FeedbackItem> itemsStored = getUnsentItems();
        itemsStored.addAll(items);
        if (itemsStored.size() > MAX_ITEMS_COUNT) {
            int difference = itemsStored.size() - MAX_ITEMS_COUNT;
            for (int i = 0; i < difference; i++) {
                itemsStored.remove(0);
            }
        }
        clearUnsentItems();
        saveAll(itemsStored);
    }

    private void saveAll(List<FeedbackItem> items) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < items.size(); i++) {
            editor.putString(getElementPrefKey(i), gson.toJson(items.get(i)));
        }

        editor.putInt(PREF_UNSENT_ITEMS_COUNT, items.size());
        editor.apply();
    }

    /**
     * Deletes all unsent items.
     */
    public void clearUnsentItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < MAX_ITEMS_COUNT; i++) {
            editor.remove(getElementPrefKey(i));
        }

        editor.putInt(PREF_UNSENT_ITEMS_COUNT, 0);
        editor.apply();
    }
}
