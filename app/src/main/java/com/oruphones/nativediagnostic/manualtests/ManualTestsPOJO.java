package com.oruphones.nativediagnostic.manualtests;

/**
 * Created by Pervacio on 07/08/2017.
 */

public class ManualTestsPOJO {

    private String testName;
    private boolean isChecked;
    private String displayName;

    public String getTestName() {

        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return testName;
    }
}
