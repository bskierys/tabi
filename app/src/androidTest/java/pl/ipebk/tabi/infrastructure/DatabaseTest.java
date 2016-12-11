/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import pl.ipebk.tabi.infrastructure.openHelper.DatabaseTestOpenHelper;

public class DatabaseTest extends AndroidTestCase {
    protected static DatabaseTestOpenHelper databaseHelper;

    @Override public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        databaseHelper = new DatabaseTestOpenHelper(context);
        databaseHelper.init();
    }

    @Override public void tearDown() throws Exception {
        databaseHelper.purge();
    }
}
