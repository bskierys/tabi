/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.models;

import java.util.ArrayList;
import java.util.List;

public class Place implements ModelInterface {
    private long id;
    private String name;
    private Type type;
    private String voivodeship;
    private String powiat;
    private String gmina;
    private List<Plate> plates;
    private boolean hasOwnPlate;

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public String getPowiat() {
        return powiat;
    }

    public void setPowiat(String powiat) {
        this.powiat = powiat;
    }

    public String getGmina() {
        return gmina;
    }

    public void setGmina(String gmina) {
        this.gmina = gmina;
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public void setPlates(List<Plate> plates) {
        this.plates = plates;
    }

    public boolean isHasOwnPlate() {
        return hasOwnPlate;
    }

    public void setHasOwnPlate(boolean hasOwnPlate) {
        this.hasOwnPlate = hasOwnPlate;
    }

    @Override public String toString() {
        return getName() + "," + getGmina() + ","
                + getPowiat() + "," + getVoivodeship();
    }

    /**
     * @return main {@link Plate} for place or null if list is null or empty.
     */
    public Plate getMainPlate() {
        if (plates != null && plates.size() > 0) {
            return plates.get(0);
        } else {
            return null;
        }
    }

    /**
     * @param pattern Start of pattern to search for.
     * @return First plate that starts with given pattern. Main plate if null given.
     * Null if none matches pattern.
     */
    public Plate getPlateMatchingPattern(String pattern) {
        Plate plate = null;
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
     * @return String representation of list of plates separated by commas.
     * The one matching pattern is not included in this string
     * @see {@link #getPlateMatchingPattern(String)}
     */
    public String platesToStringExceptMatchingPattern(String pattern) {
        List<Plate> platesToShow = new ArrayList<>();
        Plate plateMatchingPattern = getPlateMatchingPattern(pattern);
        for (Plate plate : plates) {
            if (!plate.equals(plateMatchingPattern)) {
                platesToShow.add(plate);
            }
        }
        return platesToString(platesToShow);
    }

    private String platesToString(List<Plate> plates) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < plates.size(); i++) {
            builder.append(plates.get(i).toString());
            if (i != plates.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public enum Type {
        VOIVODE_CITY,
        POWIAT_CITY,
        TOWN,
        PART_OF_TOWN,
        VILLAGE,
        SPECIAL,
        UNSPECIFIED
    }
}
