package pl.ipebk.tabi.ui.custom;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Instance of {@link android.support.v7.widget.RecyclerView.Adapter} that can be easily managed with sections and takes
 * {@link Cursor} as data source. Use {@link #addSection(int, String, Integer)} and {@link #removeSection(int)} to add,
 * update, and remove sections
 */
public abstract class SectionedCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    protected boolean isValid = true;
    protected HashMap<Integer, Section> sections = new HashMap<>();
    protected List<Integer> sectionIndexes = new ArrayList<>();

    public SectionedCursorRecyclerViewAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return createItemViewHolder(parent);
        } else if (viewType == TYPE_HEADER) {
            return createSectionViewHolder(parent);
        }

        throw new RuntimeException("there is no type that matches the type "
                                           + viewType + " + make sure your using types correctly");
    }

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent);

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            bindHeaderViewHolder(viewHolder, position, sections.get(position));
        } else {
            super.onBindViewHolder(viewHolder, sectionedPositionToPosition(position));
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor, int position) {
        bindItemViewHolder(viewHolder, cursor, position);
    }

    protected abstract void bindItemViewHolder(RecyclerView.ViewHolder holder, Cursor cursor, int position);

    protected abstract void bindHeaderViewHolder(RecyclerView.ViewHolder holder, int position, Section section);

    /**
     * Adds or updates section in specific position.
     *
     * @param position Position of future section. Must be relative to all rows in list f. ex. if you plan to add
     * section on beginning, then two item rows and again section add sections for position 0 and 3.
     * @param title String title of section
     * @param eventId click event for header will be posted with this id. set null for non clickable sections
     */
    public void addSection(int position, @NonNull String title, Integer eventId) {
        if (sections.get(position) != null) {
            Timber.d("Section already exists. Updating for name: %s", title);
            sections.put(position, new Section(title, eventId));

            itemChanged(position);
        } else {
            Timber.d("Adding new section: %s", title);
            sections.put(position, new Section(title, eventId));
            sectionIndexes.add(position);
            Collections.sort(sectionIndexes);

            itemInserted(position);
        }
    }

    /**
     * Removes section from given position.
     * @param position Position from which to remove section
     */
    public void removeSection(int position) {
        sections.remove(position);
        sectionIndexes.remove(position);
        Collections.sort(sectionIndexes);

        itemRemoved(position);
    }

    protected void itemInserted(int position) {
        notifyItemInserted(position);
    }

    protected void itemRemoved(int position) {
        notifyItemRemoved(position);
    }

    protected void itemChanged(int position) {
        notifyItemChanged(position);
    }

    protected int positionToSectionedPosition(int position) {
        int offset = 0;
        for (Integer index : sectionIndexes) {
            if (index <= position) {
                ++offset;
            }
        }

        return position + offset;
    }

    protected int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (Integer index : sectionIndexes) {
            if (index <= sectionedPosition) {
                --offset;
            }
        }

        return sectionedPosition + offset;
    }

    protected boolean isSectionHeaderPosition(int position) {
        return sections.get(position) != null;
    }

    @Override public int getItemViewType(int position) {
        return isSectionHeaderPosition(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - position
                : super.getItemId(sectionedPositionToPosition(position));
    }

    @Override public int getItemCount() {
        int count = super.getItemCount();

        for (Integer index : sectionIndexes) {
            if (count >= index) {
                count++;
            }
        }

        return isValid ? count : 0;
    }

    public static class Section {
        CharSequence title;
        Integer clintEventId;

        public Section(CharSequence title, Integer clintEventId) {
            this.title = title;
            this.clintEventId = clintEventId;
        }

        public CharSequence getTitle() {
            return title;
        }

        public Integer getClintEventId() {
            return clintEventId;
        }
    }
}
