package pl.ipebk.tabi.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.search.SearchActivity;

public class MainActivity extends BaseActivity implements MainMvpView {
    @Inject MainPresenter presenter;
    @Bind(R.id.txt_prompt) TextView promptView;
    @Bind(R.id.img_loading) ImageView loadingView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    @OnClick(R.id.btn_go) public void onGo() {
        presenter.goToSearch();
    }

    //region Mvp view methods
    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        promptView.setText("Preparing database");
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.INVISIBLE);
        promptView.setText("");
    }

    @Override public void showTime(String time) {
        promptView.setText(time);
    }

    @Override public void showError(String errorText) {
        promptView.setText(errorText);
    }

    @Override public void goToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
    //endregion
}
