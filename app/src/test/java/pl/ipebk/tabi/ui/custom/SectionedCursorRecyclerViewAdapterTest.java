package pl.ipebk.tabi.ui.custom;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SectionedCursorRecyclerViewAdapterTest {
    public static final String FAKE_SECTION_TITLE = "title";
    @Mock Cursor cursor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test public void testPositionToSectionedPosition1() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(35);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        int expected = 6;
        int actual = adapter.positionToSectionedPosition(4);

        assertEquals(expected, actual);
    }

    @Test public void testPositionToSectionedPosition2() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(35);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        int expected = 1;
        int actual = adapter.positionToSectionedPosition(0);

        assertEquals(expected, actual);
    }

    @Test public void testSectionedPositionToPositionWhenSection() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(35);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        int expected = RecyclerView.NO_POSITION;
        int actual = adapter.sectionedPositionToPosition(6);

        assertEquals(expected, actual);
    }

    @Test public void testSectionedPositionToPosition() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(35);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        int expected = 2;
        int actual = adapter.sectionedPositionToPosition(4);

        assertEquals(expected, actual);
    }

    @Test public void testIsSectionHeaderPosition() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(35);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        assertTrue(adapter.isSectionHeaderPosition(6));
        assertFalse(adapter.isSectionHeaderPosition(2));
    }

    @Test public void testGetItemCount2Sections1() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(3);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(4, FAKE_SECTION_TITLE, null);

        int expected = 5;
        int actual = adapter.getItemCount();

        assertEquals(expected, actual);
    }

    @Test public void testGetItemCount2Sections2() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(2);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(4, FAKE_SECTION_TITLE, null);

        int expected = 3;
        int actual = adapter.getItemCount();

        assertEquals(expected, actual);
    }

    @Test public void testGetItemCount3Sections1() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(4);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);

        int expected = 7;
        int actual = adapter.getItemCount();

        assertEquals(expected, actual);
    }

    @Test public void testGetItemCount3Sections2() throws Exception {
        MockSectionedAdapter adapter = new MockSectionedAdapter(cursor);
        when(cursor.getCount()).thenReturn(2);

        adapter.addSection(0, FAKE_SECTION_TITLE, null);
        adapter.addSection(3, FAKE_SECTION_TITLE, null);
        adapter.addSection(6, FAKE_SECTION_TITLE, null);
        adapter.addSection(2, FAKE_SECTION_TITLE, null);
        adapter.removeSection(2);

        int expected = 4;
        int actual = adapter.getItemCount();

        assertEquals(expected, actual);
    }

    private class MockSectionedAdapter extends SectionedCursorRecyclerViewAdapter {
        public MockSectionedAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent) {
            return null;
        }

        @Override protected RecyclerView.ViewHolder createSectionViewHolder(ViewGroup parent) {
            return null;
        }

        @Override protected void bindItemViewHolder(RecyclerView.ViewHolder holder, Cursor cursor, int position) {}

        @Override protected void bindHeaderViewHolder(RecyclerView.ViewHolder holder, int position, Section section) {}

        @Override protected void itemInserted(int position) {}

        @Override protected void itemRemoved(int position) {}

        @Override protected void itemChanged(int position) {}
    }
}