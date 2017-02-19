/*
* author: Bartlomiej Kierys
* date: 2017-02-19
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

/**
 * Allows an ObjectAnimator to set/get margins of a view
 */
public class MarginProxy {
    private View mView;

    public MarginProxy(View view) {
        mView = view;
    }

    public float getLeftMargin() {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        return lp.leftMargin;
    }

    public void setLeftMargin(float margin) {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        lp.setMargins((int) margin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
        mView.requestLayout();
    }

    public float getTopMargin() {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        return lp.topMargin;
    }

    public void setTopMargin(float margin) {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        lp.setMargins(lp.leftMargin, (int) margin, lp.rightMargin, lp.bottomMargin);
        mView.requestLayout();
    }

    public float getRightMargin() {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        return lp.rightMargin;
    }

    public void setRightMargin(float margin) {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, (int) margin, lp.bottomMargin);
        mView.requestLayout();
    }

    public float getBottomMargin() {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        return lp.bottomMargin;
    }

    public void setBottomMargin(float margin) {
        MarginLayoutParams lp = (MarginLayoutParams) mView.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, (int) margin);
        mView.requestLayout();
    }
}
