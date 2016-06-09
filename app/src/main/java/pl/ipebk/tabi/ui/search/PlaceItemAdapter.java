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

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.custom.SectionedCursorRecyclerViewAdapter;
import pl.ipebk.tabi.utils.NameFormatHelper;
import rx.Observable;
import timber.log.Timber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Adapter for items of type {@link PlaceListItem}. Some dependencies are filled by constructor, but you have to use
 * {@link #setType(SearchType)}, and {@link #setEventListener(PlaceFragmentEventListener)} before requesting any layout
 * to avoid errors.
 */
public class PlaceItemAdapter extends SectionedCursorRecyclerViewAdapter {
    @Inject NameFormatHelper nameFormatHelper;

    private boolean historical;
    private Context context;
    private SearchType type;
    private PlaceFragmentEventListener eventListener;

    public PlaceItemAdapter(Cursor cursor, Context context) {
        super(cursor);
        this.context = context;
        // TODO: 2016-05-22 this way is simpler for all injections. Should be something like ViewModule to provide it
        // todo: to activities fragments and presenters
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
        int iconResourceId = historical ? R.drawable.ic_doodle_history : R.drawable.ic_doodle_search;

        // show shadow for last rows
        if (isSectionHeaderPosition(positionToSectionedPosition(position) + 1)
                || position == cursor.getCount() - 1) {
            holder.shadow.setVisibility(View.VISIBLE);
        } else {
            holder.shadow.setVisibility(View.GONE);
        }

        Observable<PlaceListItem> placeStream = Observable.just(cursor).first().map(this::cursorToItem);

        Observable<PlaceListItem> standardPlaceStream = placeStream
                .filter(p -> p.getPlaceType().ordinal() < Place.Type.SPECIAL.ordinal());
        Observable<PlaceListItem> specialPlaceStream = placeStream
                .filter(p -> p.getPlaceType() == Place.Type.SPECIAL);
        Observable<PlaceListItem> randomPlaceStream = placeStream
                .filter(p -> p.getPlaceType() == Place.Type.RANDOM);

        placeStream.doOnNext(place -> holder.root.setOnClickListener(
                v -> eventListener.onPlaceItemClicked(
                        place.getPlaceId(),
                        getPlateString(place.getPlateStart(), place.getPlateEnd()),
                        type, place.getPlaceType() == Place.Type.RANDOM ? PlaceListItemType.RANDOM :
                                (historical ? PlaceListItemType.HISTORICAL : PlaceListItemType.SEARCH))))
                   .map(p -> getPlateString(p.getPlateStart(), p.getPlateEnd()))
                   .doOnNext(t -> holder.icon.setImageResource(iconResourceId))
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

        if (type == SearchType.PLACE) {
            randomPlaceStream.doOnNext(place -> holder.placeNameView.setText(place.getPlaceName()))
                             .doOnNext(place -> holder.plateView.setText(NameFormatHelper.UNKNOWN_PLATE_CHARACTER))
                             .doOnNext(place -> holder.voivodeshipView.setText(context.getString(R.string.search_question_where)))
                             .doOnNext(place -> holder.powiatView.setText(context.getString(R.string.search_question_plates)))
                             .doOnNext(place -> holder.icon.setImageResource(R.drawable.ic_doodle_random))
                             .subscribe();
        } else if (type == SearchType.PLATE) {
            randomPlaceStream.doOnNext(place -> holder.placeNameView.setText(context.getString(R.string.search_question_what)))
                             .doOnNext(place -> holder.voivodeshipView.setText(context.getString(R.string.search_question_where)))
                             .doOnNext(place -> holder.powiatView.setText(nameFormatHelper.getRandomQuestion()))
                             .doOnNext(place -> holder.icon.setImageResource(R.drawable.ic_doodle_random))
                             .subscribe();
        }
    }

    protected PlaceListItem cursorToItem(Cursor cursor) {
        return new PlaceListItem(cursor);
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
        @Bind(R.id.root) View root;
        @Bind(R.id.txt_place_name) TextView placeNameView;
        @Bind(R.id.txt_plate) TextView plateView;
        @Bind(R.id.txt_voivodeship) TextView voivodeshipView;
        @Bind(R.id.txt_powiat) TextView powiatView;
        @Bind(R.id.shadow) ImageView shadow;
        @Bind(R.id.ic_row) ImageView icon;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
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