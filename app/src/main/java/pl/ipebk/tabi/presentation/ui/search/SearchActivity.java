package pl.ipebk.tabi.presentation.ui.search;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.widget.RxTextView;

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
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.DoodleImage;
import pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator;
import pl.ipebk.tabi.presentation.ui.details.DetailsSearchActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class SearchActivity extends BaseActivity implements PlaceFragmentEventListener, SearchMvpView {
    public static final String PARAM_SEARCH_TEXT = "param_search_text";
    public static final String PARAM_SHOW_KEYBOARD = "param_show_keyboard";
    public static final int EVENT_ID_HEADER_ALL = 2654;

    static final int SEARCH_PLATES_FRAGMENT_POSITION = 0;
    static final int SEARCH_PLACES_FRAGMENT_POSITION = 1;
    static final int TOTAL_NUMBER_OF_FRAGMENTS = 2;
    private static final int KEYBOARD_SHOW_DELAY = 200;

    @Inject SearchPresenter presenter;
    @Inject FontManager fontManager;
    @BindView(R.id.editTxt_search) EditText searchEditText;
    @BindView(R.id.txt_searched) TextView searchedText;
    @BindView(R.id.pager_search) ViewPager searchPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_search_wrap) View searchInputWrap;
    @BindView(R.id.toolbar_tab_indicator) SearchTabPageIndicator indicator;
    @BindView(R.id.btn_clear) View clearButton;
    @BindDimen(R.dimen.Toolbar_Height_Min) int toolbarHeight;
    @State String currentSearch;
    @State boolean isFullySearched;

    private PlaceListFragment searchPlacesFragment;
    private PlaceListFragment searchPlatesFragment;

    private BehaviorSubject<Integer> viewCreationSubject;
    private Bitmap noResultsBitmap;
    private DoodleImage noResultsDoodle;
    private Typeface doodleHeaderFont;
    private Typeface doodleDescriptionFont;

    private Subscription editorActionSubscription;
    private Subscription textChangesSubscription;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        presenter.attachView(this);

        doodleHeaderFont = fontManager.get("bebas-book", Typeface.NORMAL);
        doodleDescriptionFont = fontManager.get("montserrat", Typeface.NORMAL);

        RxViewPager.pageSelections(searchPager)
                   .subscribe(page -> {
                       if (page == SEARCH_PLACES_FRAGMENT_POSITION) {
                           searchEditText.setHint(getString(R.string.main_search_bar_hint_places));
                       } else if (page == SEARCH_PLATES_FRAGMENT_POSITION) {
                           searchEditText.setHint(getString(R.string.main_search_bar_hint_plates));
                       }
                   }, ex -> Timber.e(ex, "Page cannot be changed"));

        searchedText.setVisibility(View.GONE);
        preparePlaceFragments();
        prepareDoodleImages();
    }

    private void preparePlaceFragments() {
        viewCreationSubject = BehaviorSubject.create();

        searchPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(searchPager);

        searchPlatesFragment = retainSearchFragment(SEARCH_PLATES_FRAGMENT_POSITION);
        searchPlacesFragment = retainSearchFragment(SEARCH_PLACES_FRAGMENT_POSITION);
    }

    private void prepareDoodleImages() {
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(this)
                .headerFont(doodleHeaderFont)
                .descriptionFont(doodleDescriptionFont)
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

    private Observable<Bitmap> getNoResultsBitmap() {
        if (noResultsBitmap != null) {
            return Observable.just(noResultsBitmap);
        }
        return Observable.just(noResultsDoodle).subscribeOn(Schedulers.computation())
                         .doOnNext(DoodleImage::preComputeScale)
                         .observeOn(AndroidSchedulers.mainThread())
                         .map(DoodleImage::draw)
                         .doOnNext(bitmap -> noResultsBitmap = bitmap);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override protected void onResume() {
        super.onResume();

        indicator.setVisibility(View.VISIBLE);

        boolean shouldShowKeyboard = getIntent().getBooleanExtra(PARAM_SHOW_KEYBOARD, false);
        String searchText = getIntent().getStringExtra(PARAM_SEARCH_TEXT);
        getIntent().removeExtra(PARAM_SEARCH_TEXT);
        getIntent().removeExtra(PARAM_SHOW_KEYBOARD);

        searchEditText.postDelayed(() -> prepareSearch(searchText, shouldShowKeyboard), KEYBOARD_SHOW_DELAY);
    }

    private void prepareSearch(String searchText, boolean showKeyboard) {
        if (currentSearch != null && !currentSearch.equals("")) {
            showClearButton();
            if (isFullySearched) {
                presenter.deepSearchForText(currentSearch);
            } else {
                presenter.quickSearchForText(currentSearch);
            }
        } else {
            hideClearButton();
        }

        PublishSubject<Integer> textChangeSubject = PublishSubject.create();
        textChangeSubject.asObservable().subscribe(i -> {
            Timber.d("Text can now be searched");
        });

        if (searchText != null) {
            currentSearch = searchText;
            if (searchText.equals("")) {
                hideClearButton();
            } else {
                showClearButton();
            }
            presenter.startInitialSearchForText(searchText);
        }

        if (showKeyboard) {
            showKeyboard();
        } else {
            hideKeyboard();
        }

        textChangesSubscription = RxTextView
                .textChanges(searchEditText)
                .skipUntil(textChangeSubject)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .doOnNext(presenter::quickSearchForText)
                .doOnNext(text -> currentSearch = text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    if (text != null && !text.equals("")) {
                        showClearButton();
                    } else {
                        hideClearButton();
                    }
                }, ex -> Timber.e(ex, "Text subscription fail"));

        editorActionSubscription = RxTextView
                .editorActionEvents(searchEditText)
                .filter(event -> event.actionId() == EditorInfo.IME_ACTION_SEARCH)
                .subscribe(e -> {
                    presenter.deepSearchForText(searchEditText.getText().toString());
                }, ex -> Timber.e(ex, "Text search click fail"));

        presenter.refreshSearch();
        // makes sure subscription for text changes is no fired too soon
        textChangeSubject.onNext(1);
    }

    @Override protected void onPause() {
        super.onPause();
        RxUtil.unsubscribe(editorActionSubscription);
        RxUtil.unsubscribe(textChangesSubscription);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }

    @OnClick(R.id.btn_clear) public void onClearButton() {
        presenter.clearSearch();
        hideClearButton();
    }

    //region View callbacks
    @Override public void onPlaceItemClicked(View view, AggregateId placeId, String plateClicked,
                                             SearchType type, PlaceListItemType itemType, int position) {
        if (placeId.isValid()) {
            this.goToPlaceDetails(view, placeId, searchEditText.getText().toString(), type, itemType, position);
            presenter.placeSelected(placeId, plateClicked, type);
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
            isFullySearched = true;
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
            searchPlacesFragment.showFullHeaders(cursor.getCount());
        }
    }

    @Override public void showBestSearchInPlacesSection(Cursor cursor) {
        if (searchPlacesFragment.isViewCreated()) {
            isFullySearched = false;
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
            searchPlacesFragment.showQuickHeaders(cursor.getCount());
        }
    }

    @Override public void showInitialSearchInPlacesSection(Cursor cursor) {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
            searchPlacesFragment.showInitialHeaders();
        }
    }

    @Override public void showEmptyStateInPlacesSection() {
        if (searchPlacesFragment.isViewCreated()) {
            isFullySearched = false;
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
        if (searchText == null) {
            searchText = "";
        }

        searchEditText.setText(searchText.toLowerCase());
        searchEditText.setSelection(searchText.length());
    }

    @Override public void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        searchEditText.clearFocus();
        keyboard.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    @Override public void showKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        searchEditText.requestFocus();
        keyboard.showSoftInput(searchEditText, 0);
    }

    public void goToPlaceDetails(View view, AggregateId placeId, String searchedPlate,
                                 SearchType searchType, PlaceListItemType itemType, int position) {

        indicator.setVisibility(View.GONE);

        Intent intent = new Intent(this, DetailsSearchActivity.class);
        intent.putExtra(DetailsSearchActivity.PARAM_PLACE_ID, placeId.getValue());
        intent.putExtra(DetailsSearchActivity.PARAM_SEARCHED_PLATE, searchedPlate);
        intent.putExtra(DetailsSearchActivity.PARAM_SEARCHED_TYPE, searchType);
        intent.putExtra(DetailsSearchActivity.PARAM_ITEM_TYPE, itemType);
        intent.putExtra(DetailsSearchActivity.PARAM_ADAPTER_POSITION, position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<Pair<View, String>> transitions = new ArrayList<>();
            // shared elements
            transitions.add(Pair.create(indicator, SharedTransitionNaming.getName(getString(R.string.trans_tab_indicator), position)));
            transitions.add(Pair.create(searchInputWrap, SharedTransitionNaming.getName(getString(R.string.trans_search_input), position)));
            transitions.add(Pair.create(view, SharedTransitionNaming.getName(getString(R.string.trans_row_background), position)));
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

    @Override public void showInitialSearchInPlatesSection(Cursor cursor) {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
            searchPlatesFragment.showInitialHeaders();
        }
    }

    @Override public void showClearButton() {
        clearButton.setVisibility(View.VISIBLE);
    }

    @Override public void hideClearButton() {
        clearButton.setVisibility(View.INVISIBLE);
    }

    @Override public void showEmptyStateInPlatesSection() {
        if (searchPlatesFragment.isViewCreated()) {
            isFullySearched = false;
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
            isFullySearched = true;
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
            searchPlatesFragment.showFullHeaders(cursor.getCount());
        }
    }

    @Override public void showBestSearchInPlatesSection(Cursor cursor) {
        if (searchPlatesFragment.isViewCreated()) {
            isFullySearched = false;
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
            searchPlatesFragment.showQuickHeaders(cursor.getCount());
        }
    }
    //endregion

    //region View pager management
    protected PlaceListFragment retainSearchFragment(int position) {
        String fragmentTag = "android:switcher:" + searchPager.getId() + ":" + position;
        Fragment savedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (savedFragment != null) {
            return (PlaceListFragment) savedFragment;
        } else {
            switch (position) {
                case SEARCH_PLATES_FRAGMENT_POSITION:
                    return PlaceListFragment.newInstance(SearchType.LICENSE_PLATE);
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return PlaceListFragment.newInstance(SearchType.PLACE);
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
                    return getString(R.string.search_tab_plate);
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return getString(R.string.search_tab_place);
            }
            return getString(R.string.search_tab_unknown);
        }

        @Override
        public int getCount() {
            return TOTAL_NUMBER_OF_FRAGMENTS;
        }
    }
    //endregion
}
