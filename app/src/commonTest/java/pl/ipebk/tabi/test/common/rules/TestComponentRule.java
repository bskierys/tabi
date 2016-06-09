/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.rules;

import android.content.Context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.manager.DataManager;
import pl.ipebk.tabi.test.common.injection.component.DaggerTestApplicationComponent;
import pl.ipebk.tabi.test.common.injection.component.TestApplicationComponent;
import pl.ipebk.tabi.test.common.injection.module.TestApplicationModule;

/**
 * Test rule that creates and sets a Dagger TestApplicationComponent into the application overriding the
 * existing application component.
 * Use this rule in your test case in order for the app to use mock dependencies.
 * It also exposes some of the dependencies so they can be easily accessed from the tests, e.g. to
 * stub mocks etc.
 */
public class TestComponentRule implements TestRule {

    private final TestApplicationComponent testComponent;
    private final Context context;

    public TestComponentRule(Context context) {
        this.context = context;
        App application = App.get(context);
        testComponent = DaggerTestApplicationComponent.builder()
                                                      .testApplicationModule(new TestApplicationModule(application))
                                                      .build();
    }

    public Context getContext() {
        return context;
    }

    public DataManager getMockDataManager() {
        return testComponent.dataManager();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                App application = App.get(context);
                application.setAppComponent(testComponent);
                base.evaluate();
                application.setAppComponent(null);
            }
        };
    }
}
