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
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.di.ApplicationContext;
import pl.ipebk.tabi.di.module.ApplicationModule;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.ui.search.PlaceItemAdapter;
import pl.ipebk.tabi.utils.DeviceHelper;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationContext Context context();

    Application application();

    DatabaseOpenHelper databaseHelper();

    DataManager dataManager();
}
