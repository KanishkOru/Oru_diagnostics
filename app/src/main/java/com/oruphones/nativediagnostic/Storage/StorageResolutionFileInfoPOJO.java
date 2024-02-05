package com.oruphones.nativediagnostic.Storage;


/**
 * Created by Satyanarayana Chidurala on 05-01-2016.
 */
public class StorageResolutionFileInfoPOJO {

    private String fileName;
    private String filePath;
    private String fileType;
    private String createdDate;
    private String fileSize;
    private boolean isChecked;
    private boolean isDeletable;
    private static int count;
    private static int videoFilesCount;
    private static int aftercount;
    private static int imageFilesCount;
    private static int afterimageFileCount;
    private static int audioFilesCount;
    private static int afteraudioFilesCount;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    private String fileKey;

    public boolean isDeletable() {
        return isDeletable;
    }
    public void setIsDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getBeforeDeletionVideoFilesCount(){
        return videoFilesCount;
    }
    public void setBeforeDeletionVideoFilesCount(int videoFilesCount){
        this.videoFilesCount = videoFilesCount;
    }


    public int getBeforeDeltionImageFilesCount(){
        return imageFilesCount;
    }
    public void setBeforeDeltionImageFilesCount(int imageFilesCount){
        this.imageFilesCount = imageFilesCount;
    }

    public int getAfterDeletionVideoFilesCount(){
        return aftercount;
    }
    public void setAfterDeletionVideoFilesCount(int aftercount){
        this.aftercount = aftercount;
    }



    public int getAfterDeltionImageFilesCount(){
        return afterimageFileCount;
    }
    public void setAfterDeltionImageFilesCount(int afterimageFileCount){
        this.afterimageFileCount = afterimageFileCount;
    }

    public int getBeforeDeltionAudioFilesCount(){
        return audioFilesCount;
    }
    public void setBeforeDeltionAudioFilesCount(int audioFilesCount){
        this.audioFilesCount = audioFilesCount;
    }

    public int getAfterDeltionAudioFilesCount(){
        return afteraudioFilesCount;
    }
    public void setAfterDeltionAudioFilesCount(int afteraudioFilesCount){
        this.afteraudioFilesCount = afteraudioFilesCount;
    }

}
