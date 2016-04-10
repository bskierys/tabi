/*
* author: Bartlomiej Kierys
* date: 2016-03-20
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
 * Class that draws doodle image along with header and description. This class is build by builder. If you do not supply
 * builder with all data, default values will be used. Check out implementation of builder class for full details.
 */
public class DoodleImage {
    final String headerText;
    final String descriptionText;
    final int imageResource;
    final int spaceBeforeImage;
    final int spaceAfterImage;
    final int minimalMargin;
    final int width;
    final int height;
    final Typeface headerFont;
    final Typeface descriptionFont;
    final Resources resources;

    private final TextPaint headerPaint = new TextPaint();
    private final TextPaint descriptionPaint = new TextPaint();
    private final Canvas canvas = new Canvas();

    private List<String> headerLines;
    private List<String> descriptionLines;
    private Bitmap originalImage;
    float scale;

    public DoodleImage(final Builder builder) {
        this.headerText = builder.headerText;
        this.descriptionText = builder.descriptionText;
        this.imageResource = builder.imageResource;
        this.spaceBeforeImage = builder.spaceBeforeImage;
        this.spaceAfterImage = builder.spaceAfterImage;
        this.minimalMargin = builder.minimalMargin;
        this.width = builder.width;
        this.height = builder.height;
        this.headerFont = builder.headerFont;
        this.descriptionFont = builder.descriptionFont;
        this.resources = builder.context.getResources();

        initPaintingFields();
    }

    /**
     * Creates default doodle image <br /> <b>Default values:</b> <ul> <li>headerText =
     * R.string.doodle_default_header</li> <li>descriptionText = R.string .doodle_default_description</li>
     * <li>imageResource = R.drawable.tabi_placeholder</li> <li>spaceBeforeImage = R.dimen
     * .Doodle_Height_Space_Before</li>
     * <li>spaceAfterImage = R.dimen .Doodle_Height_Space_After</li> <li>width = R.dimen.Doodle_Width_Default</li>
     * <li>height = R.dimen.Doodle_Height_Default</li> </ul>
     */
    public static DoodleImage createDefault(Context context) {
        return new Builder(context).build();
    }

    private void initPaintingFields() {
        float headerTextSize = resources.getDimensionPixelSize(R.dimen.Doodle_Text_Header);
        int headerFontColor = resources.getColor(R.color.grey_850);
        headerPaint.setColor(headerFontColor);
        headerPaint.setTextSize(headerTextSize);
        headerPaint.setTextAlign(Paint.Align.CENTER);
        headerPaint.setAntiAlias(true);
        headerPaint.setTypeface(headerFont);

        float descriptionTextSize = resources
                .getDimensionPixelSize(R.dimen.Doodle_Text_Description);
        int descriptionFontColor = resources.getColor(R.color.grey_700);
        descriptionPaint.setColor(descriptionFontColor);
        descriptionPaint.setTextSize(descriptionTextSize);
        descriptionPaint.setTextAlign(Paint.Align.CENTER);
        descriptionPaint.setAntiAlias(true);
        descriptionPaint.setTypeface(descriptionFont);
    }

    /**
     * Method for pre-computation bounds of image to draw it faster when times come
     */
    public void preComputeScale() {
        originalImage = BitmapFactory.decodeResource(resources, imageResource);
        Rect textBounds = new Rect();

        headerLines = getLines(headerText, headerPaint);
        int headerWidth = getMaxLineWidth(textBounds, headerLines, headerPaint);
        int headerHeight = textBounds.height() * headerLines.size();

        descriptionLines = getLines(descriptionText, descriptionPaint);
        int descriptionWidth = getMaxLineWidth(textBounds, descriptionLines, descriptionPaint);
        int descriptionHeight = textBounds.height() * descriptionLines.size();

        int totalWidth = Math
                .max(descriptionWidth, Math.max(headerWidth, originalImage.getWidth()));
        int totalHeight = minimalMargin + headerHeight + spaceBeforeImage + originalImage
                .getHeight()
                + spaceAfterImage + descriptionHeight + minimalMargin;

        if (totalWidth > width || totalHeight > height) {
            float widthScale = (float) width / totalWidth;
            float heightScale = (float) height / totalHeight;

            scale = Math.min(widthScale, heightScale);
        } else {
            scale = 1.f;
        }
    }

    private int getMaxLineWidth(Rect textBounds, List<String> lines, TextPaint paint) {
        int descriptionWidth = 0;

        for (String line : lines) {
            int lineWidth = getTextWidth(line, paint, textBounds);
            if (lineWidth > descriptionWidth) {
                descriptionWidth = lineWidth;
            }
        }
        return descriptionWidth;
    }

    /**
     * Draws doodle along with description and header and returns bitmap. This method is not perfectly optimized so you
     * can help it by performing pre-computation off the main thread. Just call {@link #preComputeScale()} from another
     * thread and then call this one from main thread.
     */
    public Bitmap draw() {
        if (scale == 0.f) {
            preComputeScale();
        }

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas c = canvas;
        canvas.setBitmap(bitmap);

        Rect doodleBounds = drawDoodle(c);
        drawHeader(c, doodleBounds);
        drawDescription(c, doodleBounds);

        return bitmap;
    }

    public Drawable asDrawable() {
        return new BitmapDrawable(resources, draw());
    }

    private Rect drawDoodle(Canvas canvas) {
        Bitmap doodle;

        if (scale == 1) {
            doodle = originalImage;
        } else {
            int newWidth = (int) (originalImage.getWidth() * scale);
            int newHeight = (int) (originalImage.getHeight() * scale);
            doodle = Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true);
        }

        int doodleHeight = doodle.getHeight();
        int doodleWidth = doodle.getWidth();

        float centerX = width / 2;
        float centerY = height / 2;

        Rect doodleBounds = new Rect((int) (centerX - doodleWidth / 2),
                                     (int) (centerY - doodleHeight / 2),
                                     (int) (centerX + doodleWidth / 2),
                                     (int) (centerY + doodleHeight / 2));

        Paint doodlePaint = new Paint();
        doodlePaint.setFilterBitmap(true);

        canvas.drawBitmap(doodle, doodleBounds.left, doodleBounds.top, doodlePaint);

        return doodleBounds;
    }

    protected void drawHeader(Canvas canvas, Rect doodleBounds) {
        if (scale != 1) {
            headerPaint.setTextSize(scale * headerPaint.getTextSize());
        }

        Rect descriptionBounds = new Rect();
        float descriptionBottom = doodleBounds.top - spaceBeforeImage * scale;
        for (int i = headerLines.size() - 1; i >= 0; i--) {
            String line = headerLines.get(i);
            getTextWidth(line, headerPaint, descriptionBounds);

            canvas.drawText(line, width / 2, descriptionBottom, headerPaint);
            descriptionBottom -= descriptionBounds.height();
        }
    }

    protected void drawDescription(Canvas canvas, Rect doodleBounds) {
        if (scale != 1) {
            descriptionPaint.setTextSize(scale * descriptionPaint.getTextSize());
        }

        Rect descriptionBounds = new Rect();
        float descriptionBottom = doodleBounds.bottom + spaceAfterImage * scale;
        for (int i = 0; i < descriptionLines.size(); i++) {
            String line = descriptionLines.get(i);
            getTextWidth(line, descriptionPaint, descriptionBounds);
            descriptionBottom += descriptionBounds.height();

            canvas.drawText(line, width / 2, descriptionBottom, descriptionPaint);
        }
    }

    protected List<String> getLines(String text, Paint paint) {
        Rect textBounds = new Rect();

        StringTokenizer tok = new StringTokenizer(text, " ");
        StringBuilder output = new StringBuilder(text.length());
        List<String> lines = new ArrayList<>();
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (getTextWidth(output + " " + word, paint, textBounds) > width) {
                lines.add(output.toString());
                output = new StringBuilder(text.length());
            }
            output.append(word);
            output.append(" ");
        }
        lines.add(output.toString());

        return lines;
    }

    private int getTextWidth(String text, Paint paint, Rect bounds) {
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * Builder for {@link DoodleImage}
     */
    public static class Builder {
        private String headerText;
        private String descriptionText;
        private int imageResource;
        private int spaceBeforeImage;
        private int spaceAfterImage;
        private int minimalMargin;
        private int width;
        private int height;
        private Typeface headerFont;
        private Typeface descriptionFont;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Header text that displays above image. Default: R.string.doodle_default_header
         */
        public Builder headerText(String headerText) {
            this.headerText = headerText;
            return this;
        }

        /**
         * Description text that displays below image Default: R.string.doodle_default_description
         */
        public Builder descriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
            return this;
        }

        /**
         * Image resource to display Default: R.drawable.tabi_placeholder
         */
        public Builder imageResource(int imageResource) {
            this.imageResource = imageResource;
            return this;
        }

        /**
         * Margin between image and header Default: R.dimen.Doodle_Height_Space_Before
         */
        public Builder spaceBeforeImage(int spaceBeforeImage) {
            this.spaceBeforeImage = spaceBeforeImage;
            return this;
        }

        /**
         * Margin between image and description Default: R.dimen.Doodle_Height_Space_After
         */
        public Builder spaceAfterImage(int spaceAfterImage) {
            this.spaceAfterImage = spaceAfterImage;
            return this;
        }

        /**
         * Minimal margin around image
         */
        public Builder minimalMargin(int minimalMargin){
            this.minimalMargin = minimalMargin;
            return this;
        }

        /**
         * Width of image to be computed. If image is smaller it will be drawn inside of canvas. If image is larger it
         * will be rescalled with default margin around whole. Default: R.dimen.Doodle_Width_Default
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * Height of image to be computed. If image is smaller it will be drawn inside of canvas. If image is larger it
         * will be rescalled with default margin around whole. Default: R.dimen.Doodle_Height_Default
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * Font to draw header with. Default: Bebas
         */
        public Builder headerFont(Typeface headerFont) {
            this.headerFont = headerFont;
            return this;
        }

        /**
         * Font to draw description with. Default: Montserrat
         */
        public Builder descriptionFont(Typeface descriptionFont) {
            this.descriptionFont = descriptionFont;
            return this;
        }

        /**
         * Builds {@link DoodleImage} object.
         */
        public DoodleImage build() {
            initEmptyFieldsWithDefaultValues();
            return new DoodleImage(this);
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (headerText == null) {
                headerText = context.getResources().getString(R.string.doodle_default_header);
            }
            if (descriptionText == null) {
                descriptionText = context.getResources()
                                         .getString(R.string.doodle_default_description);
            }
            if (imageResource == 0) {
                imageResource = R.drawable.tabi_placeholder;
            }
            if (spaceBeforeImage == 0) {
                spaceBeforeImage = context.getResources().getDimensionPixelSize(
                        R.dimen.Doodle_Height_Space_Before);
            }
            if (spaceAfterImage == 0) {
                spaceAfterImage = context.getResources().getDimensionPixelSize(
                        R.dimen.Doodle_Height_Space_After);
            }
            if(minimalMargin == 0){
                minimalMargin = context.getResources().getDimensionPixelSize(R.dimen.Doodle_Margin_Minimal);
            }
            if (width == 0) {
                width = context.getResources().getDimensionPixelSize(R.dimen.Doodle_Width_Default);
            }
            if (height == 0) {
                height = context.getResources().getDimensionPixelSize(R.dimen.Doodle_Height_Default);
            }
            if (headerFont == null) {
                headerFont = FontManager.getInstance().get("bebas", Typeface.NORMAL);
            }
            if (descriptionFont == null) {
                descriptionFont = FontManager.getInstance().get("montserrat", Typeface.NORMAL);
            }
        }
    }
}
