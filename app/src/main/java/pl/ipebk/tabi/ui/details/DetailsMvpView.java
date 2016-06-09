/*
* author: Bartlomiej Kierys
* date: 2016-02-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.details;

import android.net.Uri;

import pl.ipebk.tabi.ui.base.MvpView;

public interface DetailsMvpView extends MvpView {
    void showPlaceIcon(int iconResId);

    void showSearchedText(String searchedText);

    void showPlaceName(String name);

    void showPlate(String plate);

    void showGmina(String gmina);

    void showPowiat(String powiat);

    void showVoivodeship(String voivodeship);

    void showAdditionalInfo(String additionalInfo);

    void showMap(Uri uri);

    void enableActionButtons();

    void disableActionButtons();

    void showInfoMessage(String message);

    void startMap(Uri uri);

    void startWebSearch(String searchPhrase);

    void showPlaceHolder();
}
