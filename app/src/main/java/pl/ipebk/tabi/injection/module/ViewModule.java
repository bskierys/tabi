/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.injection.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.infrastructure.views.DatabaseViewPlaceAndPlateDtoFactory;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.presentation.ui.search.RandomTextProvider;
import pl.ipebk.tabi.utils.FontManager;

@Module
public class ViewModule {
    private Context context;
    private FontManager fontManager;

    public ViewModule(Context context) {
        this.context = context;
        this.fontManager = FontManager.getInstance();
        this.fontManager.initialize(context, R.xml.fonts);
    }

    @Provides FontManager provideFontManager() {
        return fontManager;
    }

    @Provides PlaceLocalizationHelper providePlaceLocalizationHelper() {
        return new PlaceLocalizationHelper(context);
    }

    @Provides public RandomTextProvider provideRandomTextProvider() {
        return new RandomTextProvider(context);
    }

    @Provides PlaceAndPlateDtoFactory providePlaceAndPlateFactory(DatabaseViewPlaceAndPlateDtoFactory factory) {
        return factory;
    }
}
