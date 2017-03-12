/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.details;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseFragment;
import pl.ipebk.tabi.presentation.ui.custom.DoodleImage;
import pl.ipebk.tabi.presentation.ui.custom.ObservableSizeLayout;
import pl.ipebk.tabi.presentation.ui.custom.chromeTabs.CustomTabActivityHelper;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.utils.ViewUtil;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.presentation.ui.utils.animation.RxAnimator;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.presentation.ui.utils.animation.SimpleTransitionListener;
import pl.ipebk.tabi.presentation.utils.Stopwatch;
import pl.ipebk.tabi.presentation.utils.StopwatchManager;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DetailsFragment extends BaseFragment implements DetailsMvpView, Callback {
    private final static String ARG_PLACE_ID = "param_place_id";
    private final static String ARG_SEARCHED_PLATE = "param_searched_plate";
    private final static String ARG_SEARCHED_TYPE = "param_searched_type";
    private final static String ARG_ITEM_TYPE = "param_item_type";
    private final static String ARG_ADAPTER_POSITION = "param_adapter_position";
    private static final int BUTTON_PANEL_MAP_INDEX = 1;
    private static final int BUTTON_PANEL_GOOGLE_INDEX = 0;

    @Inject DetailsPresenter presenter;
    @Inject AnimationCreator animationCreator;
    @Inject StopwatchManager stopwatchManager;
    @Inject FontManager fontManager;
    @Inject MapScaleCalculator mapScaleCalculator;
    @Inject CustomTabActivityHelper chromeTabHelper;

    // texts
    @BindView(R.id.txt_place_name) TextView placeNameView;
    @BindView(R.id.txt_plate) TextView plateView;
    @BindView(R.id.txt_voivodeship) TextView voivodeshipView;
    @BindView(R.id.txt_powiat) TextView powiatView;
    @BindView(R.id.txt_gmina) TextView gminaView;
    @BindView(R.id.txt_additional) TextView additionalInfoView;
    // map and panel
    @BindView(R.id.img_map) ImageView mapView;
    @BindView(R.id.wrap_map) ObservableSizeLayout mapWrapper;
    @BindView(R.id.img_pin) ImageView pinView;
    @BindView(R.id.map_with_panel) ViewGroup mapAndPanel;
    @BindView(R.id.card_panel) CardView panelCard;
    @BindViews({R.id.btn_google_it, R.id.btn_map}) List<Button> actionButtons;
    // others
    @BindView((R.id.ic_row)) ImageView placeIcon;
    @BindView(R.id.wrap_place_header) ObservableSizeLayout placeHeaderWrapper;
    @BindView(R.id.img_placeholder) ImageView placeHolder;
    @BindView(R.id.divider) View divider;
    // animations
    @BindView(R.id.sceneRoot) ViewGroup transitionSceneRoot;
    @BindView(R.id.animation_bg_google) View animGoogleBg;
    @BindView(R.id.animation_bg_map) View animMapBg;
    @BindView(R.id.animation_info_bg) View animInfoBg;
    @BindView(R.id.info_wrap) View infoWrap;
    @BindView(R.id.animation_root) View animationRoot;

    private String preloadedSearchPhrase;
    private Stopwatch stopwatch;
    private Typeface doodleHeaderFont;
    private Typeface doodleDescriptionFont;
    private Picasso picasso;
    private boolean transitionUsed;

    private PublishSubject<Integer> mapWidthStream = PublishSubject.create();
    private PublishSubject<Integer> mapHeightStream = PublishSubject.create();
    private Subscription mapErrorSub;
    private Subscription delayedStartSub;
    private CompositeSubscription animSubs;

    public static DetailsFragment newInstance(long placeId, String searchedPlate, PlaceListItemType itemType,
                                              SearchType searchType, int position) {
        Bundle args = new Bundle();

        DetailsFragment fragment = new DetailsFragment();
        args.putLong(ARG_PLACE_ID, placeId);
        args.putString(ARG_SEARCHED_PLATE, searchedPlate);
        args.putInt(ARG_ADAPTER_POSITION, position);
        args.putSerializable(ARG_ITEM_TYPE, itemType);
        args.putSerializable(ARG_SEARCHED_TYPE, searchType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        presenter.attachView(this);
        animSubs = new CompositeSubscription();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_details, container, false);
        ButterKnife.bind(this, view);
        setupEnterAndReturnTransitions();

        doodleHeaderFont = fontManager.get("bebas-book", Typeface.NORMAL);
        doodleDescriptionFont = fontManager.get("montserrat", Typeface.NORMAL);

        picasso = new Picasso.Builder(getActivity())
                .listener((picasso, uri, e) -> onError(e, uri))
                .build();

        stopwatch = stopwatchManager.getDefaultStopwatch();
        stopwatch.reset();
        clearPreviewLayout();
        loadData();

        Timber.d("Fragment layout creation time: %s", stopwatch.getElapsedTimeString());

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        if (animGoogleBg.getVisibility() == View.VISIBLE) {
            animateButtonBack(actionButtons.get(BUTTON_PANEL_GOOGLE_INDEX), animGoogleBg);
        }
        if (animMapBg.getVisibility() == View.VISIBLE) {
            animateButtonBack(actionButtons.get(BUTTON_PANEL_MAP_INDEX), animMapBg);
        }
    }

    private void animateButtonBack(View button, View mockBg) {
        Rect buttonBounds = getViewBounds(button);
        Rect screenBounds = ViewUtil.getScreenBounds(getActivity().getWindowManager());

        AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
        Animator animator = anim.createDetailActionAnim(mockBg, screenBounds, buttonBounds);

        animSubs.add(RxAnimator.animationEnd(animator).subscribe(a -> mockBg.setVisibility(View.INVISIBLE)));
        animator.start();
    }

    private void setupEnterAndReturnTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int position = getArguments().getInt(ARG_ADAPTER_POSITION);

            placeNameView.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_place_name), position));
            plateView.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_place_plate), position));
            placeIcon.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_place_icon), position));
            voivodeshipView.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_voivodeship_name), position));
            powiatView.setTransitionName(SharedTransitionNaming.getName(getString(R.string.trans_powiat_name), position));

            animationCreator.getDetailsAnimator().prepareViewForPanelAnim(panelCard);

            Transition enterTransition = getActivity().getWindow().getEnterTransition();
            Transition returnTransition = getActivity().getWindow().getReturnTransition();

            enterTransition.addListener(new SimpleTransitionListener.Builder()
                                                .withOnStartAction(t -> {
                                                    transitionUsed = true;
                                                    animateEnter();
                                                    mapAndPanel.setVisibility(View.INVISIBLE);
                                                })
                                                .withOnEndAction(t -> {
                                                    divider.setVisibility(View.VISIBLE);
                                                    mapAndPanel.setVisibility(View.VISIBLE);
                                                    computeMapBounds();
                                                    mapWrapper.getBoundsStream().filter(bounds -> bounds.height() > 0)
                                                              .throttleLast(100, TimeUnit.MILLISECONDS).subscribe(bounds -> {
                                                        mapWrapper.post(this::computeMapBounds);
                                                    });
                                                })
                                                .unregisterOnEnd().build());
            returnTransition.addListener(new SimpleTransitionListener.Builder()
                                                 .withOnStartAction(t -> {
                                                     panelCard.setVisibility(View.INVISIBLE);
                                                     divider.setVisibility(View.INVISIBLE);
                                                     gminaView.setVisibility(View.INVISIBLE);
                                                     mapAndPanel.setVisibility(View.INVISIBLE);
                                                     additionalInfoView.setVisibility(View.INVISIBLE);
                                                 }).build());
        } else {
            transitionUsed = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateEnter() {
        gminaView.setVisibility(View.INVISIBLE);
        divider.setVisibility(View.INVISIBLE);
        additionalInfoView.setVisibility(View.INVISIBLE);

        Transition fadeIn = animationCreator
                .getDetailsAnimator()
                .createContentFadeInTransition(gminaView, additionalInfoView);
        TransitionManager.beginDelayedTransition(transitionSceneRoot, fadeIn);

        gminaView.setVisibility(View.VISIBLE);
        additionalInfoView.setVisibility(View.VISIBLE);
        showPanel(true);
    }

    private void clearPreviewLayout() {
        showPlaceName("");
        showGmina("");
        showPowiat("");
        showVoivodeship("");
        showAdditionalInfo("");
        showPlate("");
        placeNameView.setTextIsSelectable(true);
        plateView.setTextIsSelectable(true);
    }

    private void loadData() {
        Bundle args = getArguments();
        long placeId = args.getLong(ARG_PLACE_ID, 0L);
        String searchedPlate = args.getString(ARG_SEARCHED_PLATE);
        PlaceListItemType itemType = (PlaceListItemType) args.getSerializable(ARG_ITEM_TYPE);
        SearchType searchType = (SearchType) args.getSerializable(ARG_SEARCHED_TYPE);
        if (placeId > 0) {
            presenter.loadPlace(placeId, searchedPlate, searchType, itemType);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mapWrapper.getBoundsStream().filter(bounds -> bounds.height() > 0)
                      .throttleLast(100, TimeUnit.MILLISECONDS).subscribe(bounds -> {
                mapWrapper.post(this::computeMapBounds);
            });
        }
    }

    private void computeMapBounds() {
        int totalHeight = mapWrapper.getHeight()
                - mapWrapper.getPaddingBottom()
                - mapWrapper.getPaddingTop();
        int totalWidth = mapWrapper.getWidth()
                - mapWrapper.getPaddingLeft()
                - mapWrapper.getPaddingRight();

        Timber.d("Map bounds computed. Height: %d, width: %d", totalHeight, totalWidth);

        float density = mapScaleCalculator.getScreenDensity();
        mapHeightStream.onNext((int) (totalHeight / density));
        mapWidthStream.onNext((int) (totalWidth / density));
    }

    @Override public void onStart() {
        super.onStart();
        chromeTabHelper.bindCustomTabsService(getActivity());
        if (preloadedSearchPhrase != null) {
            Uri uri = Uri.parse(preloadedSearchPhrase);
            chromeTabHelper.mayLaunchUrl(uri, null, null);
        }

        delayedStartSub = Observable.just(null)
                                    .delay(100, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(n -> {
                                        if (!transitionUsed) {
                                            Timber.d("Transition not used in activity. starting delayed action");
                                            showPanel(false);
                                            computeMapBounds();
                                        }
                                    });
    }

    public void showPanel(boolean animate) {
        if (animate) {
            AnimatorSet set = new AnimatorSet();
            set.play(animationCreator.getDetailsAnimator().createPanelEnterScaleAnim(panelCard))
               .with(animationCreator.getDetailsAnimator().createPanelEnterFadeInAnim(panelCard));
            set.start();
        } else {
            panelCard.setAlpha(1f);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        chromeTabHelper.unbindCustomTabsService(getActivity());
        RxUtil.unsubscribe(mapErrorSub);
        RxUtil.unsubscribe(delayedStartSub);
        RxUtil.unsubscribe(animSubs);
        presenter.detachView();
    }

    @OnClick(R.id.btn_google_it) public void onSearchMore() {
        presenter.searchInGoogle();
    }

    @OnClick(R.id.btn_map) public void onShowOnMap() {
        presenter.showOnMap();
    }

    @OnClick(R.id.btn_copy) public void onCopy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Rect viewBounds = getViewBounds(infoWrap);
            Rect screenBounds = ViewUtil.getScreenBounds(getActivity().getWindowManager());
            screenBounds.bottom -= getToolbarHeight();

            animationRoot.getLayoutParams().height = screenBounds.height();
            animationRoot.getLayoutParams().width = screenBounds.width();
            animationRoot.requestLayout();

            placeMockViewInBounds(animInfoBg, viewBounds);
            copyViewToImageView(infoWrap, (ImageView) animInfoBg.findViewById(R.id.image_layer));

            AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
            Animator copyAnim = anim.createCopyAnim(viewBounds, animInfoBg);

            animInfoBg.setVisibility(View.VISIBLE);
            copyAnim.start();

            animSubs.add(RxAnimator.animationEnd(copyAnim).subscribe(a -> {
                presenter.copyToClipboard();
                animInfoBg.setVisibility(View.INVISIBLE);
                animationRoot.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;
                animationRoot.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                animationRoot.requestLayout();
            }));
        } else {
            presenter.copyToClipboard();
        }
    }

    public void copyViewToImageView(View source, ImageView copyImageView) {
        source.setDrawingCacheEnabled(true);
        Bitmap copyImage = Bitmap.createBitmap(source.getDrawingCache());
        source.setDrawingCacheEnabled(false);
        copyImageView.setImageBitmap(copyImage);
    }

    @Override public void showPlaceIcon(int iconResId) {
        placeIcon.setImageResource(iconResId);
    }

    @Override public void showPlaceName(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startPostponedEnterTransition();
        }
        placeNameView.setText(name);
    }

    @Override public void showPlate(String plate) {
        plateView.setText(plate);
    }

    @Override public void showGmina(String gmina) {
        gminaView.setText(gmina);
    }

    @Override public void showPowiat(String powiat) {
        powiatView.setText(powiat);
    }

    @Override public void showVoivodeship(String voivodeship) {
        voivodeshipView.setText(voivodeship);
    }

    @Override public void showAdditionalInfo(String additionalInfo) {
        additionalInfoView.setText(additionalInfo);
    }

    @Override public void showMap(Uri uri) {
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(getActivity())
                .height(mapView.getHeight())
                .width(mapView.getWidth())
                .headerFont(doodleHeaderFont)
                .descriptionFont(doodleDescriptionFont)
                .spaceBeforeImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Map_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Map_Space_After));

        DoodleImage loadingDoodle = doodleBuilder
                .imageResource(R.drawable.tabi_map_loading)
                .headerText(getString(R.string.details_doodle_loading_header))
                .descriptionText(getString(R.string.details_doodle_loading_description))
                .build();

        DoodleImage errorDoodle = doodleBuilder
                .imageResource(R.drawable.tabi_map_error)
                .headerText(getString(R.string.details_doodle_error_header))
                .descriptionText(getString(R.string.details_doodle_error_description))
                .build();

        Observable.combineLatest(
                Observable.just(loadingDoodle).doOnNext(DoodleImage::preComputeScale),
                Observable.just(errorDoodle).doOnNext(DoodleImage::preComputeScale),
                Pair<DoodleImage, DoodleImage>::new)
                  .subscribeOn(Schedulers.computation())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(pair -> loadImageWithPicasso(uri, pair.first.asDrawable(),
                                                          pair.second.asDrawable()));
    }

    @Override public void showMapError() {
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(getActivity())
                .height(mapView.getHeight())
                .width(mapView.getWidth())
                .headerFont(doodleHeaderFont)
                .descriptionFont(doodleDescriptionFont)
                .spaceBeforeImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Map_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Map_Space_After));

        DoodleImage errorDoodle = doodleBuilder
                .imageResource(R.drawable.tabi_map_error)
                .headerText(getString(R.string.details_doodle_error_header))
                .descriptionText(getString(R.string.details_doodle_error_description))
                .build();

        mapView.setImageDrawable(errorDoodle.asDrawable());
        pinView.setVisibility(View.INVISIBLE);
    }

    private void loadImageWithPicasso(Uri uri, Drawable loading, Drawable error) {
        stopwatch.reset();
        picasso.load(uri).fit().centerCrop()
               .error(error)
               .placeholder(loading)
               .into(mapView, this);
    }

    @Override public void enableActionButtons() {
        ButterKnife.apply(actionButtons, new ButterKnife.Action<Button>() {
            @Override public void apply(@NonNull Button view, int index) {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override public void disableActionButtons() {
        ButterKnife.apply(actionButtons, new ButterKnife.Action<Button>() {
            @Override public void apply(@NonNull Button view, int index) {
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override public void showInfoMessageCopied() {
        Toast.makeText(getActivity(), getString(R.string.details_info_copy_done), Toast.LENGTH_SHORT).show();
    }

    @Override public void startMapApp(Uri geoLocation) {
        Button mapButton = actionButtons.get(BUTTON_PANEL_MAP_INDEX);

        Rect buttonBounds = getViewBounds(mapButton);
        Rect screenBounds = ViewUtil.getScreenBounds(getActivity().getWindowManager());

        placeMockViewInBounds(animMapBg, buttonBounds);
        animMapBg.setVisibility(View.VISIBLE);

        AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
        Animator animator = anim.createDetailActionAnim(animMapBg, buttonBounds, screenBounds);

        animSubs.add(RxAnimator.animationEnd(animator).subscribe(a -> openGoogleMaps(geoLocation)));
        animator.start();
    }

    private void openGoogleMaps(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Timber.e("Cannot find activity to start for maps");
        }
    }

    @Override public void preloadWebSearch(String searchPhrase) {
        preloadedSearchPhrase = getSearchUrlForPhrase(searchPhrase);
    }

    private String getSearchUrlForPhrase(String searchPhrase) {
        return "http://www.google.com/search?q=" + searchPhrase;
    }

    @Override public void startWebSearch(String searchPhrase) {
        Button googleButton = actionButtons.get(BUTTON_PANEL_GOOGLE_INDEX);

        Rect buttonBounds = getViewBounds(googleButton);
        Rect screenBounds = ViewUtil.getScreenBounds(getActivity().getWindowManager());

        placeMockViewInBounds(animGoogleBg, buttonBounds);
        animGoogleBg.setVisibility(View.VISIBLE);

        AnimationCreator.DetailsAnimator anim = animationCreator.getDetailsAnimator();
        Animator animator = anim.createDetailActionAnim(animGoogleBg, buttonBounds, screenBounds);

        animSubs.add(RxAnimator.animationEnd(animator).subscribe(a -> startChromeTab(searchPhrase)));
        animator.start();
    }

    private void placeMockViewInBounds(View view, Rect newBounds) {
        view.getLayoutParams().height = newBounds.height();
        view.getLayoutParams().width = newBounds.width();
        view.setY(newBounds.top);
        view.setX(newBounds.left);
        view.requestLayout();
    }

    @NonNull private Rect getViewBounds(View view) {
        int toolbarHeight = getToolbarHeight();

        int left = ViewUtil.getRelativeLeft(view);
        int right = left + view.getWidth();
        int top = ViewUtil.getRelativeTop(view) - toolbarHeight;
        int bottom = top + view.getHeight();

        return new Rect(left, top, right, bottom);
    }

    private int getToolbarHeight() {
        return getResources().getDimensionPixelSize(R.dimen.Toolbar_Height_Min)
                + getResources().getDimensionPixelSize(R.dimen.StatusBar);
    }

    private void startChromeTab(String searchPhrase) {
        String url = getSearchUrlForPhrase(searchPhrase);
        int primaryColor = getResources().getColor(R.color.white);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(primaryColor);
        intentBuilder.enableUrlBarHiding();
        intentBuilder.setStartAnimations(getActivity(), 0, 0);
        intentBuilder.setExitAnimations(getActivity(), 0, 0);

        CustomTabsIntent customTabsIntent = intentBuilder.build();
        chromeTabHelper.openCustomTab(getActivity(), customTabsIntent, Uri.parse(url), ((activity, uri) -> {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, searchPhrase);
            startActivity(intent);
        }));
    }

    @Override public void showPlaceHolder() {
        stopwatch.reset();
        placeHeaderWrapper.getBoundsStream().filter(bounds -> bounds.height() > 0)
                          .throttleLast(100, TimeUnit.MILLISECONDS).subscribe(bounds -> {
            Timber.d("Placeholder bounds computed. Height: %d, width: %d", bounds.height(), bounds.width());
            placeHeaderWrapper.post(this::setPlaceHolderImage);
        });

        placeHolder.setVisibility(View.VISIBLE);
        mapAndPanel.setVisibility(View.GONE);
        voivodeshipView.setVisibility(View.GONE);
        powiatView.setVisibility(View.GONE);
        gminaView.setVisibility(View.GONE);
        additionalInfoView.setVisibility(View.GONE);
        panelCard.setVisibility(View.GONE);
        ButterKnife.apply(actionButtons, new ButterKnife.Action<Button>() {
            @Override public void apply(@NonNull Button view, int index) {
                view.setVisibility(View.GONE);
            }
        });
    }

    @Override public Observable<Integer> getMapWidthStream() {
        return mapWidthStream.asObservable();
    }

    @Override public Observable<Integer> getMapHeightStream() {
        return mapHeightStream.asObservable();
    }

    private void setPlaceHolderImage() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int headerHeight = getActionBarSize() + getStatusBarHeight() + placeHeaderWrapper.getHeight();

        int preferredPlaceholderSize = getResources().getDimensionPixelSize(R.dimen.Details_Height_Placeholder);

        int height = metrics.heightPixels - headerHeight;
        int width = metrics.widthPixels;

        if (height > preferredPlaceholderSize) {
            height = preferredPlaceholderSize;
        }

        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(getActivity())
                .height(height - placeHolder.getPaddingTop() - placeHolder.getPaddingBottom())
                .width(width - placeHolder.getPaddingRight() - placeHolder.getPaddingLeft())
                .headerFont(doodleHeaderFont)
                .descriptionFont(doodleDescriptionFont)
                .spaceBeforeImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Empty_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelSize(
                        R.dimen.Details_Height_Doodle_Empty_Space_After));

        DoodleImage placeholderDoodle = doodleBuilder
                .imageResource(R.drawable.tabi_placeholder)
                .headerText(getString(R.string.details_doodle_empty_header))
                .descriptionText(getString(R.string.details_doodle_empty_description))
                .build();

        Observable.just(placeholderDoodle).doOnNext(DoodleImage::preComputeScale)
                  .subscribeOn(Schedulers.computation())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(doodle -> {
                      placeHolder.setImageBitmap(doodle.draw());
                      Timber.d("Rendering placeholder took: %s", stopwatch.getElapsedTimeString());
                  });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getActionBarSize() {
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }

    //region Picasso callback methods
    @Override public void onSuccess() {
        pinView.setVisibility(View.VISIBLE);
        Timber.d("Map loading time: %s", stopwatch.getElapsedTimeString());
    }

    @Override public void onError() {
        pinView.setVisibility(View.INVISIBLE);
    }

    private void onError(Throwable e, Uri uri) {
        Timber.e(e, "Failed to load image: %s", uri);

        if (!isNetworkAvailable() || e instanceof UnknownHostException) {
            mapErrorSub = ReactiveNetwork.observeNetworkConnectivity(getActivity())
                                         .subscribeOn(Schedulers.io())
                                         .filter(connectivity -> connectivity.getState() == NetworkInfo.State.CONNECTED)
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(con -> showMap(uri));
        }
    }

    private boolean isNetworkAvailable() {
        if (isDetached() || getActivity() == null) {
            return false;
        }

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    //endregion
}
