/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * TODO: Generic description. Replace with real one.
 */
public class BaseAggreagateRoot {
    protected AggregateId aggregateId;

    public AggregateId getAggregateId() {
        return aggregateId;
    }
}
