package com.oruphones.nativediagnostic.util;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Venkatesh Pendlikal on 17-12-2015.
 */
public class AppResolutionPojo {

    private int totalAppsFound;
    private int totalAppsIssuesFound;
    private int totalPaginationCount;
    private int currentPaginationCount;

    private HashMap<String, Integer> currentAppItem;
    private Drawable drawable;
    private String appIcon;
    private String appName;
    private String packageName;
    private String versionName;
    private String installer;
    private String rcutimestamp;
    private String appType;
    private ArrayList<String> permision;
    private ArrayList<String> permisionLevel;
    private String lastUsed;
    private String installedDate;
    private String updatedDate;
    private String appSize;
    private String malware;
    private String riskyapp;
    private String batteryconsumingapps;
    private String addware;
    private String bandwidthconsumingapp;
    private String outdated;
    private String recentlyused;


    private String[] diSpyCategory;
    private String[] diSpyName;
    private String diDetectRatio;
    private String[] justification;

    private int totalRiskyApps;
    private int totalBatteryConsumingApps;
    private int totalBatBatteryConsumingApps;
    private int totalCrashedApps;
    private int totalMalwareApps;
    private int totalAdwareAppss;
    private int totalBandwidthApps;
    private int totalOutdatedApps;
    private int totalLastUsedApps;

    private int totalUninstallRiskyApps;
    private int totalUninstallBatteryConsumingApps;
    private int totalBatteryUninstallBatteryConsumingApps;
    private int totalUninstallCrashedApps;
    private int totalUninstallMalwareApps;
    private int totalUninstallAdwareAppss;
    private int totalUninstallBandwidthApps;
    private int totalUninstallOutdatedApps;
    private int totalUninstallLastUsedApps;

    private int before_totalUninstallRiskyApps;
    private int before_totalUninstallBatteryConsumingApps;
    private int before_batteryUninstallBatteryConsumingApps;
    private int before_totalUninstallCrashedApps;
    private int before_totalUninstallMalwareApps;
    private int before_totalUninstallAdwareAppss;
    private int before_totalUninstallBandwidthApps;
    private int before_totalUninstallOutdatedApps;
    private int before_totalUninstallLastUsedApps;
    private int before_totalAppsIssuesFound;
    private boolean isChecked;

    ArrayList<AppResolutionPojo> appResolutionMalware;
    ArrayList<AppResolutionPojo> appResolutionRisky;
    ArrayList<AppResolutionPojo> appResolutionBatteryConsumin;
    ArrayList<AppResolutionPojo> batteryResolutionBatteryConsumin;
    ArrayList<AppResolutionPojo> appResolutionCrashed;
    ArrayList<AppResolutionPojo> appResolutionAdware;
    ArrayList<AppResolutionPojo> appResolutionBandwidth;
    ArrayList<AppResolutionPojo> appResolutionOutdated;
    ArrayList<AppResolutionPojo> appResolutionLastUsed;

    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapMalware ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapRisky ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapBatteryConsumin ;
    HashMap<String, AppResolutionPojo> batteryResolutionPojoHashMapBatteryConsumin ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapCrashed ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapAdware ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapBandwidth ;
    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapLastUsed ;

    public int getBefore_totalAppsIssuesFound() {
        return before_totalAppsIssuesFound;
    }

    public void setBefore_totalAppsIssuesFound(int before_totalAppsIssuesFound) {
        this.before_totalAppsIssuesFound = before_totalAppsIssuesFound;
    }

    private Double TotalAppSizeKB=0.0;


    public String getRecentlyused() {
        return recentlyused;
    }

    public void setRecentlyused(String recentlyused) {
        this.recentlyused = recentlyused;
    }


    public int getTotalLastUsedApps() {
        return totalLastUsedApps;
    }

    public void setTotalLastUsedApps(int totalLastUsedApps) {
        this.totalLastUsedApps = totalLastUsedApps;
    }

    public int getTotalUninstallLastUsedApps() {
        return totalUninstallLastUsedApps;
    }

    public void setTotalUninstallLastUsedApps(int totalUninstallLastUsedApps) {
        this.totalUninstallLastUsedApps = totalUninstallLastUsedApps;
    }

    public int getBefore_totalUninstallLastUsedApps() {
        return before_totalUninstallLastUsedApps;
    }

    public void setBefore_totalUninstallLastUsedApps(int before_totalUninstallLastUsedApps) {
        this.before_totalUninstallLastUsedApps = before_totalUninstallLastUsedApps;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionLastUsed() {
        return appResolutionLastUsed;
    }

    public void setAppResolutionLastUsed(ArrayList<AppResolutionPojo> appResolutionLastUsed) {
        this.appResolutionLastUsed = appResolutionLastUsed;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapLastUsed() {
        return appResolutionPojoHashMapLastUsed;
    }

    public void setAppResolutionPojoHashMapLastUsed(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapLastUsed) {
        this.appResolutionPojoHashMapLastUsed = appResolutionPojoHashMapLastUsed;
    }

    public int getBefore_totalUninstallRiskyApps() {
        return before_totalUninstallRiskyApps;
    }

    public void setBefore_totalUninstallRiskyApps(int before_totalUninstallRiskyApps) {
        this.before_totalUninstallRiskyApps = before_totalUninstallRiskyApps;
    }

    public int getBefore_totalUninstallBatteryConsumingApps() {
        return before_totalUninstallBatteryConsumingApps;
    }

    public void setBefore_totalUninstallBatteryConsumingApps(int before_totalUninstallBatteryConsumingApps) {
        this.before_totalUninstallBatteryConsumingApps = before_totalUninstallBatteryConsumingApps;
    }

    public int getBefore_totalUninstallCrashedApps() {
        return before_totalUninstallCrashedApps;
    }

    public void setBefore_totalUninstallCrashedApps(int before_totalUninstallCrashedApps) {
        this.before_totalUninstallCrashedApps = before_totalUninstallCrashedApps;
    }

    public int getBefore_totalUninstallMalwareApps() {
        return before_totalUninstallMalwareApps;
    }

    public void setBefore_totalUninstallMalwareApps(int before_totalUninstallMalwareApps) {
        this.before_totalUninstallMalwareApps = before_totalUninstallMalwareApps;
    }

    public int getBefore_totalUninstallAdwareAppss() {
        return before_totalUninstallAdwareAppss;
    }

    public void setBefore_totalUninstallAdwareAppss(int before_totalUninstallAdwareAppss) {
        this.before_totalUninstallAdwareAppss = before_totalUninstallAdwareAppss;
    }

    public int getBefore_totalUninstallBandwidthApps() {
        return before_totalUninstallBandwidthApps;
    }

    public void setBefore_totalUninstallBandwidthApps(int before_totalUninstallBandwidthApps) {
        this.before_totalUninstallBandwidthApps = before_totalUninstallBandwidthApps;
    }

    public int getBefore_totalUninstallOutdatedApps() {
        return before_totalUninstallOutdatedApps;
    }

    public void setBefore_totalUninstallOutdatedApps(int before_totalUninstallOutdatedApps) {
        this.before_totalUninstallOutdatedApps = before_totalUninstallOutdatedApps;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapOutdated() {
        return appResolutionPojoHashMapOutdated;
    }

    public void setAppResolutionPojoHashMapOutdated(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapOutdated) {
        this.appResolutionPojoHashMapOutdated = appResolutionPojoHashMapOutdated;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapMalware() {
        return appResolutionPojoHashMapMalware;
    }

    public void setAppResolutionPojoHashMapMalware(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapMalware) {
        this.appResolutionPojoHashMapMalware = appResolutionPojoHashMapMalware;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapRisky() {
        return appResolutionPojoHashMapRisky;
    }

    public void setAppResolutionPojoHashMapRisky(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapRisky) {
        this.appResolutionPojoHashMapRisky = appResolutionPojoHashMapRisky;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapBatteryConsumin() {
        return appResolutionPojoHashMapBatteryConsumin;
    }

    public void setAppResolutionPojoHashMapBatteryConsumin(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapBatteryConsumin) {
        this.appResolutionPojoHashMapBatteryConsumin = appResolutionPojoHashMapBatteryConsumin;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapCrashed() {
        return appResolutionPojoHashMapCrashed;
    }

    public void setAppResolutionPojoHashMapCrashed(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapCrashed) {
        this.appResolutionPojoHashMapCrashed = appResolutionPojoHashMapCrashed;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapAdware() {
        return appResolutionPojoHashMapAdware;
    }

    public void setAppResolutionPojoHashMapAdware(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapAdware) {
        this.appResolutionPojoHashMapAdware = appResolutionPojoHashMapAdware;
    }

    public HashMap<String, AppResolutionPojo> getAppResolutionPojoHashMapBandwidth() {
        return appResolutionPojoHashMapBandwidth;
    }

    public void setAppResolutionPojoHashMapBandwidth(HashMap<String, AppResolutionPojo> appResolutionPojoHashMapBandwidth) {
        this.appResolutionPojoHashMapBandwidth = appResolutionPojoHashMapBandwidth;
    }

    HashMap<String, AppResolutionPojo> appResolutionPojoHashMapOutdated ;
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

    public int getTotalAppsIssuesFound() {
        return totalAppsIssuesFound;
    }

    public void setTotalAppsIssuesFound(int totalAppsIssuesFound) {
        this.totalAppsIssuesFound = totalAppsIssuesFound;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionMalware() {
        return appResolutionMalware;
    }

    public void setAppResolutionMalware(ArrayList<AppResolutionPojo> appResolutionMalware) {
        this.appResolutionMalware = appResolutionMalware;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionRisky() {
        return appResolutionRisky;
    }

    public void setAppResolutionRisky(ArrayList<AppResolutionPojo> appResolutionRisky) {
        this.appResolutionRisky = appResolutionRisky;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionBatteryConsumin() {
        return appResolutionBatteryConsumin;
    }

    public void setAppResolutionBatteryConsumin(ArrayList<AppResolutionPojo> appResolutionBatteryConsumin) {
        this.appResolutionBatteryConsumin = appResolutionBatteryConsumin;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionCrashed() {
        return appResolutionCrashed;
    }

    public void setAppResolutionCrashed(ArrayList<AppResolutionPojo> appResolutionCrashed) {
        this.appResolutionCrashed = appResolutionCrashed;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionAdware() {
        return appResolutionAdware;
    }

    public void setAppResolutionAdware(ArrayList<AppResolutionPojo> appResolutionAdware) {
        this.appResolutionAdware = appResolutionAdware;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionBandwidth() {
        return appResolutionBandwidth;
    }

    public void setAppResolutionBandwidth(ArrayList<AppResolutionPojo> appResolutionBandwidth) {
        this.appResolutionBandwidth = appResolutionBandwidth;
    }

    public ArrayList<AppResolutionPojo> getAppResolutionOutdated() {
        return appResolutionOutdated;
    }

    public void setAppResolutionOutdated(ArrayList<AppResolutionPojo> appResolutionOutdated) {
        this.appResolutionOutdated = appResolutionOutdated;
    }

    public Double getTotalAppSizeKB() {
        return TotalAppSizeKB;
    }

    public void setTotalAppSizeKB(Double TotalAppSizeKB) {
        this.TotalAppSizeKB = TotalAppSizeKB;
    }

    public int getTotalUninstallRiskyApps() {
        return totalUninstallRiskyApps;
    }

    public void setTotalUninstallRiskyApps(int totalUninstallRiskyApps) {
        this.totalUninstallRiskyApps = totalUninstallRiskyApps;
    }

    public int getTotalUninstallBatteryConsumingApps() {
        return totalUninstallBatteryConsumingApps;
    }

    public void setTotalUninstallBatteryConsumingApps(int totalUninstallBatteryConsumingApps) {
        this.totalUninstallBatteryConsumingApps = totalUninstallBatteryConsumingApps;
    }

    public int getTotalUninstallCrashedApps() {
        return totalUninstallCrashedApps;
    }

    public void setTotalUninstallCrashedApps(int totalUninstallCrashedApps) {
        this.totalUninstallCrashedApps = totalUninstallCrashedApps;
    }

    public int getTotalUninstallMalwareApps() {
        return totalUninstallMalwareApps;
    }

    public void setTotalUninstallMalwareApps(int totalUninstallMalwareApps) {
        this.totalUninstallMalwareApps = totalUninstallMalwareApps;
    }

    public int getTotalUninstallAdwareAppss() {
        return totalUninstallAdwareAppss;
    }

    public void setTotalUninstallAdwareAppss(int totalUninstallAdwareAppss) {
        this.totalUninstallAdwareAppss = totalUninstallAdwareAppss;
    }

    public int getTotalUninstallBandwidthApps() {
        return totalUninstallBandwidthApps;
    }

    public void setTotalUninstallBandwidthApps(int totalUninstallBandwidthApps) {
        this.totalUninstallBandwidthApps = totalUninstallBandwidthApps;
    }

    public int getTotalUninstallOutdatedApps() {
        return totalUninstallOutdatedApps;
    }

    public void setTotalUninstallOutdatedApps(int totalUninstallOutdatedApps) {
        this.totalUninstallOutdatedApps = totalUninstallOutdatedApps;
    }

    public int getTotalRiskyApps() {
        return totalRiskyApps;
    }

    public void setTotalRiskyApps(int totalRiskyApps) {
        this.totalRiskyApps = totalRiskyApps;
    }

    public int getTotalBatteryConsumingApps() {
        return totalBatteryConsumingApps;
    }

    public void setTotalConsumingBatteryApps(int totalBatteryConsumingApps) {
        this.totalBatteryConsumingApps = totalBatteryConsumingApps;
    }

    public int getTotalCrashedApps() {
        return totalCrashedApps;
    }

    public void setTotalCrashedApps(int totalCrashedApps) {
        this.totalCrashedApps = totalCrashedApps;
    }

    public int getTotalMalwareApps() {
        return totalMalwareApps;
    }

    public void setTotalMalwareApps(int totalMalwareApps) {
        this.totalMalwareApps = totalMalwareApps;
    }

    public int getTotalAdwareAppss() {
        return totalAdwareAppss;
    }

    public void setTotalAdwareAppss(int totalAdwareAppss) {
        this.totalAdwareAppss = totalAdwareAppss;
    }

    public int getTotalBandwidthApps() {
        return totalBandwidthApps;
    }

    public void setTotalBandwidthApps(int totalBandwidthApps) {
        this.totalBandwidthApps = totalBandwidthApps;
    }

    public int getTotalOutdatedApps() {
        return totalOutdatedApps;
    }

    public void setTotalOutdatedApps(int totalOutdatedApps) {
        this.totalOutdatedApps = totalOutdatedApps;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }

    public String getRcutimestamp() {
        return rcutimestamp;
    }

    public void setRcutimestamp(String rcutimestamp) {
        this.rcutimestamp = rcutimestamp;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public ArrayList<String> getPermision() {
        return permision;
    }

    public void setPermision(ArrayList<String> permision) {
        this.permision = permision;
    }

    public ArrayList<String> getPermisionLevel() {
        return permisionLevel;
    }

    public void setPermisionLevel(ArrayList<String> permisionLevel) {
        this.permisionLevel = permisionLevel;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(String installedDate) {
        this.installedDate = installedDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getMalware() {
        return malware;
    }

    public void setMalware(String malware) {
        this.malware = malware;
    }

    public String getRiskyapp() {
        return riskyapp;
    }

    public void setRiskyapp(String riskyapp) {
        this.riskyapp = riskyapp;
    }

    public String getBatteryconsumingapps() {
        return batteryconsumingapps;
    }

    public void setBatteryconsumingapps(String batteryconsumingapps) {
        this.batteryconsumingapps = batteryconsumingapps;
    }

    public String getAddware() {
        return addware;
    }

    public void setAddware(String addware) {
        this.addware = addware;
    }

    public String getBandwidthconsumingapp() {
        return bandwidthconsumingapp;
    }

    public void setBandwidthconsumingapp(String bandwidthconsumingapp) {
        this.bandwidthconsumingapp = bandwidthconsumingapp;
    }

    public String getOutdated() {
        return outdated;
    }

    public void setOutdated(String outdated) {
        this.outdated = outdated;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getTotalAppsFound() {
        return totalAppsFound;
    }

    public void setTotalAppsFound(int totalAppsFound) {
        this.totalAppsFound = totalAppsFound;
    }

    public HashMap<String, Integer> getCurrentAppItem() {
        return currentAppItem;
    }

    public Integer getCurrentAppItemStatus(String Key) {
        return  this.currentAppItem.get(Key);
    }

    public void setCurrentAppItem(HashMap<String, Integer> currentAppItem) {
        this.currentAppItem = currentAppItem;
    }

    public Boolean checkAll(boolean check){
        return true;
    }

    public int getBefore_batteryUninstallBatteryConsumingApps() {
        return before_batteryUninstallBatteryConsumingApps;
    }

    public void setBefore_batteryUninstallBatteryConsumingApps(int before_batteryUninstallBatteryConsumingApps) {
        this.before_batteryUninstallBatteryConsumingApps = before_batteryUninstallBatteryConsumingApps;
    }

    public ArrayList<AppResolutionPojo> getBatteryResolutionBatteryConsumin() {
        return batteryResolutionBatteryConsumin;
    }

    public void setBatteryResolutionBatteryConsumin(ArrayList<AppResolutionPojo> batteryResolutionBatteryConsumin) {
        this.batteryResolutionBatteryConsumin = batteryResolutionBatteryConsumin;
    }

    public HashMap<String, AppResolutionPojo> getBatteryResolutionPojoHashMapBatteryConsumin() {
        return batteryResolutionPojoHashMapBatteryConsumin;
    }

    public void setBatteryResolutionPojoHashMapBatteryConsumin(HashMap<String, AppResolutionPojo> batteryResolutionPojoHashMapBatteryConsumin) {
        this.batteryResolutionPojoHashMapBatteryConsumin = batteryResolutionPojoHashMapBatteryConsumin;
    }

    public int getTotalBatBatteryConsumingApps() {
        return totalBatBatteryConsumingApps;
    }

    public void setTotalBatBatteryConsumingApps(int totalBatBatteryConsumingApps) {
        this.totalBatBatteryConsumingApps = totalBatBatteryConsumingApps;
    }

    public int getTotalBatteryUninstallBatteryConsumingApps() {
        return totalBatteryUninstallBatteryConsumingApps;
    }

    public void setTotalBatteryUninstallBatteryConsumingApps(int totalBatteryUninstallBatteryConsumingApps) {
        this.totalBatteryUninstallBatteryConsumingApps = totalBatteryUninstallBatteryConsumingApps;
    }

    public String[] getDiSpyCategory() {
        return this.diSpyCategory;
    }

    public void setDiSpyCategory(String[] diSpyCategory) {
        this.diSpyCategory = diSpyCategory;
    }

    public String[] getDiSpyName() {
        return this.diSpyName;
    }

    public void setDiSpyName(String[] diSpyName) {
        this.diSpyName = diSpyName;
    }

    public String getDiDetectRatio() {
        return this.diDetectRatio;
    }

    public void setDiDetectRatio(String diDetectRatio) {
        this.diDetectRatio = diDetectRatio;
    }

    public String[] getJustification() {
        return this.justification;
    }

    public void setJustification(String[] justification) {
        this.justification = justification;
    }
}
