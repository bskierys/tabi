package pl.ipebk.tabi.presentation.ui.main;

import android.animation.AnimatorSet;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.presentation.ui.about.AboutAppActivity;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.feedback.FeedbackTypeActivity;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.RxAnimator;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RxRecyclerViewExtension;
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
    @Inject FeedbackClient feedbackClient;
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

    private float scrollPercent;
    private MainItemAdapter adapter;
    private int bigHeaderIndex;
    private int footerIndex;
    private BlockingLayoutManager manager;
    private String feedbackApiKey;
    private CompositeSubscription scrollSubscriptions;
    @State boolean isDemoDialogShown;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        Timber.e("Problem computing bounds");

        presenter.attachView(this);

        manager = new BlockingLayoutManager(this, GRID_COLUMNS_NUMBER);

        recyclerView.setLayoutManager(manager);
        adapter = new MainItemAdapter(new ArrayList<>(), doodleTextFormatter, this);
        prepareMenuItems();
        recyclerView.setAdapter(adapter);
        feedbackApiKey = getString(R.string.feedback_api_key);
        feedbackClient.init(feedbackApiKey);
        scrollSubscriptions = new CompositeSubscription();

        scrollSubscriptions.add(RxRecyclerView.scrollEvents(recyclerView)
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .map(RecyclerViewScrollEvent::dy)
                                              .doOnNext(dy -> doodleBack.setY(doodleBack.getY() - dy))
                                              .doOnNext(dy -> doodleFront.setY(doodleFront.getY() - dy))
                                              .subscribe());

        scrollSubscriptions.add(RxRecyclerViewExtension.totalScrollEvents(recyclerView)
                                                       .observeOn(AndroidSchedulers.mainThread())
                                                       .map(RecyclerViewTotalScrollEvent::totalScrollY)
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

        items.add(new MainListElementItem(getString(R.string.main_list_element_dolnoslaskie),
                                          getResources().getDrawable(R.drawable.vic_dolnoslaskie), "d"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_kujawskopomorskie),
                                          getResources().getDrawable(R.drawable.vic_kujawskopomorskie), "c"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lodzkie),
                                          getResources().getDrawable(R.drawable.vic_lodzkie), "e"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lubelskie),
                                          getResources().getDrawable(R.drawable.vic_lubelskie), "l"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_lubuskie),
                                          getResources().getDrawable(R.drawable.vic_lubuskie), "f"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_malopolskie),
                                          getResources().getDrawable(R.drawable.vic_malopolskie), "k"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_mazowieckie),
                                          getResources().getDrawable(R.drawable.vic_mazowieckie), "w"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_opolskie),
                                          getResources().getDrawable(R.drawable.vic_opolskie), "o"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_podkarpackie),
                                          getResources().getDrawable(R.drawable.vic_podkarpackie), "r"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_podlaskie),
                                          getResources().getDrawable(R.drawable.vic_podlaskie), "b"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_pomorskie),
                                          getResources().getDrawable(R.drawable.vic_pomorskie), "g"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_slaskie),
                                          getResources().getDrawable(R.drawable.vic_slaskie), "s"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_swietokrzyskie),
                                          getResources().getDrawable(R.drawable.vic_swietokrzyskie), "t"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_warminskomazurskie),
                                          getResources().getDrawable(R.drawable.vic_warminskomazurskie), "n"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_wielkopolskie),
                                          getResources().getDrawable(R.drawable.vic_wielkopolskie), "p"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_zachodniopomorskie),
                                          getResources().getDrawable(R.drawable.vic_zachodniopomorskie), "z"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_sluzbybezpieczenstwa),
                                          getResources().getDrawable(R.drawable.vic_sluzbybezpieczenstwa), "h"));
        items.add(new MainListElementItem(getString(R.string.main_list_element_tablicewojskowe),
                                          getResources().getDrawable(R.drawable.vic_tablicewojskowe), "u"));

        items.add(new MainListHeaderItem(getString(R.string.main_list_header_about_app)));

        items.add(new MainListElementItem(getString(R.string.main_list_element_licenses),
                                          getResources().getDrawable(R.drawable.vic_licenses), ACTION_SHOW_LICENSES));
        items.add(new MainListElementItem(getString(R.string.main_list_element_rate),
                                          getResources().getDrawable(R.drawable.vic_rate), ACTION_GIVE_FEEDBACK));

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
                * scrollPercent
                + highestSearchBarPosition;

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

    @Override public void showFeedbackDialog() {
        Intent feedbackIntent = new Intent(this, FeedbackTypeActivity.class);
        startActivity(feedbackIntent);
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
        // TODO: 2016-06-03 different animation when tile is clicked
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

    @Override public void goToAboutAppPage() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    @Override public void showDemoGreeting() {
        if(!isDemoDialogShown) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(PARAM_DIALOG);
            if (prev == null) {
                MessageDialog demoDialog = MessageDialog
                        .newInstance(getString(R.string.english_greeting_title),
                                     getString(R.string.english_greeting_body),
                                     getString(R.string.english_greeting_confirm));
                demoDialog.setOnClickListener(v -> isDemoDialogShown = false);
                demoDialog.show(ft, PARAM_DIALOG);
                isDemoDialogShown = true;
            }
        }
    }

    @Override public void onMenuItemClicked(String action) {
        if (ACTION_SHOW_LICENSES.equals(action)) {
            goToAboutAppPage();
        } else if (ACTION_GIVE_FEEDBACK.equals(action)) {
            showFeedbackDialog();
        } else {
            Timber.d("Menu item clicked has literal as action");
            goToSearch(action);
        }
    }
}
