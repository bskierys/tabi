/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import pl.ipebk.tabi.utils.FontDecorator;

/**
 * EditText with custom fonts. Custom font may be applied by fontFamily attribute or by {@link FontDecorator}
 */
public class FontEditText extends EditText {
    public FontEditText(Context context) {
        super(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontDecorator.initFromAttributes(this, context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontDecorator.initFromAttributes(this, context, attrs);
    }

    public void setCustomFont(String fontFamily) {
        FontDecorator.setCustomFont(this, fontFamily);
    }

    public void setCustomFont(String fontFamily, int style) {
        FontDecorator.setCustomFont(this, fontFamily, style);
    }
}
