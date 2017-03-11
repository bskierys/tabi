package pl.ipebk.tabi.presentation.ui.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;

public class DetailsCategoryActivity extends DetailsActivity {
    public final static String PARAM_CATEGORY_NAME = "param_category_name";
    public final static String PARAM_CATEGORY_PLATE = "param_category_plate";

    @Inject AnimationCreator animationCreator;

    @BindView(R.id.txt_title) TextView toolbarTitle;
    @BindView(R.id.txt_plate) TextView toolbarPlate;
    @BindView(R.id.btn_back) ImageView backButton;
    @BindView(R.id.content_container) View contentContainer;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        afterLayoutInflate(savedInstanceState);

        prepareToolbar();
        loadData();
    }

    private void prepareToolbar() {
        try {
            String categoryName = getIntent().getStringExtra(PARAM_CATEGORY_NAME);
            String categoryPlate = getIntent().getStringExtra(PARAM_CATEGORY_PLATE);
            if (categoryName == null || categoryPlate == null) {
                throw new NullPointerException("Category name is null");
            }
            toolbarTitle.setText(categoryName);
            toolbarPlate.setText(categoryPlate);
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not initialize CategoryActivity: category key was not passed");
        }

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_back_light);
        backButton.setImageDrawable(backArrow);
    }

    @Override protected void loadData() {
        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        int position = intent.getIntExtra(PARAM_ADAPTER_POSITION, -1);

        DetailsFragment fragment = DetailsFragment.newInstance(placeId, searchedPlate, PlaceListItemType.SEARCH,
                                                               SearchType.LICENSE_PLATE, position);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_container, fragment);
        ft.commit();
    }

    @Override protected void onNotOverscrolled(Float scroll) {
        // do nothing
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }
}
