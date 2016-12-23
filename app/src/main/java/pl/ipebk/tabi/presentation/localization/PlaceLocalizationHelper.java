/*
* author: Bartlomiej Kierys
* date: 2016-12-19
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.localization;

import android.content.Context;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Helper class that handles text formatting for places.
 */
public class PlaceLocalizationHelper {
    private final static String POWIAT_REPLACE_FORMAT = "$z$";

    private Context context;

    public PlaceLocalizationHelper(Context context) {
        this.context = context;
    }

    public String formatVoivodeship(String voivodeship) {
        return context.getString(R.string.details_voivodeship) + " " + voivodeship;
    }

    public String formatPowiat(String powiat) {
        String zReplacement = context.getString(R.string.details_powiat_territorial);
        powiat = powiat.replace(POWIAT_REPLACE_FORMAT, zReplacement);
        return context.getString(R.string.details_powiat) + " " + powiat;
    }

    public String formatGmina(String gmina) {
        return context.getString(R.string.details_gmina) + " " + gmina;
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
            placeType = context.getString(R.string.details_additional_part_of_town) + " " + place.getGmina();
        } else if (place.getType() == PlaceType.VILLAGE) {
            placeType = context.getString(R.string.details_additional_village);
        }

        String otherPlates = "";
        if (place.hasAdditionalPlates()) {
            otherPlates = ", " + context.getString(R.string.details_additional_other_plates) + ": "
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
