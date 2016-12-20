/*
* author: Bartlomiej Kierys
* date: 2016-05-22
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.ui.search;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;

/**
 * This interface must be implemented by activities that contain this fragment to allow an interaction in this
 * fragment to be communicated to the activity and potentially other fragments contained in that activity.
 */
public interface PlaceFragmentEventListener {
    void onPlaceItemClicked(AggregateId placeId, String plateClicked, SearchType type, PlaceListItemType itemType);

    void onHeaderClicked(int eventId);

    void onFragmentViewCreated(SearchType type);
}
