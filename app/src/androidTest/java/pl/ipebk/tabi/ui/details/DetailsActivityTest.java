package pl.ipebk.tabi.ui.details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.test.common.rules.TestComponentRule;
import pl.ipebk.tabi.util.OrientationChangeAction;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.ipebk.tabi.util.VisibilityAssertions.isGone;

@RunWith(AndroidJUnit4.class)
public class DetailsActivityTest {
    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<DetailsActivity> details =
            new ActivityTestRule<>(DetailsActivity.class, false, false);

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule public final TestRule chain = RuleChain.outerRule(component).around(details);
    @Mock PlaceDao mockPlaceDao;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        DatabaseOpenHelper databaseHelper = mock(DatabaseOpenHelper.class);
        when(databaseHelper.getPlaceDao()).thenReturn(mockPlaceDao);
        when(component.getMockDataManager().getDatabaseHelper()).thenReturn(databaseHelper);
    }

    @Test public void isViewAttachedAfterCreation() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        Intent intent = new Intent(component.getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, 1L);

        DetailsActivity detailsActivity = details.launchActivity(intent);

        Assert.assertTrue(detailsActivity.presenter.isViewAttached());
    }

    @Test public void testPlaceDisplaysOnScreen() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        Intent intent = new Intent(component.getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, 1L);

        details.launchActivity(intent);

        // check place name
        onView(withId(R.id.txt_place_name)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_place_name)).check(matches(withText(name)));

        // other texts
        onView(withId(R.id.txt_plate)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_voivodeship)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_powiat)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_gmina)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_additional)).check(matches(isDisplayed()));

        // buttons
        onView(withId(R.id.btn_google_it)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_map)).check(matches(isDisplayed()));

        // map
        onView(withId(R.id.img_map)).check(matches(isDisplayed()));
    }

    @Test public void testSpecialCategoryDisplaysCorrectly() {
        String name = "Central Anticorruption Bureau";
        Place special = TestDataFactory.createSpecialPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(special));

        Intent intent = new Intent(component.getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, 1L);

        details.launchActivity(intent);

        onView(withId(R.id.txt_place_name)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_place_name)).check(matches(withText(name)));

        // other texts
        onView(withId(R.id.txt_plate)).check(matches(isDisplayed()));

        // not displayed views
        onView(withId(R.id.txt_voivodeship)).check(isGone());
        onView(withId(R.id.txt_powiat)).check(isGone());
        onView(withId(R.id.txt_gmina)).check(isGone());
        onView(withId(R.id.txt_additional)).check(isGone());
        onView(withId(R.id.btn_google_it)).check(isGone());
        onView(withId(R.id.btn_map)).check(isGone());
        onView(withId(R.id.map_with_panel)).check(isGone());
    }

    @Test public void testActivitySurvivesScreenOrientationChange() {
        String name = "Malbork";
        Place malbork = TestDataFactory.createStandardPlace(name);

        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));

        Intent intent = new Intent(component.getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, 1L);

        details.launchActivity(intent);

        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape());

        // check place name
        onView(withId(R.id.txt_place_name)).check(matches(isDisplayed()));
        onView(withId(R.id.txt_place_name)).check(matches(withText(name)));

        // map
        onView(withId(R.id.img_map)).check(matches(isDisplayed()));
    }
}