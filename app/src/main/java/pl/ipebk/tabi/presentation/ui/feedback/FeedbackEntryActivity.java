package pl.ipebk.tabi.presentation.ui.feedback;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;

public class FeedbackEntryActivity extends BaseActivity {
    @BindView(R.id.edit_issue) EditText issueEntry;
    @BindView(R.id.txt_title) TextView titleBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_entry);
        ButterKnife.bind(this);

        int issueType = getIntent().getIntExtra(FeedbackTypeActivity.PARAM_ISSUE_TYPE, 0);
        String hint;
        String title;
        switch (issueType) {
            case FeedbackTypeActivity.ISSUE_TYPE_BUG: {
                hint = "Z czym masz problem?";
                title = "Zgłoś nam swój problem";
                break;
            }
            case FeedbackTypeActivity.ISSUE_TYPE_IDEA: {
                hint = "Zaproponuj coś od siebie...";
                title = "Zgłoś nam swój pomysł";
                break;
            }
            case FeedbackTypeActivity.ISSUE_TYPE_QUESTION: {
                hint = "O co chesz zapytać?";
                title = "Zadaj nam swoje pytanie";
                break;
            }
            default: {
                hint = "brak tekstu";
                title = "brak tekstu";
                break;
            }
        }
        issueEntry.setHint(hint);
        titleBar.setText(title);
    }

    @Override public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @OnClick(R.id.btn_back) public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_clear) public void clearText() {
        issueEntry.setText(null);
    }

    @OnClick(R.id.btn_send) public void onSend() {
        String issueEntryText = issueEntry.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FeedbackTypeActivity.PARAM_ISSUE_RESULT, issueEntryText);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0,0);
    }
}
