/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assertions;

/**
 * Helper constants for accessing items in test list in more natural way
 */
public final class Order {
    // prevent creation
    @SuppressWarnings("unused") private Order() {}

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;
    public static final int LAST = -1;
}
