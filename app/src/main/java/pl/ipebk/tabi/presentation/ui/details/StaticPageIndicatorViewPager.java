/*
* author: Bartlomiej Kierys
* date: 2017-02-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Very simple extension of {@link ViewPager} that is not meant to be rendered on view, but simply to be passed to {@link
 * pl.ipebk.tabi.presentation.ui.custom.indicator.PageIndicator} to render it's static layout without hanging to real instance of {@link ViewPager}
 */
public class StaticPageIndicatorViewPager extends ViewPager {
    private CharSequence[] tabTitles;

    /**
     * One and only constructor for {@link StaticPageIndicatorViewPager}
     *
     * @param context Instance of {@link Context}
     * @param tabTitles Titles that will be displayed in your tabs
     */
    public StaticPageIndicatorViewPager(Context context, CharSequence... tabTitles) {
        super(context);
        this.tabTitles = tabTitles;
        init();
    }

    /**
     * Deprecated: To change item in {@link pl.ipebk.tabi.presentation.ui.custom.indicator.SearchTabPageIndicator} use its own method.
     */
    @Deprecated @Override public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    private void init() {
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override public int getCount() {
                return tabTitles.length;
            }

            @Override public boolean isViewFromObject(View view, Object object) {
                return false;
            }

            @Override public CharSequence getPageTitle(int position) {
                return tabTitles[position];
            }
        };

        this.setAdapter(pagerAdapter);
    }
}
