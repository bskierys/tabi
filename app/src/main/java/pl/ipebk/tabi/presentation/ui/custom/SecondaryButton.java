/*
* author: Bartlomiej Kierys
* date: 2017-01-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.custom.font.FontButton;

/**
 * Instance of {@link android.widget.Button} that has apriopriate style for secondary behaviour
 */
public class SecondaryButton extends FrameLayout {
    @BindView(R.id.btn_inner) FontButton button;
    protected String buttonText;

    public SecondaryButton(Context context) {
        super(context);
        init(null);
    }

    public SecondaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SecondaryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SecondaryButton, 0, 0);
        try {
            buttonText = typedArray.getString(R.styleable.SecondaryButton_android_text);
        } finally {
            typedArray.recycle();
        }
        inflate(getContext(), R.layout.button_secondary, this);
        ButterKnife.bind(this);
        if (buttonText != null) {
            buttonText = buttonText.toUpperCase();
            button.setText(buttonText);
        }
    }

    public void setText(CharSequence text) {
        button.setText(text);
    }

    @Override public void setOnClickListener(OnClickListener listener) {
        ButterKnife.findById(this, R.id.btn_inner).setOnClickListener(listener);
    }
}
