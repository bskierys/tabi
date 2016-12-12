/*
* author: Bartlomiej Kierys
* date: 2016-12-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.searchhistory;

import java.util.Date;

import javax.inject.Inject;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.readmodel.SearchType;

/**
 * TODO: Generic description. Replace with real one.
 */
public class SearchHistoryFactory {
    private SearchTimeProvider timeProvider;

    @Inject public SearchHistoryFactory(SearchTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public SearchHistory create(AggregateId placeId, String searchedPlate, SearchType type) {
        Date now = timeProvider.now();
        return new SearchHistory(placeId, searchedPlate, now, type);
    }
}
