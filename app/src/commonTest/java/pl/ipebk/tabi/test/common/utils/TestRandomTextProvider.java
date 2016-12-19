/*
* author: Bartlomiej Kierys
* date: 2016-05-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.utils;

import android.content.Context;

import pl.ipebk.tabi.ui.search.RandomTextProvider;

public class TestRandomTextProvider extends RandomTextProvider {
    public static final String RANDOM_MOCK_QUESTION = "random";

    public TestRandomTextProvider(Context context) {
        super(context);
    }

    @Override public String getRandomQuestion() {
        return RANDOM_MOCK_QUESTION;
    }
}
