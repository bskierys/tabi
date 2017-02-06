/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.feedback.Feedback;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.feedback.FeedbackTypeAdapterFactory;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.CalendarSearchTimeProvider;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.infrastructure.finders.DaoLicensePlateFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoPlaceFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoSearchHistoryFinder;
import pl.ipebk.tabi.infrastructure.repositories.DaoPlaceRepository;
import pl.ipebk.tabi.infrastructure.repositories.DaoSearchHistoryRepository;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.presentation.ui.main.DoodleTextFormatter;
import pl.ipebk.tabi.presentation.ui.details.ClipboardCopyMachine;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides Application provideApplication() {
        return application;
    }

    @Provides @ApplicationContext Context provideContext() {
        return application;
    }

    @Provides @Singleton public StopwatchManager provideStopwatchManager() {
        return new StopwatchManager();
    }

    @Provides public DoodleTextFormatter provideDoodleTextFormatter() {
        return new DoodleTextFormatter(application);
    }

    @Provides public ClipboardCopyMachine provideClipboardCopyMachine() {
        return new ClipboardCopyMachine(application);
    }

    @Provides public SearchTimeProvider provideSearchTimeProvider(CalendarSearchTimeProvider provider) {
        return provider;
    }

    @Provides public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides public FeedbackClient provideFeedbackClient() {
        return Feedback.getClient();
    }
}