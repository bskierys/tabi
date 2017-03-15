/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Adapter for items of type {@link PlaceAndPlateDto}. Some dependencies are filled by constructor, but you have to use {@link #setType(SearchType)}, {@link
 * #setPlaceClickListener(PlaceClickListener)} and {@link #setHeaderClickListener(HeaderClickListener)} before requesting any layout to avoid errors.
 */
public class SearchPlaceItemAdapter extends PlaceItemAdapter {
    private HeaderClickListener hClickListener;

    public SearchPlaceItemAdapter(Cursor cursor, Context context, RandomTextProvider randomTextProvider,
                                  PlaceAndPlateFactory itemFactory) {
        super(cursor, context, randomTextProvider, itemFactory);
    }

    public void setHeaderClickListener(HeaderClickListener hClickListener) {
        this.hClickListener = hClickListener;
    }

    @Override protected void checkAllArgumentsFilled() {
        super.checkAllArgumentsFilled();
        checkNotNull(hClickListener, "HeaderClickListener is not set");
    }

    @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                  int position, Section section) {
        checkAllArgumentsFilled();

        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        holder.header.setText(section.getTitle());
        holder.root.setClickable(section.getClintEventId() != null);

        if (section.getClintEventId() != null) {
            holder.root.setOnClickListener((v) -> hClickListener.onHeaderClicked(section.getClintEventId()));
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

    private Integer getLastSectionIndex() {
        int lastSection = -1;

        for (Integer sectionIndex : sectionIndexes) {
            if (lastSection < sectionIndex) {
                lastSection = sectionIndex;
            }
        }

        return lastSection < 0 ? null : lastSection;
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

    public interface HeaderClickListener {
        void onHeaderClicked(int eventId);
    }
}