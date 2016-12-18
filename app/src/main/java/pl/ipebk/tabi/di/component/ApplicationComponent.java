/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.di.ApplicationContext;
import pl.ipebk.tabi.di.module.ApplicationModule;
import pl.ipebk.tabi.domain.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.presentation.SqliteDatabaseLoader;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationContext Context context();

    Application application();

    DatabaseOpenHelper databaseHelper();

    SearchHistoryFactory searchHistoryFactory();
}
