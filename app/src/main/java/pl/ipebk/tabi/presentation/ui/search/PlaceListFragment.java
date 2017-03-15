package pl.ipebk.tabi.presentation.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.BaseFragment;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;
import pl.ipebk.tabi.utils.RxUtil;
import rx.Subscription;
import timber.log.Timber;

/**
 * A fragment representing a list of Places.
 */
public class PlaceListFragment extends BaseFragment {
    private static final String ARG_FRAGMENT_TYPE = "fragmentType";
    static final int SECTION_FIRST_POSITION = 0;
    static final int SECTION_SECOND_POSITION = 4;

    @Inject RandomTextProvider randomTextProvider;
    @Inject PlaceAndPlateFactory placeFactory;
    @Inject AnimationCreator animationCreator;
    @BindView(R.id.img_no_results) ImageView noResultsImage;
    @BindView(R.id.place_list) RecyclerView recyclerView;

    protected SearchType type;
    private boolean viewCreated;
    private Cursor placeCursor;
    private SearchPlaceItemAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private PlaceFragmentEventListener fragmentEventListener;

    @SuppressWarnings("unused")
    public static PlaceListFragment newInstance(SearchType type) {
        PlaceListFragment fragment = new PlaceListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);

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

        manager = getLayoutManager();
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        hideNoResultsImage();
        hideList();

        fragmentEventListener.onFragmentViewCreated(type);
        viewCreated = true;

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        adapter.unlockRowClicks();
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
    public SearchPlaceItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new SearchPlaceItemAdapter(placeCursor, getActivity(), randomTextProvider, placeFactory);
            adapter.setHeaderClickListener(s -> fragmentEventListener.onHeaderClicked(s));
            adapter.setPlaceClickListener((v, id, plate, sType, pType, pos) -> {
                fragmentEventListener.onPlaceItemClicked(v, id, plate, sType, pType, pos);
            });
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
