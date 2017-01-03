/*
* author: Bartlomiej Kierys
* date: 2016-05-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.App;
import pl.ipebk.tabi.BuildConfig;
import pl.ipebk.tabi.R;
import timber.log.Timber;

public class MainItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NONE = -1;
    private static final int TYPE_BIG_HEADER = 0;
    private static final int TYPE_SMALL_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 3;

    @Inject MainScreenResourceFinder resourceHelper;
    @Inject DoodleTextFormatter doodleTextFormatter;
    private List<MainListItem> categoryList;
    private Context context;
    private final MenuItemClickListener listener;
    private String caption;

    public MainItemAdapter(List<MainListItem> categoryList, Context context, @NonNull MenuItemClickListener listener) {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;
        App.get(context).getViewComponent().inject(this);
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_BIG_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_big_header, parent, false);
            return new BigHeaderViewHolder(view);
        } else if (viewType == TYPE_SMALL_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header, parent, false);
            return new SmallHeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_footer, parent, false);
            return new FooterViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type "
                                           + viewType + " + make sure your using types correctly");
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            MainListElementItem item = (MainListElementItem) categoryList.get(position - 1);

            itemViewHolder.rootView.setOnClickListener(v -> listener.onMenuItemClicked(item.getActionKey()));
            String categoryName;
            try{
                categoryName = resourceHelper.getStringResourceForKey(item.getTitleResourceKey());
            } catch (Resources.NotFoundException e){
                Timber.e("Failed to find category name for name: %s", item.getTitleResourceKey());
                categoryName = context.getString(R.string.default_resource_string);
            }
            itemViewHolder.categoryName.setText(categoryName);

            Drawable categoryIcon;
            try{
                categoryIcon = resourceHelper.getDrawableResourceForKey(item.getImageResourceKey());
            } catch (Resources.NotFoundException e){
                Timber.e("Failed to find category drawable for name: %s", item.getTitleResourceKey());
                categoryIcon = context.getResources().getDrawable(R.drawable.default_resource_drawable);
            }
            itemViewHolder.categoryIcon.setImageDrawable(categoryIcon);
        } else if (holder instanceof BigHeaderViewHolder) {
            // TODO: 2016-05-31 same holder names
            BigHeaderViewHolder headerViewHolder = (BigHeaderViewHolder) holder;

            // TODO: 2016-06-07 caption should be generic or depends on sharedPrefs
            String captionToSet;
            if (caption == null || caption.equals("")) {
                captionToSet = context.getString(R.string.main_doodle_caption);
            } else {
                captionToSet = caption;
            }

            headerViewHolder.caption.setText(doodleTextFormatter.formatDoodleCaption(captionToSet),
                                             TextView.BufferType.SPANNABLE);
            headerViewHolder.greeting.setText(doodleTextFormatter.formatDoodleGreeting());
        } else if (holder instanceof SmallHeaderViewHolder) {
            SmallHeaderViewHolder headerViewHolder = (SmallHeaderViewHolder) holder;
            MainListHeaderItem item = (MainListHeaderItem) categoryList.get(position - 1);

            String headerName;
            try{
                headerName = resourceHelper.getStringResourceForKey(item.getTitleResourceKey());
            } catch (Resources.NotFoundException e){
                Timber.e("Failed to find header for name: %s", item.getTitleResourceKey());
                headerName = context.getString(R.string.default_resource_string);
            }
            headerViewHolder.header.setText(headerName);
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            // TODO: 2017-01-01 should be provided by presenter - same with big header
            String versionName = context.getString(R.string.main_version, BuildConfig.VERSION_NAME);
            footerViewHolder.version.setText(versionName);
        }
    }

    @Override public int getItemCount() {
        return categoryList.size() + 2;
    }

    @Override public int getItemViewType(int position) {
        if (isSectionHeaderPosition(position)) {
            return TYPE_BIG_HEADER;
        } else if(isSectionFooterPosition(position)) {
            return TYPE_FOOTER;
        } else {
            MainListItem item = categoryList.get(position - 1);
            if (item instanceof MainListElementItem) {
                return TYPE_ITEM;
            } else if (item instanceof MainListHeaderItem) {
                return TYPE_SMALL_HEADER;
            }
        }

        return TYPE_NONE;
    }

    public void setCaption(String caption) {
        this.caption = caption;
        notifyItemChanged(getBigHeaderPosition());
    }

    public void swapItems(List<MainListItem> items) {
        this.categoryList.clear();
        this.categoryList = items;

        notifyDataSetChanged();
    }

    // TODO: 2017-01-01 simple helper
    public static int getBigHeaderPosition() {
        return 0;
    }

    public static int getFooterPosition(List<MainListItem> items) {
        return items.size() + 1;
    }

    protected boolean isSectionHeaderPosition(int position) {
        return position == getBigHeaderPosition();
    }

    protected boolean isSectionFooterPosition(int position) {
        return position == getFooterPosition(categoryList);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @BindView(R.id.txt_category) TextView categoryName;
        @BindView(R.id.ic_category) ImageView categoryIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    public static class SmallHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_header) TextView header;

        public SmallHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class BigHeaderViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @BindView(R.id.txt_caption) TextView caption;
        @BindView(R.id.txt_greeting) TextView greeting;

        public BigHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_version) TextView version;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface MenuItemClickListener {
        void onMenuItemClicked(String action);
    }
}
