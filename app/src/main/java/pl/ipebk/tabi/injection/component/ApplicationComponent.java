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
import com.suredigit.inappfeedback.FeedbackClient;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.injection.module.ApplicationModule;
import pl.ipebk.tabi.presentation.localization.DemoGreetingPredicate;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.presentation.ui.search.RandomTextProvider;

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

    RandomTextProvider randomTextProvider();

    PlaceAndPlateDtoFactory placeAndPlateDtoFactory();
}
