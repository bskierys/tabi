/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.animation.ObjectAnimator;
import android.view.animation.Interpolator;

/**
 * Builder class to help building {@link ObjectAnimator} class objects in single chain.
 */
public class AnimatorBuilder {
    private String propertyName;
    private float[] floatValues;
    private Object target;
    private Long duration;
    private Long startDelay;
    private Interpolator interpolator;

    public ObjectAnimator build() {
        ObjectAnimator animation = new ObjectAnimator();
        if (propertyName != null) {
            animation.setPropertyName(propertyName);
        }
        if (floatValues != null) {
            animation.setFloatValues(floatValues);
        }
        if (target != null) {
            animation.setTarget(target);
        }
        if (duration != null) {
            animation.setDuration(duration);
        }
        if (startDelay != null) {
            animation.setStartDelay(startDelay);
        }
        if (interpolator != null) {
            animation.setInterpolator(interpolator);
        }
        return animation;
    }

    public AnimatorBuilder setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public AnimatorBuilder setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public AnimatorBuilder setFloatValues(float... values) {
        this.floatValues = values;
        return this;
    }

    public AnimatorBuilder setTarget(Object target) {
        this.target = target;
        return this;
    }

    public AnimatorBuilder setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public AnimatorBuilder setStartDelay(long delay) {
        this.startDelay = delay;
        return this;
    }
}
