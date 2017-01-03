/*
* author: Bartlomiej Kierys
* date: 2017-01-03
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * Item represents large header for beggining of main screen
 */
class MainListBigHeaderItem extends MainListItem {
    private String greeting;
    private String caption;

    /**
     * Constructor for {@link MainListBigHeaderItem} Object
     */
    MainListBigHeaderItem(String greeting, String caption) {
        this.greeting = greeting;
        this.caption = caption;
    }

    String getGreeting() {
        return greeting;
    }

    String getCaption() {
        return caption;
    }
}
