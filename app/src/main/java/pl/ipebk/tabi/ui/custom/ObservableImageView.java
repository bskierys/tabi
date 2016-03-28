/*
* author: Bartlomiej Kierys
* date: 2016-03-20
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.custom;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Dimple {@link ImageView} that allows to observe it's bounds through Rx observable.
 */
public class ObservableImageView extends ImageView {
    private Rect bounds;
    private PublishSubject<Rect> boundsStream;

    public ObservableImageView(Context context) {
        super(context);

        init();
    }

    public ObservableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ObservableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
