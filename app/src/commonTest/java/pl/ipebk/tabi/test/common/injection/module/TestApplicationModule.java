/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.presentation.SqliteDatabaseLoader;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment This allows injecting mocks if
 * necessary.
 */
@Module
public class TestApplicationModule {

    private final Application application;

    public TestApplicationModule(Application application) {
        this.application = application;
    }

    @Provides Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext Context provideContext() {
        return application;
    }

    /*************
     * MOCKS
     *************/

    @Provides public DatabaseOpenHelper provideDatabaseOpenHelper() {
        return mock(DatabaseOpenHelper.class);
    }

    @Provides
    @Singleton public SqliteDatabaseLoader provideDataManager() {
        return mock(SqliteDatabaseLoader.class);
    }

    @Provides public SearchTimeProvider provideSearchTimeProvider() {
        return mock(SearchTimeProvider.class);
    }

    @Provides public PlaceRepository providePlaceRepository() {
        return mock(PlaceRepository.class);
    }

    @Provides public SearchHistoryFinder provideSearchHistoryFinder() {
        return mock(SearchHistoryFinder.class);
    }
}
