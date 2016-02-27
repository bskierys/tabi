/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import pl.ipebk.tabi.ui.base.MvpView;

/**
 * TODO: Generic description. Replace with real one.
 */
public interface MainMvpView extends MvpView {
    void showLoading();

    void hideLoading();

    void showTime(String time);

    void showError(String errorText);

    void goToSearch();
}
