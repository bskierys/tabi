package pl.ipebk.tabi.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class TabiTreeTest {
    @Test public void testCreateTag() throws Exception {
        TabiTree tree = new TabiTree();
        String className = "com.ipebk.tabi.utils.helpers.SearchMvpViewPresenterHelper";
        String expected = "tbi.hlpr.prsntr.vw.mvp.srch";

        StackTraceElement element = new StackTraceElement(className,"fakeMethod","fakeFile",67);
        String actual = tree.createStackElementTag(element);

        assertEquals(expected,actual);
    }
}