/*
* author: Bartlomiej Kierys
* date: 2016-03-15
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.TimeUnit;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Special decor for overscrolling, that allows you to bound to scroll and release
 * streams with rx java observables.
 */
public class ObservableVerticalOverScrollBounceEffectDecorator extends VerticalOverScrollBounceEffectDecorator {
    private Float lastOffset;
    private MyBounceBackState fakeBounceBackState;
    private PublishSubject<Float> releaseSubject;
    private PublishSubject<Float> scrollSubject;

    public ObservableVerticalOverScrollBounceEffectDecorator(IOverScrollDecoratorAdapter viewAdapter, float touchDragRatioFwd, float touchDragRatioBck, float
            decelerateFactor) {
        super(viewAdapter, touchDragRatioFwd, touchDragRatioBck, decelerateFactor);
        fakeBounceBackState = new MyBounceBackState(decelerateFactor);
        releaseSubject = PublishSubject.create();
        scrollSubject = PublishSubject.create();
    }

    public Observable<Float> getScrollEventStream() {
        return scrollSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Float> getReleaseEventStream() {
        return releaseSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            releaseSubject.onNext(lastOffset);
            lastOffset = null;
            setupBackScroll();
        }
        return super.onTouch(v, event);
    }

    private void setupBackScroll() {
        long duration = computeBackAnimationDuration();

        if (duration > 0) {
            Subscription animSub = Observable.interval(15, TimeUnit.MILLISECONDS)
                                             .subscribe(m -> {
                                                 float y = mViewAdapter.getView().getY();
                                                 scrollSubject.onNext(y);
                                             });

            releaseSubject.doOnUnsubscribe(() -> RxUtil.unsubscribe(animSub));
            scrollSubject.doOnUnsubscribe(() -> RxUtil.unsubscribe(animSub));

            Observable.just(animSub).delay(duration, TimeUnit.MILLISECONDS).subscribe(RxUtil::unsubscribe);
        }
    }

    private long computeBackAnimationDuration() {
        AnimatorSet a = (AnimatorSet) fakeBounceBackState.createAnimator();
        long duration = 0;
        for (Animator ad : a.getChildAnimations()) {
            duration += ad.getDuration();
        }
        return duration;
    }

    @Override protected void translateView(View view, float offset) {
        super.translateView(view, offset);
        lastOffset = offset;
        scrollSubject.onNext(offset);
    }

    @Override protected void enterState(IDecoratorState state) {
        super.enterState(state);
        if (state instanceof IdleState) {
            scrollSubject.onNext(0f);
        }
    }

    private class MyBounceBackState extends BounceBackState {
        private MyBounceBackState(float decelerateFactor) {
            super(decelerateFactor);
        }

        @Override protected Animator createAnimator() {
            return super.createAnimator();
        }
    }
}
