/*
* author: Bartlomiej Kierys
* date: 2016-03-20
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pl.ipebk.tabi.R;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DoodleBitmapProvider {
    private static final int ALPHA_OPAQUE = 255;
    private static final int ALPHA_TRANSPARENT = 0;

    private final DoodleDrawableConfig config;
    private Resources resources;

    private final TextPaint headerPaint = new TextPaint();
    private final TextPaint descriptionPaint = new TextPaint();
    private int alpha = ALPHA_OPAQUE;
    private ColorFilter colorFilter;

    private List<String> headerLines;
    private List<String> descriptionLines;
    private Bitmap originalImage;
    private float scale;

    private Rect bounds;
    private final Canvas canvas = new Canvas();

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        this.bounds = new Rect(left, top, right, bottom);
    }

    public DoodleBitmapProvider(DoodleDrawableConfig config) {
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

    public void preComputeScale() {
        setBounds(0, 0, config.width, config.height);

        originalImage = BitmapFactory.decodeResource(resources, config.imageResource);

        int doodleHeight = originalImage.getHeight();
        int doodleWidth = originalImage.getWidth();

        float centerX = config.width / 2;
        float centerY = config.height / 2;

        Rect doodleBounds = new Rect((int)(centerX - doodleWidth / 2),
                                     (int)(centerY - doodleHeight / 2),
                                     (int)(centerX + doodleWidth / 2),
                                     (int)(centerY + doodleHeight / 2));

        int minimalMargin = resources.getDimensionPixelOffset(R.dimen.Doodle_Margin_Minimal);

        Rect textBounds = new Rect();
        headerLines = getLines(config.headerText, headerPaint);

        int headerWidth = 0;

        for(String line : headerLines){
            int lineWidth = getTextWidth(line,headerPaint,textBounds);
            if(lineWidth>headerWidth){
                headerWidth = lineWidth;
            }
        }

        int headerHeight = textBounds.height()*headerLines.size();

        descriptionLines = getLines(config.descriptionText,descriptionPaint);

        int descriptionWidth = 0;

        for(String line : descriptionLines){
            int lineWidth = getTextWidth(line, descriptionPaint,textBounds);
            if(lineWidth>descriptionWidth){
                descriptionWidth = lineWidth;
            }
        }

        int descriptionHeight = textBounds.height()*descriptionLines.size();

        int totalWidth = Math.max(descriptionWidth,Math.max(headerWidth,doodleBounds.width()));
        int totalHeight = minimalMargin + headerHeight + config.spaceBeforeImage +  doodleBounds.height()
                + config.spaceAfterImage + descriptionHeight + minimalMargin;

        if(totalWidth>config.width || totalHeight>config.height){
            float widthScale = (float)config.width/totalWidth;
            float heightScale = (float)config.height/totalHeight;

            scale = Math.min(widthScale,heightScale);
        }else {
            scale = 1.f;
        }
    }

    public Bitmap draw() {
        if(scale == 0.f){
            preComputeScale();
        }

        final Bitmap bitmap = Bitmap.createBitmap(getBounds().width(),
                                                  getBounds().height(),
                                                  Bitmap.Config.ARGB_8888);

        final Canvas c = canvas;
        canvas.setBitmap(bitmap);

        Rect doodleBounds = drawDoodle(c);
        drawHeader(c, doodleBounds);
        drawDescription(c, doodleBounds);

        return bitmap;
    }

    public Drawable asDrawable() {
        return new BitmapDrawable(resources,draw());
    }

    private Rect drawDoodle(Canvas canvas) {
        Bitmap doodle;

        if(scale==1){
            doodle = originalImage;
        }else {
            int newWidth = (int)(originalImage.getWidth()*scale);
            int newHeight = (int)(originalImage.getHeight()*scale);
            doodle = Bitmap.createScaledBitmap(originalImage,newWidth,newHeight,true);
        }

        int doodleHeight = doodle.getHeight();
        int doodleWidth = doodle.getWidth();

        float centerX = config.width / 2;
        float centerY = config.height / 2;

        Rect doodleBounds = new Rect((int)(centerX - doodleWidth / 2),
                                     (int)(centerY - doodleHeight / 2),
                                     (int)(centerX + doodleWidth / 2),
                                     (int)(centerY + doodleHeight / 2));

        Paint doodlePaint = new Paint();
        doodlePaint.setFilterBitmap(true);
        doodlePaint.setAlpha(alpha);
        doodlePaint.setColorFilter(colorFilter);

        canvas.drawBitmap(doodle, doodleBounds.left, doodleBounds.top, doodlePaint);

        return doodleBounds;
    }

    private void drawHeader(Canvas canvas, Rect doodleBounds) {
        headerPaint.setAlpha(alpha);
        headerPaint.setColorFilter(colorFilter);

        if(scale!=1){
            headerPaint.setTextSize(scale*headerPaint.getTextSize());
        }

        Rect descriptionBounds = new Rect();
        float descriptionBottom = doodleBounds.top - config.spaceBeforeImage*scale;
        for(int i =headerLines.size()-1; i>=0;i--){
            String line = headerLines.get(i);
            getTextWidth(line,headerPaint,descriptionBounds);


            canvas.drawText(line,getBounds().centerX(),descriptionBottom,headerPaint);
            descriptionBottom -= descriptionBounds.height();
        }
    }

    private void drawDescription(Canvas canvas, Rect doodleBounds) {
        descriptionPaint.setAlpha(alpha);
        descriptionPaint.setColorFilter(colorFilter);

        if(scale!=1){
            descriptionPaint.setTextSize(scale*descriptionPaint.getTextSize());
        }

        Rect descriptionBounds = new Rect();
        float descriptionBottom = doodleBounds.bottom + config.spaceAfterImage*scale;
        for(int i =0; i<descriptionLines.size();i++){
            String line = descriptionLines.get(i);
            getTextWidth(line,descriptionPaint,descriptionBounds);
            descriptionBottom += descriptionBounds.height();

            canvas.drawText(line,getBounds().centerX(),descriptionBottom,descriptionPaint);
        }
    }

    private List<String> getLines(String text, Paint paint){
        Rect textBounds = new Rect();

        StringTokenizer tok = new StringTokenizer(text, " ");
        StringBuilder output = new StringBuilder(text.length());
        List<String> lines = new ArrayList<>();
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (getTextWidth(output+" "+word,paint,textBounds) > config.width) {
                lines.add(output.toString());
                output = new StringBuilder(text.length());
            }
            output.append(word);
            output.append(" ");
        }
        lines.add(output.toString());

        return lines;
    }

    private int getTextWidth(String text, Paint paint,Rect bounds){
        paint.getTextBounds(text,0,text.length(),bounds);
        return bounds.width();
    }
}
