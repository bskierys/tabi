package pl.ipebk.tabi.infrastructure.repositories;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.concurrent.TimeUnit;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.place.Place;
import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import rx.observers.TestSubscriber;

import static org.junit.Assert.*;

public class DaoPlaceRepositoryTest extends DatabaseTest {
    private DaoPlaceRepository repository;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
        repository = new DaoPlaceRepository(databaseHelper.getPlaceDao());
    }

    @MediumTest public void test_loadsPlaceProperly() throws Exception {
        addToDatabase(givenPlace().withName("a").withPlate("TAB").and().withPlate("BAT").withOwnPlate());
        TestSubscriber<Place> testSubscriber = TestSubscriber.create();

        repository.loadByIdObservable(givenPlaceId()).subscribe(testSubscriber);
        testSubscriber.awaitValueCount(1, 500, TimeUnit.MILLISECONDS);
        Place loadedPlace = testSubscriber.getOnNextEvents().get(0);

        assertEquals(placeModel.name(), loadedPlace.getName());
        assertEquals(placeModel.voivodeship(), loadedPlace.getVoivodeship());
        assertEquals(placeModel.powiat(), loadedPlace.getPowiat());
        assertEquals(placeModel.gmina(), loadedPlace.getGmina());
        assertEquals(placeModel.plates().size(), loadedPlace.getPlates().size());

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