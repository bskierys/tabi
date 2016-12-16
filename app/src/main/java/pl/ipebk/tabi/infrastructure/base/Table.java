/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.base;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Base class for all database tables. It consists of basic methods for all tables.
 */
public abstract class Table<E> extends DataStructure<E> {
    public void upgrade(SQLiteDatabase database, int oldVersion,
                        int newVersion) {
        drop(database);
        create(database);
    }

    @Override public void drop(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + getName());
    }

    /**
     * Converts model to {@link ContentValues} for convenient database insertions.
     *
     * @param model Entity to be converted to values.
     * @return Appropriate {@link ContentValues} for given entity.
     */
    public abstract ContentValues modelToContentValues(E model);
}
