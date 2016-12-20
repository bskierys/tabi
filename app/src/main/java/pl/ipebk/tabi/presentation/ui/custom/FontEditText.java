/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import pl.ipebk.tabi.utils.FontDecorator;

/**
 * EditText with custom fonts. Custom font may be applied by fontFamily attribute or by {@link FontDecorator}
 */
public class FontEditText extends EditText {
    private FontDecorator decorator;

    public FontEditText(Context context) {
        super(context);
        // TODO: 2016-11-29 should be injected
        decorator = new FontDecorator(context, this);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        decorator = new FontDecorator(context, this);
        decorator.initFromAttributes(attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
