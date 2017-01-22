/*
* author: Bartlomiej Kierys
* date: 2017-01-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.ipebk.tabi.R;
import timber.log.Timber;

/**
 * Instance of {@link android.support.v7.widget.RecyclerView.Adapter} that holds items for list of third party libraries.
 */
public class LibraryAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;

    protected List<LibsItem> libraries;
    protected Context context;

    public LibraryAdapter(Context context, List<LibsItem> libraries) {
        this.libraries = libraries;
        this.context = context;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_header, parent, false);
            viewHolder = new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
            viewHolder = new LibraryViewHolder(view);
        }
        return viewHolder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            if (getItemCount() > 1) {
                holder.progress.setVisibility(View.GONE);
            } else {
                holder.progress.setVisibility(View.VISIBLE);
            }
        } else {
            LibraryViewHolder holder = (LibraryViewHolder) viewHolder;
            Library library = libraries.get(position).library;
            holder.libraryName.setText(library.getLibraryName());
            holder.libraryCreator.setText(library.getAuthor());
            holder.libraryDescription.setText(Html.fromHtml(library.getLibraryDescription()));
            holder.card.setOnClickListener((view) -> {
                String link = "";
                if(!TextUtils.isEmpty(library.getLibraryWebsite())) {
                    link = library.getLibraryWebsite();
                }
                openWebsite(link);
            });
            holder.libraryCreator.setOnClickListener((view) -> {
                String link = "";
                if(!TextUtils.isEmpty(library.getAuthorWebsite())) {
                    link = library.getAuthorWebsite();
                }
                openWebsite(link);
            });
        }
    }

    @Override public int getItemCount() {
        if (libraries == null) {
            return 0;
        }
        return libraries.size();
    }

    @Override public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress) ProgressBar progress;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder {
        CardView card;

        @BindView(R.id.libraryName) TextView libraryName;
        @BindView(R.id.libraryCreator) TextView libraryCreator;
        @BindView(R.id.libraryDescriptionDivider) View libraryDescriptionDivider;
        @BindView(R.id.libraryDescription) TextView libraryDescription;

        public LibraryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            card = (CardView) itemView;
            card.setCardBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), com.mikepenz.aboutlibraries.R.attr.about_libraries_card, com.mikepenz.aboutlibraries.R.color.about_libraries_card));

            libraryName.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), com.mikepenz.aboutlibraries.R.attr.about_libraries_title_openSource, com.mikepenz.aboutlibraries.R.color.about_libraries_title_openSource));
            libraryCreator.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), com.mikepenz.aboutlibraries.R.attr.about_libraries_text_openSource, com.mikepenz.aboutlibraries.R.color.about_libraries_text_openSource));
            libraryDescriptionDivider.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), com.mikepenz.aboutlibraries.R.attr.about_libraries_dividerLight_openSource, com.mikepenz.aboutlibraries.R.color.about_libraries_dividerLight_openSource));
            libraryDescription.setTextColor(UIUtils.getThemeColorFromAttrOrRes(itemView.getContext(), com.mikepenz.aboutlibraries.R.attr.about_libraries_text_openSource, com.mikepenz.aboutlibraries.R.color.about_libraries_text_openSource));
        }
    }

    public static class LibsItem {

        public Library library;

        public LibsItem(Library library) {
            this.library = library;
        }
    }

    protected void openWebsite(String link) {
        if(!TextUtils.isEmpty(link)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Timber.w(e, "User doesn't have web browser...");
            }
        }
    }

    public void appendList(List<LibsItem> list) {
        libraries.addAll(list);
    }
}
