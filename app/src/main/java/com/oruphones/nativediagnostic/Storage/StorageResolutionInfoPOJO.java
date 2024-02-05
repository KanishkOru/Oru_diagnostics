package com.oruphones.nativediagnostic.Storage;

import org.pervacio.onediaglib.internalstorage.ApplicationData;

import java.util.HashMap;

/**
 * Created by Venkatesh Pendlikal on 22-12-2015.
 */
public class StorageResolutionInfoPOJO {

    private double totalStorageKB;
    private double availableStorageKB;

    private double duplicateFilesKB;
    private double tempFilesKB;
    private double otherFilesKB;
    private double totalVideosKB;
    private double totalImagesKB;
    private double totalMusicKB;
    private double totalAppKB;

    private double beforeAvailableStorageKB;

    private double beforeDuplicateFilesKB;
    private double beforeTempFilesKB;
    private double beforeOtherFilesKB;
    private double beforeTotalVideosKB;
    private double beforeTotalImagesKB;
    private double beforeTotalMusicKB;
    private double beforeTotalAppKB;

    private int totalApps;
    private int totalImages;
    private int totalMusics;
    private int totalDuplicatedfiless;
    private int totalOtherFiless;
    private int totalVideos;


    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoDuplicateFileMap;
    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoTempFileMap;
    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoOtherFileMap;
    HashMap<String, ApplicationData> storageResolutionInfoAppsMap;
    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoVideosMap;
    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoImagesMap;
    HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoMusicMap;

    private int totalPaginationCount;
    private int currentPaginationCount;

    public double getBeforeAvailableStorageKB() {
        return beforeAvailableStorageKB;
    }

    public void setBeforeAvailableStorageKB(double beforeAvailableStorageKB) {
        this.beforeAvailableStorageKB = beforeAvailableStorageKB;
    }

    public double getBeforeDuplicateFilesKB() {
        return beforeDuplicateFilesKB;
    }

    public void setBeforeDuplicateFilesKB(double beforeDuplicateFilesKB) {
        this.beforeDuplicateFilesKB = beforeDuplicateFilesKB;
    }

    public double getBeforeTempFilesKB() {
        return beforeTempFilesKB;
    }

    public void setBeforeTempFilesKB(double beforeTempFilesKB) {
        this.beforeTempFilesKB = beforeTempFilesKB;
    }

    public double getBeforeOtherFilesKB() {
        return beforeOtherFilesKB;
    }

    public void setBeforeOtherFilesKB(double beforeOtherFilesKB) {
        this.beforeOtherFilesKB = beforeOtherFilesKB;
    }

    public double getBeforeTotalVideosKB() {
        return beforeTotalVideosKB;
    }

    public void setBeforeTotalVideosKB(double beforeTotalVideosKB) {
        this.beforeTotalVideosKB = beforeTotalVideosKB;
    }

    public double getBeforeTotalImagesKB() {
        return beforeTotalImagesKB;
    }

    public void setBeforeTotalImagesKB(double beforeTotalImagesKB) {
        this.beforeTotalImagesKB = beforeTotalImagesKB;
    }

    public double getBeforeTotalMusicKB() {
        return beforeTotalMusicKB;
    }

    public void setBeforeTotalMusicKB(double beforeTotalMusicKB) {
        this.beforeTotalMusicKB = beforeTotalMusicKB;
    }

    public double getBeforeTotalAppKB() {
        return beforeTotalAppKB;
    }

    public void setBeforeTotalAppKB(double beforeTotalAppKB) {
        this.beforeTotalAppKB = beforeTotalAppKB;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoDuplicateFileMap() {
        return storageResolutionInfoDuplicateFileMap;
    }

    public void setStorageResolutionInfoDuplicateFileMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoDuplicateFileMap) {
        this.storageResolutionInfoDuplicateFileMap = storageResolutionInfoDuplicateFileMap;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoTempFileMap() {
        return storageResolutionInfoTempFileMap;
    }

    public void setStorageResolutionInfoTempFileMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoTempFileMap) {
        this.storageResolutionInfoTempFileMap = storageResolutionInfoTempFileMap;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoOtherFileMap() {
        return storageResolutionInfoOtherFileMap;
    }

    public void setStorageResolutionInfoOtherFileMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoOtherFileMap) {
        this.storageResolutionInfoOtherFileMap = storageResolutionInfoOtherFileMap;
    }

    public HashMap<String, ApplicationData> getStorageResolutionInfoAppsMap() {
        return storageResolutionInfoAppsMap;
    }

    public void setStorageResolutionInfoAppsMap(HashMap<String, ApplicationData> storageResolutionInfoAppsMap) {
        this.storageResolutionInfoAppsMap = storageResolutionInfoAppsMap;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoVideosMap() {
        return storageResolutionInfoVideosMap;
    }

    public void setStorageResolutionInfoVideosMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoVideosMap) {
        this.storageResolutionInfoVideosMap = storageResolutionInfoVideosMap;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoImagesMap() {
        return storageResolutionInfoImagesMap;
    }

    public void setStorageResolutionInfoImagesMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoImagesMap) {
        this.storageResolutionInfoImagesMap = storageResolutionInfoImagesMap;
    }

    public HashMap<String, StorageResolutionFileInfoPOJO> getStorageResolutionInfoMusicMap() {
        return storageResolutionInfoMusicMap;
    }

    public void setStorageResolutionInfoMusicMap(HashMap<String, StorageResolutionFileInfoPOJO> storageResolutionInfoMusicMap) {
        this.storageResolutionInfoMusicMap = storageResolutionInfoMusicMap;
    }

    public double getDuplicateFilesKB() {
        return duplicateFilesKB;
    }

    public void setDuplicateFilesKB(double duplicateFilesKB) {
        this.duplicateFilesKB = duplicateFilesKB;
    }

    public double getTempFilesKB() {
        return tempFilesKB;
    }

    public void setTempFilesKB(double tempFilesKB) {
        this.tempFilesKB = tempFilesKB;
    }

    public double getOtherFilesKB() {
        return otherFilesKB;
    }

    public void setOtherFilesKB(double otherFilesKB) {
        this.otherFilesKB = otherFilesKB;
    }

    public double getTotalVideosKB() {
        return totalVideosKB;
    }

    public void setTotalVideosKB(double totalVideosKB) {
        this.totalVideosKB = totalVideosKB;
    }

    public double getTotalImagesKB() {
        return totalImagesKB;
    }

    public void setTotalImagesKB(double totalImagesKB) {
        this.totalImagesKB = totalImagesKB;
    }

    public double getTotalMusicKB() {
        return totalMusicKB;
    }

    public void setTotalMusicKB(double totalMusicKB) {
        this.totalMusicKB = totalMusicKB;
    }

    public int getTotalApps() {
        return totalApps;
    }

    public void setTotalApps(int totalApps) {
        this.totalApps = totalApps;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(int totalImages) {
        this.totalImages = totalImages;
    }

    public int getTotalMusics() {
        return totalMusics;
    }

    public void setTotalMusics(int totalMusics) {
        this.totalMusics = totalMusics;
    }

    public int getTotalDuplicatedfiless() {
        return totalDuplicatedfiless;
    }

    public void setTotalDuplicatedfiless(int totalDuplicatedfiless) {
        this.totalDuplicatedfiless = totalDuplicatedfiless;
    }

    public int getTotalOtherFiless() {
        return totalOtherFiless;
    }

    public void setTotalOtherFiless(int totalOtherFiless) {
        this.totalOtherFiless = totalOtherFiless;
    }

    public double getTotalAppKB() {
        return totalAppKB;
    }

    public void setTotalAppKB(double totalAppKB) {
        this.totalAppKB = totalAppKB;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    public int getTotalPaginationCount() {
        return totalPaginationCount;
    }

    public void setTotalPaginationCount(int totalPaginationCount) {
        this.totalPaginationCount = totalPaginationCount;
    }

    public int getCurrentPaginationCount() {
        return currentPaginationCount;
    }

    public void setCurrentPaginationCount(int currentPaginationCount) {
        this.currentPaginationCount = currentPaginationCount;
    }

    public double getTotalStorageKB() {
        return totalStorageKB;
    }

    public void setTotalStorageKB(double totalStorageKB) {
        this.totalStorageKB = totalStorageKB;
    }

    public double getAvailableStorageKB() {
        return availableStorageKB;
    }

    public void setAvailableStorageKB(double availableStorageKB) {
        this.availableStorageKB = availableStorageKB;
    }
}
