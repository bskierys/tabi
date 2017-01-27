package pl.ipebk.tabi.presentation.ui.category;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.search.PlaceFragmentEventListener;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RxRecyclerViewExtension;
import pl.ipebk.tabi.readmodel.LicensePlateFinder;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CategoryActivity extends BaseActivity implements CategoryMvpView {
    public static final String EXTRA_CATEGORY_KEY = "extra_category_key";

    @Inject CategoryPresenter presenter;
    @Inject LicensePlateFinder plateFinder;
    @Inject PlaceLocalizationHelper localizationHelper;

    @BindView(R.id.toolbar_parent) View toolbar;
    @BindView(R.id.place_list) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;
    @BindView(R.id.txt_title) TextView title;
    @BindView(R.id.txt_plate) TextView plateStart;
    @BindDimen(R.dimen.Toolbar_Elevation) float toolbarElevation;
    @BindDimen(R.dimen.Category_Toolbar_Limit) float toolbarLimit;

    private String categoryKey;
    Subscription searchSubscription;
    CategoryPlaceItemAdapter adapter;
    Subscription scrollSubscription;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        try {
            categoryKey = getIntent().getStringExtra(EXTRA_CATEGORY_KEY);
            if (categoryKey == null) {
                throw new NullPointerException("Category key is null");
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not initialize CategoryActivity: category key was not passed");
        }

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(getAdapter());

        title.setText(localizationHelper.formatCategory(categoryKey));
        plateStart.setText(localizationHelper.getCategoryPlate(categoryKey));

        searchSubscription = Observable
                .just(categoryKey)
                .map(localizationHelper::getCategoryPlate)
                .subscribeOn(Schedulers.io())
                .flatMap(plate -> plateFinder.findPlacesForPlateStart(plate, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showSearchResults,
                           e -> Timber.e(e, "Error during searching for places", e));

        scrollSubscription = RxRecyclerViewExtension.totalScrollEvents(recyclerView)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .map(RecyclerViewTotalScrollEvent::totalScrollY)
                                                    .subscribe(this::raiseToolbar);
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

    @Override protected void onDestroy() {
        super.onDestroy();

        RxUtil.unsubscribe(searchSubscription);
        RxUtil.unsubscribe(scrollSubscription);
        presenter.detachView();
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
            CategoryPlaceItemAdapter.BigHeader header =
                    new CategoryPlaceItemAdapter.BigHeader(localizationHelper.getCategoryTitle(categoryKey),
                                                           localizationHelper.getCategoryBody(categoryKey),
                                                           localizationHelper.getCategoryLink(categoryKey),
                                                           getIconForKey(categoryKey));

            adapter = new CategoryPlaceItemAdapter(null, this, header);
            adapter.setType(SearchType.LICENSE_PLATE);
            adapter.setEventListener(new PlaceFragmentEventListener() {
                @Override public void onPlaceItemClicked(AggregateId placeId, String plateClicked, SearchType type, PlaceListItemType itemType) {

                }

                @Override public void onHeaderClicked(int eventId) {

                }

                @Override public void onFragmentViewCreated(SearchType type) {

                }
            });
        }

        return adapter;
    }

    private Drawable getIconForKey(String key) {
        String resourceName = "vic_" + key;
        Drawable categoryIcon;
        try {
            int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            categoryIcon = getResources().getDrawable(resourceId);
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Could not found resource for name: %s", resourceName);
            categoryIcon = getResources().getDrawable(R.drawable.vic_default);
        }
        return categoryIcon;
    }

    public void showSearchResults(Cursor cursor) {
        getAdapter().changeCursor(cursor);
        int placesFound = cursor.getCount();
        Timber.d("Number of places found: %d", placesFound);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
