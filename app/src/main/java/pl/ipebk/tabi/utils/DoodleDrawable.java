/*
* author: Bartlomiej Kierys
* date: 2016-03-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import pl.ipebk.tabi.R;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DoodleDrawable extends Drawable {
    private static final int ALPHA_OPAQUE = 255;
    private static final int ALPHA_TRANSPARENT = 0;

    private DoodleDrawableConfig config;
    private Resources resources;

    private final TextPaint headerPaint = new TextPaint();
    private final TextPaint descriptionPaint = new TextPaint();
    private int alpha = ALPHA_OPAQUE;
    private ColorFilter colorFilter;

    public DoodleDrawable(DoodleDrawableConfig config) {
        this.config = config;
        this.resources = config.context.getResources();

        initPaintingFields();
    }

    private void initPaintingFields() {
        Typeface bebas = FontManager.getInstance().get("bebas", Typeface.NORMAL);
        float headerTextSize = resources.getDimensionPixelSize(R.dimen.Doodle_Text_Header);
        int headerFontColor = resources.getColor(R.color.grey_850);
        headerPaint.setColor(headerFontColor);
        headerPaint.setTextSize(headerTextSize);
        headerPaint.setTextAlign(Paint.Align.CENTER);
        headerPaint.setAntiAlias(true);
        headerPaint.setTypeface(bebas);

        Typeface montserrat = FontManager.getInstance().get("montserrat", Typeface.NORMAL);
        float descriptionTextSize = resources
                .getDimensionPixelSize(R.dimen.Doodle_Text_Description);
        int descriptionFontColor = resources.getColor(R.color.grey_700);
        descriptionPaint.setColor(descriptionFontColor);
        descriptionPaint.setTextSize(descriptionTextSize);
        descriptionPaint.setTextAlign(Paint.Align.CENTER);
        descriptionPaint.setAntiAlias(true);
        descriptionPaint.setTypeface(montserrat);
    }

    @Override public void draw(Canvas canvas) {
        setBounds(0, 0, config.width, config.height);

        RectF doodleBounds = drawDoodle(canvas);
        drawHeader(canvas, doodleBounds);
        drawDescription(canvas, doodleBounds);
    }

    private RectF drawDoodle(Canvas canvas) {
        Bitmap doodle = BitmapFactory.decodeResource(resources, config.imageResource);

        int doodleHeight = doodle.getHeight();
        int doodleWidth = doodle.getWidth();

        float centerX = config.width / 2;
        float centerY = config.height / 2;

        RectF doodleBounds = new RectF(centerX - doodleWidth / 2,
                                       centerY - doodleHeight / 2,
                                       centerX + doodleWidth / 2,
                                       centerY + doodleHeight / 2);

        Paint doodlePaint = new Paint();
        doodlePaint.setFilterBitmap(true);
        doodlePaint.setAlpha(alpha);
        doodlePaint.setColorFilter(colorFilter);

        canvas.drawBitmap(doodle, doodleBounds.left, doodleBounds.top, doodlePaint);

        return doodleBounds;
    }

    private void drawHeader(Canvas canvas, RectF doodleBounds) {
        float headerBottom = doodleBounds.top - config.spaceBeforeImage;
        headerPaint.setAlpha(alpha);
        headerPaint.setColorFilter(colorFilter);

        StaticLayout textLayout = new StaticLayout(config.headerText, headerPaint,
                                                   canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL,
                                                   1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(getBounds().centerX(), headerBottom);
        textLayout.draw(canvas);
        canvas.restore();
    }

    private void drawDescription(Canvas canvas, RectF doodleBounds) {
        Rect descriptionBounds = new Rect();
        descriptionPaint.getTextBounds(config.descriptionText, 0, config.descriptionText.length(),
                                       descriptionBounds);
        float descriptionBottom = doodleBounds.bottom + config.spaceAfterImage +
                descriptionBounds.height();
        descriptionPaint.setAlpha(alpha);
        descriptionPaint.setColorFilter(colorFilter);

        StaticLayout textLayout = new StaticLayout(config.descriptionText, descriptionPaint,
                                                   canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL,
                                                   1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(getBounds().centerX(), descriptionBottom);
        textLayout.draw(canvas);
        canvas.restore();
    }

    @Override public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    @Override public int getOpacity() {
        if (alpha == ALPHA_OPAQUE) {
            return PixelFormat.OPAQUE;
        } else if (alpha == ALPHA_TRANSPARENT) {
            return PixelFormat.TRANSPARENT;
        } else {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
