package pl.ipebk.tabi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;

public class DetailsActivity extends BaseActivity {
    public final static String PARAM_PLACE_ID = "param_place_id";
    @Bind(R.id.place_name) TextView placeName;
    @Bind(R.id.plate) TextView plateView;
    @Bind(R.id.voivodeship) TextView voivodeship;
    @Bind(R.id.powiat) TextView powiat;
    @Bind(R.id.gmina) TextView gmina;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        long placeId = intent.getLongExtra(PARAM_PLACE_ID, 0L);
        if (placeId > 0) {
            place = databaseHelper.getPlaceDao().getById(placeId);
            prepareLayout();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void prepareLayout() {
        Plate plate = place.getPlates().get(0);
        String plateText = plate.getPattern();
        if (plate.getEnd() != null) {
            plateText += "..." + plate.getEnd();
        }
        placeName.setText(place.getName());
        plateView.setText(plateText);
        voivodeship.setText(place.getVoivodeship());
        powiat.setText(place.getPowiat());
        gmina.setText(place.getGmina());
    }
}
