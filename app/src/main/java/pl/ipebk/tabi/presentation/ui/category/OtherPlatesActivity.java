package pl.ipebk.tabi.presentation.ui.category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.chromeTabs.CustomTabActivityHelper;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;

public class OtherPlatesActivity extends BaseActivity {

    @Inject AnimationCreator animationCreator;
    @Inject CustomTabActivityHelper chromeTabHelper;

    @BindView(R.id.content_root) View rootOfView;
    @BindView(R.id.toolbar_parent) View toolbar;
    @BindView(R.id.content_container) View contentContainer;
    @BindView(R.id.background_layout) View background;
    @BindView(R.id.other_plate_category_list) RecyclerView recyclerView;
    @BindView(R.id.txt_title) TextView title;
    @BindView(R.id.txt_plate) TextView plateStart;

    private OtherPlatesAdapter adapter;
    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_plates);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        title.setText(getString(R.string.main_list_element_oth));
        plateStart.setText("");
        setupTransition();

        manager = getLayoutManager();
        adapter = getAdapter();
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void setupTransition() {
        AnimationCreator.CategoryAnimator anim = animationCreator.getCategoryAnimator();

        Transition enterTransition = anim.createBgFadeInTransition();
        enterTransition.addListener(new SimpleTransitionListener.Builder()
                                            .withOnStartAction(t -> {
                                                contentContainer.setVisibility(View.INVISIBLE);
                                                toolbar.setVisibility(View.INVISIBLE);
                                            })
                                            .withOnEndAction(t -> {
                                                toolbar.setVisibility(View.VISIBLE);
                                                contentContainer.setVisibility(View.VISIBLE);
                                            })
                                            .build());
        getWindow().setEnterTransition(enterTransition);

        Transition returnTransition = anim.createBgFadeOutTransition();
        getWindow().setReturnTransition(returnTransition);

        anim.alterSharedTransition(getWindow().getSharedElementEnterTransition());
        anim.alterSharedTransition(getWindow().getSharedElementReturnTransition());
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    public OtherPlatesAdapter getAdapter() {
        if (adapter == null) {

            List<CategoryInfo> otherPlates = new ArrayList<>();
            otherPlates.add(new AutoValue_CategoryInfo(
                    getString(R.string.other_classic_car_title),
                    getString(R.string.other_classic_car_body),
                    getString(R.string.other_classic_car_link),
                    getResources().getDrawable(R.drawable.vic_oth)
            ));
            otherPlates.add(new AutoValue_CategoryInfo(
                    getString(R.string.other_custom_title),
                    getString(R.string.other_custom_body),
                    getString(R.string.other_custom_link),
                    getResources().getDrawable(R.drawable.vic_oth)
            ));
            otherPlates.add(new AutoValue_CategoryInfo(
                    getString(R.string.other_temporary_title),
                    getString(R.string.other_temporary_body),
                    getString(R.string.other_temporary_link),
                    getResources().getDrawable(R.drawable.vic_oth)
            ));
            adapter = new OtherPlatesAdapter(otherPlates, this::launchUri);
        }

        return adapter;
    }

    // TODO: Copied
    private void launchUri(String url) {
        int primaryColor = getResources().getColor(R.color.white);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(primaryColor);
        intentBuilder.enableUrlBarHiding();
        intentBuilder.setStartAnimations(this, 0, 0);
        intentBuilder.setExitAnimations(this, 0, 0);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        chromeTabHelper.openCustomTab(this, customTabsIntent, Uri.parse(url), ((activity, uri) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }));
    }

    @OnClick(R.id.btn_back) public void onBack() {
        onBackPressed();
    }

    @Override protected void onStart() {
        super.onStart();
        chromeTabHelper.bindCustomTabsService(this);
    }

    @Override protected void onStop() {
        super.onStop();
        chromeTabHelper.unbindCustomTabsService(this);
    }
}
