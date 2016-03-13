package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.ui.custom.RecyclerItemClickListener;

/**
 * A fragment representing a list of Places.
 */
public class PlaceFragment extends Fragment {
    private static final String ARG_FRAGMENT_TYPE = "fragmentType";

    @Bind(R.id.txt_prompt) TextView promptView;
    @Bind(R.id.progress_search) ProgressBar searchProgress;
    @Bind(R.id.place_list) RecyclerView recyclerView;

    private SearchHistory.SearchType type;
    private boolean viewCreated;
    private Cursor placeCursor;
    private PlaceRecyclerViewAdapter adapter;
    private onPlaceClickedListener placeClickedListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceFragment() {
    }

    @SuppressWarnings("unused")
    public static PlaceFragment newInstance(SearchHistory.SearchType type) {
        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_TYPE, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlaceRecyclerViewAdapter(getActivity(), placeCursor);
        viewCreated = false;

        if (getArguments() != null) {
            int typeOrdinal = getArguments().getInt(ARG_FRAGMENT_TYPE);
            type = SearchHistory.SearchType.values()[typeOrdinal];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);
        ButterKnife.bind(this, view);

        if (placeCursor != null) {
            adapter.changeCursor(placeCursor);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                (v, position) -> placeClickedListener.onPlaceClicked(adapter.getItemId(position), type)));

        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        hideProgress();
        hideText();
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

    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void hideList() {
        recyclerView.setVisibility(View.GONE);
    }

    public void showText(String text) {
        promptView.setVisibility(View.VISIBLE);
        promptView.setText(text);
    }

    public void hideText() {
        promptView.setText("");
        promptView.setVisibility(View.INVISIBLE);
    }

    public void showProgress() {
        searchProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        searchProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onPlaceClickedListener) {
            placeClickedListener = (onPlaceClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onPlaceClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        placeClickedListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface onPlaceClickedListener {
        void onPlaceClicked(long placeId, SearchHistory.SearchType type);

        void onFragmentViewCreated(SearchHistory.SearchType type);
    }
}
