/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import icepick.Icepick;
import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.injection.component.ActivityComponent;
import pl.ipebk.tabi.injection.component.ConfigPersistentComponent;
import pl.ipebk.tabi.injection.component.DaggerConfigPersistentComponent;
import pl.ipebk.tabi.injection.module.ActivityModule;
import timber.log.Timber;

/**
 * Abstract activity that every other Activity in this application must implement. It handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent survive across configuration changes.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final Map<Long, ConfigPersistentComponent> sComponentsMap = new HashMap<>();

    private ActivityComponent activityComponent;
    private long mActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleEnterTransition(getWindow().getEnterTransition());
            handleReturnTransition(getWindow().getReturnTransition());
            setTaskDescription();
        }

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        mActivityId = savedInstanceState != null ?
                savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (!sComponentsMap.containsKey(mActivityId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                                                                       .applicationComponent(App.get(this).getAppComponent())
                                                                       .build();
            sComponentsMap.put(mActivityId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = sComponentsMap.get(mActivityId);
        }
        activityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTaskDescription() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(
                getString(R.string.app_name), bm, getResources().getColor(R.color.white));
        setTaskDescription(taskDesc);
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * Method fired only on devices with lollipop or higher api. It allows you to handle enter transition. Includes api filtering. Remember to copy target api annotation
     * when overrides
     *
     * @param transition Transition you can alter. ex. add listener
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void handleEnterTransition(Transition transition) {}

    /**
     * Method fired only on devices with lollipop or higher api. It allows you to handle return transition. Includes api filtering. Remember to copy target api annotation
     * when overrides
     *
     * @param transition Transition you can alter. ex. add listener
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void handleReturnTransition(Transition transition) {}

    @Override public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
