/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import pl.ipebk.tabi.presentation.ui.base.MvpView;

public interface MainMvpView extends MvpView {
    void showFeedbackDialog();
    void showGreetingCaption();
    void showFeedbackCaption();
    void showVersion(String versionName);
    void showLoading();
    void hideLoading();
    void goToSearch(String phrase);
    void goToAboutAppPage();
    void showDemoGreeting();
    void showResponseToFeedback(String response);
}
