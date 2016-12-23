/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

public class MainListHeaderItem extends MainListItem {
    private static final String HEADER_KEY_PREFIX = "header_";

    public MainListHeaderItem(String resourceKey) {
        super(resourceKey);
    }

    @Override public String getTitleResourceKey() {
        return MAIN_LIST_ITEM_PREFIX + HEADER_KEY_PREFIX + resourceKey;
    }
}
