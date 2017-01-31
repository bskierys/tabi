/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import android.content.Context;
import android.os.Build;

import com.suredigit.inappfeedback.Installation;

import javax.inject.Inject;

import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.injection.ApplicationContext;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DeviceInfoProvider {
    private Context context;

    @Inject public DeviceInfoProvider(@ApplicationContext Context context) {
        this.context = context;
    }

    public String getInstallationId() {
        return Installation.id(context);
    }

    public int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public String getPackageName() {
        return context.getPackageName();
    }

    public String getDeviceModel() {
        return Build.MODEL;
    }

    public String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}
