package pl.ipebk.tabi.presentation.ui.details;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

import javax.inject.Inject;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.utils.FontManager;

/**
 * Button that is placed in the panel in details view. It consists of image with text. Should be of specific height to
 * fit perfectly.
 */
public class DetailsButton extends Button {
    private String text;
    private Drawable doodle;
    private Context context;
    @Inject FontManager fontManager;

    private TextPaint textPaint;
    private float textHeight;
    private int doodleWidth;
    private int doodleHeight;

    private int paddingTop;
    private int paddingBottom;

    public DetailsButton(Context context) {
        super(context);
        this.context = context;

        if (!isInEditMode()) {
            init(null, 0);
        }
    }

    public DetailsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        if (!isInEditMode()) {
            init(attrs, 0);
        }
    }

    public DetailsButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        if (!isInEditMode()) {
            init(attrs, defStyle);
        }
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DetailsButton, defStyle, 0);
        App.get(context).getViewComponent().inject(this);

        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

        text = a.getString(R.styleable.DetailsButton_text);

        if (a.hasValue(R.styleable.DetailsButton_doodle)) {
            doodle = a.getDrawable(R.styleable.DetailsButton_doodle);
            doodle.setCallback(this);

            doodleHeight = getContext().getResources().getDimensionPixelSize(R.dimen.Details_Height_Button_Image);
            doodleWidth = (doodle.getIntrinsicWidth() * doodleHeight) / doodle.getIntrinsicHeight();
        }

        a.recycle();

        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        Typeface bebas = fontManager.get("bebas", Typeface.NORMAL);

        textPaint.setTypeface(bebas);
        textPaint.setTextSize(getContext().getResources().getDimensionPixelSize(R.dimen.Details_Text_Button));
        textPaint.setColor(getContext().getResources().getColor(R.color.sienna_700));

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInEditMode()) {
            drawDoodleWithText(canvas);
        }
    }

    private void drawDoodleWithText(Canvas canvas) {
        int centerX = getWidth() / 2;

        canvas.drawText(text, centerX, getHeight() - textHeight - paddingBottom, textPaint);

        if (doodle != null) {
            doodle.getIntrinsicHeight();
            doodle.setBounds(centerX - doodleWidth / 2, paddingTop,
                             centerX + doodleWidth / 2, paddingTop + doodleHeight);
            doodle.draw(canvas);
        }
    }

    /**
     * Gets the text attribute value.
     *
     * @return The text attribute value.
     */
    public String getButtonText() {
        return text;
    }

    /**
     * Sets the view's text attribute value.
     *
     * @param exampleString The text attribute value to use.
     */
    public void setButtonText(String exampleString) {
        text = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets doodle attribute value.
     *
     * @return The doodle attribute value.
     */
    public Drawable getDoodle() {
        return doodle;
    }

    /**
     * Sets the view's doodle attribute value. Doodle is picture that is placed in the top of the button.
     *
     * @param exampleDrawable The doodle drawable attribute value to use.
     */
    public void setDoodle(Drawable exampleDrawable) {
        doodle = exampleDrawable;
    }
}
