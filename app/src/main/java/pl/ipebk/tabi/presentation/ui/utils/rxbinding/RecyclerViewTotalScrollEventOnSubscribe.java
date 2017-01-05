/*
* author: Bartlomiej Kierys
* date: 2016-06-02
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.rxbinding;

import android.support.v7.widget.RecyclerView;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

final class RecyclerViewTotalScrollEventOnSubscribe implements Observable.OnSubscribe<RecyclerViewTotalScrollEvent> {
    final RecyclerView recyclerView;
    int totalScrollY;
    int totalScrollX;

    public RecyclerViewTotalScrollEventOnSubscribe(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override public void call(Subscriber<? super RecyclerViewTotalScrollEvent> subscriber) {
        checkUiThread();

        final RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalScrollX += dx;
                totalScrollY += dy;
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(RecyclerViewTotalScrollEvent.create(recyclerView, totalScrollX, totalScrollY));
                }
            }
        };
        recyclerView.addOnScrollListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                recyclerView.removeOnScrollListener(listener);
            }
        });
    }
}
