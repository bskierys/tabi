/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.place;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import rx.Observable;

/**
 * This repository exists only to load places with plates by it's id
 */
public interface PlaceRepository {
    Observable<Place> loadByIdObservable(AggregateId id);
}
