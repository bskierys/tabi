/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import android.database.Cursor;

import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.base.MvpView;

public interface SearchMvpView extends MvpView {
    // plates section
    void showEmptyStateInPlatesSection();

    void hideEmptyStateInPlatesSection();

    void showFullSearchInPlatesSection(Cursor cursor);

    void showBestSearchInPlatesSection(Cursor cursor);

    void showInitialSearchInPlatesSection(Cursor cursor);

    // places section
    void showFullSearchInPlacesSection(Cursor cursor);

    void showBestSearchInPlacesSection(Cursor cursor);

    void showInitialSearchInPlacesSection(Cursor cursor);

    void showEmptyStateInPlacesSection();

    void hideEmptyStateInPlacesSection();

    void setSearchText(String searchText);

    void hideKeyboard();

    void goToPlaceDetails(long placeId, String searchedText, SearchType searchType);
}
