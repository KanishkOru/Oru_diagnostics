package com.oruphones.nativediagnostic.api;

import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.models.CompanyConfigData;

public class StoreIdConfig {

    @SerializedName("resultData")
    private CompanyConfigData companyData;

    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    public CompanyConfigData getCompanyData() {
        return companyData;
    }

    public void setCompanyData(CompanyConfigData companyData) {
        this.companyData = companyData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
