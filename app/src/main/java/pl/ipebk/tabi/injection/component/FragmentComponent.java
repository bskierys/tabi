/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import dagger.Subcomponent;
import pl.ipebk.tabi.injection.PerActivity;
import pl.ipebk.tabi.injection.module.FragmentModule;
import pl.ipebk.tabi.presentation.ui.details.DetailsFragment;

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerActivity
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {
    void inject(DetailsFragment fragment);
}
