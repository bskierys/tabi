package pl.ipebk.tabi.presentation.ui.feedback;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;

public class FeedbackEntryActivity extends BaseActivity {
    @BindView(R.id.edit_issue) EditText issueEntry;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_entry);
        ButterKnife.bind(this);

        int issueType = getIntent().getIntExtra(FeedbackTypeActivity.PARAM_ISSUE_TYPE, 0);
        String hint;
        switch (issueType) {
            case FeedbackTypeActivity.ISSUE_TYPE_BUG: {
                hint = "Z czym masz problem?";
                break;
            }
            case FeedbackTypeActivity.ISSUE_TYPE_IDEA: {
                hint = "Zaproponuj coś od siebie...";
                break;
            }
            case FeedbackTypeActivity.ISSUE_TYPE_QUESTION: {
                hint = "O co chesz zapytać?";
                break;
            }
            default: {
                hint = "brak tekstu";
                break;
            }
        }
        issueEntry.setHint(hint);
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
    }
}
