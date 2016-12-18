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
import pl.ipebk.tabi.readmodel.LicensePlateDto;
import pl.ipebk.tabi.readmodel.PlaceDto;
import pl.ipebk.tabi.readmodel.PlaceType;
import pl.ipebk.tabi.utils.NameFormatHelper;

/**
 * TODO: Generic description. Replace with real one.
 * todo: not exactly domain model - rebuild
 */
public class Place extends BaseAggregateRoot {
    private PlaceDto dto;
    private NameFormatHelper nameFormatHelper;

    // TODO: 2016-12-14 make private
    @SuppressWarnings("unused") Place() {}

    Place(PlaceDto dto, NameFormatHelper formatHelper) {
        this.dto = dto;
        this.nameFormatHelper = formatHelper;
    }

    // TODO: 2016-12-10 factory to create this object
    public Place(String name, PlaceType type, String voivodeship, String powiat,
                 String gmina, List<LicensePlate> plates) {
        List<LicensePlateDto> plateDtos = new ArrayList<>();
        for (LicensePlate plate: plates) {
            plateDtos.add(plate.getDto());
        }

        this.dto = PlaceDto.create(name, type, voivodeship, powiat, gmina, plateDtos);
    }

    public String getName() {
        return dto.name();
    }

    public PlaceType getType() {
        return dto.placeType();
    }

    public String getVoivodeship() {
        return nameFormatHelper.formatVoivodeship(dto.voivodeship());
    }

    public String getPowiat() {
        return nameFormatHelper.formatPowiat(dto.powiat());
    }

    public String getGmina() {
        return nameFormatHelper.formatGmina(dto.gmina());
    }

    public String getAdditionalInfo(String searchedPlate) {
        return nameFormatHelper.formatAdditionalInfo(this, searchedPlate);
    }

    public String getFullInfo() {
        return nameFormatHelper.formatPlaceInfo(this);
    }

    // TODO: 2016-12-18 describe
    public String getSearchPhrase() {
        return nameFormatHelper.formatPlaceToSearch(this);
    }

    public List<LicensePlate> getPlates() {
        List<LicensePlate> plates = new ArrayList<>();
        for(LicensePlateDto plateDto: dto.plates()) {
            plates.add(new LicensePlate(aggregateId, plateDto.pattern(), plateDto.end()));
        }
        return plates;
    }

    @Override public String toString() {
        return getName() + "," + getGmina() + ","
                + getPowiat() + "," + getVoivodeship();
    }

    /**
     * @return main {@link LicensePlate} for place or null if list is null or empty.
     */
    public LicensePlate getMainPlate() {
        if (dto.plates() != null && dto.plates().size() > 0) {
            LicensePlateDto plateDto = dto.plates().get(0);
            return new LicensePlate(aggregateId, plateDto.pattern(), plateDto.end());
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
            while (plate == null && i < dto.plates().size()) {
                if (dto.plates().get(i).pattern().startsWith(pattern)) {
                    LicensePlateDto plateDto = dto.plates().get(i);
                    plate = new LicensePlate(aggregateId, plateDto.pattern(), plateDto.end());
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
        List<LicensePlate> plates = new ArrayList<>();
        for (LicensePlateDto plateDto : dto.plates()) {
            LicensePlate plate = new LicensePlate(aggregateId, plateDto.pattern(), plateDto.end());
            plates.add(plate);
        }
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
        for (LicensePlateDto plateDto : dto.plates()) {
            LicensePlate plate = new LicensePlate(aggregateId, plateDto.pattern(), plateDto.end());
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
