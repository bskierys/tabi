/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.canonicalmodel;

/**
 * Value Object to handles id of model items in app
 */
public final class AggregateId {
    private long id;

    public AggregateId(long id) {
        this.id = id;
    }

    public long getValue() {
        return id;
    }

    public boolean isValid() {
        return id > 0;
    }
}
