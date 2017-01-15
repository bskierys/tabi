/*
* author: Bartlomiej Kierys
* date: 2017-01-15
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.localization;

import android.content.Context;

import javax.inject.Inject;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.injection.ApplicationContext;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DemoGreetingPredicate {
    private Context context;

    @Inject public DemoGreetingPredicate(@ApplicationContext Context context) {
        this.context = context;
    }

    public boolean shouldShowDemoGreeting(){
        return context.getResources().getBoolean(R.bool.use_english_greeting);
    }
}
