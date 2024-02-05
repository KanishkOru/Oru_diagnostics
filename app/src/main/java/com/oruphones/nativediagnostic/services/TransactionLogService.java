package com.oruphones.nativediagnostic.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.api.BuildConfig;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.history.History;
import com.oruphones.nativediagnostic.models.LogTransactionResp;
import com.oruphones.nativediagnostic.models.PDCommandDetails;
import com.oruphones.nativediagnostic.models.PDDiagLogging;
import com.oruphones.nativediagnostic.models.tests.BatteryPerformanceResult;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;
import com.pervacio.batterydiaglib.util.BatteryUtil;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Pervacio on 30/10/17.
 */

public class TransactionLogService extends IntentService {

    public static final String START_SESSION = "START_SESSION";
    public static final String END_SESSION = "END_SESSION";
    public static final String SESSION_STATUS = "SESSION_STATUS";
    private static String TAG = TransactionLogService.class.getSimpleName();
    private String sessionStatus = "Initiated";
    private String deviceStatus = "Initiated";
    private int sendResultsRetryCount = 0;


    public TransactionLogService() {
        super("TransactionLogService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        sessionStatus = PervacioTest.getInstance().getSessionStatus();
        prepareRequest(deviceStatus, sessionStatus);
    }


    public void prepareRequest(String deviceStatus, String sessionStatus) {
        try {
            DLog.d(TAG, "In TransactionLogService -  Session status : " + PervacioTest.getInstance().getSessionStatus());
            if (PervacioTest.getInstance().isOfflineDiagnostics()) {
                if ("Initiated".equalsIgnoreCase(PervacioTest.getInstance().getSessionStatus())) {
                    String timestamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                    PervacioTest.getInstance().setOfflineSessionID(timestamp);
                    DLog.d(TAG, "created OfflineSessionID : " + timestamp);
                } else {
                    PDDiagLogging offlineData = prepareSessionData(deviceStatus, sessionStatus);
                    History.getInstance().saveOfflineHistory(PervacioTest.getInstance().getOfflineSessionID(), (new Gson()).toJson(offlineData));
                    DLog.d(TAG, "saving OfflineSessionID : " + PervacioTest.getInstance().getOfflineSessionID());
                    DLog.d(TAG,offlineData.toString());
                }

            } else {
                if ("Initiated".equalsIgnoreCase(PervacioTest.getInstance().getSessionStatus())) {
                    deviceStatus = "Initiated";
                } else if ("Success".equalsIgnoreCase(PervacioTest.getInstance().getSessionStatus())) {
                    deviceStatus = "Passed";
                }

                PDDiagLogging logData = prepareSessionData(deviceStatus, sessionStatus);
                DLog.d("LogDataCheck online",logData.toString());
                DLog.d(TAG, logData.toString());
                sendResultsToServer(logData);

            }

        } catch (Exception e) {
            DLog.e(TAG, "Exception while getObjectFromData" + e.getMessage());
            sendError(e, null);
        }
    }

    private void sendResultsToServer(final PDDiagLogging logData) {

        DLog.d(TAG,"CHECK FOR sendResultsToServer");
        Call<LogTransactionResp> transaction = ODDNetworkModule.getInstance().getDiagServerApiInterface().logTransaction(logData);
        try {
            Response<LogTransactionResp> response = transaction.execute();
//            LogUtil.printLog(TAG, "response : " + response.body().toString());
            LogTransactionResp transactionResponse = response.body();
            Intent bintent = new Intent("org.pervacio.wirelessapp.TEST_RESULTS_UPDATED");
            if (transactionResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                GlobalConfig.getInstance().setSessionId(Long.parseLong(transactionResponse.getSessionId()));
                bintent.putExtra("result", 1);
                DLog.d(TAG,"CHECK FOR sendResultsToServer SUCCESS" +logData.toString());
            } else {
                bintent.putExtra("result", 0);
            }
            LocalBroadcastManager.getInstance(TransactionLogService.this).sendBroadcast(bintent);
        } catch (Exception e) {
            e.printStackTrace();
            DLog.d(TAG,"CHECK FOR sendResultsToServer" + e.getMessage());
            sendError(e, logData);
        }
    }

    private void sendError(Exception e, PDDiagLogging logData) {
        DLog.e(TAG, "Exception  " + e.getMessage());
        if (logData != null && sendResultsRetryCount < 2) {
            sendResultsRetryCount = sendResultsRetryCount + 1;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            sendResultsToServer(logData);
        } else {
            Intent bintent = new Intent("org.pervacio.wirelessapp.TEST_RESULTS_UPDATED");
            bintent.putExtra("result", 0);
            LocalBroadcastManager.getInstance(this).sendBroadcast(bintent);
        }

    }


    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }


    public String readStream(InputStream in) {
        DLog.d(TAG, "in readStream.........");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                DLog.d(TAG, "Response Line: " + nextLine);
                sb.append(nextLine);
            }
        } catch (IOException e) {
            DLog.e(TAG, "readStream exception: " + e.getMessage());
        }
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        DLog.d(TAG, "Service destroyed");
        super.onDestroy();
    }


    private static ArrayList<PDCommandDetails> getTestDetails() {
        ArrayList<PDCommandDetails> pdCommandDetailsList = new ArrayList<>();
        HashMap testResult = PervacioTest.getInstance().getTestResult();
        testResult.putAll(PervacioTest.getInstance().getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());
        final long sessionID = GlobalConfig.getInstance().getSessionId();
        Iterator it = testResult.entrySet().iterator();
        List<String> ResultItemList = GlobalConfig.getInstance().getItemList();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            TestInfo testInfo = (TestInfo) pair.getValue();
            PDCommandDetails pdCommandDetails = new PDCommandDetails();
            pdCommandDetails.setCommandName(testInfo.getName());
            pdCommandDetails.setTestStatus(testInfo.getTestResult());
            pdCommandDetails.setMessage("");
            String mTestName = testInfo.getName();
           
            if (TestName.QUICKBATTERYTEST.equalsIgnoreCase(testInfo.getName()) ||
                TestName.BATTERYPERFORMANCE.equalsIgnoreCase(testInfo.getName())) {
                QuickBatteryTestInfo quickBatteryData = PervacioTest.getInstance().getQuickBatteryTestInfo();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("batteryChargeLevel", "" + quickBatteryData.getCurrentBatteryLevel());
                    jsonObject.put("batteryDesignCapacity", "" + quickBatteryData.getBatteryDesignCapacityQuick());
                    jsonObject.put("batteryTemperature", "" + BatteryUtil.getBatteryTemperature(OruApplication.getAppContext()));
                    jsonObject.put("batteryTechnology", "" + BatteryUtil.getBatteryTechnology(OruApplication.getAppContext()));
                    if(TestName.QUICKBATTERYTEST.equalsIgnoreCase(testInfo.getName())) {
                        jsonObject.put("batteryFullChargeCapacity", "" + quickBatteryData.getBatteryFullChargeCapacity());
                        if (!quickBatteryData.getBatteryHealth().equalsIgnoreCase(OruApplication.getAppContext().getString(R.string.unsupported))) {
                            jsonObject.put("batteryHealth", "" + quickBatteryData.getBatteryHealth());
                            if (quickBatteryData.isSOHFromCondition()) {
                                jsonObject.put("batterySOH", "NA");
                            } else {
                                jsonObject.put("batterySOH", "" + quickBatteryData.getBatterySOH());
                            }
                        } else {
                            jsonObject.put("batterySOH", "" + "NA");
                            String batteryHealth = BatteryUtil.getBatteryHealthByAndroidAPI(OruApplication.getAppContext());
                            if("BATTERY HEALTH GOOD".equalsIgnoreCase(batteryHealth)) {
                                jsonObject.put("batteryHealth", "VeryGood");
                            } else if("BATTERY HEALTH UNKNOWN".equalsIgnoreCase(batteryHealth)) {
                                jsonObject.put("batteryHealth", "NA");
                            } else {
                                jsonObject.put("batteryHealth", "Bad");
                            }
                        }
                    } else {  //BatteryPerformanceTest
                        jsonObject.put("batteryFullChargeCapacity", "" + BatteryPerformanceResult.getInstance().getBatteryCalculatedCapacity());
                        jsonObject.put("batteryHealth", "" + BatteryPerformanceResult.getInstance().getBatteryHealth());
                        jsonObject.put("batterySOH", ""+BatteryPerformanceResult.getInstance().getBatterySOH());
                        jsonObject.put("batterySohByE", ""+ BatteryPerformanceResult.getInstance().getBatterySohByE());
                        jsonObject.put("batterySohByT", ""+BatteryPerformanceResult.getInstance().getBatterySohByT());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pdCommandDetails.setMessage(jsonObject.toString());
            }

            else{
                switch (mTestName) {
                    case TestName.MICROPHONE2TEST:
                        handleTest("Mic2Test", pdCommandDetails);
                        break;
                    case TestName.MICROPHONETEST:
                        handleTest("MicTest", pdCommandDetails);
                        break;
                    case TestName.SPEAKERTEST:
                        handleTest("Speaker", pdCommandDetails);
                        break;
                    case TestName.EARPIECETEST:
                        handleTest("Earpiece", pdCommandDetails);
                        break;
                    case TestName.VIBRATIONTEST:
                        handleTest("Vibration", pdCommandDetails);
                        break;
                    case TestName.CAMERAFLASHTEST:
                        handleTest("Flash Test", pdCommandDetails);
                        break;
                    case TestName.EARPHONETEST:
                        handleTest("Earphone", pdCommandDetails);
                        break;
                    case TestName.REARCAMERAVIDEOTEST:
                    case TestName.REARCAMERAVIDEOTEST1:
                    case TestName.REARCAMERAVIDEOTEST2:
                    case TestName.REARCAMERAVIDEOTEST3:
                    case TestName.REARCAMERAVIDEOTEST4:
                    case TestName.REARCAMERAVIDEOTEST5:
                    case TestName.REARCAMERAVIDEOTEST6:
                        handleTest("Rear Camera video test", pdCommandDetails);
                        break;
                    case TestName.FRONTCAMERAVIDEOTEST:
                    case TestName.FRONTCAMERAVIDEOTEST1:
                    case TestName.FRONTCAMERAVIDEOTEST2:
                    case TestName.FRONTCAMERAVIDEOTEST3:
                    case TestName.FRONTCAMERAVIDEOTEST4:
                    case TestName.FRONTCAMERAVIDEOTEST5:
                    case TestName.FRONTCAMERAVIDEOTEST6:
                        handleTest("Front Camera video test", pdCommandDetails);
                        break;
                    default:
                        //default case
                        break;
                }
            }
            if (("bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav)) && (TestName.DEADPIXELTEST.equalsIgnoreCase(testInfo.getName()) || TestName.DISCOLORATIONTEST.equalsIgnoreCase(testInfo.getName()) ||
                    TestName.SCREENBURNTEST.equalsIgnoreCase(testInfo.getName()))) {
                String testImgPath = PervacioTest.getInstance().getScreenTestImgPath(testInfo.getName());
                String imgString = getImgFileString(testImgPath);
                if (imgString != null) {
                    pdCommandDetails.setMessage(imgString);
                }
            }
            pdCommandDetails.setStartDateTime(testInfo.getTestStartTime());
            pdCommandDetails.setEndDateTime(testInfo.getTestEndTime());
            pdCommandDetails.setSessionId(sessionID);
            pdCommandDetailsList.add(pdCommandDetails);
        }

        /*if (Util.isFivePointCheckRequired()) {
            PDCommandDetails pdCommandDetails = new PDCommandDetails();
            pdCommandDetails.setCommandName(TestName.FIVEPOINTCHECK);
            pdCommandDetails.setTestStatus(TestResult.PASS);
            Iterator<Map.Entry<String, Boolean>> iterator = PervacioTest.getInstance().getFivePointCheckResultMap().entrySet().iterator();
            JSONObject jsonObjectMain = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                try {
                    jsonObject.put((String) pair.getKey(), "" + (Boolean) pair.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonObjectMain.put("damageChecks", jsonObject);
                jsonObjectMain.put("notes", PervacioTest.getInstance().getNotesMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pdCommandDetails.setMessage(jsonObjectMain.toString());
            pdCommandDetailsList.add(pdCommandDetails);
        }*/
        /*if (Util.isBatteryQuestionaireRequired()) {
            pdCommandDetailsList.add(getBatteryChecks());
        }*/
        if (PervacioTest.getInstance().getPhysicalDamageResultMap().size() > 0) {
            for (Map.Entry<String, Boolean> phyDmgEntry : PervacioTest.getInstance().getPhysicalDamageResultMap().entrySet()) {
                PDCommandDetails pdCommandDetails = new PDCommandDetails();
                pdCommandDetails.setCommandName(phyDmgEntry.getKey());
                pdCommandDetails.setTestStatus(phyDmgEntry.getValue() ? TestResult.PASS : TestResult.FAIL);
                pdCommandDetails.setSessionId(sessionID);
                pdCommandDetails.setMessage("");
                pdCommandDetails.setStartDateTime(0);
                pdCommandDetails.setEndDateTime(0);
                pdCommandDetailsList.add(pdCommandDetails);
            }

        }
        if (!TextUtils.isEmpty(PervacioTest.getInstance().getSummaryResult())) {
            PDCommandDetails pdCommandDetails = new PDCommandDetails();
            pdCommandDetails.setCommandName("Summary_Delivery_Push");
            pdCommandDetails.setTestStatus(PervacioTest.getInstance().getSummaryResult());
            pdCommandDetails.setSessionId(sessionID);
            pdCommandDetails.setMessage("");
            pdCommandDetails.setStartDateTime(0);
            pdCommandDetails.setEndDateTime(0);
            pdCommandDetailsList.add(pdCommandDetails);
        }

        return pdCommandDetailsList;
    }

    private static void handleTest(String fieldNamePart, PDCommandDetails pdCommandDetails) {
        List<String> ResultItemList = GlobalConfig.getInstance().getItemList();
        try {
            String fieldValue = GlobalConfig.getInstance().getFieldFromItemList(fieldNamePart, ResultItemList);
            if (fieldValue != null) {
                pdCommandDetails.setMessage(fieldValue);
                DLog.d("ServerList  "+ fieldNamePart +" \n  "+fieldValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static PDCommandDetails getBatteryChecks() {
        PDCommandDetails pdCommandDetails = new PDCommandDetails();
        pdCommandDetails.setCommandName(TestName.BATTERYHEALTHCHECK);
        pdCommandDetails.setTestStatus(TestResult.PASS);
        Iterator<Map.Entry<String, String>> iterator = PervacioTest.getInstance().getBatteryCheckResultMap().entrySet().iterator();
        JSONObject jsonObjectMain = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            try {
                jsonObject.put((String) pair.getKey(), "" + (String) pair.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObjectMain.put("questionairre", jsonObject);
            int state = 0;
            String batteryFinalResult = PervacioTest.getInstance().getBatteryFinalResult();
            if ("Replacement Not Needed".equalsIgnoreCase(batteryFinalResult)) {
                state = 3;
            } else if ("Consider Replacing Battery".equalsIgnoreCase(batteryFinalResult)) {
                state = 2;
            } else if ("Replace Battery".equalsIgnoreCase(batteryFinalResult)) {
                state = 1;
            }
            jsonObjectMain.put("batteryState", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pdCommandDetails.setMessage(jsonObjectMain.toString());
        return pdCommandDetails;
    }

    /*
     private PDDiagLogging prepareSessionData() {
        DeviceInfo deviceInfo = DeviceInfo.getInstance(PervacioTest.getAppContext());
        GlobalConfig globalConfig =  GlobalConfig.getInstance() ;

        PDDiagLogging pdDiaglogging = new PDDiagLogging();
        pdDiaglogging.setStoreId(globalConfig.getStoreID());
        pdDiaglogging.setCertified(globalConfig.isCertified());
        pdDiaglogging.setApplicationVersion(BuildConfig.VERSION_NAME);
        pdDiaglogging.setSesionStatus(sessionStatus);
        if (!PervacioTest.getInstance().isOfflineDiagnostics()) {
            pdDiaglogging.setSessionId(globalConfig.getSessionId());
        }
         pdDiaglogging.setStartDateTime(globalConfig.getSessionStartTime());
        pdDiaglogging.setEndDateTime(globalConfig.getSessionEndTime());
        pdDiaglogging.setLastRestart(globalConfig.getLastRestartFromDevice());
        pdDiaglogging.setPlatform("Android");
        pdDiaglogging.setProductName("SSD");
         pdDiaglogging.setAbortReason(pervacioTest.getAbortReasons());



         pdDiaglogging.setSerialNumber(deviceInfo.getSerialNumber());
        pdDiaglogging.setCarriers(deviceInfo.get_carrierName());
        pdDiaglogging.setCategoryName(PervacioTest.getInstance().getSelectedCategory());
        pdDiaglogging.setCompanyName(globalConfig.getCompanyName());
        pdDiaglogging.setDeviceStatus(deviceStatus);
        pdDiaglogging.setDeviceUniqueId(deviceInfo.get_imei());
        pdDiaglogging.setFirmware(deviceInfo.getFirmwareVersion());
        pdDiaglogging.setMarketingName(globalConfig.getDeviceModelName());
        pdDiaglogging.setMake(deviceInfo.get_make());
        pdDiaglogging.setModel(deviceInfo.get_model());

        pdDiaglogging.setOsVersion(deviceInfo.getOSVersionName());

        pdDiaglogging.setUserName("");
        pdDiaglogging.setTransactionName("");
        pdDiaglogging.setCommandDetails(getTestDetails());
        pdDiaglogging.setRanNumber(Util.ranNumber);
         LogUtil.printLog(TAG,"pdDiaglogging getRanNumber : "+pdDiaglogging.getRanNumber());
        return pdDiaglogging;
}
    * */

    public static PDDiagLogging prepareBaseSessionData(String sessionStatus){

        GlobalConfig globalConfig = GlobalConfig.getInstance();
        PervacioTest pervacioTest = PervacioTest.getInstance();

        PDDiagLogging pdDiaglogging = new PDDiagLogging();
        pdDiaglogging.setStoreId(globalConfig.getStoreID());
        pdDiaglogging.setCertified(globalConfig.isCertified());
        pdDiaglogging.setApplicationVersion(BuildConfig.VERSION_NAME);
        pdDiaglogging.setSesionStatus(sessionStatus);

        if (!PervacioTest.getInstance().isOfflineDiagnostics()) {
            pdDiaglogging.setSessionId(globalConfig.getSessionId());
        }

        pdDiaglogging.setPlatform("Android");
        pdDiaglogging.setProductName("SSD");

        pdDiaglogging.setAbortReason(pervacioTest.getAbortReasons());
        pdDiaglogging.setAllLocksRemoved(globalConfig.isLocksRemoved());
        return pdDiaglogging;
    }


    public  PDDiagLogging prepareSessionData(String deviceStatus, String sessionStatus) {
        DeviceInfo deviceInfo = DeviceInfo.getInstance(PervacioTest.getAppContext());
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        PervacioTest pervacioTest = PervacioTest.getInstance();

        PDDiagLogging pdDiaglogging = prepareBaseSessionData(sessionStatus);
        pdDiaglogging.setSerialNumber(deviceInfo.getSerialNumber());
        pdDiaglogging.setCarriers(deviceInfo.get_carrierName());
        pdDiaglogging.setCategoryName(pervacioTest.getSelectedCategory());
        pdDiaglogging.setCompanyName(globalConfig.getCompanyName());
        pdDiaglogging.setDeviceStatus(deviceStatus);

        pdDiaglogging.setDeviceUniqueId(deviceInfo.get_imei());
        pdDiaglogging.setFirmware(deviceInfo.getFirmwareVersion());
        pdDiaglogging.setMarketingName(globalConfig.getDeviceModelName());
        pdDiaglogging.setMake(deviceInfo.get_make());
        pdDiaglogging.setModel(deviceInfo.get_model());

        pdDiaglogging.setStartDateTime(globalConfig.getSessionStartTime());
        pdDiaglogging.setEndDateTime(globalConfig.getSessionEndTime());
        pdDiaglogging.setLastRestart(globalConfig.getLastRestartFromDevice());

        pdDiaglogging.setOsVersion(deviceInfo.getOSVersionName());

        pdDiaglogging.setUserName("");
        pdDiaglogging.setTransactionName(ProductFlowUtil.isTradein()?"Trade_In":"OnDeviceDiagnostics");
        pdDiaglogging.setCommandDetails(getTestDetails());
        pdDiaglogging.setRanNumber(Util.ranNumber);
      //  fixed length error TODO:let it be fix from server and and client side handling
       // File file = DLog.logToFile("SAVE","SAVE","SAVE");

        DLog.d(TAG, "pdDiaglogging getRanNumber : " + pdDiaglogging.getRanNumber());
        return pdDiaglogging;
    }


    private static String getImgFileString(String path) {
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        byte[] baat = null;
        String encodeString = null;
        try {
            bmp = BitmapFactory.decodeFile(path);
            if (bmp == null) {
                return null;
            }

            baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            baat = baos.toByteArray();
            encodeString = Base64.encodeToString(baat, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return encodeString;
    }

}
