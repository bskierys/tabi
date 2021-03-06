/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.module;

import android.content.Context;
import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.infrastructure.repositories.DaoPlaceRepository;
import pl.ipebk.tabi.injection.FragmentContext;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import pl.ipebk.tabi.presentation.ui.details.ClipboardCopyMachine;
import pl.ipebk.tabi.presentation.ui.custom.chromeTabs.CustomTabActivityHelper;
import pl.ipebk.tabi.presentation.ui.details.MapScaleCalculator;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.utils.FontManager;

@Module
public class FragmentModule {
    private Fragment fragment;
    private FontManager fontManager;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
        this.fontManager = FontManager.getInstance();
        this.fontManager.initialize(providesContext(), R.xml.fonts);
    }

    @Provides Fragment provideFragment() {
        return fragment;
    }

    @Provides @FragmentContext Context providesContext() {
        return fragment.getContext();
    }

    @Provides FontManager provideFontManager() {
        return fontManager;
    }

    @Provides public PlaceRepository providePlaceRepository(DaoPlaceRepository repository) {
        return repository;
    }

    @Provides ClipboardCopyMachine provideClipboardCopyMachine() {
        return new ClipboardCopyMachine(providesContext());
    }

    @Provides MapScaleCalculator provideMapScaleCalculator() {
        return new MapScaleCalculator(providesContext());
    }

    @Provides PlaceLocalizationHelper providePlaceLocalizationHelper() {
        return new PlaceLocalizationHelper(providesContext());
    }

    @Provides AnimationCreator provideAnimationHelper() {
        return new AnimationCreator(providesContext());
    }

    @Provides CustomTabActivityHelper provideCustomTabActivityHelper() {
        return new CustomTabActivityHelper();
    }
}
