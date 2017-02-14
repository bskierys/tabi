/*
* author: Bartlomiej Kierys
* date: 2017-01-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.category;

import android.database.Cursor;
import android.view.View;

import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.ui.base.MvpView;

public interface CategoryMvpView extends MvpView {
    void showCategoryName(String name);
    void showCategoryPlate(String plate);
    void showCategoryInfo(CategoryInfo info);
    void showPlates(Cursor cursor);
    void goToDetails(View view, AggregateId placeId, String searchedPlate, String categoryName, String categoryPlate);
}
