/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.graphics.drawable.Drawable;

/**
 * Item representing element action on main screen.
 */
class MainListElementItem extends MainListItem {
    private String elementName;
    private Drawable elementIcon;
    private String actionKey;

    /**
     * Constructor for {@link MainListElementItem} Object
     */
    MainListElementItem(String elementName, Drawable elementIcon, String actionKey) {
        this.elementName = elementName;
        this.elementIcon = elementIcon;
        this.actionKey = actionKey;
    }

    String getElementName() {
        return elementName;
    }

    Drawable getElementIcon() {
        return elementIcon;
    }

    String getActionKey() {
        return actionKey;
    }
}
