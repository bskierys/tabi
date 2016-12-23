/*
* author: Bartlomiej Kierys
* date: 2016-06-02
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.rxbinding;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEventOnSubscribe;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables} for {@link RecyclerView}.
 */
public final class RxRecyclerViewExtension {
    /**
     * Create an observable of scroll events on {@code recyclerView}. It does not emit delta of scrolled points but
     * total scroll from top of screen.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code recyclerView}. Unsubscribe to free
     * this reference.
     */
    @CheckResult @NonNull
    public static Observable<RecyclerViewTotalScrollEvent> totalScrollEvents(
            @NonNull RecyclerView view) {
        checkNotNull(view, "view == null");
        return Observable.create(new RecyclerViewTotalScrollEventOnSubscribe(view));
    }
}
