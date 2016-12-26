/*
* author: Bartlomiej Kierys
* date: 2016-12-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.about;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

public class AboutAppActivity extends LibsActivity {
    @Override public void onCreate(Bundle savedInstanceState) {

        LibsBuilder builder = new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withAboutIconShown(true)
                .withActivityTitle("O aplikacji")
                .withAboutVersionShown(true);

        setIntent(builder.intent(this));
        super.onCreate(savedInstanceState);

        // TODO: 2016-12-26 this activity should rather have fragment and custom toolbar to support ui customization 
        // TODO: 2016-12-26 separate section about ap from about libraries?
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
