package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlate;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceAndPlateDtoDtoAssembler;
import pl.ipebk.tabi.test.common.injection.component.DaggerTestViewComponent;
import pl.ipebk.tabi.test.common.injection.component.TestViewComponent;
import pl.ipebk.tabi.test.common.injection.module.TestViewModule;
import pl.ipebk.tabi.test.common.utils.TestNameFormatHelper;
import pl.ipebk.tabi.utils.AggregateIdMatcher;
import pl.ipebk.tabi.utils.NameFormatHelper;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class PlaceItemAdapterTest {

    @Mock List<PlaceAndPlateDto> mockItems;
    @Mock PlaceFragmentEventListener eventListener;
    @Mock Cursor cursor;

    private TestNameFormatHelper formatHelper;
    private PlaceAndPlateFactory factory;
    private PlaceItemAdapter.HeaderViewHolder headerHolder;
    private PlaceItemAdapter.ItemViewHolder itemHolder;
    private PlaceItemAdapter adapter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        App application = App.get(RuntimeEnvironment.application);
        formatHelper = new TestNameFormatHelper(application);
        factory = new PlaceAndPlateFactory(null, formatHelper);
        TestViewComponent testComponent = DaggerTestViewComponent.builder()
                                                                 .testViewModule(new TestModule(application))
                                                                 .build();
        application.setViewComponent(testComponent);

        when(cursor.getCount()).thenReturn(1);
        adapter = new TestablePlaceItemAdapter(cursor, application);
        adapter.setEventListener(eventListener);
        adapter.setType(SearchType.LICENSE_PLATE);

        LayoutInflater inflater = (LayoutInflater) RuntimeEnvironment
                .application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = inflater.inflate(R.layout.row_place, null, false);
        itemHolder = new PlaceItemAdapter.ItemViewHolder(listItemView);

        View listHeaderView = inflater.inflate(R.layout.row_place_header, null, false);
        headerHolder = new PlaceItemAdapter.HeaderViewHolder(listHeaderView);
    }

    // TODO: 2016-12-14 separate into more tests
    // TODO: 2016-12-14 bdd tests
    // TODO: 2016-12-14 zbyt duże splątanie
    @Test public void testBindStandardPlace() throws Exception {
        PlaceAndPlateDto place = assemblePlace().withName("Name").withId(10)
                                                .inPowiat("powiat")
                                                .inVoivodeship("voivo")
                                                .withPlate("TAB").standard()
                                                .assemble();
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText("Name");
        assertThat(itemHolder.plateView).hasText("TAB");
        assertThat(itemHolder.voivodeshipView).hasText(formatHelper.formatVoivodeship("voivo"));
        assertThat(itemHolder.powiatView).hasText(formatHelper.formatPowiat("powiat"));

        itemHolder.root.performClick();

        verify(eventListener).onPlaceItemClicked(agIdEq(new AggregateId(10)), eq("TAB"),
                                                 eq(SearchType.LICENSE_PLATE), eq(PlaceListItemType.SEARCH));
    }

    @Test public void testBindSpecialPlace() throws Exception {
        PlaceAndPlateDto place = assemblePlace().withName("Name this").inPowiat("powiat")
                                                .withPlate("TAB").special().assemble();
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText("Name");
        assertThat(itemHolder.plateView).hasText("TAB");
        assertThat(itemHolder.voivodeshipView).hasText("this");
        assertThat(itemHolder.powiatView).hasText(formatHelper.formatVoivodeship("voivodeship"));
    }

    @Test public void testBindHeader() throws Exception {
        String name = "Name";
        String plateStart = "TAB";
        String title = "title";

        adapter.addSection(0, title, 1);
        PlaceAndPlateDto place = assemblePlace().withName(name).withPlate(plateStart).random().assemble();
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(headerHolder, 0);

        assertThat(headerHolder.header).hasText(title);

        headerHolder.root.performClick();

        verify(eventListener).onHeaderClicked(1);
    }

    @Test public void testBindRandomPlacePlateSection() throws Exception {
        String name = "Name";
        String plateStart = "TAB";

        adapter.setType(SearchType.LICENSE_PLATE);
        PlaceAndPlateDto place = assemblePlace().withName(name).withPlate(plateStart).random().assemble();
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
        PlaceAndPlateDto place = assemblePlace().withName(name).withPlate(plateStart).random().assemble();
        when(mockItems.get(0)).thenReturn(place);

        adapter.onBindViewHolder(itemHolder, cursor, 0);

        assertThat(itemHolder.placeNameView).hasText(name);
        assertThat(itemHolder.plateView).hasText("???");
        assertThat(itemHolder.voivodeshipView).doesNotContainText(formatHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).doesNotContainText(formatHelper.formatPowiat(name));
    }

    private PlaceAndPlateDtoDtoAssembler assemblePlace(){
        return new PlaceAndPlateDtoDtoAssembler();
    }

    static AggregateId agIdEq(AggregateId expected) {
        return argThat(new AggregateIdMatcher(expected));
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

        @Override protected PlaceAndPlate cursorToItem(Cursor cursor) {
            return factory.createFromDto(mockItems.get(0));
        }
    }

    public class TestModule extends TestViewModule {

        public TestModule(Context context) {
            super(context);
        }

        @Override public NameFormatHelper provideNameFormatHelper() {
            return formatHelper;
        }
    }
}