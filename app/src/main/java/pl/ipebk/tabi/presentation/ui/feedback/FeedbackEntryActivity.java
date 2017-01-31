package pl.ipebk.tabi.presentation.ui.feedback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.feedback.FeedbackType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;

public class FeedbackEntryActivity extends BaseActivity {
    @Inject FeedbackClient feedbackClient;

    @BindView(R.id.edit_issue) EditText issueEntry;
    @BindView(R.id.txt_title) TextView titleBar;

    private FeedbackType feedbackType;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_entry);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        feedbackType = (FeedbackType) getIntent().getSerializableExtra(FeedbackTypeActivity.PARAM_ISSUE_TYPE);
        tailorTextsToIssueType(feedbackType);
    }

    private void tailorTextsToIssueType(FeedbackType issueType) {
        String hint;
        String title;
        switch (issueType) {
            case BUG: {
                hint = getString(R.string.feedback_hint_bug);
                title = getString(R.string.feedback_title_bug);
                break;
            }
            case IDEA: {
                hint = getString(R.string.feedback_hint_idea);
                title = getString(R.string.feedback_title_idea);
                break;
            }
            case QUESTION: {
                hint = getString(R.string.feedback_hint_question);
                title = getString(R.string.feedback_title_question);
                break;
            }
            default: {
                hint = getString(R.string.default_resource_string);
                title = getString(R.string.default_resource_string);
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

    // TODO: 2017-01-31 progress and delay for sending feedback
    @OnClick(R.id.btn_send) public void onSend() {
        String issueEntryText = issueEntry.getText().toString();

        // TODO: 2017-01-31 should be observable we can subscribe to 
        feedbackClient.sendFeedback(issueEntryText, feedbackType);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(0, 0);
    }
}
