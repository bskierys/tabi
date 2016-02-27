/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.di.ActivityContext;
import pl.ipebk.tabi.utils.SpellCorrector;

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides Activity provideActivity() {
        return activity;
    }

    @Provides @ActivityContext Context providesContext() {
        return activity;
    }

    @Provides SpellCorrector provideSpellCorrector() {
        return new SpellCorrector();
    }
}