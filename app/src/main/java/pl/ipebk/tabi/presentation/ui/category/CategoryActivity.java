package pl.ipebk.tabi.presentation.ui.category;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.search.PlaceFragmentEventListener;
import pl.ipebk.tabi.presentation.ui.search.PlaceItemAdapter;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
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

    @BindView(R.id.place_list) RecyclerView recyclerView;
    @BindView(R.id.progress) ProgressBar progressBar;

    private String categoryKey;
    Subscription searchSubscription;
    CategoryPlaceItemAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);

        try {
            categoryKey = getIntent().getStringExtra(EXTRA_CATEGORY_KEY);
            if(categoryKey == null) {
                throw new NullPointerException("Category key is null");
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not initialize CategoryActivity: category key was not passed");
        }

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(getAdapter());

        searchSubscription = Observable
                .just(categoryKey)
                .subscribeOn(Schedulers.io())
                .flatMap(plate -> plateFinder.findPlacesForPlateStart(plate, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> showSearchResults(results),
                           e -> Timber.e(e, "Error during searching for places", e));
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        RxUtil.unsubscribe(searchSubscription);
        presenter.detachView();
    }

    /**
     * Test methods to be replaced with mocks
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    public CategoryPlaceItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CategoryPlaceItemAdapter(null, this);
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

    public void showSearchResults(Cursor cursor) {
        getAdapter().changeCursor(cursor);
        int placesFound = cursor.getCount();
        Timber.d("Number of places found: %d", placesFound);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
