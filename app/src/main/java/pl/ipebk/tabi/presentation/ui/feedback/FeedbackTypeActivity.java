package pl.ipebk.tabi.presentation.ui.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;

public class FeedbackTypeActivity extends BaseActivity {
    private static final String TABI_FEEDBACK_EMAIL = "tabi.appfeedback@gmail.com";
    private final Map<FeedbackType, String> emailBodyTexts = new HashMap<>();

    @BindView(R.id.background_layout) View background;
    @BindView(R.id.content_container) View contentContainer;
    @BindView(R.id.fake_toolbar) View toolbar;

    @Inject AnimationCreator animationCreator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_type);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        setupTransition();
        setupEmailBodyTexts();
    }

    private void setupEmailBodyTexts() {
        emailBodyTexts.put(FeedbackType.BUG, getString(R.string.feedback_email_body_bug));
        emailBodyTexts.put(FeedbackType.IDEA, getString(R.string.feedback_email_body_idea));
        emailBodyTexts.put(FeedbackType.QUESTION, getString(R.string.feedback_email_body_question));
    }

    private void setupTransition() {
        AnimationCreator.CategoryAnimator anim = animationCreator.getCategoryAnimator();

        Transition enterTransition = anim.createBgFadeInTransition();
        enterTransition.addListener(new SimpleTransitionListener.Builder()
                                            .withOnStartAction(t -> {
                                                contentContainer.setVisibility(View.INVISIBLE);
                                                toolbar.setVisibility(View.INVISIBLE);
                                                background.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
                                            })
                                            .withOnEndAction(t -> {
                                                contentContainer.setVisibility(View.VISIBLE);
                                                toolbar.setVisibility(View.VISIBLE);
                                                background.setBackgroundColor(getResources().getColor(R.color.transparent));
                                            })
                                            .build());
        getWindow().setEnterTransition(enterTransition);

        Transition returnTransition = anim.createBgFadeOutTransition();
        returnTransition.addListener(new SimpleTransitionListener.Builder()
                                             .withOnStartAction(t -> {
                                                 background.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
                                             })
                                             .build());
        getWindow().setReturnTransition(returnTransition);

        anim.alterSharedTransition(getWindow().getSharedElementEnterTransition());
        anim.alterSharedTransition(getWindow().getSharedElementReturnTransition());
    }

    @OnClick(R.id.btn_back) public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_issue_bug) public void onIssueBug() {
        promptIssueInput(FeedbackType.BUG);
    }

    @OnClick(R.id.btn_issue_idea) public void onIssueIdea() {
        promptIssueInput(FeedbackType.IDEA);
    }

    @OnClick(R.id.btn_issue_question) public void onIssueQuestion() {
        promptIssueInput(FeedbackType.QUESTION);
    }

    private void promptIssueInput(FeedbackType type) {
        Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
        feedbackIntent.setType("plain/text");
        feedbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{TABI_FEEDBACK_EMAIL});
        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject));
        feedbackIntent.putExtra(Intent.EXTRA_TEXT, emailBodyTexts.get(type));
        startActivity(Intent.createChooser(feedbackIntent, getString(R.string.feedback_email_chooser_title)));
    }
}
