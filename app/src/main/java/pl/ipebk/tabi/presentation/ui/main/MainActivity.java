package pl.ipebk.tabi.presentation.ui.main;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.about.AboutAppActivity;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.category.CategoryActivity;
import pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator;
import pl.ipebk.tabi.presentation.ui.details.StaticPageIndicatorViewPager;
import pl.ipebk.tabi.presentation.ui.feedback.FeedbackTypeActivity;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.MarginProxy;
import pl.ipebk.tabi.presentation.ui.utils.animation.RxAnimator;
import pl.ipebk.tabi.utils.RxUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, MainItemAdapter.MenuItemClickListener {
    private static final String ACTION_SHOW_LICENSES = "licenses";
    private static final String ACTION_GIVE_FEEDBACK = "rate";
    private static final String PARAM_DIALOG = "dialog";

    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int GRID_COLUMNS_SINGLE = 1;

    @Inject MainPresenter presenter;
    @Inject AnimationCreator animationCreator;
    @Inject DoodleTextFormatter doodleTextFormatter;
    @BindView(R.id.img_loading) ImageView loadingView;
    @BindView(R.id.category_list) RecyclerView recyclerView;
    @BindView(R.id.search_bar) View searchBar;
    @BindView(R.id.img_tabi_back) View doodleBack;
    @BindView(R.id.img_tabi_front) View doodleFront;
    @BindView(R.id.txt_searched) TextView searchText;
    @BindView(R.id.search_bar_content) View searchBarContent;
    @BindView(R.id.ic_search) View searchIcon;
    @BindView(R.id.toolbar_tab_indicator) SearchTabPageIndicator toolbarIndicator;
    @BindView(R.id.fake_toolbar) View fakeToolbar;
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Lowest) float lowestSearchBarPosition;
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Highest) float highestSearchBarPosition;
    @BindDimen(R.dimen.Main_Margin_Greeting_Doodle_Top) float lowestDoodlePosition;
    @State boolean isDialogShown;
    @State int scrolledY;
    private float scrollPercent;
    private MainItemAdapter adapter;
    private int bigHeaderIndex;
    private int footerIndex;
    private boolean paused;
    private BlockingLayoutManager manager;
    private CompositeSubscription scrollSubscriptions;
    private CompositeSubscription animSubs;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);

        scrollSubscriptions = new CompositeSubscription();
        animSubs = new CompositeSubscription();
        manager = new BlockingLayoutManager(this, GRID_COLUMNS_NUMBER);

        recyclerView.setLayoutManager(manager);
        adapter = new MainItemAdapter(new ArrayList<>(), doodleTextFormatter, this);

        prepareMenuItems();
        prepareToolbar();
        recyclerView.setAdapter(adapter);

        scrollSubscriptions.add(RxRecyclerView.scrollEvents(recyclerView)
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .map(RecyclerViewScrollEvent::dy)
                                              .map(y -> y += scrolledY)
                                              .doOnNext(y -> scrolledY = y)
                                              .map(this::computePercentScrolled)
                                              .doOnNext(percent -> scrollPercent = percent)
                                              .subscribe(this::setAnimationState));
    }

    private float computePercentScrolled(int scrollPosition) {
        float distance = lowestSearchBarPosition - highestSearchBarPosition;

        float percent = (float) scrollPosition / distance;
        if (percent > 1) {
            percent = 0.99f;
        }

        return percent;
    }

    private void prepareToolbar() {
        // use static page indicator for fake hook to viewpager
        toolbarIndicator.setViewPager(new StaticPageIndicatorViewPager(
                this, getString(R.string.search_tab_plate), getString(R.string.search_tab_place)));
        toolbarIndicator.setCurrentItem(0);

        MarginProxy indicatorMarginManager = new MarginProxy(toolbarIndicator);
        indicatorMarginManager.setTopMargin(0);
    }

    private void prepareMenuItems() {
        List<MainListItem> items = new ArrayList<>();

        items.add(new MainListBigHeaderItem(getString(R.string.main_doodle_greeting),
                                            getString(R.string.main_doodle_caption)));
        bigHeaderIndex = 0;
        items.add(new MainListHeaderItem(getString(R.string.main_list_header_browse)));

        items.add(new MainListElementItem(getString(R.string.main_list_element_dol),
                                          getResources().getDrawable(R.drawable.vic_dol), "d", "dol"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_kuj),
                                          getResources().getDrawable(R.drawable.vic_kuj), "c", "kuj"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lod),
                                          getResources().getDrawable(R.drawable.vic_lod), "e", "lod"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lbl),
                                          getResources().getDrawable(R.drawable.vic_lbl), "l", "lbl"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lbu),
                                          getResources().getDrawable(R.drawable.vic_lbu), "f", "lbu"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_mal),
                                          getResources().getDrawable(R.drawable.vic_mal), "k", "mal"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_maz),
                                          getResources().getDrawable(R.drawable.vic_maz), "w", "maz"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_opo),
                                          getResources().getDrawable(R.drawable.vic_opo), "o", "opo"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_rze),
                                          getResources().getDrawable(R.drawable.vic_rze), "r", "rze"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_bie),
                                          getResources().getDrawable(R.drawable.vic_bie), "b", "bie"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_pom),
                                          getResources().getDrawable(R.drawable.vic_pom), "g", "pom"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_sla),
                                          getResources().getDrawable(R.drawable.vic_sla), "s", "sla"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_swi),
                                          getResources().getDrawable(R.drawable.vic_swi), "t", "swi"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_war),
                                          getResources().getDrawable(R.drawable.vic_war), "n", "war"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_wie),
                                          getResources().getDrawable(R.drawable.vic_wie), "p", "wie"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_zah),
                                          getResources().getDrawable(R.drawable.vic_zah), "z", "zah"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_slu),
                                          getResources().getDrawable(R.drawable.vic_slu), "h", "slu"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_woj),
                                          getResources().getDrawable(R.drawable.vic_woj), "u", "woj"));

        items.add(new MainListHeaderItem(getString(R.string.main_list_header_about_app)));

        items.add(new MainListElementItem(getString(R.string.main_list_element_licenses),
                                          getResources().getDrawable(R.drawable.vic_licenses), null, ACTION_SHOW_LICENSES));
        items.add(new MainListElementItem(getString(R.string.main_list_element_rate),
                                          getResources().getDrawable(R.drawable.vic_rate), null, ACTION_GIVE_FEEDBACK));

        items.add(new MainListFooterItem(getString(R.string.main_loading)));
        footerIndex = items.size() - 1;

        final List<Integer> spannedIndexes = new ArrayList<>();
        spannedIndexes.add(bigHeaderIndex);
        spannedIndexes.add(footerIndex);
        for (int i = 0; i < items.size(); i++) {
            MainListItem item = items.get(i);
            if (item instanceof MainListHeaderItem) {
                spannedIndexes.add(i);
            }
        }

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                if (spannedIndexes.contains(position)) {
                    return GRID_COLUMNS_NUMBER;
                } else {
                    return GRID_COLUMNS_SINGLE;
                }
            }
        });

        adapter.swapItems(items);
    }

    private void setAnimationState(float percent) {
        if (paused) {
            return;
        }

        percent = 1 - percent;
        float moveSearchTo = (lowestSearchBarPosition - highestSearchBarPosition)
                * percent + highestSearchBarPosition;

        float diff = lowestDoodlePosition - lowestSearchBarPosition;
        float moveDoodleTo = moveSearchTo + diff;

        doodleBack.setY(moveDoodleTo);
        doodleFront.setY(moveDoodleTo);

        searchBar.setY(moveSearchTo);
        searchBarContent.setY(moveSearchTo);

        doodleBack.setAlpha(percent);
        doodleFront.setAlpha(percent);
    }

    @Override protected void onResume() {
        super.onResume();
        paused = false;
        fakeToolbar.setVisibility(View.INVISIBLE);
        presenter.refreshView();

        searchBar.post(this::animateSearchBack);
    }

    private void animateSearchBack() {
        float currentBarPos = searchBar.getY();
        float targetY;
        if (scrollPercent > 0) {
            targetY = highestSearchBarPosition + (lowestSearchBarPosition - highestSearchBarPosition) * (1 - scrollPercent);
        } else {
            targetY = lowestSearchBarPosition;
        }

        AnimationCreator.SearchAnimator anim = animationCreator.getSearchAnimator();

        AnimatorSet searchAnim = new AnimatorSet();
        AnimatorSet.Builder builder;
        builder = searchAnim.play(anim.createMoveAnim(searchBar, currentBarPos, targetY))
                            .with(anim.createScaleDownAnim(searchBar))
                            .with(anim.createMoveAnim(searchBarContent, currentBarPos, targetY))
                            .with(anim.createFadeInAnim(searchBarContent))
                            .with(anim.createFadeInAnim(searchIcon));

        if (scrollPercent == 0) {
            builder.with(anim.createFadeInAnim(doodleBack, 0.5f, true))
                   .with(anim.createFadeInAnim(doodleFront, 0.5f, true));
        }

        if (currentBarPos == 0) {
            searchAnim.start();
        }
    }

    @Override protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        RxUtil.unsubscribe(scrollSubscriptions);
        presenter.detachView();
    }

    @OnClick(R.id.search_bar) public void onSearchBarClicked() {
        presenter.goToSearch();
    }

    public void showFeedbackDialog(View card) {
        Intent feedbackIntent = new Intent(this, FeedbackTypeActivity.class);
        List<Pair<View, String>> transitions = new ArrayList<>();
        transitions.add(Pair.create(card, getString(R.string.trans_main_card_bg)));
        // status and nav bar
        View statusBar = findViewById(android.R.id.statusBarBackground);
        if (statusBar != null) {
            transitions.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        View navigationBar = findViewById(android.R.id.navigationBarBackground);
        if (navigationBar != null) {
            transitions.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        Pair<View, String>[] transitionsArray = transitions.toArray(new Pair[transitions.size()]);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, transitionsArray);
        startActivity(feedbackIntent, transitionActivityOptions.toBundle());
    }

    @Override public void showGreetingCaption() {
        showCaption(getString(R.string.main_doodle_caption));
    }

    @Override public void showFeedbackCaption() {
        showCaption(getString(R.string.main_doodle_caption_feedback));
    }

    private void showCaption(String caption) {
        adapter.refreshItem(new MainListBigHeaderItem(getString(R.string.main_doodle_greeting),
                                                      caption), bigHeaderIndex);
    }

    @Override public void showVersion(String versionName) {
        adapter.refreshItem(new MainListFooterItem(getString(R.string.main_version, versionName)), footerIndex);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.INVISIBLE);
    }

    @Override public void goToSearch(String phrase) {
        AnimatorSet searchAnim = new AnimatorSet();
        AnimationCreator.SearchAnimator anim = animationCreator.getSearchAnimator();
        AnimatorSet.Builder builder;
        builder = searchAnim.play(anim.createMoveAnim(searchBar, searchBar.getY(), highestSearchBarPosition))
                            .with(anim.createScaleUpAnim(searchBar))
                            .with(anim.createFadeOutAnim(searchIcon))
                            .with(anim.createMoveAnim(searchBarContent, searchBarContent.getY(),
                                                      highestSearchBarPosition));

        if (scrollPercent == 0) {
            builder.with(anim.createFadeOutAnim(doodleFront, 0.5f, false))
                   .with(anim.createFadeOutAnim(doodleBack, 0.5f, false));
        }

        animSubs.add(RxAnimator.animationStart(searchAnim).subscribe(a -> manager.lockScroll()));
        animSubs.add(RxAnimator.animationEnd(searchAnim)
                               .subscribe(a -> {
                                   manager.unlockScroll();
                                   fakeToolbar.setVisibility(View.VISIBLE);
                                   startSearchActivity(phrase);
                               }));

        searchAnim.start();
    }

    private void startSearchActivity(String phrase) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        boolean shouldShowKeyboard = (phrase == null || phrase.equals(""));
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, shouldShowKeyboard);
        intent.putExtra(SearchActivity.PARAM_SEARCH_TEXT, phrase);

        List<Pair<View, String>> transitions = new ArrayList<>();
        View searchInput = fakeToolbar.findViewById(R.id.txt_search_wrap);
        transitions.add(Pair.create(searchInput, getString(R.string.trans_search_input)));
        transitions.add(Pair.create(toolbarIndicator, getString(R.string.trans_tab_indicator)));
        // status and nav bar
        View statusBar = findViewById(android.R.id.statusBarBackground);
        if (statusBar != null) {
            transitions.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        View navigationBar = findViewById(android.R.id.navigationBarBackground);
        if (navigationBar != null) {
            transitions.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        Pair<View, String>[] transitionsArray = transitions.toArray(new Pair[transitions.size()]);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, transitionsArray);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    public void goToAboutAppPage(View card) {
        Intent intent = new Intent(this, AboutAppActivity.class);

        List<Pair<View, String>> transitions = new ArrayList<>();
        transitions.add(Pair.create(card, getString(R.string.trans_main_card_bg)));
        // status and nav bar
        View statusBar = findViewById(android.R.id.statusBarBackground);
        if (statusBar != null) {
            transitions.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        View navigationBar = findViewById(android.R.id.navigationBarBackground);
        if (navigationBar != null) {
            transitions.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        Pair<View, String>[] transitionsArray = transitions.toArray(new Pair[transitions.size()]);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, transitionsArray);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    @Override public void showDemoGreeting() {
        if (!isDialogShown) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(PARAM_DIALOG);
            if (prev == null) {
                MessageDialog demoDialog = MessageDialog
                        .newInstance(getString(R.string.english_greeting_title),
                                     getString(R.string.english_greeting_body),
                                     getString(R.string.english_greeting_confirm));
                demoDialog.setOnClickListener(v -> isDialogShown = false);
                demoDialog.show(ft, PARAM_DIALOG);
                isDialogShown = true;
            }
        }
    }

    @Override public void onMenuItemClicked(View target, MainListElementItem item) {
        if (ACTION_SHOW_LICENSES.equals(item.getCategoryKey())) {
            goToAboutAppPage(target);
        } else if (ACTION_GIVE_FEEDBACK.equals(item.getCategoryKey())) {
            showFeedbackDialog(target);
        } else {
            Timber.d("Menu item clicked has literal as action");
            goToCategoryView(target, item.getCategoryKey());
        }
    }

    private void goToCategoryView(View view, String categoryKey) {
        Intent categoryIntent = new Intent(this, CategoryActivity.class);
        categoryIntent.putExtra(CategoryActivity.EXTRA_CATEGORY_KEY, categoryKey);

        List<Pair<View, String>> transitions = new ArrayList<>();
        transitions.add(Pair.create(view, getString(R.string.trans_main_card_bg)));
        // status and nav bar
        View statusBar = findViewById(android.R.id.statusBarBackground);
        if (statusBar != null) {
            transitions.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        View navigationBar = findViewById(android.R.id.navigationBarBackground);
        if (navigationBar != null) {
            transitions.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        Pair<View, String>[] transitionsArray = transitions.toArray(new Pair[transitions.size()]);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, transitionsArray);
        startActivity(categoryIntent, transitionActivityOptions.toBundle());
    }
}
