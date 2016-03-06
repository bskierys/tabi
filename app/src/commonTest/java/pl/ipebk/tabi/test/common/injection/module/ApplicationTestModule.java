/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.di.ApplicationContext;
import pl.ipebk.tabi.manager.DataManager;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
        mApplication = application;
    }

    @Provides Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton Bus provideEventBus() {
        return new Bus();
    }

    /*************
     * MOCKS
     *************/

    @Provides
    @Singleton DataManager provideDataManager() {
        return mock(DataManager.class);
    }
}
