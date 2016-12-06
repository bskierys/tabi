package pl.ipebk.tabi.ui.search;

import android.app.Application;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.Scheduler;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.test.common.injection.component.DaggerTestApplicationComponent;
import pl.ipebk.tabi.test.common.injection.component.TestApplicationComponent;
import pl.ipebk.tabi.test.common.injection.module.TestApplicationModule;
import rx.Observable;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.assertj.android.api.Assertions.assertThat;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class SearchActivityTest {
    /*@Mock PlaceDao mockPlaceDao;
    @Mock DatabaseOpenHelper databaseOpenHelper;
    @Mock DataManager dataManager;

    public SearchActivityTest() {
        App application = App.get(RuntimeEnvironment.application);
        TestApplicationComponent testComponent =
                DaggerTestApplicationComponent.builder()
                                              .testApplicationModule(new TestModule(application))
                                              .build();
        application.setAppComponent(testComponent);
    }

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dataManager.getDatabaseHelper()).thenReturn(databaseOpenHelper);
        when(databaseOpenHelper.getPlaceDao()).thenReturn(mockPlaceDao);
    }

    @Test public void testToolbarEditTextEmptyAtStart() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(), any())).thenReturn(Observable.just(cursor));
        SearchActivity activity = getSearchActivity();

        EditText editText = (EditText) activity.findViewById(R.id.editTxt_search);

        assertNotNull("Search edit text not loaded", editText);
        assertEquals("Search edit text is not empty", "", editText.getText().toString());
    }

    @Test public void testToolbarHiddenTextEmptyAtStart() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(), any())).thenReturn(Observable.just(cursor));

        SearchActivity activity = getSearchActivity();

        TextView hiddenSearch = (TextView) activity.findViewById(R.id.txt_searched);

        assertNotNull("Searched text not loaded", hiddenSearch);
        assertEquals("Searched text is not empty", "", hiddenSearch.getText().toString());
        assertNotEquals("Hidden text is visible", View.VISIBLE, hiddenSearch.getVisibility());
    }

    private SearchActivity getSearchActivity() {
        ShadowLooper.pauseMainLooper();
        Scheduler uiThreadScheduler = ShadowApplication.getInstance().getForegroundThreadScheduler();
        SearchActivity activity = Robolectric.setupActivity(SearchActivity.class);
        assertTrue(uiThreadScheduler.areAnyRunnable());
        uiThreadScheduler.advanceToLastPostedRunnable();
        return activity;
    }

    @Test public void testArePlaceFragmentsLoaded() throws Exception {
        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(), any())).thenReturn(Observable.just(cursor));

        SearchActivity activity = getSearchActivity();

        ViewPager pager = (ViewPager) activity.findViewById(R.id.pager_search);
        PagerAdapter adapter = pager.getAdapter();

        assertEquals(SearchActivity.TOTAL_NUMBER_OF_FRAGMENTS, adapter.getCount());
    }

    // TODO: 2016-05-21 test only activity methods f.ex. if interaction with buttons fires presenter or if mvp
    // todo: methods fires mocked fragment
    @Test public void testTextClearedWhenXClicked() throws Exception {
        String sampleText = "text";

        Cursor cursor = mock(Cursor.class);
        when(mockPlaceDao.getHistoryPlaces(anyInt(), any())).thenReturn(Observable.just(cursor));

        SearchActivity activity = getSearchActivity();

        View xButton = activity.findViewById(R.id.btn_clear);
        EditText editText = (EditText) activity.findViewById(R.id.editTxt_search);

        editText.setText(sampleText);
        xButton.performClick();

        assertThat(editText).doesNotContainText(sampleText);

        // TODO: 2016-11-29 invoked too many times
        verify(mockPlaceDao, atLeastOnce()).getHistoryPlaces(anyInt(), any(SearchType.class));
    }

    @Test public void testDataSearchedWhenTextEntered() throws Exception {

    }

    @Test public void testOnClearButton() throws Exception {

    }

    @Test public void testOnPlaceItemClicked() throws Exception {

    }

    @Test public void testShowFullSearchInPlacesSection() throws Exception {

    }

    @Test public void testShowBestSearchInPlacesSection() throws Exception {

    }

    @Test public void testShowInitialSearchInPlacesSection() throws Exception {

    }

    @Test public void testShowEmptyStateInPlacesSection() throws Exception {

    }

    @Test public void testHideEmptyStateInPlacesSection() throws Exception {

    }

    @Test public void testSetSearchText() throws Exception {

    }

    @Test public void testHideKeyboard() throws Exception {

    }

    @Test public void testGoToPlaceDetails() throws Exception {

    }

    @Test public void testShowInitialSearchInPlatesSection() throws Exception {

    }

    @Test public void testShowEmptyStateInPlatesSection() throws Exception {

    }

    @Test public void testHideEmptyStateInPlatesSection() throws Exception {

    }

    @Test public void testShowFullSearchInPlatesSection() throws Exception {

    }

    @Test public void testShowBestSearchInPlatesSection() throws Exception {

    }

    public class TestModule extends TestApplicationModule {

        public TestModule(Application application) {
            super(application);
        }

        @Override public DataManager provideDataManager() {
            return dataManager;
        }
    }*/
}