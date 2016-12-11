/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.place.LicensePlate;
import pl.ipebk.tabi.domain.place.Place;
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DaoPlaceRepository implements PlaceRepository {
    private PlaceDao dao;

    @Inject DaoPlaceRepository(DatabaseOpenHelper openHelper) {
        this.dao = openHelper.getPlaceDao();
    }

    // TODO: 2016-12-10 what to do with that constructor
    public DaoPlaceRepository(PlaceDao dao) {
        this.dao = dao;
    }

    @Override public Observable<Place> loadByIdObservable(AggregateId id) {
        // TODO: 2016-12-04 factory to make domain place
        return dao.getByIdObservable(id.getValue()).map(model ->{
            List<LicensePlate> licensePlates = new ArrayList<LicensePlate>();
            for (PlateModel plate: model.plates()) {
                licensePlates.add(new LicensePlate(new AggregateId(model.getId()), plate.pattern(), plate.end()));
            }

            Place place = new Place(model.name(), model.type(),
                                    model.voivodeship(), model.powiat(),
                                    model.gmina(), licensePlates, model.hasOwnPlate());

            return place;
        });
    }
}
