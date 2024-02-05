package com.oruphones.nativediagnostic.api;

import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.models.DiagConfiguration;


import java.io.Serializable;

/**
 * Created by Pervacio on 16-08-2017.
 */

public class ServerConfig implements Serializable {

    @SerializedName("data")
    private DiagConfiguration diagConfiguration;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("sessionId")
    private String sessionId;

    public DiagConfiguration getDiagConfiguration() {
        return diagConfiguration;
    }

    public void setDiagConfiguration(DiagConfiguration diagConfiguration) {
        this.diagConfiguration = diagConfiguration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
