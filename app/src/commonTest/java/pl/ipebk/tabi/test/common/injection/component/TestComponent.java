/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.di.component.ApplicationComponent;
import pl.ipebk.tabi.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
