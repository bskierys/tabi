/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import dagger.Subcomponent;
import pl.ipebk.tabi.injection.PerActivity;
import pl.ipebk.tabi.injection.module.ActivityModule;
import pl.ipebk.tabi.presentation.ui.category.CategoryActivity;
import pl.ipebk.tabi.presentation.ui.details.DetailsActivity;
import pl.ipebk.tabi.presentation.ui.details.DetailsCategoryActivity;
import pl.ipebk.tabi.presentation.ui.details.DetailsSearchActivity;
import pl.ipebk.tabi.presentation.ui.feedback.FeedbackEntryActivity;
import pl.ipebk.tabi.presentation.ui.main.MainActivity;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(SearchActivity searchActivity);

    void inject(CategoryActivity categoryActivity);

    void inject(FeedbackEntryActivity activity);

    void inject(DetailsActivity activity);
}
