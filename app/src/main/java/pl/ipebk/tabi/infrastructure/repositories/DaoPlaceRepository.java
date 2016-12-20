/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import javax.inject.Inject;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.presentation.model.place.PlaceDto;
import pl.ipebk.tabi.presentation.model.place.PlaceRepository;
import rx.Observable;

/**
 * Implementation of {@link PlaceRepository} that uses app's dao
 */
public class DaoPlaceRepository implements PlaceRepository {
    private PlaceDao dao;

    @Inject DaoPlaceRepository(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getPlaceDao();
    }

    /**
     * Internal constructor for tests
     */
    DaoPlaceRepository(PlaceDao dao) {
        this.dao = dao;
    }

    @Override public Observable<PlaceDto> loadByIdObservable(AggregateId id) {
        return dao.getByIdObservable(id.getValue()).map(PlaceModel::dto);
    }
}
