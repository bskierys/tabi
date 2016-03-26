/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.ipebk.tabi.database.models.ModelInterface;
import pl.ipebk.tabi.database.tables.Table;
import rx.Observable;
import timber.log.Timber;

/**
 * Base class for all daos. Typical methods should work for all basic data types.
 */
public abstract class Dao<E extends ModelInterface> {
    protected final Class<E> type;
    protected Table<E> table;
    protected BriteDatabase db;

    public Dao(Class<E> type, BriteDatabase database) {
        this.type = type;
        this.db = database;
    }

    /**
     * @return Table representing this table in database for easy mapping
     */
    public Table<E> getTable() {
        return table;
    }

    /**
     * Adds Entity to database and sets it's id.
     * Look in your log to detect insertion errors.
     *
     * @param model Entity to insert.
     */
    public void add(E model) {
        ContentValues values = table.modelToContentValues(model);
        Long id = db.insert(table.getTableName(), values);
        model.setId(id);
        if (id < 0) {
            Timber.e("Unable to insert entity %s", type.toString());
        } else {
            Timber.d("Inserted entity %s with id: %d", type.toString(), id);
        }
    }

    /**
     * Adds list of entities to database and sets their ids.
     *
     * @param models List of entities to put into database.
     */
    public void addList(List<E> models) {
        List<Long> ids = new ArrayList<>();
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (E model : models) {
                ContentValues values = table.modelToContentValues(model);
                Long id = db.insert(table.getTableName(), values);
                if (id < 0) {
                    Timber.e("Unable to insert entity %s", type.toString());
                } else {
                    Timber.d("Inserted entity %s with id: %d", type.toString(), id);
                }
                model.setId(id);
                ids.add(id);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    /**
     * Deletes an entity. Look in your log to search for deletion errors.
     *
     * @param id
     */
    public void delete(Long id) {
        int rowsAffected = db.delete(table.getTableName(), Table.COLUMN_ID + " = " + id, null);
        if (rowsAffected < 1) {
            Timber.e("Unable to delete entity %s with id: %d", type.toString(), id);
        } else {
            Timber.d("Deleted entity %s with id: %d", type.toString(), id);
        }
    }

    /**
     * Deletes list of entities
     *
     * @param ids Ids of elements in database.
     */
    public void deleteList(List<Long> ids) {
        String args = TextUtils.join(", ", ids);
        int rowsAffected = db.delete(table.getTableName(),
                Table.COLUMN_ID + " IN (?) ", args);
        Timber.d("Rows deleted: %d", rowsAffected);
    }

    /**
     * Deletes all records from specific table.
     *
     * @return Number of rows deleted.
     */
    public int deleteAll() {
        int rowsAffected = db.delete(table.getTableName(), "1");
        Timber.d("Rows deleted: %d", rowsAffected);
        return rowsAffected;
    }

    @NonNull protected String getQualifiedColumnsCommaSeparated() {
        String commaSeparated = "";
        String[] columns = table.getQualifiedColumns();
        for (int i = 0; i < columns.length; i++) {
            commaSeparated += columns[i];
            if (i != columns.length - 1) {
                commaSeparated += ", ";
            }
        }
        return commaSeparated;
    }

    /**
     * Gets element fom database of specific kind and id.
     *
     * @param id Id of an entity to look for.
     * @return Entity with given id or null if not present.
     */
    public E getById(Long id) {
        String selection = Table.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), selection, null, null, null, null);

        Cursor cursor = db.query(sql, selectionArgs);
        return getModelForCursor(cursor);
    }

    /**
     * Gets element fom database of specific kind and id.
     *
     * @param id Id of an entity to look for.
     * @return Observable of entity with given id or null if not present.
     */
    public Observable<E> getByIdObservable(Long id) {
        String selection = Table.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), selection, null, null, null, null);

        return db.createQuery(table.getTableName(), sql, selectionArgs)
                .mapToOne(cursor -> table.cursorToModel(cursor));
    }

    protected E getModelForCursor(Cursor cursor) {
        E model = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                model = table.cursorToModel(cursor);
            }
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
        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), null, null, null, null, null);
        return getListOfModelsForCursor(db.query(sql));
    }

    /**
     * Gets all entities from database.
     *
     * @return Observable of list of all entities from database.
     * Empty list if there are no objects in table.
     */
    public Observable<List<E>> getAllObservable() {
        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), null, null, null, null, null);
        return db.createQuery(table.getTableName(), sql)
                .mapToList(cursor -> table.cursorToModel(cursor));
    }

    /**
     * Gets all entities from database.
     *
     * @return Observable of cursor of all entities from database.
     * Empty list if there are no objects in table.
     */
    public Observable<Cursor> getAllCursorObservable() {
        String sql = SQLiteQueryBuilder.buildQueryString(false, table.getTableName(),
                table.getQualifiedColumns(), null, null, null, null, null);
        return db.createQuery(table.getTableName(), sql).map(SqlBrite.Query::run);
    }

    /**
     * Maps cursor to list of models
     *
     * @param cursor cursor of appropriate models
     * @return List of models of appropriate type
     */
    @NonNull public List<E> getListOfModelsForCursor(Cursor cursor) {
        List<E> models = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                E model = table.cursorToModel(cursor);
                models.add(model);
                cursor.moveToNext();
            }
            cursor.close();
        }
        models.removeAll(Collections.singleton(null));
        return models;
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
            Timber.d("Rows updated: %d", rowsAffected);
        } else {
            Timber.e("Update: Unable to update. Object has no id");
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
