package com.oruphones.nativediagnostic.models;


import java.io.Serializable;

public class AdditionalInfoTest implements Serializable{

    public AdditionalInfoTest() {
    }

    public AdditionalInfoTest(String aditionalInfoTestName, String aditionalInfoTestKey, String aditionalInfoTestValue) {
        this.aditionalInfoTestName = aditionalInfoTestName;
        this.aditionalInfoTestKey = aditionalInfoTestKey;
        this.aditionalInfoTestValue = aditionalInfoTestValue;
    }

    String aditionalInfoTestName;
    String aditionalInfoTestKey;
    String aditionalInfoTestValue;

    public String getAditionalInfoTestName() {
        return aditionalInfoTestName;
    }

    public void setAditionalInfoTestName(String aditionalInfoTestName) {
        this.aditionalInfoTestName = aditionalInfoTestName;
    }

    public String getAditionalInfoTestKey() {
        return aditionalInfoTestKey;
    }

    public void setAditionalInfoTestKey(String aditionalInfoTestKey) {
        this.aditionalInfoTestKey = aditionalInfoTestKey;
    }

    public String getAditionalInfoTestValue() {
        return aditionalInfoTestValue;
    }

    public void setAditionalInfoTestValue(String aditionalInfoTestValue) {
        this.aditionalInfoTestValue = aditionalInfoTestValue;
    }



}
