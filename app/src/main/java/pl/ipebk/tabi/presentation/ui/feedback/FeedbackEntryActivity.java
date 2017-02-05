package pl.ipebk.tabi.presentation.ui.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.feedback.FeedbackClient;
import pl.ipebk.tabi.feedback.FeedbackType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Subscription;
import timber.log.Timber;

public class FeedbackEntryActivity extends BaseActivity {
    @Inject FeedbackClient feedbackClient;

    @BindView(R.id.edit_issue) EditText issueEntry;
    @BindView(R.id.txt_title) TextView titleBar;

    private ProgressDialog progressDialog;
    private FeedbackType feedbackType;
    private boolean destroyed;
    private Subscription sendingSub;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_entry);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        feedbackType = (FeedbackType) getIntent().getSerializableExtra(FeedbackTypeActivity.PARAM_ISSUE_TYPE);
        tailorTextsToIssueType(feedbackType);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(sendingSub);
        destroyed = true;
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

    protected void showProgressDialog(final CharSequence message) {
        final Context context = this;
        runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
            }
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        });
    }

    protected void dismissLoadingDialog() {
        runOnUiThread(() -> {
            if (progressDialog != null && !destroyed) {
                progressDialog.dismiss();
            }
        });
    }

    @Override public void onBackPressed() {
        Intent returnIntent = new Intent(this, FeedbackTypeActivity.class);
        startActivity(returnIntent);
    }

    @OnClick(R.id.btn_back) public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_clear) public void clearText() {
        issueEntry.setText(null);
    }

    @OnClick(R.id.btn_send) public void onSend() {
        String issueEntryText = issueEntry.getText().toString();

        showProgressDialog(getString(R.string.feedback_sending));
        Toast.makeText(this, getString(R.string.main_feedback_done), Toast.LENGTH_SHORT).show();
        sendingSub = feedbackClient.sendFeedback(issueEntryText, feedbackType)
                                   .subscribe(v -> Timber.d("Feedback has been sent"), error -> {
                                       Timber.w(error, "Could not send feedback. Postponing");
                                       dismissLoadingDialog();
                                       closeScreen();
                                   }, () -> {
                                       dismissLoadingDialog();
                                       closeScreen();
                                   });
    }

    private void closeScreen() {
        finish();
        overridePendingTransition(0, 0);
    }
}
