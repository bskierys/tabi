/*
* author: Bartlomiej Kierys
* date: 2016-12-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.about;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;

public class AboutAppActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        LibsSupportFragment fragment = new LibsBuilder()
                .withAboutAppName(getString(R.string.app_name))
                .withAboutIconShown(true)
                .withAboutVersionShownName(true)
                .withVersionShown(true)
                .withLicenseShown(true)
                .supportFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }
}
