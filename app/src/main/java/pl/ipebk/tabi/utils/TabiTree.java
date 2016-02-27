/*
* author: Bartlomiej Kierys
* date: 2016-02-27
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import timber.log.Timber;

/**
 * Replacement for {@link timber.log.Timber.DebugTree} that constructs
 * tags in way that can be easy searched within android monitor.
 * Tag goes like this: tabi.{class name split by camel case, separated
 * by dots, and to lower case. Ah and all vowels are deleted}
 * f.ex tabi.tbl.plcs means class PlacesTable
 */
public class TabiTree extends Timber.DebugTree {
    @Override protected String createStackElementTag(StackTraceElement element) {
        String[] camelTags = splitCamelCase(super.createStackElementTag(element));
        String tabiTag = "tbi";
        for (int i = camelTags.length - 1; i >= 0; i--) {
            tabiTag += "." + removeVowels(camelTags[i]).toLowerCase();
        }

        return tabiTag;
    }

    private String removeVowels(String original) {
        return original.replaceAll("[AEIOUYaeiouy]", "");
    }

    private String[] splitCamelCase(String original) {
        return original.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }
}
