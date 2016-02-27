/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.base;

import android.support.v7.app.AppCompatActivity;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.di.component.ActivityComponent;
import pl.ipebk.tabi.di.component.DaggerActivityComponent;
import pl.ipebk.tabi.di.module.ActivityModule;

/**
 * Base Activity for all activities across application.
 * Provides method to help presenter injection
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ActivityComponent activityComponent;

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(App.get(this).getAppComponent())
                    .build();
        }
        return activityComponent;
    }
}
