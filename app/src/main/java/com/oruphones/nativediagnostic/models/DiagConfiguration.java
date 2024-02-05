package com.oruphones.nativediagnostic.models;


import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.models.tests.TestName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DiagConfiguration implements Serializable {
    @SerializedName("serialVersionUID")
    private static final long serialVersionUID = 1L;
    String longDateFormat = "";
    @SerializedName("category")
    private List<Issue> category = new ArrayList<>();
    @SerializedName("checkMyDevice")
    private Issue checkMyDevice = new Issue();
    @SerializedName("physicalTests")
    private List<PhysicalDamageTest> physicalTests = new ArrayList<>();
    @SerializedName("fivePointCheck")
    private LinkedList<Map<String, Boolean>> fivePointCheck = new LinkedList<>();
    @SerializedName("marketingName")
    private String marketingName = "";
    @SerializedName("certified")
    private Boolean certified = false;
    @SerializedName("autobrightnessAvl")
    private Boolean autobrightnessAvl = true;
    @SerializedName("ownershipCheckProceed")
    private Boolean ownershipCheckProceed = true;
    @SerializedName("batteryDesignCapacity")
    private String batteryDesignCapacity = "";
    @SerializedName("deviceSupported")
    private Boolean deviceSupported = true;
    @SerializedName("callTestNumber")
    private String callTestNumber = "";
    @SerializedName("pkeys")
    private String pkeys = "POWER,VOLUME_UP,VOLUME_DOWN";
    @SerializedName("vkeys")
    private String vkeys = "MENU,BACK,HOME";

    @SerializedName("latestFirmware")
    private Boolean latestFirmware = false;

    @SerializedName("latestFirmwareVersion")
    private String latestFirmwareVersion;

    @SerializedName("serverWARVersion")
    private String serverWARVersion;

    @Expose
    @SerializedName("sessionTimeoutInMins")
    private int sessionTimeOut;

    @SerializedName("lastRestartThresholdDays")
    private int lastRestartThresholdDays = 0;
    @SerializedName("currentServerTime")
    private long currentServerTime;
    @SerializedName("shortDateFormat")
    private String shortDateFormat = "";
    @SerializedName("sohRange")
    private Integer[] sohRange;
    @SerializedName("runAllManualTests")
    private boolean runAllManualTests;
    @SerializedName("storeemail")
    private String storeemail = "";
    @SerializedName("countryemail")
    private String countryemail = "";
    /*RAP & RAN */
    @SerializedName("enableRAPFeature")
    private boolean enableRAPFeature;
    @SerializedName("generateRAN")
    private boolean generateRAN;

    @SerializedName("batteryConfig")
    private JsonObject batteryConfig;

    @SerializedName("summaryDisplayElements")
    private SummaryDisplayElement[] summaryDisplayElements;

    @SerializedName("agentUserId")
    private String agentUserId;

    @SerializedName("preferredCarrier")
    private String preferredCarrier;

    public String[] getHybridTests() {
        return hybridTests;
    }

    public void setHybridTests(String[] hybridTests) {
        this.hybridTests = hybridTests;
    }

    @SerializedName("hybridTests")
    private String hybridTests[];

    @SerializedName("checkIMEIStolenStatus")
    private boolean checkIMEIStolenStatus;

    @SerializedName("showAnalyzeDeviceScreen")
    private boolean showAnalyzeDeviceScreen = true;

    @SerializedName("enableEmailSummary")
    private boolean enableEmailSummary = true;

    @SerializedName("enableCSAT")
    private boolean enableCSAT = true;

    @SerializedName("enableTradeInFlow")
    private boolean enableTradeInFlow = false;

    @SerializedName("enableDiagTradeInFlow")
    private boolean enableDiagTradeInFlow = false;

    @SerializedName("tradeIn")
    private Issue tradeIn;

    @SerializedName("verifyDevice")
    private Issue verifyDevice;

    public Issue getVerifyDevice() {
        return verifyDevice;
    }

    public void setVerifyDevice(Issue verifyDevice) {
        this.verifyDevice = verifyDevice;
    }

    public Issue getTradeInCategory() {
        return tradeIn;
    }

    public boolean isEnableDiagTradeInFlow() {
        return enableDiagTradeInFlow;
    }

    public boolean isEnableEmailSummary() {
        return enableEmailSummary;
    }

    public boolean isEnableCSAT() {
        return enableCSAT;
    }

    public boolean isEnableTradeInFlow() {
        return enableTradeInFlow;
    }

    public String getPreferredCarrier() {
        return preferredCarrier;
    }

    public void setPreferredCarrier(String preferredCarrier) {
        this.preferredCarrier = preferredCarrier;
    }


    public SummaryDisplayElement[] getSummaryDisplayElements() {
        return summaryDisplayElements;
    }

    public String getAgentUserId() {
        return agentUserId;
    }

    private int unusedAppsThreshold = 0;

    public DiagConfiguration() {

    }

    public JsonObject getBatteryConfig() {
        return batteryConfig;
    }

    public void setBatteryConfig(JsonObject batteryConfig) {
        this.batteryConfig = batteryConfig;
    }

    public boolean isRunAllManualTests() {
        return runAllManualTests;
    }

    public void setRunAllManualTests(boolean runAllManualTests) {
        this.runAllManualTests = runAllManualTests;
    }

    public int getUnusedAppsThreshold() {
        return unusedAppsThreshold==0?5:unusedAppsThreshold;
    }

    public void setUnusedAppsThreshold(int unusedAppsThreshold) {
        this.unusedAppsThreshold = unusedAppsThreshold;
    }

    public int getLastRestartThresholdDays() {
        return lastRestartThresholdDays;
    }

    public void setLastRestartThresholdDays(int lastRestartThresholdDays) {
        this.lastRestartThresholdDays = lastRestartThresholdDays;
    }

    public long getCurrentServerTime() {
        return currentServerTime;
    }

    public void setCurrentServerTime(long currentServerTime) {
        this.currentServerTime = currentServerTime;
    }

    public String getShortDateFormat() {
        return shortDateFormat;
    }

    public void setShortDateFormat(String shortDateFormat) {
        this.shortDateFormat = shortDateFormat;
    }

    public String getLongDateFormat() {
        return longDateFormat;
    }

    public void setLongDateFormat(String longDateFormat) {
        this.longDateFormat = longDateFormat;
    }

    public LinkedList<Map<String, Boolean>> getFivePointCheck() {
        return fivePointCheck;
    }

    public void setFivePointCheck(LinkedList<Map<String, Boolean>> fivePointCheck) {
        this.fivePointCheck = fivePointCheck;
    }

    public Integer[] getSohRange() {
        return sohRange;
    }

    public void setSohRange(Integer[] sohRange) {
        this.sohRange = sohRange;
    }

    public List<Issue> getCategory() {
        return category;
    }

    public void setCategory(List<Issue> category) {
        this.category = category;
    }

    public Issue getCheckMyDevice() {
        return checkMyDevice;
    }

    public void setCheckMyDevice(Issue checkMyDevice) {
        this.checkMyDevice = checkMyDevice;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public Boolean getCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }

    public Boolean isLatestFirmware() {
        return latestFirmware;
    }

    public String getLatestFirmwareVersion() {
        return latestFirmwareVersion;
    }

    public String getServerWARVersion() {
        return serverWARVersion;
    }

    public Boolean getAutobrightnessAvl() {
        return autobrightnessAvl;
    }

    public void setAutobrightnessAvl(Boolean autobrightnessAvl) {
        this.autobrightnessAvl = autobrightnessAvl;
    }

    public Boolean getOwnershipCheckProceed() {
        return ownershipCheckProceed;
    }

    public void setOwnershipCheckProceed(Boolean ownershipCheckProceed) {
        this.ownershipCheckProceed = ownershipCheckProceed;
    }

    public List<PhysicalDamageTest> getPhysicalTests() {
        return physicalTests;
    }

    public void setPhysicalTests(List<PhysicalDamageTest> physicalTests) {
        this.physicalTests = physicalTests;
    }

    public String getBatteryDesignCapacity() {
        return batteryDesignCapacity;
    }

    public void setBatteryDesignCapacity(String batteryDesignCapacity) {
        this.batteryDesignCapacity = batteryDesignCapacity;
    }

    public Boolean getDeviceSupported() {
        return deviceSupported;
    }

    public void setDeviceSupported(Boolean deviceSupported) {
        this.deviceSupported = deviceSupported;
    }

    public String getCallTestNumber() {
        return callTestNumber;
    }

    public void setCallTestNumber(String callTestNumber) {
        this.callTestNumber = callTestNumber;
    }

    public String getPkeys() {
        return pkeys;
    }

    public String getVkeys() {
        return vkeys;
    }

    public String getStoreMailId() {
        return storeemail;
    }

    public String getCountryMailId() {
        return countryemail;
    }

    public boolean isEnableRAPFeature() {
        return enableRAPFeature;
    }

    public boolean isGenerateRAN() {
        return generateRAN;
    }
    /*RAP & RAN*/


    /*SessionTiem out */
    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public boolean isCheckIMEIStolenStatus() {
        return checkIMEIStolenStatus;
    }

    /*Analyze Device Screen Hideen*/
    public boolean isShowAnalyzeDeviceScreen() {
        return showAnalyzeDeviceScreen;
    }

    public static class Issue implements Serializable, Comparable<Issue> {
        @SerializedName("serialVersionUID")
        private static final long serialVersionUID = 1L;
        @SerializedName("issueName")
        private String issueName = "";
        @SerializedName("displayname")
        private String displayname = "";
        @SerializedName("description")
        private String description = "";
        @SerializedName("autoTests")
        private List<Test> autoTests = new ArrayList<>();
        @SerializedName("manualTests")
        private List<Test> manualTests = new ArrayList<>();
        public Issue() {

        }
        public Issue(String issueName, String displayname,String desc ,List<Test> autoTests,List<Test> manualTests) {
            this(issueName,displayname,desc);
            this.autoTests=autoTests;
            this.manualTests=manualTests;
        }

        public Issue(String issueName, String displayname,String desc) {
            this.issueName = issueName;
            this.displayname = displayname;
            this.description=desc;
        }

        public String getIssueName() {
            return issueName;
        }

        public void setIssueName(String issueName) {
            this.issueName = issueName;
        }

        public String getDisplayname() {
            return displayname;
        }

        public void setDisplayname(String displayname) {
            this.displayname = displayname;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Test> getAutoTests() {
            return autoTests;
        }

        public List<Test> getManualTests() {
           /* if(BuildConfig.DEBUG && ("CheckMyDevice".equalsIgnoreCase(issueName)||"RunAllDiagnostics".equalsIgnoreCase(issueName))&&manualTests!=null && !manualTests.isEmpty()){
                manualTests.add(Test.getSimulatedTest());
            }*/
            return manualTests;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((issueName == null) ? 0 : issueName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Issue other = (Issue) obj;
            if (issueName == null) {
                if (other.issueName != null)
                    return false;
            } else if (!issueName.equals(other.issueName))
                return false;
            return true;
        }

        @Override
        public int compareTo(Issue o) {
            return 0;
        }
    }

    public static class Test implements Serializable, Comparable<Test> {
        @SerializedName("serialVersionUID")
        private static final long serialVersionUID = 1L;
        @SerializedName("name")
        private String name = "";
        @SerializedName("displayname")
        private String displayname = "";
        @SerializedName("category")
        private String category = "";
        @SerializedName("severity")
        private String severity = "";
        @SerializedName("status")
        private String status = "";
        @SerializedName("testDescription")
        private String testDescription;
        @SerializedName("testConfirmationMsg")
        private String testConfirmationMsg;

        public Test() {

        }
        public Test(String name, String displayName) {
            this.name = name;
            this.displayname = displayName;
        }

        public Test(String name, String displayName,String category) {
            this(name, displayName);
            this.category=category;
        }

        public Test(String name, String displayName,String category,String severity) {
            this(name, displayName,category);
            this.severity=severity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDisplayname() {
            return displayname;
        }

        public void setDisplayname(String displayname) {
            this.displayname = displayname;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTestTryMessage() {
            return testDescription;
        }

        public String getTestResultMessage() {
            return testConfirmationMsg;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Test other = (Test) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public int compareTo(Test o) {
            return 0;
        }
        public static Test getSimulatedTest(){
            Test test = new Test(TestName.FINGERPRINTSENSORTEST,TestName.FINGERPRINTSENSORTEST,"Apps");
            test.setSeverity("LOW");
            test.setStatus("NONE");
            return test;
        }

        @Override
        public String toString() {
            return "Test{" +
                    "name='" + name + '\'' +
                    ", displayname='" + displayname + '\'' +
                    ", category='" + category + '\'' +
                    ", severity='" + severity + '\'' +
                    ", status='" + status + '\'' +
                    ", testDescription='" + testDescription + '\'' +
                    ", testConfirmationMsg='" + testConfirmationMsg + '\'' +
                    '}';
        }
    }



    @Override
    public String toString() {
        return "DiagConfiguration{" +
                "category=" + category +
                ", checkMyDevice=" + checkMyDevice +
                ", marketingName='" + marketingName + '\'' +
                ", certified=" + certified +
                ", autobrightnessAvl=" + autobrightnessAvl +
                ", ownershipCheckProceed=" + ownershipCheckProceed +
                ", batteryDesignCapacity='" + batteryDesignCapacity + '\'' +
                ", deviceSupported=" + deviceSupported +
                ", callTestNumber='" + callTestNumber + '\'' +
                ", pkeys='" + pkeys + '\'' +
                ", vkeys='" + vkeys + '\'' +
                '}';
    }

    public static class PhysicalDamageTest implements Serializable, Comparable<PhysicalDamageTest> {
        @SerializedName("serialVersionUID")
        private static final long serialVersionUID = 1L;
        @SerializedName("name")
        private String name = "";
        @SerializedName("displayName")
        private String displayName = "";
        @SerializedName("description")
        private String description = "";
        @SerializedName("category")
        private String category = "";
        @SerializedName("severity")
        private String severity = "";
        @SerializedName("orderNum")
        private Integer orderNum;

        public PhysicalDamageTest() {

        }

        public PhysicalDamageTest(String name, String displayName, String description, String category, String severity,
                                  Integer orderNum) {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.severity = severity;
            this.orderNum = orderNum;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public Integer getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(Integer orderNum) {
            this.orderNum = orderNum;
        }

        public String getDescription() {
            return description;
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Test other = (Test) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public int compareTo(PhysicalDamageTest o) {
            return this.orderNum - o.orderNum;
        }
    }

}


