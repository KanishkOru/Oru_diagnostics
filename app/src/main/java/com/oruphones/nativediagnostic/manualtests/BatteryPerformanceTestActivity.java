package com.oruphones.nativediagnostic.manualtests;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.models.tests.BatteryPerformanceResult;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.pervacio.batterydiaglib.api.BatteryTest;
import com.pervacio.batterydiaglib.api.BatteryTestResult;


public class BatteryPerformanceTestActivity extends ManualTestsProgressBarActivity {

    private boolean testStarted = false;
    private boolean testCompleted= false;
    private static String TAG = BatteryPerformanceTestActivity.class.getSimpleName();
    private String  testResult= "";
    String inputJson = "";
    int minBatteryLevel = 30;
    AlertDialog userDialog = null;
    public GlobalConfig globalConfig;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        String batteryConfigStr = "{\"sohThreshold\":80,\"deepdiveConfig\":{\"percentDrop\":2,\"minBatteryLevel\":30},\"gldProfile\":\"{\"configuration\":{\"asset_info\":{\"apilevel\":23,\"buildnumber\":\"ONE A2003_24_171024\",\"unique_id\":867290023176976,\"serialnumber\":\"c109ecab\",\"model\":\"ONE A2003\",\"osversion\":\"ONE A2003_24_171024\",\"platform\":\"Android\",\"manufacturer\":\"OnePlus\",\"androidversion\":\"6.0.1\"},\"tests\":{\"test\":{\"batstats\":{\"stat\":[{\"current\":1094048,\"temperature\":390,\"time\":4,\"battery\":62,\"voltage\":3763},{\"current\":1563253,\"temperature\":400,\"time\":24723,\"battery\":61,\"voltage\":3626}]},\"displayname\":\"Battery Drain Test\",\"name\":\"DrainTest\",\"draintestdone\":true,\"endtime\":\"06-07-2020 11:13:21\",\"starttime\":\"06-02-2020 12:45:0\",\"reporttime\":1581939472716}},\"battery_info\":{\"technology\":\"Li-ion\"}}}\"}";
/*        //String batteryConfigStr = "{\\\"sohThreshold\\\":80,\\\"deepdiveConfig\\\":{\\\"percentDrop\\\":2,\\\"minBatteryLevel\\\":30},\\\"gldProfile\\\":\\\"{\\\"configuration\\\":{\\\"asset_info\\\":{\\\"apilevel\\\":23,\\\"buildnumber\\\":\\\"ONE A2003_24_171024\\\",\\\"unique_id\\\":867290023176976,\\\"serialnumber\\\":\\\"c109ecab\\\",\\\"model\\\":\\\"ONE A2003\\\",\\\"osversion\\\":\\\"ONE A2003_24_171024\\\",\\\"platform\\\":\\\"Android\\\",\\\"manufacturer\\\":\\\"OnePlus\\\",\\\"androidversion\\\":\\\"6.0.1\\\"},\\\"tests\\\":{\\\"test\\\":{\\\"batstats\\\":{\\\"stat\\\":[{\\\"current\\\":1094048,\\\"temperature\\\":390,\\\"time\\\":4,\\\"battery\\\":62,\\\"voltage\\\":3763},{\\\"current\\\":1563253,\\\"temperature\\\":400,\\\"time\\\":24723,\\\"battery\\\":61,\\\"voltage\\\":3626}]},\\\"displayname\\\":\\\"Battery Drain Test\\\",\\\"name\\\":\\\"DrainTest\\\",\\\"draintestdone\\\":true,\\\"endtime\\\":\\\"06-07-2020 11:13:21\\\",\\\"starttime\\\":\\\"06-02-2020 12:45:0\\\",\\\"reporttime\\\":1581939472716}},\\\"battery_info\\\":{\\\"technology\\\":\\\"Li-ion\\\"}}}\\\"}";
        //JsonObject batteryConfigJson = (new Gson()).fromJson(batteryConfigStr, JsonObject.class);
        JsonObject batteryConfigJson = new JsonParser().parse(batteryConfigStr).getAsJsonObject();
        GlobalConfig.getInstance().setBatteryConfig(batteryConfigJson);*/
        globalConfig= GlobalConfig.getInstance();
        if(globalConfig.getBatteryConfig() != null) {
            JsonObject batteryConfig = globalConfig.getBatteryConfig();
            DLog.d(TAG ,"ServerInputJson: "+batteryConfig.toString());
            inputJson = prepareGoldenProfile(batteryConfig);
            DLog.d(TAG, "ApiFormattedInputJson: "+inputJson);
            setBatteryConfig(inputJson);
        }
        else
        DLog.d(TAG, "inputJson is null");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mInteractionMonitor.setIsEligibleForTimeOut(false,0);
        DLog.d(TAG, "In OnResume testStarted="+testStarted);
        if(!testStarted && !testCompleted){
            startDeepDiveTest();
        }
        if(testStarted && testCompleted){
            if(isAirplaneModeOn(getApplicationContext())){
                showUserMessage(R.string.airplane_mode_off);
            } else {
                postResult();
            }
        }
    }

    private void postResult() {
        manualTestResultDialog(TestName.BATTERYPERFORMANCE, testResult, BatteryPerformanceTestActivity.this);
    }

    private void startDeepDiveTest(){
        DLog.d(TAG, "DeepDive pre check...");
        if(getCurrentBatteryLevel(BatteryPerformanceTestActivity.this) < minBatteryLevel){
            DLog.d(TAG, "Not enough battery, need at least 30%");
            //Toast.makeText(BatteryPerformanceTestActivity.this, "Not enough battery, need at least 30% ", Toast.LENGTH_LONG).show();
            showUserMessage(R.string.battery_level_alert);
        } else if (isUSBConnected()) {
            DLog.d(TAG, "Please disconnect USB Cable");
            //Toast.makeText(BatteryPerformanceTestActivity.this, "Please disconnect USB Cable", Toast.LENGTH_LONG).show();
            showUserMessage(R.string.usb_connected_alert);
        } else if (!isAirplaneModeOn(getApplicationContext())) {
            DLog.d(TAG, "Please switch on Airplane Mode");
            //Toast.makeText(BatteryPerformanceTestActivity.this, "Please switch on Airplane Mode", Toast.LENGTH_LONG).show();
            showUserMessage(R.string.airplane_mode_on);
        } else {
            DLog.d(TAG, "Starting DeepDive test...");
            testStarted = true;
            DLog.d(TAG, "BatteryConfig: " + inputJson);
            BatteryTest.getInstance().performDeepDiveTest(BatteryPerformanceTestActivity.this, inputJson);
        }
    }

    private String prepareGoldenProfile(JsonObject serverJson) {
        JsonArray batstatsArray = null;
        String inputJson = "";
        try {
            if (serverJson != null && !"".equalsIgnoreCase(serverJson.toString())) {
                inputJson = serverJson.toString();
                if(serverJson.has("gldProfile")) {
                    String goldenProfilestring = serverJson.getAsJsonPrimitive("gldProfile").getAsString();
                    DLog.d(TAG, "goldenProfilestring: " + goldenProfilestring);
                    JsonObject rootJson = (new Gson()).fromJson(goldenProfilestring, JsonObject.class);
                    if (rootJson != null && rootJson.has("configuration")) {
                        JsonObject configuration = rootJson.getAsJsonObject("configuration");
                        if (configuration.has("tests")) {
                            JsonObject tests = configuration.getAsJsonObject("tests");
                            if (tests.has("test")) {
                                JsonObject test = tests.getAsJsonObject("test");
                                if (test.has("batstats")) {
                                    JsonObject batstats = test.getAsJsonObject("batstats");
                                    if (batstats.has("stat")) {
                                        batstatsArray = batstats.getAsJsonArray("stat");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            DLog.e(TAG,"Input Json Exception: "+e.getStackTrace());
        }
        if(batstatsArray != null){
            try {
                DLog.d(TAG,"batstatsArray string: " + batstatsArray.toString());
                //goldenProfileJson = "{\"batteryConfig\":{\"sohThreshold\":80,\"deepdiveConfig\":{\"percentDrop\":2,\"minBatteryLevel\":40},\"gldProfile\":{\"batstats\":" + batstatsArray.toString()+"}}}";
                if(serverJson != null) {
                    serverJson.remove("gldProfile");
                    JsonObject batStats = new JsonObject();
                    batStats.add("batstats",batstatsArray);
                    serverJson.add("gldProfile", batStats);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonObject finalJson = new JsonObject();
        finalJson.add("batteryConfig", serverJson);
        inputJson = finalJson.toString();
        DLog.d(TAG,"Formatted InputJson: " + inputJson);
        return inputJson;
    }

    private void setBatteryConfig(String inputJsonString) {
        try {
            if (inputJsonString != null && !"".equalsIgnoreCase(inputJsonString)) {
                JsonObject inputJson = (JsonObject)(new Gson()).fromJson(inputJsonString, JsonObject.class);
                JsonObject batteryConfigRoot = new JsonObject();
                try {
                    if (inputJson.has("batteryConfig")) {
                        batteryConfigRoot = inputJson.get("batteryConfig").getAsJsonObject();
                    } else {
                        DLog.d(TAG, "root element batteryConfig not found....");
                    }
                } catch (NumberFormatException var9) {
                    DLog.e(TAG, var9.getMessage());
                }
                if(batteryConfigRoot == null )
                    return;
                    try {
                        if (batteryConfigRoot.has("sohThreshold")) {
                            String sohThreshold = batteryConfigRoot.get("sohThreshold").getAsString().trim();
                            DLog.d(TAG, "sohThreshold=" + sohThreshold);
                        }
                    } catch (NumberFormatException var9) {
                        DLog.e(TAG, var9.getMessage());
                    }

                    JsonObject deepdiveConfig;
                    try {
                        if (batteryConfigRoot.has("deepdiveConfig")) {
                            deepdiveConfig = batteryConfigRoot.get("deepdiveConfig").getAsJsonObject();
                            String percentDrop;
                            if (deepdiveConfig.has("percentDrop")) {
                                percentDrop = deepdiveConfig.get("percentDrop").getAsString().trim();
                                DLog.d(TAG, "percentDrop=" + percentDrop);
                            }

                            if (deepdiveConfig.has("minBatteryLevel")) {
                                String minBatteryLevelStr = deepdiveConfig.get("minBatteryLevel").getAsString();
                                try {
                                    minBatteryLevel = Integer.parseInt(minBatteryLevelStr);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    minBatteryLevel = 30;
                                }
                                DLog.d(TAG, "minBatteryLevel=" + minBatteryLevel);
                            }
                        }
                    } catch (NumberFormatException var8) {
                        DLog.e(TAG, var8.getMessage());
                    }
            }
        } catch (Exception var10) {
            DLog.e(TAG, var10.getMessage());
        }
    }

    private void showUserMessage(final int message_id) {
        DLog.d(TAG, "In showUserMessage"+"message_id="+message_id);
        if(userDialog != null)
            userDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(BatteryPerformanceTestActivity.this);
        String messageString;
        if(message_id == R.string.battery_level_alert) {
            messageString = getResources().getString(message_id).replace("$%",minBatteryLevel+"%");
        } else {
            messageString = getResources().getString(message_id);
        }
            builder.setTitle(R.string.alert)
                .setMessage(messageString)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DLog.d(TAG, "In showUserMessage onClick...");
                        if(userDialog != null)
                            userDialog.dismiss();
                        if(message_id == R.string.airplane_mode_on || message_id == R.string.airplane_mode_off){
                            DLog.d(TAG, "In starting Airplane settings...");
                            registerReceiver(mAirplaneModeChangeReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
                            startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                        } else {
                            onResume();
                        }
                    }
                });
        if(message_id == R.string.battery_level_alert){
           builder.setNegativeButton(R.string.str_skip, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    skip_msg_id = R.string.skip_status;
                    testResult = TestResult.SKIPPED;
                    postResult();
                }
            }
            );
         }

        AlertDialog userDialog = builder.create();
        userDialog.setCancelable(false);
        userDialog.setCanceledOnTouchOutside(false);
        userDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.d(TAG,"In onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if(resultCode == BatteryTest.BATTERY_TEST_RESULT_CODE) {
            testCompleted = true;
            BatteryTestResult batteryTestResult = (BatteryTestResult) data.getSerializableExtra(BatteryTestResult.INFO_EXTRA);
/*            if(batteryTestResult.getResultCode() == BatteryTestResult.RESULT_CODE_ERROR) {
                Log.d("BatteryTest", "ErrorCode: " + batteryTestResult.getErrorCode());
*//*                if(batteryTestResult.getErrorCode() == BatteryTestResult.ERROR_CODE_LOW_BATTERY_LEVEL)
                    //showUserMessage(R.string.battery_level_alert);
                if(batteryTestResult.getErrorCode() == BatteryTestResult.ERROR_CODE_USB_CONNECTED)
                    //showUserMessage(R.string.usb_connected_alert);
                if(batteryTestResult.getErrorCode() == BatteryTestResult.ERROR_CODE_AIRPLANE_MODE_OFF)
                    //showUserMessage(R.string.airplane_mode_alert);*//*
            } else if(batteryTestResult.getResultCode() == BatteryTestResult.RESULT_CODE_UNKNOWN) {
                testResult = TestResult.FAIL;
                Log.d("BatteryTest","Battery Test Fails due to unknown reasons...");
                //showUserMessage(R.string.test_fail_unknown_reason_alert);
            }*/
            String batteryHealth = "NA";
            if(batteryTestResult.getResultCode() == BatteryTestResult.RESULT_CODE_PASS) {
                testResult = TestResult.PASS;
                //batteryHealth = "Good";
            } else if(batteryTestResult.getResultCode() == BatteryTestResult.RESULT_CODE_FAIL) {
                testResult = TestResult.FAIL;
                //batteryHealth = "Bad";
            } else {
                skip_msg_id = R.string.not_supported_status;
                testResult = TestResult.NOTSUPPORTED;
                //batteryHealth = "Unknow";
            }

            batteryHealth = batteryTestResult.getBatteryHealth();

            DLog.d(TAG,"::::::::::::::BatteryPerformanceTest Result::::::::::::::");
            DLog.d(TAG, "Test Result: "+batteryHealth);
            DLog.d(TAG, "Result Info: ");
            DLog.d(TAG, "Result Code: "+batteryTestResult.getResultCode());
            DLog.d(TAG, "Error Code: "+batteryTestResult.getErrorCode());
            JsonObject batteryConfig = globalConfig.getBatteryConfig();
            DLog.d(TAG, "ServerInputJson: "+batteryConfig.toString());
            DLog.d(TAG, "ApiFormattedInputJson: "+inputJson);
            DLog.d(TAG,"BatterySoHByE: "+ batteryTestResult.getBatterySohByE());
            DLog.d(TAG,"BatterySoHByT: "+ batteryTestResult.getBatterySohByT());
            DLog.d(TAG,"BatteryCalculatedCapacity: "+ batteryTestResult.getBatteryCalculatedCapacity());
            DLog.d(TAG,"BatteryDesignCapacity: "+ batteryTestResult.getBatteryDesignCapacity());
            DLog.d(TAG,"TestProfile: "+ batteryTestResult.getBatteryTestProfile());
            DLog.d(TAG,"::::::::::::::BatteryPerformanceTest Result End::::::::::::::");
            BatteryPerformanceResult.getInstance().setResultCode(batteryTestResult.getResultCode());
            BatteryPerformanceResult.getInstance().setErrorCode(batteryTestResult.getErrorCode());
            BatteryPerformanceResult.getInstance().setBatteryResult(testResult);
            BatteryPerformanceResult.getInstance().setBatteryHealth(batteryHealth);
            BatteryPerformanceResult.getInstance().setBatteryDesignCapacity(batteryTestResult.getBatteryDesignCapacity());
            BatteryPerformanceResult.getInstance().setBatteryCalculatedCapacity(batteryTestResult.getBatteryCalculatedCapacity()>batteryTestResult.getBatteryDesignCapacity()?batteryTestResult.getBatteryDesignCapacity():batteryTestResult.getBatteryCalculatedCapacity()); // CAP for CalculatedCapacity
            BatteryPerformanceResult.getInstance().setBatterySOH(batteryTestResult.getBatterySoh()>100?100:batteryTestResult.getBatterySoh()); //100 CAP for SOH
            BatteryPerformanceResult.getInstance().setBatterySohByE(batteryTestResult.getBatterySohByE()>100?100:batteryTestResult.getBatterySohByE()); //100 CAP for SOHE
            BatteryPerformanceResult.getInstance().setBatterySohByT(batteryTestResult.getBatterySohByT()>100?100:batteryTestResult.getBatterySohByT()); //100 CAP for SOHT
            BatteryPerformanceResult.getInstance().setBatteryTestProfile(batteryTestResult.getBatteryTestProfile());
            BatteryPerformanceResult.getInstance().setBatteryConfig(inputJson);
/*
            Log.d("BatteryTest","  ");

            Log.d("BatteryTest","::::::::::::::QuickBatteryTest Result::::::::::::::");
            BatteryDiagConfig batteryDiagConfig = new BatteryDiagConfig.BatteryDiagConfigBuilder(true).build();
            ActivityResultInfo activityResultInfo = new QuickTestComputeEngine(
                    batteryDiagConfig, BatteryUtil.getBatteryCapacity(context)).computeQuickTestSoh();
            Log.d("BatteryTest", "Test Result: "+activityResultInfo.getTestResult().name());
            Log.d("BatteryTest", "Quick Test Possible: "+BatteryTest.getInstance().isQuickTestPossible(this));
            Log.d("BatteryTest", "Result Info: "+activityResultInfo.toString());
            Log.d("BatteryTest","::::::::::::::QuickBatteryTest Result End::::::::::::::");

            Log.d("BatteryTest","  ");

            Log.d("BatteryTest","::::::::::::::BatteryApiTest Result::::::::::::::");
            Log.d("BatteryTest", "Test Result: "+BatteryUtil.getBatteryHealthByAndroidAPI(this));
            Log.d("BatteryTest", "Result Info: "+BatteryUtil.getBatteryStatFromApi(this).toString());
            Log.d("BatteryTest","::::::::::::::BatteryApiTest Result End::::::::::::::");*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mInteractionMonitor.setIsEligibleForTimeOut(true,globalConfig.getUserInteractionSessionTimeOut());
            if(alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
                if(mAirplaneModeChangeReceiver != null)
                unregisterReceiver(mAirplaneModeChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.BATTERYPERFORMANCE);
    }

    @Override
    protected void stopButtonClicked() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    private int getCurrentBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    private boolean isUSBConnected() {
        int pluggedIn = -1;
        try {
            Intent intent1 = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            pluggedIn = intent1.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        } catch (Exception ex) {
            DLog.e(TAG, "IsPowerConnected"+ex.toString());
        }
        return pluggedIn == BatteryManager.BATTERY_PLUGGED_AC || pluggedIn == BatteryManager.BATTERY_PLUGGED_USB;
    }

    BroadcastReceiver mAirplaneModeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                try {
                    if (mAirplaneModeChangeReceiver != null)
                        unregisterReceiver(mAirplaneModeChangeReceiver);
                    Intent newIntent = getIntent();
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(newIntent);
                } catch (Exception e) {
                    DLog.e(TAG, "Exception : " + e.getStackTrace().toString());
                }
            }
        }
    };

}
