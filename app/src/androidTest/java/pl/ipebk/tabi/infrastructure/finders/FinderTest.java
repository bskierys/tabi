/*
* author: Bartlomiej Kierys
* date: 2016-12-07
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.infrastructure.finders;

import java.util.List;

import pl.ipebk.tabi.infrastructure.DatabaseTest;
import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.test.common.assemblers.PlaceModelAssembler;
import pl.ipebk.tabi.test.common.assertions.PlaceDtoCollectionAssert;

public class FinderTest extends DatabaseTest {
    private List<PlaceAndPlateDto> foundPlaces;

    PlaceModelAssembler givenPlace() {
        return new PlaceModelAssembler();
    }

    void addToDatabase(PlaceModelAssembler assembler) {
        databaseHelper.getPlaceDao().add(assembler.assemble());
    }

    void whenSearched(List<PlaceAndPlateDto> places) {
        this.foundPlaces = places;
    }

    PlaceDtoCollectionAssert thenFoundPlaces() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }

    PlaceDtoCollectionAssert then() {
        return new PlaceDtoCollectionAssert(foundPlaces);
    }
}
