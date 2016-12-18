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
import pl.ipebk.tabi.domain.place.PlaceRepository;
import pl.ipebk.tabi.infrastructure.daos.PlaceDao;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.readmodel.LicensePlateDto;
import pl.ipebk.tabi.readmodel.PlaceDto;
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
        // TODO: 2016-12-04 factory to make domain place
        return dao.getByIdObservable(id.getValue()).map(model -> {
            List<LicensePlateDto> licensePlates = new ArrayList<>();
            for (PlateModel plate : model.plates()) {
                licensePlates.add(LicensePlateDto.create(plate.pattern(), plate.end()));
            }

            return PlaceDto.create(model.name(), model.type(), model.voivodeship(),
                                   model.powiat(), model.gmina(), licensePlates);
        });
    }
}
