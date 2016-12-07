/*
* author: Bartlomiej Kierys
* date: 2016-12-06
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.test.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.ipebk.tabi.infrastructure.models.PlaceModel;
import pl.ipebk.tabi.infrastructure.models.PlateModel;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * TODO: Generic description. Replace with real one.
 */
public class TestModelFactory {
    protected PlaceModel placeModel;
    protected PlaceModelAssembler placeModelAssembler;

    public PlaceModelAssembler givenPlace() {
        placeModelAssembler = new PlaceModelAssembler();
        return placeModelAssembler;
    }

    public static class PlaceModelAssembler {
        private static final String RANDOM_PLATE_SOURCE = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
        private static final String DEFAULT_NAME = "Malbork";
        private static final String DEFAULT_VOIVODESHIP = "default_voivodeship";
        private static final String DEFAULT_POWIAT = "default_powiat";
        private static final String DEFAULT_GMINA = "default_gmina";
        private static final PlaceType DEAFULT_TYPE = PlaceType.POWIAT_CITY;

        private Random random = new Random();

        private String name;
        private PlaceType type;
        private String voivodeship;
        private String powiat;
        private String gmina;
        private List<PlateModel> plates;
        private boolean hasOwnPlate;

        public PlaceModelAssembler withName(String name) {
            this.name = name;
            return this;
        }

        public PlaceModelAssembler withPlate(String pattern) {
            if (plates == null) {
                plates = new ArrayList<>();
            }

            plates.add(PlateModel.create(pattern, ""));
            return this;
        }

        public PlaceModelAssembler and() {
            return this;
        }

        public PlaceModelAssembler withRandomPlates(int randomPlatesNumber) {
            if (plates == null) {
                plates = new ArrayList<>();
            }

            for (int i = 0; i < randomPlatesNumber; i++) {
                plates.add(getRandomPlate());
            }

            return this;
        }

        private PlateModel getRandomPlate() {
            int sourceLength = RANDOM_PLATE_SOURCE.length();
            StringBuilder patternBuilder = new StringBuilder(3);
            for (int i = 0; i < 3; i++) {
                int rndInt = random.nextInt(sourceLength);
                patternBuilder.append(RANDOM_PLATE_SOURCE.charAt(rndInt));
            }

            return PlateModel.create(patternBuilder.toString(), "");
        }

        public PlaceModelAssembler withOwnPlate() {
            this.hasOwnPlate = true;
            return this;
        }

        public PlaceModelAssembler ofType(PlaceType type) {
            this.type = type;
            return this;
        }

        public PlaceModel assemble() {
            fillDefaultValues();
            return PlaceModel.create(name, type, voivodeship, powiat, gmina, plates, hasOwnPlate);
        }

        private void fillDefaultValues() {
            if (name == null) {
                name = DEFAULT_NAME;
            }

            if (type == null) {
                type = DEAFULT_TYPE;
            }

            if (voivodeship == null) {
                voivodeship = DEFAULT_VOIVODESHIP;
            }

            if (powiat == null) {
                powiat = DEFAULT_POWIAT;
            }

            if (gmina == null) {
                gmina = DEFAULT_GMINA;
            }

            if (plates == null) {
                plates = new ArrayList<>();
                plates.add(getRandomPlate());
            }
        }
    }
}
