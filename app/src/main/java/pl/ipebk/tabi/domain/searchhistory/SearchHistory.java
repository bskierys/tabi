/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.searchhistory;

import java.util.Date;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.BaseAggregateRoot;
import pl.ipebk.tabi.readmodel.SearchType;

/**
 * This aggregate exists purely to be inserted into database. That is why it does not have getter or setter for id. Id
 * in database models are autoincrement and can only by obtained from database.
 */
public class SearchHistory extends BaseAggregateRoot {
    private AggregateId placeId;
    private String plate;
    private Date timeSearched;
    private SearchType searchType;

    SearchHistory(AggregateId placeId, String plate, Date timeSearched, SearchType searchType) {
        this.placeId = placeId;
        this.plate = plate;
        this.timeSearched = timeSearched;
        this.searchType = searchType;
    }

    // TODO: 2016-12-04 correcting searched plate belongs here not in presenter

    // getters only for repo
    public long getPlaceId() { return placeId.getValue(); }
    public String getPlate() { return plate; }
    public Date getTimeSearched() { return timeSearched; }
    public SearchType getSearchType() { return searchType; }
}
