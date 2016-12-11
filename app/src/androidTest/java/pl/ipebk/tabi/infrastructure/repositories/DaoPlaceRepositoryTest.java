package pl.ipebk.tabi.infrastructure.repositories;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;

import static org.junit.Assert.*;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceRepositoryTest extends DatabaseTest {
    private DaoPlaceRepository repository;
    private PlaceModelAssembler placeModelAssembler;
    private PlaceModel placeModel;

    @Override public void setUp() throws Exception {
        super.setUp();
        repository = new DaoPlaceRepository(databaseHelper.getPlaceDao());
    }

    // TODO: 2016-12-11 load test when other tests refactored
}