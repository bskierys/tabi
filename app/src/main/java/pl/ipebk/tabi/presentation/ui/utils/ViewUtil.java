package pl.ipebk.tabi.presentation.ui.utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Set of static methods that can help with computing views bounds and position
 */
public class ViewUtil {
    /**
     * Computes View left relative to whole screen.
     * Warning: {@link View#setX(float)} will set position in parent not this relative one
     *
     * @param myView View to compute bound for
     */
    public static int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getLeft();
        } else {
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
        }
    }

    /**
     * Computes View right relative to whole screen.
     * Warning: {@link View#setX(float)} will set position in parent not this relative one
     *
     * @param myView View to compute bound for
     */
    public static int getRelativeRight(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getRight();
        } else {
            return myView.getRight() + getRelativeRight((View) myView.getParent());
        }
    }

    /**
     * Computes View top relative to whole screen.
     * Warning: {@link View#setY(float)} will set position in parent not this relative one
     *
     * @param myView View to compute bound for
     */
    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getTop();
        } else {
            return myView.getTop() + getRelativeTop((View) myView.getParent());
        }
    }

    /**
     * Computes View bottom relative to whole screen.
     * Warning: {@link View#setY(float)} will set position in parent not this relative one
     *
     * @param myView View to compute bound for
     */
    public static int getRelativeBottom(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            return myView.getBottom();
        } else {
            return myView.getBottom() + getRelativeBottom((View) myView.getParent());
        }
    }

    /**
     * Get bounds of whole screen
     *
     * @param manager Manager to get instance of {@link Display}
     */
    public static Rect getScreenBounds(WindowManager manager) {
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            size.set(display.getWidth(), display.getHeight());
        }

        return new Rect(0, 0, size.x, size.y);
    }
}
