package com.oruphones.nativediagnostic.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeviceInformation implements Serializable {
    @SerializedName("serialVersionUID")
    private static final long serialVersionUID = 1L;
    @SerializedName("deviceId")
    private String deviceId = "";
    @SerializedName("serialNo")
    private String serialNo = "";
    @SerializedName("make")
    private String make = "";
    @SerializedName("model")
    private String model = "";
    @SerializedName("genuineOS")
    private boolean genuineOS = true;
    @SerializedName("firmware")
    private String firmware = "";
    @SerializedName("buildnumber")
    private String buildnumber = "";
    @SerializedName("osVersion")
    private String osVersion = "";
    @SerializedName("platform")
    private String platform = "";
    @SerializedName("apilevel")
    private String apilevel = "";
    @SerializedName("batteryType")
    private String batteryType = "";
    @SerializedName("batteryLevel")
    private int batteryLevel = 0;
    @SerializedName("batteryHealth")
    private String batteryHealth = "";
    @SerializedName("batterySOH")
    private double batterySOH;
    @SerializedName("batteryFullChargeCapacity")
    private long batteryFullChargeCapacity;
    @SerializedName("batteryPlugged")
    private String batteryPlugged;
    @SerializedName("batteryCharging")
    private boolean batteryCharging = false;
    @SerializedName("batteryTemperature")
    private double batteryTemperature;
    @SerializedName("batteryVoltage")
    private int batteryVoltage;
    @SerializedName("batteryDesignCapacityQuick")
    private int batteryDesignCapacityQuick;
    @SerializedName("carriers")
    private String carriers = "";
    @SerializedName("countryCode")
    private String countryCode = "";
    @SerializedName("storeId")
    private String storeId = "";
    @SerializedName("appSubMode")
    private String appSubMode = "";
    @SerializedName("deviceLocale")
    private String deviceLocale = "en";
    @SerializedName("appVersion")
    private String appVersion = "";
    @SerializedName("deviceStorageCapacity")
    private long deviceStorageCapacity = 0;
    @SerializedName("avlRAM")
    private long avlRAM = 0;
    @SerializedName("totalRAM")
    private long totalRAM = 0;
    @SerializedName("avlInternalStorage")
    private long avlInternalStorage = 0;
    @SerializedName("totalInternalStorage")
    private long totalInternalStorage = 0;
    @SerializedName("lastRestart")
    private long lastRestart = 0;
    @SerializedName("batteryMaxCapacity")
    private int batteryMaxCapacity;

    @SerializedName("transactionName")
    private String transactionName;

    public String getConnectedNetworkType() {
        return connectedNetworkType;
    }

    public void setConnectedNetworkType(String connectedNetworkType) {
        this.connectedNetworkType = connectedNetworkType;
    }

    public String getSimSlot1() {
        return simSlot1;
    }

    public void setSimSlot1(String simSlot1) {
        this.simSlot1 = simSlot1;
    }

    public String getSimSlot2() {
        return simSlot2;
    }

    public void setSimSlot2(String simSlot2) {
        this.simSlot2 = simSlot2;
    }

    public String getDefaultMobileData() {
        return defaultMobileData;
    }

    public void setDefaultMobileData(String defaultMobileData) {
        this.defaultMobileData = defaultMobileData;
    }

    public String getSim1ICCID() {
        return sim1ICCID;
    }

    public void setSim1ICCID(String sim1ICCID) {
        this.sim1ICCID = sim1ICCID;
    }

    public String getSim2ICCID() {
        return sim2ICCID;
    }

    public void setSim2ICCID(String sim2ICCID) {
        this.sim2ICCID = sim2ICCID;
    }

    public boolean isAirplaneMode() {
        return airplaneMode;
    }

    public void setAirplaneMode(boolean airplaneMode) {
        this.airplaneMode = airplaneMode;
    }

    public boolean isRoamingMobileData() {
        return roamingMobileData;
    }

    public void setRoamingMobileData(boolean roamingMobileData) {
        this.roamingMobileData = roamingMobileData;
    }

    @SerializedName("connectedNetworkType")
    private String connectedNetworkType;
    @SerializedName("simSlot1")
    private String simSlot1;
    @SerializedName("simSlot2")
    private String simSlot2;
    @SerializedName("defaultMobileData")
    private String defaultMobileData;
    @SerializedName("sim1ICCID")
    private String sim1ICCID;
    @SerializedName("sim2ICCID")
    private String sim2ICCID;

    @SerializedName("airplaneMode")
    private boolean airplaneMode = false;
    @SerializedName("mobileData")
    private boolean mobileData = false;
    @SerializedName("roamingMobileData")
    private boolean roamingMobileData = false;

    /**/
    /*New Implementation*/
    private String apn;
    //private boolean mobileData;
    private boolean wifi;
    private String networkMode;
    private String volteCalls;

    @SerializedName("availableRearCams")
    private int availableRearCams = 0;
    @SerializedName("availableFrontCams")
    private int availableFrontCams = 0;
    @SerializedName("unavailableFeatures")
    private String[] unavailableFeatures = {"SPen",
            "CameraFlash",
            "FrontFacingCamera",
            "Telephony",
            "Vibration",
            "GyroscopeSensor",
            "MagneticSensor",
            "ProximitySensor",
            "AccelerometerSensor",
            "LightSensor",
            "BarometerSensor",
            "AmbientTemperatureSensor",
            "RelativeHumiditySensor",
            "WiFi",
            "NFC",
            "Bluetooth",
            "GPS",
            "FingerPrintSensor",
            "RearCamera",
            "SoftKeys",
            "SdCardSlot",
            "SdCard",
            "Receiver",
            "DeviceCharging",
            "Call",
            "LTESupported",
            "SIM"
    };


    public DeviceInformation() {
        //        TODO: uncomment below for previous change.
//        this.transactionName= BuildConfig.PRODUCT_NAME;
    }

    public DeviceInformation(String make, String model, String firmware, String osVersion, String deviceId) {
        this();
        this.make = make;
        this.model = model;
        this.firmware = firmware;
        this.osVersion = osVersion;
        this.deviceId = deviceId;
    }

    public void setTransectionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public int getAvailableRearCams() {
        return availableRearCams;
    }

    public void setAvailableRearCams(int availableRearCams) {
        this.availableRearCams = availableRearCams;
    }

    public int getAvailableFrontCams() {
        return availableFrontCams;
    }

    public void setAvailableFrontCams(int availableFrontCams) {
        this.availableFrontCams = availableFrontCams;
    }

    public long getLastRestart() {
        return lastRestart;
    }

    public void setLastRestart(long lastRestarted) {
        this.lastRestart = lastRestarted;
    }

    public long getAvlRAM() {
        return avlRAM;
    }

    public void setAvlRAM(long avlRAM) {
        this.avlRAM = avlRAM;
    }

    public long getTotalRAM() {
        return totalRAM;
    }

    public void setTotalRAM(long totalRAM) {
        this.totalRAM = totalRAM;
    }

    public long getAvlInternalStorage() {
        return avlInternalStorage;
    }

    public void setAvlInternalStorage(long avlInternalStorage) {
        this.avlInternalStorage = avlInternalStorage;
    }

    public long getTotalInternalStorage() {
        return totalInternalStorage;
    }

    public void setTotalInternalStorage(long totalInternalStorage) {
        this.totalInternalStorage = totalInternalStorage;
    }

    public String getImei() {
        return deviceId;
    }

    public void setImei(String imei) {
        this.deviceId = imei;
    }

    public String getSerialno() {
        return serialNo;
    }

    public void setSerialno(String serialNo) {
        this.serialNo = serialNo;
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

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getBuildnumber() {
        return buildnumber;
    }

    public void setBuildnumber(String buildnumber) {
        this.buildnumber = buildnumber;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getApilevel() {
        return apilevel;
    }

    public void setApilevel(String apilevel) {
        this.apilevel = apilevel;
    }

    public String getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(String batteryType) {
        this.batteryType = batteryType;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getCarriers() {
        return carriers;
    }

    public void setCarriers(String carriers) {
        this.carriers = carriers;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAppSubMode() {
        return appSubMode;
    }

    public void setAppSubMode(String appSubMode) {
        this.appSubMode = appSubMode;
    }

    public String getDeviceLocale() {
        return deviceLocale;
    }

    public void setDeviceLocale(String deviceLocale) {
        this.deviceLocale = deviceLocale;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public long getDeviceStorageCapacity() {
        return deviceStorageCapacity;
    }

    public void setDeviceStorageCapacity(long deviceStorageCapacity) {
        this.deviceStorageCapacity = deviceStorageCapacity;
    }

    public Boolean getGenuineOS() {
        return genuineOS;
    }

    public void setGenuineOS(Boolean genuineOS) {
        this.genuineOS = genuineOS;
    }

    public String[] getUnavailableFeatures() {
        return unavailableFeatures;
    }

    public void setUnavailableFeatures(String[] unavailableFeatures) {
        this.unavailableFeatures = unavailableFeatures;
    }

    public String getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(String batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public double getBatterySOH() {
        return batterySOH;
    }

    public void setBatterySOH(double batterySOH) {
        this.batterySOH = batterySOH;
    }

    public long getBatteryFullChargeCapacity() {
        return batteryFullChargeCapacity;
    }

    public void setBatteryFullChargeCapacity(long batteryFullChargeCapacity) {
        this.batteryFullChargeCapacity = batteryFullChargeCapacity;
    }

    public String isBatteryPlugged() {
        return batteryPlugged;
    }

    public void setBatteryPlugged(String batteryPlugged) {
        this.batteryPlugged = batteryPlugged;
    }

    public boolean isBatteryCharging() {
        return batteryCharging;
    }

    public void setBatteryCharging(boolean batteryCharging) {
        this.batteryCharging = batteryCharging;
    }

    public double getBatteryTemperature() {
        return batteryTemperature;
    }

    public void setBatteryTemperature(double batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    public int getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(int batteryVoltage) {
        batteryVoltage = batteryVoltage;
    }

    public int getBatteryDesignCapacityQuick() {
        return batteryDesignCapacityQuick;
    }

    public void setBatteryDesignCapacityQuick(int batteryDesignCapacity) {
        batteryDesignCapacityQuick = batteryDesignCapacity;
    }


    /*D2D */
    public boolean isMobileData() {
        return mobileData;
    }

    public void setMobileData(boolean mobileData) {
        this.mobileData = mobileData;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }


}
