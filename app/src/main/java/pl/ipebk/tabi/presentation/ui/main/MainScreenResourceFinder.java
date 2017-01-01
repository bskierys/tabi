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
 * Util class that finds main screen resources by reflection. Be sure name your resources properly.
 * See {@link MainListItem} and subclasses to find out about resource proper naming.
 */
public class MainScreenResourceFinder {
    private Context context;
    private String packageName;

    public MainScreenResourceFinder(Context context) {
        this.context = context;
        packageName = context.getPackageName();
    }

    public String getStringResourceForKey(String key) throws Resources.NotFoundException {
        int resourceId = context.getResources().getIdentifier(key, "string", packageName);
        return context.getString(resourceId);
    }

    public Drawable getDrawableResourceForKey(String key) throws Resources.NotFoundException {
        int resourceId = context.getResources().getIdentifier(key, "drawable", packageName);
        return context.getResources().getDrawable(resourceId);
    }
}
