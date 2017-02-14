package pl.ipebk.tabi.presentation.ui.search;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;

import static org.assertj.android.api.Assertions.assertThat;
import static org.assertj.android.recyclerview.v7.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class PlaceListFragmentTest {
    private TestablePlaceListFragment fragment;
    @Mock LinearLayoutManager mockLayoutManager;
    @Mock SearchPlaceItemAdapter mockAdapter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fragment = new TestablePlaceListFragment();
        fragment.type = SearchType.PLACE;
        fragment.setLayoutManager(mockLayoutManager);
        fragment.setAdapter(mockAdapter);

        //Start the fragment!
        SupportFragmentTestUtil.startFragment(fragment, TestableActivity.class);
    }

    @Test public void testIsProperlyMocked() {
        assertNotNull("Fragment is not loaded correctly", fragment.getView());
        assertTrue("Fragment onCreateView not called", fragment.isViewCreated());

        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.place_list);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        assertThat(layoutManager).isEqualTo(mockLayoutManager);
    }

    @Test public void testProperlyCreated() throws Exception {
        TestableActivity activity = (TestableActivity) fragment.getActivity();

        verify(activity.getEventListener()).onFragmentViewCreated(SearchType.PLACE);
        verify(mockAdapter, never()).changeCursor(any(Cursor.class));
        verify(mockAdapter, never()).swapCursor(any(Cursor.class));
    }

    @Test public void testSetDataTriggersAdapter() throws Exception {
        Cursor cursor = mock(Cursor.class);
        fragment.setData(cursor);

        verify(mockAdapter).changeCursor(any(Cursor.class));
    }

    @Test public void testShowQuickHeaders() throws Exception {
        int itemCount = 10;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showQuickHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_click);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection,
                                       SearchActivity.EVENT_ID_HEADER_ALL);
    }

    @Test public void testShowQuickHeadersWhen3() throws Exception {
        int itemCount = 3;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showQuickHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_click);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection,
                                       SearchActivity.EVENT_ID_HEADER_ALL);
    }

    @Test public void testShowQuickHeadersWhenLessThan3() throws Exception {
        int itemCount = 1;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showQuickHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_click);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        // should not add second section
        verify(mockAdapter, never()).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection,
                                                SearchActivity.EVENT_ID_HEADER_ALL);
    }

    @Test public void testShowFullHeaders() throws Exception {
        int itemCount = 10;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showFullHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_all);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection, null);
    }

    @Test public void testShowFullHeadersWhen3() throws Exception {
        int itemCount = 3;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showFullHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_all);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        // should not add second section
        verify(mockAdapter, never()).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection, null);
    }

    @Test public void testShowFullHeadersWhenLessThan3() throws Exception {
        int itemCount = 1;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showFullHeaders(itemCount);

        String firstSection = fragment.getString(R.string.search_header_best);
        String secondSection = fragment.getString(R.string.search_header_all);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(false);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        // should not add second section
        verify(mockAdapter, never()).addSection(PlaceListFragment.SECTION_SECOND_POSITION, secondSection, null);
    }

    @Test public void testShowInitialHeaders() throws Exception {
        int itemCount = 3;

        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(itemCount);
        fragment.setData(cursor);
        fragment.showInitialHeaders();

        String firstSection = fragment.getString(R.string.search_header_suggestions);

        verify(mockAdapter).changeCursor(any(Cursor.class));
        verify(mockAdapter).setHistorical(true);
        verify(mockAdapter).addSection(PlaceListFragment.SECTION_FIRST_POSITION, firstSection, null);
        // should not add second section
        verify(mockAdapter, never()).addSection(eq(PlaceListFragment.SECTION_SECOND_POSITION),
                                                anyString(), any(Integer.class));
    }

    @Test public void testShowList() throws Exception {
        assertNotNull("Fragment is not loaded correctly", fragment.getView());
        View list = fragment.getView().findViewById(R.id.place_list);
        fragment.showList();

        assertThat(list).isVisible();
    }

    @Test public void testHideList() throws Exception {
        assertNotNull("Fragment is not loaded correctly", fragment.getView());
        View list = fragment.getView().findViewById(R.id.place_list);
        fragment.hideList();

        assertThat(list).isNotVisible();
    }

    @Test public void testShowNoResultsImage() throws Exception {
        assertNotNull("Fragment is not loaded correctly", fragment.getView());
        ImageView image = (ImageView) fragment.getView().findViewById(R.id.img_no_results);

        Bitmap bitmap = mock(Bitmap.class);
        fragment.showNoResultsImage(bitmap);

        assertThat(image).isVisible();
        assertNotNull(image.getDrawable());
    }

    @Test public void testHideNoResultsImage() throws Exception {
        assertNotNull("Fragment is not loaded correctly", fragment.getView());
        ImageView image = (ImageView) fragment.getView().findViewById(R.id.img_no_results);
        fragment.hideNoResultsImage();

        assertThat(image).isNotVisible();
    }

    // TODO: 2016-05-21 test adapter
    // https://chelseatroy.com/2015/09/27/android-examples-a-test-driven-recyclerview/
    public static class TestableActivity extends AppCompatActivity implements PlaceFragmentEventListener {
        PlaceFragmentEventListener eventListener;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            eventListener = mock(PlaceFragmentEventListener.class);
        }

        public PlaceFragmentEventListener getEventListener() {
            return eventListener;
        }

        @Override public void onPlaceItemClicked(View view, AggregateId placeId, String plateClicked, SearchType type,
                                                 PlaceListItemType itemType) {
            eventListener.onPlaceItemClicked(view, placeId, plateClicked, type, itemType);
        }

        @Override public void onHeaderClicked(int eventId) {
            eventListener.onHeaderClicked(eventId);
        }

        @Override public void onFragmentViewCreated(SearchType type) {
            eventListener.onFragmentViewCreated(type);
        }
    }

    public static class TestablePlaceListFragment extends PlaceListFragment {
        private RecyclerView.LayoutManager layoutManager;
        private SearchPlaceItemAdapter adapter;

        @Override public RecyclerView.LayoutManager getLayoutManager() {
            return layoutManager;
        }

        public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override public SearchPlaceItemAdapter getAdapter() {
            return adapter;
        }

        public void setAdapter(SearchPlaceItemAdapter adapter) {
            this.adapter = adapter;
        }
    }
}