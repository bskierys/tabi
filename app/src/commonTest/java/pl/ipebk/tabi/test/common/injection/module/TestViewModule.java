/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.ui.main.DoodleTextFormatter;
import pl.ipebk.tabi.ui.search.RandomTextProvider;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.ResourceHelper;
import static org.mockito.Mockito.mock;

@Module
public class TestViewModule {

    protected Context context;

    public TestViewModule(Context context) {
        this.context = context;
    }

    @Provides public DoodleTextFormatter provideNameFormatHelper() {
        return mock(DoodleTextFormatter.class);
    }

    @Provides public RandomTextProvider provideRandomTextProvider() {
        return mock(RandomTextProvider.class);
    }

    @Provides PlaceLocalizationHelper providePlaceLocalizationHelper() {
        return mock(PlaceLocalizationHelper.class);
    }

    @Provides public FontManager provideFontManager() {
        return mock(FontManager.class);
    }

    @Provides ResourceHelper provideResourceHelper() {
        return mock(ResourceHelper.class);
    }

    @Provides PlaceAndPlateDtoFactory providePlaceAndPlateFactory() {
        return mock(PlaceAndPlateDtoFactory.class);
    }
}
