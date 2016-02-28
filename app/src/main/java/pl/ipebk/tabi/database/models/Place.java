/*
* author: Bartlomiej Kierys
* date: 2016-02-09
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.database.models;

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

    @Override public String toString(){
        return getName() + "," + getGmina() + ","
                + getPowiat() + "," + getVoivodeship();
    }

    public Plate getMainPlate(){
        if(plates!=null && plates.size()>0){
            return plates.get(0);
        }else {
            return null;
        }
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
