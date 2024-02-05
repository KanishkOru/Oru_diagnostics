package com.oruphones.nativediagnostic.webservices;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FileUploadResponse implements Serializable {

    @SerializedName("data")
    private FileUploadData data;

    @SerializedName("message")
    private String message;

    public FileUploadData getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getLink()
    {
        return getData().getFilePath();
    }

}
