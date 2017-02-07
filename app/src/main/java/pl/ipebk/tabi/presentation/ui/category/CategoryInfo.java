/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.category;

import android.graphics.drawable.Drawable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CategoryInfo {
    public abstract String title();
    public abstract String body();
    public abstract String link();
    public abstract Drawable icon();
}
