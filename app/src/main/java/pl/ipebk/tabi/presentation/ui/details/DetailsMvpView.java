/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.net.Uri;

import pl.ipebk.tabi.presentation.ui.base.MvpView;
import rx.Observable;

public interface DetailsMvpView extends MvpView {
    void showPlaceIcon(int iconResId);
    void showPlaceName(String name);
    void showPlate(String plate);
    void showGmina(String gmina);
    void showPowiat(String powiat);
    void showVoivodeship(String voivodeship);
    void showAdditionalInfo(String additionalInfo);
    void showMap(Uri uri);
    void showMapError();
    void enableActionButtons();
    void disableActionButtons();
    void showInfoMessageCopied();
    void startMapApp(Uri uri);
    void preloadWebSearch(String searchPhrase);
    void startWebSearch(String searchPhrase);
    void showPlaceHolder();

    // returning methods
    Observable<Integer> getMapWidthStream();
    Observable<Integer> getMapHeightStream();
}
