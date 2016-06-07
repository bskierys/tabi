/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.animation.ObjectAnimator;
import android.view.animation.Interpolator;

/**
 * TODO: Generic description. Replace with real one.
 */
public class AnimatorBuilder {
    private String propertyName;
    private float[] values;
    private Object target;
    private Long duration;
    private Long startDelay;
    private Interpolator interpolator;

    public ObjectAnimator build() {
        ObjectAnimator animation = new ObjectAnimator();
        if(propertyName !=null){
            animation.setPropertyName(propertyName);
        }
        if(values !=null){
            animation.setFloatValues(values);
        }
        if(target !=null){
            animation.setTarget(target);
        }
        if(duration!=null){
            animation.setDuration(duration);
        }
        if(startDelay!=null){
            animation.setStartDelay(startDelay);
        }
        if(interpolator!=null){
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
        this.values = values;
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
