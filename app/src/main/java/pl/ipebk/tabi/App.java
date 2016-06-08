/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree;

import io.fabric.sdk.android.Fabric;
import pl.ipebk.tabi.di.component.DaggerViewComponent;
import pl.ipebk.tabi.di.component.ViewComponent;
import pl.ipebk.tabi.di.component.ApplicationComponent;
import pl.ipebk.tabi.di.component.DaggerApplicationComponent;
import pl.ipebk.tabi.di.module.ViewModule;
import pl.ipebk.tabi.di.module.ApplicationModule;
import pl.ipebk.tabi.utils.MonitorLoggingTree;
import timber.log.Timber;

public class App extends Application {
    private ApplicationComponent appComponent;
    private ViewComponent viewComponent;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new MonitorLoggingTree());
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsLogExceptionTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsLogExceptionTree());
        }
    }

    protected ApplicationModule getApplicationModule() {
        return new ApplicationModule(this);
    }

    public ApplicationComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerApplicationComponent.builder()
                                                     .applicationModule(getApplicationModule())
                                                     .build();
        }
        return appComponent;
    }

    public void setAppComponent(ApplicationComponent component) {
        this.appComponent = component;
    }

    public ViewComponent getViewComponent() {
        if (viewComponent == null) {
            viewComponent = DaggerViewComponent.builder()
                                               .applicationModule(getApplicationModule())
                                               .viewModule(new ViewModule(this))
                                               .build();
        }
        return viewComponent;
    }

    public void setViewComponent(ViewComponent viewComponent) {
        this.viewComponent = viewComponent;
    }
}
