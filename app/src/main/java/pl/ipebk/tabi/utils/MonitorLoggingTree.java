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
 * android monitor. Tag goes like this: tbi.{package name without pl.ipebk.tabi to lower case. Ah and all vowels
 * are deleted} f.ex "tbi.cmmnctn.bltth.wrpprs" means class "pl.ipebk.tabi.communication.bluetooth.wrappers". Logged
 * message is aldo prefixed with class name, method and line of code that was logged.
 */
public class MonitorLoggingTree extends Timber.DebugTree {
    private static final int CALL_STACK_INDEX = 5;
    private static final String PACKAGE = "pl.ipebk.tabi";
    private static final String TAG = "tbi";
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private static final String DELIMITER = " ---> ";

    @Override protected String createStackElementTag(StackTraceElement element) {
        String[] path = getPackageName(element).split("\\.");

        StringBuilder tag = new StringBuilder();
        tag.append(getPrefix(path));
        path = removePackage(path);

        for (String pathElement : path) {
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

    private String getPrefix(String[] path) {
        if (path.length < 3) {
            return TAG;
        }

        String mainPackageName = path[0] + "." + path[1] + "." + path[2];

        if (isOurPackage(mainPackageName)) {
            return TAG;
        } else {
            return path[2].toUpperCase();
        }
    }

    private boolean isOurPackage(String packageName) {
        return packageName.startsWith(PACKAGE);
    }

    private String[] removePackage(String[] path) {
        if (path.length > 3) {
            String[] pathWithoutPackage = new String[path.length - 3];
            for (int i = 0; i < path.length - 3; i++) {
                pathWithoutPackage[i] = path[i + 3];
            }
            return pathWithoutPackage;
        } else {
            return path;
        }
    }

    @NonNull private String getFullClassName(StackTraceElement element) {
        String className = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(className);
        if (m.find()) {
            className = m.replaceAll("");
        }

        return className;
    }

    @NonNull private String getPackageName(StackTraceElement element) {
        String className = getFullClassName(element);
        return getFullClassName(element).substring(0, className.lastIndexOf('.') + 1);
    }

    @NonNull private String getClassName(StackTraceElement element) {
        String className = getFullClassName(element);
        return className.substring(className.lastIndexOf('.') + 1, className.length());
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
        return getClassName(element)
                + ", " + element.getMethodName()
                + ", " + Integer.toString(element.getLineNumber())
                + DELIMITER + originalMessage;
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
