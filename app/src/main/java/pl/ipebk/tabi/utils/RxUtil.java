/*
* author: Bartlomiej Kierys
* date: 2016-11-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxUtil {

    /**
     * Unsubscribes {@link Subscription} that is not already unsubscribed
     */
    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /**
     * Unsubscribes {@link CompositeSubscription} that is not already unsubscribed
     */
    public static void unsubscribe(CompositeSubscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
