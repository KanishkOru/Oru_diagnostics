package com.oruphones.nativediagnostic.models.tests;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Pervacio on 31-08-2017.
 */

public class TestInfo implements Cloneable, Serializable {
    private String name = "";
    private String displayName = "";
    private String resultMessage ;
    private String tryMessage;
    private String testResult = TestResult.SKIPPED;

    private long testStartTime;
    private String category;
    private long testEndTime;
    private boolean isChecked;
    private String testAdditionalInfo;


    public TestInfo() {
    }


    public TestInfo(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public TestInfo(String name, String displayName, String tryMessage,String resultMessage) {
        this.name = name;
        this.displayName = displayName;
        this.resultMessage = resultMessage;
        this.tryMessage = tryMessage;
    }

    public TestInfo(String name, String displayName, String testResult) {
        this.name = name;
        this.displayName = displayName;
        this.testResult = testResult;
    }

    public TestInfo(String name, String displayName, String tryMessage,String resultMessage,String testAdditionalInfo) {
        this.name = name;
        this.displayName = displayName;
        this.resultMessage = resultMessage;
        this.tryMessage = tryMessage;
        this.testAdditionalInfo = testAdditionalInfo;
    }



    public long getTestStartTime() {
        return testStartTime;
    }

    public void setTestStartTime(long testStartTime) {
        this.testStartTime = testStartTime;
    }

    public long getTestEndTime() {
        return testEndTime;
    }

    public void setTestEndTime(long testEndTime) {
        this.testEndTime = testEndTime;
    }


    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getResultMessage() {
        return resultMessage;
    }

    public String getTryMessage() {
        return tryMessage;
    }

    public String getTestAdditionalInfo() {
        return testAdditionalInfo;
    }

    public void setTestAdditionalInfo(String testAdditionalInfo) {
        this.testAdditionalInfo = testAdditionalInfo;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        String otherName = obj.toString();
        if (otherName.equalsIgnoreCase(name)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


    @NonNull
    @Override
    public TestInfo clone() throws CloneNotSupportedException {
        return (TestInfo) super.clone();
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Integer getRespectiveResultSortingIndex() {
        if (TextUtils.isEmpty(getTestResult())) {
            return 100;
        }
        switch (getTestResult()) {
            case TestResult.FAIL:
                return 10;
            case TestResult.CANBEIMPROVED:
                return 20;
            case TestResult.OPTIMIZED:
                return 30;
            case TestResult.PASS:
                return 40;
            case TestResult.NONE:
                return 45;
            case TestResult.SKIPPED:
                return 50;
            case TestResult.ACCESSDENIED:
                return 55;
            case TestResult.USERINPUT:
                return 60;
            case TestResult.TIMEOUT:
                return 65;
        }
      return 100;

    }
}
