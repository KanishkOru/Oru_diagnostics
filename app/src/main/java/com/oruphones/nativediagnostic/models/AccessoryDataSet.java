package com.oruphones.nativediagnostic.models;


import java.io.Serializable;
import java.util.Objects;

public class AccessoryDataSet extends DeviceInfoDataSet implements Serializable,Comparable<AccessoryDataSet> {

    Integer id ;
    public AccessoryDataSet(int id, String titleId, int drawableId, String value) {
        super(titleId, drawableId, value);
        this.id=id;
    }


    @Override
    public int compareTo(AccessoryDataSet o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessoryDataSet that = (AccessoryDataSet) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
