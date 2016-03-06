/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.net.Uri;

import pl.ipebk.tabi.ui.base.MvpView;

public interface DetailsMvpView extends MvpView {
    void showPlaceName(String name);

    void showSearchedPlate(String plate);

    void showGmina(String gmina);

    void showPowiat(String powiat);

    void showVoivodeship(String voivodeship);

    void showAdditionalInfo(String additionalInfo);

    void showMap(Uri uri);

    void enableActionButtons();

    void disableActionButtons();

    void goToSearchForPhrase(String phrase);

    void startMap(Uri uri);

    void startWebSearch(String searchPhrase);

    void showPlaceHolder();
}
