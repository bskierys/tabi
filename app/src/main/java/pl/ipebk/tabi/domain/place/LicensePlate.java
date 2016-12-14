/*
* author: Bartlomiej Kierys
* date: 2016-12-04
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.domain.place;

import pl.ipebk.tabi.canonicalmodel.AggregateId;
import pl.ipebk.tabi.domain.BaseAggreagateRoot;

/**
 * TODO: Generic description. Replace with real one.
 * todo: not exactly domain model - rebuild
 */
public class LicensePlate extends BaseAggreagateRoot {
    private AggregateId placeId;
    private String pattern;
    private String end;

    // TODO: 2016-12-14 make private
    @SuppressWarnings("unused") LicensePlate() {}

    // TODO: 2016-12-10 this package private - make factory
    public LicensePlate(AggregateId placeId, String pattern, String end) {
        this.placeId = placeId;
        this.pattern = pattern;
        this.end = end;
    }

    public AggregateId getPlaceId() {
        return placeId;
    }

    public String getPattern() {
        return pattern;
    }

    public String getEnd() {
        return end;
    }

    void setPlaceId(AggregateId placeId) {
        this.placeId = placeId;
    }

    void setPattern(String pattern) {
        this.pattern = pattern;
    }

    void setEnd(String end) {
        this.end = end;
    }

    void setAggregateId(long id) {
        this.aggregateId = new AggregateId(id);
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
        if (!pattern.equals(plate.pattern)) {
            return false;
        }
        return end != null ? end.equals(plate.end) : plate.end == null;
    }

    @Override public int hashCode() {
        int result = (int) (placeId.getValue() ^ (placeId.getValue() >>> 32));
        result = 31 * result + pattern.hashCode();
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        String result = pattern;
        if (end != null) {
            result += "..." + end;
        }
        return result;
    }
}
