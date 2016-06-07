/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import cimi.com.easeinterpolator.EaseCubicInOutInterpolator;

/**
 * TODO: Generic description. Replace with real one.
 */
public class AnimationHelper {
    private static final long SEARCH_BAR_MOVE_ANIM_DURATION = 400;
    private static final long SEARCH_BAR_SCALE_ANIM_DURATION = 500;
    private static final long SEARCH_BAR_SCALE_ANIM_DELAY = 0;

    public Animator createMoveAnim(View target, float startPos, float endPos) {
        return new AnimatorBuilder().setPropertyName("y").setFloatValues(startPos, endPos)
                                    .setTarget(target).setInterpolator(new EaseCubicInOutInterpolator())
                                    .setDuration(SEARCH_BAR_MOVE_ANIM_DURATION).build();
    }

    public Animator createScaleUpAnim(View target) {
        return createScaleAnim(target, 1.0f, 1.2f);
    }

    public Animator createScaleDownAnim(View target) {
        return createScaleAnim(target, 1.2f, 1.0f);
    }

    private Animator createScaleAnim(View target, float startScale, float endScale) {
        AnimatorBuilder scaleAnimationBuilder = new AnimatorBuilder()
                .setFloatValues(startScale, endScale).setTarget(target)
                .setInterpolator(new EaseCubicInOutInterpolator())
                .setDuration(SEARCH_BAR_SCALE_ANIM_DURATION);

        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.play(scaleAnimationBuilder.setPropertyName("scaleY").build())
                .with(scaleAnimationBuilder.setPropertyName("scaleX").build());

        scaleSet.setStartDelay(SEARCH_BAR_SCALE_ANIM_DELAY);
        return scaleSet;
    }

    public Animator createFadeOutAnim(View target) {
        return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(1.0f, 0.0f)
                                    .setTarget(target).setInterpolator(new LinearInterpolator())
                                    .setDuration(SEARCH_BAR_MOVE_ANIM_DURATION).build();
    }

    public Animator createFadeInAnim(View target) {
        return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(0.0f, 1.0f)
                                    .setTarget(target).setInterpolator(new LinearInterpolator())
                                    .setDuration(SEARCH_BAR_MOVE_ANIM_DURATION).build();
    }
}
