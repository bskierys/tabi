package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import pl.ipebk.tabi.R;

/**
 * Simple wrapper for {@link android.support.v7.widget.Toolbar} within {@link AppBarLayout}
 */
public class Toolbar extends AppBarLayout {
    private final static int DEFAULT_BACKGROUND_COLOR_RES = R.color.colorPrimary;

    private int layoutContentId;
    private Drawable background;

    public Toolbar(Context context) {
        super(context);
        init(null, 0);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Toolbar, defStyle, 0);
        layoutContentId = a.getResourceId(R.styleable.Toolbar_content, R.layout.toolbar_content_simple);
        background = a.getDrawable(R.styleable.Toolbar_android_background);
        if(background == null) {
            background = getResources().getDrawable(DEFAULT_BACKGROUND_COLOR_RES);
        }
        a.recycle();

        inflate(getContext(), R.layout.toolbar_core, this);
        this.setBackground(background);
        FrameLayout container = (FrameLayout) findViewById(R.id.toolbar_content);
        inflate(getContext(), layoutContentId, container);
    }
}
