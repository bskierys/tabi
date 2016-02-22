package pl.ipebk.tabi.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.ui.fragments.PlaceFragment;
import pl.ipebk.tabi.utils.SpellCorrector;
import pl.ipebk.tabi.utils.Stopwatch;

public class SearchActivity extends BaseActivity implements PlaceFragment.OnListFragmentInteractionListener, TextWatcher {
    @Bind(R.id.search_phrase) EditText searchPhrase;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private PlaceFragment searchPlacesFragment;
    private PlaceFragment searchPlatesFragment;
    private SpellCorrector spellCorrector;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        spellCorrector = new SpellCorrector();

        searchPhrase.addTextChangedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        searchPlatesFragment = getFragment(0);
        searchPlacesFragment = getFragment(1);
    }

    private PlaceFragment getFragment(int position) {
        String fragmentTag = "android:switcher:" + mViewPager.getId() + ":" + position;
        Fragment savedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (savedFragment != null) return (PlaceFragment) savedFragment;
        else {
            switch (position) {
                case 0:
                    return PlaceFragment.newInstance(1);
                case 1:
                    return PlaceFragment.newInstance(1);
            }
        }
        return null;
    }

    @Override public void onListFragmentInteraction(Place place) {

    }

    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String rawPhrase = charSequence.toString();
        String phrase = spellCorrector.cleanForSearch(rawPhrase);
        searchPlacesFragment.setPlaceCursor(databaseHelper.getPlaceDao().getPlacesByName(phrase, null));
        searchPlatesFragment.setPlaceCursor(databaseHelper.getPlaceDao().getPlacesForPlateStart(phrase.toUpperCase(), null));
    }

    @Override public void afterTextChanged(Editable editable) {

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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return searchPlatesFragment;
                case 1:
                    return searchPlacesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
