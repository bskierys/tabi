/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package com.suredigit.inappfeedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * TODO: Generic description. Replace with real one.
 */
class DeviceInfoProvider {
    private PackageInfo packageInfo;
    private Context context;

    private DeviceInfoProvider() {}

    DeviceInfoProvider(Context context) {
        this.context = context;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not find package info for package");
        }
    }

    String getInstallationId() {
        return Installation.id(context);
    }

    int getAppVersionCode() {
        return packageInfo.versionCode;
    }

    String getAppVersionName() {
        return packageInfo.versionName;
    }

    String getPackageName() {
        return context.getPackageName();
    }

    String getDeviceModel() {
        return Build.MODEL;
    }

    String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}
