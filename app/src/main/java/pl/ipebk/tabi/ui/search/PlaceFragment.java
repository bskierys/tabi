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

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;

/**
 * A fragment representing a list of Places.
 */
public class PlaceFragment extends Fragment {
    private static final String ARG_FRAGMENT_TYPE = "fragmentType";
    static final int SECTION_FIRST_POSITION = 0;
    static final int SECTION_SECOND_POSITION = 4;

    @BindView(R.id.img_no_results) ImageView noResultsImage;
    @BindView(R.id.place_list) RecyclerView recyclerView;

    protected SearchType type;
    private boolean viewCreated;
    private Cursor placeCursor;
    private PlaceItemAdapter adapter;
    private PlaceFragmentEventListener fragmentEventListener;

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

        if (getArguments() != null) {
            int typeOrdinal = getArguments().getInt(ARG_FRAGMENT_TYPE);
            type = SearchType.values()[typeOrdinal];
        }

        getAdapter();
        viewCreated = false;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);
        ButterKnife.bind(this, view);

        if (placeCursor != null) {
            getAdapter().changeCursor(placeCursor);
        }

        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(adapter);

        hideNoResultsImage();
        hideList();

        fragmentEventListener.onFragmentViewCreated(type);
        viewCreated = true;

        return view;
    }

    /**
     * Test methods to be replaced with mocks
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * Test methods to be replaced with mocks
     */
    public PlaceItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new PlaceItemAdapter(placeCursor, getActivity());
            adapter.setEventListener(fragmentEventListener);
            adapter.setType(type);
        }

        return adapter;
    }

    public boolean isViewCreated() {
        return viewCreated;
    }

    public void setData(Cursor placeCursor) {
        this.placeCursor = placeCursor;
        getAdapter().changeCursor(placeCursor);
    }

    public void showInitialHeaders() {
        getAdapter().addSection(SECTION_FIRST_POSITION, getString(R.string.search_header_suggestions), null);
        getAdapter().removeSection(SECTION_SECOND_POSITION);
        getAdapter().setHistorical(true);
    }

    public void showQuickHeaders(int outcomeCount) {
        getAdapter().setHistorical(false);
        getAdapter().addSection(SECTION_FIRST_POSITION, getString(R.string.search_header_best), null);
        if (outcomeCount >= SearchPresenter.SEARCH_QUANTITY_QUICK) {
            getAdapter().addSection(SECTION_SECOND_POSITION, getString(R.string.search_header_click),
                                    SearchActivity.EVENT_ID_HEADER_ALL);
        } else {
            getAdapter().removeSection(SECTION_SECOND_POSITION);
        }
    }

    public void showFullHeaders(int outcomeCount) {
        getAdapter().setHistorical(false);
        getAdapter().addSection(SECTION_FIRST_POSITION, getString(R.string.search_header_best), null);
        if (outcomeCount > SearchPresenter.SEARCH_QUANTITY_QUICK) {
            getAdapter().addSection(SECTION_SECOND_POSITION, getString(R.string.search_header_all), null);
        } else {
            getAdapter().removeSection(SECTION_SECOND_POSITION);
        }
    }

    private void onHeaderClicked(int eventId) {
        fragmentEventListener.onHeaderClicked(eventId);
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
        if (context instanceof PlaceFragmentEventListener) {
            fragmentEventListener = (PlaceFragmentEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement PlaceFragmentEventListener");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        fragmentEventListener = null;
    }
}
