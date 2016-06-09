/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import pl.ipebk.tabi.di.component.ViewComponent;
import pl.ipebk.tabi.test.common.injection.module.TestViewModule;

@Singleton
@Component(modules = TestViewModule.class)
public interface TestViewComponent extends ViewComponent {}
