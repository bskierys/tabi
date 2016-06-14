/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import java.util.List;

import pl.ipebk.tabi.ui.base.MvpView;

/**
 * TODO: Generic description. Replace with real one.
 */
public interface MainMvpView extends MvpView {
    void showFeedbackDialog();

    void showCaption(String caption);

    void showLoading();

    void hideLoading();

    void showCategories(List<MainListItem> categories);

    void goToSearch(String phrase);

    // TODO: 2016-06-03 remove this method
    void prompt(String message);
}
