package com.oruphones.nativediagnostic.Storage;

import android.graphics.drawable.Drawable;

import org.pervacio.onediaglib.internalstorage.AppPermissionInfo;

import java.util.ArrayList;

public class ApplicationData {

	private String appName;
	private String pkgName;
	private String version;
	private String installer;
	private String recentlyused;
	private String rcutimestamp;
	private String installedtime;
	private String lastupdatetime;
	private String type;
	private ArrayList<AppPermissionInfo> appPermissonInfo;
	private AppSize appSize;
	private boolean riskyapp;
	private boolean addWare;
	private boolean bandwidthconsumingapp;
	private boolean btryConsumingApps;
	private Drawable drawable;

	private boolean isChecked;

	public boolean isChecked() {
		return isChecked;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

    public ArrayList<AppPermissionInfo> getAppPermissonInfo() {
		return appPermissonInfo;
	}

	public boolean isRiskyapp() {
		return riskyapp;
	}

	public void setRiskyapp(boolean riskyapp) {
		this.riskyapp = riskyapp;
	}

	public boolean isAddWare() {
		return addWare;
	}

	public void setAddWare(boolean addWare) {
		this.addWare = addWare;
	}

	public boolean isBandwidthconsumingapp() {
		return bandwidthconsumingapp;
	}

	public void setBandwidthconsumingapp(boolean bandwidthconsumingapp) {
		this.bandwidthconsumingapp = bandwidthconsumingapp;
	}

	public boolean isBtryConsumingApps() {
		return btryConsumingApps;
	}

	public void setBtryConsumingApps(boolean btryConsumingApps) {
		this.btryConsumingApps = btryConsumingApps;
	}

	public void setAppPermissonInfo(
			ArrayList<AppPermissionInfo> appPermissonInfo) {
		this.appPermissonInfo = appPermissonInfo;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInstaller() {
		return installer;
	}

	public void setInstaller(String installer) {
		this.installer = installer;
	}

	public String getRecentlyused() {
		return recentlyused;
	}

	public void setRecentlyused(String recentlyused) {
		this.recentlyused = recentlyused;
	}

	public String getRcutimestamp() {
		return rcutimestamp;
	}

	public void setRcutimestamp(String rcutimestamp) {
		this.rcutimestamp = rcutimestamp;
	}

	public String getInstalledtime() {
		return installedtime;
	}

	public void setInstalledtime(String installedtime) {
		this.installedtime = installedtime;
	}

	public String getLastupdatetime() {
		return lastupdatetime;
	}

	public void setLastupdatetime(String lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AppSize getAppSize() {
		return appSize;
	}

	public void setAppSize(AppSize appSize) {
		this.appSize = appSize;
	}

	public ApplicationData() {

	}

}
