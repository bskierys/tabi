package pl.ipebk.tabi.feedback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.test.common.utils.ShadowSharedPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeedbackStorageTest {
    private FeedbackStorage storage;
    private ShadowSharedPreferences mockPrefs;
    private Gson gson;
    private StorageAssembler storageAssembler;
    private StorageAssert storageAssert;

    @Before public void setUp() throws Exception {
        gson = new GsonBuilder().registerTypeAdapterFactory(FeedbackTypeAdapterFactory.create()).create();
        mockPrefs = new ShadowSharedPreferences();
        storage = new FeedbackStorage(mockPrefs, gson);
    }

    @Test public void shouldContain4Items_whenGiven4Items() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2").withQuestion("comment3").withBug("comment4");
        whenStorage();
        thenStorage().hasItems(4);
    }

    @Test public void shouldContainComment_whenWasInserted() throws Exception {
        givenStorage().withBug("comment1");
        whenStorage();
        thenStorage().hasItems(1).containsComment("comment1");
    }

    @Test public void shouldContainNoElements_whenCleared() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2").withQuestion("comment3");
        whenStorage().clearUnsentItems();
        thenStorage().hasItems(0);
    }

    @Test public void shouldContainElements_whenAddedAdditionalElements() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2");
        whenStorage();
        thenStorage().hasItems(2);
        givenStorage().withBug("comment3").withIdea("comment4");
        whenStorage();
        thenStorage().hasItems(4).containsComment("comment1").containsComment("comment4");
    }

    @Test public void shouldContainElements_whenAddedAfterClearing() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2");
        whenStorage().clearUnsentItems();
        givenStorage().withBug("comment3").withIdea("comment4");
        whenStorage();
        thenStorage().hasItems(2).containsComment("comment3").containsComment("comment4");
    }

    @Test public void shouldAddOnly20Items_whenAdding22() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2");
        whenStorage();
        thenStorage().hasItems(20);
    }

    @Test public void shouldBeMax20Items_whenAddingToExisting() throws Exception {
        givenStorage().withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2")
                      .withBug("comment1").withIdea("comment2").withBug("comment1").withIdea("comment2");
        whenStorage();
        thenStorage().hasItems(20);
        givenStorage().withBug("comment3").withIdea("comment4");
        whenStorage();
        thenStorage().hasItems(20).containsComment("comment3").containsComment("comment4");
    }

    private StorageAssembler givenStorage() {
        storageAssembler = new StorageAssembler();
        return storageAssembler;
    }

    private FeedbackStorage whenStorage() {
        storageAssembler.assemble();
        return storage;
    }

    private StorageAssert thenStorage() {
        storageAssert = new StorageAssert(storage);
        return storageAssert;
    }

    class StorageAssembler {
        private List<FeedbackItem> items;

        StorageAssembler() {
            items = new ArrayList<>();
        }

        FeedbackStorage assemble() {
            storage.putUnsentItems(items);
            return storage;
        }

        StorageAssembler withBug(String comment) {
            items.add(createFeedbackItem(comment, FeedbackType.BUG));
            return this;
        }

        StorageAssembler withIdea(String comment) {
            items.add(createFeedbackItem(comment, FeedbackType.IDEA));
            return this;
        }

        StorageAssembler withQuestion(String comment) {
            items.add(createFeedbackItem(comment, FeedbackType.QUESTION));
            return this;
        }

        private FeedbackItem createFeedbackItem(String comment, FeedbackType type) {
            return FeedbackItem.create(comment, type.toString(), "ts", "model", "manufact",
                                       "sdk", "pname", "uuid", "libver", "versionName",
                                       "versionCode", "custom");
        }
    }

    class StorageAssert {
        private FeedbackStorage feedbackStorage;

        StorageAssert(FeedbackStorage feedbackStorage) {
            this.feedbackStorage = feedbackStorage;
        }

        StorageAssert hasItems(int count) {
            List<FeedbackItem> items = feedbackStorage.getUnsentItems();
            assertEquals(count, items.size());
            assertEquals(count, feedbackStorage.getUnsentItemsCount());
            return this;
        }

        StorageAssert containsComment(String comment) {
            List<FeedbackItem> items = feedbackStorage.getUnsentItems();
            boolean isPresent = false;
            for (FeedbackItem item : items) {
                if (item.comment().equals(comment)) {
                    isPresent = true;
                }
            }
            assertTrue(isPresent);
            return this;
        }
    }
}