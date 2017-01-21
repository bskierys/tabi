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
import pl.ipebk.tabi.utils.PreferenceHelper;

/**
 * Utility class responsible for handling whenever greeting demo dialog should be shown or not.
 */
public class DemoGreetingPredicate {
    private Context context;
    private PreferenceHelper prefHelper;

    @Inject public DemoGreetingPredicate(@ApplicationContext Context context, PreferenceHelper prefHelper) {
        this.context = context;
        this.prefHelper = prefHelper;
    }

    /**
     * @return Should return true only when demo dialog was not shown never before and when locale is not polish
     */
    public boolean shouldShowDemoGreeting(){
        return context.getResources().getBoolean(R.bool.use_english_greeting) && !prefHelper.wasDemoGreetingShown();
    }

    /**
     * Should be marked shown as soon as dialog shows up
     */
    public void markDemoGreetingShown() {
        prefHelper.demoGreetingShown();
    }
}
