package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.view.View;
import android.view.ViewGroup;

/**
 * Allows an ObjectAnimator to set/get height/width of a view
 */
public class SizeProxy {
    private View mView;

    public SizeProxy(View view) {
        mView = view;
    }

    public float getHeight() {
        return mView.getHeight();
    }

    public void setHeight(float height) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        lp.height = (int) height;
        mView.setLayoutParams(lp);
        mView.requestLayout();
    }

    public float getWidth() {
        return mView.getWidth();
    }

    public void setWidth(float width) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        lp.width = (int) width;
        mView.setLayoutParams(lp);
        mView.requestLayout();
    }
}
