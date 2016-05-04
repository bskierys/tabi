/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.models;

import java.util.Date;

import pl.ipebk.tabi.database.base.ModelInterface;

public class SearchHistory implements ModelInterface {
    private long id;
    private long placeId;
    private String plate;
    private Date timeSearched;
    private SearchType searchType;

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Date getTimeSearched() {
        return timeSearched;
    }

    public void setTimeSearched(Date timeSearched) {
        this.timeSearched = timeSearched;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
}
