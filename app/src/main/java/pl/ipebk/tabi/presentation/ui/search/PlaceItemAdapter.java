/*
* author: Bartlomiej Kierys
* date: 2017-01-28
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlate;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.custom.recycler.SectionedCursorRecyclerViewAdapter;
import pl.ipebk.tabi.presentation.ui.utils.animation.SharedTransitionNaming;
import pl.ipebk.tabi.readmodel.PlaceType;
import rx.Observable;
import timber.log.Timber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Adapter for items of type {@link PlaceAndPlateDto}.
 */
public abstract class PlaceItemAdapter extends SectionedCursorRecyclerViewAdapter {
    private RandomTextProvider randomTextProvider;
    private PlaceAndPlateFactory itemFactory;
    protected Context context;
    private boolean historical;
    private PlaceClickListener pClickListener;
    private SearchType type;

    public PlaceItemAdapter(Cursor cursor, Context context,
                            RandomTextProvider randomTextProvider,
                            PlaceAndPlateFactory itemFactory) {
        super(cursor);
        this.context = context;
        this.randomTextProvider = randomTextProvider;
        this.itemFactory = itemFactory;
    }

    public void setHistorical(boolean historical) {
        this.historical = historical;
    }

    public void setPlaceClickListener(PlaceClickListener clickListener) {
        this.pClickListener = clickListener;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    @Override protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        return new ItemViewHolder(view);
    }

    protected void checkAllArgumentsFilled() {
        checkNotNull(type, "Search type is not set");
        checkNotNull(context, "Context is not set");
        checkNotNull(pClickListener, "PlaceClickListener is not set");
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
                  .doOnNext(place -> bindCommonFieldsInViewHolder(holder, place, position))
                  .subscribe(place -> {
                      PlaceType type = place.placeType();
                      if (type.ordinal() < PlaceType.SPECIAL.ordinal()) {
                          bindStandardPlaceViewHolder(holder, place);
                      } else if (type == PlaceType.SPECIAL) {
                          bindSpecialPlaceViewHolder(holder, place);
                      } else if (type == PlaceType.RANDOM) {
                          bindRandomPlaceViewHolder(holder, place);
                      }
                  }, ex -> Timber.e(ex, "Error rendering row view"));
    }

    private void bindCommonFieldsInViewHolder(ItemViewHolder holder, PlaceAndPlate place, int position) {
        holder.root.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.rowBackground.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_row_background), position));
                holder.placeNameView.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_place_name), position));
                holder.plateView.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_place_plate), position));
                holder.icon.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_place_icon), position));
                holder.voivodeshipView.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_voivodeship_name), position));
                holder.powiatView.setTransitionName(SharedTransitionNaming.getName(context.getString(R.string.trans_powiat_name), position));
            }

            pClickListener.onPlaceItemClicked(
                    v, place.id(), place.plateString(), type,
                    place.placeType() == PlaceType.RANDOM ? PlaceListItemType.RANDOM :
                            (historical ? PlaceListItemType.HISTORICAL : PlaceListItemType.SEARCH), position);
        });

        holder.plateView.setText(place.plateString());
    }

    private void bindRandomPlaceViewHolder(ItemViewHolder holder, PlaceAndPlate place) {
        if (type == SearchType.PLACE) {
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

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root) View root;
        @BindView(R.id.txt_place_name) TextView placeNameView;
        @BindView(R.id.txt_plate) TextView plateView;
        @BindView(R.id.txt_voivodeship) TextView voivodeshipView;
        @BindView(R.id.txt_powiat) TextView powiatView;
        @BindView(R.id.shadow) ImageView shadow;
        @BindView(R.id.ic_row) ImageView icon;
        @BindView(R.id.wrp_row) LinearLayout rowBackground;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface PlaceClickListener {
        void onPlaceItemClicked(View view, AggregateId placeId, String plateClicked, SearchType type, PlaceListItemType itemType, int position);
    }
}
