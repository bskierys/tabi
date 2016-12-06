/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.place.Place;
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceRepository implements PlaceRepository {
    private PlaceDao dao;

    public DaoPlaceRepository(PlaceDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Place> loadByIdObservable(AggregateId id) {
        // TODO: 2016-12-04 factory to make domain place
        //return dao.getByIdObservable(id.getValue());
        return null;
    }
}
