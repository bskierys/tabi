/*
* author: Bartlomiej Kierys
* date: 2016-05-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.utils;

import android.content.Context;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.utils.NameFormatHelper;

public class TestNameFormatHelper extends NameFormatHelper {
    public static final String VOIVODESHIP_MOCK_NAME = "voivo";
    public static final String POWIAT_MOCK_NAME = "powiat";
    public static final String GMINA_MOCK_NAME = "gmina";
    public static final String RANDOM_MOCK_QUESTION = "random";

    public TestNameFormatHelper(Context context) {
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

    @Override public String getRandomQuestion() {
        return RANDOM_MOCK_QUESTION;
    }
}
