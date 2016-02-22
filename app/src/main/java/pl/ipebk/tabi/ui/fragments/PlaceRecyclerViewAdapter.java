package pl.ipebk.tabi.ui.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.ui.fragments.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link PlaceFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter<PlaceRecyclerViewAdapter.ViewHolder> {
    private List<Place> mValues;
    private PlaceFragment.OnListFragmentInteractionListener mListener;

    public PlaceRecyclerViewAdapter(List<Place> items, PlaceFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void swapItems(List<Place> places){
        if(mValues==null){
            mValues = new ArrayList<>();
        }else {
            this.mValues.clear();
        }

        this.mValues = places;
        notifyDataSetChanged();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        Place place = mValues.get(position);
        Plate plate = place.getPlates().get(0);
        String plateText = plate.getPattern();
        if(plate.getEnd()!=null){
            plateText += "&#8230;" + plate.getEnd();
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
                    //mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return mValues.size();
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
            ButterKnife.bind(this,view);
        }
    }
}
