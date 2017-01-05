/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * Item representing small header on main screen.
 */
class MainListHeaderItem extends MainListItem {
    private String headerText;

    /**
     * Constructor for {@link MainListElementItem} Object
     */
    MainListHeaderItem(String headerText) {
        this.headerText = headerText;
    }

    String getHeaderText() {
        return headerText;
    }
}
