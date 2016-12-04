/*
* author: Bartlomiej Kierys
* date: 2016-11-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import rx.Subscription;

public class RxUtil {

    /**
     * Unsubscribes subscription that is not already unsubscribed
     */
    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
