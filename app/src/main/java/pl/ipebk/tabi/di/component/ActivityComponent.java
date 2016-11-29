/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.component;

import dagger.Subcomponent;
import pl.ipebk.tabi.di.PerActivity;
import pl.ipebk.tabi.di.module.ActivityModule;
import pl.ipebk.tabi.ui.details.DetailsActivity;
import pl.ipebk.tabi.ui.main.MainActivity;
import pl.ipebk.tabi.ui.search.SearchActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(SearchActivity searchActivity);

    void inject(DetailsActivity detailsActivity);
}
