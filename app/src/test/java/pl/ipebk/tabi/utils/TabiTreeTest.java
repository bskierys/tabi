package pl.ipebk.tabi.utils;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TabiTreeTest {
    @Test public void testCreateLogTag1() throws Exception {
        TabiTree tree = new TabiTree();
        String className = "pl.ipebk.tabi.utils.advancedHelpers.SearchMvpViewPresenterHelper";
        String expected = "tbi.tls.dvncd.hlprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTag2() throws Exception {
        TabiTree tree = new TabiTree();
        String className = "pl.ipebk.tabi.ui.main.MainActivity";
        String expected = "tbi.ui.mn";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogMessage() throws Exception {
        TabiTree tree = new TabiTree();
        String className = "pl.ipebk.tabi.ui.main.MainActivity";
        String methodName = "loadDoodleImage";
        int line = 67;
        String message = "Hello world!";

        String expectedTag = "tbi.ui.mn";
        String expectedMessage = String.format(Locale.UK, "%s, %d ---> %s", methodName, line, message);

        StackTraceElement element = new StackTraceElement(className, methodName, "fakeFile", line);
        String actualTag = tree.createStackElementTag(element);
        String actualMessage = tree.createStackElementMessage(message, element);

        assertEquals(expectedTag, actualTag);
        assertEquals(expectedMessage, actualMessage);
    }
}