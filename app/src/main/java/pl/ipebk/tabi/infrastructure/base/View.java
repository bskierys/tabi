/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.base;

import android.database.sqlite.SQLiteDatabase;

/**
 * Base class for all database views. It consists of basic methods for all views.
 */
public abstract class View<E> extends DataStructure<E> {
    @Override public void drop(SQLiteDatabase database) {
        database.execSQL("DROP VIEW IF EXISTS " + getName());
    }
}
