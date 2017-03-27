package pl.ipebk.tabi.presentation.ui.details;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.MarginProxy;

public class DetailsSearchActivity extends DetailsActivity {
    public final static String PARAM_SEARCHED_TYPE = "param_searched_type";
    public final static String PARAM_ITEM_TYPE = "param_item_type";

    // toolbar
    @BindView(R.id.txt_searched) TextView searchedTextView;
    @BindView(R.id.editTxt_search) EditText searchedEditText;
    @BindView(R.id.toolbar_tab_indicator) SearchTabPageIndicator toolbarIndicator;
    @BindView(R.id.btn_clear) View clearButton;

    @BindDimen(R.dimen.Toolbar_Height_Min) int toolbarHeight;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        afterLayoutInflate(savedInstanceState);

        prepareScrollEvents();
        prepareToolbar();
        loadData();
    }

    private void prepareScrollEvents() {
        MarginProxy indicatorMarginManager = new MarginProxy(toolbarIndicator);

        scrollSubscriptions.add(getScrollStream().filter(scroll -> scroll != null)
                                                 .filter(scroll -> scroll <= toolbarHeight)
                                                 .subscribe(scroll -> indicatorMarginManager.setTopMargin(scroll.intValue())));
    }

    private void prepareToolbar() {
        SearchType searchType = (SearchType) getIntent().getSerializableExtra(PARAM_SEARCHED_TYPE);
        // use static page indicator for fake hook to viewpager
        toolbarIndicator.setViewPager(new StaticPageIndicatorViewPager(
                this, getString(R.string.search_tab_plate), getString(R.string.search_tab_place)));
        toolbarIndicator.setCurrentItem(searchType.ordinal());

        MarginProxy indicatorMarginManager = new MarginProxy(toolbarIndicator);
        indicatorMarginManager.setTopMargin(0);
    }

    @Override protected void onNotOverscrolled(Float scroll) {
        Animator anim = animationCreator
                .getDetailsAnimator()
                .createIndicatorBackAnim(toolbarIndicator, scroll);
        anim.start();
    }

    @Override protected void loadData() {
        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        PlaceListItemType itemType = (PlaceListItemType) intent.getSerializableExtra(PARAM_ITEM_TYPE);
        SearchType searchType = (SearchType) getIntent().getSerializableExtra(PARAM_SEARCHED_TYPE);
        int position = intent.getIntExtra(PARAM_ADAPTER_POSITION, -1);

        showSearchedText(searchedPlate);

        DetailsFragment fragment = DetailsFragment.newInstance(placeId, searchedPlate, itemType, searchType, position);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_container, fragment);
        ft.commit();
    }

    private void showSearchedText(String searchedText) {
        searchedEditText.setVisibility(View.GONE);
        searchedTextView.setText(searchedText);
        if (searchedText != null && !searchedText.equals("")) {
            clearButton.setVisibility(View.VISIBLE);
        } else {
            clearButton.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }

    @OnClick(R.id.btn_clear) public void onClearButton() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, false);
        intent.putExtra(SearchActivity.PARAM_SEARCH_TEXT, "");
        startActivity(intent);
    }

    @OnClick(R.id.txt_searched) public void onSearchClicked() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, true);
        startActivity(intent);
    }
}
