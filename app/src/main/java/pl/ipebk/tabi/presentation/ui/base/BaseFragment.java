/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.injection.component.ConfigPersistentComponent;
import pl.ipebk.tabi.injection.component.DaggerConfigPersistentComponent;
import pl.ipebk.tabi.injection.component.FragmentComponent;
import pl.ipebk.tabi.injection.module.FragmentModule;
import timber.log.Timber;

/**
 * Abstract fragment that every other Fragment in this application must implement. It handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent survive across configuration changes.
 */
public class BaseFragment extends Fragment {
    private static final String KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final Map<Long, ConfigPersistentComponent> componentsMap = new HashMap<>();

    private FragmentComponent fragmentComponent;
    private long fragmentId;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the FragmentComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        fragmentId = savedInstanceState != null ?
                savedInstanceState.getLong(KEY_FRAGMENT_ID) : NEXT_ID.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (!componentsMap.containsKey(fragmentId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", fragmentId);
            configPersistentComponent = DaggerConfigPersistentComponent
                    .builder()
                    .applicationComponent(App.get(this.getContext()).getAppComponent())
                    .build();
            componentsMap.put(fragmentId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", fragmentId);
            configPersistentComponent = componentsMap.get(fragmentId);
        }
        fragmentComponent = configPersistentComponent.fragmentComponent(new FragmentModule(this));
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_FRAGMENT_ID, fragmentId);
    }

    @Override public void onDestroy() {
        Timber.i("Clearing ConfigPersistentComponent id=%d", fragmentId);
        componentsMap.remove(fragmentId);
        super.onDestroy();
    }

    protected FragmentComponent fragmentComponent() {
        return fragmentComponent;
    }
}
