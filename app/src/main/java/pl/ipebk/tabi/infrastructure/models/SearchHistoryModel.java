/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.models;

import com.google.auto.value.AutoValue;

import java.util.Date;

import pl.ipebk.tabi.infrastructure.base.Model;

@AutoValue
public abstract class SearchHistoryModel implements Model {
    private long id;
    public abstract long placeId();
    public abstract String plate();
    public abstract Date timeSearched();
    public abstract int searchType();

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public static SearchHistoryModel create(long id, long placeId, String plate, Date timeSearched, int searchType) {
        SearchHistoryModel history = new AutoValue_SearchHistoryModel(placeId, plate, timeSearched, searchType);
        history.setId(id);
        return history;
    }
}
