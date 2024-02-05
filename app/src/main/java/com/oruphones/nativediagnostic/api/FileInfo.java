package com.oruphones.nativediagnostic.api;

import java.io.Serializable;

/**
 * Created by Pervacio on 23-09-2017.
 */

public class FileInfo implements Serializable {
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public double getFileSizeInDouble() {
        if (getFileSize() != null) {
            try {
                return Double.parseDouble(getFileSize());
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return 0;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String fileName;
    private String filePath;
    private String fileType;
    private String createdDate;
    private String fileSize;
    private boolean isChecked;
    private String key;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileInfo) {
            FileInfo fileInfo = (FileInfo) obj;
            return this.filePath.equalsIgnoreCase(fileInfo.filePath);
        }
        return false;
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}
