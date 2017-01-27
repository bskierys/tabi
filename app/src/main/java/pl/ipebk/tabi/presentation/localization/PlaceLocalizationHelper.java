/*
* author: Bartlomiej Kierys
* date: 2016-12-19
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.localization;

import android.content.Context;
import android.content.res.Resources;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.readmodel.PlaceType;
import timber.log.Timber;

/**
 * Helper class that handles text formatting for places.
 */
public class PlaceLocalizationHelper {
    private final static String POWIAT_REPLACE_FORMAT = "$z$";
    private final static String VOIVODESHIP_NAME_RESOURCE_KEY = "voivodeship_name_";
    private final static String CATEGORY_NAME_RESOURCE_KEY = "main_list_element_";
    private final static String CATEGORY_PLATE_RESOURCE_KEY = "voivodeship_plate_";
    private final static String CATEGORY_TITLE_RESOURCE_KEY = "category_title_";
    private final static String CATEGORY_BODY_RESOURCE_KEY = "category_body_";
    private final static String CATEGORY_LINK_RESOURCE_KEY = "category_link_";

    private Context context;

    public PlaceLocalizationHelper(Context context) {
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

    public String formatVoivodeship(String voivodeship) {
        String resourceName = VOIVODESHIP_NAME_RESOURCE_KEY + voivodeship.replace("#", "");
        String voivodeshipName;
        try {
            voivodeshipName = getStringResourceForKey(resourceName);
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Could not found resource for name: %s", resourceName);
            voivodeshipName = context.getString(R.string.default_resource_string);
        }
        return voivodeshipName;
    }

    private String getStringResourceForKey(String key) throws Resources.NotFoundException {
        int resourceId = context.getResources().getIdentifier(key, "string", context.getPackageName());
        return context.getString(resourceId);
    }

    public String formatPowiat(String powiat) {
        String zReplacement = context.getString(R.string.details_powiat_territorial);
        String fullName;
        if (powiat.contains(POWIAT_REPLACE_FORMAT)) {
            powiat = powiat.replace(POWIAT_REPLACE_FORMAT, "").trim();
            fullName = context.getString(R.string.details_powiat, powiat, zReplacement);
        } else {
            fullName = context.getString(R.string.details_powiat, powiat, "");
        }
        return fullName.trim().replaceAll("\\s+", " ");
    }

    public String formatGmina(String gmina) {
        return context.getString(R.string.details_gmina, gmina);
    }

    /**
     * Formats additional info: 'type of place, list of plates'
     *
     * @param place place info to show
     * @param searchedPlate needed to exclude it from list of plates
     * @return formatted info or empty string if place is null
     */
    public String formatAdditionalInfo(Place place, String searchedPlate) {
        if (place == null) {
            return "";
        }

        String placeType = "";
        if (place.getType().ordinal() < PlaceType.PART_OF_TOWN.ordinal()) {
            placeType = context.getString(R.string.details_additional_town);
        } else if (place.getType() == PlaceType.PART_OF_TOWN) {
            placeType = context.getString(R.string.details_additional_part_of_town, place.getGmina());
        } else if (place.getType() == PlaceType.VILLAGE) {
            placeType = context.getString(R.string.details_additional_village);
        }

        String otherPlates = "";
        if (place.hasAdditionalPlates()) {
            otherPlates = ", " + context.getString(R.string.details_additional_other_plates)
                    + place.platesToStringExceptMatchingPattern(searchedPlate);
        }

        return placeType + otherPlates;
    }

    /**
     * Formats place data in readable way. Depends on localized names of regions.
     *
     * @param place Instance of {@link Place} to format.
     */
    public String formatPlaceInfo(Place place) {
        StringBuilder builder = new StringBuilder();
        builder.append(place.getName());
        builder.append(", ");
        builder.append(formatVoivodeship(place.getVoivodeship()));
        builder.append(", ");
        builder.append(formatPowiat(place.getPowiat()));
        builder.append(", ");
        builder.append(formatGmina(place.getGmina()));
        builder.append(", ");
        builder.append(context.getString(R.string.details_country));
        return builder.toString();
    }

    /**
     * Formats place data into format that is understandable by search engines.
     */
    public String formatPlaceToSearch(Place place) {
        return place + "," + context.getString(R.string.details_country);
    }
}
