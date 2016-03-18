package pl.ipebk.tabi.ui.details;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.ui.base.BaseActivity;
import pl.ipebk.tabi.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.ui.search.SearchActivity;
import pl.ipebk.tabi.utils.DoodleDrawable;
import pl.ipebk.tabi.utils.DoodleDrawableConfig;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class DetailsActivity extends BaseActivity implements DetailsMvpView, Callback {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";

    @Inject DetailsPresenter presenter;
    @Inject Picasso picasso;
    @Bind(R.id.txt_place_name) TextView placeNameView;
    @Bind(R.id.txt_plate) TextView plateView;
    @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
    @Bind(R.id.txt_powiat) TextView powiatView;
    @Bind(R.id.txt_gmina) TextView gminaView;
    @Bind(R.id.txt_additional) TextView additionalInfoView;
    @Bind(R.id.img_map) ImageView mapView;
    @Bind(R.id.img_pin) ImageView pinView;
    @Bind(R.id.img_placeholder) ImageView placeHolder;
    @Bind(R.id.wrap_map) View mapWrapper;
    @Bind(R.id.scroll_container) ScrollView scrollContainer;
    @Bind({R.id.btn_google_it, R.id.btn_voivodeship, R.id.btn_map}) List<Button> actionButtons;

    Bitmap doodle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.attachView(this);

        float marginOffset = getResources().getDimensionPixelOffset(R.dimen.Details_Height_Release_Scroll);

        ObservableVerticalOverScrollBounceEffectDecorator decorator =
                new ObservableVerticalOverScrollBounceEffectDecorator(
                        new ScrollViewOverScrollDecorAdapter(scrollContainer),
                        3f, VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK, -1
                );

        decorator.getReleaseEventStream()
                .filter(scroll -> scroll != null)
                .filter(scroll -> scroll >= marginOffset || scroll <= marginOffset * (-1))
                .subscribe(scroll -> onBackPressed());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BehaviorSubject<Integer> mapWidthStream = BehaviorSubject.create();
        BehaviorSubject<Integer> mapHeightStream = BehaviorSubject.create();

        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);
        if (placeId > 0) {
            presenter.loadPlace(placeId, searchedPlate,
                    mapWidthStream.asObservable(),
                    mapHeightStream.asObservable());
        }

        mapView.post(() -> {
            mapHeightStream.onNext(mapView.getHeight());
            mapWidthStream.onNext(mapView.getWidth());
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
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

    @Override public void showPlaceName(String name) {
        placeNameView.setText(name);
    }

    @Override public void showSearchedPlate(String plate) {
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

        DoodleDrawableConfig doodleImage = new DoodleDrawableConfig.Builder(this)
                .imageResource(R.drawable.tabi_map_loading)
                .headerText("Poczekaj chwilę")
                .descriptionText("Zaznac").build();
        //mapView.setImageBitmap(doodleImage.draw());
        //mapView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        Drawable d = new DoodleDrawable(doodleImage,this);
        picasso.load(uri).fit().centerCrop().error(R.color.red_300)
               .placeholder(d).into(mapView, this);
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
        placeHolder.setVisibility(View.VISIBLE);
        mapWrapper.setVisibility(View.GONE);
        voivodeshipView.setVisibility(View.GONE);
        powiatView.setVisibility(View.GONE);
        gminaView.setVisibility(View.GONE);
        additionalInfoView.setVisibility(View.GONE);
        ButterKnife.apply(actionButtons, (button, index) -> button.setVisibility(View.GONE));
    }

    //region Picasso callback methods
    @Override public void onSuccess() {
        pinView.setVisibility(View.VISIBLE);
    }

    @Override public void onError() {
        pinView.setVisibility(View.INVISIBLE);
    }
    //endregion
}
