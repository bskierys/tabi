/*
* author: Bartlomiej Kierys
* date: 2016-04-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.readmodel.PlaceType;
import timber.log.Timber;

/**
 * Helper class that handles readable text formatting for places.
 */
public class NameFormatHelper {
    private final static String RANDOM_QUESTION_RESOURCE_NAME = "search_random_question_";
    private final static int RANDOM_QUESTION_MAX_VALUE = 8;
    private final static String POWIAT_REPLACE_FORMAT = "$z$";
    public final static String UNKNOWN_PLATE_CHARACTER = "???";

    private Context context;
    private Random random = new Random();

    public NameFormatHelper(Context context) {
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
    // TODO: 2016-12-06 this should be domain helper
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
        if (place.getPlates().size() > 1) {
            otherPlates = ", " + context.getString(R.string.details_additional_other_plates) + ": "
                    + place.platesToStringExceptMatchingPattern(searchedPlate);
        }

        return placeType + otherPlates;
    }

    /**
     * Returns random question for random search result. If you want to add another question please add another resource
     * named search_random_question_{next_number} and increase max search number. In case of error it retrieves named
     * search_random_question_0
     */
    public String getRandomQuestion() {
        String packageName = context.getPackageName();
        int randomQuestionNumber = random.nextInt(RANDOM_QUESTION_MAX_VALUE + 1);
        String randomQuestionIdentifier = RANDOM_QUESTION_RESOURCE_NAME + Integer.toString(randomQuestionNumber);

        String randomQuestion;
        try {
            int randomQuestionResourceId = context
                    .getResources().getIdentifier(randomQuestionIdentifier, "string", packageName);
            randomQuestion = context.getString(randomQuestionResourceId);
        } catch (Exception e) {
            Timber.e("Error retrieving for resource name %s", randomQuestionIdentifier);
            randomQuestion = context.getString(R.string.search_random_question_0);
            e.printStackTrace();
        }

        return randomQuestion;
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

    public String formatDoodleGreeting() {
        // TODO: 2016-06-05 different greetings
        return context.getString(R.string.main_doodle_greeting);
    }

    public SpannableString formatDoodleCaption(String base) {
        List<Integer> positions = getSpanKeyPoints(base);
        if (positions.size() % 2 == 1) {
            throw new IllegalArgumentException("Invalid number of asterisks in base text");
        }
        int spanCount = positions.size() - 1;

        String altered = base.replace("*", "");

        SpannableString text = new SpannableString(altered);
        for (int i = 0; i < spanCount; i++) {
            TextAppearanceSpan span;
            if (i % 2 == 0) {
                span = new TextAppearanceSpan(context, R.style.Main_Text_Caption_Small);
            } else {
                span = new TextAppearanceSpan(context, R.style.Main_Text_Caption_Large);
            }

            text.setSpan(span, positions.get(i), positions.get(i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    @NonNull private List<Integer> getSpanKeyPoints(String base) {
        List<Integer> positions = new ArrayList<>();
        positions.add(0);
        int asterisksSpotted = 0;
        for (int i = 0; i < base.length(); i++) {
            if (base.charAt(i) == '*') {
                positions.add(i - asterisksSpotted);
                asterisksSpotted++;
            }
        }
        positions.add(base.length() - asterisksSpotted);

        return positions;
    }
}
