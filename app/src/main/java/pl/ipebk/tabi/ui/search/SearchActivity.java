package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.details.DetailsActivity;
import pl.ipebk.tabi.utils.DoodleImage;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class SearchActivity extends BaseActivity implements PlaceFragment.onPlaceClickedListener, SearchMvpView {
    public static final String PARAM_SEARCH_TEXT = "param_search_text";
    public static final int EVENT_ID_HEADER_ALL = 2654;

    private static final int SEARCH_PLATES_FRAGMENT_POSITION = 0;
    private static final int SEARCH_PLACES_FRAGMENT_POSITION = 1;
    private static final int TOTAL_NUMBER_OF_FRAGMENTS = 2;

    @Inject SearchPresenter presenter;
    @Bind(R.id.editTxt_search) EditText searchEditText;
    @Bind(R.id.txt_searched) TextView searchedText;
    @Bind(R.id.pager_search) ViewPager searchPager;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.indicator) SearchTabPageIndicator indicator;

    private PlaceFragment searchPlacesFragment;
    private PlaceFragment searchPlatesFragment;

    private BehaviorSubject<Integer> viewCreationSubject;
    private Bitmap noResultsBitmap;
    private DoodleImage noResultsDoodle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        presenter.attachView(this);

        prepareSearchToolbar();
        preparePlaceFragments();
        prepareDoodleImages();
    }

    private void preparePlaceFragments() {
        String textToSearch = getIntent().getStringExtra(PARAM_SEARCH_TEXT);

        viewCreationSubject = BehaviorSubject.create();

        searchPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(searchPager);

        searchPlatesFragment = retainSearchFragment(SEARCH_PLATES_FRAGMENT_POSITION);
        searchPlacesFragment = retainSearchFragment(SEARCH_PLACES_FRAGMENT_POSITION);

        Observable<Integer> viewCreationStream = viewCreationSubject.asObservable().scan((a, b) -> a + b).filter
                (fragmentsCreated -> fragmentsCreated == TOTAL_NUMBER_OF_FRAGMENTS);

        Observable<String> initialSearchStream = Observable
                .combineLatest(viewCreationStream, Observable.just(textToSearch),
                               (fragmentsCreated, searchText) -> searchText)
                .filter(text -> text != null);

        initialSearchStream.subscribe(searchText -> presenter.startInitialSearchForText(searchText));
    }

    private void prepareSearchToolbar() {
        searchedText.setVisibility(View.GONE);

        RxTextView.textChanges(searchEditText)
                  .debounce(300, TimeUnit.MILLISECONDS)
                  .subscribe(text -> {
                      presenter.quickSearchForText(text.toString());
                  });
        RxTextView.editorActionEvents(searchEditText)
                  .filter(event -> event.actionId() == EditorInfo.IME_ACTION_SEARCH)
                  .subscribe(e -> {
                      presenter.deepSearchForText(searchEditText.getText().toString());
                  });
    }

    private void prepareDoodleImages(){
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(this)
                .height(getResources().getDimensionPixelSize(R.dimen.Search_Height_Doodle))
                .spaceBeforeImage(getResources().getDimensionPixelSize(
                        R.dimen.Search_Height_Doodle_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelSize(
                        R.dimen.Search_Height_Doodle_Space_After))
                .minimalMargin(getResources().getDimensionPixelSize(R.dimen.Search_Margin_Doodle));

        noResultsDoodle = doodleBuilder
                .imageResource(R.drawable.tabi_search_empty)
                .headerText(getString(R.string.search_doodle_no_results_header))
                .descriptionText(getString(R.string.search_doodle_no_results_description))
                .build();
    }

    private Observable<Bitmap> getNoResultsBitmap(){
        if(noResultsBitmap!=null){
            return Observable.just(noResultsBitmap);
        }
        return Observable.just(noResultsDoodle).subscribeOn(Schedulers.computation())
                .doOnNext(DoodleImage::preComputeScale)
                .observeOn(AndroidSchedulers.mainThread())
                .map(DoodleImage::draw)
                .doOnNext(bitmap -> noResultsBitmap = bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //region View callbacks
    @Override public void onPlaceClicked(long placeId, SearchType type) {
        if (placeId > 0) {
            presenter.placeSelected(placeId, searchEditText.getText().toString(), type);
        }
    }

    @Override public void onHeaderClicked(int eventId) {
        if (EVENT_ID_HEADER_ALL == eventId) {
            presenter.deepSearchForText(searchEditText.getText().toString());
        }
    }

    @Override public void onFragmentViewCreated(SearchType type) {
        viewCreationSubject.onNext(1);
    }
    //endregion

    //region Mvp View methods
    @Override public void showFullSearchInPlacesSection(Cursor cursor) {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
            searchPlacesFragment.showFullHeaders();
        }
    }

    @Override public void showBestSearchInPlacesSection(Cursor cursor) {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
            searchPlacesFragment.showQuickHeaders();
        }
    }

    @Override public void showEmptyStateInPlacesSection() {
        if (searchPlacesFragment.isViewCreated()) {
            getNoResultsBitmap().subscribe(searchPlacesFragment::showNoResultsImage);
            searchPlacesFragment.setData(null);
            searchPlacesFragment.hideList();
        }
    }

    @Override public void hideEmptyStateInPlacesSection() {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.hideNoResultsImage();
            searchPlacesFragment.showList();
        }
    }

    @Override public void setSearchText(String searchText) {
        searchEditText.setText(searchText.toLowerCase());
        searchEditText.setSelection(searchText.length());
    }

    @Override public void hideKeyboard() {
        View view = this.getCurrentFocus();

        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override public void goToPlaceDetails(long placeId, String searchedPlate, SearchType searchType) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, placeId);
        intent.putExtra(DetailsActivity.PARAM_SEARCHED_PLATE, searchedPlate);
        intent.putExtra(DetailsActivity.PARAM_SEARCHED_TYPE, searchType.ordinal());
        startActivity(intent);
    }

    @Override public void showEmptyStateInPlatesSection() {
        if (searchPlatesFragment.isViewCreated()) {
            getNoResultsBitmap().subscribe(searchPlatesFragment::showNoResultsImage);
            searchPlatesFragment.setData(null);
            searchPlatesFragment.hideList();
        }
    }

    @Override public void hideEmptyStateInPlatesSection() {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.hideNoResultsImage();
            searchPlatesFragment.showList();
        }
    }

    @Override public void showFullSearchInPlatesSection(Cursor cursor) {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
            searchPlatesFragment.showFullHeaders();
        }
    }

    @Override public void showBestSearchInPlatesSection(Cursor cursor) {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
            searchPlatesFragment.showQuickHeaders();
        }
    }
    //endregion

    //region View pager management

    private PlaceFragment retainSearchFragment(int position) {
        String fragmentTag = "android:switcher:" + searchPager.getId() + ":" + position;
        Fragment savedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (savedFragment != null) {
            return (PlaceFragment) savedFragment;
        } else {
            switch (position) {
                case SEARCH_PLATES_FRAGMENT_POSITION:
                    return PlaceFragment.newInstance(SearchType.PLATE);
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return PlaceFragment.newInstance(SearchType.PLACE);
            }
        }
        return null;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SEARCH_PLATES_FRAGMENT_POSITION:
                    return searchPlatesFragment;
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return searchPlacesFragment;
            }
            return null;
        }

        @Override public CharSequence getPageTitle(int position) {
            switch (position) {
                case SEARCH_PLATES_FRAGMENT_POSITION:
                    return "PO TABLICY";
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return "PO MIEJSCU";
            }
            return "CZO TY CHCESZ?";
        }

        @Override
        public int getCount() {
            return TOTAL_NUMBER_OF_FRAGMENTS;
        }
    }
    //endregion
}
