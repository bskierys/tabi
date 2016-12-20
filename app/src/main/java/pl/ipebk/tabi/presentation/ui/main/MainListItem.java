/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

public abstract class MainListItem {
    protected static final String MAIN_LIST_ITEM_PREFIX = "main_list_";
    protected String resourceKey;

    public MainListItem(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public abstract String getTitleResourceKey();
}
