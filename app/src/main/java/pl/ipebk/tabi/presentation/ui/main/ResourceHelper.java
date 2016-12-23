/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import pl.ipebk.tabi.R;
import timber.log.Timber;

/**
 * TODO: Generic description. Replace with real one.
 */
public class ResourceHelper {

    private Context context;
    private String packageName;

    public ResourceHelper(Context context) {
        this.context = context;
        packageName = context.getPackageName();
    }

    // TODO: 2016-05-26 move to another helper
    public String getStringResourceForKey(String key) throws Resources.NotFoundException {
        // TODO: 2016-05-26 move to constants
        try {
            int resourceId = context.getResources().getIdentifier(key, "string", packageName);
            return context.getString(resourceId);
        } catch (Exception e) {
            Timber.e("Failed to find string for name: %s", key);
            return context.getString(R.string.default_resource_string);
        }
    }

    public Drawable getDrawableResourceForKey(String key) throws Resources.NotFoundException {
        // TODO: 2016-05-26 move to constants
        try {
            int resourceId = context.getResources().getIdentifier(key, "drawable", packageName);
            return context.getResources().getDrawable(resourceId);
        } catch (Resources.NotFoundException e) {
            Timber.e("Failed to find drawable for name: %s", key);
            return context.getResources().getDrawable(R.drawable.default_resource_drawable);
        }
    }
}
