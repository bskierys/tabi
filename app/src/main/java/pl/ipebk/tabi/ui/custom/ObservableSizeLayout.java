/*
* author: Bartlomiej Kierys
* date: 2016-03-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Simple {@link FrameLayout} that allows to observe it's bounds through Rx observable.
 */
public class ObservableSizeLayout extends FrameLayout {
    private Rect bounds;
    private PublishSubject<Rect> boundsStream;

    public ObservableSizeLayout(Context context) {
        super(context);

        init();
    }

    public ObservableSizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ObservableSizeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        if (!isInEditMode()) {
            boundsStream = PublishSubject.create();
            bounds = new Rect();
        }
    }

    public Observable<Rect> getBoundsStream() {
        return boundsStream.asObservable();
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!isInEditMode()) {
            bounds.set(left, top, right, bottom);
            boundsStream.onNext(bounds);
        }
    }
}
