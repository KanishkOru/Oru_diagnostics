package com.oruphones.nativediagnostic.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.webservices.APIClient;


/*

* */
public class CSATData {
    @SerializedName("customerRating")
    private String customerRating = "";
    @SerializedName("customerRatingReason")
    private String customerRatingReason = "";
    @SerializedName("agentRating")
    private String agentRating = "";
    @SerializedName("agentRatingReason")
    private String agentRatingReason = "";

    @SerializedName("sessionId")
    private long sessionId = -1;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(String customerRating) {
        this.customerRating = customerRating;
    }

    public String getCustomerRatingReason() {
        return customerRatingReason;
    }

    public void setCustomerRatingReason(String customerRatingReason) {
        this.customerRatingReason = customerRatingReason;
    }

    public String getAgentRating() {
        return agentRating;
    }

    public void setAgentRating(String agentRating) {
        this.agentRating = agentRating;
    }

    public String getAgentRatingReason() {
        return agentRatingReason;
    }

    public void setAgentRatingReason(String agentRatingReason) {
        this.agentRatingReason = agentRatingReason;
    }

    public JsonObject getJson(){
        JsonElement element = APIClient.getGson().toJsonTree(this);
        return element.getAsJsonObject();
    }

    @Override
    public String toString() {
        return "CSATData{" +
                "customerRating='" + customerRating + '\'' +
                ", customerRatingReason='" + customerRatingReason + '\'' +
                ", agentRating='" + agentRating + '\'' +
                ", agentRatingReason='" + agentRatingReason + '\'' +
                ", sessionId=" + sessionId +
                '}';
    }
}
