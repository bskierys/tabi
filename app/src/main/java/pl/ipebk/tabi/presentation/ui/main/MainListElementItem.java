/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

/**
 * <p>Item representing small header on main screen. Resources are delivered with power of reflection.</p>
 * <ul>
 *     <li>String resources should be named with pattern: main_list_element_{name}</li>
 *     <li>Drawable resources should be named with pattern: vic_main_list_element_{name}</li>
 * </ul>
 * String resources should be named with pattern: main_list_element_{name}
 */
public class MainListElementItem extends MainListItem {
    private static final String ELEMENT_KEY_PREFIX = "element_";
    private static final String ELEMENT_IMAGE_PREFIX = "vic_";
    private String actionKey;

    /**
     * Constructor for {@link MainListElementItem} Object
     */
    public MainListElementItem(String resourceKey, String actionKey) {
        super(resourceKey);
        this.actionKey = actionKey;
    }

    @Override public String getTitleResourceKey() {
        return MAIN_LIST_ITEM_PREFIX + ELEMENT_KEY_PREFIX + resourceKey;
    }

    /**
     * @return Resource for title of this item. Resources should match pattern: main_list_element_{name}
     */
    public String getImageResourceKey(){
        return ELEMENT_IMAGE_PREFIX + resourceKey;
    }

    /**
     * @return Resource for drawable of this item. Resources should match pattern: vic_main_list_element_{name}
     */
    public String getActionKey() {
        return actionKey;
    }
}
