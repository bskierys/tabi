package pl.ipebk.tabi.presentation.ui.main;


import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class DoodleTextFormatterTest {
    Context context;
    DoodleTextFormatter doodleTextFormatter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.context = RuntimeEnvironment.application;
        doodleTextFormatter = new DoodleTextFormatter(context);
    }

    @Test public void testFormatDoodleCaptionWrongFormatting() throws Exception {
        String unFormattedText = "this is *invalid text";
        try {
            doodleTextFormatter.formatDoodleCaption(unFormattedText);
            fail();
        } catch (IllegalArgumentException e) {
            // we are expecting exception
        }
    }

    @Test public void testFormatDoodleCaptionRemovesAsterisks() throws Exception {
        String unFormattedText = "this *text* need *caption*";
        String formattedText = "this text need caption";

        SpannableString actual = doodleTextFormatter.formatDoodleCaption(unFormattedText);

        assertEquals(formattedText, actual.toString());
    }

    @Test public void testFormatDoodleCaptionWithCaption() throws Exception {
        String unFormattedText = "this *text* need *caption*";

        int[] expectedStarts = new int[]{0, 5, 9, 15, 22};
        int[] expectedEnds = new int[]{5, 9, 15, 22, 22};

        SpannableString actual = doodleTextFormatter.formatDoodleCaption(unFormattedText);
        TextAppearanceSpan[] spans = actual.getSpans(0, unFormattedText.length(), TextAppearanceSpan.class);
        int[] starts = new int[spans.length];
        int[] ends = new int[spans.length];

        for (int i = 0; i < spans.length; i++) {
            starts[i] = actual.getSpanStart(spans[i]);
            ends[i] = actual.getSpanEnd(spans[i]);
        }

        assertEquals(5, spans.length);
        assertEquals(2, getNumberOfCaptionSpans(spans));
        assertEquals(expectedEnds.length, ends.length);
        assertEquals(expectedStarts.length, ends.length);
        assertEquals(starts.length, ends.length);
        for (int i = 0; i < expectedStarts.length; i++) {
            assertEquals(expectedStarts[i], starts[i]);
            assertEquals(expectedEnds[i], ends[i]);
        }
    }

    @Test public void testFormatDoodleCaptionWithCaptionAtStart() throws Exception {
        String unFormattedText = "*this* text *need* caption";

        int[] expectedStarts = new int[]{0, 0, 4, 10, 14};
        int[] expectedEnds = new int[]{0, 4, 10, 14, 22};

        SpannableString actual = doodleTextFormatter.formatDoodleCaption(unFormattedText);
        TextAppearanceSpan[] spans = actual.getSpans(0, unFormattedText.length(), TextAppearanceSpan.class);
        int[] starts = new int[spans.length];
        int[] ends = new int[spans.length];

        for (int i = 0; i < spans.length; i++) {
            starts[i] = actual.getSpanStart(spans[i]);
            ends[i] = actual.getSpanEnd(spans[i]);
        }

        assertEquals(5, spans.length);
        assertEquals(2, getNumberOfCaptionSpans(spans));
        assertEquals(expectedEnds.length, ends.length);
        assertEquals(expectedStarts.length, ends.length);
        assertEquals(starts.length, ends.length);
        for (int i = 0; i < expectedStarts.length; i++) {
            assertEquals(expectedStarts[i], starts[i]);
            assertEquals(expectedEnds[i], ends[i]);
        }
    }

    @Test public void testFormatDoodleCaptionNoCaption() throws Exception {
        String unFormattedText = "this text does not need caption";

        SpannableString actual = doodleTextFormatter.formatDoodleCaption(unFormattedText);
        TextAppearanceSpan[] spans = actual.getSpans(0, unFormattedText.length(), TextAppearanceSpan.class);

        assertEquals(1, spans.length);
        assertEquals(0, getNumberOfCaptionSpans(spans));
    }

    private int getNumberOfCaptionSpans(TextAppearanceSpan[] spans) {
        int captionTextColor = context.getResources().getColor(R.color.colorPrimary);

        int numberOfCaptionsDetected = 0;
        for (TextAppearanceSpan span : spans) {
            if (span.getTextColor().getDefaultColor() == captionTextColor) {
                numberOfCaptionsDetected++;
            }
        }
        return numberOfCaptionsDetected;
    }
}