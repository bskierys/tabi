package pl.ipebk.tabi.presentation.ui.details;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.MarginProxy;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;
import pl.ipebk.tabi.utils.RxUtil;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DetailsSearchActivity extends BaseActivity {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";
    public final static String PARAM_SEARCHED_TYPE = "param_searched_type";
    public final static String PARAM_ITEM_TYPE = "param_item_type";
    public final static String PARAM_ADAPTER_POSITION = "param_adapter_position";

    // toolbar
    @BindView(R.id.txt_searched) TextView searchedTextView;
    @BindView(R.id.editTxt_search) EditText searchedEditText;
    @BindView(R.id.toolbar_tab_indicator) SearchTabPageIndicator toolbarIndicator;
    @BindView(R.id.btn_clear) View clearButton;
    // others
    @BindView(R.id.scroll_container) ScrollView scrollContainer;
    @BindView(R.id.overlay_black) ImageView blackOverlay;
    @BindView(R.id.row_bg) View rowBackground;

    @BindDimen(R.dimen.Toolbar_Height_Min) int toolbarHeight;
    @Inject AnimationCreator animationCreator;

    private CompositeSubscription scrollSubscriptions;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        prepareIndicator();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTransitions();
        }

        prepareOverScroll();
        loadData();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitions() {
        int position = getIntent().getIntExtra(PARAM_ADAPTER_POSITION, -1);
        rowBackground.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_row_background), position));

        AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
        Transition enterTransition = anim.createBgFadeInTransition(blackOverlay);
        enterTransition.excludeTarget(scrollContainer, true);
        enterTransition.addListener(new SimpleTransitionListener.Builder().withOnEndAction(t -> {
            scrollContainer.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
            rowBackground.getLayoutParams().height = scrollContainer.getHeight();
        }).build());
        getWindow().setEnterTransition(enterTransition);

        Transition returnTransition = anim.createBgFadeOutTransition(blackOverlay);
        returnTransition.addListener(new SimpleTransitionListener.Builder().withOnStartAction(t -> {
            scrollContainer.setBackgroundColor(getResources().getColor(R.color.transparent));
        }).build());
        getWindow().setReturnTransition(returnTransition);

        anim.alterSharedTransition(getWindow().getSharedElementEnterTransition());
        anim.alterSharedTransition(getWindow().getSharedElementReturnTransition());
    }

    private void prepareIndicator() {
        SearchType searchType = (SearchType) getIntent().getSerializableExtra(PARAM_SEARCHED_TYPE);
        // use static page indicator for fake hook to viewpager
        toolbarIndicator.setViewPager(new StaticPageIndicatorViewPager(
                this, getString(R.string.search_tab_plate), getString(R.string.search_tab_place)));
        toolbarIndicator.setCurrentItem(searchType.ordinal());

        MarginProxy indicatorMarginManager = new MarginProxy(toolbarIndicator);
        indicatorMarginManager.setTopMargin(0);
    }

    private void prepareOverScroll() {
        float marginOffset = getResources().getDimensionPixelOffset(R.dimen.Details_Height_Release_Scroll);
        scrollSubscriptions = new CompositeSubscription();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollContainer.setElevation(getResources().getDimensionPixelSize(R.dimen.Details_Elevation));
        }

        ObservableVerticalOverScrollBounceEffectDecorator decorator =
                new ObservableVerticalOverScrollBounceEffectDecorator(
                        new ScrollViewOverScrollDecorAdapter(scrollContainer), 3f, 1f, -1
                );

        MarginProxy indicatorMarginManager = new MarginProxy(toolbarIndicator);

        scrollSubscriptions.add(decorator.getScrollEventStream()
                                         .filter(scroll -> scroll != null)
                                         .filter(scroll -> scroll <= toolbarHeight)
                                         .subscribe(scroll -> indicatorMarginManager.setTopMargin(scroll.intValue())));

        MarginProxy bgMarginManager = new MarginProxy(rowBackground);
        scrollSubscriptions.add(decorator.getScrollEventStream().filter(scroll -> scroll != null)
                                         .subscribe(scroll -> bgMarginManager.setTopMargin(scroll.intValue())));

        scrollSubscriptions.add(decorator.getReleaseEventStream()
                                         .filter(scroll -> scroll != null)
                                         .subscribe(scroll -> {
                                             if (scroll >= marginOffset || scroll <= marginOffset * (-1)) {
                                                 onOverscrolled();
                                             } else {
                                                 Animator anim = animationCreator
                                                         .getDetailsAnimator()
                                                         .createIndicatorBackAnim(toolbarIndicator, scroll);
                                                 anim.start();
                                             }
                                         }));
    }

    private void onOverscrolled() {
        Timber.d("Screen overscrolled");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // going back without animation will be confusing. go back only when there is animation (API >= 21)
            Timber.d("API over lollipop. Loading previous screen");
            onBackPressed();
        }
    }

    private void loadData() {
        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        PlaceListItemType itemType = (PlaceListItemType) intent.getSerializableExtra(PARAM_ITEM_TYPE);
        SearchType searchType = (SearchType) getIntent().getSerializableExtra(PARAM_SEARCHED_TYPE);
        int position = intent.getIntExtra(PARAM_ADAPTER_POSITION, -1);

        showSearchedText(searchedPlate);

        // TODO: 2017-03-04 pass it
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

    @Override protected void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(scrollSubscriptions);
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
