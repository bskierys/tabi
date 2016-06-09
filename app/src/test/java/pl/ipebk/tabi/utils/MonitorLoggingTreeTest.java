package pl.ipebk.tabi.utils;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class MonitorLoggingTreeTest {
    @Test public void testCreateLogTag1() throws Exception {
        MonitorLoggingTree tree = new MonitorLoggingTree();
        String className = "pl.ipebk.tabi.utils.advancedHelpers.SearchMvpViewPresenterHelper";
        String expected = "tbi.tls.dvncd.hlprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTag2() throws Exception {
        MonitorLoggingTree tree = new MonitorLoggingTree();
        String className = "pl.ipebk.tabi.communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = "tbi.cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogTagForDifferentPackage() throws Exception {
        MonitorLoggingTree tree = new MonitorLoggingTree();
        String className = "com.github.package.communication.bluetooth.wrappers.SppClientDaemonWrapper";
        String expected = "PACKAGE.cmmnctn.bltth.wrpprs";

        StackTraceElement element = new StackTraceElement(className, "fakeMethod", "fakeFile", 67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected, actual);
    }

    @Test public void testCreateLogMessage() throws Exception {
        MonitorLoggingTree tree = new MonitorLoggingTree();
        String packageName = "pl.ipebk.tabi.communication.bluetooth.wrappers";
        String className = "SppClientDaemonWrapper";
        String fullClassName = packageName + "." + className;
        String methodName = "onError";
        int line = 67;
        String message = "Hello world!";

        String expectedTag = "tbi.cmmnctn.bltth.wrpprs";
        String expectedMessage = String.format(Locale.UK, "%s, %s, %d ---> %s", className, methodName, line, message);

        StackTraceElement element = new StackTraceElement(fullClassName, methodName, "fakeFile", line);
        String actualTag = tree.createStackElementTag(element);
        String actualMessage = tree.createStackElementMessage(message, element);

        assertEquals(expectedTag, actualTag);
        assertEquals(expectedMessage, actualMessage);
    }
}