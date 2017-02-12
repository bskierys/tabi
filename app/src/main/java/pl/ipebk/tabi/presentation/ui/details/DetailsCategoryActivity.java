package pl.ipebk.tabi.presentation.ui.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseActivity;
import pl.ipebk.tabi.presentation.ui.custom.ObservableVerticalOverScrollBounceEffectDecorator;
import pl.ipebk.tabi.presentation.ui.search.PlaceListItemType;
import pl.ipebk.tabi.presentation.ui.search.SearchActivity;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Subscription;
import timber.log.Timber;

public class DetailsCategoryActivity extends BaseActivity {
    public final static String PARAM_PLACE_ID = "param_place_id";
    public final static String PARAM_SEARCHED_PLATE = "param_searched_plate";
    public final static String PARAM_CATEGORY_NAME = "param_category_name";
    public final static String PARAM_CATEGORY_PLATE = "param_category_plate";

    @BindView(R.id.txt_title) TextView toolbarTitle;
    @BindView(R.id.txt_plate) TextView toolbarPlate;
    @BindView(R.id.btn_back) ImageView backButton;
    @BindView(R.id.scroll_container) ScrollView scrollContainer;

    private Subscription overScrollSubscription;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        try {
            String categoryName = getIntent().getStringExtra(PARAM_CATEGORY_NAME);
            String categoryPlate = getIntent().getStringExtra(PARAM_CATEGORY_PLATE);
            if (categoryName == null || categoryPlate == null) {
                throw new NullPointerException("Category name is null");
            }
            toolbarTitle.setText(categoryName);
            toolbarPlate.setText(categoryPlate);
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not initialize CategoryActivity: category key was not passed");
        }

        Drawable backArrow = getResources().getDrawable(R.drawable.ic_back_light);
        backButton.setImageDrawable(backArrow);

        prepareOverScroll();
        loadData();
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

        overScrollSubscription = decorator.getReleaseEventStream()
                                          .filter(scroll -> scroll != null)
                                          .filter(scroll -> scroll >= marginOffset || scroll <= marginOffset * (-1))
                                          .subscribe(scroll -> Timber.d("Overscrolled"));
    }

    private void loadData() {
        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        String searchedPlate = intent.getStringExtra(PARAM_SEARCHED_PLATE);

        DetailsFragment fragment = DetailsFragment.newInstance(placeId, searchedPlate,
                                                               PlaceListItemType.SEARCH,
                                                               SearchType.LICENSE_PLATE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_container, fragment);
        ft.commit();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(overScrollSubscription);
    }

    @OnClick(R.id.btn_back) public void onBackButton() {
        onBackPressed();
    }
}
