/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.app.Application;
import android.content.Context;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.di.ApplicationContext;
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.domain.searchhistory.CalendarSearchTimeProvider;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.domain.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.infrastructure.finders.DaoLicensePlateFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoPlaceFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoSearchHistoryFinder;
import pl.ipebk.tabi.infrastructure.repositories.DaoPlaceRepository;
import pl.ipebk.tabi.infrastructure.repositories.DaoSearchHistoryRepository;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.utils.DeviceHelper;
import pl.ipebk.tabi.utils.NameFormatHelper;
import pl.ipebk.tabi.utils.StopwatchManager;

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

    @Provides public NameFormatHelper provideNameFormatHelper() {
        return new NameFormatHelper(application);
    }

    @Provides public DeviceHelper provideDeviceHelper() {
        return new DeviceHelper(application);
    }

    @Provides public SearchHistoryRepository provideSearchHistoryRepository(DaoSearchHistoryRepository repository) {
        return repository;
    }

    @Provides public PlaceRepository providePlaceRepository(DaoPlaceRepository repository) {
        return repository;
    }

    @Provides public LicensePlateFinder provideLicensePlateFinder(DaoLicensePlateFinder finder) {
        return finder;
    }

    @Provides public PlaceFinder providePlaceFinder(DaoPlaceFinder finder) {
        return finder;
    }

    @Provides public SearchHistoryFinder provideSearchHistoryFinder(DaoSearchHistoryFinder finder) {
        return finder;
    }

    @Provides public SearchTimeProvider provideSearchTimeProvider(CalendarSearchTimeProvider provider) {
        return provider;
    }
}