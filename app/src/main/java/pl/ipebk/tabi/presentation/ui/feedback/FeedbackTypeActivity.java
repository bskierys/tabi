package pl.ipebk.tabi.presentation.ui.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.feedback.FeedbackType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;

public class FeedbackTypeActivity extends BaseActivity {
    static final int REQUEST_CODE_ENTRY = 56;
    static final String PARAM_ISSUE_TYPE = "param_issue_type";
    static final String PARAM_ISSUE_RESULT = "param_issue_result";
    static final int ISSUE_TYPE_BUG = 1;
    static final int ISSUE_TYPE_IDEA = 2;
    static final int ISSUE_TYPE_QUESTION = 3;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_type);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_back) public void onBack(){
        onBackPressed();
    }

    @OnClick(R.id.btn_issue_bug) public void onIssueBug(){
        promptIssueInput(FeedbackType.BUG);
    }

    @OnClick(R.id.btn_issue_idea) public void onIssueIdea(){
        promptIssueInput(FeedbackType.IDEA);
    }

    @OnClick(R.id.btn_issue_question) public void onIssueQuestion(){
        promptIssueInput(FeedbackType.QUESTION);
    }

    private void promptIssueInput(FeedbackType type) {
        Intent issueIntent = new Intent(this, FeedbackEntryActivity.class);
        issueIntent.putExtra(PARAM_ISSUE_TYPE, type);
        startActivityForResult(issueIntent, REQUEST_CODE_ENTRY);
        overridePendingTransition(0,0);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ENTRY) {
            if (resultCode == RESULT_OK) {
                String issueEntryText = data.getStringExtra(PARAM_ISSUE_RESULT);
                Toast.makeText(this, issueEntryText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
