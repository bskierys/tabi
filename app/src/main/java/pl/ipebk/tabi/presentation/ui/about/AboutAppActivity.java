/*
 * author: Bartlomiej Kierys
 * date: 2016-12-26
 * email: bskierys@gmail.com
 */
package pl.ipebk.tabi.presentation.ui.about;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.main.DoodleTextFormatter;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RecyclerViewTotalScrollEvent;
import pl.ipebk.tabi.presentation.ui.utils.rxbinding.RxRecyclerViewExtension;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AboutAppActivity extends BaseActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.libraries_list) RecyclerView librariesView;
    @BindView(R.id.appBar) View appBar;
    @BindView(R.id.background_layout) View background;
    @BindDimen(R.dimen.Toolbar_Height_Min) int lowestSearchBarPosition;
    @Inject AnimationCreator animationCreator;
    @Inject DoodleTextFormatter doodleTextFormatter;
    private LibraryAdapter adapter;
    private Subscription scrollSubscription;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getActivityComponent().inject(this);

        initLibraries();
        setupTransition();
        getLoadLibsObservable().observeOn(AndroidSchedulers.mainThread())
                               .subscribeOn(Schedulers.newThread())
                               .subscribe(listItems -> {
                                   adapter.appendList(listItems);
                                   adapter.notifyDataSetChanged();
                               }, error -> Timber.w(error, "Activity already closed"));

        scrollSubscription = RxRecyclerViewExtension.totalScrollEvents(librariesView)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .map(RecyclerViewTotalScrollEvent::totalScrollY)
                                                    .map(this::computePercentScrolled)
                                                    .subscribe(this::setAnimationState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition() {
        AnimationCreator.CategoryAnimator anim = animationCreator.getCategoryAnimator();

        Transition enterTransition = anim.createBgFadeInTransition();
        enterTransition.addListener(new SimpleTransitionListener.Builder()
                                            .withOnStartAction(t -> {
                                                librariesView.setVisibility(View.INVISIBLE);
                                                toolbar.setVisibility(View.INVISIBLE);
                                                background.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
                                            })
                                            .withOnEndAction(t -> {
                                                librariesView.setVisibility(View.VISIBLE);
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

    private float computePercentScrolled(int scrollPosition) {
        float distance = lowestSearchBarPosition;

        float percent = (float) scrollPosition / distance;
        if (percent > 1) {
            percent = 0.99f;
        }

        return percent;
    }

    private void setAnimationState(float percent) {
        appBar.setAlpha(percent);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(scrollSubscription);
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }

    private void initLibraries() {
        librariesView.setHasFixedSize(true);
        librariesView.setItemAnimator(LibsConfiguration.getInstance().getItemAnimator());
        librariesView.setLayoutManager(new LinearLayoutManager(this));

        List<LibraryAdapter.LibsItem> adapterItems = new ArrayList<>();
        adapterItems.add(new LibraryAdapter.LibsItem(null));
        adapter = new LibraryAdapter(this, adapterItems, doodleTextFormatter);
        adapter.setBackListener(this::onBackButton);
        librariesView.setAdapter(adapter);
    }

    private Observable<List<LibraryAdapter.LibsItem>> getLoadLibsObservable() {
        return Observable.defer(() -> Observable.just(getItems()));
    }

    private List<LibraryAdapter.LibsItem> getItems() {
        Libs libs = new Libs(this);
        List<LibraryAdapter.LibsItem> adapterItems = new ArrayList<>();
        List<Library> libraries = libs.prepareLibraries(this, new String[]{}, null, true, true);
        for (Library library : libraries) {
            adapterItems.add(new LibraryAdapter.LibsItem(library));
        }
        return adapterItems;
    }
}
