/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.infrastructure.views.DatabaseViewPlaceAndPlateDtoFactory;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.ResourceHelper;

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

    @Provides ResourceHelper provideResourceHelper() {
        return new ResourceHelper(context);
    }

    @Provides PlaceAndPlateDtoFactory providePlaceAndPlateFactory(DatabaseViewPlaceAndPlateDtoFactory factory) {
        return factory;
    }
}
