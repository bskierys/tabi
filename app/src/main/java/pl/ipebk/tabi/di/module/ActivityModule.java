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
    private Picasso picasso;

    public ActivityModule(Activity activity) {
        this.activity = activity;
        picasso = new Picasso.Builder(activity)
                .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
                .build();
    }

    @Provides Activity provideActivity() {
        return activity;
    }

    @Provides @ActivityContext Context provideContext() {
        return activity;
    }

    @Provides Picasso providePicasso() {
        return picasso;
    }

    @Provides SpellCorrector provideSpellCorrector() {
        return new SpellCorrector();
    }
}