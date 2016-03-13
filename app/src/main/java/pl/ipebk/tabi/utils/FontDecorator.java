/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import pl.ipebk.tabi.R;

/**
 * Helper class to help apply {@link FontManager} fonts to TextViews.
 */
public class FontDecorator {

    /**
     * Initializes custom font for text view from its attributes. Use 'fontFamily'
     * attribute along with appropriate names from fonts.xml, to style your textView.
     * If font family is not set in attributes, default font will be applied
     *
     * @param textView Intence of {@link TextView} to decor with custom font
     * @param context  Context to acquire attributes from
     * @param attrs    Attributes to set
     */
    public static void initFromAttributes(TextView textView, Context context, AttributeSet attrs) {
        if (!textView.isInEditMode()) {
            // Fonts work as a combination of particular family and the style.
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Fonts);
            String family = a.getString(R.styleable.Fonts_fontFamily);
            int style = a.getInt(R.styleable.Fonts_android_textStyle, -1);

            a.recycle();

            Typeface typeface = FontManager.getInstance().get(family, style);
            textView.setTypeface(typeface);
        }
    }

    /**
     * Set the typeface based on the family and the style combination.
     *
     * @param family Font family name
     * @param style  style to apply.
     */
    public static void setCustomFont(TextView textView, String family, int style) {
        Typeface typeface = FontManager.getInstance().get(family, style);
        textView.setTypeface(typeface);
    }

    /**
     * Set the typeface. Regular typeface style will be applied.
     *
     * @param family Font family name
     */
    public static void setCustomFont(TextView textView, String family) {
        Typeface typeface = FontManager.getInstance().get(family, Typeface.NORMAL);
        textView.setTypeface(typeface);
    }
}
