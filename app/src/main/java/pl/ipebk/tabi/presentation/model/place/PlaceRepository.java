/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.place;

import pl.ipebk.tabi.presentation.model.AggregateId;
import rx.Observable;

/**
 * This repository exists only to load places with plates by it's id
 */
public interface PlaceRepository {
    /**
     * Load Place by it's id
     */
    Observable<PlaceDto> loadByIdObservable(AggregateId id);
}
