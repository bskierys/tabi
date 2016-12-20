/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.github.bskierys.pine.Pine;

import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree;

import io.fabric.sdk.android.Fabric;
import pl.ipebk.tabi.injection.component.DaggerViewComponent;
import pl.ipebk.tabi.injection.component.ViewComponent;
import pl.ipebk.tabi.injection.component.ApplicationComponent;
import pl.ipebk.tabi.injection.component.DaggerApplicationComponent;
import pl.ipebk.tabi.injection.module.ViewModule;
import pl.ipebk.tabi.injection.module.ApplicationModule;
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
            Timber.plant(new Pine.Builder().addPackageReplacePattern(getPackageName(),"TABI").grow());
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
