/*
* author: Bartlomiej Kierys
* date: 2017-01-23
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.category;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.annotation.Resource;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.search.PlaceItemAdapter;
import timber.log.Timber;

/**
 * TODO: Generic description. Replace with real one.
 */
public class CategoryPlaceItemAdapter extends PlaceItemAdapter {

    private BigHeader header;

    public CategoryPlaceItemAdapter(Cursor cursor, Context context, BigHeader header) {
        super(cursor, context);
        this.header = header;
        sections.put(0, new Section("Tablice", null));
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
        holder.icon.setImageDrawable(header.icon);
        holder.title.setText(header.title);
        holder.body.setText(header.body);
        holder.moreButton.setOnClickListener(v -> Timber.d("Link to go to: %s ", header.link));
    }

    public static class BigHeader {
        private String title;
        private String body;
        private String link;
        private Drawable icon;

        public BigHeader(String title, String body, String link, Drawable icon) {
            this.title = title;
            this.body = body;
            this.link = link;
            this.icon = icon;
        }
    }

    public static class BigHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_icon) ImageView icon;
        @BindView(R.id.txt_header) TextView header;
        @BindView(R.id.shadow) View shadow;
        @BindView(R.id.txt_title) TextView title;
        @BindView(R.id.txt_body) TextView body;
        @BindView(R.id.btn_more) TextView moreButton;

        public BigHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
