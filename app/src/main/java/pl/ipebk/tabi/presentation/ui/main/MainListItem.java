/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * List items for main screen categories. Names and drawables are found by reflection. See subclasses for guide how to
 * name your resources properly
 */
public abstract class MainListItem {
    protected static final String MAIN_LIST_ITEM_PREFIX = "main_list_";
    protected String resourceKey;

    /**
     * Constructor for {@link MainListItem} Object
     */
    public MainListItem(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public abstract String getTitleResourceKey();
}
