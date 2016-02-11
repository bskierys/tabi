package pl.ipebk.tabi.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import pl.ipebk.tabi.R;
import pl.ipebk.tabi.utils.Stopwatch;

public class MainActivity extends BaseActivity {
    private TextView prompt;
    private String elapsed;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prompt = (TextView) findViewById(R.id.prompt);
        prompt.setText("Preparing database");
        if (elapsed != null) {
            prompt.setText(elapsed);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override protected void onDatabasePrepared(Stopwatch.ElapsedTime elapsedTime) {
        elapsed = elapsedTime.toString();
        if (prompt != null) {
            prompt.setText(elapsed);
        }
    }
}
