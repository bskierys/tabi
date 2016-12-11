package pl.ipebk.tabi.infrastructure.repositories;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.Date;
import java.util.List;

import pl.ipebk.tabi.domain.searchhistory.SearchHistory;
import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.readmodel.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

public class DaoSearchHistoryRepositoryTest extends DatabaseTest {
    private DaoSearchHistoryRepository repository;
    private PlaceModelAssembler placeModelAssembler;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
        repository = new DaoSearchHistoryRepository(databaseHelper.getSearchHistoryDao());
    }

    @MediumTest public void test_shouldSaveHistoryToDatabase() throws Exception {
        givenPlace().withName("a").and().withPlate("GRA");
        addedToDatabase();

        SearchHistory history = new SearchHistory(placeModel.getId(), placeModel.plates().get(0).pattern(),
                                                  new Date(10), SearchType.PLACE);
        repository.save(history);

        SearchHistoryModel model = databaseHelper.getSearchHistoryDao().getAll().get(0);
        assertEquals(model.placeId(), history.getPlaceId());
        assertEquals(model.plate(), history.getPlate());
        assertEquals(0,model.timeSearched().compareTo(history.getTimeSearched()));
        assertEquals(model.searchType(), history.getSearchType().ordinal());
    }

    @MediumTest public void test_shouldReplaceSearchHistory_whenSearchedSecondTime() throws Exception {
        givenPlace().withName("a").and().withPlate("GRA");
        addedToDatabase();

        SearchHistory history = new SearchHistory(placeModel.getId(), placeModel.plates().get(0).pattern(),
                                                  new Date(10), SearchType.PLACE);
        SearchHistory history2 = new SearchHistory(placeModel.getId(), placeModel.plates().get(0).pattern(),
                                                  new Date(20), SearchType.PLACE);
        repository.save(history);
        repository.save(history2);

        List<SearchHistoryModel> models = databaseHelper.getSearchHistoryDao().getAll();
        assertEquals(1, models.size());

        SearchHistoryModel model = models.get(0);

        assertEquals(model.placeId(), history.getPlaceId());
        assertEquals(20, model.timeSearched().getTime());
    }

    public PlaceModelAssembler givenPlace() {
        placeModelAssembler = new PlaceModelAssembler();
        return placeModelAssembler;
    }

    // TODO: 2016-12-10 rename
    public void addedToDatabase() {
        this.placeModel = placeModelAssembler.assemble();
        databaseHelper.getPlaceDao().add(this.placeModel);
    }
}