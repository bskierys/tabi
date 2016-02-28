/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.di.ApplicationContext;
import timber.log.Timber;

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

    @Provides @Singleton Bus provideEventBus() {
        return new Bus();
    }
}