/*
* author: Bartlomiej Kierys
* date: 2016-12-19
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.utils;

import android.content.Context;

import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.place.Place;

public class TestPlaceLocalizationHelper extends PlaceLocalizationHelper {
    public static final String VOIVODESHIP_MOCK_NAME = "voivo";
    public static final String POWIAT_MOCK_NAME = "powiat";
    public static final String GMINA_MOCK_NAME = "gmina";

    public TestPlaceLocalizationHelper(Context context) {
        super(context);
    }

    @Override public String formatVoivodeship(String voivodeship) {
        return VOIVODESHIP_MOCK_NAME + " " + voivodeship;
    }

    @Override public String formatPowiat(String powiat) {
        return POWIAT_MOCK_NAME + " " + powiat;
    }

    @Override public String formatGmina(String gmina) {
        return GMINA_MOCK_NAME + " " + gmina;
    }

    @Override public String formatAdditionalInfo(Place place, String searchedPlate) {
        String outcome = "";
        if (place != null) {
            outcome += place.getName();
        }
        outcome += " " + searchedPlate;

        return outcome;
    }
}
