/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.localization;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import pl.ipebk.tabi.R;
import timber.log.Timber;

/**
 * Helper class that handles text formatting for categories.
 */
public class CategoryLocalizationHelper {
    private final static String CATEGORY_NAME_RESOURCE_KEY = "main_list_element_";
    private final static String CATEGORY_PLATE_RESOURCE_KEY = "voivodeship_plate_";
    private final static String CATEGORY_TITLE_RESOURCE_KEY = "category_title_";
    private final static String CATEGORY_BODY_RESOURCE_KEY = "category_body_";
    private final static String CATEGORY_LINK_RESOURCE_KEY = "category_link_";

    private Context context;

    public CategoryLocalizationHelper(Context context) {
        this.context = context;
    }

    public String formatCategory(String key) {
        String resourceName = CATEGORY_NAME_RESOURCE_KEY + key;
        String categoryName;
        try {
            categoryName = getStringResourceForKey(resourceName);
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Could not found resource for name: %s", resourceName);
            categoryName = context.getString(R.string.default_resource_string);
        }
        return categoryName;
    }

    public String getCategoryPlate(String key) {
        String resourceName = CATEGORY_PLATE_RESOURCE_KEY + key;
        return getStringResourceForKey(resourceName).toUpperCase();
    }

    public String getCategoryTitle(String key) {
        String resourceName = CATEGORY_TITLE_RESOURCE_KEY + key;
        return getStringResourceForKey(resourceName);
    }

    public String getCategoryBody(String key) {
        String resourceName = CATEGORY_BODY_RESOURCE_KEY + key;
        return getStringResourceForKey(resourceName);
    }

    public String getCategoryLink(String key) {
        String resourceName = CATEGORY_LINK_RESOURCE_KEY + key;
        return getStringResourceForKey(resourceName);
    }

    private String getStringResourceForKey(String key) throws Resources.NotFoundException {
        int resourceId = context.getResources().getIdentifier(key, "string", context.getPackageName());
        return context.getString(resourceId);
    }

    public Drawable getCategoryIcon(String key) {
        String resourceName = "vic_" + key;
        Drawable categoryIcon;
        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            categoryIcon = context.getResources().getDrawable(resourceId);
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Could not found resource for name: %s", resourceName);
            categoryIcon = context.getResources().getDrawable(R.drawable.vic_default);
        }
        return categoryIcon;
    }
}
