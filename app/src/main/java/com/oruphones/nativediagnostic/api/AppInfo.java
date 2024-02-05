package com.oruphones.nativediagnostic.api;

import android.graphics.drawable.Drawable;

/**
 * Created by Pervacio on 10-09-2017.
 */

public class AppInfo {
    private String appName;
    private String packageName = "";
    private Drawable appIcon;
    private long installedDate;
    private String appSizeKB = "";
    private boolean isChecked;
    private String usedRamKB = "";

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getUsedRamKB() {
        return usedRamKB;
    }

    public void setUsedRamKB(String usedRamKB) {
        this.usedRamKB = usedRamKB;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(long installedDate) {
        this.installedDate = installedDate;
    }

    public String getAppSizeKB() {
        return appSizeKB;
    }

    public void setAppSizeKB(String appSizeKB) {
        this.appSizeKB = appSizeKB;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppInfo) {
            AppInfo appInfo = (AppInfo) obj;
            return this.packageName.equalsIgnoreCase(appInfo.packageName);
        }
        return false;
    }

    @Override
    public String toString() {
        return appName;
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

}
