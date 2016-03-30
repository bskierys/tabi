/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;

import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.ui.base.MvpView;

public interface SearchMvpView extends MvpView {
    // plates section
    void showEmptyStateInPlatesSection();

    void hideEmptyStateInPlatesSection();

    void showPlacesInPlatesSection(Cursor cursor);

    // places section
    void showPlacesInPlacesSection(Cursor cursor);

    void showEmptyStateInPlacesSection();

    void hideEmptyStateInPlacesSection();

    void setSearchText(String searchText);

    void hideKeyboard();

    void goToPlaceDetails(long placeId, String searchedText, SearchHistory.SearchType searchType);
}
