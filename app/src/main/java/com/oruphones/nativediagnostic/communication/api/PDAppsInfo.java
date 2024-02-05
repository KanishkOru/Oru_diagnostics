package com.oruphones.nativediagnostic.communication.api;

public class PDAppsInfo implements Comparable<PDAppsInfo>{

	private String icon;
	private String appName;
	private String packageName;
	private String version;
	private String installer;
	private long rcutimestamp;
	private long lastUsed;
	private long updatedDate;
	private long size;
	private boolean malware;
	private boolean riskyapp;
	private boolean addware;
	private boolean outdated;
	private boolean unused;
	private String[] diSpyCategory;
	private String[] diSpyName;
	private String diDetectRatio;
	private String[] justification;


	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public long getRcutimestamp() {
		return rcutimestamp;
	}

	public void setRcutimestamp(long rcutimestamp) {
		this.rcutimestamp = rcutimestamp;
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	public long getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(long updatedDate) {
		this.updatedDate = updatedDate;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isMalware() {
		return malware;
	}

	public void setMalware(boolean malware) {
		this.malware = malware;
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

	public boolean isOutdated() {
		return outdated;
	}

	public void setOutdated(boolean outdated) {
		this.outdated = outdated;
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
	public boolean isUnused() {
		return unused;
	}

	public void setUnused(boolean unused) {
		this.unused = unused;
	}


	@Override
	public int compareTo(PDAppsInfo info) {
		return getPackageName().compareToIgnoreCase(info.getPackageName());
	}
}