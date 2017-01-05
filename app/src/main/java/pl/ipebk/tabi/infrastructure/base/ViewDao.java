/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.base;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all view daos. Only helper class to help with list of models from cursor
 */
public class ViewDao<E> {
    protected final Class<E> type;
    protected View<E> view;
    protected BriteDatabase db;

    public ViewDao(Class<E> type, BriteDatabase database) {
        this.type = type;
        this.db = database;
    }

    /**
     * Maps cursor to list of models
     *
     * @param cursor cursor of appropriate models
     * @return List of models of appropriate type
     */
    @NonNull protected List<E> getListOfModelsForCursor(Cursor cursor) {
        List<E> models = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                E model = view.cursorToModel(cursor);
                models.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        models.removeAll(Collections.singleton(null));
        return models;
    }
}
