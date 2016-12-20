/*
* author: Bartlomiej Kierys
* date: 2016-02-17
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.daos;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.List;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.SearchHistoryModel;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.presentation.model.searchhistory.SearchType;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import pl.ipebk.tabi.test.common.assemblers.SearchHistoryAssembler;

public class SearchHistoryDaoTest extends DatabaseTest {
    private PlaceModel placeModel;

    @MediumTest public void testAddOrUpdateHistory() throws Exception {
        addToDatabase(givenPlace().withName("a"));

        remember(thatISearched().forPlaceWithId(givenPlaceId()).atTime(0));
        assertEquals(1, databaseHelper.getSearchHistoryDao().getAll().size());

        remember(thatISearched().forPlaceWithId(givenPlaceId()).atTime(10));
        List<SearchHistoryModel> wholeHistory = databaseHelper.getSearchHistoryDao().getAll();

        assertEquals(1, wholeHistory.size());
        assertEquals(10, wholeHistory.get(0).timeSearched().getTime());
    }

    @MediumTest public void testAddHistoryDoeInOtherSearchType() throws Exception {
        addToDatabase(givenPlace().withName("a"));

        remember(thatISearched().forPlaceWithId(givenPlaceId()).within(SearchType.PLACE).atTime(0));
        remember(thatISearched().forPlaceWithId(givenPlaceId()).within(SearchType.LICENSE_PLATE).atTime(10));

        assertEquals(2, databaseHelper.getSearchHistoryDao().getAll().size());
    }

    @MediumTest public void testGetNotSpecialPowiatPlacesCount() throws Exception {
        addToDatabase(givenPlace().ofType(PlaceType.TOWN).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY).withOwnPlate());

        addToDatabase(givenPlace().ofType(PlaceType.TOWN));
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE));
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY));
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY));

        addToDatabase(givenPlace().ofType(PlaceType.RANDOM).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.SPECIAL).withOwnPlate());

        int expected = 4;
        int actual = databaseHelper.getSearchHistoryDao().getStandardPlacesWithPlateCount();

        assertEquals(expected, actual);
    }

    @MediumTest public void testGetCount() throws Exception {
        addToDatabase(givenPlace().ofType(PlaceType.TOWN).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VILLAGE).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.POWIAT_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.VOIVODE_CITY).withOwnPlate());
        addToDatabase(givenPlace().ofType(PlaceType.SPECIAL).withOwnPlate());

        int expected = 4;
        int actual = databaseHelper.getSearchHistoryDao().getPlacesCount();

        assertEquals(expected, actual);
    }

    private void addToDatabase(PlaceModelAssembler assembler) {
        this.placeModel = assembler.assemble();
        databaseHelper.getPlaceDao().add(placeModel);
    }

    private void remember(SearchHistoryAssembler assembler) {
        databaseHelper.getSearchHistoryDao().updateOrAdd(assembler.assemble());
    }

    private PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    private long givenPlaceId() {
        return this.placeModel.getId();
    }

    private SearchHistoryAssembler thatISearched() {
        return new SearchHistoryAssembler();
    }
}
