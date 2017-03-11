package pl.ipebk.tabi.presentation.ui.details;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import javax.inject.Inject;

import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.MarginProxy;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Simple base class for details activities. It handles common methods for both classes
 */
public abstract class DetailsActivity extends BaseActivity {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";
    public final static String PARAM_ADAPTER_POSITION = "param_adapter_position";

    protected ScrollView scrollContainer;
    protected ImageView blackOverlay;
    protected View rowBackground;
    private ObservableVerticalOverScrollBounceEffectDecorator overscrollDecorator;

    protected CompositeSubscription scrollSubscriptions;

    @Inject AnimationCreator animationCreator;

    /**
     * This metod must be called after inflating layout in child activity
     */
    protected void afterLayoutInflate(Bundle savedInstanceState) {
        getActivityComponent().inject(this);
        scrollContainer = (ScrollView) findViewById(R.id.scroll_container);
        blackOverlay = (ImageView) findViewById(R.id.overlay_black);
        rowBackground = findViewById(R.id.row_bg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupTransitions();
        }
        prepareOverScroll();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(scrollSubscriptions);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupTransitions() {
        int position = getIntent().getIntExtra(PARAM_ADAPTER_POSITION, -1);
        rowBackground.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_row_background), position));

        AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
        Transition enterTransition = anim.createBgFadeInTransition(blackOverlay);
        enterTransition.excludeTarget(scrollContainer, true);
        enterTransition.addListener(new SimpleTransitionListener.Builder().withOnEndAction(t -> {
            scrollContainer.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
            rowBackground.getLayoutParams().height = scrollContainer.getHeight();
        }).build());
        getWindow().setEnterTransition(enterTransition);

        Transition returnTransition = anim.createBgFadeOutTransition(blackOverlay);
        returnTransition.addListener(new SimpleTransitionListener.Builder().withOnStartAction(t -> {
            scrollContainer.setBackgroundColor(getResources().getColor(R.color.transparent));
        }).build());
        getWindow().setReturnTransition(returnTransition);

        anim.alterSharedTransition(getWindow().getSharedElementEnterTransition());
        anim.alterSharedTransition(getWindow().getSharedElementReturnTransition());
    }

    private void prepareOverScroll() {
        float marginOffset = getResources().getDimensionPixelOffset(R.dimen.Details_Height_Release_Scroll);
        scrollSubscriptions = new CompositeSubscription();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollContainer.setElevation(getResources().getDimensionPixelSize(R.dimen.Details_Elevation));
        }

        overscrollDecorator = new ObservableVerticalOverScrollBounceEffectDecorator(
                new ScrollViewOverScrollDecorAdapter(scrollContainer), 3f, 1f, -1
        );

        MarginProxy bgMarginManager = new MarginProxy(rowBackground);
        scrollSubscriptions.add(overscrollDecorator.getScrollEventStream().filter(scroll -> scroll != null)
                                                   .subscribe(scroll -> bgMarginManager.setTopMargin(scroll.intValue())));

        scrollSubscriptions.add(overscrollDecorator.getReleaseEventStream()
                                                   .filter(scroll -> scroll != null)
                                                   .subscribe(scroll -> {
                                                       if (scroll >= marginOffset || scroll <= marginOffset * (-1)) {
                                                           onOverscrolled();
                                                       } else {
                                                           onNotOverscrolled(scroll);
                                                       }
                                                   }));
    }

    protected abstract void loadData();

    protected Observable<Float> getScrollStream() {
        if(overscrollDecorator == null) {
            throw new IllegalStateException("Called too soon. Overscroll not prepared yet");
        }

        return overscrollDecorator.getScrollEventStream();
    }

    protected void onOverscrolled() {
        Timber.d("Screen overscrolled");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // going back without animation will be confusing. go back only when there is animation (API >= 21)
            Timber.d("API over lollipop. Loading previous screen");
            onBackPressed();
        }
    }

    protected abstract void onNotOverscrolled(Float scroll);
}
