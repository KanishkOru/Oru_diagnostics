/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oruphones.nativediagnostic.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Pervacio
 */
public class PDAppResolutionInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appIcon="";
    private String appName;
    private String packageName="";
    private String versionName="";
    private String installer="";
    private String rcutimestamp="";
    private String appType="";
    private ArrayList<String> permision=new ArrayList<>();
    private ArrayList<String> permisionLevel=new ArrayList<>();
    private String lastUsed="";
    private String installedDate="";
    private String updatedDate="";
    private String appSizeKB="";
    private String malware=PDConstants.PDFALSE;
    private String riskyapp=PDConstants.PDFALSE;
    private String batteryconsumingapps=PDConstants.PDFALSE;
    private String addware=PDConstants.PDFALSE;
    private String bandwidthconsumingapp=PDConstants.PDFALSE;
    private String outdated=PDConstants.PDFALSE;
    private String checkForOutdated=PDConstants.PDTRUE;
    private String md5Digest="";
    private String[] diSpyCategory;
    private String[] diSpyName;
    private String diDetectRatio;
    private String[] justification;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public String getAppSizeKB() {
        return appSizeKB;
    }

    public void setAppSizeKB(String appSizeKB) {
        this.appSizeKB = appSizeKB;
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

    public String getMd5Digest() {
        return md5Digest;
    }

    public void setMd5Digest(String md5Digest) {
        this.md5Digest = md5Digest;
    }

	public String[] getDiSpyCategory() {
		return diSpyCategory;
	}

	public void setDiSpyCategory(String[] diSpyCategory) {
		this.diSpyCategory = diSpyCategory;
	}

	public String[] getDiSpyName() {
		return diSpyName;
	}

	public void setDiSpyName(String[] diSpyName) {
		this.diSpyName = diSpyName;
	}

	public String getDiDetectRatio() {
		return diDetectRatio;
	}

	public void setDiDetectRatio(String diDetectRatio) {
		this.diDetectRatio = diDetectRatio;
	}

	public String[] getJustification() {
		return justification;
	}

	public void setJustification(String[] justification) {
		this.justification = justification;
	}

	public String getCheckForOutdated() {
		return checkForOutdated;
	}

	public void setCheckForOutdated(String checkForOutdated) {
		this.checkForOutdated = checkForOutdated;
	}
}