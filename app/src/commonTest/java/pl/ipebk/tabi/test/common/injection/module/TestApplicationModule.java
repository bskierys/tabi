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
import pl.ipebk.tabi.di.ApplicationContext;
import pl.ipebk.tabi.domain.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.manager.DataManager;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module public class TestApplicationModule {

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

    @Provides
    @Singleton public DataManager provideDataManager() {
        return null;
    }

    @Provides public SearchTimeProvider provideSearchTimeProvider() {
        return null;
    }
}
