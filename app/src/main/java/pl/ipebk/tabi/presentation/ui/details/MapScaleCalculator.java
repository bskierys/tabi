/*
* author: Bartlomiej Kierys
* date: 2016-12-20
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import javax.inject.Inject;

/**
 * Util class that helps calculating map scale based on current device screen density
 */
public class MapScaleCalculator {
    private Context context;

    @Inject public MapScaleCalculator(Context context) {
        this.context = context;
    }

    public float getScreenDensity(){
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();

        return metrics.density;
    }

    public int getMapScale(){
        return getScale(getScreenDensity());
    }

    /**
     * Densities for android are ldpi -> 0.75, mdpi -> 1.0, hdpi -> 1.5, xhdpi -> 2.0, xxhdpi -> 3.0, xxxhdpi -> 4.0.
     * Scale for map should match these values. Unfortunately non-premium users can only scale up to 2, so we use this
     * method as computing helper.
     *
     * @param density Android pixel density
     * @return Google static maps api scale
     */
    private int getScale(float density) {
        if (density < 2.0f) {
            return 1;
        } else {
            return 2;
        }
    }
}
