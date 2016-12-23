/*
* author: Bartlomiej Kierys
* date: 2016-12-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Empty service used by the custom tab to bind to, raising the application's importance.
 */
public class KeepAliveService extends Service {
    private static final Binder sBinder = new Binder();

    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }
}
