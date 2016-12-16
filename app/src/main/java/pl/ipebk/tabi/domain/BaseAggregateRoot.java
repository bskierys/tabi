/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * Base class for all aggregates
 */
public class BaseAggregateRoot {
    protected AggregateId aggregateId;

    public AggregateId getAggregateId() {
        return aggregateId;
    }
}
