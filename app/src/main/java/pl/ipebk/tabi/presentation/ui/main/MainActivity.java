package pl.ipebk.tabi.presentation.ui.main;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
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
import java.util.concurrent.TimeUnit;

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
import pl.ipebk.tabi.presentation.ui.feedback.FeedbackTypeActivity;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
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
    private static final int SCROLL_SAMPLE_PERIOD = 500;

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
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Lowest) float lowestSearchBarPosition;
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Highest) float highestSearchBarPosition;
    @BindDimen(R.dimen.Main_Margin_Greeting_Doodle_Top) float lowestDoodlePosition;

    private float scrollPercent;
    private MainItemAdapter adapter;
    private int bigHeaderIndex;
    private int footerIndex;
    private BlockingLayoutManager manager;
    private CompositeSubscription scrollSubscriptions;
    @State boolean isDialogShown;
    @State int scrolledY;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);

        scrollSubscriptions = new CompositeSubscription();
        manager = new BlockingLayoutManager(this, GRID_COLUMNS_NUMBER);

        recyclerView.setLayoutManager(manager);
        adapter = new MainItemAdapter(new ArrayList<>(), doodleTextFormatter, this, animationCreator);

        prepareMenuItems();
        recyclerView.setAdapter(adapter);
        scrollSubscriptions.add(RxRecyclerView.scrollEvents(recyclerView)
                                              .sample(SCROLL_SAMPLE_PERIOD, TimeUnit.MILLISECONDS)
                                              .subscribe(event -> {
                                                  int lastPosition = manager.findLastCompletelyVisibleItemPosition();
                                                  Timber.d("Visible position: %d", lastPosition);
                                                  adapter.setLastAnimatedItem(lastPosition);
                                              }));

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
        percent = 1 - percent;

        return percent;
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
        presenter.refreshView();

        float targetY;
        if (scrollPercent > 0) {
            targetY = highestSearchBarPosition + (lowestSearchBarPosition - highestSearchBarPosition) * scrollPercent;
        } else {
            targetY = lowestSearchBarPosition;
        }

        float currentY = searchBar.getY();

        AnimatorSet searchAnim = new AnimatorSet();
        searchAnim.play(animationCreator.getSearchAnimator().createMoveAnim(searchBar, currentY, targetY))
                  .with(animationCreator.getSearchAnimator().createScaleDownAnim(searchBar))
                  .with(animationCreator.getSearchAnimator().createMoveAnim(searchBarContent, currentY, targetY))
                  .with(animationCreator.getSearchAnimator().createFadeInAnim(searchBarContent))
                  .with(animationCreator.getSearchAnimator().createFadeInAnim(searchIcon));

        if (scrollPercent > 0) {
            searchAnim.start();
        }
    }

    @Override protected void onPause() {
        super.onPause();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } else {
            startActivity(feedbackIntent);
        }
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
        searchAnim.play(animationCreator.getSearchAnimator().createMoveAnim(searchBar, searchBar.getY(),
                                                                            highestSearchBarPosition))
                  .with(animationCreator.getSearchAnimator().createScaleUpAnim(searchBar))
                  .with(animationCreator.getSearchAnimator().createFadeOutAnim(searchIcon))
                  .with(animationCreator.getSearchAnimator().createMoveAnim(searchBarContent, searchBarContent.getY(),
                                                                            highestSearchBarPosition));

        RxAnimator.animationStart(searchAnim).subscribe(a -> manager.lockScroll());
        RxAnimator.animationEnd(searchAnim).doOnNext(a -> manager.unlockScroll())
                  .map(a -> new Intent(MainActivity.this, SearchActivity.class))
                  .doOnNext(intent -> {
                      boolean shouldShowKeyboard = (phrase == null || phrase.equals(""));
                      intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, shouldShowKeyboard);
                  })
                  .doOnNext(intent -> intent.putExtra(SearchActivity.PARAM_SEARCH_TEXT, phrase))
                  .subscribe(MainActivity.this::startActivity);

        searchAnim.start();
    }

    public void goToAboutAppPage(View card) {
        Intent intent = new Intent(this, AboutAppActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } else {
            startActivity(intent);
        }
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

    @Override public void showResponseToFeedback(String response) {
        if (!isDialogShown) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(PARAM_DIALOG);
            if (prev == null) {
                MessageDialog demoDialog = MessageDialog
                        .newInstance(getString(R.string.main_feedback_response_title), response,
                                     getString(R.string.main_feedback_response_confirm));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        } else {
            startActivity(categoryIntent);
        }
    }
}
