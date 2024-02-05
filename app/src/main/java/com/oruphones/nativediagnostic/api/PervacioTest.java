package com.oruphones.nativediagnostic.api;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.history.History;
import com.oruphones.nativediagnostic.models.AbortReasons;
import com.oruphones.nativediagnostic.models.DeviceInfoDataSet;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.services.AppService;
import com.oruphones.nativediagnostic.services.InitService;
import com.oruphones.nativediagnostic.services.TransactionLogService;
import com.oruphones.nativediagnostic.services.TransactionLogWorker;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ResultComparator;


import org.pervacio.onediaglib.atomicfunctions.AFBluetooth;
import org.pervacio.onediaglib.atomicfunctions.AFGPS;
import org.pervacio.onediaglib.atomicfunctions.AFNFC;
import org.pervacio.onediaglib.atomicfunctions.AFWiFi;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class PervacioTest extends PervacioApplication implements TestName {
    private static PervacioTest pervacioTest;
    private final Context context = PervacioApplication.getAppContext();
    private static String TAG = PervacioTest.class.getSimpleName();
    private AFBluetooth aFBluetooth = null;
    private DeviceInfo deviceInfo = null;
    private AFGPS afGPS = null;
    private AFWiFi afWifi;
    private AFNFC afNFC;
    private GlobalConfig globalConfig = null;
    private boolean globalConfigDone = false;
    private HashMap<String, TestInfo> autoTestResult = new HashMap<String, TestInfo>();
    private HashMap<String, TestInfo> manualTestResult = new HashMap<String, TestInfo>();
    private HashMap<String, Boolean> fivePointCheckResultMap = new HashMap<String, Boolean>();
    private LinkedHashMap<String, String> batteryCheckResultMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String,Boolean> physicalDamageResultMap = new LinkedHashMap<>();
    private String batteryFinalResult = "";
    private HashMap<String, TestInfo> testResult = new HashMap<String, TestInfo>();
    private List<DeviceInfoDataSet> physicalDamageInfo = new ArrayList<>();
    private String selectedCategory = "";
    private String notesMessage="";
    private String summaryResult = null;
    private String sessionStatus = "";
    private AbortReasons mAbortReasons;


    private String deadPixelTestImgPath;
    private String discolorTestImgPath;
    private String screenBurnTestImgPath;

    private QuickBatteryTestInfo quickBatteryTestInfo;
    public boolean isOfflineDiagnostics() {
        return isOfflineDiagnostics;
    }

    public void setOfflineDiagnostics(boolean offlineDiagnostics) {
        isOfflineDiagnostics = offlineDiagnostics;
    }

    private boolean isOfflineDiagnostics = false;

    public String getOfflineSessionID() {
        return offlineSessionID;
    }

    public void setOfflineSessionID(String offlineSessionID) {
        this.offlineSessionID = offlineSessionID;
    }
    public String getNotesMessage() {
        return notesMessage;
    }

    public void setNotesMessage(String notesMessage) {
        this.notesMessage = notesMessage;
    }
    private String offlineSessionID = "";
    SharedPreferences sharedPreferences ;

    public PervacioTest() {
        afGPS = new AFGPS();
        afWifi = new AFWiFi();
        afNFC = new AFNFC();
        aFBluetooth = new AFBluetooth();
        deviceInfo = DeviceInfo.getInstance(context.getApplicationContext());
        globalConfig = GlobalConfig.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        physicalDamageInfo.clear();
    }

    public static PervacioTest getInstance() {
        if (pervacioTest == null) pervacioTest = new PervacioTest();
        return pervacioTest;
    }

    public static PervacioTest getInstance(boolean newSession) {
        if(newSession) {
            pervacioTest = null;
            GlobalConfig.clearInstance();
            Resolution.clearInstance();
            History.clearInstance();
            CommandServer.clearInstance();
        }
        return getInstance();
    }

    public static byte[] gzip(String s) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            OutputStreamWriter osw = new OutputStreamWriter(gzip, Charset.forName("UTF-8"));
            osw.write(s);
            osw.close();
            byte[] bytes = bos.toByteArray();
            // String hexcodes=Hex.encodeHexString( bytes ) ;
            return bytes;
        } catch (Exception e) {
            DLog.e(TAG, "GZip Exception" + e.getMessage());
        }
        return null;
    }


    public void setPhysicalInfo(List<DeviceInfoDataSet> physicalInfo) {
        this.physicalDamageInfo = physicalInfo;
    }

    public List<DeviceInfoDataSet> getPhysicalDamageInfo() {
        return physicalDamageInfo;
    }

    public HashMap<String, TestInfo> getAutoTestResult() {
        return autoTestResult;
    }

    public HashMap<String, TestInfo> getManualTestResult() {
        return manualTestResult;
    }

    public HashMap<String, TestInfo> getTestResult() {
        return testResult;
    }


    public void setTestResult(HashMap<String, TestInfo> testResult) {
        this.testResult = testResult;
    }

    public void initialize() {
        Intent initService = new Intent(context, InitService.class);
        context.startService(initService);
    }
    public void initializeApps() {
        Intent initService = new Intent(context, AppService.class);
        context.startService(initService);
    }
    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
    public void saveGlobalConfig(GlobalConfig globalConfig){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("GlobalConfig", (new Gson()).toJson(globalConfig));
        editor.commit();
    }
    public GlobalConfig getGlobalConfig(){
        if(globalConfig != null) return globalConfig;
        String GlobalConfigData = sharedPreferences.getString("GlobalConfig", "");
        if ("".equalsIgnoreCase(GlobalConfigData)) {
            globalConfigDone = false;
            globalConfig = GlobalConfig.getInstance();
            globalConfigDone = true;
        } else {
            globalConfig = (GlobalConfig) PervacioTest.getInstance().getObjectFromData(GlobalConfigData, new TypeToken<GlobalConfig>() {
            }.getType());
        }
        return globalConfig;
    }

    public void initConfig() {
        try {
            DLog.d(TAG, "initConfig...................");
            String product = "";
            String server_url = "";
            String mode="DIAGR";
            String storeId = "";
            String company = "";

            String debugSupport = "no";
            String central_url = "";
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            InputStream is = context.getAssets().open("config.xml");
            xpp.setInput(is, null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("product")) {
                        product = xpp.nextText();
                    } else if (xpp.getName().equalsIgnoreCase("base_url")) {
                        server_url = xpp.nextText();
                    } else if(xpp.getName().equalsIgnoreCase("central_url")){
                        central_url = xpp.nextText();
                    } else if(xpp.getName().equalsIgnoreCase("debug_support")){
                        debugSupport = xpp.nextText();
                    }else if(xpp.getName().equalsIgnoreCase("store_id")){
                        storeId = xpp.nextText();
                    }else if(xpp.getName().equalsIgnoreCase("company")){
                        company = xpp.nextText();
                    }
                }
                eventType = xpp.next(); // move to next element
            }

            DLog.d(TAG, "product= " + product);
            DLog.d(TAG, "server_url =" + server_url);
            BaseActivity.needDebugSupport = "yes".equalsIgnoreCase(debugSupport);
/*            Log.i("PinValidationActivity", "mode ="+mode);
            Log.i("PinValidationActivity", "Company Name:" + companyName);*/
            globalConfig.setOnDeviceApp(product.equalsIgnoreCase("ODD"));
            globalConfig.setProductName(product);
            globalConfig.setServerUrl(server_url);
            globalConfig.setCompanyName("Pervacio"); // WE are not using it from Config.XML, It should override by server response
            globalConfig.setSubMode("DIAGR");
            globalConfig.setCentralUrl(central_url);
            globalConfig.setStoreID(storeId);
            globalConfig.setCompanyName(company);
            DLog.d(TAG, "central_url =" + globalConfig.getCentralUrl());

            BaseActivity.setIsAssistedApp(product);
            if(product.equalsIgnoreCase("ASSISTED") || product.equalsIgnoreCase("STORE_ASSISTED")) {
                BaseActivity.isAssistedApp = true;
            } else {
                BaseActivity.isAssistedApp = false;
            }

            if(mode.equalsIgnoreCase("GDV")) {
                BaseActivity.isTradeInApp = true;
            } else {
                BaseActivity.isTradeInApp = false;
            }
/*            if(!mode.isEmpty()) {
                BaseActivity.summaryType = mode;
            }
            else{
                BaseActivity.summaryType = Constants.DIAGNOSTICS_RESOLUTIONS;
            }
            if (!companyName.isEmpty()){
                BaseActivity.companyName=companyName;
            }
            else{
                BaseActivity.companyName="Pervacio";
            }*/
        } catch (Exception e) {
            DLog.w(TAG, "Exception= " + e.getMessage());
        }
    }

    public boolean isFeatureAvailable(String testName) {
        switch (testName) {
            case BLUETOOTH_OFF:
            case BLUETOOTH_ON:
            case BLUETOOTHCOMPREHENSIVETEST:
                return (aFBluetooth != null && aFBluetooth.isFeatureAvailable());
            case GPS_OFF:
            case GPS_ON:
            case GPSCOMPREHENSIVETEST:
                return (afGPS != null && afGPS.isFeatureAvailable());
            case WIFI_OFF:
            case WIFI_ON:
            case WIFICOMPREHENSIVETEST:
                return (afWifi != null && afWifi.isFeatureAvailable());
            case NFC_OFF:
            case NFC_ON:
                return (afNFC != null && afNFC.isFeatureAvailable());
            case SDCARD:
            case SDCARDCAPACITY:
                return globalConfig.isDeviceHasSDCardSlot();
            case GYROSCOPESENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_GYROSCOPE);
            case BAROMETERTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_PRESSURE);
            case MAGNETICSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD);
            case GAMEROTATIONVECTORSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_GAME_ROTATION_VECTOR);
            case GEOMAGNETICROTATIONVECTORSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            case ROTATIONVETORSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_ROTATION_VECTOR);
            case LINEARACCELERATIONSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION);

            default:
                return true;
        }
    }


    public <T> T getObjectFromData(String data, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(data, type);
        } catch (Exception e) {
            DLog.e(TAG, "Exception while getObjectFromData" + e.getMessage());
        }
        return null;
    }

    public List<AppDetails> getMalwareRiskyApps(Context context, ArrayList<AppDetails> pdAppResolutionInfos) {
//        if (pdAppResolutionInfos == null || pdAppResolutionInfos.size() == 0) {
//            Log.e(TAG, "------------0 Apps, Skipping WEB Service Call----------------");
//            return pdAppResolutionInfos;
//        }
//        WebrootAppsDetails pdWebrootAppDetails = new WebrootAppsDetails();
//        pdWebrootAppDetails.setPlatform("Android");
//        pdWebrootAppDetails.setCompany(globalConfig.getCompanyName());
//        pdWebrootAppDetails.setUid(deviceInfo.get_imei());
//        pdWebrootAppDetails.setApps(pdAppResolutionInfos);
//        globalConfig.setAppsAccessDenied(false);
//
//        ArrayList<TestInfo> testList = globalConfig.getAutoTestList(PervacioTest.getInstance().getSelectedCategory());
//        for (TestInfo testInfo : testList){
//            if(testInfo.getName().equalsIgnoreCase(org.pervacio.wirelessapp.models.tests.TestName.OUTDATEDAPPS)){
//                pdWebrootAppDetails.setSkipOutdatedAppCheck(false);
//            } else if(testInfo.getName().equalsIgnoreCase(org.pervacio.wirelessapp.models.tests.TestName.RISKYAPPS)){
//                pdWebrootAppDetails.setSkipRiskyAppCheck(false);
//            }
//        }
//
//        //return NetworkModule.getInstance().getAppDetailsFromWebroot(pdWebrootAppDetails);
//        Call<List<AppDetails>> call = ODDNetworkModule.getInstance().getDiagServerApiInterface().getAppDetailsFromWebroot(pdWebrootAppDetails);
//        try {
//            Response<List<AppDetails>> execute = call.execute();
//            return execute.body();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return pdWebrootAppDetails.getApps();
//        }
        return null;
    }

    public void startSession() {
        GlobalConfig.getInstance().setSessionStartTime(System.currentTimeMillis());
        PervacioTest.getInstance().setSessionStatus("Initiated");
        Intent intent = new Intent(PervacioTest.getAppContext(), TransactionLogService.class);
        //intent.putExtra(TransactionLogService.START_SESSION, true);
        PervacioTest.getAppContext().startService(intent);
    }

    public void updateSession() {
        updateSession(false);
    }

    public void updateSession(boolean isSessionEnd) {
        GlobalConfig.getInstance().setSessionEndTime(System.currentTimeMillis());
        Intent intent = new Intent(PervacioTest.getAppContext(), TransactionLogService.class);
        intent.putExtra(TransactionLogService.START_SESSION, false);
        intent.putExtra(TransactionLogService.END_SESSION, isSessionEnd);
        intent.putExtra(TransactionLogService.SESSION_STATUS,"");
        PervacioTest.getAppContext().startService(intent);
    }

    public void updateSessionOnAppClose(){
        TransactionLogWorker.scheduleTheFileUpload();
    }

    public HashMap<String, Boolean> getFivePointCheckResultMap() {
        return fivePointCheckResultMap;
    }

    public void setFivePointCheckResultMap(HashMap<String, Boolean> fivePointCheckResultMap) {
        this.fivePointCheckResultMap = fivePointCheckResultMap;
    }

    public QuickBatteryTestInfo getQuickBatteryTestInfo() {
        return quickBatteryTestInfo;
    }

    public void setQuickBatteryTestInfo(QuickBatteryTestInfo quickBatteryTestInfo) {
        this.quickBatteryTestInfo = quickBatteryTestInfo;
    }

    public LinkedHashMap<String, String> getBatteryCheckResultMap() {
        return batteryCheckResultMap;
    }

    public void setBatteryCheckResultMap(LinkedHashMap<String, String> batteryCheckResultMap) {
        this.batteryCheckResultMap = batteryCheckResultMap;
    }

    public String getBatteryFinalResult() {
        return batteryFinalResult;
    }

    public void setBatteryFinalResult(String batteryFinalResult) {
        this.batteryFinalResult = batteryFinalResult;
    }

    public LinkedHashMap<String, Boolean> getPhysicalDamageResultMap() {
        return physicalDamageResultMap;
    }

    public void setScreenTestTestImgPath(String testName, String imgPath) {
        if (TestName.DEADPIXELTEST.equalsIgnoreCase(testName)){
            deadPixelTestImgPath = imgPath;
        }else if(TestName.DISCOLORATIONTEST.equalsIgnoreCase(testName)) {
            discolorTestImgPath = imgPath;
        }else {
            screenBurnTestImgPath = imgPath;
        }
    }

    public String getScreenTestImgPath(String testName) {
        if (TestName.DEADPIXELTEST.equalsIgnoreCase(testName)){
            return deadPixelTestImgPath;
        }else if(TestName.DISCOLORATIONTEST.equalsIgnoreCase(testName)) {
            return discolorTestImgPath;
        }else {
            return screenBurnTestImgPath;
        }
    }

    public String getSummaryResult() {
        return summaryResult;
    }

    public void setSummaryResult(String summaryResult) {
        this.summaryResult = summaryResult;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }


    /*ABORT REASON */

    public AbortReasons getAbortReasons() {
        return mAbortReasons;
    }

    public void setAbortReasons(AbortReasons abortReasons) {
        mAbortReasons = abortReasons;
    }
    /* FAILED TEST :  */

    public List<TestInfo> getResults(@NonNull String testStatus) {
        List<TestInfo> resultList = new ArrayList<>();
        for (TestInfo testInfo : getSortedTestResults()) {
            if (testStatus.equals(testInfo.getTestResult())) {
                resultList.add(testInfo);
            }
        }
        return resultList;
    }

    public List<TestInfo> getSortedTestResults(){
        List<TestInfo> resultList = new ArrayList<TestInfo>();
        HashMap<String, TestInfo> testResult = getTestResult();
        testResult.putAll(getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());
        for (Map.Entry<String, TestInfo> stringTestInfoEntry : testResult.entrySet()) {
            TestInfo testInfo = (TestInfo) ((Map.Entry) stringTestInfoEntry).getValue();
            resultList.add(testInfo);
        }
        Collections.sort(resultList, new ResultComparator());
        return resultList;
    }
}
