/*
* author: Bartlomiej Kierys
* date: 2016-06-02
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.rxbinding;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.jakewharton.rxbinding.view.ViewEvent;

/**
 * A scroll event on a recyclerView. It is designed to hold events of total amount of points scrolled in recyclerView.
 * <p>
 * <strong>Warning:</strong> Instances keep a strong reference to the recyclerView. Operators that cache instances have
 * the potential to leak the associated {@link Context}.
 */
public final class RecyclerViewTotalScrollEvent extends ViewEvent<RecyclerView> {
    @CheckResult @NonNull
    public static RecyclerViewTotalScrollEvent create(@NonNull RecyclerView recyclerView, int totalScrollX, int
            totalScrollY) {
        return new RecyclerViewTotalScrollEvent(recyclerView, totalScrollX, totalScrollY);
    }

    private final int totalScrollY;
    private final int totalScrollX;

    private RecyclerViewTotalScrollEvent(@NonNull RecyclerView view, int totalScrollX, int totalScrollY) {
        super(view);
        this.totalScrollY = totalScrollY;
        this.totalScrollX = totalScrollX;
    }

    public int totalScrollY() {
        return totalScrollY;
    }

    public int totalScrollX() {
        return totalScrollX;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecyclerViewTotalScrollEvent that = (RecyclerViewTotalScrollEvent) o;

        if (totalScrollY != that.totalScrollY) {
            return false;
        }
        return totalScrollX == that.totalScrollX;
    }

    @Override public int hashCode() {
        int result = totalScrollY;
        result = 31 * result + totalScrollX;
        return result;
    }

    @Override public String toString() {
        return "RecyclerViewTotalScrollEvent{" +
                "totalScrollY=" + totalScrollY +
                ", totalScrollX=" + totalScrollX +
                '}';
    }
}
