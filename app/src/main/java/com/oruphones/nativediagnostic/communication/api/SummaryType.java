package com.oruphones.nativediagnostic.communication.api;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SummaryType implements Serializable {

    @SerializedName("sessionId")
    private long sessionId;

    @SerializedName("locale")
    private String locale;

    @SerializedName("email")
    private String email;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getEmailId() {
        return email;
    }

    public void setEmailId(String emailId) {
        this.email = emailId;
    }

}
