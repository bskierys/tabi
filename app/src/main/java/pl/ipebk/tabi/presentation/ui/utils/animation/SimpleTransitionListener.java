/*
* author: Bartlomiej Kierys
* date: 2017-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.utils.animation;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.Transition;

import rx.functions.Action1;

/**
 * Implementation of {@link android.transition.Transition.TransitionListener} that makes use of a builder instead of overriding all methods
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SimpleTransitionListener implements Transition.TransitionListener {
    private Builder builder;

    private SimpleTransitionListener(Builder builder) {
        this.builder = builder;
    }

    @Override public void onTransitionStart(Transition transition) {
        if (builder.startAction != null) {
            builder.startAction.call(transition);
        }
    }

    @Override public void onTransitionEnd(Transition transition) {
        transition.removeListener(this);
        if (builder.endAction != null) {
            builder.endAction.call(transition);
        }
    }

    @Override public void onTransitionCancel(Transition transition) {
        transition.removeListener(this);
        if (builder.cancelAction != null) {
            builder.cancelAction.call(transition);
        }
    }

    @Override public void onTransitionPause(Transition transition) {
        if (builder.pauseAction != null) {
            builder.pauseAction.call(transition);
        }
    }

    @Override public void onTransitionResume(Transition transition) {
        if (builder.resumeAction != null) {
            builder.resumeAction.call(transition);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static class Builder {
        private Action1<Transition> startAction;
        private Action1<Transition> endAction;
        private Action1<Transition> cancelAction;
        private Action1<Transition> pauseAction;
        private Action1<Transition> resumeAction;

        public Builder withOnStartAction(Action1<Transition> startAction) {
            this.startAction = startAction;
            return this;
        }

        public Builder withOnEndAction(Action1<Transition> endAction) {
            this.endAction = endAction;
            return this;
        }

        public Builder withOnCancelAction(Action1<Transition> cancelAction) {
            this.cancelAction = cancelAction;
            return this;
        }

        public Builder withOnPauseAction(Action1<Transition> pauseAction) {
            this.pauseAction = pauseAction;
            return this;
        }

        public Builder withOnResumeAction(Action1<Transition> resumeAction) {
            this.resumeAction = resumeAction;
            return this;
        }

        public Transition.TransitionListener build() {
            return new SimpleTransitionListener(this);
        }
    }
}
