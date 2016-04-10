/*
* author: Bartlomiej Kierys
* date: 2016-04-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.viewpagerindicator.UnderlinePageIndicator;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.utils.FontManager;

/**
 * Custom made tab indicator. Tab is indicated by moving underline. Underline, selected and unselected color, typeface
 * and textSize can be set either by attributes or by methods. Indicator can also fade away. Set it to your viewPager
 * through {@link #setViewPager(ViewPager)} method. Tab can be changed be dragging on viewPager, dragging on indicator
 * or just clicking on desired tab.
 */
public class SearchTabPageIndicator extends UnderlinePageIndicator {
    //region Constants
    private static final CharSequence EMPTY_TITLE = "";
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int DEFAULT_SELECTED_TEXT_COLOR = Color.RED;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_LINE_HEIGHT = 12;
    //endregion

    //region Fields
    private TextPaint textPaint;
    private float[] activeTextHSV = new float[3];
    private float[] inactiveTextHSV = new float[3];
    private boolean isTestMode;

    private RectF[] bounds;
    private CharSequence[] titles;

    private int itemCount;
    //endregion

    //region Properties
    private int textSize;
    private int textColor;
    private int selectedTextColor;
    private int lineHeight;
    //endregion

    //region Construction
    public SearchTabPageIndicator(Context context) {
        super(context);
        init(null);
    }

    public SearchTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchTabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }
    //endregion

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SearchTabPageIndicator, 0, 0);

        if (selectedTextColor == 0) {
            selectedTextColor = a.getColor(
                    R.styleable.SearchTabPageIndicator_selectedTextColor,
                    DEFAULT_SELECTED_TEXT_COLOR);
            Color.colorToHSV(selectedTextColor, activeTextHSV);
        }
        if (textColor == 0) {
            textColor = a.getColor(
                    R.styleable.SearchTabPageIndicator_android_textColor,
                    DEFAULT_TEXT_COLOR);
            Color.colorToHSV(textColor, inactiveTextHSV);
        }
        if (textSize == 0) {
            float defaultTextSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_TEXT_SIZE,
                    a.getResources().getDisplayMetrics());

            textSize = a.getDimensionPixelSize(
                    R.styleable.SearchTabPageIndicator_android_textSize,
                    (int) defaultTextSize);
        }
        // TODO: 2016-04-10 change to different attribute naming ("fontFamily" to be consistent)
        String fontFamily = a.getString(R.styleable.SearchTabPageIndicator_typeface);
        if (fontFamily != null && !isTestMode) {
            textPaint.setTypeface(FontManager.getInstance().get(fontFamily, Typeface.NORMAL));
        }
        if (lineHeight == 0) {
            float defaultLineHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    DEFAULT_LINE_HEIGHT,
                    a.getResources().getDisplayMetrics());

            lineHeight = a.getDimensionPixelSize(
                    R.styleable.SearchTabPageIndicator_lineHeight,
                    (int) defaultLineHeight);
        }


        textPaint.setTextSize(textSize);

        a.recycle();
    }
    //endregion

    //region Setters
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
        Color.colorToHSV(selectedTextColor, activeTextHSV);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        Color.colorToHSV(textColor, inactiveTextHSV);
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
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
        Rect textBounds = new Rect();
        textPaint.getTextBounds(title, 0, title.length(), textBounds);

        float textY = bound.centerY() + textBounds.height() / 2;
        float textX = bound.centerX() - textBounds.width() / 2;

        int fromPage = mCurrentPage;
        int toPage = mCurrentPage + 1;

        float[] currentHSV = new float[3];

        if (tab == fromPage) {

            double multiplier = (1 - mPositionOffset);
            if (mPositionOffset == 0.0f) {
                currentHSV = activeTextHSV;
            } else {
                currentHSV[0] = computeCurrentValue(0, multiplier);
                currentHSV[1] = computeCurrentValue(1, multiplier);
                currentHSV[2] = computeCurrentValue(2, multiplier);
            }
        } else if (tab == toPage) {
            double multiplier = mPositionOffset;
            if (mPositionOffset == 0.0f) {
                currentHSV = inactiveTextHSV;
            } else {
                currentHSV[0] = computeCurrentValue(0, multiplier);
                currentHSV[1] = computeCurrentValue(1, multiplier);
                currentHSV[2] = computeCurrentValue(2, multiplier);
            }
        } else {
            currentHSV = inactiveTextHSV;
        }

        textPaint.setColor(Color.HSVToColor(currentHSV));

        canvas.drawText(title, 0, title.length(), textX, textY, textPaint);
    }

    private float computeCurrentValue(int index, double multiplier) {
        return (float) ((activeTextHSV[index] - inactiveTextHSV[index]) * multiplier + inactiveTextHSV[index]);
    }

    @Override protected void drawMoving(Canvas canvas, RectF bounds) {
        bounds.top = bounds.bottom - lineHeight;
        super.drawMoving(canvas, bounds);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (itemCount == 0) {
            return;
        }

        // compute tab size
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
        itemCount = adapter.getCount();
        titles = new CharSequence[itemCount];
        bounds = new RectF[itemCount];
        for (int i = 0; i < itemCount; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
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
