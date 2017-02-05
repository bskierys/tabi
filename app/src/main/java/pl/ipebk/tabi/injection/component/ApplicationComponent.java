/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.feedback.DeviceInfoProvider;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.feedback.FeedbackRestClient;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.injection.module.ApplicationModule;
import pl.ipebk.tabi.presentation.localization.DemoGreetingPredicate;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.utils.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationContext Context context();

    Application application();

    DatabaseOpenHelper databaseHelper();

    SearchHistoryFactory searchHistoryFactory();

    DemoGreetingPredicate demoGreetingPredicate();

    RxEventBus eventBus();

    DeviceInfoProvider deviceInfoProvider();

    SharedPreferences sharedPreferences();

    FeedbackRestClient feedbackRestClient();

    FeedbackClient feedbackClient();

    Gson gson();
}
