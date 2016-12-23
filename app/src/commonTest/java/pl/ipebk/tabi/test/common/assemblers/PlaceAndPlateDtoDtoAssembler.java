/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assemblers;

import android.database.Cursor;

import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDto;
import pl.ipebk.tabi.presentation.model.placeandplate.PlaceAndPlateDtoFactory;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Assembler of {@link PlaceAndPlateDto} for tests
 */
public class PlaceAndPlateDtoDtoAssembler extends PlaceAndPlateDtoFactory {
    private static final String DEFAULT_PLACE_NAME = "place_name";
    private static final String DEFAULT_PLATE_START = "RPY";
    private static final String DEFAULT_VOIVODESHIP = "voivodeship";
    private static final String DEFAULT_POWIAT = "powiat";

    private long placeId;
    private String placeName;
    private String plateStart;
    private String plateEnd;
    private String voivodeship;
    private String powiat;
    private PlaceType placeType;

    public PlaceAndPlateDto assemble() {
        fillDefaultValues();
        return create(placeId,placeName, plateStart, plateEnd, voivodeship, powiat, placeType);
    }

    public PlaceAndPlateDtoDtoAssembler withId(long id) {
        this.placeId = id;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler withName(String name) {
        this.placeName = name;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler inPowiat(String powiat) {
        this.powiat = powiat;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler inVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler withPlate(String plateStart) {
        this.plateStart = plateStart;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler standard() {
        this.placeType = PlaceType.TOWN;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler random() {
        this.placeType = PlaceType.RANDOM;
        return this;
    }

    public PlaceAndPlateDtoDtoAssembler special() {
        this.placeType = PlaceType.SPECIAL;
        return this;
    }

    private void fillDefaultValues() {
        if(placeName == null) {
            placeName = DEFAULT_PLACE_NAME;
        }

        if(plateStart == null) {
            plateStart = DEFAULT_PLATE_START;
        }

        if(voivodeship == null) {
            voivodeship = DEFAULT_VOIVODESHIP;
        }

        if(powiat == null) {
            powiat = DEFAULT_POWIAT;
        }
    }

    // fake method
    @Override public PlaceAndPlateDto createFromCursor(Cursor cursor) {
        return null;
    }
}
