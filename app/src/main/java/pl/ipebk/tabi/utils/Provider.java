/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;

import pl.ipebk.tabi.database.openHelper.DatabaseHelperInterface;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;

public class Provider {
    private DatabaseHelperInterface databaseHelper;

    public Provider(Context context) {
        // to create database from scratch use DatabaseTestOpenHelper
        // to copy database from assets use DatabaseOpenHelper
        databaseHelper = new DatabaseOpenHelper(context);
    }

    public DatabaseHelperInterface getDatabaseHelper() {
        return databaseHelper;
    }
}
