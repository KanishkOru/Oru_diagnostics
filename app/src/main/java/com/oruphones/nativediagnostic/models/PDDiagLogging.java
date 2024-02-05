package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PDDiagLogging implements Serializable {

	@SerializedName("serialVersionUID")
	private static final long serialVersionUID = 1L;

	@SerializedName("sessionId")
	private long sessionId;
	@SerializedName("certified")
	private boolean certified;
	@SerializedName("companyName")
	private String companyName;
	@SerializedName("storeId")
	private String storeId;
	@SerializedName("userName")
	private String userName;
	@SerializedName("productName")
	private String productName;
	@SerializedName("deviceUniqueId")
	private String deviceUniqueId;
	@SerializedName("serialNumber")
	private String serialNumber;
	@SerializedName("applicationVersion")
	private String applicationVersion;
	@SerializedName("make")
	private String make;
	@SerializedName("model")
	private String model;
	@SerializedName("carriers")
	private String carriers;
	@SerializedName("firmware")
	private String firmware;
	@SerializedName("transactionName")
	private String transactionName;
	@SerializedName("platform")
	private String platform;
	@SerializedName("startDateTime")
	private Long startDateTime;;
	@SerializedName("endDateTime")
	private Long endDateTime;
	@SerializedName("sesionStatus")
	private String sesionStatus;
	@SerializedName("deviceStatus")
	private String deviceStatus;
	@SerializedName("categoryName")
	private String categoryName;
	@SerializedName("marketingName")
	private String marketingName;
	@SerializedName("transactionType")
	private String transactionType;
	@SerializedName("storageCapacity")
	private String storageCapacity;
	@SerializedName("stageSessionStatus")
	private String stageSessionStatus;
	@SerializedName("commandDetails")
	private List<PDCommandDetails> commandDetails;
	@SerializedName("ranNumber")
	private String ranNumber;

	@SerializedName("osVersion")
	private String osVersion;
	@SerializedName("lastRestart")
	private Long lastRestart;
	@SerializedName("systemLogs")
	private String systemLogs;
	//AbortReason
	@SerializedName("abortReason")
	private AbortReasons abortReason;
	@SerializedName("locksRemoved")
	private String locksRemoved;


	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeviceUniqueId() {
		return deviceUniqueId;
	}
	public void setDeviceUniqueId(String deviceUniqueId) {
		this.deviceUniqueId = deviceUniqueId;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCarriers() {
		return carriers;
	}
	public void setCarriers(String carriers) {
		this.carriers = carriers;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public String getTransactionName() {
		return transactionName;
	}
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public List<PDCommandDetails> getCommandDetails() {
		return commandDetails;
	}
	public void setCommandDetails(List<PDCommandDetails> commandDetails) {
		this.commandDetails = commandDetails;
	}
	public void setStartDateTime(long startDateTime) {
		this.startDateTime = startDateTime;
	}

	public void setEndDateTime(long endDateTime) {
		this.endDateTime = endDateTime;
	}
	public String getSesionStatus() {
		return sesionStatus;
	}
	public void setSesionStatus(String sesionStatus) {
		this.sesionStatus = sesionStatus;
	}
	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getMarketingName() {
		return marketingName;
	}
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	public String getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getStorageCapacity() {
		return storageCapacity;
	}
	public void setStorageCapacity(String storageCapacity) {
		this.storageCapacity = storageCapacity;
	}
	public String getStageSessionStatus() {
		return stageSessionStatus;
	}
	public void setStageSessionStatus(String stageSessionStatus) {
		this.stageSessionStatus = stageSessionStatus;
	}
	public String getStoreId() {
		return storeId;
	}
	public String getSystemLogs() {
		return systemLogs;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public boolean isCertified() {
		return certified;
	}

	public void setCertified(boolean certified) {
		this.certified = certified;
	}
	public String getRanNumber() {
		return ranNumber;
	}

	public void setRanNumber(String ranNumber) {
		this.ranNumber = ranNumber;
	}


	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public void setLastRestart(long lastRestart) {
		this.lastRestart = lastRestart;
	}

	public void setAbortReason(AbortReasons abortReason) {
		this.abortReason = abortReason;
	}

	public void setAllLocksRemoved(String allLocksRemoved) {
		this.locksRemoved = allLocksRemoved;
	}
	public void setSystemLogs(String systemLogs) {
		this.systemLogs = systemLogs;
	}
	@Override
	public String toString() {
		return "PDDiagLogging{" +
				"sessionId=" + sessionId +
				", certified=" + certified +
				", companyName='" + companyName + '\'' +
				", storeId='" + storeId + '\'' +
				", userName='" + userName + '\'' +
				", productName='" + productName + '\'' +
				", deviceUniqueId='" + deviceUniqueId + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				", applicationVersion='" + applicationVersion + '\'' +
				", make='" + make + '\'' +
				", model='" + model + '\'' +
				", carriers='" + carriers + '\'' +
				", firmware='" + firmware + '\'' +
				", transactionName='" + transactionName + '\'' +
				", platform='" + platform + '\'' +
				", startDateTime=" + startDateTime +
				", endDateTime=" + endDateTime +
				", sesionStatus='" + sesionStatus + '\'' +
				", deviceStatus='" + deviceStatus + '\'' +
				", categoryName='" + categoryName + '\'' +
				", marketingName='" + marketingName + '\'' +
				", transactionType='" + transactionType + '\'' +
				", storageCapacity='" + storageCapacity + '\'' +
				", stageSessionStatus='" + stageSessionStatus + '\'' +
				", commandDetails=" + commandDetails +
				", ranNumber='" + ranNumber + '\'' +
				", osVersion='" + osVersion + '\'' +
				", lastRestart='" + lastRestart + '\'' +
				", abortReason='" + abortReason + '\'' +
				", systemLogs='" + systemLogs + '\'' +
				'}';
	}
}
