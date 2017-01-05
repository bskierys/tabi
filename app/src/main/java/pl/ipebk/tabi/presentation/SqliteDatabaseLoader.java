/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.ipebk.tabi.infrastructure.openHelper.DatabaseOpenHelper;
import rx.Observable;
import rx.Subscriber;

/**
 * Implementation of {@link DatabaseLoader}. Responsible for initiating sqlite database
 */
public class SqliteDatabaseLoader implements DatabaseLoader {
    private final DatabaseOpenHelper databaseHelper;

    @Inject public SqliteDatabaseLoader(DatabaseOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override public Observable<Void> initDatabase() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                databaseHelper.init();
                subscriber.onCompleted();
            }
        });
    }
}
