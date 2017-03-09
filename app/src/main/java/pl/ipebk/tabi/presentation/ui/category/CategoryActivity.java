package pl.ipebk.tabi.presentation.ui.category;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.chromeTabs.CustomTabActivityHelper;
import pl.ipebk.tabi.presentation.ui.details.DetailsCategoryActivity;
import pl.ipebk.tabi.presentation.ui.search.RandomTextProvider;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RxRecyclerViewExtension;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class CategoryActivity extends BaseActivity implements CategoryMvpView {
    public static final String EXTRA_CATEGORY_KEY = "extra_category_key";

    @Inject CategoryPresenter presenter;
    @Inject LicensePlateFinder plateFinder;
    @Inject PlaceLocalizationHelper localizationHelper;
    @Inject RandomTextProvider randomTextProvider;
    @Inject PlaceAndPlateFactory placeFactory;
    @Inject CustomTabActivityHelper chromeTabHelper;
    @Inject AnimationCreator animationCreator;

    @BindView(R.id.toolbar_parent) View toolbar;
    @BindView(R.id.content_container) View contentContainer;
    @BindView(R.id.background_layout) View background;
    @BindView(R.id.place_list) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.txt_title) TextView title;
    @BindView(R.id.txt_plate) TextView plateStart;
    @BindDimen(R.dimen.Toolbar_Elevation) float toolbarElevation;
    @BindDimen(R.dimen.Category_Toolbar_Limit) float toolbarLimit;

    private String categoryKey;
    private CategoryPlaceItemAdapter adapter;
    private Subscription scrollSubscription;
    private Subscription delayedStartSub;
    private boolean transitionUsed;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        try {
            categoryKey = getIntent().getStringExtra(EXTRA_CATEGORY_KEY);
            if (categoryKey == null) {
                throw new NullPointerException("Category key is null");
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not initialize CategoryActivity: category key was not passed");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTransition();
        } else {
            transitionUsed = false;
        }

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(getAdapter());

        scrollSubscription = RxRecyclerViewExtension.totalScrollEvents(recyclerView)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .map(RecyclerViewTotalScrollEvent::totalScrollY)
                                                    .subscribe(this::raiseToolbar);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition() {
        AnimationCreator.CategoryAnimator anim = animationCreator.getCategoryAnimator();

        Transition enterTransition = anim.createBgFadeInTransition();
        enterTransition.addListener(new SimpleTransitionListener.Builder()
                                            .withOnStartAction(t -> {
                                                transitionUsed = true;
                                                contentContainer.setVisibility(View.INVISIBLE);
                                                toolbar.setVisibility(View.INVISIBLE);
                                                presenter.initCategory(categoryKey);
                                            })
                                            .withOnEndAction(t -> {
                                                contentContainer.setVisibility(View.VISIBLE);
                                                toolbar.setVisibility(View.VISIBLE);
                                                background.setVisibility(View.GONE);
                                            })
                                            .build());
        getWindow().setEnterTransition(enterTransition);

        Transition returnTransition = anim.createBgFadeOutTransition();
        getWindow().setReturnTransition(returnTransition);

        anim.alterSharedTransition(getWindow().getSharedElementEnterTransition());
        anim.alterSharedTransition(getWindow().getSharedElementReturnTransition());
    }

    private void raiseToolbar(int scrolled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scrolled < toolbarLimit) {
                toolbar.setElevation(0);
            } else {
                toolbar.setElevation(toolbarElevation);
            }
        }
    }

    @Override protected void onStart() {
        super.onStart();
        chromeTabHelper.bindCustomTabsService(this);

        delayedStartSub = Observable.just(null)
                                    .delay(100, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(n -> {
                                        if (!transitionUsed) {
                                            Timber.d("Transition not used in activity. starting delayed action");
                                            presenter.initCategory(categoryKey);
                                        }
                                    });
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        RxUtil.unsubscribe(scrollSubscription);
        RxUtil.unsubscribe(delayedStartSub);
        presenter.detachView();
    }

    @Override protected void onStop() {
        super.onStop();
        chromeTabHelper.unbindCustomTabsService(this);
    }

    @OnClick(R.id.btn_back) public void onBack() {
        onBackPressed();
    }

    /**
     * Test methods to be replaced with mocks
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    public CategoryPlaceItemAdapter getAdapter() {
        if (adapter == null) {

            adapter = new CategoryPlaceItemAdapter(null, this, randomTextProvider, placeFactory);
            adapter.setType(SearchType.LICENSE_PLATE);
            adapter.setPlaceClickListener((v, id, plate, sType, pType, pos) -> presenter.loadPlaceDetails(v, id, plate, pos));
            adapter.setMoreInfoClickListener(this::launchUri);
        }

        return adapter;
    }

    private void launchUri(String url) {
        int primaryColor = getResources().getColor(R.color.white);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(primaryColor);
        intentBuilder.enableUrlBarHiding();
        intentBuilder.setStartAnimations(this, 0, 0);
        intentBuilder.setExitAnimations(this, 0, 0);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        chromeTabHelper.openCustomTab(this, customTabsIntent, Uri.parse(url), ((activity, uri) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }));
    }

    @Override public void showCategoryName(String name) {
        title.setText(name);
    }

    @Override public void showCategoryPlate(String plate) {
        plateStart.setText(plate);
    }

    @Override public void showCategoryInfo(CategoryInfo info) {
        getAdapter().setCategoryInfo(info);

        chromeTabHelper.mayLaunchUrl(Uri.parse(info.link()), null, null);
    }

    @Override public void showPlates(Cursor cursor) {
        getAdapter().changeCursor(cursor);
        int placesFound = cursor.getCount();
        Timber.d("Number of places found: %d", placesFound);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override public void goToDetails(View view, AggregateId placeId, String searchedPlate, String categoryName,
                                      String categoryPlate, int position) {
        Intent intent = new Intent(this, DetailsCategoryActivity.class);
        intent.putExtra(DetailsCategoryActivity.PARAM_PLACE_ID, placeId.getValue());
        intent.putExtra(DetailsCategoryActivity.PARAM_SEARCHED_PLATE, searchedPlate);
        intent.putExtra(DetailsCategoryActivity.PARAM_CATEGORY_NAME, categoryName);
        intent.putExtra(DetailsCategoryActivity.PARAM_CATEGORY_PLATE, categoryPlate);
        intent.putExtra(DetailsCategoryActivity.PARAM_ADAPTER_POSITION, position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<Pair<View, String>> transitions = new ArrayList<>();
            // shared elements
            transitions.add(Pair.create(view.findViewById(R.id.wrp_row), SharedTransitionNaming.getName(getString(R.string.trans_row_background), position)));
            transitions.add(Pair.create(view.findViewById(R.id.txt_voivodeship), SharedTransitionNaming.getName(getString(R.string.trans_voivodeship_name), position)));
            transitions.add(Pair.create(view.findViewById(R.id.txt_powiat), SharedTransitionNaming.getName(getString(R.string.trans_powiat_name), position)));
            transitions.add(Pair.create(view.findViewById(R.id.txt_place_name), SharedTransitionNaming.getName(getString(R.string.trans_place_name), position)));
            transitions.add(Pair.create(view.findViewById(R.id.ic_row), SharedTransitionNaming.getName(getString(R.string.trans_place_icon), position)));
            transitions.add(Pair.create(view.findViewById(R.id.txt_plate), SharedTransitionNaming.getName(getString(R.string.trans_place_plate), position)));
            // status and nav bar
            View statusBar = findViewById(android.R.id.statusBarBackground);
            if(statusBar !=null) {
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
}
