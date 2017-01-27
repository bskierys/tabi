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
    private String plateStart;
    private String categoryKey;

    /**
     * Constructor for {@link MainListElementItem} Object
     */
    public MainListElementItem(String elementName, Drawable elementIcon, String plateStart, String categoryKey) {
        this.elementName = elementName;
        this.elementIcon = elementIcon;
        this.plateStart = plateStart;
        this.categoryKey = categoryKey;
    }

    String getElementName() {
        return elementName;
    }

    Drawable getElementIcon() {
        return elementIcon;
    }

    String getPlateStart() {
        return plateStart;
    }

    public String getCategoryKey() {
        return categoryKey;
    }
}
