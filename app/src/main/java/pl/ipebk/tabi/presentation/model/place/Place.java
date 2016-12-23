/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.model.place;

import java.util.ArrayList;
import java.util.List;

import pl.ipebk.tabi.presentation.localization.PlaceLocalizationHelper;
import pl.ipebk.tabi.readmodel.PlaceType;

/**
 * Full model of place that can format itself into UI
 */
public class Place {
    private PlaceDto dto;
    private PlaceLocalizationHelper localizationHelper;

    Place(PlaceDto dto, PlaceLocalizationHelper localizationHelper) {
        this.dto = dto;
        this.localizationHelper = localizationHelper;
    }

    Place(String name, PlaceType type, String voivodeship, String powiat,
          String gmina, List<LicensePlateDto> plates) {
        this.dto = PlaceDto.create(name, type, voivodeship, powiat, gmina, plates);
    }

    public String getName() {
        return dto.name();
    }

    public PlaceType getType() {
        return dto.placeType();
    }

    /**
     * @return Voivodeship - already formatted
     */
    public String getVoivodeship() {
        return localizationHelper.formatVoivodeship(dto.voivodeship());
    }

    /**
     * @return Powiat - already formatted
     */
    public String getPowiat() {
        return localizationHelper.formatPowiat(dto.powiat());
    }

    /**
     * @return Gmina - already formatted
     */
    public String getGmina() {
        return localizationHelper.formatGmina(dto.gmina());
    }

    /**
     * @param searchedPlate late that was searched, to exclude it from additional info
     * @return Already formatted additional info
     */
    public String getAdditionalInfo(String searchedPlate) {
        return localizationHelper.formatAdditionalInfo(this, searchedPlate);
    }

    public String getFullInfo() {
        return localizationHelper.formatPlaceInfo(this);
    }

    /**
     * @return String that can be put into search engine to search for place
     */
    public String getSearchPhrase() {
        return localizationHelper.formatPlaceToSearch(this);
    }

    public boolean hasAdditionalPlates() {
        return dto.plates().size() > 1;
    }

    @Override public String toString() {
        return getName() + "," + getGmina() + ","
                + getPowiat() + "," + getVoivodeship();
    }

    /**
     * @return main {@link LicensePlateDto} for place or null if list is null or empty.
     */
    public LicensePlateDto getMainPlate() {
        if (dto.plates() != null && dto.plates().size() > 0) {
            return dto.plates().get(0);
        } else {
            return null;
        }
    }

    /**
     * @param pattern Start of pattern to search for.
     * @return First plate that starts with given pattern. Main plate if null given or if none matches pattern.
     */
    public LicensePlateDto getPlateMatchingPattern(String pattern) {
        LicensePlateDto plate = null;
        if (pattern == null) {
            plate = getMainPlate();
        } else {
            int i = 0;
            while (plate == null && i < dto.plates().size()) {
                if (dto.plates().get(i).pattern().startsWith(pattern)) {
                    plate = dto.plates().get(i);
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
        return platesToString(dto.plates());
    }

    /**
     * @param pattern Start of pattern to search for.
     * @return String representation of list of plates separated by commas. The one matching pattern is not included in
     * this string
     * @see {@link #getPlateMatchingPattern(String)}
     */
    public String platesToStringExceptMatchingPattern(String pattern) {
        List<LicensePlateDto> platesToShow = new ArrayList<>();
        LicensePlateDto plateMatchingPattern = getPlateMatchingPattern(pattern);
        for (LicensePlateDto plate : dto.plates()) {
            if (!plate.equals(plateMatchingPattern)) {
                platesToShow.add(plate);
            }
        }
        return platesToString(platesToShow);
    }

    private String platesToString(List<LicensePlateDto> plates) {
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
