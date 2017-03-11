/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import javax.inject.Inject;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.utils.FontManager;

/**
 * Helper class to help apply {@link FontManager} fonts to TextViews.
 */
public class FontDecorator {
    private Context context;
    private TextView textView;
    @Inject FontManager fontManager;

    public FontDecorator(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
        // TODO: 2016-11-29 should have inject constructor
        if(!textView.isInEditMode()){
            App.get(context).getViewComponent().inject(this);
        }
    }

    /**
     * Initializes custom font for text view from its attributes. Use 'fontFamily'
     * attribute along with appropriate names from fonts.xml, to style your textView.
     * If font family is not set in attributes, default font will be applied
     *
     * @param attrs    Attributes to set
     */
    public void initFromAttributes(AttributeSet attrs) {
        if (!textView.isInEditMode()) {
            // Fonts work as a combination of particular family and the style.
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Fonts);
            String family = a.getString(R.styleable.Fonts_fontFamily);
            int style = a.getInt(R.styleable.Fonts_android_textStyle, -1);

            a.recycle();

            Typeface typeface = fontManager.get(family, style);
            textView.setTypeface(typeface);
        }
    }

    /**
     * Set the typeface based on the family and the style combination.
     *
     * @param family Font family name
     * @param style  style to apply.
     */
    public void setCustomFont(String family, int style) {
        Typeface typeface = fontManager.get(family, style);
        textView.setTypeface(typeface);
    }

    /**
     * Set the typeface. Regular typeface style will be applied.
     *
     * @param family Font family name
     */
    public void setCustomFont(String family) {
        Typeface typeface = fontManager.get(family, Typeface.NORMAL);
        textView.setTypeface(typeface);
    }
}
