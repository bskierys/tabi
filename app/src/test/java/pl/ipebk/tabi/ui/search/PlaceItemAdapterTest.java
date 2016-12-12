package pl.ipebk.tabi.ui.search;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import pl.ipebk.tabi.BuildConfig;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class PlaceItemAdapterTest {

    /*@Mock List<PlaceListItem> mockItems;
    @Mock PlaceFragmentEventListener eventListener;
    @Mock Cursor cursor;

    private TestNameFormatHelper formatHelper;
    private PlaceItemAdapter.HeaderViewHolder headerHolder;
    private PlaceItemAdapter.ItemViewHolder itemHolder;
    private PlaceItemAdapter adapter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        App application = App.get(RuntimeEnvironment.application);
        formatHelper = new TestNameFormatHelper(application);
        TestViewComponent testComponent = DaggerTestViewComponent.builder()
                                                                 .testViewModule(new TestModule(application))
                                                                 .build();
        application.setViewComponent(testComponent);

        when(cursor.getCount()).thenReturn(1);
        adapter = new TestablePlaceItemAdapter(cursor, application);
        adapter.setEventListener(eventListener);
        adapter.setType(SearchType.PLATE);

        LayoutInflater inflater = (LayoutInflater) RuntimeEnvironment
                .application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = inflater.inflate(R.layout.row_place, null, false);
        itemHolder = new PlaceItemAdapter.ItemViewHolder(listItemView);

        View listHeaderView = inflater.inflate(R.layout.row_place_header, null, false);
        headerHolder = new PlaceItemAdapter.HeaderViewHolder(listHeaderView);
    }

    @Test public void testBindStandardPlace() throws Exception {
        String name = "Name";
        String plateStart = "TAB";

        PlaceListItem place = TestDataFactory.createStandardPlaceItem(name, plateStart, Place.Type.TOWN);
        place.setPlaceId(10);
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText(name);
        assertThat(itemHolder.plateView).hasText(plateStart);
        assertThat(itemHolder.voivodeshipView).hasText(formatHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).hasText(formatHelper.formatPowiat(name));

        itemHolder.root.performClick();

        verify(eventListener).onPlaceItemClicked(10, plateStart, SearchType.PLATE, PlaceListItemType.SEARCH);
    }

    @Test public void testBindSpecialPlace() throws Exception {
        String name = "Name this";
        String plateStart = "TAB";

        PlaceListItem place = TestDataFactory.createSpecialPlaceItem(name, plateStart);
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText("Name");
        assertThat(itemHolder.plateView).hasText(plateStart);
        assertThat(itemHolder.voivodeshipView).hasText("this");
        assertThat(itemHolder.powiatView).hasText(name);
    }

    @Test public void testBindHeader() throws Exception {
        String name = "Name";
        String plateStart = "TAB";
        String title = "title";

        adapter.addSection(0, title, 1);
        PlaceListItem place = TestDataFactory.createStandardPlaceItem(name, plateStart, Place.Type.RANDOM);
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(headerHolder, 0);

        assertThat(headerHolder.header).hasText(title);

        headerHolder.root.performClick();

        verify(eventListener).onHeaderClicked(1);
    }

    @Test public void testBindRandomPlacePlateSection() throws Exception {
        String name = "Name";
        String plateStart = "TAB";

        adapter.setType(SearchType.PLATE);
        PlaceListItem place = TestDataFactory.createStandardPlaceItem(name, plateStart, Place.Type.RANDOM);
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).doesNotContainText(name);
        assertThat(itemHolder.plateView).hasText(plateStart);
        assertThat(itemHolder.voivodeshipView).doesNotContainText(formatHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).hasText(TestNameFormatHelper.RANDOM_MOCK_QUESTION);
    }

    @Test public void testBindRandomPlacePlaceSection() throws Exception {
        String name = "Name";
        String plateStart = "TAB";

        adapter.setType(SearchType.PLACE);
        PlaceListItem place = TestDataFactory.createStandardPlaceItem(name, plateStart, Place.Type.RANDOM);
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText(name);
        assertThat(itemHolder.plateView).hasText("???");
        assertThat(itemHolder.voivodeshipView).doesNotContainText(formatHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).doesNotContainText(formatHelper.formatPowiat(name));
    }

    public class TestablePlaceItemAdapter extends PlaceItemAdapter {
        public TestablePlaceItemAdapter(Cursor cursor, Context context) {
            super(cursor, context);
        }

        @Override protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
            return itemHolder;
        }

        @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
            return headerHolder;
        }

        @Override protected PlaceListItem cursorToItem(Cursor cursor) {
            return mockItems.get(0);
        }
    }

    public class TestModule extends TestViewModule {

        public TestModule(Context context) {
            super(context);
        }

        @Override public NameFormatHelper provideNameFormatHelper() {
            return formatHelper;
        }
    }*/
}