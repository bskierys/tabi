/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.models;

public class Plate implements ModelInterface {
    private long id;
    private long placeId;
    private String pattern;
    private String end;

    @Override public long getId() {
        return id;
    }

    @Override public void setId(long id) {
        this.id = id;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Plate plate = (Plate) o;

        if (placeId != plate.placeId) return false;
        if (!pattern.equals(plate.pattern)) return false;
        return end != null ? end.equals(plate.end) : plate.end == null;
    }

    @Override public int hashCode() {
        int result = (int) (placeId ^ (placeId >>> 32));
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
