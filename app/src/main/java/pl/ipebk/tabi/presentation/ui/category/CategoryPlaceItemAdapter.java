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
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.ui.search.PlaceItemAdapter;
import pl.ipebk.tabi.presentation.ui.search.RandomTextProvider;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * TODO: Generic description. Replace with real one.
 */
public class CategoryPlaceItemAdapter extends PlaceItemAdapter {

    private CategoryInfo categoryInfo;
    private CategoryInfo defaultInfo;
    private MoreInfoClickListener mClickListener;

    public CategoryPlaceItemAdapter(Cursor cursor, Context context,
                                    RandomTextProvider randomTextProvider,
                                    PlaceAndPlateFactory itemFactory) {
        super(cursor, context, randomTextProvider, itemFactory);
        sections.put(0, new Section("Tablice", null));
        // TODO: 2017-01-28 another way of constructing default
        defaultInfo = new AutoValue_CategoryInfo("title", "body", "link", context.getResources().getDrawable(R.drawable.vic_default));
    }

    public void setCategoryInfo(CategoryInfo categoryInfo) {
        this.categoryInfo = categoryInfo;
        notifyItemChanged(0);
    }

    @Override protected void checkAllArgumentsFilled() {
        super.checkAllArgumentsFilled();
        checkNotNull(mClickListener, "MoreInfoClickListener is not set");
    }

    @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_big_header, parent, false);
        return new BigHeaderViewHolder(view);
    }

    public void setMoreInfoClickListener(MoreInfoClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                  int position, Section section) {
        checkAllArgumentsFilled();
        if (categoryInfo == null) {
            categoryInfo = defaultInfo;
        }

        BigHeaderViewHolder holder = (BigHeaderViewHolder) viewHolder;
        holder.header.setText(section.getTitle());
        holder.shadow.setVisibility(View.VISIBLE);
        holder.icon.setImageDrawable(categoryInfo.icon());
        holder.title.setText(categoryInfo.title());
        holder.body.setText(categoryInfo.body());
        holder.moreButton.setOnClickListener(v -> mClickListener.onMoreInfoClick(categoryInfo.link()));
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

    public interface MoreInfoClickListener {
        void onMoreInfoClick(String url);
    }
}
