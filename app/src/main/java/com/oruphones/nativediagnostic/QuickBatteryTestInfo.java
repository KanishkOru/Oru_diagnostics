package com.oruphones.nativediagnostic;


import com.oruphones.nativediagnostic.models.tests.TestInfo;

public class QuickBatteryTestInfo extends TestInfo {

    private String batteryHealth = "";
    private double batterySOH;
    private int batteryDesignCapacityQuick;
    private int currentBatteryLevel;
    private long batteryFullChargeCapacity;
    private boolean isSOHFromCondition;

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

    public int getBatteryDesignCapacityQuick() {
        return batteryDesignCapacityQuick;
    }

    public void setBatteryDesignCapacityQuick(int batteryDesignCapacityQuick) {
        this.batteryDesignCapacityQuick = batteryDesignCapacityQuick;
    }

    public int getCurrentBatteryLevel() {
        return currentBatteryLevel;
    }

    public void setCurrentBatteryLevel(int currentBatteryLevel) {
        this.currentBatteryLevel = currentBatteryLevel;
    }

    public long getBatteryFullChargeCapacity() {
        return batteryFullChargeCapacity;
    }

    public void setBatteryFullChargeCapacity(long batteryFullChargeCapacity) {
        this.batteryFullChargeCapacity = batteryFullChargeCapacity;
    }

    public boolean isSOHFromCondition() {
        return isSOHFromCondition;
    }

    public void setSOHFromCondition(boolean SOHFromCondition) {
        isSOHFromCondition = SOHFromCondition;
    }
}
