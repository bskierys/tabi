/*
* author: Bartlomiej Kierys
* date: 2016-12-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.searchhistory;

import java.util.Date;

import javax.inject.Inject;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * Util factory of purpose of constructing search history with current time
 */
public class SearchHistoryFactory {
    private SearchTimeProvider timeProvider;

    @Inject public SearchHistoryFactory(SearchTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public SearchHistory create(AggregateId placeId, String searchedPlate, SearchType type) {
        Date now = timeProvider.now();
        return new AutoValue_SearchHistory(placeId, searchedPlate, now, type);
    }
}
