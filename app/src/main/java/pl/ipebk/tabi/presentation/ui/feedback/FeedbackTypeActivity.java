package pl.ipebk.tabi.presentation.ui.feedback;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import com.suredigit.inappfeedback.FeedbackType;

import javax.inject.Inject;

import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;

public class FeedbackTypeActivity extends BaseActivity {
    static final String PARAM_ISSUE_TYPE = "param_issue_type";

    @BindView(R.id.background_layout) View background;
    @BindView(R.id.content_container) View contentContainer;
    @BindView(R.id.fake_toolbar) View toolbar;

    @Inject AnimationCreator animationCreator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_type);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTransition();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
