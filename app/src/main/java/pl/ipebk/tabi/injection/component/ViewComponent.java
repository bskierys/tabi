/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.injection.module.ApplicationModule;
import pl.ipebk.tabi.injection.module.ViewModule;
import pl.ipebk.tabi.presentation.ui.custom.font.FontDecorator;
import pl.ipebk.tabi.presentation.ui.details.DetailsButton;
import pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator;

@Singleton
@Component(dependencies = ApplicationModule.class, modules = ViewModule.class)
public interface ViewComponent {
    void inject(FontDecorator decorator);
    void inject(DetailsButton button);
    void inject(SearchTabPageIndicator indicator);
}
