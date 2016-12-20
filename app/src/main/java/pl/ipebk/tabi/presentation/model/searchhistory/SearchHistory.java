/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.searchhistory;

import com.google.auto.value.AutoValue;

import java.util.Date;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * This aggregate exists purely to be inserted into database. That is why it does not have getter or setter for id. Id
 * in database models are autoincrement and can only by obtained from database.
 */
@AutoValue
public abstract class SearchHistory {
    public abstract AggregateId placeId();
    public abstract String plate();
    public abstract Date timeSearched();
    public abstract SearchType searchType();
}
