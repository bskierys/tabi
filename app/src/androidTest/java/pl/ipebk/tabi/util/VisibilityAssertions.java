/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.util;

import android.support.test.espresso.ViewAssertion;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;

/**
 * Assertion to help determine view visibility.
 */
public class VisibilityAssertions {
    public static ViewAssertion isVisible() {
        return (view, noView) -> assertThat(view, new VisibilityMatcher(View.VISIBLE));
    }

    public static ViewAssertion isGone() {
        return (view, noView) -> assertThat(view, new VisibilityMatcher(View.GONE));
    }

    public static ViewAssertion isInvisible() {
        return (view, noView) -> assertThat(view, new VisibilityMatcher(View.INVISIBLE));
    }

    private static class VisibilityMatcher extends BaseMatcher<View> {

        private int visibility;

        public VisibilityMatcher(int visibility) {
            this.visibility = visibility;
        }

        @Override public void describeTo(Description description) {
            String visibilityName;
            if (visibility == View.GONE) visibilityName = "GONE";
            else if (visibility == View.VISIBLE) visibilityName = "VISIBLE";
            else visibilityName = "INVISIBLE";
            description.appendText("View visibility must has equals " + visibilityName);
        }

        @Override public boolean matches(Object o) {

            if (o == null) {
                if (visibility == View.GONE || visibility == View.INVISIBLE) return true;
                else if (visibility == View.VISIBLE) return false;
            }

            if (!(o instanceof View))
                throw new IllegalArgumentException("Object must be instance of View. Object is instance of " + o);
            return ((View) o).getVisibility() == visibility;
        }
    }
}
