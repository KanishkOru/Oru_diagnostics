package com.oruphones.nativediagnostic.api;

import android.content.Context;

import com.google.gson.JsonObject;


import com.oruphones.nativediagnostic.models.DiagConfiguration;
import com.oruphones.nativediagnostic.models.ManualTestItem;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.models.SummaryDisplayElement;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pervacio on 16-08-2017.
 */
public class GlobalConfig implements Serializable {


    private  boolean isRetry=false;
    private static String TAG = GlobalConfig.class.getSimpleName();
    private String lastCurrentTest="";
    private  String retryTestName="";
    private boolean checkScroll = false;

    /**  sidd : count try manual */
    public static final int[]  ctn = {1};

    private String otpFetched;
    private boolean  lastBluetoothstate;
    private static final long serialVersionUID = 1L;
    private static GlobalConfig globalConfig = null;
    public String firmWare;
    private String productName = "Sprint";
    public String subMode = "DIAGR";
    public String storeID = "-";
    private boolean autobrightnessAvailable = false;
    private boolean latestFirmware = false;
    private String latestFirmwareVersion;
    private String serverWarVersion;
    private boolean enableRAPFeature;
    private boolean generateRAN;
    private int lastRestartThresholdVal = 0;
    private long lastRestartFromDevice = 0;
    private boolean checkNextList =  false;
    private float magneticSensorMinValue = 0f;
    private float magneticSensorMaxValue = 100000f;
    private float gyroscopeSensorMinValue = 0f;
    private float gyroscopeSensorMaxValue = 100000f;
    private float AccelerometerSensorMaxValue = 100000f;
    private float AccelerometerSensorMinValue = 0f;
    private float barometerMinValue = 0f;
    private long currentServerTime;
    private String shortDateFormat;
    private String longDateFormat;
    private String serverKey;

    private Boolean earPhoneTestSkip = false;
    private String preferredCarrier = "";
    private static String pathUri = "";

    private List<String> ResultItemList = new ArrayList<>();

    private String currentTest = "";

    private String currentTestManual = "";

    private Map<String, List<Integer>> testIntegerLists = new HashMap<>();
    private int position = 0;

    private boolean isResultSubmitted = false;
    private List<ManualTestItem> testStates = new ArrayList<>();
    public void setTestStates(List<ManualTestItem> testStates) {
        this.testStates = testStates;

    }
    public boolean getIsResultSubmitted(){
        return isResultSubmitted;
    }
    public void setIsResultSubmitted(boolean isResultSubmitted){
        this.isResultSubmitted = isResultSubmitted;
    }
    public void setCurentTestManual(String CurrentTest){
        this.currentTestManual = CurrentTest;
    }

    public String getCurrentTestManual(){
        return currentTestManual;
    }

    public List<ManualTestItem> getTestStates(){
        return testStates;
    }
    public void setState(String testName){

        testStates.add(new ManualTestItem(testName,true));
        setTestStates(testStates);
    }

    public void setScrollPosition(int position){
        this.position = position;
    }

    public int getScrollPosition(){
        return position;
    }
    public void saveIntegerForTest(String testName, int integerToSave) {
        List<Integer> testList = testIntegerLists.get(testName);
        if (testList == null) {
            testList = new ArrayList<>();
            testIntegerLists.put(testName, testList);
        }
        testList.add(integerToSave);
    }

    public List<Integer> getTestIntegers(String testName) {
        List<Integer> testList = testIntegerLists.get(testName);
        return testList != null ? new ArrayList<>(testList) : new ArrayList<>();
    }


    public void clearAllTestIntegers() {
        testIntegerLists.clear();
    }
    public void addItemToList(String item) {
        ResultItemList.add(item);
    }

    public String getFieldFromItemList(String fieldNamePart, List<String> ResultItemList) {
        StringBuilder resultBuilder = new StringBuilder();
        if (!ResultItemList.isEmpty()){
            for (String message : ResultItemList) {
                if (message.contains(fieldNamePart)) {
                    resultBuilder.append(message).append(", ");
                }
            }
            return resultBuilder.toString();
        }else{
            return null;
        }

    }
    public List<String> getItemList() {
        return ResultItemList;
    }

    public void setCurrentTest(String Test){
        this.currentTest = Test;

    }


    public String getCurrentTest(){
        return currentTest;
    }

    public void clearItemList() {
        ResultItemList.clear();
    }

    public void setCountTry(int ctn){
        this.ctn[0]=ctn;
    }

    public int getCountTry(){
        return ctn[0];
    }


    public String getRetryTestName() {
        return retryTestName;
    }

    public void setRetryTestName(String retryTestName) {
        this.retryTestName = retryTestName;
    }

    public  boolean isIsRetry() {
        return isRetry;
    }

    public  void setIsRetry(boolean isRetry) {
        this.isRetry = isRetry;
    }
    public String getLastCurrentTest() {
        return lastCurrentTest;
    }

    public void setLastCurrentTest(String lastCurrentTest) {
        this.lastCurrentTest = lastCurrentTest;
    }

    public void setEarPhoneTestResult(Boolean earPhoneTestSkip) {
        this.earPhoneTestSkip = earPhoneTestSkip;
    }

    public Boolean getEarPhoneTestResult() {
        return earPhoneTestSkip;
    }
    public void setCheckScroll(Boolean checkScroll) {
        this.checkScroll = checkScroll;
    }
    public Boolean getCheckScroll() {return checkScroll;}

    public String getAutoOtp(){
        return otpFetched;
    }

    public void setAutoOtp(String OTP) {
        this.otpFetched = OTP;
    }


    public String getPreferredCarrier() {
        return preferredCarrier;
    }

    public void setPreferredCarrier(String preferredCarrier) {
        this.preferredCarrier = preferredCarrier;
    }

    public long getLastRestartFromDevice() {
        return lastRestartFromDevice;
    }

    public String getLastRestartFromDeviceFormatted() {
        long lastRestartValue = getLastRestartFromDevice();
        long currentServerTime = getCurrentServerTime();
        long currentSystemTime = System.currentTimeMillis();
        if (currentServerTime != 0)
            lastRestartValue = currentServerTime - lastRestartValue;
        else {
            lastRestartValue = currentSystemTime - lastRestartValue;
        }
        /*long lastrestartinhrs = GlobalConfig.getInstance().getLastRestartFromDevice();

        lastRestartHrs= String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(lastrestartinhrs),
                TimeUnit.MILLISECONDS.toMinutes(lastrestartinhrs) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(lastrestartinhrs)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(lastrestartinhrs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lastrestartinhrs)));*/

        return BaseUtils.DateUtil.format(lastRestartValue, BaseUtils.DateUtil.DateFormats.dd_MM_yyyy_HH_mm_Slash);
    }

    public void setLastRestartFromDevice(long lastRestartFromDevice) {
        this.lastRestartFromDevice = lastRestartFromDevice;
    }

    public void setLastBluetoothstate(boolean state)
    {
        this.lastBluetoothstate=state;
    }

    public boolean getLastBluetoothstate()
    {
        return this.lastBluetoothstate;
    }

    private float barometerMaxValue = 100000f;
    private boolean deviceHasSDCardSlot;
    private String callTestNumber;
    private String deviceModelName;
    private String deviceHardKeys;
    private String deviceSoftKeys;
    private String selectedCategory = PDConstants.RUN_ALL_DIAGNOSTICS;
    private ArrayList<CategoryInfo> categoryList;
    private HashMap<String, ArrayList> autoTestMap;
    private HashMap<String, ArrayList> manualTestMap;
    private LinkedHashMap<String, Boolean> fivePointCheckList;
    private long sessionId = 0;
    private long sessionStartTime = 0;
    private long sessionEndTime = 0;
    private int unusedAppsThresholdVal = 0;
    private HashMap<String, String> testNameMap = new HashMap<String, String>();
    private HashMap<String, TestInfo> testInfoMap = new HashMap<String, TestInfo>();
    private HashMap<String, String> categoryNameMap = new HashMap<String, String>();
    private boolean initCompleted = false;
    private boolean onDeviceApp = false;
    private String serverUrl;
    private String se;
    private String centralServer;
    private String companyName;
    private String secretKey;
    private boolean appsAccessDenied = false;
    private String marketingName = "";
    private boolean certified = false;
    private boolean autobrightnessAvl = false;
    private boolean ownershipCheckProceed = false;
    private String batteryDesignCapacity = "";
    private boolean deviceSupported = false;
    private String pkeys = "";
    private String vkeys = "";
    private Integer[] sohRange = {0, 0};
    private boolean runAllManualTests = false;
    private String storeMailId = "";
    private boolean isFirstTime2 = true;
    private String countryMailId = "";
    private float maxHumidityValue = 100;
    private float minHumidityValue = 0;
    private int userInteractionSessionTimeOut;
    private List<DiagConfiguration.Issue> category = new ArrayList<>();
    private DiagConfiguration.Issue checkMyDevice = new DiagConfiguration.Issue();
    private SummaryDisplayElement[] summaryDisplayElements = SummaryDisplayElement.values();
    private String agentUserId;
    private String[] imeiBlackListStatus;
    private boolean checkIMEIStatus;
    private String[] hybridTests = {}; //{TestName.SPEAKERTEST, TestName.EARPIECETEST, TestName.MICROPHONETEST,TestName.MICROPHONE2TEST,TestName.VIBRATIONTEST};
    private String audioTestNumString = "";

    public String getVibrationTestNumString() {
        return vibrationTestNumString;
    }

    public void setVibrationTestNumString(String vibrationTestNumString) {
        this.vibrationTestNumString = vibrationTestNumString;
    }

    private String vibrationTestNumString = "";

    public String getFlashTestNumString() {
        return flashTestNumString;
    }

    public void setFlashTestNumString(String flashTestNumString) {
        this.flashTestNumString = flashTestNumString;
    }

    private String flashTestNumString = "";
    private boolean isTradeIn;
    private boolean showAnalyzeDeviceScreen;
    private boolean enableTradeIn;
    private boolean diagTradeInEnabled;
    private boolean emailSummary;
    private boolean enableCSAT;
    private String locksRemoved = "";
    private boolean isVerification = false;
    private boolean isBuyerVerification = false;
    private boolean isFinalVerify = false;
    private int batteryMah = 0;
    private String mic2TestResult = TestResult.NONE;
    private String mic1TestResult = TestResult.NONE;

    public void setResultMap(){

    }
    private void getResultMap(){

    }
    public boolean isVerification() {
        return isVerification;
    }

    public boolean isBuyerVerification() {
        return isBuyerVerification;
    }

    public boolean isFinalVerify() {
        DLog.d(TAG, "isFinalVerify: " + isFinalVerify);
        return isFinalVerify;
    }

    public void setVerification(boolean verification) {
        isVerification = verification;
    }
    public void setBuyerVerification(boolean verification) {
        isBuyerVerification = verification;
    }

    public void setFinalVerify(boolean verification) {
        isFinalVerify = verification;
    }

    public int getBatteryMah() {
        return batteryMah;
    }

    public void setBatteryMah(int battery) {
        batteryMah = battery;
    }
    public void setisFirstTime(Boolean isFirstTime2) {
        this.isFirstTime2 = isFirstTime2;
    }
    public Boolean getisFirstTime() {
        return isFirstTime2;
    }
    public boolean isDiagTradeInEnabled() {
        return diagTradeInEnabled;
    }

    public void setDiagTradeInEnabled(boolean diagTradeInEnabled) {
        this.diagTradeInEnabled = diagTradeInEnabled;
    }

    public String isLocksRemoved() {
        return locksRemoved;
    }

    public void setLocksRemoved(String locksRemoved) {
        this.locksRemoved = locksRemoved;
    }

    public boolean isTradeInEnabled() {
        return enableTradeIn;
    }

    public void setEnableTradeIn(boolean enableTradeIn) {
        this.enableTradeIn = enableTradeIn;
    }

    public boolean isEmailSummary() {
        return emailSummary;
    }

    public void setEmailSummary(boolean emailSummary) {
        this.emailSummary = emailSummary;
    }

    public boolean isCSATEnabled() {
        return enableCSAT;
    }

    public void setEnableCSAT(boolean enableCSAT) {
        this.enableCSAT = enableCSAT;
    }

    public JsonObject getBatteryConfig() {
        return batteryConfig;
    }

    public void setBatteryConfig(JsonObject batteryConfig) {
        this.batteryConfig = batteryConfig;
    }

    private JsonObject batteryConfig;

    public boolean isRunAllManualTests() {
        return runAllManualTests;
    }

    public void setRunAllManualTests(boolean runAllManualTests) {
        this.runAllManualTests = runAllManualTests;
    }

    private List<DiagConfiguration.PhysicalDamageTest> physicalTests = new ArrayList<>();

    public int getUnusedAppsThresholdVal() {
        return unusedAppsThresholdVal;
    }

    public void setUnusedAppsThresholdVal(int unusedAppsThresholdVal) {
        this.unusedAppsThresholdVal = unusedAppsThresholdVal;
    }

    public String getCentralUrl() {
        return centralUrl;
    }

    public void setCentralUrl(String centralUrl) {
        this.centralUrl = centralUrl;
    }

    private String centralUrl;

    private GlobalConfig() {
    }

    public static synchronized GlobalConfig getInstance() {
        if (globalConfig == null)
            globalConfig = new GlobalConfig();
        return globalConfig;
    }

    public static void setInstance(GlobalConfig instance) {
        globalConfig = instance;
    }

    public static void clearInstance() {
        globalConfig = null;
    }

    public int getLastRestartThresholdVal() {
        return lastRestartThresholdVal;
    }

    public void setLastRestartThresholdVal(int restartThresholdVal) {
        this.lastRestartThresholdVal = restartThresholdVal;
    }

    public String getSubMode() {
        return subMode;
    }

    public void setSubMode(String subMode) {
        this.subMode = subMode;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public boolean isAutobrightnessAvl() {
        return autobrightnessAvl;
    }

    public void setAutobrightnessAvl(boolean autobrightnessAvl) {
        this.autobrightnessAvl = autobrightnessAvl;
    }

    public boolean isOwnershipCheckProceed() {
        return ownershipCheckProceed;
    }

    public void setOwnershipCheckProceed(boolean ownershipCheckProceed) {
        this.ownershipCheckProceed = ownershipCheckProceed;
    }

    public String getBatteryDesignCapacity() {
        return batteryDesignCapacity;
    }

    public void setBatteryDesignCapacity(String batteryDesignCapacity) {
        this.batteryDesignCapacity = batteryDesignCapacity;
    }

    public boolean isDeviceSupported() {
        return deviceSupported;
    }

    public void setDeviceSupported(boolean deviceSupported) {
        this.deviceSupported = deviceSupported;
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

    public String getPkeys() {
        return pkeys;
    }

    public void setPkeys(String pkeys) {
        this.pkeys = pkeys;
    }

    public String getVkeys() {
        return vkeys;
    }

    public void setVkeys(String vkeys) {
        this.vkeys = vkeys;
    }

    public List<DiagConfiguration.Issue> getCategory() {
        return category;
    }

    public void setCategory(List<DiagConfiguration.Issue> category) {
        this.category = category;
    }

    public DiagConfiguration.Issue getCheckMyDevice() {
        return checkMyDevice;
    }

    public void setCheckMyDevice(DiagConfiguration.Issue checkMyDevice) {
        this.checkMyDevice = checkMyDevice;
    }

    public List<DiagConfiguration.PhysicalDamageTest> getPhysicalTests() {
        return physicalTests;
    }

    public void setPhysicalTests(List<DiagConfiguration.PhysicalDamageTest> physicalTests) {
        this.physicalTests = physicalTests;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public long getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(long sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public HashMap<String, String> getCategoryNameMap() {
        return this.categoryNameMap;
    }

    public void setCategoryNameMap(HashMap<String, String> catagoryNameMap) {
        this.categoryNameMap = catagoryNameMap;
    }

    public HashMap<String, String> getTestNameMap() {
        return testNameMap;
    }

    public HashMap<String, TestInfo> getTestInfoMap() {
        return testInfoMap;
    }

    public void setTestNameMap(HashMap<String, String> testNameMap) {
        this.testNameMap = testNameMap;
    }

    public String getCategoryDisplayName(String categoryName) {
        return getCategoryNameMap().get(categoryName);
    }

    public String getTestDisplayName(String testName) {
        return getTestNameMap().get(testName);
    }

    public TestInfo getTestInfoByName(String testName) {
        return getTestInfoMap().get(testName);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFirmWare() {
        return firmWare;
    }

    public void setFirmWare(String firmWare) {
        this.firmWare = firmWare;
    }

    public String getCompanyName() {
        return "Oruphones";
    }

    public void setCompanyName(String companyName) {
        ProductFlowUtil.setCompanyName(companyName);
        this.companyName = companyName;
    }

    public boolean isAppsAccessDenied() {
        return appsAccessDenied;
    }

    public void setAppsAccessDenied(boolean appsAccessDenied) {
        this.appsAccessDenied = appsAccessDenied;
    }

    public boolean isInitCompleted() {
        return initCompleted;
    }

    public void setInitCompleted(boolean initCompleted) {
        this.initCompleted = initCompleted;
    }

    public boolean isOnDeviceApp() {
        return onDeviceApp;
    }

    public void setOnDeviceApp(boolean onDeviceApp) {
        this.onDeviceApp = onDeviceApp;
    }


    /*SERVER URL */
    public String getServerUrl() {
//        if (TextUtils.isEmpty(PervacioApplication.getTestURL())) {
//            return serverUrl;
//        }
//        return PervacioApplication.getTestURL();
        return "https://prodbackend.oruphones.com/api/v1/";
    }

    public String getProdBaseUrl() {
//        if (TextUtils.isEmpty(PervacioApplication.getTestURL())) {
//            return serverUrl;
//        }
//        return PervacioApplication.getTestURL();
        return "https://prodbackend.oruphones.com/";
    }
    public String   getDevBaseUrl() {
//        if (TextUtils.isEmpty(PervacioApplication.getTestURL())) {
//            return serverUrl;
//        }
//        return PervacioApplication.getTestURL();
        return "https://devbackend.oruphones.com/";
    }

    public void setServerUrl(String serverUrl) {
//        this.serverUrl = serverUrl;
        this.serverUrl = "https://prodbackend.oruphones.com/api/v1/";
    }

    /*CENTRAL URL */
    public String getCentralServer() {
        return centralServer;
    }

    public void setCentralServer(String centralServer) {
        this.centralServer = centralServer;
    }


    public ArrayList<TestInfo> getAutoTestList(String catagoryName) {
        return (getAutoTestMap().get(catagoryName));
    }

    public ArrayList<TestInfo> getManualTestList(String catagoryName) {
        return (getManualTestMap().get(catagoryName));
    }

    public List<TestInfo> cloneManualTestList(String catagoryName) throws CloneNotSupportedException {
        List<TestInfo> list = getManualTestList(catagoryName);
        if (list == null || list.isEmpty())
            return new ArrayList<>();

        List<TestInfo> newClonedList = new ArrayList<>();
        for (TestInfo testInfo : list) {
            newClonedList.add(testInfo.clone());
        }
        return newClonedList;
    }

    public ArrayList<CategoryInfo> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<CategoryInfo> categoryList) {
        this.categoryList = categoryList;
    }

    public HashMap<String, ArrayList> getAutoTestMap() {
        return autoTestMap;
    }

    public void setAutoTestMap(HashMap<String, ArrayList> autoTestMap) {
        this.autoTestMap = autoTestMap;
    }

    public HashMap<String, ArrayList> getManualTestMap() {
        return manualTestMap;
    }

    public void setManualTestMap(HashMap<String, ArrayList> manualTestMap) {
        this.manualTestMap = manualTestMap;
    }

    public String getCallTestNumber() {
        return callTestNumber;
    }

    public void setCallTestNumber(String callTestNumber) {
        this.callTestNumber = callTestNumber;
    }

    public String getDeviceModelName() {
        return deviceModelName;
    }

    public void setDeviceModelName(String deviceModelName) {
        this.deviceModelName = deviceModelName;
    }

    public String getDeviceHardKeys() {
        return deviceHardKeys;
    }

    public void setDeviceHardKeys(String deviceHardKeys) {
        this.deviceHardKeys = deviceHardKeys;
    }

    public String getDeviceSoftKeys() {
        return deviceSoftKeys;
    }

    public void setDeviceSoftKeys(String deviceSoftKeys) {
        this.deviceSoftKeys = deviceSoftKeys;
    }

    public boolean isDeviceHasSDCardSlot() {
        return deviceHasSDCardSlot;
    }

    public void setDeviceHasSDCardSlot(boolean deviceHasSDCardSlot) {
        this.deviceHasSDCardSlot = deviceHasSDCardSlot;
    }

    public boolean getAutobrightnessAvailable() {
        return autobrightnessAvailable;
    }

    public void setAutobrightnessAvailable(boolean available) {
        autobrightnessAvailable = available;
    }

    public boolean isLatestFirmware() {
        return latestFirmware;
    }

    public void setLatestFirmware(boolean available) {
        latestFirmware = available;
    }

    // Latest Version :

    public String getLatestFirmwareVersion() {
        return latestFirmwareVersion;
    }

    public void setLatestFirmwareVersion(String latestFirmwareVersion) {
        this.latestFirmwareVersion = latestFirmwareVersion;
    }

    /*SERVER VERSION */

    public String getServerWarVersion() {
        return serverWarVersion;
    }

    public void setServerWarVersion(String serverWarVersion) {
        this.serverWarVersion = serverWarVersion;
    }

    public float getMagneticSensorMinValue() {
        return magneticSensorMinValue;
    }

    public void setMagneticSensorMinValue(float value) {
        magneticSensorMinValue = value;
    }

    public float getMagneticSensorMaxValue() {
        return magneticSensorMaxValue;
    }

    public void setMagneticSensorMaxValue(float value) {
        magneticSensorMaxValue = value;
    }

    public float getGyroscopeSensorMinValue() {
        return gyroscopeSensorMinValue;
    }

    public void setGyroscopeSensorMinValue(float value) {
        gyroscopeSensorMinValue = value;
    }

    public float getGyroscopeSensorMaxValue() {
        return gyroscopeSensorMaxValue;
    }

    public void setGyroscopeSensorMaxValue(float value) {
        gyroscopeSensorMaxValue = value;
    }

    public float getbarometerMinValue() {
        return barometerMinValue;
    }

    public void setbarometerMinValue(float value) {
        barometerMinValue = value;
    }

    public float getbarometerMaxValue() {
        return barometerMaxValue;
    }

    public void setbarometerMaxValue(float value) {
        barometerMaxValue = value;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    public LinkedHashMap<String, Boolean> getFivePointCheckList() {
        return fivePointCheckList;
    }

    public void setFivePointCheckList(LinkedHashMap<String, Boolean> fivePointCheckList) {
        this.fivePointCheckList = fivePointCheckList;
    }

    public Integer[] getSohRange() {
        return sohRange;
    }

    public void setSohRange(Integer[] quickBatteryThreshold) {
        this.sohRange = quickBatteryThreshold;
    }

    public String getStoreMailId() {
        return storeMailId;
    }

    public void setStoreMailId(String storeMailId) {
        this.storeMailId = storeMailId;
    }

    public String getCountryMailId() {
        return countryMailId;
    }

    public void setCountryMailId(String countryMailId) {
        this.countryMailId = countryMailId;
    }


    /*RAP & RAN */

    public boolean isEnableRAPFeature() {
        return enableRAPFeature;
    }

    public void setEnableRAPFeature(boolean enableRAPFeature) {
        this.enableRAPFeature = enableRAPFeature;
    }

    public boolean isGenerateRAN() {
        return generateRAN;
    }

    public void setGenerateRAN(boolean generateRAN) {
        this.generateRAN = generateRAN;
    }

    /*User interaction time out */

    public int getUserInteractionSessionTimeOut() {
        return userInteractionSessionTimeOut;
    }

    public void setUserInteractionSessionTimeOut(int userInteractionSessionTimeOut) {
        this.userInteractionSessionTimeOut = userInteractionSessionTimeOut;
    }

    public float getMaxHumidityValue() {
        return maxHumidityValue;
    }


    public float getMinHumidityValue() {
        return minHumidityValue;
    }

    public float getMinAccelerometerValue() {
        return AccelerometerSensorMinValue;
    }

    public float getMaxAccelerometerValue() {
        return AccelerometerSensorMaxValue;
    }

    /*User Agent Id for history */

    public String getAgentUserId() {
        return agentUserId;
    }

    public void setAgentUserId(String agentUserId) {
        this.agentUserId = agentUserId;
    }

    /*IMEI STOLEN */

    public String[] getImeiBlackListStatus(Context context) {
        return imeiBlackListStatus;
        //return TextUtils.isEmpty(imeiBlackListStatus)?context.getString(R.string.not_available):imeiBlackListStatus;
    }

    public void setImeiBlackListStatus(String[] imeiBlackListStatus) {
        this.imeiBlackListStatus = imeiBlackListStatus;
    }

    public List<TestInfo> getAllTest() {
        List<TestInfo> testInfoList = new ArrayList<>();

        HashMap<String, ArrayList> autoTests = getAutoTestMap();
        if (autoTests != null) {
            for (Map.Entry<String, ArrayList> entry : autoTests.entrySet())
                testInfoList.addAll(entry.getValue());
        }

        HashMap<String, ArrayList> manualTest = getManualTestMap();
        if (manualTest != null) {
            for (Map.Entry<String, ArrayList> entry : manualTest.entrySet())
                testInfoList.addAll(entry.getValue());
        }

        return testInfoList;
    }

    /*Summary display items*/
    public SummaryDisplayElement[] getSummaryDisplayElements() {
        if (summaryDisplayElements == null) {
            summaryDisplayElements = SummaryDisplayElement.values();
        }
        return summaryDisplayElements;
    }

    public void setSummaryDisplayElements(SummaryDisplayElement[] summaryDisplayElements) {
        this.summaryDisplayElements = summaryDisplayElements;
    }

    public String[] getHybridTests() {
        return hybridTests;
    }

    public void setHybridTests(String[] hybridTests) {
        this.hybridTests = hybridTests;
    }

    public String getAudioTestNumString() {
        return audioTestNumString;
    }

    public void setAudioTestNumString(String audioTestNumString) {
        this.audioTestNumString = audioTestNumString;
    }

    public boolean isCheckIMEIStatus() {
        return checkIMEIStatus;
    }

    public void setCheckIMEIStatus(boolean checkIMEIStatus) {
        this.checkIMEIStatus = checkIMEIStatus;
    }

    public boolean isTradeIn() {
        return isTradeIn;
    }

    public void setTradeIn(boolean tradeIn) {
        isTradeIn = tradeIn;
    }

    public boolean isShowAnalyzeDeviceScreen() {
        return showAnalyzeDeviceScreen;
    }

    public void showAnalyzeDeviceScreen(boolean isShowAnalyzeDeviceScreen) {
        this.showAnalyzeDeviceScreen = isShowAnalyzeDeviceScreen;
    }

    public String getMic2TestResult() {
        return mic2TestResult;
    }

    public void setMic2TestResult(String mic2TestResult) {
        if (this.mic2TestResult != null && mic2TestResult != null && !this.mic2TestResult.equalsIgnoreCase("PASS"))
            this.mic2TestResult = mic2TestResult;
    }

    public String getMic1TestResult() {
        return mic1TestResult;
    }

    public void setMic1TestResult(String mic1TestResult) {
        if (this.mic1TestResult != null && mic1TestResult != null && !this.mic1TestResult.equalsIgnoreCase("PASS"))
            this.mic1TestResult = mic1TestResult;
    }

    public void setPathUri(String pathUri) {
        this.pathUri = pathUri;
        DLog.d(TAG, "setPathUri: " + this.pathUri);
    }

    public static String getPathUri() {
        DLog.d(TAG, "getPathUri: " + pathUri);
        return pathUri;
    }

    //its for bug occured for vanishing at last test
    public void setCheckNextList(Boolean checkNextList) {
        this.checkNextList = checkNextList;
    }
    public Boolean getCheckNextList() { return checkNextList; }

    public void setCameraTestResult(boolean result) {
    }
}
