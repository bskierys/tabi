/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils.rxbinding;

import android.animation.Animator;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain rx.functions.Action1 actions}
 * for {@link Animator}.
 */
public final class RxAnimator {
    /**
     * Create an observable of animator start events on {@code animator}.
     */
    @CheckResult @NonNull
    public static Observable<Animator> animationStart(@NonNull Animator animator) {
        checkNotNull(animator, "animator == null");

        final PublishSubject<Animator> animatorSubject = PublishSubject.create();
        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) { }

            @Override public void onAnimationEnd(Animator animator) {
                animatorSubject.onNext(animator);
            }

            @Override public void onAnimationCancel(Animator animator) { }

            @Override public void onAnimationRepeat(Animator animator) { }
        };
        animator.addListener(listener);

        return animatorSubject
                .asObservable()
                .doOnUnsubscribe(() -> animator.removeListener(listener));
    }

    /**
     * Create an observable of animator end events on {@code animator}.
     */
    @CheckResult @NonNull
    public static Observable<Animator> animationEnd(@NonNull Animator animator) {
        checkNotNull(animator, "animator == null");

        final PublishSubject<Animator> animatorSubject = PublishSubject.create();
        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) { }

            @Override public void onAnimationEnd(Animator animator) {
                animatorSubject.onNext(animator);
            }

            @Override public void onAnimationCancel(Animator animator) { }

            @Override public void onAnimationRepeat(Animator animator) { }
        };

        animator.addListener(listener);

        return animatorSubject
                .asObservable()
                .doOnUnsubscribe(() -> animator.removeListener(listener));
    }

    /**
     * Create an observable of animator cancel events on {@code animator}.
     */
    @CheckResult @NonNull
    public static Observable<Animator> animationCancel(@NonNull Animator animator) {
        checkNotNull(animator, "animator == null");

        final PublishSubject<Animator> animatorSubject = PublishSubject.create();
        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) { }

            @Override public void onAnimationEnd(Animator animator) { }

            @Override public void onAnimationCancel(Animator animator) {
                animatorSubject.onNext(animator);
            }

            @Override public void onAnimationRepeat(Animator animator) { }
        };

        animator.addListener(listener);

        return animatorSubject
                .asObservable()
                .doOnUnsubscribe(() -> animator.removeListener(listener));
    }

    /**
     * Create an observable of animator repeat events on {@code animator}.
     */
    @CheckResult @NonNull
    public static Observable<Animator> animationRepeat(@NonNull Animator animator) {
        checkNotNull(animator, "animator == null");

        final PublishSubject<Animator> animatorSubject = PublishSubject.create();
        final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) { }

            @Override public void onAnimationEnd(Animator animator) { }

            @Override public void onAnimationCancel(Animator animator) { }

            @Override public void onAnimationRepeat(Animator animator) {
                animatorSubject.onNext(animator);
            }
        };

        animator.addListener(listener);

        return animatorSubject
                .asObservable()
                .doOnUnsubscribe(() -> animator.removeListener(listener));
    }
}
