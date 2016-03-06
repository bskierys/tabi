package pl.ipebk.tabi.ui.details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import pl.ipebk.tabi.database.daos.PlaceDao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.test.common.TestDataFactory;
import pl.ipebk.tabi.test.common.rules.TestComponentRule;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DetailsActivityTest {
    public final TestComponentRule component =
            new TestComponentRule(InstrumentationRegistry.getTargetContext());
    public final ActivityTestRule<DetailsActivity> main =
            new ActivityTestRule<DetailsActivity>(DetailsActivity.class, false, false) {
                @Override
                protected Intent getActivityIntent() {
                    return super.getActivityIntent();
                }
            };

    // TestComponentRule needs to go first to make sure the Dagger ApplicationTestComponent is set
    // in the Application before any Activity is launched.
    @Rule public final TestRule chain = RuleChain.outerRule(component).around(main);

    // TODO: 2016-03-06 more tests
    @Test public void testPlaceDisplaysOnScreen() {
        String name = "Malbork";
        Place malbork = TestDataFactory.makePlace(name);

        PlaceDao mockPlaceDao = mock(PlaceDao.class);
        when(mockPlaceDao.getByIdObservable(1L)).thenReturn(Observable.just(malbork));
        DatabaseOpenHelper databaseHelper = mock(DatabaseOpenHelper.class);
        when(databaseHelper.getPlaceDao()).thenReturn(mockPlaceDao);

        when(component.getMockDataManager().getDatabaseHelper()).thenReturn(databaseHelper);

        Intent intent = new Intent(component.getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.PARAM_PLACE_ID, 1L);

        main.launchActivity(intent);

        onView(withText(name)).check(matches(isDisplayed()));
    }
}