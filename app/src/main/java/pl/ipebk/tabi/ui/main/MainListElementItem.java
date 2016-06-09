/*
* author: Bartlomiej Kierys
* date: 2016-05-26
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

public class MainListElementItem extends MainListItem {
    private static final String ELEMENT_KEY_PREFIX = "element_";
    private static final String ELEMENT_IMAGE_PREFIX = "vic_";
    private String actionKey;

    public MainListElementItem(String resourceKey, String actionKey) {
        super(resourceKey);
        this.actionKey = actionKey;
    }

    @Override public String getTitleResourceKey() {
        return MAIN_LIST_ITEM_PREFIX + ELEMENT_KEY_PREFIX + resourceKey;
    }

    public String getImageResourceKey(){
        return ELEMENT_IMAGE_PREFIX + resourceKey;
    }

    public String getActionKey() {
        return actionKey;
    }
}
