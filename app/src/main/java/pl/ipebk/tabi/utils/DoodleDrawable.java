/*
* author: Bartlomiej Kierys
* date: 2016-03-18
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import pl.ipebk.tabi.R;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DoodleDrawable extends Drawable {
    private DoodleDrawableConfig config;

    private Context context;

    private final TextPaint headerPaint = new TextPaint();
    private final TextPaint descriptionPaint = new TextPaint();

    public DoodleDrawable(DoodleDrawableConfig config) {
        this.config = config;
        this.context = config.context;

        initPaintingFields();
    }

    private void initPaintingFields() {
        Typeface bebas = FontManager.getInstance().get("bebas", Typeface.NORMAL);
        Typeface montserrat = FontManager.getInstance().get("montserrat", Typeface.NORMAL);
        float headerTextSize = context.getResources()
                                      .getDimensionPixelSize(R.dimen.Doodle_Text_Header);
        float descriptionTextSize = context.getResources()
                                           .getDimensionPixelSize(R.dimen.Doodle_Text_Description);
        int fontColor = context.getResources().getColor(R.color.grey_850);

        headerPaint.setColor(fontColor);
        headerPaint.setTextSize(headerTextSize);
        headerPaint.setTextAlign(Paint.Align.CENTER);
        headerPaint.setAntiAlias(true);
        headerPaint.setTypeface(bebas);

        descriptionPaint.setColor(fontColor);
        descriptionPaint.setTextSize(descriptionTextSize);
        descriptionPaint.setTextAlign(Paint.Align.CENTER);
        descriptionPaint.setAntiAlias(true);
        descriptionPaint.setTypeface(montserrat);
    }

    @Override public void draw(Canvas canvas) {
        Bitmap doodle = BitmapFactory.decodeResource(context.getResources(), config.imageResource);

        int doodleHeight = doodle.getHeight();
        int doodleWidth = doodle.getWidth();

        Rect headerBounds = new Rect();
        headerPaint.getTextBounds(config.headerText, 0, config.headerText.length(), headerBounds);
        Rect descriptionBounds = new Rect();
        descriptionPaint.getTextBounds(config.descriptionText, 0, config.descriptionText.length(),
                                       descriptionBounds);

        int totalHeight = headerBounds.height() + config.spaceBeforeImage + doodleHeight
                + config.spaceAfterImage + descriptionBounds.height();
        int totalWidth = Math
                .max(doodleWidth, Math.max(headerBounds.width(), descriptionBounds.width()));

        //final Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config
        // .ARGB_8888);

        //canvas.setBitmap(bitmap);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        float cx = totalWidth / 2 - doodleWidth / 2;
        float cy = totalHeight / 2 - doodleHeight / 2;

        canvas.drawBitmap(doodle, cx, cy, paint);

        canvas.drawText(config.headerText, headerBounds.centerX(), headerBounds.height(),
                        headerPaint);
        // TODO: 2016-03-16 aligment is wrong
        canvas.drawText(config.descriptionText, descriptionBounds.centerX(),
                        totalHeight - descriptionBounds.height(), descriptionPaint);
    }

    @Override public void setAlpha(int i) {

    }

    @Override public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override public int getOpacity() {
        return 0;
    }
}
