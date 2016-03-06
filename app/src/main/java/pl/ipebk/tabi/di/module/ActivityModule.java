/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.app.Activity;
import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.di.ActivityContext;
import pl.ipebk.tabi.utils.SpellCorrector;
import timber.log.Timber;

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides Activity provideActivity() {
        return activity;
    }

    @Provides @ActivityContext Context provideContext() {
        return activity;
    }

    // TODO: 2016-02-28 inject as singleton
    @Provides Picasso providePicasso() {
        return new Picasso.Builder(activity)
                .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
                .build();
    }

    @Provides SpellCorrector provideSpellCorrector() {
        return new SpellCorrector();
    }
}