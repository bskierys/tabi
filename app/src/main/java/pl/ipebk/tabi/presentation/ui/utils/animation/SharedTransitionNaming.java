package pl.ipebk.tabi.presentation.ui.utils.animation;

/**
 * Utility class to help naming shared transition elements
 */
public class SharedTransitionNaming {

    /**
     * @param name Base transition name of an element
     * @param position position of clicked item
     * @return Name for shared element that should be common across activities
     */
    public static String getName(String name, int position) {
        return name + Integer.toString(position);
    }
}
