/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.place;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.BaseAggregateRoot;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * TODO: Generic description. Replace with real one.
 * todo: not exactly domain model - rebuild
 */
public class Place extends BaseAggregateRoot {
    private String name;
    private PlaceType type;
    private String voivodeship;
    private String powiat;
    private String gmina;
    private List<LicensePlate> plates;
    private boolean hasOwnPlate;

    // TODO: 2016-12-14 make private
    @SuppressWarnings("unused") Place() {}

    // TODO: 2016-12-10 factory to create this object
    public Place(String name, PlaceType type, String voivodeship, String powiat, String gmina, List<LicensePlate>
            plates, boolean hasOwnPlate) {
        this.name = name;
        this.type = type;
        this.voivodeship = voivodeship;
        this.powiat = powiat;
        this.gmina = gmina;
        this.plates = plates;
        this.hasOwnPlate = hasOwnPlate;
    }

    public String getName() {
        return name;
    }

    public PlaceType getType() {
        return type;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public String getPowiat() {
        return powiat;
    }

    public String getGmina() {
        return gmina;
    }

    public List<LicensePlate> getPlates() {
        return plates;
    }

    public boolean hasOwnPlate() {
        return hasOwnPlate;
    }

    void setName(String name) {
        this.name = name;
    }

    void setType(PlaceType type) {
        this.type = type;
    }

    void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    void setPowiat(String powiat) {
        this.powiat = powiat;
    }

    void setGmina(String gmina) {
        this.gmina = gmina;
    }

    void setPlates(List<LicensePlate> plates) {
        this.plates = plates;
    }

    void setHasOwnPlate(boolean hasOwnPlate) {
        this.hasOwnPlate = hasOwnPlate;
    }

    void setAggregateId(long id) {
        this.aggregateId = new AggregateId(id);
    }

    @Override public String toString() {
        return getName() + "," + getGmina() + ","
                + getPowiat() + "," + getVoivodeship();
    }

    /**
     * @return main {@link LicensePlate} for place or null if list is null or empty.
     */
    public LicensePlate getMainPlate() {
        if (plates != null && plates.size() > 0) {
            return plates.get(0);
        } else {
            return null;
        }
    }

    /**
     * @param pattern Start of pattern to search for.
     * @return First plate that starts with given pattern. Main plate if null given or if none matches pattern.
     */
    public LicensePlate getPlateMatchingPattern(String pattern) {
        LicensePlate plate = null;
        if (pattern == null) {
            plate = getMainPlate();
        } else {
            int i = 0;
            while (plate == null && i < plates.size()) {
                if (plates.get(i).getPattern().startsWith(pattern)) {
                    plate = plates.get(i);
                }
                i++;
            }
        }

        if (plate == null) {
            plate = getMainPlate();
        }

        return plate;
    }

    /**
     * @return String representation of list of plates separated by commas
     */
    public String platesToString() {
        return platesToString(plates);
    }

    /**
     * @param pattern Start of pattern to search for.
     * @return String representation of list of plates separated by commas. The one matching pattern is not included in
     * this string
     * @see {@link #getPlateMatchingPattern(String)}
     */
    public String platesToStringExceptMatchingPattern(String pattern) {
        List<LicensePlate> platesToShow = new ArrayList<>();
        LicensePlate plateMatchingPattern = getPlateMatchingPattern(pattern);
        for (LicensePlate plate : plates) {
            if (!plate.equals(plateMatchingPattern)) {
                platesToShow.add(plate);
            }
        }
        return platesToString(platesToShow);
    }

    private String platesToString(List<LicensePlate> plates) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < plates.size(); i++) {
            builder.append(plates.get(i));
            if (i != plates.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
