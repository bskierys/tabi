package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.ui.custom.CursorRecyclerViewAdapter;
import rx.Observable;
import timber.log.Timber;

public class PlaceRecyclerViewAdapter extends CursorRecyclerViewAdapter<PlaceRecyclerViewAdapter.ViewHolder> {
    private Context context;

    public PlaceRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.row_place, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        Observable<PlaceListItem> placeStream = Observable.just(cursor).map(PlaceListItem::new);

        Observable<PlaceListItem> standardPlaceStream = placeStream.filter(p -> p.getPlaceType() != Place.Type.SPECIAL);
        Observable<PlaceListItem> specialPlaceStream = placeStream.filter(p -> p.getPlaceType() == Place.Type.SPECIAL);

        placeStream.map(p -> getPlateString(p.getPlateStart(), p.getPlateEnd()))
                   .subscribe(plateText -> holder.plateView.setText(plateText));

        // TODO: 2016-02-27 should be common with details view
        standardPlaceStream.doOnNext(place -> holder.placeNameView.setText(place.getPlaceName()))
                           .doOnNext(place -> holder.voivodeshipView.setText(
                                   context.getString(R.string.details_voivodeship) + " " + place.getVoivodeship()))
                           .doOnNext(place -> holder.powiatView.setText(
                                   context.getString(R.string.details_powiat) + " " + place.getPowiat()))
                           .subscribe();

        specialPlaceStream.doOnNext(place -> holder.powiatView.setText(place.getVoivodeship()))
                          .map(place -> place.getPlaceName().split(" "))
                          .doOnNext(name -> holder.placeNameView.setText(name[0]))
                          .doOnNext(name -> holder.voivodeshipView.setText(getPlaceSubName(name)))
                          .doOnError(error -> Timber.e("Error processing special place name: %s", error))
                          .subscribe();
    }

    private String getPlateString(String plateStart, String plateEnd) {
        Plate plate = new Plate();
        plate.setPattern(plateStart);
        plate.setEnd(plateEnd);
        return plate.toString();
    }

    private String getPlaceSubName(String[] words) {
        String subName = "";
        if (words.length > 1) {
            for (int i = 1; i < words.length; i++) {
                subName += words[i] + " ";
            }
        }

        return subName;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_place_name) TextView placeNameView;
        @Bind(R.id.txt_plate) TextView plateView;
        @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
        @Bind(R.id.txt_powiat) TextView powiatView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
