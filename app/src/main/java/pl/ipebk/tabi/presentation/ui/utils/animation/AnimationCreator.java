/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import cimi.com.easeinterpolator.EaseCubicInOutInterpolator;
import pl.ipebk.tabi.R;

/**
 * Helper class to provide common animations across application
 */
public class AnimationCreator {
    private Context context;

    public AnimationCreator(Context context) {
        this.context = context;
    }

    public SearchBarAnimator getSearchAnimator() {
        return new SearchBarAnimator();
    }

    public DetailsAnimator getDetailsAnimator() {
        return new DetailsAnimator();
    }

    public final class SearchBarAnimator {
        private static final long SEARCH_BAR_MOVE_ANIM_DURATION = 400;
        private static final long SEARCH_BAR_SCALE_ANIM_DURATION = 500;
        private static final long SEARCH_BAR_SCALE_ANIM_DELAY = 0;

        SearchBarAnimator() { }

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

    public class DetailsAnimator {
        private static final long PANEL_FADE_ANIM_DURATION = 100;
        private static final long PANEL_SCALE_ANIM_DURATION = 3600;
        private float panelElevationStart;
        private float panelElevationEnd;

        DetailsAnimator() {
            panelElevationEnd = context.getResources().getDimensionPixelSize(R.dimen.Details_Elevation_Button);
            panelElevationStart = 0.0F;
        }

        public Animator createPanelEnterFadeInAnim(View target) {
            return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(0.6f, 1.0f)
                                        .setTarget(target).setInterpolator(new LinearInterpolator())
                                        .setDuration(PANEL_FADE_ANIM_DURATION).build();
        }

        public Animator createPanelEnterScaleAnim(View target) {
            AnimatorSet animator = new AnimatorSet();

            ObjectAnimator elevation = new AnimatorBuilder().setPropertyName("cardElevation")
                                                            .setFloatValues(panelElevationStart, panelElevationEnd)
                                                            .setTarget(target).setDuration(PANEL_SCALE_ANIM_DURATION)
                                                            .setInterpolator(new DecelerateInterpolator()).build();

            AnimatorBuilder scaleAnimationBuilder = new AnimatorBuilder()
                    .setFloatValues(.9F, 1.0F).setTarget(target)
                    .setInterpolator(new EaseCubicInOutInterpolator())
                    .setDuration(PANEL_SCALE_ANIM_DURATION);

            AnimatorSet scaleSet = new AnimatorSet();
            scaleSet.play(scaleAnimationBuilder.setPropertyName("scaleY").build())
                    .with(scaleAnimationBuilder.setPropertyName("scaleX").build());

            animator.play(scaleSet).with(elevation);
            return animator;
        }

        public Animator createIndicatorBackAnim(View target, float startValue) {
            AnimatorSet animator = new AnimatorSet();

            ObjectAnimator margin = new AnimatorBuilder().setPropertyName("topMargin")
                    .setFloatValues(startValue, 0f)
                    .setTarget(new MarginProxy(target))
                    .setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).build();
            animator.play(margin);
            return animator;
        }
    }
}
