package com.oruphones.nativediagnostic.models.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/*
For SSD
{"uniqueDeviceId":"359470085399915","storeId":"TFSSD_Colombia"}
for D2D
{"uniqueDeviceId":"356252070197782","customerUserId":"appstoreuser"}
* */
public class HistoryRequestDto {
    @Expose
    @SerializedName("uniqueDeviceId")
    private String uniqueDeviceId ;
    @Expose
    @SerializedName("storeId")
    private String storeId ;
    @Expose
    @SerializedName("platform")
    private String platform = "Android";



    @Expose
    @SerializedName("customerUserId")
    private String customerUserId ;

    public HistoryRequestDto() {
    }

    public HistoryRequestDto(String uniqueDeviceId, String storeOrAgentUserId) {
        this.uniqueDeviceId = uniqueDeviceId;
        this.storeId = storeOrAgentUserId;
        this.customerUserId = storeOrAgentUserId;
    }



}
