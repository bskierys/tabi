package pl.ipebk.tabi.presentation.ui.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import pl.ipebk.tabi.presentation.ui.custom.DoodleImage;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class DoodleImageTest extends InstrumentationTestCase {
    public static final String DEFAULT_TEXT = "default_text";
    public static final int DEFAULT_DIMEN = 1;
    public static final int DEFAULT_COLOR = Color.RED;

    @Mock Context context;
    @Mock Resources resources;
    @Mock Typeface typeface;

    @Override public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(context.getResources()).thenReturn(resources);
        when(resources.getString(anyInt())).thenReturn(DEFAULT_TEXT);
        when(resources.getDimensionPixelSize(anyInt())).thenReturn(DEFAULT_DIMEN);
        when(resources.getColor(anyInt())).thenReturn(DEFAULT_COLOR);
    }

    @SmallTest public void testBuilderFillsAllData() throws Exception {
        DoodleImage.Builder builder = new DoodleImage.Builder(context);
        builder.headerFont(typeface);
        builder.descriptionFont(typeface);
        DoodleImage image = builder.build();

        assertNotNull(image.headerText);
        assertNotNull(image.descriptionText);
        assertNotNull(image.resources);
        assertTrue(image.width > 0);
        assertTrue(image.height > 0);
        assertTrue(image.imageResource > 0);
        assertTrue(image.spaceAfterImage > 0);
        assertTrue(image.spaceBeforeImage > 0);
    }

    @SmallTest public void testGetLines() throws Exception {
        String text = "This is simple text";
        int numberOfWords = text.split(" ").length;

        DoodleImage.Builder builder = new DoodleImage.Builder(context);
        builder.width(1);
        builder.headerFont(typeface);
        builder.descriptionFont(typeface);
        DoodleImage image = builder.build();

        List<String> lines = image.getLines(text, new MockPaint());

        assertEquals(numberOfWords, lines.size());
    }

    private class MockPaint extends Paint {
        @Override public void getTextBounds(String text, int start, int end, Rect bounds) {
            int size = text.trim().split(" ").length;
            bounds.set(0, 0, size, size);
        }
    }
}