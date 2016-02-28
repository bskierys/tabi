/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.ui.base.MvpView;

public interface DetailsMvpView extends MvpView {
    void showPlace(Place place);

    void showMap(String url);
}
