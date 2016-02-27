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
import pl.ipebk.tabi.ui.custom.CursorRecyclerViewAdapter;

public class PlaceRecyclerViewAdapter extends CursorRecyclerViewAdapter<PlaceRecyclerViewAdapter.ViewHolder> {
    public PlaceRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_place_list, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        // TODO: 2016-02-27 should be common with details view
        PlaceListItem place = new PlaceListItem(cursor);
        String plateText = place.getPlateStart();
        if (place.getPlateEnd() != null) {
            plateText += "..." + place.getPlateEnd();
        }

        holder.placeNameView.setText(place.getPlaceName());
        holder.plateView.setText(plateText);
        holder.voivodeshipView.setText(place.getVoivodeship());
        holder.powiatView.setText(place.getPowiat());
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
