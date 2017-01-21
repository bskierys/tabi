/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.module;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.CalendarSearchTimeProvider;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryRepository;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchTimeProvider;
import pl.ipebk.tabi.infrastructure.finders.DaoLicensePlateFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoPlaceFinder;
import pl.ipebk.tabi.infrastructure.finders.DaoSearchHistoryFinder;
import pl.ipebk.tabi.infrastructure.repositories.DaoPlaceRepository;
import pl.ipebk.tabi.infrastructure.repositories.DaoSearchHistoryRepository;
import pl.ipebk.tabi.presentation.DatabaseLoader;
import pl.ipebk.tabi.presentation.SqliteDatabaseLoader;
import pl.ipebk.tabi.presentation.ui.details.MapScaleCalculator;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.readmodel.PlaceFinder;
import pl.ipebk.tabi.readmodel.SearchHistoryFinder;
import pl.ipebk.tabi.presentation.ui.main.DoodleTextFormatter;
import pl.ipebk.tabi.presentation.ui.details.ClipboardCopyMachine;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.SpellCorrector;
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

    // somehow making it "ActivityContext" breaks it
    @Provides Context provideContext() {
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

    @Provides AnimationCreator provideAnimationHelper() {
        return new AnimationCreator(activity);
    }

    @Provides DoodleTextFormatter provideDoodleTextFormatter() {
        return new DoodleTextFormatter(activity);
    }

    @Provides PlaceLocalizationHelper providePlaceLocalizationHelper() {
        return new PlaceLocalizationHelper(activity);
    }

    @Provides ClipboardCopyMachine provideClipboardCopyMachine() {
        return new ClipboardCopyMachine(activity);
    }

    @Provides MapScaleCalculator provideMapScaleCalculator() {
        return new MapScaleCalculator(activity);
    }

    @Provides public SearchHistoryRepository provideSearchHistoryRepository(DaoSearchHistoryRepository repository) {
        return repository;
    }

    @Provides public PlaceRepository providePlaceRepository(DaoPlaceRepository repository) {
        return repository;
    }

    @Provides public LicensePlateFinder provideLicensePlateFinder(DaoLicensePlateFinder finder) {
        return finder;
    }

    @Provides public PlaceFinder providePlaceFinder(DaoPlaceFinder finder) {
        return finder;
    }

    @Provides public SearchHistoryFinder provideSearchHistoryFinder(DaoSearchHistoryFinder finder) {
        return finder;
    }

    @Provides public SearchTimeProvider provideSearchTimeProvider(CalendarSearchTimeProvider provider) {
        return provider;
    }

    @Provides public DatabaseLoader provideDatabaseLoader(SqliteDatabaseLoader loader) {
        return loader;
    }

    @Provides public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(activity);
    }
}