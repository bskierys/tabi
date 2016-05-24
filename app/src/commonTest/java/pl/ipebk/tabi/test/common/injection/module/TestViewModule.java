/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.NameFormatHelper;
import static org.mockito.Mockito.*;

@Module public class TestViewModule {

    protected Context context;

    public TestViewModule(Context context) {
        this.context = context;
    }

    @Provides public NameFormatHelper provideNameFormatHelper() {
        return mock(NameFormatHelper.class);
    }

    @Provides public FontManager provideFontManager() {
        return mock(FontManager.class);
    }
}
