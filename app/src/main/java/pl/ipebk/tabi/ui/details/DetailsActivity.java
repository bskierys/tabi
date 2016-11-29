package pl.ipebk.tabi.ui.details;

import android.animation.AnimatorSet;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.custom.ObservableSizeLayout;
import pl.ipebk.tabi.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.ui.search.PlaceListItemType;
import pl.ipebk.tabi.ui.search.SearchActivity;
import pl.ipebk.tabi.ui.search.SearchTabPageIndicator;
import pl.ipebk.tabi.utils.AnimationHelper;
import pl.ipebk.tabi.utils.DeviceHelper;
import pl.ipebk.tabi.utils.DoodleImage;
import pl.ipebk.tabi.utils.FontManager;
import pl.ipebk.tabi.utils.Stopwatch;
import pl.ipebk.tabi.utils.StopwatchManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class DetailsActivity extends BaseActivity implements DetailsMvpView, Callback {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";
    public final static String PARAM_SEARCHED_TYPE = "param_searched_type";
    public final static String PARAM_ITEM_TYPE = "param_item_type";

    @Inject DetailsPresenter presenter;
    @Inject Picasso picasso;
    @Inject AnimationHelper animationHelper;
    @Inject StopwatchManager stopwatchManager;
    @Inject FontManager fontManager;
    @Inject DeviceHelper deviceHelper;
    // toolbar
    @BindView(R.id.txt_searched) TextView searchedTextView;
    @BindView(R.id.editTxt_search) EditText searchedEditText;
    @BindView(R.id.indicator) SearchTabPageIndicator toolbarIndicator;
    @BindView(R.id.btn_clear) View clearButton;
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
    @BindView(R.id.map_with_panel) View mapAndPanel;
    @BindView(R.id.card_panel) CardView panelCard;
    @BindViews({R.id.btn_google_it, R.id.btn_map}) List<Button> actionButtons;
    // others
    @BindView((R.id.ic_row)) ImageView placeIcon;
    @BindView(R.id.wrap_place_header) ObservableSizeLayout placeHeaderWrapper;
    @BindView(R.id.img_placeholder) ImageView placeHolder;
    @BindView(R.id.scroll_container) ScrollView scrollContainer;

    private Stopwatch stopwatch;
    private Typeface doodleHeaderFont;
    private Typeface doodleDescriptionFont;

    private PublishSubject<Integer> mapWidthStream = PublishSubject.create();
    private PublishSubject<Integer> mapHeightStream = PublishSubject.create();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);

        presenter.attachView(this);
        toolbarIndicator.setVisibility(View.GONE);
        doodleHeaderFont = fontManager.get("bebas", Typeface.NORMAL);
        doodleDescriptionFont = fontManager.get("montserrat", Typeface.NORMAL);

        stopwatch = stopwatchManager.getDefaultStopwatch();
        stopwatch.reset();
        clearPreviewLayout();
        prepareOverScroll();
        loadData();

        Timber.d("Activity creation time: %s", stopwatch.getElapsedTimeString());
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

    private void prepareOverScroll() {
        float marginOffset = getResources()
                .getDimensionPixelOffset(R.dimen.Details_Height_Release_Scroll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scrollContainer.setElevation(getResources().getDimensionPixelSize(R.dimen.Details_Elevation));
        }

        ObservableVerticalOverScrollBounceEffectDecorator decorator =
                new ObservableVerticalOverScrollBounceEffectDecorator(
                        new ScrollViewOverScrollDecorAdapter(scrollContainer), 3f,
                        VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
                        -1
                );

        decorator.getReleaseEventStream()
                 .filter(scroll -> scroll != null)
                 .filter(scroll -> scroll >= marginOffset || scroll <= marginOffset * (-1))
                 .subscribe(scroll -> Timber.d("Overscrolled"));
    }

    private void loadData() {
        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        PlaceListItemType itemType = (PlaceListItemType) intent.getSerializableExtra(PARAM_ITEM_TYPE);
        SearchType searchType = SearchType.values()
                [intent.getIntExtra(PARAM_SEARCHED_TYPE, SearchType.UNKNOWN.ordinal())];
        if (placeId > 0) {
            presenter.loadPlace(placeId, searchedPlate, searchType, itemType);
        }

        mapWrapper.getBoundsStream().filter(bounds -> bounds.height() > 0)
                  .throttleLast(100, TimeUnit.MILLISECONDS).subscribe(bounds -> {
            mapWrapper.post(() -> {
                int totalHeight = mapWrapper.getHeight()
                        - mapWrapper.getPaddingBottom()
                        - mapWrapper.getPaddingTop();
                int totalWidth = mapWrapper.getWidth()
                        - mapWrapper.getPaddingLeft()
                        - mapWrapper.getPaddingRight();

                Timber.d("Map bounds computed. Height: %d, width: %d", totalHeight, totalWidth);

                float density = deviceHelper.getScreenDensity();
                mapHeightStream.onNext((int) (totalHeight / density));
                mapWidthStream.onNext((int) (totalWidth / density));
            });
        });
    }

    @Override protected void onStart() {
        super.onStart();

        AnimatorSet set = new AnimatorSet();
        set.play(animationHelper.getDetailsAnimator().createScaleAnim(panelCard))
           .with(animationHelper.getDetailsAnimator().createFadeInAnim(panelCard));
        set.start();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        // TODO: 2016-03-30 animation on back
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, false);
        startActivity(intent);
    }

    @OnClick(R.id.btn_clear) public void onClearButton() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, false);
        intent.putExtra(SearchActivity.PARAM_SEARCH_TEXT, "");
        startActivity(intent);
    }

    @OnClick(R.id.btn_google_it) public void onSearchMore() {
        presenter.searchInGoogle();
    }

    @OnClick(R.id.btn_map) public void onShowOnMap() {
        presenter.showOnMap();
    }

    @OnClick(R.id.btn_copy) public void onCopy() {
        presenter.copyToClipboard();
    }

    @OnClick(R.id.txt_searched) public void onSearchClicked() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SHOW_KEYBOARD, true);
        startActivity(intent);
    }

    @Override public void showPlaceIcon(int iconResId) {
        placeIcon.setImageResource(iconResId);
    }

    @Override public void showSearchedText(String searchedText) {
        searchedEditText.setVisibility(View.GONE);
        searchedTextView.setText(searchedText);
        if (searchedText != null && !searchedText.equals("")) {
            clearButton.setVisibility(View.VISIBLE);
        } else {
            clearButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override public void showPlaceName(String name) {
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
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(this)
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
        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(this)
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
        Toast.makeText(this, getString(R.string.details_info_copy_done), Toast.LENGTH_SHORT).show();
    }

    @Override public void startMapApp(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Timber.e("Cannot find activity to start for maps");
        }
    }

    @Override public void startWebSearch(String searchPhrase) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchPhrase);
        startActivity(intent);
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

    @Override public String getLocalizedPoland() {
        return getString(R.string.details_country);
    }

    private void setPlaceHolderImage() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int headerHeight = getActionBarSize() + getStatusBarHeight() + placeHeaderWrapper.getHeight();

        int preferredPlaceholderSize = getResources().getDimensionPixelSize(R.dimen.Details_Height_Placeholder);

        int height = metrics.heightPixels - headerHeight;
        int width = metrics.widthPixels;

        if (height > preferredPlaceholderSize) {
            height = preferredPlaceholderSize;
        }

        DoodleImage.Builder doodleBuilder = new DoodleImage.Builder(this)
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
        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
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
    //endregion
}
