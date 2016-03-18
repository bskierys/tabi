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

    private DoodleDrawableConfig(final Builder builder) {
        this.headerText = builder.headerText;
        this.descriptionText = builder.descriptionText;
        this.imageResource = builder.imageResource;
        this.spaceBeforeImage = builder.spaceBeforeImage;
        this.spaceAfterImage = builder.spaceAfterImage;
        this.width = builder.width;
        this.height = builder.height;
        this.context = builder.context;
    }

    public static class Builder {
        private String headerText;
        private String descriptionText;
        private int imageResource;
        private int spaceBeforeImage;
        private int spaceAfterImage;
        private int width;
        private int height;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder headerText(String headerText) {
            this.headerText = headerText;
            return this;
        }

        public Builder descriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
            return this;
        }

        public Builder imageResource(int imageResource) {
            this.imageResource = imageResource;
            return this;
        }

        public Builder spaceBeforeImage(int spaceBeforeImage) {
            this.spaceBeforeImage = spaceBeforeImage;
            return this;
        }

        public Builder spaceAfterImage(int spaceAfterImage) {
            this.spaceAfterImage = spaceAfterImage;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public DoodleDrawableConfig build() {
            initEmptyFieldsWithDefaultValues();
            return new DoodleDrawableConfig(this);
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
                spaceBeforeImage = context.getResources().getDimensionPixelOffset(
                        R.dimen.Doodle_Height_Space_Before);
            }
            if (spaceAfterImage == 0) {
                spaceAfterImage = context.getResources().getDimensionPixelOffset(
                        R.dimen.Doodle_Height_Space_After);
            }
            if (width == 0) {
                width = context.getResources()
                               .getDimensionPixelOffset(R.dimen.Doodle_Width_Default);
            }
            if (height == 0) {
                height = context.getResources()
                                .getDimensionPixelOffset(R.dimen.Doodle_Height_Default);
            }
        }
    }
}
