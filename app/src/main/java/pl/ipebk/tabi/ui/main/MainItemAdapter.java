/*
* author: Bartlomiej Kierys
* date: 2016-05-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.ipebk.tabi.App;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.utils.NameFormatHelper;
import pl.ipebk.tabi.utils.ResourceHelper;

public class MainItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int TYPE_BIG_HEADER = 0;
    static final int TYPE_SMALL_HEADER = 1;
    static final int TYPE_ITEM = 2;

    @Inject ResourceHelper resourceHelper;
    @Inject NameFormatHelper nameFormatHelper;
    private List<MainListItem> categoryList;
    private Context context;
    private final MenuItemClickListener listener;

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
        } else if(viewType == TYPE_SMALL_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header, parent, false);
            return new SmallHeaderViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type "
                                           + viewType + " + make sure your using types correctly");
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            MainListElementItem item = (MainListElementItem) categoryList.get(position - 1);

            itemViewHolder.rootView.setOnClickListener(v -> listener.onMenuItemClicked(item.getActionKey()));
            itemViewHolder.categoryName.setText(resourceHelper.getStringResourceForKey(item.getTitleResourceKey()));
            itemViewHolder.categoryIcon.setImageDrawable(resourceHelper.getDrawableResourceForKey(item.getImageResourceKey()));
        } else if(holder instanceof BigHeaderViewHolder) {
            // TODO: 2016-05-31 same holder names
            BigHeaderViewHolder headerViewHolder = (BigHeaderViewHolder) holder;

            // TODO: 2016-06-07 caption should be generic or depends on sharedPrefs
            // TODO: 2016-06-07 make sharedPrefsHelper 
            String caption = context.getString(R.string.main_doodle_caption);

            headerViewHolder.caption.setText(nameFormatHelper.formatDoodleCaption(caption),
                                             TextView.BufferType.SPANNABLE);
            headerViewHolder.greeting.setText(nameFormatHelper.formatDoodleGreeting());
        } else if(holder instanceof SmallHeaderViewHolder) {
            SmallHeaderViewHolder headerViewHolder = (SmallHeaderViewHolder) holder;
            MainListHeaderItem item = (MainListHeaderItem) categoryList.get(position - 1);

            headerViewHolder.header.setText(resourceHelper.getStringResourceForKey(item.getTitleResourceKey()));
        }
    }

    @Override public int getItemCount() {
        return categoryList.size()+1;
    }

    @Override public int getItemViewType(int position) {
        if(isSectionHeaderPosition(position)){
            return TYPE_BIG_HEADER;
        } else {
            MainListItem item = categoryList.get(position-1);
            if(item instanceof MainListElementItem) {
                return TYPE_ITEM;
            } else if (item instanceof MainListHeaderItem){
                return TYPE_SMALL_HEADER;
            }
        }

        return 30;
    }

    public void swapItems(List<MainListItem> items) {
        this.categoryList.clear();
        this.categoryList = items;

        notifyDataSetChanged();
    }

    protected boolean isSectionHeaderPosition(int position) {
        return position == 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @Bind(R.id.txt_category) TextView categoryName;
        @Bind(R.id.ic_category) ImageView categoryIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    public static class SmallHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_header) TextView header;

        public SmallHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class BigHeaderViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        @Bind(R.id.txt_caption) TextView caption;
        @Bind(R.id.txt_greeting) TextView greeting;

        public BigHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }
    }

    public interface MenuItemClickListener {
        void onMenuItemClicked(String action);
    }
}
