/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class RxEventBus {
    private final PublishSubject<Object> busSubject;

    @Inject public RxEventBus() {
        busSubject = PublishSubject.create();
    }

    public void post(Object object) {
        busSubject.onNext(object);
    }

    public Observable<Object> toObservable() {
        return busSubject;
    }

    public <T> Observable<T> toObservable(final Class<T> toClass) {
        return busSubject.ofType(toClass).cast(toClass);
    }

    public boolean hasObservers() {
        return busSubject.hasObservers();
    }
}