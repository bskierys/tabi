/*
* author: Bartlomiej Kierys
* date: 2017-01-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Button with custom fonts. Custom font may be applied by fontFamily attribute or by {@link FontDecorator}
 */
public class FontButton extends Button {
    private FontDecorator decorator;

    public FontButton(Context context) {
        super(context);
        decorator = new FontDecorator(context, this);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        decorator = new FontDecorator(context, this);
        decorator.initFromAttributes(attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
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