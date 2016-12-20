/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlate;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.ui.custom.SectionedCursorRecyclerViewAdapter;
import rx.Observable;
import timber.log.Timber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Adapter for items of type {@link PlaceAndPlateDto}. Some dependencies are filled by constructor, but you have to use
 * {@link #setType(SearchType)}, and {@link #setEventListener(PlaceFragmentEventListener)} before requesting any layout
 * to avoid errors.
 */
public class PlaceItemAdapter extends SectionedCursorRecyclerViewAdapter {
    @Inject RandomTextProvider randomTextProvider;
    @Inject PlaceAndPlateFactory itemFactory;

    private boolean historical;
    private Context context;
    private SearchType type;
    private PlaceFragmentEventListener eventListener;

    public PlaceItemAdapter(Cursor cursor, Context context) {
        super(cursor);
        this.context = context;
        App.get(context).getViewComponent().inject(this);
    }

    public void setHistorical(boolean historical) {
        this.historical = historical;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public void setEventListener(PlaceFragmentEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private void checkAllArgumentsFilled() {
        checkNotNull(type, "Search type is not set");
        checkNotNull(context, "Context is not set");
        checkNotNull(eventListener, "PlaceFragmentEventListener is not set");
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
        checkAllArgumentsFilled();

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        // show shadow for last rows
        if (isSectionHeaderPosition(positionToSectionedPosition(position) + 1)
                || position == cursor.getCount() - 1) {
            holder.shadow.setVisibility(View.VISIBLE);
        } else {
            holder.shadow.setVisibility(View.GONE);
        }

        Observable.just(cursor).first().map(this::cursorToItem)
                .doOnNext(place -> bindCommonFieldsInViewHolder(holder, place))
                .subscribe(place -> {
                    PlaceType type = place.placeType();
                    if(type.ordinal() < PlaceType.SPECIAL.ordinal()){
                        bindStandardPlaceViewHolder(holder, place);
                    } else if(type == PlaceType.SPECIAL) {
                        bindSpecialPlaceViewHolder(holder, place);
                    } else if(type == PlaceType.RANDOM) {
                        bindRandomPlaceViewHolder(holder, place);
                    }
                }, ex -> Timber.e(ex, "Error rendering row view"));
    }

    private void bindCommonFieldsInViewHolder(ItemViewHolder holder, PlaceAndPlate place) {
        holder.root.setOnClickListener(v -> eventListener.onPlaceItemClicked(
                        place.id(),
                        place.plateString(),
                        type, place.placeType() == PlaceType.RANDOM ? PlaceListItemType.RANDOM :
                                (historical ? PlaceListItemType.HISTORICAL : PlaceListItemType.SEARCH)));

        holder.plateView.setText(place.plateString());
    }

    private void bindRandomPlaceViewHolder(ItemViewHolder holder, PlaceAndPlate place) {
        if(type == SearchType.PLACE){
            holder.placeNameView.setText(place.name());
            holder.plateView.setText(randomTextProvider.getUnknownPlatePlaceholder());
            holder.voivodeshipView.setText(context.getString(R.string.search_question_where));
            holder.powiatView.setText(context.getString(R.string.search_question_plates));
            holder.icon.setImageResource(R.drawable.ic_doodle_random);
        } else if (type == SearchType.LICENSE_PLATE) {
            holder.placeNameView.setText(context.getString(R.string.search_question_what));
            holder.voivodeshipView.setText(context.getString(R.string.search_question_where));
            holder.powiatView.setText(randomTextProvider.getRandomQuestion());
            holder.icon.setImageResource(R.drawable.ic_doodle_random);
        }
    }

    private void bindSpecialPlaceViewHolder(ItemViewHolder holder, PlaceAndPlate place) {
        int iconResourceId = historical ? R.drawable.ic_doodle_history : R.drawable.ic_doodle_search;
        String[] nameParts = place.name().split(" ");

        holder.powiatView.setText(place.voivodeship());
        holder.placeNameView.setText(nameParts[0]);
        holder.voivodeshipView.setText(getPlaceSubName(nameParts));
        holder.icon.setImageResource(iconResourceId);
    }

    private void bindStandardPlaceViewHolder(ItemViewHolder holder, PlaceAndPlate place) {
        int iconResourceId = historical ? R.drawable.ic_doodle_history : R.drawable.ic_doodle_search;

        holder.placeNameView.setText(place.name());
        holder.voivodeshipView.setText(place.voivodeship());
        holder.powiatView.setText(place.powiat());
        holder.icon.setImageResource(iconResourceId);
    }

    protected PlaceAndPlate cursorToItem(Cursor cursor) {
        return itemFactory.createFromCursor(cursor);
    }

    private String getPlaceSubName(String[] words) {
        String subName = "";
        if (words.length > 1) {
            for (int i = 1; i < words.length; i++) {
                subName += words[i] + " ";
            }
        }

        return subName.trim();
    }

    private Integer getLastSectionIndex() {
        int lastSection = -1;

        for (Integer sectionIndex : sectionIndexes) {
            if (lastSection < sectionIndex) {
                lastSection = sectionIndex;
            }
        }

        return lastSection < 0 ? null : lastSection;
    }

    @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                  int position, Section section) {
        checkAllArgumentsFilled();

        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        holder.header.setText(section.getTitle());
        holder.root.setClickable(section.getClintEventId() != null);

        if (section.getClintEventId() != null) {
            holder.root.setOnClickListener((v) -> eventListener.onHeaderClicked(section.getClintEventId()));
        }

        // if section is last - add divider
        if (position == getItemCount() - 1) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }

        // if there are rows above - hide shadow
        Integer lastSectionIndex = getLastSectionIndex();
        if (lastSectionIndex != null && position == lastSectionIndex) {
            holder.shadow.setVisibility(View.GONE);
        } else {
            holder.shadow.setVisibility(View.VISIBLE);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root) View root;
        @BindView(R.id.txt_place_name) TextView placeNameView;
        @BindView(R.id.txt_plate) TextView plateView;
        @BindView(R.id.txt_voivodeship) TextView voivodeshipView;
        @BindView(R.id.txt_powiat) TextView powiatView;
        @BindView(R.id.shadow) ImageView shadow;
        @BindView(R.id.ic_row) ImageView icon;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root) View root;
        @BindView(R.id.txt_header) TextView header;
        @BindView(R.id.shadow) View shadow;
        @BindView(R.id.divider) ImageView divider;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}