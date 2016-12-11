/*
* author: Bartlomiej Kierys
* date: 2016-12-10
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assemblers;

import java.util.Date;

import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.readmodel.SearchType;

/**
 * TODO: Generic description. Replace with real one.
 */
public class SearchHistoryAssembler {
    public static final SearchType DEFAULT_SEARCH_TYPE = SearchType.PLACE;
    private static final String DEFAULT_PLATE = "TAB";

    private long placeId;
    private String plate;
    private Date timeSearched;
    private Integer searchType;

    public SearchHistoryAssembler searchedFor(PlaceModel place) {
        this.placeId = place.getId();
        this.plate = place.plates().get(0).pattern();
        return this;
    }

    public SearchHistoryAssembler forPlaceWithId(long id) {
        this.placeId = id;
        return this;
    }

    public SearchHistoryAssembler forPlate(String plate) {
        this.plate = plate;
        return this;
    }

    public SearchHistoryAssembler within(SearchType type) {
        this.searchType = type.ordinal();
        return this;
    }

    public SearchHistoryAssembler atTime(long time) {
        this.timeSearched = new Date(time);
        return this;
    }

    public SearchHistoryModel assemble() {
        if (placeId == 0) {
            return null;
        }

        if(plate == null) {
            plate = DEFAULT_PLATE;
        }

        if (timeSearched == null) {
            timeSearched = new Date(0);
        }

        if(searchType == null) {
            searchType = DEFAULT_SEARCH_TYPE.ordinal();
        }

        return SearchHistoryModel.create(placeId, plate, timeSearched, searchType);
    }
}
