package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.custom.SectionedCursorRecyclerViewAdapter;
import pl.ipebk.tabi.utils.NameFormatHelper;
import rx.Observable;
import timber.log.Timber;

/**
 * A fragment representing a list of Places.
 */
public class PlaceFragment extends Fragment {
    private static final String ARG_FRAGMENT_TYPE = "fragmentType";
    private static final int SECTION_FIRST_POSITION = 0;
    private static final int SECTION_SECOND_POSITION = 4;

    @Bind(R.id.img_no_results) ImageView noResultsImage;
    @Bind(R.id.place_list) RecyclerView recyclerView;

    private SearchType type;
    private boolean viewCreated;
    private Cursor placeCursor;
    private SectionedCursorRecyclerViewAdapter adapter;
    private onPlaceClickedListener placeClickedListener;
    private NameFormatHelper nameFormatHelper;

    @SuppressWarnings("unused")
    public static PlaceFragment newInstance(SearchType type) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlaceItemAdapter(placeCursor);
        viewCreated = false;

        if (getArguments() != null) {
            int typeOrdinal = getArguments().getInt(ARG_FRAGMENT_TYPE);
            type = SearchType.values()[typeOrdinal];
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);
        ButterKnife.bind(this, view);
        nameFormatHelper = new NameFormatHelper(getActivity());

        if (placeCursor != null) {
            adapter.changeCursor(placeCursor);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        hideNoResultsImage();
        hideList();

        placeClickedListener.onFragmentViewCreated(type);
        viewCreated = true;

        return view;
    }

    public boolean isViewCreated() {
        return viewCreated;
    }

    public void setData(Cursor placeCursor) {
        this.placeCursor = placeCursor;

        if (adapter != null) {
            this.adapter.changeCursor(placeCursor);
        }
    }

    public void showQuickHeaders() {
        adapter.addSection(SECTION_FIRST_POSITION, getString(R.string.search_header_best), null);
        adapter.addSection(SECTION_SECOND_POSITION, getString(R.string.search_header_click),
                           SearchActivity.EVENT_ID_HEADER_ALL);
    }

    public void showFullHeaders(int outcomeCount) {
        if (outcomeCount > SearchPresenter.SEARCH_QUANTITY_QUICK) {
            adapter.addSection(SECTION_SECOND_POSITION, getString(R.string.search_header_all), null);
        }else {
            adapter.removeSection(SECTION_SECOND_POSITION);
        }
    }

    private void onHeaderClicked(int eventId) {
        placeClickedListener.onHeaderClicked(eventId);
    }

    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void hideList() {
        recyclerView.setVisibility(View.GONE);
    }

    public void showNoResultsImage(Bitmap doodle) {
        noResultsImage.setImageBitmap(doodle);
        noResultsImage.setVisibility(View.VISIBLE);
    }

    public void hideNoResultsImage() {
        noResultsImage.setImageBitmap(null);
        noResultsImage.setVisibility(View.INVISIBLE);
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onPlaceClickedListener) {
            placeClickedListener = (onPlaceClickedListener) context;
        } else {
            throw new RuntimeException(context + " must implement onPlaceClickedListener");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        placeClickedListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this
     * fragment to be communicated to the activity and potentially other fragments contained in that activity.
     */
    public interface onPlaceClickedListener {
        void onPlaceClicked(long placeId, SearchType type);

        void onHeaderClicked(int eventId);

        void onFragmentViewCreated(SearchType type);
    }

    public class PlaceItemAdapter extends SectionedCursorRecyclerViewAdapter {
        public PlaceItemAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
            return new ItemViewHolder(view);
        }

        @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place_header, parent, false);
            return new HeaderViewHolder(view);
        }

        @Override protected void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor, int position) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;

            // show shadow for last rows
            if (isSectionHeaderPosition(positionToSectionedPosition(position) + 1)
                    || position == cursor.getCount() - 1) {
                holder.shadow.setVisibility(View.VISIBLE);
            } else {
                holder.shadow.setVisibility(View.GONE);
            }

            Observable<PlaceListItem> placeStream = Observable.just(cursor).map(PlaceListItem::new);

            Observable<PlaceListItem> standardPlaceStream = placeStream
                    .filter(p -> p.getPlaceType() != Place.Type.SPECIAL);
            Observable<PlaceListItem> specialPlaceStream = placeStream
                    .filter(p -> p.getPlaceType() == Place.Type.SPECIAL);

            placeStream.doOnNext(place -> holder.root.setOnClickListener(
                    v -> placeClickedListener.onPlaceClicked(place.getPlaceId(), type)))
                       .map(p -> getPlateString(p.getPlateStart(), p.getPlateEnd()))
                       .subscribe(plateText -> holder.plateView.setText(plateText));

            standardPlaceStream.doOnNext(place -> holder.placeNameView.setText(place.getPlaceName()))
                               .doOnNext(place -> holder.voivodeshipView.setText(
                                       nameFormatHelper.formatVoivodeship(place.getVoivodeship())))
                               .doOnNext(place -> holder.powiatView.setText(
                                       nameFormatHelper.formatPowiat(place.getPowiat())))
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

        @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                      int position, Section section) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.header.setText(section.getTitle());
            holder.root.setClickable(section.getClintEventId() != null);

            if (section.getClintEventId() != null) {
                holder.root.setOnClickListener((v) -> onHeaderClicked(section.getClintEventId()));
            }

            // if section is last - add divider
            if (position == getItemCount() - 1) {
                holder.divider.setVisibility(View.VISIBLE);
            } else {
                holder.divider.setVisibility(View.GONE);
            }

            // if there are rows above - hide shadow
            if (position == SECTION_SECOND_POSITION) {
                holder.shadow.setVisibility(View.GONE);
            } else {
                holder.shadow.setVisibility(View.VISIBLE);
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.root) View root;
            @Bind(R.id.txt_place_name) TextView placeNameView;
            @Bind(R.id.txt_plate) TextView plateView;
            @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
            @Bind(R.id.txt_powiat) TextView powiatView;
            @Bind(R.id.shadow) ImageView shadow;

            public ItemViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.root) View root;
            @Bind(R.id.txt_header) TextView header;
            @Bind(R.id.shadow) View shadow;
            @Bind(R.id.divider) ImageView divider;

            public HeaderViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
