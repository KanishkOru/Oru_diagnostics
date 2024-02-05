package com.oruphones.nativediagnostic.webservices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FileUploadData implements Serializable {

    @SerializedName("filePath")
    @Expose
    private String filePath;

    @SerializedName("fileKey")
    @Expose
    private String fileKey;

    // Add other properties as needed

    public String getFilePath() {
        return filePath;
    }

    public String getFileKey() {
        return fileKey;
    }

    // Add getters for other properties

}
