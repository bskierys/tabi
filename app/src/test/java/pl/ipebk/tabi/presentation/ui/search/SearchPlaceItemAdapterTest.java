package pl.ipebk.tabi.presentation.ui.search;

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
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlate;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceAndPlateDtoDtoAssembler;
import pl.ipebk.tabi.test.common.injection.component.DaggerTestViewComponent;
import pl.ipebk.tabi.test.common.injection.component.TestViewComponent;
import pl.ipebk.tabi.test.common.injection.module.TestViewModule;
import pl.ipebk.tabi.test.common.utils.TestPlaceLocalizationHelper;
import pl.ipebk.tabi.test.common.utils.TestRandomTextProvider;
import pl.ipebk.tabi.utils.AggregateIdMatcher;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class SearchPlaceItemAdapterTest {

    @Mock List<PlaceAndPlateDto> mockItems;
    @Mock PlaceItemAdapter.PlaceClickListener placeListener;
    @Mock SearchPlaceItemAdapter.HeaderClickListener headerListener;
    @Mock Cursor cursor;

    private TestPlaceLocalizationHelper localizationHelper;
    private TestRandomTextProvider randomProvider;
    private PlaceAndPlateFactory factory;
    private SearchPlaceItemAdapter.HeaderViewHolder headerHolder;
    private SearchPlaceItemAdapter.ItemViewHolder itemHolder;
    private SearchPlaceItemAdapter adapter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        App application = App.get(RuntimeEnvironment.application);
        randomProvider = new TestRandomTextProvider(application);
        localizationHelper = new TestPlaceLocalizationHelper(application);
        factory = new PlaceAndPlateFactory(null, localizationHelper);
        TestViewComponent testComponent = DaggerTestViewComponent.builder()
                                                                 .testViewModule(new TestModule(application))
                                                                 .build();
        application.setViewComponent(testComponent);

        when(cursor.getCount()).thenReturn(1);
        adapter = new TestablePlaceItemAdapter(cursor, application, randomProvider, factory);
        adapter.setPlaceClickListener(placeListener);
        adapter.setHeaderClickListener(headerListener);
        adapter.setType(SearchType.LICENSE_PLATE);

        LayoutInflater inflater = (LayoutInflater) RuntimeEnvironment
                .application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listItemView = inflater.inflate(R.layout.row_place, null, false);
        itemHolder = new SearchPlaceItemAdapter.ItemViewHolder(listItemView);

        View listHeaderView = inflater.inflate(R.layout.row_place_header, null, false);
        headerHolder = new SearchPlaceItemAdapter.HeaderViewHolder(listHeaderView);
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
        assertThat(itemHolder.voivodeshipView).hasText(localizationHelper.formatVoivodeship("voivo"));
        assertThat(itemHolder.powiatView).hasText(localizationHelper.formatPowiat("powiat"));

        itemHolder.root.performClick();

        verify(placeListener).onPlaceItemClicked(any(), agIdEq(new AggregateId(10)), eq("TAB"),
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
        assertThat(itemHolder.powiatView).hasText(localizationHelper.formatVoivodeship("voivodeship"));
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

        verify(headerListener).onHeaderClicked(1);
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
        assertThat(itemHolder.voivodeshipView).doesNotContainText(localizationHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).hasText(TestRandomTextProvider.RANDOM_MOCK_QUESTION);
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
        assertThat(itemHolder.voivodeshipView).doesNotContainText(localizationHelper.formatVoivodeship(name));
        assertThat(itemHolder.powiatView).doesNotContainText(localizationHelper.formatPowiat(name));
    }

    private PlaceAndPlateDtoDtoAssembler assemblePlace(){
        return new PlaceAndPlateDtoDtoAssembler();
    }

    static AggregateId agIdEq(AggregateId expected) {
        return argThat(new AggregateIdMatcher(expected));
    }

    public class TestablePlaceItemAdapter extends SearchPlaceItemAdapter {
        public TestablePlaceItemAdapter(Cursor cursor, Context context,
                                        RandomTextProvider randomTextProvider,
                                        PlaceAndPlateFactory factory) {
            super(cursor, context, randomTextProvider, factory);
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

        @Override public RandomTextProvider provideRandomTextProvider() {
            return randomProvider;
        }
    }
}