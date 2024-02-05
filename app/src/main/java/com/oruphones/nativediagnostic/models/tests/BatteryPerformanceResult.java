package com.oruphones.nativediagnostic.models.tests;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryPerformanceResult {
    private static BatteryPerformanceResult instance = null;
    private String batteryHealth = "NA";
    private double batterySOH = 0;
    private double batteryDesignCapacity = 0;
    private int batterySohByE = 0;
    private int batterySohByT = 0;
    private String batteryTestProfile = "";
    private double batteryCalculatedCapacity = 0;
    private int resultCode = -1;
    private int errorCode = -1;
    private String batteryResult;
    private String batteryConfig;

    public static synchronized BatteryPerformanceResult getInstance() {
        if (instance == null)
            instance = new BatteryPerformanceResult();
        return instance;
    }

    public String getBatteryConfig() {
        return batteryConfig;
    }

    public void setBatteryConfig(String batteryConfig) {
        this.batteryConfig = batteryConfig;
    }

    public int getBatterySohByE() {
        return batterySohByE;
    }

    public void setBatterySohByE(int batterySohByE) {
        this.batterySohByE = batterySohByE;
    }

    public int getBatterySohByT() {
        return batterySohByT;
    }

    public void setBatterySohByT(int batterySohByT) {
        this.batterySohByT = batterySohByT;
    }

    public String getBatteryTestProfile() {
        return batteryTestProfile;
    }

    public void setBatteryTestProfile(String batteryTestProfile) {
        this.batteryTestProfile = batteryTestProfile;
    }

    public double getBatteryCalculatedCapacity() {
        return batteryCalculatedCapacity;
    }

    public void setBatteryCalculatedCapacity(double batteryCalculatedCapacity) {
        this.batteryCalculatedCapacity = batteryCalculatedCapacity;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getBatteryResult() {
        return batteryResult;
    }

    public void setBatteryResult(String batteryResult) {
        this.batteryResult = batteryResult;
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

    public double getBatteryDesignCapacity() {
        return batteryDesignCapacity;
    }

    public void setBatteryDesignCapacity(double batteryDesignCapacity) {
        this.batteryDesignCapacity = batteryDesignCapacity;
    }

    public int getCurrentBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }
}
