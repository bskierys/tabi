package pl.ipebk.tabi.ui.adapters;

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
import pl.ipebk.tabi.database.daos.PlateDao;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.tables.PlacesTable;
import pl.ipebk.tabi.ui.fragments.PlaceFragment;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PlaceRecyclerViewAdapter extends CursorRecyclerViewAdapter<PlaceRecyclerViewAdapter.ViewHolder> {

    PlacesTable placesTable;
    private PlaceFragment.OnListFragmentInteractionListener mListener;

    public PlaceRecyclerViewAdapter(Context context, Cursor cursor, PlateDao plateDao,
                                    PlaceFragment.OnListFragmentInteractionListener listener) {
        super(context, cursor);
        mListener = listener;
        placesTable = new PlacesTable();
        placesTable.setPlateDao(plateDao);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        Place place = placesTable.cursorToModel(cursor);
        Plate plate = place.getPlates().get(0);
        String plateText = plate.getPattern();
        if (plate.getEnd() != null) {
            plateText += "..." + plate.getEnd();
        }

        holder.place = place;
        holder.placeName.setText(place.getName());
        holder.plate.setText(plateText);
        holder.voivodeship.setText(place.getVoivodeship());
        holder.powiat.setText(place.getPowiat());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.place);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @Bind(R.id.place_name) TextView placeName;
        @Bind(R.id.plate) TextView plate;
        @Bind(R.id.voivodeship) TextView voivodeship;
        @Bind(R.id.powiat) TextView powiat;
        Place place;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
