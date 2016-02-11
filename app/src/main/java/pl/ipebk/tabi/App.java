/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi;

import android.app.Application;

import pl.ipebk.tabi.utils.Provider;

public class App extends Application {
    protected Provider provider;

    @Override
    public void onCreate() {
        super.onCreate();
        provider = new Provider(this);
    }

    public Provider getProvider() {
        return provider;
    }
}
