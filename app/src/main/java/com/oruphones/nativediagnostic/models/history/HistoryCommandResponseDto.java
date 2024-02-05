package com.oruphones.nativediagnostic.models.history;

import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.util.CommonUtil;


public class HistoryCommandResponseDto {
    @SerializedName("commandHistoryId")
    private String commandHistoryId;

    @SerializedName("commandName")
    private String commandName;

    @SerializedName("status")
    private int status;
    @SerializedName("startDateTime")
    private long startDateTime;

    @SerializedName("endDateTime")
    private long endDateTime;


    public String getCommandName() {
        return commandName;
    }

    public TestInfo getRespectiveTestInfo(TestInfo testInfo) {
        //TestInfo testInfo = new TestInfo(commandName, commandName, );
        testInfo.setTestResult(CommonUtil.getMappedTestResult(status));
        testInfo.setTestEndTime(endDateTime);
        testInfo.setTestStartTime(startDateTime);
        return testInfo;
    }

}