/*
* author: Bartlomiej Kierys
* date: 2016-04-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.place.Place;
import pl.ipebk.tabi.readmodel.PlaceType;
import timber.log.Timber;

/**
 * Helper class that handles readable text formatting for places.
 */
public class DoodleTextFormatter {
    private Context context;

    public DoodleTextFormatter(Context context) {
        this.context = context;
    }

    public SpannableString formatDoodleCaption(String base) {
        return formatSpannable(base, R.style.Main_Text_Caption_Small, R.style.Main_Text_Caption_Large);
    }

    public SpannableString formatClickableText(String base) {
        return formatSpannable(base, R.style.Text_Body2_Primary, R.style.Text_Body2_Accent);
    }

    private SpannableString formatSpannable(String base, int normalStyle, int accentedStyle) {
        List<Integer> positions = getSpanKeyPoints(base);
        if (positions.size() % 2 == 1) {
            throw new IllegalArgumentException("Invalid number of asterisks in base text");
        }
        int spanCount = positions.size() - 1;

        String altered = base.replace("*", "");

        SpannableString text = new SpannableString(altered);
        for (int i = 0; i < spanCount; i++) {
            TextAppearanceSpan span;
            if (i % 2 == 0) {
                span = new TextAppearanceSpan(context, normalStyle);
            } else {
                span = new TextAppearanceSpan(context, accentedStyle);
            }

            text.setSpan(span, positions.get(i), positions.get(i + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    @NonNull private List<Integer> getSpanKeyPoints(String base) {
        List<Integer> positions = new ArrayList<>();
        positions.add(0);
        int asterisksSpotted = 0;
        for (int i = 0; i < base.length(); i++) {
            if (base.charAt(i) == '*') {
                positions.add(i - asterisksSpotted);
                asterisksSpotted++;
            }
        }
        positions.add(base.length() - asterisksSpotted);

        return positions;
    }
}
