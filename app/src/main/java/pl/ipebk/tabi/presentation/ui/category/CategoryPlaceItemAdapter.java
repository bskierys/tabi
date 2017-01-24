/*
* author: Bartlomiej Kierys
* date: 2017-01-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.category;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.search.PlaceItemAdapter;

/**
 * TODO: Generic description. Replace with real one.
 */
public class CategoryPlaceItemAdapter extends PlaceItemAdapter {

    public CategoryPlaceItemAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        sections.put(0, new Section("Wszystkie tablice", null));
    }

    @Override protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_place, parent, false);
        return new ItemViewHolder(view);
    }

    @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_big_header, parent, false);
        return new BigHeaderViewHolder(view);
    }

    @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                  int position, Section section) {
        BigHeaderViewHolder holder = (BigHeaderViewHolder) viewHolder;
        holder.header.setText(section.getTitle());
        holder.shadow.setVisibility(View.VISIBLE);
    }

    public static class BigHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_header) TextView header;
        @BindView(R.id.shadow) View shadow;

        public BigHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
