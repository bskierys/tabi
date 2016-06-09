/*
* author: Bartlomiej Kierys
* date: 2015-06-30
* email: bartlomiej.kierys@imed24.pl
*/
package com.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.viewpagerindicator.R;

/**
 * Custom made tab indicator. Tab is indicated by moving background color and arrow at the bottom. Icon, text, arrow,
 * background and selected color can be set either by attributes or by methods. Indicator can also fade away. Set it to
 * your viewPager through {@link #setViewPager(ViewPager)} method. Tab can be changed be dragging on viewPager, dragging
 * on indicator or just clicking on desired tab.
 */
public class FancyTabPageIndicator extends UnderlinePageIndicator {
    //region Constants
    private static final CharSequence EMPTY_TITLE = "";
    private static final int ACTIVE_ALPHA = 100;
    private static final int INACTIVE_ALPHA = 54;
    private static final int DEFAULT_TEXT_SIZE = 18;
    private static final int DEFAULT_FRONT_COLOR = 0xFFF;
    private static final int DEFAULT_ARROW_COLOR = Color.parseColor("#eeeeee");
    //endregion

    //region Fields
    private Paint arrowPaint;
    private Paint iconPaint;
    private TextPaint textPaint;

    private Bitmap menuArrow;
    private Bitmap[] icons;
    private RectF[] bounds;
    private CharSequence[] titles;

    private int itemCount;
    private float textPadding;
    //endregion

    //region Properties
    private int textSize;
    private int frontColor;
    private int arrowColor;
    //endregion

    //region Construction
    public FancyTabPageIndicator(Context context) {
        super(context);
        init(null);
    }

    public FancyTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FancyTabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FancyTabPageIndicator, 0, 0);

        // icon
        if (frontColor == 0) {
            frontColor = a.getColor(R.styleable.FancyTabPageIndicator_frontColor, DEFAULT_FRONT_COLOR);
        }
        iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ColorFilter iconCf = new PorterDuffColorFilter(frontColor, PorterDuff.Mode.MULTIPLY);
        iconPaint.setColorFilter(iconCf);
        // text
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(frontColor);
        if (textSize == 0) {
            textSize = a.getDimensionPixelSize(R.styleable.FancyTabPageIndicator_textSize, DEFAULT_TEXT_SIZE);
        }
        textPaint.setTextSize(textSize);
        // arrow
        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (arrowColor == 0) {
            arrowColor = a.getColor(com.viewpagerindicator.R.styleable.FancyTabPageIndicator_arrowColor, DEFAULT_ARROW_COLOR);
        }
        arrowPaint.setColor(arrowColor);
        ColorFilter arrowCf = new PorterDuffColorFilter(arrowColor, PorterDuff.Mode.MULTIPLY);
        arrowPaint.setColorFilter(arrowCf);
        menuArrow = BitmapFactory.decodeResource(getResources(), R.drawable.menuarrow);

        a.recycle();
    }
    //endregion

    //region Setters
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setFrontColor(int frontColor) {
        this.frontColor = frontColor;
    }

    public void setArrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
    }
    //endregion

    //region Draw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemCount == 0) {
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            drawStatic(canvas, i, bounds[i]);
        }
    }

    /**
     * Draws static tabs content. Measures heights from bottom to top. Order: arrow/padding/title/padding/icon
     *
     * @param canvas Canvas to draw to
     * @param tab current tab to draw
     * @param bound tab bounds
     */
    protected void drawStatic(Canvas canvas, int tab, RectF bound) {
        String title = titles[tab].toString();
        Bitmap icon = icons[tab];
        Rect textBounds = new Rect();
        textPaint.getTextBounds(title, 0, title.length(), textBounds);

        // draw from bottom to top - arrow/padding/title/padding/icon
        float arrowTop = bound.bottom - menuArrow.getHeight();
        float textY = arrowTop - textPadding;
        float textX = bound.centerX() - textBounds.width() / 2;

        int fromPage = mCurrentPage;
        int toPage = mCurrentPage + 1;

        int percent;

        if (tab == fromPage) {
            double multiplier = (1 - mPositionOffset);
            if (mPositionOffset == 0.0f) {
                percent = ACTIVE_ALPHA;
            } else {
                percent = (int) ((ACTIVE_ALPHA - INACTIVE_ALPHA) * multiplier + INACTIVE_ALPHA);
            }
        } else if (tab == toPage) {
            double multiplier = mPositionOffset;
            if (mPositionOffset == 0.0f) {
                percent = INACTIVE_ALPHA;
            } else {
                percent = (int) ((ACTIVE_ALPHA - INACTIVE_ALPHA) * multiplier + INACTIVE_ALPHA);
            }
        } else {
            percent = INACTIVE_ALPHA;
        }

        int alpha = getAlphaFromPercent(percent);
        iconPaint.setAlpha(alpha);
        textPaint.setAlpha(alpha);

        if (icon != null) {
            float iconTop = textY - textBounds.height() - textPadding - icon.getHeight();
            float iconLeft = bound.centerX() - icon.getWidth() / 2;
            canvas.drawBitmap(icon, iconLeft, iconTop, iconPaint);
        }

        canvas.drawText(title, 0, title.length(), textX, textY, textPaint);
    }

    private int getAlphaFromPercent(int percent) {
        double alphaValue = percent / 100.0d;
        return (int) Math.round(alphaValue * 255);
    }

    @Override protected void drawMoving(Canvas canvas, RectF bounds) {
        super.drawMoving(canvas, bounds);
        final float arrowLeft = (bounds.left + bounds.right) / 2 - menuArrow.getWidth() / 2;
        final float arrowTop = bounds.bottom - menuArrow.getHeight();

        canvas.drawBitmap(menuArrow, arrowLeft, arrowTop, arrowPaint);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (itemCount == 0) {
            return;
        }

        // compute tab size
        textPadding = h / 16;
        final int paddingLeft = getPaddingLeft();
        final float pageWidth = (w - paddingLeft - getPaddingRight()) / (1f * itemCount);
        float top = getPaddingTop();
        final float bottom = h - getPaddingBottom();
        for (int i = 0; i < itemCount; i++) {
            float left = paddingLeft + pageWidth * i;
            final float right = left + pageWidth;
            bounds[i].set(left, top, right, bottom);
        }
    }
    //endregion

    //region Bound
    @Override
    public void setViewPager(ViewPager viewPager) {
        super.setViewPager(viewPager);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        itemCount = adapter.getCount();
        icons = new Bitmap[itemCount];
        titles = new CharSequence[itemCount];
        bounds = new RectF[itemCount];
        for (int i = 0; i < itemCount; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            icons[i] = BitmapFactory.decodeResource(getResources(), iconResId);
            titles[i] = title;
            bounds[i] = new RectF();
        }
    }
    //endregion

    //region Click
    @Override public boolean onTouchEvent(MotionEvent ev) {
        if (itemCount == 0) {
            return false;
        }

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP) {
            if (!mIsDragging) {
                for (int i = 0; i < itemCount; i++) {
                    if (ev.getX() < bounds[i].right && ev.getX() > bounds[i].left) {
                        mCurrentPage = i;
                        mViewPager.setCurrentItem(i);
                        return true;
                    }
                }
            }
            mIsDragging = false;
            mActivePointerId = INVALID_POINTER;
            if (mViewPager.isFakeDragging()) {
                mViewPager.endFakeDrag();
            }
        } else {
            return super.onTouchEvent(ev);
        }

        return true;
    }
    //endregion
}
