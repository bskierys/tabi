/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.injection.module.ApplicationModule;
import pl.ipebk.tabi.presentation.localization.DemoGreetingPredicate;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationContext Context context();

    Application application();

    DatabaseOpenHelper databaseHelper();

    SearchHistoryFactory searchHistoryFactory();

    DemoGreetingPredicate demoGreetingPredicate();

    SharedPreferences sharedPreferences();

    FeedbackClient feedbackClient();
}
