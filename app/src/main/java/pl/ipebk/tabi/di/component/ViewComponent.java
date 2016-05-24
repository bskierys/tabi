/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.component;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.di.module.ViewModule;
import pl.ipebk.tabi.di.module.ApplicationModule;
import pl.ipebk.tabi.ui.details.DetailsActivity;
import pl.ipebk.tabi.ui.details.DetailsButton;
import pl.ipebk.tabi.ui.search.PlaceItemAdapter;
import pl.ipebk.tabi.ui.search.SearchTabPageIndicator;
import pl.ipebk.tabi.utils.DoodleImage;
import pl.ipebk.tabi.utils.FontDecorator;

@Singleton
@Component(dependencies = ApplicationModule.class, modules = ViewModule.class)
public interface ViewComponent {
    void inject(PlaceItemAdapter adapter);
    void inject(FontDecorator decorator);
    void inject(DetailsButton button);
    void inject(SearchTabPageIndicator indicator);
}
