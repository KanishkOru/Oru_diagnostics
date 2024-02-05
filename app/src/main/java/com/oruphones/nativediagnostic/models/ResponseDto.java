package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseDto<T> implements Serializable {

    @SerializedName("data")
    @Expose
    private T data;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sessionId")
    @Expose
    private long sessionId;

    public T getData() {
        return data;
    }

    public boolean isPassed() {
        return "PASS".equalsIgnoreCase(status);
    }


    public String getMessage() {
        return message;
    }


    public long getSessionId() {
        return sessionId;
    }
}
