/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.utils;

import android.content.Context;

import pl.ipebk.tabi.database.DatabaseOpenHelper;

public class Provider {
    private DatabaseOpenHelper databaseHelper;

    public Provider(Context context){
        databaseHelper = new DatabaseOpenHelper(context);
    }

    public DatabaseOpenHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
