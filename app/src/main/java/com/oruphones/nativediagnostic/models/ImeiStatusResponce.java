package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImeiStatusResponce {
    @SerializedName("imeiBlackListStatus")
    private String[] imeiBlackListStatus;

    @SerializedName("imeiStolenHistory")
    private List<Object> imeiStolenHistory;

    public String[] getImeiBlackListStatus() {
        return imeiBlackListStatus;
    }

    public List<Object> getImeiStolenHistory() {
        return imeiStolenHistory;
    }
}
