/*
* author: Bartlomiej Kierys
* date: 2016-05-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;

class MainItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NONE = -1;
    private static final int TYPE_BIG_HEADER = 0;
    private static final int TYPE_SMALL_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 3;

    private DoodleTextFormatter doodleTextFormatter;
    private List<MainListItem> categoryList;
    private final MenuItemClickListener listener;

    MainItemAdapter(List<MainListItem> categoryList, DoodleTextFormatter doodleTextFormatter,
                    @NonNull MenuItemClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
        this.doodleTextFormatter = doodleTextFormatter;
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
            MainListElementItem item = (MainListElementItem) categoryList.get(position);

            itemViewHolder.rootView.setOnClickListener(v -> listener.onMenuItemClicked(item));
            itemViewHolder.categoryName.setText(item.getElementName());
            itemViewHolder.categoryIcon.setImageDrawable(item.getElementIcon());
        } else if (holder instanceof BigHeaderViewHolder) {
            BigHeaderViewHolder headerViewHolder = (BigHeaderViewHolder) holder;
            MainListBigHeaderItem item = (MainListBigHeaderItem) categoryList.get(position);

            headerViewHolder.caption.setText(doodleTextFormatter.formatDoodleCaption(item.getCaption()),
                                             TextView.BufferType.SPANNABLE);
            headerViewHolder.greeting.setText(item.getGreeting());
        } else if (holder instanceof SmallHeaderViewHolder) {
            SmallHeaderViewHolder headerViewHolder = (SmallHeaderViewHolder) holder;
            MainListHeaderItem item = (MainListHeaderItem) categoryList.get(position);
            headerViewHolder.header.setText(item.getHeaderText());
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            MainListFooterItem item = (MainListFooterItem) categoryList.get(position);

            footerViewHolder.version.setText(item.getVersionName());
        }
    }

    void refreshItem(MainListItem item, int index) {
        categoryList.set(index, item);
        notifyItemChanged(index);
    }

    @Override public int getItemCount() {
        return categoryList.size();
    }

    @Override public int getItemViewType(int position) {
        MainListItem item = categoryList.get(position);
        if (item instanceof MainListBigHeaderItem) {
            return TYPE_BIG_HEADER;
        } else if (item instanceof MainListHeaderItem) {
            return TYPE_SMALL_HEADER;
        } else if (item instanceof MainListElementItem) {
            return TYPE_ITEM;
        } else if (item instanceof MainListFooterItem) {
            return TYPE_FOOTER;
        }

        return TYPE_NONE;
    }

    void swapItems(List<MainListItem> items) {
        this.categoryList.clear();
        this.categoryList = items;

        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @BindView(R.id.txt_category) TextView categoryName;
        @BindView(R.id.ic_category) ImageView categoryIcon;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    static class SmallHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_header) TextView header;

        SmallHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class BigHeaderViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @BindView(R.id.txt_caption) TextView caption;
        @BindView(R.id.txt_greeting) TextView greeting;

        BigHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_version) TextView version;

        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface MenuItemClickListener {
        void onMenuItemClicked(MainListElementItem item);
    }
}
