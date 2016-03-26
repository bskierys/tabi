/*
* author: Bartlomiej Kierys
* date: 2016-02-27
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Replacement for {@link timber.log.Timber.DebugTree} that constructs tags in way that can be easy searched within
 * android monitor. Tag goes like this: tabi.{class name split by camel case, separated by dots, and to lower case. Ah
 * and all vowels are deleted} f.ex tabi.tbl.plcs means class PlacesTable
 */
public class TabiTree extends Timber.DebugTree {
    private static final int CALL_STACK_INDEX = 5;
    private static final String PACKAGE = "pl.ipebk.tabi";
    private static final String TAG = "tbi";
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private static final String DELIMITER = " ---> ";

    @Override protected String createStackElementTag(StackTraceElement element) {
        String[] path = getPackageName(element).replace(PACKAGE + ".", "").split("\\.");

        StringBuilder tag = new StringBuilder();
        tag.append(TAG);

        for(String pathElement: path){
            pathElement = replaceCamelsWithDots(pathElement).toLowerCase();
            String newElement = removeVowels(pathElement);

            tag.append(".");
            if (newElement.equals("")) {
                tag.append(pathElement);
            } else {
                tag.append(newElement);
            }
        }

        return tag.toString();
    }

    @NonNull private String getPackageName(StackTraceElement element) {
        String className = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(className);
        if (m.find()) {
            className = m.replaceAll("");
        }

        return className.substring(0, className.lastIndexOf('.') + 1);
    }

    private String replaceCamelsWithDots(String original) {
        String[] splitByCamel = splitCamelCase(original);
        if (splitByCamel.length > 0) {
            original = "";
            for (String el : splitByCamel) {
                original += el + ".";
            }
            original = original.substring(0, original.length() - 1);
        }
        return original;
    }

    protected String createStackElementMessage(String originalMessage, StackTraceElement element) {
        return element.getMethodName() + ", " + Integer.toString(element.getLineNumber()) + DELIMITER + originalMessage;
    }

    private String removeVowels(String original) {
        return original.replaceAll("[AEIOUYaeiouy]", "");
    }

    private String[] splitCamelCase(String original) {
        return original.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    }

    @Override protected void log(int priority, String tag, String message, Throwable t) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }

        StackTraceElement element = stackTrace[CALL_STACK_INDEX];

        super.log(priority, tag, createStackElementMessage(message, element), t);
    }
}
