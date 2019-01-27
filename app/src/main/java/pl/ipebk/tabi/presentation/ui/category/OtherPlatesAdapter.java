package pl.ipebk.tabi.presentation.ui.category;

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

class OtherPlatesAdapter extends RecyclerView.Adapter<OtherPlatesAdapter.ItemViewHolder> {
    private List<CategoryInfo> categoryInfos;
    private MoreInfoClickListener infoClickListener;

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_icon) ImageView icon;
        @BindView(R.id.shadow) View shadow;
        @BindView(R.id.txt_title) TextView title;
        @BindView(R.id.txt_body) TextView body;
        @BindView(R.id.btn_more) TextView moreButton;
        @BindView(R.id.divider) ImageView divider;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    OtherPlatesAdapter(List<CategoryInfo> categoryInfos, MoreInfoClickListener infoClickListener) {
        this.categoryInfos = categoryInfos;
        this.infoClickListener = infoClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_other_plates, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        CategoryInfo categoryInfo = categoryInfos.get(position);

        if (position == getItemCount() - 1) {
            holder.shadow.setVisibility(View.VISIBLE);
        } else {
            holder.shadow.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        holder.icon.setImageDrawable(categoryInfo.icon());
        holder.title.setText(categoryInfo.title());
        holder.body.setText(categoryInfo.body());
        holder.moreButton.setOnClickListener(v -> infoClickListener.onMoreInfoClick(categoryInfo.link()));
    }

    @Override
    public int getItemCount() {
        return categoryInfos.size();
    }

    public interface MoreInfoClickListener {
        void onMoreInfoClick(String url);
    }
}
