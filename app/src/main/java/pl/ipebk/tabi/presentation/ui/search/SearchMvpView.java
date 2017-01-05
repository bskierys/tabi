/*
* author: Bartlomiej Kierys
* date: 2016-02-24
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.search;

import android.database.Cursor;

import pl.ipebk.tabi.presentation.model.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.presentation.ui.base.MvpView;

public interface SearchMvpView extends MvpView {
    void showClearButton();

    void hideClearButton();

    // TODO: 2016-05-14 clean it up
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

    void showKeyboard();

    void goToPlaceDetails(AggregateId placeId, String searchedText, SearchType searchType, PlaceListItemType itemType);
}
