/*
* author: Bartlomiej Kierys
* date: 2016-03-13
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.view.InflateException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for effective handling fonts. Uses singleton design pattern for loading fonts only once.
 */
public class FontManager {

    // Different tags used in XML file.
    private static final String TAG_FAMILY = "family";
    private static final String TAG_NAMESET = "nameset";
    private static final String TAG_NAME = "name";
    private static final String TAG_FILESET = "fileset";
    private static final String TAG_FILE = "file";
    // Different styles supported.
    private static final String STYLE_BOLD = "-Bold.ttf";
    private static final String STYLE_ITALIC = "-Italic.ttf";
    private static final String STYLE_BOLDITALIC = "-BoldItalic.ttf";
    private List<Font> fonts;
    private boolean isInitialized;

    private boolean isName;
    private boolean isFile;

    private FontManager() {
    }

    public static FontManager getInstance() {
        return FontManager.InstanceHolder.INSTANCE;
    }

    /**
     * Parse the resId and initialize the parser. If already initialized, does nothing.
     *
     * @param context {@link Context} to get resources from
     * @param resId Id of xml resource containing list of fonts
     */
    public void initialize(Context context, int resId) {
        if (isInitialized) {
            return;
        }
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getXml(resId);
            fonts = new ArrayList<>();

            String tag;
            int eventType = parser.getEventType();

            Font font = null;

            do {
                tag = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tag.equals(TAG_FAMILY)) {
                            // one of the font-families.
                            font = new Font();
                        } else if (tag.equals(TAG_NAMESET)) {
                            // a list of font-family names supported.
                            font.families = new ArrayList<>();
                        } else if (tag.equals(TAG_NAME)) {
                            isName = true;
                        } else if (tag.equals(TAG_FILESET)) {
                            // a list of files specifying the different styles.
                            font.styles = new ArrayList<>();
                        } else if (tag.equals(TAG_FILE)) {
                            isFile = true;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tag.equals(TAG_FAMILY)) {
                            // add it to the list.
                            if (font != null) {
                                fonts.add(font);
                                font = null;
                            }
                        } else if (tag.equals(TAG_NAME)) {
                            isName = false;
                        } else if (tag.equals(TAG_FILE)) {
                            isFile = false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (isName) {
                            // value is a name, add it to list of family-names.
                            if (font.families != null) {
                                font.families.add(text);
                            }
                        } else if (isFile) {
                            // value is a file, add it to the proper kind.
                            FontStyle fontStyle = new FontStyle();
                            fontStyle.font = Typeface.createFromAsset(context.getAssets(), text);

                            if (text.endsWith(STYLE_BOLD)) {
                                fontStyle.style = Typeface.BOLD;
                            } else if (text.endsWith(STYLE_ITALIC)) {
                                fontStyle.style = Typeface.ITALIC;
                            } else if (text.endsWith(STYLE_BOLDITALIC)) {
                                fontStyle.style = Typeface.BOLD_ITALIC;
                            } else {
                                fontStyle.style = Typeface.NORMAL;
                            }

                            font.styles.add(fontStyle);
                        }
                }

                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
            isInitialized = true;
        } catch (XmlPullParserException e) {
            throw new InflateException("Error inflating font XML", e);
        } catch (IOException e) {
            throw new InflateException("Error inflating font XML", e);
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    /**
     * Search for already initialized typeface in this class instance. All fonts are held in singleton instance so you
     * don't have to worry about view performance.
     *
     * @param family Family specified in xml used to instantiate class
     * @param style One of available styles for fonts
     * @return Typeface ready to set to TextView
     */
    public Typeface get(String family, int style) {
        for (Font font : fonts) {
            for (String familyName : font.families) {
                if (familyName.equals(family)) {
                    // if no style in specified, return normal style.
                    if (style == -1) {
                        style = Typeface.NORMAL;
                    }

                    for (FontStyle fontStyle : font.styles) {
                        if (fontStyle.style == style) {
                            return fontStyle.font;
                        }
                    }
                }
            }
        }

        return null;
    }

    //Making FontManager a singleton class
    private static class InstanceHolder {
        private static final FontManager INSTANCE = new FontManager();
    }

    private class FontStyle {
        int style;
        Typeface font;
    }

    private class Font {
        // different font-family names that this Font will respond to.
        List<String> families;

        // different styles for this font.
        List<FontStyle> styles;
    }
}
