/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import org.mockito.ArgumentMatcher;

import pl.ipebk.tabi.canonicalmodel.AggregateId;

/**
 * TODO: Generic description. Replace with real one.
 */
public class AggregateIdMatcher implements ArgumentMatcher<AggregateId> {
    private final AggregateId expected;

    public AggregateIdMatcher(AggregateId expected) {
        this.expected = expected;
    }

    @Override public boolean matches(AggregateId argument) {
        if(argument == null && expected == null) {
            return true;
        }
        if(argument == null || expected == null) {
            return false;
        }

        return argument.getValue() == expected.getValue();
    }
}
