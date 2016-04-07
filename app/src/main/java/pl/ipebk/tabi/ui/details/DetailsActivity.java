package pl.ipebk.tabi.ui.details;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.custom.ObservableSizeLayout;
import pl.ipebk.tabi.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.ui.search.SearchActivity;
import pl.ipebk.tabi.utils.DoodleImage;
import pl.ipebk.tabi.utils.Stopwatch;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class DetailsActivity extends BaseActivity implements DetailsMvpView, Callback {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";
    public final static String PARAM_SEARCHED_TYPE = "param_searched_type";

    @Inject DetailsPresenter presenter;
    @Inject Picasso picasso;
    // toolbar
    @Bind(R.id.txt_searched) TextView searchedTextView;
    @Bind(R.id.editTxt_search) EditText searchedEditText;
    // texts
    @Bind(R.id.txt_place_name) TextView placeNameView;
    @Bind(R.id.txt_plate) TextView plateView;
    @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
    @Bind(R.id.txt_powiat) TextView powiatView;
    @Bind(R.id.txt_gmina) TextView gminaView;
    @Bind(R.id.txt_additional) TextView additionalInfoView;
    // map and panel
    @Bind(R.id.img_map) ImageView mapView;
    @Bind(R.id.wrap_map) ObservableSizeLayout mapWrapper;
    @Bind(R.id.img_pin) ImageView pinView;
    @Bind(R.id.map_with_panel) View mapAndPanel;
    @Bind(R.id.card_panel) CardView panelCard;
    @Bind({R.id.btn_google_it, R.id.btn_voivodeship, R.id.btn_map}) List<Button> actionButtons;
    // others
    @Bind(R.id.wrap_place_header) ObservableSizeLayout placeHeaderWrapper;
    @Bind(R.id.img_placeholder) ImageView placeHolder;
    @Bind(R.id.scroll_container) ScrollView scrollContainer;

    private final Stopwatch stopwatch = new Stopwatch();

    @Override protected void onCreate(Bundle savedInstanceState) {
        stopwatch.reset();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.attachView(this);

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
                 .subscribe(scroll -> onBackPressed());
    }

    private void loadData() {
        final PublishSubject<Integer> mapWidthStream = PublishSubject.create();
        final PublishSubject<Integer> mapHeightStream = PublishSubject.create();

        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        SearchType searchType = SearchType.values()
                [intent.getIntExtra(PARAM_SEARCHED_TYPE, SearchType.UNKNOWN.ordinal())];
        if (placeId > 0) {
            presenter.loadPlace(placeId, searchedPlate, searchType,
                                mapWidthStream.asObservable(),
                                mapHeightStream.asObservable());
        }

        mapWrapper.getBoundsStream().filter(bounds -> bounds.height() > 0)
                  .first().subscribe(bounds -> {
            mapWrapper.post(() -> {
                int totalHeight = mapWrapper.getHeight()
                        - mapWrapper.getPaddingBottom() - mapWrapper.getPaddingTop();
                int totalWidth = mapWrapper.getWidth()
                        - mapWrapper.getPaddingLeft() - mapWrapper.getPaddingRight();
                mapHeightStream.onNext(totalHeight);
                mapWidthStream.onNext(totalWidth);
            });
        });
    }

    @Override protected void onStart() {
        super.onStart();

        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.panel_animation);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(panelCard);
        set.start();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        // TODO: 2016-03-30 better back behaviour
        onBackPressed();
    }

    @OnClick(R.id.btn_google_it) public void onSearchMore() {
        presenter.searchInGoogle();
    }

    @OnClick(R.id.btn_voivodeship) public void onShowMoreInVoivodeship() {
        presenter.showMoreForVoivodeship();
    }

    @OnClick(R.id.btn_map) public void onShowOnMap() {
        presenter.showOnMap();
    }

    @Override public void showSearchedText(String searchedText) {
        searchedEditText.setVisibility(View.GONE);
        searchedTextView.setText(searchedText);
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
                .spaceBeforeImage(getResources().getDimensionPixelOffset(
                        R.dimen.Details_Height_Doodle_Map_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelOffset(
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

    private void loadImageWithPicasso(Uri uri, Drawable loading, Drawable error) {
        stopwatch.reset();
        picasso.load(uri).fit().centerCrop()
               .error(error)
               .placeholder(loading)
               .into(mapView, this);
    }

    @Override public void enableActionButtons() {
        ButterKnife.apply(actionButtons, (button, index) -> button.setVisibility(View.VISIBLE));
    }

    @Override public void disableActionButtons() {
        ButterKnife.apply(actionButtons, (button, index) -> button.setVisibility(View.INVISIBLE));
    }

    @Override public void goToSearchForPhrase(String phrase) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.PARAM_SEARCH_TEXT, phrase);
        startActivity(intent);
    }

    @Override public void startMap(Uri geoLocation) {
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
                          .sample(100, TimeUnit.MILLISECONDS).subscribe(bounds -> {
            placeHeaderWrapper.post(this::setPlaceHolderImage);
        });

        placeHolder.setVisibility(View.VISIBLE);
        mapAndPanel.setVisibility(View.GONE);
        voivodeshipView.setVisibility(View.GONE);
        powiatView.setVisibility(View.GONE);
        gminaView.setVisibility(View.GONE);
        additionalInfoView.setVisibility(View.GONE);
        panelCard.setVisibility(View.GONE);
        ButterKnife.apply(actionButtons, (button, index) -> button.setVisibility(View.GONE));
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
                .spaceBeforeImage(getResources().getDimensionPixelOffset(
                        R.dimen.Details_Height_Doodle_Empty_Space_Before))
                .spaceAfterImage(getResources().getDimensionPixelOffset(
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
