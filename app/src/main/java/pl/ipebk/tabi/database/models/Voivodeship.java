/*
* author: Bartlomiej Kierys
* date: 2016-02-14
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.models;

public class Voivodeship {
    private String name;
    private Character plateStart;
    private Place.Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getPlateStart() {
        return plateStart;
    }

    public void setPlateStart(Character plateStart) {
        this.plateStart = plateStart;
    }

    public Place.Type getType() {
        return type;
    }

    public void setType(Place.Type type) {
        this.type = type;
    }
}
