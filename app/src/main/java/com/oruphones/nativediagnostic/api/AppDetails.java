package com.oruphones.nativediagnostic.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppDetails implements Serializable {

	@SerializedName("packageName")
	private String packageName;
	@SerializedName("malware")
	private boolean malware;
	@SerializedName("md5Digest")
	private String md5Digest="";
	@SerializedName("riskyapp")
	private boolean riskyapp;
	@SerializedName("addware")
	private boolean addware;
	@SerializedName("outdated")
	private boolean outdated;
	@SerializedName("updatedDate")
	private long updatedDate;
	@SerializedName("diSpyName")
	private String[] diSpyName;
	@SerializedName("diSpyCategory")
	private String[] diSpyCategory;
	@SerializedName("diDetectRatio")
	private String diDetectRatio;
	@SerializedName("justification")
	private String[] justification;

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isMalware() {
		return malware;
	}
	public void setMalware(boolean malware) {
		this.malware = malware;
	}
	public String getMd5Digest() {
		return md5Digest;
	}
	public void setMd5Digest(String md5Digest) {
		this.md5Digest = md5Digest;
	}
	public boolean isRiskyapp() {
		return riskyapp;
	}
	public void setRiskyapp(boolean riskyapp) {
		this.riskyapp = riskyapp;
	}
	public boolean isAddware() {
		return addware;
	}
	public void setAddware(boolean addware) {
		this.addware = addware;
	}
	public String[] getDiSpyName() {
		return diSpyName;
	}
	public void setDiSpyName(String[] diSpyName) {
		this.diSpyName = diSpyName;
	}
	public String[] getDiSpyCategory() {
		return diSpyCategory;
	}
	public void setDiSpyCategory(String[] diSpyCategory) {
		this.diSpyCategory = diSpyCategory;
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
	public boolean isOutdated() {
		return outdated;
	}
	public void setOutdated(boolean outdated) {
		this.outdated = outdated;
	}
	public long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(long updatedDate) {
		this.updatedDate = updatedDate;
	}

}

