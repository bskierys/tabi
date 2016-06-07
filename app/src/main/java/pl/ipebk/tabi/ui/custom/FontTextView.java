/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import pl.ipebk.tabi.utils.FontDecorator;

/**
 * TextView with custom fonts. Custom font may be applied by fontFamily attribute or by {@link FontDecorator}
 */
public class FontTextView extends TextView {
    private FontDecorator decorator;

    public FontTextView(Context context) {
        super(context);
        decorator = new FontDecorator(context, this);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        decorator = new FontDecorator(context, this);
        decorator.initFromAttributes(attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        decorator = new FontDecorator(context, this);
        decorator.initFromAttributes(attrs);
    }

    public void setCustomFont(String fontFamily) {
        decorator.setCustomFont(fontFamily);
    }

    public void setCustomFont(String fontFamily, int style) {
        decorator.setCustomFont(fontFamily, style);
    }
}
