package pl.ipebk.tabi.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.ui.base.BaseActivity;

public class DetailsActivity extends BaseActivity implements DetailsMvpView {
    public final static String PARAM_PLACE_ID = "param_place_id";

    @Inject DetailsPresenter presenter;
    @Bind(R.id.txt_place_name) TextView placeNameView;
    @Bind(R.id.txt_plate) TextView plateView;
    @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
    @Bind(R.id.txt_powiat) TextView powiatView;
    @Bind(R.id.txt_gmina) TextView gminaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.attachView(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        if (placeId > 0) {
            presenter.loadPlace(placeId);
        }
    }

    @Override public void showPlace(Place place) {
        // TODO: 2016-02-27 same method as in search rows
        Plate plate = place.getPlates().get(0);
        String plateText = plate.getPattern();
        if (plate.getEnd() != null) {
            plateText += "..." + plate.getEnd();
        }
        placeNameView.setText(place.getName());
        plateView.setText(plateText);
        voivodeshipView.setText(place.getVoivodeship());
        powiatView.setText(place.getPowiat());
        gminaView.setText(place.getGmina());
    }
}
