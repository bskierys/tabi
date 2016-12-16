/*
* author: Bartlomiej Kierys
* date: 2016-12-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common.assemblers;

import android.database.Cursor;

import pl.ipebk.tabi.readmodel.PlaceAndPlateDto;
import pl.ipebk.tabi.readmodel.PlaceAndPlateFactory;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Assembler of {@link PlaceAndPlateDto} for tests
 */
public class PlaceAndPlateDtoAssembler extends PlaceAndPlateFactory {
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

    public PlaceAndPlateDtoAssembler withId(long id) {
        this.placeId = id;
        return this;
    }

    public PlaceAndPlateDtoAssembler withName(String name) {
        this.placeName = name;
        return this;
    }

    public PlaceAndPlateDtoAssembler inPowiat(String powiat) {
        this.powiat = powiat;
        return this;
    }

    public PlaceAndPlateDtoAssembler inVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
        return this;
    }

    public PlaceAndPlateDtoAssembler withPlate(String plateStart) {
        this.plateStart = plateStart;
        return this;
    }

    public PlaceAndPlateDtoAssembler standard() {
        this.placeType = PlaceType.TOWN;
        return this;
    }

    public PlaceAndPlateDtoAssembler random() {
        this.placeType = PlaceType.RANDOM;
        return this;
    }

    public PlaceAndPlateDtoAssembler special() {
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
