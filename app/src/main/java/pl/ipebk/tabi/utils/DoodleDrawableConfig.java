/*
* author: Bartlomiej Kierys
* date: 2016-03-16
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import pl.ipebk.tabi.R;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DoodleDrawableConfig {
    final String headerText;
    final String descriptionText;
    final int imageResource;
    final int spaceBeforeImage;
    final int spaceAfterImage;
    final int width;
    final int height;
    final Context context;

    /*private final TextPaint headerPaint = new TextPaint();
    private final TextPaint descriptionPaint = new TextPaint();
    private final Canvas canvas = new Canvas();*/

    private DoodleDrawableConfig(final Builder builder){
        this.headerText = builder.headerText;
        this.descriptionText = builder.descriptionText;
        this.imageResource = builder.imageResource;
        this.spaceBeforeImage = builder.spaceBeforeImage;
        this.spaceAfterImage = builder.spaceAfterImage;
        this.context = builder.context;
    }

    /*private void initPaintingFields(){
        Typeface bebas = FontManager.getInstance().get("bebas",Typeface.NORMAL);
        Typeface montserrat = FontManager.getInstance().get("montserrat", Typeface.NORMAL);
        float headerTextSize = context.getResources().getDimensionPixelSize(R.dimen.Doodle_Text_Header);
        float descriptionTextSize = context.getResources().getDimensionPixelSize(R.dimen.Doodle_Text_Description);
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

    public Bitmap draw(){
        Bitmap doodle = BitmapFactory.decodeResource(context.getResources(),imageResource);

        int doodleHeight = doodle.getHeight();
        int doodleWidth = doodle.getWidth();

        Rect headerBounds = new Rect();
        headerPaint.getTextBounds(headerText,0,headerText.length(),headerBounds);
        Rect descriptionBounds = new Rect();
        descriptionPaint.getTextBounds(descriptionText, 0, descriptionText.length(),descriptionBounds);

        int totalHeight = headerBounds.height() + spaceBeforeImage + doodleHeight
                + spaceAfterImage + descriptionBounds.height();
        int totalWidth = Math.max(doodleWidth,Math.max(headerBounds.width(),descriptionBounds.width()));

        final Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);

        final Canvas c = canvas;
        c.setBitmap(bitmap);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        float cx = totalWidth/2 - doodleWidth/2;
        float cy = totalHeight/2 - doodleHeight/2;

        c.drawBitmap(doodle,cx,cy,paint);

        c.drawText(headerText,headerBounds.centerX(),headerBounds.height(),headerPaint);
        // TODO: 2016-03-16 aligment is wrong
        c.drawText(descriptionText,descriptionBounds.centerX(),totalHeight-descriptionBounds.height(),descriptionPaint);

        return bitmap;
    }*/

    public static class Builder{
        private String headerText;
        private String descriptionText;
        private int imageResource;
        private int spaceBeforeImage;
        private int spaceAfterImage;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder headerText(String headerText){
            this.headerText = headerText;
            return this;
        }

        public Builder descriptionText(String descriptionText){
            this.descriptionText = descriptionText;
            return this;
        }

        public Builder imageResource(int imageResource){
            this.imageResource = imageResource;
            return this;
        }

        public Builder spaceBeforeImage(int spaceBeforeImage){
            this.spaceBeforeImage = spaceBeforeImage;
            return this;
        }

        public Builder spaceAfterImage(int spaceAfterImage){
            this.spaceAfterImage = spaceAfterImage;
            return this;
        }

        public DoodleDrawableConfig build(){
            initEmptyFieldsWithDefaultValues();
            return new DoodleDrawableConfig(this);
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (headerText == null) {
                headerText = context.getResources().getString(R.string.doodle_default_header);
            }
            if (descriptionText ==null) {
                descriptionText = context.getResources().getString(R.string.doodle_default_description);
            }
            if (imageResource == 0) {
                imageResource = R.drawable.tabi_placeholder;
            }
            if (spaceBeforeImage == 0) {
                spaceBeforeImage = context.getResources().getDimensionPixelOffset(R.dimen.Doodle_Height_Space_Before);
            }
            if (spaceAfterImage == 0) {
                spaceAfterImage = context.getResources().getDimensionPixelOffset(R.dimen.Doodle_Height_Space_After);
            }
        }
    }


}
