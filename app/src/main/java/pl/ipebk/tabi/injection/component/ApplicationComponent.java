/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.injection.ApplicationContext;
import pl.ipebk.tabi.injection.module.ApplicationModule;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationContext Context context();

    Application application();

    DatabaseOpenHelper databaseHelper();

    SearchHistoryFactory searchHistoryFactory();
}
