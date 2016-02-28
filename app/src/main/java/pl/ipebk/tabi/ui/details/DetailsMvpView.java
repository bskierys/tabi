/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.net.Uri;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.ui.base.MvpView;

public interface DetailsMvpView extends MvpView {
    void showPlaceName(String name);
    void showSearchedPlate(String plate);
    void showGmina(String gmina);
    void showPowiat(String powiat);
    void showVoivodeship(String voivodeship);
    void showAdditionalInfo(String additionalInfo);
    void showMap(Uri url);
}
