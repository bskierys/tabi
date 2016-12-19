/*
* author: Bartlomiej Kierys
* date: 2016-12-19
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.content.Context;

import java.util.Random;

import pl.ipebk.tabi.R;
import timber.log.Timber;

/**
 * Helps to format search rows for random places
 */
public class RandomTextProvider {
    private final static String RANDOM_QUESTION_RESOURCE_NAME = "search_random_question_";
    private final static int RANDOM_QUESTION_MAX_VALUE = 8;
    private final static String UNKNOWN_PLATE_CHARACTER = "???";

    private Context context;
    private Random random;

    public RandomTextProvider(Context context) {
        this.context = context;
        random = new Random();
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

    public String getUnknownPlatePlaceholder() {
        return UNKNOWN_PLATE_CHARACTER;
    }
}
