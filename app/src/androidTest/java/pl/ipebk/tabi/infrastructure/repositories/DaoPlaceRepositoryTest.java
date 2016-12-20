package pl.ipebk.tabi.infrastructure.repositories;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.concurrent.TimeUnit;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.presentation.model.place.PlaceDto;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import rx.observers.TestSubscriber;

public class DaoPlaceRepositoryTest extends DatabaseTest {
    private DaoPlaceRepository repository;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
        repository = new DaoPlaceRepository(databaseHelper.getPlaceDao());
    }

    @MediumTest public void test_loadsPlaceProperly() throws Exception {
        addToDatabase(givenPlace().withName("a").withPlate("TAB").and().withPlate("BAT").withOwnPlate());
        TestSubscriber<PlaceDto> testSubscriber = TestSubscriber.create();

        repository.loadByIdObservable(givenPlaceId()).subscribe(testSubscriber);
        testSubscriber.awaitValueCount(1, 500, TimeUnit.MILLISECONDS);
        PlaceDto loadedPlace = testSubscriber.getOnNextEvents().get(0);

        assertEquals(placeModel.name(), loadedPlace.name());
        assertEquals(placeModel.voivodeship(), loadedPlace.voivodeship());
        assertEquals(placeModel.powiat(), loadedPlace.powiat());
        assertEquals(placeModel.gmina(), loadedPlace.gmina());
        assertEquals(placeModel.plates().size(), loadedPlace.plates().size());

        testSubscriber.unsubscribe();
    }

    private PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    private AggregateId givenPlaceId() {
        return new AggregateId(placeModel.getId());
    }

    private void addToDatabase(PlaceModelAssembler assembler) {
        this.placeModel = assembler.assemble();
        databaseHelper.getPlaceDao().add(placeModel);
    }
}