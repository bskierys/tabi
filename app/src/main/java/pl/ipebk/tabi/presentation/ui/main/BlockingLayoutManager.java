/*
* author: Bartlomiej Kierys
* date: 2016-05-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Custom implementation of {@link GridLayoutManager} that can be blocked for scrolling
 */
public class BlockingLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public BlockingLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BlockingLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public BlockingLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void lockScroll() {
        isScrollEnabled = false;
    }

    public void unlockScroll() {
        isScrollEnabled = true;
    }

    @Override public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollHorizontally();
    }
}
