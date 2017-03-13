/*
* author: Bartlomiej Kierys
* date: 2016-05-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import cimi.com.easeinterpolator.EaseCircularInInterpolator;
import cimi.com.easeinterpolator.EaseCircularOutInterpolator;
import cimi.com.easeinterpolator.EaseCubicInInterpolator;
import cimi.com.easeinterpolator.EaseCubicInOutInterpolator;
import cimi.com.easeinterpolator.EaseCubicOutInterpolator;
import cimi.com.easeinterpolator.EaseQuadInOutInterpolator;
import cimi.com.easeinterpolator.EaseQuadOutInterpolator;
import cimi.com.easeinterpolator.EaseQuintInInterpolator;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.utils.ViewUtil;

/**
 * Helper class to provide common animations across application
 */
public class AnimationCreator {
    private Context context;
    private float animSpeedScale;

    public AnimationCreator(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            animSpeedScale = Settings.Global.getFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
        } else {
            animSpeedScale = 1.0f;
        }
    }

    public SearchAnimator getSearchAnimator() {
        return new SearchAnimator();
    }

    public DetailsAnimator getDetailsAnimator() {
        return new DetailsAnimator();
    }

    public CategoryAnimator getCategoryAnimator() {
        return new CategoryAnimator();
    }

    public class SearchAnimator {
        private static final long SEARCH_BAR_MOVE_ANIM_DURATION = 400;
        private static final long SEARCH_BAR_SCALE_ANIM_DURATION = 500;
        private static final long SEARCH_BAR_SCALE_ANIM_DELAY = 0;
        private static final int LIST_ITEM_ENTER_DURATION = 400;
        private static final int LIST_ITEM_ENTER_DELAY = 120;

        SearchAnimator() { }

        public Animator createMoveAnim(View target, float startPos, float endPos) {
            long duration = (long) (SEARCH_BAR_MOVE_ANIM_DURATION * animSpeedScale);
            return new AnimatorBuilder().setPropertyName("y").setFloatValues(startPos, endPos)
                                        .setTarget(target).setInterpolator(new EaseCubicInOutInterpolator())
                                        .setDuration(duration).build();
        }

        public Animator createScaleUpAnim(View target) {
            return createScaleAnim(target, 1.0f, 1.2f);
        }

        public Animator createScaleDownAnim(View target) {
            return createScaleAnim(target, 1.2f, 1.0f);
        }

        private Animator createScaleAnim(View target, float startScale, float endScale) {
            long duration = (long) (SEARCH_BAR_SCALE_ANIM_DURATION * animSpeedScale);
            long delay = (long) (SEARCH_BAR_SCALE_ANIM_DELAY * animSpeedScale);

            AnimatorBuilder scaleAnimationBuilder = new AnimatorBuilder()
                    .setFloatValues(startScale, endScale).setTarget(target)
                    .setInterpolator(new EaseCubicInOutInterpolator())
                    .setDuration(duration);

            AnimatorSet scaleSet = new AnimatorSet();
            scaleSet.play(scaleAnimationBuilder.setPropertyName("scaleY").build())
                    .with(scaleAnimationBuilder.setPropertyName("scaleX").build());

            scaleSet.setStartDelay(delay);
            return scaleSet;
        }

        public Animator createFadeOutAnim(View target) {
            long duration = (long) (SEARCH_BAR_MOVE_ANIM_DURATION * animSpeedScale);
            return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(1.0f, 0.0f)
                                        .setTarget(target).setInterpolator(new LinearInterpolator())
                                        .setDuration(duration).build();
        }

        public Animator createFadeInAnim(View target) {
            long duration = (long) (SEARCH_BAR_MOVE_ANIM_DURATION * animSpeedScale);
            return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(0.0f, 1.0f)
                                        .setTarget(target).setInterpolator(new LinearInterpolator())
                                        .setDuration(duration).build();
        }

        public Animation createItemEnterAnim(int position) {
            long duration = (long) (LIST_ITEM_ENTER_DURATION * animSpeedScale);
            long delay = (long) (LIST_ITEM_ENTER_DELAY * animSpeedScale);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.category_item_enter);
            animation.setInterpolator(new EaseCubicOutInterpolator());
            animation.setDuration(duration);
            if (position > 0) {
                animation.setStartOffset(delay);
            }
            return animation;
        }

        public Animation createMainItemEnterAnim(int position) {
            long duration = (long) (LIST_ITEM_ENTER_DURATION * animSpeedScale);
            long delay = (long) (LIST_ITEM_ENTER_DELAY * animSpeedScale);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.main_item_enter);
            animation.setInterpolator(new EaseCubicOutInterpolator());
            animation.setDuration(duration);
            if (position > 0) {
                animation.setStartOffset(delay);
            }
            return animation;
        }
    }

    public class DetailsAnimator {
        private static final long PANEL_FADE_ANIM_DURATION = 300;
        private static final long PANEL_SCALE_ANIM_DURATION = 400;
        private static final long INDICATOR_BACK_ANIM_DURATION = 200;
        private static final long PANEL_FADE_ANIM_DELAY = 100;
        private static final long PANEL_SCALE_ANIM_DELAY = 200;
        private static final long DETAIL_SHARED_DURATION = 300;
        private static final long DETAIL_CONTENT_DURATION = 200;
        private static final long DETAIL_CONTENT_DELAY = 100;
        private static final float PANEL_SCALE_START_VALUE = .9F;
        private float panelElevationStart;
        private float panelElevationEnd;

        DetailsAnimator() {
            panelElevationEnd = context.getResources().getDimensionPixelSize(R.dimen.Details_Elevation_Button);
            panelElevationStart = 0.0F;
        }

        public void prepareViewForPanelAnim(View target) {
            target.setAlpha(0);
            target.setScaleX(PANEL_SCALE_START_VALUE);
            target.setScaleY(PANEL_SCALE_START_VALUE);
        }

        public Animator createPanelEnterFadeInAnim(View target) {
            long delay = (long) (PANEL_FADE_ANIM_DELAY * animSpeedScale);
            long duration = (long) (PANEL_FADE_ANIM_DURATION * animSpeedScale);
            return new AnimatorBuilder().setPropertyName("alpha").setFloatValues(0.6f, 1.0f)
                                        .setTarget(target).setInterpolator(new LinearInterpolator())
                                        .setStartDelay(delay)
                                        .setDuration(duration).build();
        }

        public Animator createPanelEnterScaleAnim(View target) {
            AnimatorSet animator = new AnimatorSet();
            long delay = (long) (PANEL_SCALE_ANIM_DELAY * animSpeedScale);
            long duration = (long) (PANEL_SCALE_ANIM_DURATION * animSpeedScale);

            ObjectAnimator elevation = new AnimatorBuilder().setPropertyName("cardElevation")
                                                            .setFloatValues(panelElevationStart, panelElevationEnd)
                                                            .setDuration(duration)
                                                            .setStartDelay(delay)
                                                            .setTarget(target)
                                                            .setInterpolator(new EaseCubicOutInterpolator()).build();

            AnimatorBuilder scaleAnimationBuilder = new AnimatorBuilder()
                    .setFloatValues(PANEL_SCALE_START_VALUE, 1.0F).setTarget(target)
                    .setDuration(duration)
                    .setInterpolator(new EaseCubicOutInterpolator());

            AnimatorSet scaleSet = new AnimatorSet();
            scaleSet.setStartDelay(delay);
            scaleSet.play(scaleAnimationBuilder.setPropertyName("scaleY").build())
                    .with(scaleAnimationBuilder.setPropertyName("scaleX").build());

            animator.play(scaleSet).with(elevation);
            return animator;
        }

        public Animator createIndicatorBackAnim(View target, float startValue) {
            AnimatorSet animator = new AnimatorSet();

            long duration = (long) (INDICATOR_BACK_ANIM_DURATION * animSpeedScale);

            ObjectAnimator margin = new AnimatorBuilder().setPropertyName("topMargin")
                                                         .setFloatValues(startValue, 0f)
                                                         .setTarget(new MarginProxy(target))
                                                         .setDuration(duration)
                                                         .setInterpolator(new DecelerateInterpolator()).build();
            animator.play(margin);
            return animator;
        }

        public Animator createDetailActionAnim(View target, Rect startBounds, Rect endBounds) {
            long duration = (long) (400 * animSpeedScale);
            AnimatorSet set = new AnimatorSet();
            AnimatorBuilder builder = new AnimatorBuilder().setTarget(new SizeProxy(target))
                                                           .setDuration(duration)
                                                           .setInterpolator(new EaseCubicInOutInterpolator());
            ObjectAnimator height = builder.setPropertyName("height")
                                           .setFloatValues(startBounds.height(), endBounds.height()).build();
            ObjectAnimator width = builder.setPropertyName("width")
                                          .setFloatValues(startBounds.width(), endBounds.width()).build();
            ObjectAnimator x = builder.setTarget(target)
                                      .setPropertyName("x")
                                      .setFloatValues(startBounds.left, endBounds.left).build();
            ObjectAnimator y = builder.setTarget(target)
                                      .setPropertyName("y")
                                      .setFloatValues(startBounds.top, endBounds.top).build();
            set.play(height).with(width).with(x).with(y);

            return set;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @NonNull public Transition createContentFadeInTransition(View... targets) {
            long duration = (long) (DETAIL_CONTENT_DURATION * animSpeedScale);
            long delay = (long) (DETAIL_CONTENT_DELAY * animSpeedScale);
            Transition fade = new Fade(Fade.IN);
            for (View target : targets) {
                fade.addTarget(target);
            }
            fade.setDuration(duration);
            fade.setStartDelay(delay);
            fade.setInterpolator(new EaseQuadOutInterpolator());
            return fade;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @NonNull public Transition createBgFadeInTransition(View target) {
            long duration = (long) (DETAIL_SHARED_DURATION * animSpeedScale);
            Transition transition = new Fade(Fade.IN);
            transition.addTarget(target);
            transition.setDuration(duration);
            return transition;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @NonNull public Transition createBgFadeOutTransition(View target) {
            long duration = (long) (DETAIL_SHARED_DURATION * animSpeedScale);
            Transition transition = new Fade(Fade.OUT);
            transition.addTarget(target);
            transition.setDuration(duration);
            return transition;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void alterSharedTransition(Transition transition) {
            long duration = (long) (DETAIL_SHARED_DURATION * animSpeedScale);
            transition.setInterpolator(new EaseQuadInOutInterpolator())
                      .setDuration(duration);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public Animator createCopyAnim(Rect viewBounds, View target) {
            long firstAnimDuration = (long) (400 * animSpeedScale);
            long secondAnimDuration = (long) (400 * animSpeedScale);

            int cx = viewBounds.width() / 2;
            int cy = viewBounds.height() / 2;
            int copyEndElevation = context.getResources().getDimensionPixelSize(R.dimen.Details_Elevation_Copy_Anim);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            long motionEndPointY = ViewUtil.getScreenBounds(wm).bottom;
            AnimatorSet fullAnimation = new AnimatorSet();

            AnimatorSet firstAnim = new AnimatorSet();

            Animator elevation = new AnimatorBuilder()
                    .setFloatValues(0f, copyEndElevation).setDuration(firstAnimDuration)
                    .setPropertyName("elevation").setTarget(target)
                    .setInterpolator(new EaseCubicInInterpolator()).build();
            firstAnim.play(elevation);

            AnimatorSet secondAnim = new AnimatorSet();

            Animator hideCircle = ViewAnimationUtils.createCircularReveal(target, cx, cy, viewBounds.width(), 32);
            hideCircle.setDuration(secondAnimDuration);
            hideCircle.setInterpolator(new EaseCubicOutInterpolator());

            Animator moveOutOfScreen = new AnimatorBuilder()
                    .setFloatValues(viewBounds.top, motionEndPointY)
                    .setDuration(secondAnimDuration).setPropertyName("y").setTarget(target)
                    .setInterpolator(new EaseCubicInInterpolator())
                    .setPropertyName("y").build();

            secondAnim.play(hideCircle).with(moveOutOfScreen);
            fullAnimation.play(secondAnim).after(firstAnim);
            return fullAnimation;
        }
    }

    public class CategoryAnimator {
        private static final long CATEGORY_SHARED_DURATION = 300;

        CategoryAnimator() {}

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void alterSharedTransition(Transition transition) {
            long duration = (long) (CATEGORY_SHARED_DURATION * animSpeedScale);
            transition.setInterpolator(new EaseCubicInOutInterpolator())
                      .setDuration(duration);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @NonNull public Transition createBgFadeInTransition() {
            long duration = (long) (CATEGORY_SHARED_DURATION * animSpeedScale);
            Transition transition = new Fade(Fade.IN);
            transition.setDuration(duration);
            return transition;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @NonNull public Transition createBgFadeOutTransition() {
            long duration = (long) (100 * animSpeedScale);
            Transition transition = new Fade(Fade.OUT);
            transition.setDuration(duration);
            return transition;
        }
    }
}
