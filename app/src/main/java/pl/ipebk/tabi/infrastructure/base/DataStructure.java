/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Base class for all database structures. This means both tables and views.
 */
public abstract class DataStructure<E> {
    public abstract String getName();

    protected abstract String[] getColumns();

    protected abstract String getDatabaseCreateStatement();

    public void create(SQLiteDatabase database) {
        database.execSQL(getDatabaseCreateStatement());
    }

    public abstract void drop(SQLiteDatabase database);

    /**
     * Converts database cursor to desired entity.
     *
     * @param cursor Instance of {@link Cursor}. Must be opened, moved to first object and closed
     * afterwards.
     * @return Entity from database.
     */
    public abstract E cursorToModel(Cursor cursor);

    /**
     * @return Number of columns of current table.
     */
    public int getColumnsNumber() {
        return getColumns().length;
    }

    /**
     * Gets all columns of that table with table name prefix.
     */
    public String[] getQualifiedColumns() {
        String[] qualifiedColumns = new String[getColumns().length];
        for (int i = 0; i < getColumns().length; i++) {
            qualifiedColumns[i] = addPrefix(getColumns()[i]);
        }

        return qualifiedColumns;
    }

    /**
     * Get columns separated with commas fo raw sql queries. Column format is as fallow: if alias is not provided
     * '{column_name}' if alias is provided '{alias}.{column_name} as {column_name}'
     */
    public String getQualifiedColumnsCommaSeparated(String alias) {
        StringBuilder columns = new StringBuilder();

        for (int i = 0; i < getColumns().length; i++) {
            if (alias != null && !alias.equals("")) {
                columns.append(alias);
                columns.append(".");
                columns.append(getColumns()[i]);
                columns.append(" as ");
                columns.append(getColumns()[i]);
            } else {
                columns.append(getColumns()[i]);
            }

            if (i != getColumns().length - 1) {
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
        return getName() + "." + column;
    }
}
