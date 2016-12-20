package pl.ipebk.tabi.infrastructure.repositories;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.Date;
import java.util.List;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchHistoryFactory;
import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class DaoSearchHistoryRepositoryTest extends DatabaseTest {
    private DaoSearchHistoryRepository repository;
    private SearchHistoryFactory factory;
    private Date now;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
        repository = new DaoSearchHistoryRepository(databaseHelper.getSearchHistoryDao());
        factory = new SearchHistoryFactory(() -> now);
    }

    @MediumTest public void test_shouldSaveHistoryToDatabase() throws Exception {
        addToDatabase(givenPlace().withName("a").and().withPlate("GRA"));

        SearchHistory history = createHistory(10, SearchType.PLACE);
        repository.save(history);

        SearchHistoryModel model = databaseHelper.getSearchHistoryDao().getAll().get(0);
        assertEquals(model.placeId(), history.placeId().getValue());
        assertEquals(model.plate(), history.plate());
        assertEquals(0,model.timeSearched().compareTo(history.timeSearched()));
        assertEquals(model.searchType(), history.searchType().ordinal());
    }

    @MediumTest public void test_shouldReplaceSearchHistory_whenSearchedSecondTime() throws Exception {
        addToDatabase(givenPlace().withName("a").and().withPlate("GRA"));

        SearchHistory history = createHistory(10, SearchType.PLACE);
        SearchHistory history2 = createHistory(20, SearchType.PLACE);
        repository.save(history);
        repository.save(history2);

        List<SearchHistoryModel> models = databaseHelper.getSearchHistoryDao().getAll();
        assertEquals(1, models.size());

        SearchHistoryModel model = models.get(0);

        assertEquals(model.placeId(), history.placeId().getValue());
        assertEquals(20, model.timeSearched().getTime());
    }

    private PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    private void addToDatabase(PlaceModelAssembler assembler) {
        this.placeModel = assembler.assemble();
        databaseHelper.getPlaceDao().add(placeModel);
    }

    private SearchHistory createHistory(long time, SearchType type) {
        now = new Date(time);
        long placeId = placeModel.getId();
        String plate = placeModel.plates().get(0).getDto().pattern();
        return factory.create(new AggregateId(placeId), plate, type);
    }
}