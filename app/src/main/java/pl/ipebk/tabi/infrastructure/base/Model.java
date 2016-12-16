/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.base;

/**
 * Interface that should be implemented by every model class that
 * represents database entity. It handles methods linked to id, required
 * by daos.
 */
public interface Model {
    long getId();
    void setId(long id);
}
