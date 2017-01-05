/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.injection.component.ApplicationComponent;
import pl.ipebk.tabi.test.common.injection.module.TestApplicationModule;

@Singleton
@Component(modules = TestApplicationModule.class)
public interface TestApplicationComponent extends ApplicationComponent {

}
