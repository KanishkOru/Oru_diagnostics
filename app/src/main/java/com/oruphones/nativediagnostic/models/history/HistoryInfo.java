package com.oruphones.nativediagnostic.models.history;



import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by rohit kr. maurya on 30-09-2017.
 */

public class HistoryInfo {
    private long sessionId;
    private long startTime;
    private String endTime;
    private HashMap<String, TestInfo> autoTestResult;
    private HashMap<String, TestInfo> manualTestResult;
    private LinkedHashMap<String, String> batteryCheckResultHistory = new LinkedHashMap<String, String>();
    private String batteryCheckResult = "";
    private String catagoryName;
    private DeviceInformation mDeviceInformation;
    private int passFailCount[] = null;

    public HistoryInfo() {
    }

    public HistoryInfo(long sessionId, long startTime, String endTime) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public HistoryInfo(String endTime) {
        this.endTime = endTime;
    }

    public long getSessionId() {
        return sessionId;
    }


    public long getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }



    public HashMap<String, TestInfo> getAutoTestResult() {
        return autoTestResult;
    }

    public void setAutoTestResult(HashMap<String, TestInfo> autoTestResult) {
        this.autoTestResult = autoTestResult;
    }

    public HashMap<String, TestInfo> getManualTestResult() {
        return manualTestResult;
    }

    public void setManualTestResult(HashMap<String, TestInfo> manualTestResult) {
        this.manualTestResult = manualTestResult;
    }

    public String getCatagoryName() {
        return catagoryName;
    }

    public void setCatagoryName(String catagoryName) {
        this.catagoryName = catagoryName;
    }

    public int[] getTestPassFailCount() {
        return passFailCount;
    }

    public void setTestPassFailCount(int[] passedTest) {
        this.passFailCount = passedTest;
    }

    public LinkedHashMap<String, String> getBatteryCheckResultHistory() {
        return batteryCheckResultHistory;
    }

    public void setBatteryCheckResultHistory(LinkedHashMap<String, String> batteryCheckResultHistory) {
        this.batteryCheckResultHistory = batteryCheckResultHistory;
    }

    public String getBatteryCheckResult() {
        return batteryCheckResult;
    }

    public void setBatteryCheckResult(String batteryCheckResult) {
        this.batteryCheckResult = batteryCheckResult;
    }

    public void updateTestPassFailCount(List<TestInfo> TestResults){
        int passed =0, failed =0,canBeImproved = 0;

        if(TestResults != null && TestResults.size()>0) {
            for(int i=0; i<TestResults.size(); i++){
                TestInfo testInfo = TestResults.get(i);
                switch (testInfo.getTestResult()) {
                    case TestResult.PASS:
                    case TestResult.OPTIMIZED:
                        passed++;
                        break;
                    case TestResult.FAIL:
                        failed++;
                        break;
                    case TestResult.CANBEIMPROVED:
                        canBeImproved++;
                        break;
                }
            }
            int[] passFailCount = new int[]{passed, canBeImproved, failed};
           setTestPassFailCount(passFailCount);
        }
    }

    public DeviceInformation getDeviceInformation() {
        return mDeviceInformation;
    }

    public void setDeviceInformation(DeviceInformation deviceInformation) {
        mDeviceInformation = deviceInformation;
    }
}
