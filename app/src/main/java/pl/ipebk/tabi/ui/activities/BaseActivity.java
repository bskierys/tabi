/*
* author: Bartlomiej Kierys
* date: 2016-02-11
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.ipebk.tabi.App;
import pl.ipebk.tabi.database.openHelper.DatabaseHelperInterface;
import pl.ipebk.tabi.database.openHelper.DatabaseOpenHelper;
import pl.ipebk.tabi.utils.Provider;
import pl.ipebk.tabi.utils.Stopwatch;

public abstract class BaseActivity extends AppCompatActivity {
    protected DatabaseHelperInterface databaseHelper;
    protected Stopwatch stopwatch;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopwatch = new Stopwatch();
        initHelpers();
    }

    protected void initHelpers() {
        Provider provider = ((App) getApplicationContext()).getProvider();
        databaseHelper = provider.getDatabaseHelper();
        new PrepareDatabaseAsyncTask().execute();
    }

    protected void onDatabasePrepared(Stopwatch.ElapsedTime elapsedTime) {
    }

    private class PrepareDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override protected void onPreExecute() {
            stopwatch.reset();
        }

        @Override protected Void doInBackground(Void... voids) {
            databaseHelper.init();
            return null;
        }

        @Override protected void onPostExecute(Void result) {
            onDatabasePrepared(stopwatch.getElapsedTime());
        }
    }
}
