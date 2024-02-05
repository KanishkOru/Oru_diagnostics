package com.oruphones.nativediagnostic.communication;



import static com.oruphones.nativediagnostic.BaseActivity.TEST_NAME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.EndingSessionActivity;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.api.CategoryInfo;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.autotests.AutoTestActivity;
import com.oruphones.nativediagnostic.communication.api.PDDiagEvent;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.manualtests.ManualTestsTryActivity;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.DiagConfiguration;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.HybridTestUtils;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.PreferenceUtil;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.monitor.MonitorServiceDocomo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandServer {

    private static String TAG = CommandServer.class.getSimpleName();
    //public static String server_url = null;
    private String sessionID = "";
    private String ackID = "0";
    private static CommandServer instance = null;
    BlockingQueue<PDDiagEvent> postCommands = new LinkedBlockingQueue<>(100);
    private Context mContext = null;
    private boolean keeprunning =false;
    //private CountDownLatch postDataLatch = null;
    private static final long FAIL_WAIT_TIME = 3000;
    private Handler uiHandler = null;
    private Handler baseUIHandler = null;
    private static ArrayList<TestInfo> hybridTestInfos;

    public static final String odd_url = "/bootstrapservlet/diagConfig";
    public static ArrayList<String> onlineTestList = new ArrayList<String>();
    {
        onlineTestList.add(PDConstants.MALWAREAPPS);
        onlineTestList.add(PDConstants.ADWAREAPPS );
        onlineTestList.add(PDConstants.RISKYAPPS);
    }
    private CommandServer(Context context) {
        super();
        this.mContext = context;
        // server_url = GlobalConfig.getInstance().getServerUrl();
        // server_url =  "http://54.244.244.114:8080";
        //server_url =  GlobalConfig.getInstance().getServerUrl();
        // server_url =  "https://asddemo.pervacioone.com";
        // server_url =  "https://asddemo.pervacioone.com";
    }


    public  synchronized static CommandServer getInstance(Context context) {
        if (instance == null) {
            instance = new CommandServer(context);
        }
        return instance;
    }

    public static void clearInstance() {
            instance = null;
    }

    public void stopAllThreads() {
        keeprunning = false;
    }

    public void reset() {
        sessionID = "";
        ackID = "0";
        keeprunning = false;
    }

    public void postEventData(String eventName, Object eventData) {
        DLog.d(TAG, "Event Data: " +eventData +" \n  Event Name: " + eventName);
        PDDiagEvent pdDiagEvent = new PDDiagEvent();
        pdDiagEvent.setEventname(eventName);
        pdDiagEvent.setEventdata(eventData);
        try {
            postCommands.put(pdDiagEvent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void setBaseUIHandler(Handler handler) {
        baseUIHandler = handler;
    }
    public void setUIHandler(Handler handler) {
        uiHandler = handler;
    }

    public String connect(final String data, final String channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkAuthentication(data, channel);
            }
        }).start();
        return "Connecting to server...";
    }

    public void checkAuthentication(final String data, final String channel) {
        String autData;
        if(channel.contains("das")) {
            autData = getData(data, "/api/wcom/das/"+data);
        } else {
            autData = postData(data, "/api/wcom/dcs");
        }
        DLog.d(TAG, "Server Response autData = " + autData);
        if (autData != null) {
            try {
                JSONObject serverResponse = new JSONObject(autData);
                DLog.d(TAG, "serverResponse = " + serverResponse.toString());
                Bundle bundleData = new Bundle();
                if (serverResponse.has("sessionId") && serverResponse.has("pin")) {
                    sessionID = serverResponse.getString("sessionId");
                    bundleData.putString(PreferenceUtil.EX_RESULT, PreferenceUtil.EX_PIN);
                    bundleData.putString(PreferenceUtil.EX_PIN, serverResponse.getString("pin"));

                    start();
                }else if(serverResponse.has("type")){

                    if("sid".equalsIgnoreCase(serverResponse.getString("type"))){
                        sessionID = serverResponse.getString("value");
                        bundleData.putString(PreferenceUtil.EX_RESULT, PreferenceUtil.EX_AUTH_SUCCESS);
                        start();
                    }
                    if("ERROR".equalsIgnoreCase(serverResponse.getString("type"))){
                        bundleData.putString(PreferenceUtil.EX_RESULT, PreferenceUtil.EX_AUTH_ERROR);
                        bundleData.putString("value", serverResponse.getString("value"));
                    }
                }

                if(bundleData.containsKey(PreferenceUtil.EX_RESULT)){
                    sendMessageToBaseHandler(bundleData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            DLog.e(TAG, "Empty/Null response");
        }
    }


    private void sendMessageToBaseHandler(Bundle data){
        Message msg = Message.obtain();
        msg.setData(data);
        if(baseUIHandler!=null){
            baseUIHandler.sendMessage(msg);
        }else{
            DLog.e(TAG, "sendMessageToHandler : uiHandler is null ");
        }

    }


    public String postData(String data, String channel) {
        String text = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(GlobalConfig.getInstance().getServerUrl() + channel);
            DLog.d(TAG, "Post Server URL: "+GlobalConfig.getInstance().getServerUrl() + channel +"?"+data);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("charset", "UTF-8");
            conn.setRequestProperty("content-type","application/json");
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("Authorization", "Basic "+GlobalConfig.getInstance().getServerKey());
            conn.setRequestProperty("sessionId", sessionID);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            wr.write(data);
            wr.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                DLog.e(TAG, "POST Response code = " + responseCode);
                return postDataRecursive(data, channel);
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
            DLog.e(TAG, "Post Response = " + text);
        } catch (Exception ex) {
            DLog.e(TAG, "POST Exception "+ex.getMessage());
            return postDataRecursive(data, channel);
        } finally {
            try {
                if(reader!=null) {
                    reader.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        DLog.d(TAG, "Post Response: " + text);
        return text;
    }

    private String postDataRecursive(String data, String channel){
        if(!keeprunning){
            return null;
        }
        try {
            Thread.sleep(FAIL_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return postData(data,channel);
    }

    synchronized public String getData(String data, String channel) {
        String text = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(GlobalConfig.getInstance().getServerUrl() + channel/* + data*/);
            DLog.i(TAG, "GET Server URL: "+GlobalConfig.getInstance().getServerUrl() + channel/*+data*/);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Authorization", "Basic "+GlobalConfig.getInstance().getServerKey());
            conn.setRequestProperty("sessionId", sessionID);
            conn.setRequestProperty("ackId", ackID);
            conn.setConnectTimeout(60000);
            // Log.d(TAG, "Waiting For Next Command.....");
            int responseCode = conn.getResponseCode();

            DLog.e(TAG, "GET Response code "+responseCode+" SID:"+conn.getRequestProperty("sessionId"));
            if (responseCode != 200) {
                DLog.e(TAG, "NON 200 Response code "+responseCode);
                //return getDataRecursive(data, channel);
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
            DLog.e(TAG, "Command Received From Server; " + text);
        } catch (Exception ex) {
            DLog.e(TAG, "GET Exception "+ex.getMessage());
            //return getDataRecursive(data, channel);
            return null;
        } finally {
            try {
                if(reader!=null) {
                    reader.close();
                }
            } catch (Exception ex) {
                DLog.e(TAG, "finally Exception "+ex.getMessage());
                ex.printStackTrace();
            }
        }
        DLog.d(TAG, "End of GET.............................");
        return text;
    }

    private String getDataRecursive(String data, String channel){
        if(!keeprunning){
            return null;
        }
        try {
            Thread.sleep(FAIL_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getData(data, channel);
    }

    private void postDataToServer() {

        String postResult = "";
        while (keeprunning) {
            try {
                //postDataLatch = new CountDownLatch(1);
                DLog.d(TAG, "Waiting for post event.....");

                //postDataLatch.await();
                if (keeprunning) {
                    /*while (!outCommands.isEmpty()) {
                        postResult = postData((new Gson()).toJson(outCommands.removeFirst()), "/api/wcom/dpd");
                    }*/
                    PDDiagEvent postdata= postCommands.take();
                    DLog.d(TAG, "Running cmd:");
                    postResult = postData((new Gson()).toJson(postdata), "/api/wcom/dpd");
                }
            } catch (Exception ex) {
                DLog.e(TAG, "Exception while postDataToServer");
                ex.printStackTrace();
            }
        }
    }

     private void getNextCommandFromServer() {
        DLog.i(TAG, "Starting GET Thread.........");
        (new Thread() {
            @Override
            public void run() {
                while (keeprunning) {
                    String command = getData("?sessionId="+sessionID+"&ackId="+ ackID, "/api/wcom/dnc");
                    DLog.e(TAG, "Command Received From Server: " + command);
                    if (command == null) {
                        DLog.i(TAG, "cmd is null");
                    } else {
                        try {
                            if(!keeprunning)
                                break;
                            handleCommandFromServer(command);
                        } catch (Exception ex) {
                            DLog.e(TAG, "Exception while start.........");
                            ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        DLog.e(TAG, "Exception while sleep.........");
                    }
                    DLog.e(TAG, "End of While.........");
                }
            }
        }).start();
    }

    private void handleCommandFromServer(String command) {
        JSONObject commandObj = null;
        String commandString = null;
        String cmdData = null;
        String cmdName = null;
        logLargeString("handleCommandFromServer: command data : "+command);
        try {
            commandObj = new JSONObject(command);
            if (commandObj.has("ackId"))               //ack retrieve from JSON
                ackID = commandObj.getString("ackId");
            commandObj = new JSONObject(command);
            if (commandObj.has("data"))               // data part retrieve, which have commandData & CommandName
                commandString = commandObj.getString("data");
            if(/*"CMD_END_SESSION".equalsIgnoreCase(commandString) ||*/ "INVALID_SESSION".equalsIgnoreCase(commandString)){
                    stopAllThreads();
            }
            commandObj = new JSONObject(commandString);
            if (commandObj.has("cmddata"))          //retrieve commandData from JSON DataPart
                cmdData = commandObj.getString("cmddata");
            if (commandObj.has("cmdname"))          //retrieve commandName from JSON Data Part
                cmdName = commandObj.getString("cmdname");
        } catch (JSONException e) {
            DLog.e(TAG, "handleCommandFromServer: Exception in preparing JSON "+e.getMessage());
            //e.printStackTrace();
        }

        if (cmdName != null) {
            if ("CMD_DIAG_CONFIG".equalsIgnoreCase(cmdName) && !loadConfigDone) {
                DiagConfiguration diagConfiguration = PervacioTest.getInstance().getObjectFromData(
                        cmdData.toString(), DiagConfiguration.class);
                loadConfig(diagConfiguration, false, mContext);
                loadConfigDone = true;
                PervacioTest.getInstance().initializeApps();
            } else if (cmdName.equalsIgnoreCase("CMD_START_AUTO_TEST")) {
                PervacioTest.getInstance().setSelectedCategory(cmdData);
                Intent intent = new Intent(mContext, AutoTestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                MonitorServiceDocomo.startService(mContext);
            }else if("CMD_SUMMARY_DATA".equalsIgnoreCase(cmdName) && !TextUtils.isEmpty(cmdData)){
                try {
                    JSONObject jsonObject = new  JSONObject(cmdData);
                    if(jsonObject.has("sessionHistoryId")){
                        GlobalConfig.getInstance().setSessionId(jsonObject.getLong("sessionHistoryId"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (cmdName.equalsIgnoreCase("CMD_LAUNCH_SCREEN") || cmdName.equalsIgnoreCase("CMD_LAUNCH_RESOLUTION")) {
                if (cmdData != null) {
                    if("TERMS_CONDITIONS".equalsIgnoreCase(cmdData)){
                        Bundle bundleData = new Bundle();
                        bundleData.putString(PreferenceUtil.EX_RESULT, PreferenceUtil.TERMS_CONDITIONS);
                        sendMessageToBaseHandler(bundleData);
                    }else {
                        handleLaunchScreen(cmdData, mContext);
                    }

                }
            }
           // command to update manual test results
            if (cmdName.equalsIgnoreCase("CMD_TEST_STATUS")) {
                if (cmdData != null) {
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    msg.what = 700;
                    PDTestResult testResult = (PDTestResult) PervacioTest.getInstance().getObjectFromData(cmdData, new TypeToken<PDTestResult>() {
                    }.getType());
                    if(TestName.HARDKEYTEST.equalsIgnoreCase(testResult.getName()) || TestName.SOFTKEYTEST.equalsIgnoreCase(testResult.getName()))
                        uiHandler.sendMessage(msg);
                    else
                        baseUIHandler.sendMessage(msg);
                }
            }
            if (cmdName.equalsIgnoreCase("CMD_LAUNCH_SETTING") || cmdName.equalsIgnoreCase("CMD_SET_SETTING")) {
                if (cmdData != null) {
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }

            if (cmdName.equalsIgnoreCase("CMD_LAUNCH_MANUAL_TEST")) {
                if (cmdData != null) {
                    Intent actIntent = new Intent(mContext, ManualTestsTryActivity.class);
                    actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    actIntent.putExtra(TEST_NAME, cmdData);
                    mContext.startActivity(actIntent);
                }
            }

            if (cmdName.equalsIgnoreCase("CMD_START_MANUAL_TEST")) {
                if (cmdData != null) {
                    handleLaunchScreen(cmdData, mContext);
                }
            }

            if(cmdName.equalsIgnoreCase("CMD_UNINSTALL_APPS")) {
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    data.putString("cmdName", cmdName);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }

            if("CMD_START_MANUAL_TEST_WARNING".equalsIgnoreCase(cmdName)) {
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    data.putString("cmdName", cmdName);
                    Message msg = Message.obtain();
                    msg.what = 900;
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }



            if("CMD_END_SESSION".equalsIgnoreCase(cmdName)) {
                MonitorServiceDocomo.stopService(mContext);
                stopAllThreads();
                Intent intent = new Intent(mContext, EndingSessionActivity.class);
                intent.putExtra("Exit", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

                //  if(cmdData != null){
/*                Bundle data = new Bundle();
                data.putString("result", cmdData);
                data.putString("cmdName", cmdName);
                Message msg = Message.obtain();
                msg.setData(data);
                uiHandler.sendMessage(msg);
                stopAllThreads();*/
                //handleLaunchScreen(cmdName);
                // }
            }

            if(cmdName.equalsIgnoreCase("CMD_DELETE_FILES")) {
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    data.putString("cmdName", cmdName);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }
            if(cmdName.equalsIgnoreCase("CMD_EMAIL_FOR_SUMMARY")) {
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    data.putString("cmdName", cmdName);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }
            if(cmdName.equalsIgnoreCase("CMD_FIRMWARE_STATUS")) {
                if(cmdData != null){
                    String title;
                    if(cmdData.equalsIgnoreCase("YES")){
                        title = "Firmware";
                    }else{
                        title = "Firmware Mismatch";
                    }
                    GlobalConfig.getInstance().setLatestFirmware(cmdData.equalsIgnoreCase("YES"));
                    //BaseActivity.setToolBardDisplayName(TestName.FIRMWARE,title);
                }
            }
            if(cmdName.equalsIgnoreCase("CMD_PHYSICAL_DAMAGE_CHECK")) {
                DLog.i(TAG,"cmdName.equalsIgnoreCase(CMD_PHYSICAL_DAMAGE_CHECK) case");
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString("result", cmdData);
                    DLog.i(TAG,"cmdName.equalsIgnoreCase(CMD_PHYSICAL_DAMAGE_CHECK) case cmdData "+cmdData);
                    DLog.i(TAG,"cmdName.equalsIgnoreCase(CMD_PHYSICAL_DAMAGE_CHECK) case uiHandler "+uiHandler);
                    Message msg = Message.obtain();
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                }
            }
            //{"cmdname":"CMD_MANUAL_TEST_SELECTION","cmddata":["AccelerometerTest","CallTest","LightSensorTest"]}

            if("CMD_MANUAL_TEST_SELECTION".equalsIgnoreCase(cmdName)) {
                DLog.i(TAG,"cmdName.equalsIgnoreCase(CMD_MANUAL_TEST_SELECTION) ");
                if(cmdData != null){
                    Bundle data = new Bundle();
                    data.putString(PreferenceUtil.EX_SELECTION_LIST, cmdData);
                    sendMessageToHandler(data);
                }
            }else if("CMD_SCROLL_SUMMARY_PAGE".equalsIgnoreCase(cmdName)){
                if(!TextUtils.isEmpty(cmdData)){
                    try {
                        JSONObject jsonObject = new JSONObject(cmdData);
                        if(jsonObject.has(PreferenceUtil.EX_SECTION_NAME) && jsonObject.has(PreferenceUtil.EX_SECTION_INDEX)){
                            Bundle data = new Bundle();
                            data.putString(PreferenceUtil.EX_SECTION_NAME, jsonObject.getString(PreferenceUtil.EX_SECTION_NAME));
                            data.putInt(PreferenceUtil.EX_SECTION_INDEX, jsonObject.getInt(PreferenceUtil.EX_SECTION_INDEX));
                            data.putInt(PreferenceUtil.EX_INDEX, jsonObject.getInt("itemInd"));
                            sendMessageToHandler(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }else if("CMD_SCROLL_TEST_LIST_PAGE".equalsIgnoreCase(cmdName)){
                if(!TextUtils.isEmpty(cmdData)){
                    int centerIndex = Integer.parseInt(cmdData);
                    Bundle data = new Bundle();
                    data.putInt(PreferenceUtil.EX_INDEX, centerIndex);
                    sendMessageToHandler(data);
                }
            }else if("CMD_CLOSE_ACCESSORIES_POPUP".equalsIgnoreCase(cmdName)){
                if(!TextUtils.isEmpty(cmdData)){
                    Bundle data = new Bundle();
                    data.putString(PreferenceUtil.EX_COMMAND_NAME, cmdName);
                    sendMessageToHandler(data);
                }
            }else if("CMD_SHOW_DEVICE_HISTORY_DETAILS".equalsIgnoreCase(cmdName)){
                if(!TextUtils.isEmpty(cmdData)){
                    int centerIndex = Integer.parseInt(cmdData);
                    Bundle data = new Bundle();
                    data.putInt(PreferenceUtil.EX_INDEX, centerIndex);
                    sendMessageToHandler(data);
                }
            }else if("CMD_SHOW_ALL_DIAG_HISTORY_DETAILS".equalsIgnoreCase(cmdName) || "CMD_SHOW_WIFI_SETTINGS_SCREEN".equalsIgnoreCase(cmdName) || "CMD_CHECK_WIFI_STATUS".equalsIgnoreCase(cmdName)){
                    Bundle data = new Bundle();
                    data.putString(PreferenceUtil.EX_COMMAND_NAME, cmdName);
                    sendMessageToHandler(data);
            }else if("CMD_CHECK_INTERNET_RECTIFIED".equalsIgnoreCase(cmdName) || "CMD_NETWORK_SIM_SWAP".equalsIgnoreCase(cmdName)){
                Bundle data = new Bundle();
                data.putString("result", cmdName);
                Message msg = Message.obtain();
                msg.setData(data);
                uiHandler.sendMessage(msg);
            }

        }
    }
    private void sendMessageToHandler(Bundle data){
        Message msg = Message.obtain();
        msg.setData(data);
        if(uiHandler!=null){
            uiHandler.sendMessage(msg);
        }else{
            DLog.e(TAG, "sendMessageToHandler : uiHandler is null ");
        }

    }

    public static void handleLaunchScreen(String cmdData, Context context) {
        Class<?> activity = null;
        try {
            DLog.i(TAG, "++CmdData.............."+cmdData/*+" \n++Mapping.screen.............."+Mapping.screen.toString()*/);
            String args[] = TestUtil.screen.get(cmdData);
            String classname = args[0];
            String mCurrentTest=args[1];
            activity =  Class.forName(classname);
            Intent intent = new Intent(context, activity);
            String param = args[1];
            intent.putExtra("param", param);
            if(mCurrentTest!=null)
                intent.putExtra(TEST_NAME, mCurrentTest);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
/*            if("END_SESSION".equalsIgnoreCase(cmdData))
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);*/
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            DLog.e(TAG, "Loading class exception.............."+e.getMessage());
        }
    }

    public void start() {
        DLog.e(TAG, "Starting Next Command Thread..............");
        //SendDatatoBaseActivity();
        try {
            keeprunning = true;
            (new Thread() {
                @Override
                public void run() {
                    postDataToServer();
                }
            }).start();

            getNextCommandFromServer();

            /*if (postDataLatch != null) {
                postDataLatch.countDown();
            }*/
        } catch (Exception ex) {
            DLog.e(TAG, "Exception while post or getting next command...");
            ex.printStackTrace();
        }
    }
    void   logLargeString(String data) {
        final int CHUNK_SIZE = 1000;  // Typical max logcat payload.
        int offset = 0;
        while (offset + CHUNK_SIZE <= data.length()) {
            DLog.d(TAG, data.substring(offset, offset += CHUNK_SIZE));
        }
        if (offset < data.length()) {
            DLog.d(TAG, data.substring(offset));
        }
    }

    private boolean loadConfigDone = false;
    public static void loadConfig(DiagConfiguration pdDiagConfig, boolean isOffline, Context context) {
        DLog.d(TAG, "loadConfig..................."+pdDiagConfig.toString());
        HashMap<String, ArrayList> autoTestMap = new HashMap<String, ArrayList>();
        HashMap<String, ArrayList> manualTestMap = new HashMap<String, ArrayList>();
        ArrayList<CategoryInfo> categoryList = new ArrayList<>();
        GlobalConfig globalConfig = GlobalConfig.getInstance();

        if(pdDiagConfig.getPhysicalTests() != null)
        globalConfig.setPhysicalTests(pdDiagConfig.getPhysicalTests());

        if(pdDiagConfig.getCertified() != null)
            globalConfig.setCertified(pdDiagConfig.getCertified());

        if(pdDiagConfig.getOwnershipCheckProceed() != null)
            globalConfig.setOwnershipCheckProceed(pdDiagConfig.getOwnershipCheckProceed());
        try {
            globalConfig.setLastRestartThresholdVal(pdDiagConfig.getLastRestartThresholdDays());
            globalConfig.setCurrentServerTime(pdDiagConfig.getCurrentServerTime());
            globalConfig.setLongDateFormat(pdDiagConfig.getLongDateFormat());
            globalConfig.setShortDateFormat(pdDiagConfig.getShortDateFormat());
            DLog.d(TAG,"ThresholdValue:"+globalConfig.getLastRestartThresholdVal());
            DLog.d(TAG,"CurrentServerTime:"+globalConfig.getCurrentServerTime());
            DLog.d(TAG,"LongDateFormat:"+globalConfig.getLongDateFormat());
            DLog.d(TAG,"ShortDateFormat:"+globalConfig.getShortDateFormat());

            globalConfig.setUnusedAppsThresholdVal(pdDiagConfig.getUnusedAppsThreshold());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*RAP & RAN */
        globalConfig.setEnableRAPFeature(pdDiagConfig.isEnableRAPFeature());
        globalConfig.setGenerateRAN(pdDiagConfig.isGenerateRAN());
        globalConfig.setUserInteractionSessionTimeOut(pdDiagConfig.getSessionTimeOut());

        if(pdDiagConfig.getSummaryDisplayElements()!=null){
            globalConfig.setSummaryDisplayElements(pdDiagConfig.getSummaryDisplayElements());
        }

        /*IMEI Stolen*/
        //globalConfig.setImeiBlackListStatus(pdDiagConfig.getImeiBlackListStatus());

        globalConfig.setCheckIMEIStatus(pdDiagConfig.isCheckIMEIStolenStatus());

        /*Show Analyze Device Screen*/
        globalConfig.showAnalyzeDeviceScreen(pdDiagConfig.isShowAnalyzeDeviceScreen());

        globalConfig.setDiagTradeInEnabled(pdDiagConfig.isEnableDiagTradeInFlow());
        globalConfig.setEnableTradeIn(pdDiagConfig.isEnableTradeInFlow());
        globalConfig.setEmailSummary(pdDiagConfig.isEnableEmailSummary());
        globalConfig.setEnableCSAT(pdDiagConfig.isEnableCSAT());
        /*User agent id*/
        globalConfig.setAgentUserId(pdDiagConfig.getAgentUserId());
        if(pdDiagConfig.getBatteryDesignCapacity() != null)
            globalConfig.setBatteryDesignCapacity(pdDiagConfig.getBatteryDesignCapacity());

        if (pdDiagConfig.getMarketingName() != null && !"".equalsIgnoreCase(pdDiagConfig.getMarketingName()))
            globalConfig.setDeviceModelName(pdDiagConfig.getMarketingName());
        else
            globalConfig.setDeviceModelName(DeviceInfo.getInstance(context).get_model());

        if(pdDiagConfig.getDeviceSupported() != null)
        globalConfig.setDeviceSupported(pdDiagConfig.getDeviceSupported());

        DLog.d(TAG, "CallTestNumber ="+ pdDiagConfig.getCallTestNumber());
        if (pdDiagConfig.getCallTestNumber() != null && !"".equalsIgnoreCase(pdDiagConfig.getCallTestNumber())) {
            globalConfig.setCallTestNumber(pdDiagConfig.getCallTestNumber());
            //AppUtils.printLog(TAG, "CALL TEST NUMBER: " + Util.CALL_NUMBER, null, AppUtils.LogType.INCOMING_RESPONSE, AppUtils.LogSubType.DETAILS);
        }
        DLog.e(TAG, "Call TestInfo Number:" + pdDiagConfig.getCallTestNumber());
        if (pdDiagConfig.getPkeys() != null && !pdDiagConfig.getPkeys().equalsIgnoreCase("")) {
            globalConfig.setDeviceHardKeys(pdDiagConfig.getPkeys());
        } else {
            globalConfig.setDeviceHardKeys("VOLUME_UP,VOLUME_DOWN,POWER");
        }
        //AppUtils.printLog(TAG, "Device Hard Keys: " + mHardKeys, null, AppUtils.LogType.INCOMING_RESPONSE, AppUtils.LogSubType.DETAILS);
        if (pdDiagConfig.getVkeys() != null)
            globalConfig.setDeviceSoftKeys(pdDiagConfig.getVkeys());
        else {
            globalConfig.setDeviceSoftKeys("MENU,BACK,HOME");
        }
        if (pdDiagConfig.getAutobrightnessAvl() != null) {
            globalConfig.setAutobrightnessAvailable(pdDiagConfig.getAutobrightnessAvl());
            DLog.e(TAG, "autobrightnessMode Available " + pdDiagConfig.getAutobrightnessAvl());
        }

        if(pdDiagConfig.isLatestFirmware()!=null){
            globalConfig.setLatestFirmware(pdDiagConfig.isLatestFirmware());
            DLog.d(TAG,"Firmware="+pdDiagConfig.isLatestFirmware());
        }
        globalConfig.setLatestFirmwareVersion(pdDiagConfig.getLatestFirmwareVersion());
        globalConfig.setServerWarVersion(pdDiagConfig.getServerWARVersion());

        if(pdDiagConfig.getBatteryConfig() != null){
            globalConfig.setBatteryConfig(pdDiagConfig.getBatteryConfig());
            DLog.d(TAG,"BatteryConfig="+pdDiagConfig.getBatteryConfig().toString());
        }

        if(pdDiagConfig.getPreferredCarrier() != null){
            globalConfig.setPreferredCarrier(pdDiagConfig.getPreferredCarrier());
            DLog.d(TAG,"PreferredCarrier="+pdDiagConfig.getPreferredCarrier());
        }

        if(pdDiagConfig.getHybridTests() != null){
            globalConfig.setHybridTests(pdDiagConfig.getHybridTests());
            DLog.d(TAG,"Hybrid Tests="+pdDiagConfig.getHybridTests());
        }

        globalConfig.setSohRange(pdDiagConfig.getSohRange());
        globalConfig.setRunAllManualTests(pdDiagConfig.isRunAllManualTests());
        globalConfig.setCertified(pdDiagConfig.getCertified());
        globalConfig.setStoreMailId(pdDiagConfig.getStoreMailId());
        globalConfig.setCountryMailId(pdDiagConfig.getCountryMailId());
        if(ProductFlowUtil.isQuickBatteryRequired()) {
            QuickBatteryTestInfo quickBatteryTestInfo = ODDUtils.setQuickBatteryInfo();
            PervacioTest.getInstance().setQuickBatteryTestInfo(quickBatteryTestInfo);
        }
        DLog.e(TAG, "____________________________________________________");
        ArrayList<TestInfo> auto_testInfos, manual_testInfos, auto_tests_display, manual_tests_display, resolution_list;
        hybridTestInfos = new ArrayList<>();
        List<DiagConfiguration.Issue> catagories = pdDiagConfig.getCategory();
        Iterator itr = catagories.iterator();
        while (itr.hasNext()) {
            auto_testInfos = new ArrayList<TestInfo>();
            manual_testInfos = new ArrayList<TestInfo>();
            DiagConfiguration.Issue issue = (DiagConfiguration.Issue) itr.next();
            DLog.d(TAG,"CategoryInfo: "+issue.getDisplayname());

            auto_testInfos = getTestInfoList(issue.getAutoTests(), isOffline);
            manual_testInfos = getTestInfoList(issue.getManualTests(), isOffline);
            if(hybridTestInfos.size() > 0) {
                auto_testInfos.addAll(hybridTestInfos);
                hybridTestInfos.clear();
            }

            /*if((!issue.getIssueName().equalsIgnoreCase(PDConstants.RUN_ALL_DIAGNOSTICS) &&
                    !issue.getIssueName().equalsIgnoreCase(PDConstants.QUICK_CHECK)) ||  //skipping RUN_ALL_DIAGNOSTICS as it is not a part of catagory now
                    (issue.getIssueName().equalsIgnoreCase(PDConstants.RUN_ALL_DIAGNOSTICS) && !pdDiagConfig.isShowAnalyzeDeviceScreen())) */ //include RUN_ALL_DIAGNOSTICS into catagory for BPLUS
                    categoryList.add((new CategoryInfo(issue.getIssueName(), issue.getDisplayname(), issue.getDescription())));
            globalConfig.getCategoryNameMap().put(issue.getIssueName(), issue.getDisplayname());
            autoTestMap.put(issue.getIssueName(), auto_testInfos);
            manualTestMap.put(issue.getIssueName(), manual_testInfos);

        }
        LinkedList<Map<String, Boolean>> fivePointCheckList = pdDiagConfig.getFivePointCheck();
        LinkedHashMap<String, Boolean> fivePointCheckMap = new LinkedHashMap<>();
        for (int i = 0; i < fivePointCheckList.size(); i++) {
            try {
                Object fivePointCheckKey = fivePointCheckList.get(i).keySet().toArray()[0];
                Boolean fivePointCheckValue = fivePointCheckList.get(i).get((String) fivePointCheckKey);
                fivePointCheckMap.put((String) fivePointCheckKey, fivePointCheckValue);
            } catch (Exception e) {
                DLog.e(TAG, "Exception in parsing fivepointchecklist:" + e.getMessage());
            }
        }
        globalConfig.setFivePointCheckList(fivePointCheckMap);
        DiagConfiguration.Issue checkMyDevice = pdDiagConfig.getCheckMyDevice();
        ArrayList<TestInfo> checkMyDeviceAutoTests = getTestInfoList(checkMyDevice.getAutoTests(), isOffline);
        ArrayList<TestInfo> checkMyDeviceManualTests = getTestInfoList(checkMyDevice.getManualTests(), isOffline);
        checkMyDeviceAutoTests.addAll(hybridTestInfos); //Addinf Hybrid Tests to Run All.
        autoTestMap.put(checkMyDevice.getIssueName(), checkMyDeviceAutoTests);
        manualTestMap.put(checkMyDevice.getIssueName(), checkMyDeviceManualTests);
        globalConfig.getCategoryNameMap().put(checkMyDevice.getIssueName(), checkMyDevice.getDisplayname());

        DiagConfiguration.Issue tradeInCategory = pdDiagConfig.getTradeInCategory();
        if(tradeInCategory != null) {
            autoTestMap.put(tradeInCategory.getIssueName(), getTestInfoList(tradeInCategory.getAutoTests(), isOffline));
            manualTestMap.put(tradeInCategory.getIssueName(), getTestInfoList(tradeInCategory.getManualTests(), isOffline));
            globalConfig.getCategoryNameMap().put(tradeInCategory.getIssueName(), tradeInCategory.getDisplayname());
        }

        DiagConfiguration.Issue verifyCategory = pdDiagConfig.getVerifyDevice();
        if(verifyCategory != null) {
            hybridTestInfos.clear();
            ArrayList<TestInfo> verifyDeviceAutoTests = getTestInfoList(verifyCategory.getAutoTests(), isOffline);
            ArrayList<TestInfo> verifyDeviceManualTests = getTestInfoList(verifyCategory.getManualTests(), isOffline);
            if(hybridTestInfos.size() > 0) {
                verifyDeviceAutoTests.addAll(hybridTestInfos);
                hybridTestInfos.clear();
            }
            autoTestMap.put(verifyCategory.getIssueName(), verifyDeviceAutoTests);
            manualTestMap.put(verifyCategory.getIssueName(), verifyDeviceManualTests);
            globalConfig.getCategoryNameMap().put(verifyCategory.getIssueName(), verifyCategory.getDisplayname());
        }


        globalConfig.setCategoryList(categoryList);
        globalConfig.setAutoTestMap(autoTestMap);
        globalConfig.setManualTestMap(manualTestMap);

        //globalConfig.setInitCompleted(true);
        PervacioTest.getInstance().saveGlobalConfig(globalConfig);








//        /**  all test list logs  */
//
//        int i=0;
//        while(i< autoTestMap.get("RunAllDiagnostics").size()){
//            Log.d("##00","test name : :  "+ autoTestMap.get("RunAllDiagnostics").get(i));
//        i++;
//    }

//        i=0;
//        while(i< manualTestMap.get("RunAllDiagnostics").size()){
//            Log.d("##00","test name : :  "+ manualTestMap.get("RunAllDiagnostics").get(i));
//            i++;
//        }



    }

    public static  ArrayList<TestInfo> getTestInfoList(List<DiagConfiguration.Test> testSet, boolean isOffline) {
        HybridTestUtils hybridTestUtils = new HybridTestUtils();
        ArrayList<TestInfo> testInfos = new ArrayList<TestInfo>();
        if (testSet != null) {
            Iterator aitr = testSet.iterator();
            //AppUtils.printLog(TAG, "Auto TestInfo List: ", null, AppUtils.LogType.INCOMING_RESPONSE, AppUtils.LogSubType.DETAILS);
            DLog.d(TAG,"Auto TestInfo List: ");
            while (aitr.hasNext()) {
                DiagConfiguration.Test atest = (DiagConfiguration.Test) aitr.next();
                if (PervacioTest.getInstance().isFeatureAvailable(atest.getName())) {
                    DLog.d(TAG, atest.getName()+" - "+atest.getDisplayname());
                    TestInfo info =  new TestInfo(atest.getName(), atest.getDisplayname(), atest.getTestTryMessage(),atest.getTestResultMessage());
                    if (!(isOffline && onlineTestList.contains(atest.getName()))) {
                       //info.setCategory(atest.getCategory());
                        testInfos.add(info);
                        if(hybridTestUtils.isHybridTestsEnabled() && hybridTestUtils.isHybridTest(atest.getName())) {
                            hybridTestInfos.add(info);
                        }

                    }
                    GlobalConfig.getInstance().getTestNameMap().put(atest.getName(), atest.getDisplayname());
                    GlobalConfig.getInstance().getTestInfoMap().put(atest.getName(), info);
                }
            }
        }
        return testInfos;
    }

public void processPim(String pin){
        connect(pin, "/api/wcom/das");
    }
    public void sendDeviceDataToServer(DeviceInformation deviceInformation){
        postEventData("DEVICE_INFO", deviceInformation);
    }
    public void sendTermAndConditionInfo(boolean isAgreed){
        postEventData("CMD_TERMS_CONDITIONS", isAgreed?"AGREED":"DISAGREED");
    }
    public void sendDeviceInfoPhysicalDamageResponseToServer() {
        postEventData("CMD_PHYSICAL_DAMAGE_CHECK", "SELECTED");
    }

    public void sendWifiStatus(String testName, boolean isOn) {
        PDTestResult pdTestResult = new PDTestResult();
        pdTestResult.setName(testName);
        pdTestResult.setStatus(isOn ? "ON" : "OFF");
        postEventData("TEST_RESULT", pdTestResult);
    }

    public void saveServerInfo(){
        PreferenceUtil.putString(PreferenceUtil.EX_SESSION_ID,sessionID);
        PreferenceUtil.putString(PreferenceUtil.EX_ACK_ID,ackID);
    }

    public void restoreServerInfo(){
        sessionID = PreferenceUtil.getString(PreferenceUtil.EX_SESSION_ID);
        ackID = PreferenceUtil.getString(PreferenceUtil.EX_ACK_ID);
    }
/*    public synchronized void SendDatatoBaseActivity() {
        Message MenssageToActivity = uiHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(pin, " PIN");
        Log.d("raghava",pin);
        MenssageToActivity.setData(bundle);
        uiHandler.sendMessage(MenssageToActivity);

    }*/
}