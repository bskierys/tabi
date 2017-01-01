/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * Item representing small header on main screen. Resources are delivered with power of reflection. Both string
 * resources and drawables should be named with pattern: main_list_header_{name}
 */
public class MainListHeaderItem extends MainListItem {
    private static final String HEADER_KEY_PREFIX = "header_";

    /**
     * Constructor for {@link MainListElementItem} Object
     */
    public MainListHeaderItem(String resourceKey) {
        super(resourceKey);
    }

    /**
     * @return Resource for title of this item. Resources should match pattern: main_list_header_{name}
     */
    @Override public String getTitleResourceKey() {
        return MAIN_LIST_ITEM_PREFIX + HEADER_KEY_PREFIX + resourceKey;
    }
}
