package com.oruphones.nativediagnostic.models;


import com.oruphones.nativediagnostic.api.GlobalConfig;

public class ManualTestItem {
    private String testName;
    private String testDescription;
    private boolean isCurrent;
    private String currentDescription;
    private String testImageName;
    private String testResult;
    private boolean isSkipped;
    private boolean isReattempted = true;
    private boolean isLastItem = false;
    private GlobalConfig globalConfig;
    public String getTestImageName() {
        return testImageName;
    }

    public void setIsReattempted(boolean isReattempted){
        this.isReattempted = isReattempted;
    }

    public boolean isReattempted(){
        return isReattempted;
    }

    public void setIsLastItem(boolean isLastItem){
        this.isLastItem = isLastItem;
    }

    public boolean isLastItem(){
        return isLastItem;
    }

    public ManualTestItem(String testName, boolean isSkipped) {
        this.isSkipped = isSkipped;
        this.testName = testName;
    }


    public ManualTestItem(String testName, String testDescription, String currentDescription, String testImageName, String testResult) {
        this.testName = testName;
        this.testDescription = testDescription;
        this.currentDescription = currentDescription;
        this.testImageName = testImageName;
        this.testResult = testResult;

    }
    public boolean isSkipped() {
        return isSkipped;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }

    public String getTestName() {
        return testName;
    }

    public String getCurrentDescription() {
        return currentDescription;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isCurrent() {
        return isCurrent;
    }
}
