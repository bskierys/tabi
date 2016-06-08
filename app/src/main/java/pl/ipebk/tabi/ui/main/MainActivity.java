package pl.ipebk.tabi.ui.main;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.search.SearchActivity;
import pl.ipebk.tabi.utils.AnimationHelper;
import pl.ipebk.tabi.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.utils.rxbinding.RxAnimator;
import pl.ipebk.tabi.utils.rxbinding.RxRecyclerViewExtension;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, MainItemAdapter.MenuItemClickListener {
    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int GRID_COLUMNS_SINGLE = 1;
    private static final int GRID_HEADER_POSITION = 0;

    @Inject MainPresenter presenter;
    @Inject AnimationHelper animationHelper;
    @Bind(R.id.img_loading) ImageView loadingView;
    @Bind(R.id.category_list) RecyclerView recyclerView;
    @Bind(R.id.search_bar) View searchBar;
    @Bind(R.id.img_tabi_back) View doodleBack;
    @Bind(R.id.img_tabi_front) View doodleFront;
    @Bind(R.id.txt_searched) TextView searchText;
    @Bind(R.id.search_bar_content) View searchBarContent;
    @Bind(R.id.ic_search) View searchIcon;
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Lowest) float lowestSearchBarPosition;
    @BindDimen(R.dimen.Main_Margin_SearchBar_Top_Highest) float highestSearchBarPosition;

    private float scrollPercent;
    private MainItemAdapter adapter;
    private BlockingLayoutManager manager;

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

        RxRecyclerView.scrollEvents(recyclerView)
                      .observeOn(AndroidSchedulers.mainThread())
                      .map(RecyclerViewScrollEvent::dy)
                      .doOnNext(dy -> doodleBack.setY(doodleBack.getY() - dy))
                      .doOnNext(dy -> doodleFront.setY(doodleFront.getY() - dy))
                      .subscribe();

        RxRecyclerViewExtension.totalScrollEvents(recyclerView)
                               .observeOn(AndroidSchedulers.mainThread())
                               .map(RecyclerViewTotalScrollEvent::totalScrollY)
                               .map(this::computePercentScrolled)
                               .doOnNext(percent -> scrollPercent = percent)
                               .subscribe(this::setAnimationState);
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

        float targetY;
        if (scrollPercent > 0) {
            targetY = highestSearchBarPosition + (lowestSearchBarPosition - highestSearchBarPosition) * scrollPercent;
        } else {
            targetY = lowestSearchBarPosition;
        }

        float currentY = searchBar.getY();

        AnimatorSet searchAnim = new AnimatorSet();
        searchAnim.play(animationHelper.getSearchAnimator().createMoveAnim(searchBar, currentY, targetY))
                  .with(animationHelper.getSearchAnimator().createScaleDownAnim(searchBar))
                  .with(animationHelper.getSearchAnimator().createMoveAnim(searchBarContent, currentY, targetY))
                  .with(animationHelper.getSearchAnimator().createFadeInAnim(searchBarContent))
                  .with(animationHelper.getSearchAnimator().createFadeInAnim(searchIcon));

        if (scrollPercent > 0) {
            searchAnim.start();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        presenter.detachView();
    }

    @OnClick(R.id.search_bar) public void onSearchBarClicked() {
        presenter.goToSearch();
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.INVISIBLE);
    }

    @Override public void showCategories(List<MainListItem> categories) {
        final List<Integer> headerIndexes = new ArrayList<>();
        headerIndexes.add(GRID_HEADER_POSITION);
        for (int i = 0; i < categories.size(); i++) {
            MainListItem item = categories.get(i);
            if (item instanceof MainListHeaderItem) {
                headerIndexes.add(i + 1);
            }
        }

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                if (headerIndexes.contains(position)) {
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
        searchAnim.play(animationHelper.getSearchAnimator().createMoveAnim(searchBar, searchBar.getY(), highestSearchBarPosition))
                  .with(animationHelper.getSearchAnimator().createScaleUpAnim(searchBar))
                  .with(animationHelper.getSearchAnimator().createFadeOutAnim(searchIcon))
                  .with(animationHelper.getSearchAnimator().createMoveAnim(searchBarContent, searchBarContent.getY(),
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

    @Override public void prompt(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override public void onMenuItemClicked(String action) {
        presenter.menuItemClicked(action);
    }
}
