/*
* author: Bartlomiej Kierys
* date: 2016-11-29
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import dagger.Component;
import pl.ipebk.tabi.injection.ConfigPersistent;
import pl.ipebk.tabi.injection.module.ActivityModule;

/**
 * A dagger component that will live during the lifecycle of an Activity but it won't be destroy during configuration
 * changes. Check {@link pl.ipebk.tabi.presentation.ui.base.BaseActivity} to see how this components survives configuration changes.
 * Use the {@link ConfigPersistent} scope to annotate dependencies that need to survive configuration changes (for
 * example Presenters).
 */
@ConfigPersistent
@Component(dependencies = {ApplicationComponent.class})
public interface ConfigPersistentComponent {
    ActivityComponent activityComponent(ActivityModule activityModule);
}