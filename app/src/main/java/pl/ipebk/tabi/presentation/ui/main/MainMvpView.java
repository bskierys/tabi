/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import java.util.List;

import pl.ipebk.tabi.presentation.ui.base.MvpView;

public interface MainMvpView extends MvpView {
    void showFeedbackDialog();

    void showCaption(String caption);

    void showLoading();

    void hideLoading();

    void showCategories(List<MainListItem> categories);

    void goToSearch(String phrase);

    void goToAboutAppPage();
}
