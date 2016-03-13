/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import pl.ipebk.tabi.utils.FontDecorator;

/**
 * Button with custom fonts.
 * Custom font may be applied by fontFamily attribute
 * or by {@link FontDecorator}
 */
public class FontButton extends Button {
    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontDecorator.initFromAttributes(this, context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontDecorator.initFromAttributes(this, context, attrs);
    }
}
