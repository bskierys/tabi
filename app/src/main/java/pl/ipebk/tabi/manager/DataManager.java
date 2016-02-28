/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.manager;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import rx.Observable;
import rx.Subscriber;

/**
 * Responsible for data management across application.
 * Should be included in all presenters to propagate data.
 */
@Singleton
public class DataManager {
    private final DatabaseOpenHelper databaseHelper;

    @Inject public DataManager(DatabaseOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public DatabaseOpenHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public Observable<Void> initDatabase() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                databaseHelper.init();
                subscriber.onCompleted();
            }
        });
    }
}
