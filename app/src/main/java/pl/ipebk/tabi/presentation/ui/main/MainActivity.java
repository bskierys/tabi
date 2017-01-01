package pl.ipebk.tabi.presentation.ui.main;

import android.animation.AnimatorSet;
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
import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.about.AboutAppActivity;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.RxAnimator;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RxRecyclerViewExtension;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, MainItemAdapter.MenuItemClickListener {
    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int GRID_COLUMNS_SINGLE = 1;

    @Inject MainPresenter presenter;
    @Inject AnimationCreator animationCreator;
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
    private BlockingLayoutManager manager;
    private FeedbackDialog feedbackDialog;
    private String feedbackApiKey;
    private CompositeSubscription scrollSubscriptions;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        Timber.e("Problem computing bounds");

        presenter.attachView(this);

        manager = new BlockingLayoutManager(this, GRID_COLUMNS_NUMBER);

        recyclerView.setLayoutManager(manager);
        adapter = new MainItemAdapter(new ArrayList<>(), this, this);
        recyclerView.setAdapter(adapter);
        feedbackApiKey = getString(R.string.feedback_api_key);
        feedbackDialog = new FeedbackDialog(this, feedbackApiKey);
        prepareFeedbackDialog(feedbackDialog);
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

    private void prepareFeedbackDialog(FeedbackDialog dialog) {
        dialog.setDebug(BuildConfig.DEBUG);

        FeedbackSettings feedbackSettings = new FeedbackSettings();
        //SUBMIT-CANCEL BUTTONS
        feedbackSettings.setCancelButtonText(getString(R.string.main_feedback_cancel));
        feedbackSettings.setSendButtonText(getString(R.string.main_feedback_send));

        //DIALOG TEXT
        feedbackSettings.setText(getString(R.string.main_feedback_body));
        feedbackSettings.setYourComments("");
        feedbackSettings.setTitle(getString(R.string.main_feedback_title));

        //TOAST MESSAGE
        feedbackSettings.setToast(getString(R.string.main_feedback_done));

        //RADIO BUTTONS
        feedbackSettings.setBugLabel(getString(R.string.main_feedback_bug));
        feedbackSettings.setIdeaLabel(getString(R.string.main_feedback_idea));
        feedbackSettings.setQuestionLabel(getString(R.string.main_feedback_question));

        //RADIO BUTTONS ORIENTATION AND GRAVITY
        feedbackSettings.setOrientation(LinearLayout.VERTICAL);
        feedbackSettings.setGravity(Gravity.LEFT);

        //DEVELOPER REPLIES
        feedbackSettings.setReplyTitle(getString(R.string.main_feedback_reply_title));
        feedbackSettings.setReplyCloseButtonText(getString(R.string.main_feedback_reply_button));
        feedbackSettings.setReplyRateButtonText(getString(R.string.main_feedback_reply_rate));

        feedbackSettings.setModal(true);

        dialog.setSettings(feedbackSettings);
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
        feedbackDialog.dismiss();
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
        Timber.d("Showing feedback dialog for API key: %s", feedbackApiKey);
        feedbackDialog.show();
    }

    @Override public void showCaption(String caption) {
        adapter.setCaption(caption);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.INVISIBLE);
    }

    @Override public void showCategories(List<MainListItem> categories) {
        int footerPosition = MainItemAdapter.getFooterPosition(categories);
        int bigHeaderPosition = MainItemAdapter.getBigHeaderPosition();

        // TODO: 2017-01-01 move to helper
        // TODO: 2017-01-01 more generic text styles in views
        final List<Integer> spannedIndexes = new ArrayList<>();
        spannedIndexes.add(bigHeaderPosition);
        spannedIndexes.add(footerPosition);
        for (int i = 0; i < categories.size(); i++) {
            MainListItem item = categories.get(i);
            if (item instanceof MainListHeaderItem) {
                spannedIndexes.add(i + 1);
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

        adapter.swapItems(categories);
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

    @Override public void onMenuItemClicked(String action) {
        presenter.showCategoryForAction(action);
    }
}
