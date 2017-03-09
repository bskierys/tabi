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
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateFactory;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.search.PlaceItemAdapter;
import pl.ipebk.tabi.presentation.ui.search.RandomTextProvider;
import pl.ipebk.tabi.presentation.ui.utils.animation.AnimationCreator;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Adapter for items of type {@link PlaceAndPlateDto}. Some dependencies are filled by constructor, but you have to use {@link #setType(SearchType)}, {@link
 * #setPlaceClickListener(PlaceClickListener)} and {@link #setMoreInfoClickListener(MoreInfoClickListener)} before requesting any layout to avoid errors.
 */
public class CategoryPlaceItemAdapter extends PlaceItemAdapter {
    private static final int SECTION_HEADER_INDEX = 0;

    private CategoryInfo categoryInfo;
    private static CategoryInfo DEFAULT_INFO;
    private MoreInfoClickListener mClickListener;
    private String platesSectionName;
    private AnimationCreator animCreator;
    private int lastPosition = -1;

    public CategoryPlaceItemAdapter(Cursor cursor, Context context,
                                    RandomTextProvider randomTextProvider,
                                    PlaceAndPlateFactory itemFactory) {
        super(cursor, context, randomTextProvider, itemFactory);
        platesSectionName = context.getString(R.string.category_plates_section);
        String noText = context.getString(R.string.default_resource_string);
        // TODO: 2017-02-26 pass injected
        animCreator = new AnimationCreator(context);
        DEFAULT_INFO = new AutoValue_CategoryInfo(noText, noText, noText, context.getResources().getDrawable(R.drawable.vic_default));
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

    @Override public void changeCursor(Cursor newCursor) {
        super.changeCursor(newCursor);
        addSection(SECTION_HEADER_INDEX, platesSectionName, null);
    }

    @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder,
                                                  int position, Section section) {
        checkAllArgumentsFilled();
        if (categoryInfo == null) {
            categoryInfo = DEFAULT_INFO;
        }

        BigHeaderViewHolder holder = (BigHeaderViewHolder) viewHolder;
        holder.header.setText(section.getTitle());
        holder.shadow.setVisibility(View.VISIBLE);
        holder.icon.setImageDrawable(categoryInfo.icon());
        holder.title.setText(categoryInfo.title());
        holder.body.setText(categoryInfo.body());
        holder.moreButton.setOnClickListener(v -> mClickListener.onMoreInfoClick(categoryInfo.link()));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        setAnimation(viewHolder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            AnimationCreator.CategoryAnimator creator = animCreator.getCategoryAnimator();
            Animation animation = creator.createItemEnterAnim(position);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
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

    public interface MoreInfoClickListener {
        void onMoreInfoClick(String url);
    }
}
