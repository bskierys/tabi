/*
* author: Bartlomiej Kierys
* date: 2016-12-16
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.readmodel;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LicensePlateDto {
    public abstract String pattern();
    @Nullable public abstract String end();

    public static LicensePlateDto create(String pattern ,String end) {
        return new AutoValue_LicensePlateDto(pattern, end);
    }
}
