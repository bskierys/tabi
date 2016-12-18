/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.place;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.BaseAggregateRoot;
import pl.ipebk.tabi.readmodel.LicensePlateDto;

/**
 * TODO: Generic description. Replace with real one.
 * todo: not exactly domain model - rebuild
 */
public class LicensePlate extends BaseAggregateRoot {
    private AggregateId placeId;
    private LicensePlateDto dto;

    // TODO: 2016-12-14 make private
    @SuppressWarnings("unused") LicensePlate() {}

    // TODO: 2016-12-10 this package private - make factory
    public LicensePlate(AggregateId placeId, String pattern, String end) {
        this.placeId = placeId;
        this.dto = LicensePlateDto.create(pattern, end);
    }

    public AggregateId getPlaceId() {
        return placeId;
    }

    public String getPattern() {
        return dto.pattern();
    }

    public String getEnd() {
        return dto.end();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LicensePlate plate = (LicensePlate) o;

        if (placeId != plate.placeId) {
            return false;
        }

        return dto.equals(plate.dto);
    }

    @Override public int hashCode() {
        int result = (int) (placeId.getValue() ^ (placeId.getValue() >>> 32));
        result = 31 * result + dto.hashCode();
        return result;
    }

    @Override public String toString() {
        String result = dto.pattern();
        if (dto.end() != null) {
            result += "..." + dto.end();
        }
        return result;
    }
}
