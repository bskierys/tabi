/*
* author: Bartlomiej Kierys
* date: 2017-01-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Instance of {@link android.widget.Button} that has apriopriate style for secondary behaviour
 */
public class SecondaryButton extends FontButton{
    public SecondaryButton(Context context) {
        super(context);
    }

    public SecondaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
