/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.app.Activity;
import android.content.Context;

import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.di.ActivityContext;
import pl.ipebk.tabi.utils.AnimationHelper;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.utils.Stopwatch;
import pl.ipebk.tabi.utils.StopwatchManager;
import timber.log.Timber;

@Module
public class ActivityModule {
    private Activity activity;
    private Picasso picasso;
    private FontManager fontManager;

    public ActivityModule(Activity activity) {
        this.activity = activity;
        this.fontManager = FontManager.getInstance();
        this.fontManager.initialize(activity, R.xml.fonts);
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

    @Provides FontManager provideFontManager() {
        return fontManager;
    }

    @Provides AnimationHelper provideAnimationHelper() {
        return new AnimationHelper();
    }
}