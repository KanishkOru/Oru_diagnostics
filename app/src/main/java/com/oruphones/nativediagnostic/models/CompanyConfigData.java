package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.SerializedName;


/*  {"status":"PASS","resultData":{"customer":"CallCenter","storeId":null,"product":"Call-Center","language":null,"serverURL":"https://callcenter.mobilicis.com/","pin":"474445","secretKey":"YXBwc3RvcmV1c2VyOiRlY3IzVA==","captureIMEI":false,"showRepId":false}}
 */

public class CompanyConfigData {
    @SerializedName(value="companyName", alternate={"customer"})
    String companyName;
    @SerializedName("storeCode")
    String storeCode;
    @SerializedName("product")
    String product;
    @SerializedName(value="password",alternate={"secretKey"})
    String password;
    @SerializedName("language")
    String language;
    @SerializedName(value="serverUrl", alternate={"serverURL"})
    String serverUrl;
    @SerializedName("captureIMEI")
    boolean captureIMEI;

    @SerializedName("pin")
    String pin;
    @SerializedName("isRepIdRequired")
    boolean isRepIdRequired;
    @SerializedName("isPasswordrequired")
    boolean isPasswordrequired;
    @SerializedName("isRepIdValidationRequired")
    boolean isRepIdValidationRequired;

    public String getSecretKey() {
        return password;
    }

    public void setSecretKey(String secretKey) {
        this.password = secretKey;
    }

    public String getCustomer() {
        return companyName;//
    }

    public void setCustomer(String customer) {
        this.companyName = customer;
    }

    public String getStoreId() {
        return storeCode;
    }

    public void setStoreId(String storeId) {
        this.storeCode = storeId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getServerURL() {
        return serverUrl;
    }

    public void setServerURL(String serverURL) {
        this.serverUrl = serverURL;
    }

    public boolean getCaptureIMEI() {
        return captureIMEI;
    }

    public void setCaptureIMEI(boolean captureIMEI) {
        this.captureIMEI = captureIMEI;
    }

    public boolean getIsRepIdRequired() {
        return isRepIdRequired;
    }

    public void setIsRepIdRequired(boolean isRepIdRequired) {
        this.isRepIdRequired = isRepIdRequired;
    }

    public boolean getIsPasswordrequired() {
        return isPasswordrequired;
    }

    public void setIsPasswordrequired(boolean isPasswordrequired) {
        this.isPasswordrequired = isPasswordrequired;
    }

    public boolean getIsRepIdValidationRequired() {
        return isRepIdValidationRequired;
    }

    public void setIsRepIdValidationRequired(boolean isRepIdValidationRequired) {
        this.isRepIdValidationRequired = isRepIdValidationRequired;
    }

    public String getPin() {
        return pin;
    }
}
