/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Base class for all database tables. It consists of basic methods for all tables.
 */
public abstract class Table<E> {
    public abstract String getTableName();

    protected abstract String[] getTableColumns();

    protected abstract String getDatabaseCreateStatement();

    public void create(SQLiteDatabase database) {
        database.execSQL(getDatabaseCreateStatement());
    }

    public void upgrade(SQLiteDatabase database, int oldVersion,
                        int newVersion) {
        drop(database);
        create(database);
    }

    public void drop(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + getTableName());
    }

    /**
     * Converts database cursor to desired entity.
     *
     * @param cursor Instance of {@link android.database.Cursor}. Must be opened, moved to first object and closed
     * afterwards.
     * @return Entity from database.
     */
    public abstract E cursorToModel(Cursor cursor);

    /**
     * @return Number of columns of current table.
     */
    public int getColumnsNumber() {
        return getTableColumns().length;
    }

    /**
     * Converts model to {@link android.content.ContentValues} for convenient database insertions.
     *
     * @param model Entity to be converted to values.
     * @return Appropriate {@link android.content.ContentValues} for given entity.
     */
    public abstract ContentValues modelToContentValues(E model);

    /**
     * Gets all columns of that table with table name prefix.
     */
    public String[] getQualifiedColumns() {
        String[] qualifiedColumns = new String[getTableColumns().length];
        for (int i = 0; i < getTableColumns().length; i++) {
            qualifiedColumns[i] = addPrefix(getTableColumns()[i]);
        }

        return qualifiedColumns;
    }

    /**
     * Get columns separated with commas fo raw sql queries. Column format is as fallow: if alias is not provided
     * '{column_name}' if alias is provided '{alias}.{column_name} as {column_name}'
     */
    public String getQualifiedColumnsCommaSeparated(String alias) {
        StringBuilder columns = new StringBuilder();

        for (int i = 0; i < getTableColumns().length; i++) {
            if (alias != null && !alias.equals("")) {
                columns.append(alias);
                columns.append(".");
                columns.append(getTableColumns()[i]);
                columns.append(" as ");
                columns.append(getTableColumns()[i]);
            } else {
                columns.append(getTableColumns()[i]);
            }

            if (i != getTableColumns().length - 1) {
                columns.append(",");
            }
        }

        return columns.toString();
    }

    /**
     * Ads table name to column as prefix for queries where more tables are present.
     *
     * @param column Column name to ad prefix to
     * @return Column name with a prefix ex. table_name.column_name
     */
    public String addPrefix(String column) {
        return getTableName() + "." + column;
    }
}
