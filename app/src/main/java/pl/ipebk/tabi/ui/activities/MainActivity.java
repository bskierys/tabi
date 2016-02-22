package pl.ipebk.tabi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.ui.activities.BaseActivity;
import pl.ipebk.tabi.utils.Stopwatch;

public class MainActivity extends BaseActivity {
    private TextView prompt;
    private String elapsed;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prompt = (TextView) findViewById(R.id.prompt);
        prompt.setText("Preparing database");
        if (elapsed != null) {
            prompt.setText(elapsed);
        }
    }

    @OnClick(R.id.go) public void onGo(){
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }

    @Override protected void onDatabasePrepared(Stopwatch.ElapsedTime elapsedTime) {
        elapsed = elapsedTime.toString();
        if (prompt != null) {
            prompt.setText(elapsed);
        }
    }
}
