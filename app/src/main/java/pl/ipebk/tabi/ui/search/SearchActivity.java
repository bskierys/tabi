package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.details.DetailsActivity;

public class SearchActivity extends BaseActivity implements
        PlaceFragment.onPlaceClickedListener, SearchMvpView {
    public static final String PARAM_SEARCH_TEXT = "param_search_text";
    private static final int SEARCH_PLATES_FRAGMENT_POSITION = 0;
    private static final int SEARCH_PLACES_FRAGMENT_POSITION = 1;
    @Inject SearchPresenter presenter;
    @Bind(R.id.editTxt_search) EditText searchEditText;
    @Bind(R.id.pager_search) ViewPager searchPager;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private PlaceFragment searchPlacesFragment;
    private PlaceFragment searchPlatesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        presenter.attachView(this);

        String textToSearch = getIntent().getStringExtra(PARAM_SEARCH_TEXT);
        if (textToSearch != null) {
            presenter.deepSearchForText(textToSearch);
        }

        RxTextView.textChanges(searchEditText)
                .subscribe(text -> {
                    presenter.quickSearchForText(text.toString());
                });
        RxTextView.editorActionEvents(searchEditText)
                .filter(event -> event.actionId() == EditorInfo.IME_ACTION_SEARCH)
                .subscribe(e -> {
                    presenter.deepSearchForText(searchEditText.getText().toString());
                });

        searchPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        searchPlatesFragment = retainSearchFragment(SEARCH_PLATES_FRAGMENT_POSITION);
        searchPlacesFragment = retainSearchFragment(SEARCH_PLACES_FRAGMENT_POSITION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //region View callbacks
    @Override public void onPlaceClicked(long placeId, SearchHistory.SearchType type) {
        String searchedPlate = null;
        if (type == SearchHistory.SearchType.PLATE) {
            searchedPlate = searchEditText.getText().toString();
        }
        presenter.placeSelected(placeId, searchedPlate);
    }

    @Override public void onFragmentViewCreated(SearchHistory.SearchType type) {
        if (type == SearchHistory.SearchType.PLACE) {
            presenter.loadInitialStateForPlaces();
        } else if (type == SearchHistory.SearchType.PLATE) {
            presenter.loadInitialStateForPlates();
        }
    }
    //endregion

    //region Mvp View methods
    @Override public void showPlacesInPlacesSection(Cursor cursor) {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.setData(cursor);
            searchPlacesFragment.showList();
        }
    }

    @Override public void showEmptyStateInPlacesSection() {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.showText("Nothing to see here");
            searchPlacesFragment.hideList();
        }
    }

    @Override public void hideEmptyStateInPlacesSection() {
        if (searchPlacesFragment.isViewCreated()) {
            searchPlacesFragment.hideText();
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

    @Override public void goToPlaceDetails(long placeId, String searchedPlate) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, placeId);
        intent.putExtra(DetailsActivity.PARAM_SEARCHED_PLATE, searchedPlate);
        startActivity(intent);
    }

    @Override public void showEmptyStateInPlatesSection() {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.showText("Nic nie znalazłem");
            searchPlatesFragment.hideList();
        }
    }

    @Override public void hideEmptyStateInPlatesSection() {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.hideText();
            searchPlatesFragment.showList();
        }
    }

    @Override public void showPlacesInPlatesSection(Cursor cursor) {
        if (searchPlatesFragment.isViewCreated()) {
            searchPlatesFragment.setData(cursor);
            searchPlatesFragment.showList();
        }
    }
    //endregion

    //region View pager management

    private PlaceFragment retainSearchFragment(int position) {
        String fragmentTag = "android:switcher:" + searchPager.getId() + ":" + position;
        Fragment savedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (savedFragment != null) return (PlaceFragment) savedFragment;
        else {
            switch (position) {
                case SEARCH_PLATES_FRAGMENT_POSITION:
                    return PlaceFragment.newInstance(SearchHistory.SearchType.PLATE);
                case SEARCH_PLACES_FRAGMENT_POSITION:
                    return PlaceFragment.newInstance(SearchHistory.SearchType.PLACE);
            }
        }
        return null;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
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

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
    //endregion
}
