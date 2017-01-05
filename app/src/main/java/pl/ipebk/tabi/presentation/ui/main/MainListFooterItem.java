/*
* author: Bartlomiej Kierys
* date: 2017-01-03
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * Item represent simple footer on main screen
 */
public class MainListFooterItem extends MainListItem {
    private String versionName;

    /**
     * Constructor for {@link MainListFooterItem} Object
     */
    MainListFooterItem(String versionName) {
        this.versionName = versionName;
    }

    String getVersionName() {
        return versionName;
    }
}
