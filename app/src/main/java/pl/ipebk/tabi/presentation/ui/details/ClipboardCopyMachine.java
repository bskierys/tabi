/*
* author: Bartlomiej Kierys
* date: 2016-11-29
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import javax.inject.Inject;

/**
 * Helper to provide information about device that app is currently running on
 */
public class ClipboardCopyMachine {
    private Context context;

    @Inject public ClipboardCopyMachine(Context context) {
        this.context = context;
    }

    public void copyToClipBoard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText(context.getPackageName(), text);
        clipboard.setPrimaryClip(clip);
    }
}
