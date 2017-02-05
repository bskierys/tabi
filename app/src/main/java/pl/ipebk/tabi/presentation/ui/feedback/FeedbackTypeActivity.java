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
    static final String PARAM_ISSUE_TYPE = "param_issue_type";

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
        startActivity(issueIntent);
    }
}
