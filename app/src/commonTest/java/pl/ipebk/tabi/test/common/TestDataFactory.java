/*
* author: Bartlomiej Kierys
* date: 2016-03-05
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pl.ipebk.tabi.database.models.Place;
import pl.ipebk.tabi.database.models.Plate;
import pl.ipebk.tabi.database.models.SearchHistory;
import pl.ipebk.tabi.database.models.SearchType;
import pl.ipebk.tabi.ui.search.PlaceListItem;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {
    public static Place createPlaceWithPlates(String... patterns) {
        Place malbork = new Place();
        malbork.setName("Malbork");
        malbork.setPlates(getListOfPlates(patterns));

        return malbork;
    }

    private static List<Plate> getListOfPlates(String... patterns) {
        List<Plate> plates = new ArrayList<>();
        for (int i = 0; i < patterns.length; i++) {
            plates.add(createPlate(patterns[i]));
        }
        return plates;
    }

    public static Plate createPlate(String pattern) {
        Plate plate = new Plate();
        plate.setPattern(pattern);
        return plate;
    }

    public static Place createPlaceForType(Place.Type type, boolean hasOwnPlate){
        Place place = createStandardPlace("1", "AAA", type);
        place.setHasOwnPlate(hasOwnPlate);
        return place;
    }

    public static Place createStandardPlace(String name, String plateStart, Place.Type categoryType) {
        Place place = new Place();
        place.setVoivodeship(name);
        place.setName(name);
        place.setType(categoryType);
        place.setHasOwnPlate(true);
        List<Plate> plates = new ArrayList<>();
        Plate plate = new Plate();

        if (plateStart == null) {
            plate.setPattern(name + name + name);
        } else {
            plate.setPattern(plateStart);
        }

        plates.add(plate);
        place.setPlates(plates);
        return place;
    }

    public static Place createStandardPlace(String placeName) {
        Place place = getTemplatePlace(placeName);

        place.setType(Place.Type.VOIVODE_CITY);
        place.setPowiat("powiat");
        place.setGmina("gmina");

        return place;
    }

    @NonNull private static Place getTemplatePlace(String placeName) {
        Place place = new Place();
        place.setName(placeName);

        Random random = new Random();
        int tableLength = 3;
        int upLimit = placeName.length() - (tableLength + 1);

        String[] patterns = new String[3];
        for (int i = 0; i < 3; i++) {
            int randomInt = random.nextInt(upLimit);
            patterns[i] = placeName.substring(randomInt, randomInt + tableLength).toUpperCase();
        }

        place.setPlates(getListOfPlates(patterns));
        place.setHasOwnPlate(true);
        place.setVoivodeship("voivodeship");
        place.setId(random.nextLong());
        return place;
    }

    public static Place createSpecialPlace(String placeName) {
        Place place = getTemplatePlace(placeName);
        place.setType(Place.Type.SPECIAL);

        return place;
    }

    public static SearchHistory createSearchHistory(long placeId, SearchType type, long time){
        SearchHistory history = new SearchHistory();
        history.setPlaceId(placeId);
        history.setPlate("TAB");
        history.setSearchType(type);
        history.setTimeSearched(new Date(time));
        return history;
    }

    public static PlaceListItem createStandardPlaceItem(String name, String plateStart, Place.Type type){
        PlaceListItem place = new PlaceListItem();

        place.setPlaceName(name);
        place.setPlaceType(type);
        place.setPlateStart(plateStart);
        place.setPowiat(name);
        place.setVoivodeship(name);

        return place;
    }

    public static PlaceListItem createSpecialPlaceItem(String twoWordName, String plateStart){
        PlaceListItem place = new PlaceListItem();

        place.setPlaceName(twoWordName);
        place.setPlaceType(Place.Type.SPECIAL);
        place.setPlateStart(plateStart);
        place.setVoivodeship(twoWordName);

        return place;
    }
}
