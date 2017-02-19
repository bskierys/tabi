/*
* author: Bartlomiej Kierys
* date: 2016-03-15
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.view.MotionEvent;
import android.view.View;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Special decor for overscrolling, that allows you to bound to scroll and release
 * streams with rx java observables.
 */
public class ObservableVerticalOverScrollBounceEffectDecorator extends VerticalOverScrollBounceEffectDecorator {
    private Float lastOffset;
    private PublishSubject<Float> releaseSubject;
    private PublishSubject<Float> scrollSubject;

    public ObservableVerticalOverScrollBounceEffectDecorator(IOverScrollDecoratorAdapter viewAdapter) {
        super(viewAdapter);
        releaseSubject = PublishSubject.create();
        scrollSubject = PublishSubject.create();
    }

    public ObservableVerticalOverScrollBounceEffectDecorator(IOverScrollDecoratorAdapter viewAdapter, float touchDragRatioFwd, float touchDragRatioBck, float decelerateFactor) {
        super(viewAdapter, touchDragRatioFwd, touchDragRatioBck, decelerateFactor);
        releaseSubject = PublishSubject.create();
        scrollSubject = PublishSubject.create();
    }

    public Observable<Float> getScrollEventStream() {
        return scrollSubject.asObservable();
    }

    public Observable<Float> getReleaseEventStream() {
        return releaseSubject.asObservable();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            releaseSubject.onNext(lastOffset);
            lastOffset = null;
        }
        return super.onTouch(v, event);
    }

    @Override protected void translateView(View view, float offset) {
        super.translateView(view, offset);
        // TODO: 2017-02-19 connect with bck and fwd
        //offset = offset * 3;
        lastOffset = offset;
        scrollSubject.onNext(offset * 3);
    }
}
