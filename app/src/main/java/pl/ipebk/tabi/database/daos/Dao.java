/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.ipebk.tabi.database.models.ModelInterface;
import pl.ipebk.tabi.database.tables.Table;

/**
 * Base class for all daos. Typical methods should work for all basic data types.
 * Some methods are not implemented - just part of HMA interface.
 */
public abstract class Dao<E extends ModelInterface> {
    protected static final String TAG = "mHMA.Database";
    protected final Class<E> type;
    protected Table<E> table;
    protected SQLiteDatabase db;

    public Dao(Class<E> type, SQLiteDatabase database) {
        this.type = type;
        this.db = database;
    }

    /**
     * Adds Entity to database and sets it's id.
     * Look in your log to detect insertion errors.
     *
     * @param model Entity to insert.
     */
    public void add(E model) {
        ContentValues values = table.modelToContentValues(model);
        Long id = db.insert(table.getTableName(), null, values);
        model.setId(id);
        if (id < 0) Log.e(TAG, "Unable to insert entity " + type.toString());
        else Log.d(TAG, "Inserted entity " + type.toString() + " with id: " + Long.toString(id));
    }

    public E addEntity(E model) {
        ContentValues values = table.modelToContentValues(model);
        Long id = db.insert(table.getTableName(), null, values);
        model.setId(id);
        if (id < 0) Log.e(TAG, "Unable to insert entity " + type.toString());
        else Log.d(TAG, "Inserted entity " + type.toString() + " with id: " + Long.toString(id));

        return model;
    }

    /**
     * Adds list of entities to database and sets their ids.
     *
     * @param models List of entities to put into database.
     */
    public void addList(List<E> models) {
        List<Long> ids = new ArrayList<>();
        db.beginTransaction();
        try {
            for (E model : models) {
                ContentValues values = table.modelToContentValues(model);
                Long id = db.insert(table.getTableName(), null, values);
                if (id < 0) Log.e(TAG, "Unable to insert entity " + type.toString());
                else
                    Log.d(TAG, "Inserted entity " + type.toString() + " with id: " + Long.toString(id));
                model.setId(id);
                ids.add(id);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Deletes an entity. Look in your log to search for deletion errors.
     *
     * @param id
     */
    public void delete(Long id) {
        int rowsAffected = db.delete(table.getTableName(), Table.COLUMN_ID + " = " + id, null);
        if (rowsAffected < 1)
            Log.e(TAG, "Unable to delete entity " + type.toString() + " with id: " + Long.toString(id));
        else Log.d(TAG, "Deleted entity " + type.toString() + " with id: " + Long.toString(id));
    }

    /**
     * Deletes list of entities
     *
     * @param ids Ids of elements in database.
     */
    public void deleteList(List<Long> ids) {
        String args = TextUtils.join(", ", ids);
        int rowsAffected = db.delete(table.getTableName(), Table.COLUMN_ID + " IN (?) ", new String[]{args});
        Log.d(TAG, "Rows deleted: " + Integer.toString(rowsAffected));
    }

    /**
     * Deletes all records from specific table.
     *
     * @return Number of rows deleted.
     */
    public int deleteAll() {
        int rowsAffected = db.delete(table.getTableName(), "1", null);
        Log.d(TAG, "Rows deleted: " + Integer.toString(rowsAffected));
        return rowsAffected;
    }

    /**
     * Gets element fom database of specific kind and id.
     *
     * @param id Id of an entity to look for.
     * @return Entity with given id or null if not present.
     */
    public E getById(Long id) {
        E model = null;
        String selection = Table.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(table.getTableName(), table.getQualifiedColumns(), selection, selectionArgs, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) model = table.cursorToModel(cursor);
            cursor.close();
        }
        return model;
    }

    /**
     * Gets all entities from database.
     *
     * @return List of all entities from database. Empty list if there are no objects in table.
     */
    public List<E> getAll() {
        List<E> list = new ArrayList<>();
        Cursor cursor = db.query(table.getTableName(), table.getQualifiedColumns(), null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                E model = table.cursorToModel(cursor);
                list.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        list.removeAll(Collections.singleton(null));
        return list;
    }

    /**
     * @return type of entity wrapped by dao.
     */
    public Class getType() {
        return type;
    }

    /**
     * Update model in database. If model has no id, nothing happens.
     *
     * @param model Model to update.
     * @return Updated model.
     */
    public E update(E model) {
        Long id = model.getId();
        if (id > 0) {
            ContentValues values = table.modelToContentValues(model);
            String selection = Table.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            int rowsAffected = db.update(table.getTableName(), values, selection, selectionArgs);
            Log.d(TAG, "Rows updated: " + Integer.toString(rowsAffected));
        } else {
            Log.d(TAG, "Update: Unable to update. Object has no id");
        }
        return model;
    }

    /**
     * Update whole list of models.
     *
     * @param models Models to update.
     * @return List of updated models.
     */
    public List<E> updateList(List<E> models) {
        List<E> after = new ArrayList<>();
        for (E model : models) {
            after.add(update(model));
        }
        return after;
    }
}
