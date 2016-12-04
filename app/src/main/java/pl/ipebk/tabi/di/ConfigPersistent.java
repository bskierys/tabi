/*
* author: Bartlomiej Kierys
* date: 2016-11-29
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * A scoping annotation to permit dependencies conform to the life of the
 * {@link pl.ipebk.tabi.di.component.ConfigPersistentComponent}
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPersistent {
}
