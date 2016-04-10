/*
* author: Bartlomiej Kierys
* date: 2016-04-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;

/**
 * Helper class that handles readable text formatting for places.
 */
public class NameFormatHelper {
    private Context context;

    public NameFormatHelper(Context context) {
        this.context = context;
    }

    public String formatVoivodeship(String voivodeship) {
        return context.getString(R.string.details_voivodeship) + " " + voivodeship;
    }

    public String formatPowiat(String powiat) {
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
        if (place.getType().ordinal() < Place.Type.PART_OF_TOWN.ordinal()) {
            placeType = context.getString(R.string.details_additional_town);
        } else if (place.getType() == Place.Type.PART_OF_TOWN) {
            placeType = context.getString(R.string.details_additional_part_of_town) + " " + place.getGmina();
        } else if (place.getType() == Place.Type.VILLAGE) {
            placeType = context.getString(R.string.details_additional_village);
        }

        String otherPlates = "";
        if (place.getPlates().size() > 1) {
            otherPlates = ", " + context.getString(R.string.details_additional_other_plates) + ": "
                    + place.platesToStringExceptMatchingPattern(searchedPlate);
        }

        return placeType + otherPlates;
    }
}
