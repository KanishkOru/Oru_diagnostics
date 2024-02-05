package com.oruphones.nativediagnostic.communication.api;

import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.api.AppDetails;

import java.io.Serializable;
import java.util.List;


public class WebrootAppsDetails implements Serializable {

    @SerializedName("company")
    private String company;

    @SerializedName("uid")
    private String uid;

    @SerializedName("platform")
    private String platform;

    @SerializedName("apps")
    private List<AppDetails> apps;

    @SerializedName("skipOutdatedAppCheck")
    private boolean skipOutdatedAppCheck = true;

    @SerializedName("skipRiskyAppCheck")
    private boolean skipRiskyAppCheck = true;

    public boolean isSkipOutdatedAppCheck() {
        return skipOutdatedAppCheck;
    }

    public void setSkipOutdatedAppCheck(boolean skipOutdatedAppCheck) {
        this.skipOutdatedAppCheck = skipOutdatedAppCheck;
    }

    public boolean isSkipRiskyAppCheck() {
        return skipRiskyAppCheck;
    }

    public void setSkipRiskyAppCheck(boolean skipRiskyAppCheck) {
        this.skipRiskyAppCheck = skipRiskyAppCheck;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<AppDetails> getApps() {
        return apps;
    }

    public void setApps(List<AppDetails> apps) {
        this.apps = apps;
    }
}

