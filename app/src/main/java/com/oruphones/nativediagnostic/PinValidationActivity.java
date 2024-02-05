package com.oruphones.nativediagnostic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.api.BuildConfig;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.ServerConfig;
import com.oruphones.nativediagnostic.autotests.AutoTestActivity;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.DiagConfiguration;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.StartLocationAlert;
import com.oruphones.nativediagnostic.util.ThemeUtil;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;
import com.pervacio.batterydiaglib.util.NetworkUtil;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

//import javax.annotation.Nullable;


/**
 * Created by Pervacio on 22-Mar-18.
 * 170899
 */

public class PinValidationActivity extends BaseActivity implements StartLocationAlert.LocationCallback {
    private String storeId = "";
    private LocationManager locManager;
    private static String TAG = PinValidationActivity.class.getSimpleName();
    private GpsListener gpsListner;
    ProgressDialog progressDialog;
    private Runnable showDialogRun;
    private Handler showDialogHandler;
    private static final String EX_DECIDE_FLOW = "decide_flow";
    private static final String EX_RESTART = "restart";
    private static final String[] COUNTRY_LIST = {"Argentina", "Chile", "Colombia", "Ecuador", /*"Germany",*/"Mexico", "Peru", /*"United Kingdom",*/ "Uruguay"};


    public static void startActivity(Activity activity, boolean decideFlow, boolean restart) {
        Intent intent = new Intent(activity, PinValidationActivity.class);
        Bundle bundle = new Bundle();
        if (decideFlow) {
            bundle.putBoolean(EX_DECIDE_FLOW, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        } else if (restart) {
            bundle.putBoolean(EX_RESTART, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialogHandler = new Handler();
        if (showDialogRun == null)
            showDialogRun = new Runnable() {
                @Override
                public void run() {
                    initValidations();
                }
            };
        showDialogHandler.postDelayed(showDialogRun, 1000);
    }


    @Override
    protected void onStop() {
        if (showDialogHandler != null) {
            showDialogHandler.removeCallbacks(showDialogRun);
            showDialogRun = null;
            showDialogHandler = null;
        }

        super.onStop();
    }

    public void initValidations() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean(EX_DECIDE_FLOW)) {
            decideAppFlow();
            return;
        }


        progressDialog = new ProgressDialog(PinValidationActivity.this);
        locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gpsListner = new GpsListener();
        if (Util.gpsLoginRequired()) {
            progressDialog.setTitle(getResources().getString(R.string.connecting_to_server));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (DeviceInfo.getInstance(this).isGPSEnabled()) {
                if (NetworkUtil.isOnline()) {
                    gpsLocationGranted(DeviceInfo.getInstance(this).isGPSEnabled());
                }
            } else {
                new StartLocationAlert(PinValidationActivity.this, PinValidationActivity.this);
            }
        } else {
            storeId = GlobalConfig.getInstance().getStoreID();
            if (!TextUtils.isEmpty(storeId)) {
                PinValidationTask pinValidationTask = new PinValidationTask(storeId);
                pinValidationTask.execute();
            } else {  // if we don't have store id stored in  globalConfig
                showDialogue();
            }
        }
    }

    public void showDialogue() {
        dialog = new Dialog(PinValidationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_storeid_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Button pin_alert_ok = (Button) dialog.findViewById(R.id.pin_alert_ok);
        Button pin_alert_cancel = (Button) dialog.findViewById(R.id.pin_alert_cancel);
        final EditText edirText = (EditText) dialog.findViewById(R.id.store_id);
        pin_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeId = edirText.getText().toString();
                PinValidationTask pinValidationTask = new PinValidationTask(storeId);
                pinValidationTask.execute();
                if (dialog != null)
                    dialog.dismiss();
            }

        });

        pin_alert_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();
                Intent intent1 = new Intent(getApplicationContext(), EndingSessionActivity.class);
                intent1.putExtra("Exit", true);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home_splash;
    }

    @Override
    protected String getToolBarName() {
        return "Pin Validation";
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return true;
    }


    @Override
    public void onLocationConnectionFailed(boolean resultPassFail, String reason) {
        displayStoreIdList();
    }

    public class PinValidationTask extends AsyncTask<Void, Void, ServerConfig> {
        HttpURLConnection urlConnection;
        URL url;
        DeviceInformation deviceInformation;
        String storeid = null;

        public PinValidationTask(String _storeid) {
            storeid = _storeid;
        }

        @Override
        protected void onPreExecute() {
            DLog.d(TAG, "Device config data -- onPreExecute");
            deviceInformation = DeviceInfo.getInstance(getApplicationContext()).getDeviceInfromationDataFromDeviceInfo(GlobalConfig.getInstance(), PinValidationActivity.this.getApplicationContext());
            deviceInformation.setStoreId(storeid);
           // GlobalConfig globalConfig1 = GlobalConfig.getInstance();
            globalConfig.clearItemList();
            globalConfig.clearAllTestIntegers();

            if (GlobalConfig.getInstance().isVerification()) {
                DLog.d(TAG, "Customer Verification View");
                globalConfig.addItemToList("Verification Type: Seller Verification View");
                deviceInformation.setTransectionName(PDConstants.VERIFY);
            } else if (GlobalConfig.getInstance().isBuyerVerification()) {
                DLog.d(TAG, "Seller Verification View");
                globalConfig.addItemToList("Verification Type: Buyer Verification View");
                deviceInformation.setTransectionName(PDConstants.BUYER_VERIFY);
            } else if (GlobalConfig.getInstance().isFinalVerify()) {
                DLog.d(TAG, "Final Verify Called");
                deviceInformation.setTransectionName(PDConstants.FINAL_VERIFY);
            } else {
                DLog.d(TAG, "Diagnostics View");
                globalConfig.addItemToList("Verification Type: Diagnostics View");
                deviceInformation.setTransectionName(BuildConfig.PRODUCT_NAME);

            }
            progressDialog.setTitle(getResources().getString(R.string.connecting_to_server));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected ServerConfig doInBackground(Void... params) {
            ODDNetworkModule networkModule = ODDNetworkModule.getInstance();
            /*ServerConfig result = networkModule.getDeviceConfig(deviceInformation);
            return result;*/

            String deviceInfoJson = new Gson().toJson(deviceInformation);
            globalConfig.addItemToList("Device Information: "+deviceInfoJson);
            DLog.d(TAG, "Device Info: " + new Gson().toJson(deviceInformation));
            DLog.d(TAG, "storeId" + storeid);

            DLog.d(TAG, "Device config data -- " + new Gson().toJson(deviceInformation));

            try {
                Call<ServerConfig> call = networkModule.getDiagServerApiInterface().getDeviceConfig(deviceInformation);
//                LogUtil.printLog(TAG, "API payload: " + deviceInformation.toString());
                Response<ServerConfig> serverConfigResponse = call.execute();
//                LogUtil.printLog(TAG, "API Response in try: " + serverConfigResponse.body());


                return serverConfigResponse.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                return jsonToMap();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
            return null;
        }
//
//        public ServerConfig jsonToMap() throws JSONException {
//
////            ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"Full Diagnostics\",\"description\":\"Run all checks on the device.\",\"autoTests\":[{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus 8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyy HH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Valid store id\",\"sessionId\":165123}", ServerConfig.class);
//       // ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"Full Diagnostics\",\"description\":\"Run all checks on the device.\",\"autoTests\":[{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus 8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyy HH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Valid store id\",\"sessionId\":165123}", ServerConfig.class);
////         1   ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"BatteryCharging\",\"displayname\":\"Battery & Charging\",\"description\":\"Checks the device's battery health and charging capacity.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"},{\"name\":\"LiveWallpaperTest\",\"displayname\":\"Live Wallpaper\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT039\"},{\"name\":\"ScreenTimeoutTest\",\"displayname\":\"Screen Timeout\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT037\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"NFCOffTest\",\"displayname\":\"NFC Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT047\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"QuickBatteryAutoTest\",\"displayname\":\"Battery\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT093\"}],\"manualTests\":[{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"WallChargingTest\",\"displayname\":\"Charging\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT061\"}]},{\"issueName\":\"SystemCrash\",\"displayname\":\"Freeze & Crash\",\"description\":\"Checks storage space and identify reasons that cause the device to freeze or crash.\",\"autoTests\":[{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"UnusedApp\",\"displayname\":\"Unused Apps\",\"category\":\"Apps\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT064\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"RAMMemoryTest\",\"displayname\":\"RAM Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT161\"},{\"name\":\"SDCardTest\",\"displayname\":\"SD Card Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT162\"}],\"manualTests\":[]},{\"issueName\":\"Connectivity\",\"displayname\":\"Connectivity\",\"description\":\"Checks the status of device's capability to connect to a network, GPS etc.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"BluetoothOnTest\",\"displayname\":\"Bluetooth Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT044\"},{\"name\":\"NFCOnTest\",\"displayname\":\"NFC Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT046\"},{\"name\":\"GPSOnTest\",\"displayname\":\"GPS Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT048\"},{\"name\":\"WLANOnTest\",\"displayname\":\"Wi-Fi Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT042\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"}],\"manualTests\":[]},{\"issueName\":\"AudioVibrate\",\"displayname\":\"Audio & Vibrate\",\"description\":\"Checks the device's audio components.\",\"autoTests\":[{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"}],\"manualTests\":[{\"name\":\"MicTest\",\"displayname\":\"Microphone (Primary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Microphone (Secondary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"}]},{\"issueName\":\"Camera\",\"displayname\":\"Camera\",\"description\":\"Checks the device's front, rear camera, and flash.\",\"autoTests\":[{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"RAMMemoryTest\",\"displayname\":\"RAM Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT161\"},{\"name\":\"SDCardTest\",\"displayname\":\"SD Card Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT162\"},{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"}],\"manualTests\":[{\"name\":\"RearCameraVideoTest\",\"displayname\":\"Rear camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT027\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"FrontCameraVideoTest\",\"displayname\":\"Front camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT029\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"}]},{\"issueName\":\"DisplayTouch\",\"displayname\":\"Display & Touch\",\"description\":\"Checks the device's display and touch functionality.\",\"autoTests\":[{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"}],\"manualTests\":[{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]},{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"Full Diagnostics\",\"description\":\"Run all checks on the device.\",\"autoTests\":[{\"name\":\"GyroscopeSensorTest\",\"displayname\":\"Gyroscope\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT054\"},{\"name\":\"MagneticSensorTest\",\"displayname\":\"Magnetic Sensor\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT055\"},{\"name\":\"GameRotationSensorTest\",\"displayname\":\"Game Rotation Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT123\"},{\"name\":\"GeomagneticRotationSensorTest\",\"displayname\":\"Geomagnetic Rotation Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT124\"},{\"name\":\"RotationVectorSensorTest\",\"displayname\":\"Rotation Vector Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT125\"},{\"name\":\"LinearAccelerationSensorTest\",\"displayname\":\"Linear Acceleration Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT127\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"UnusedApp\",\"displayname\":\"Unused Apps\",\"category\":\"Apps\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT064\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"RAMMemoryTest\",\"displayname\":\"RAM Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT161\"},{\"name\":\"SDCardTest\",\"displayname\":\"SD Card Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT162\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"},{\"name\":\"LiveWallpaperTest\",\"displayname\":\"Live Wallpaper\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT039\"},{\"name\":\"ScreenTimeoutTest\",\"displayname\":\"Screen Timeout\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT037\"},{\"name\":\"BarometerTest\",\"displayname\":\"Barometer\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT056\"},{\"name\":\"BluetoothOnTest\",\"displayname\":\"Bluetooth Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT044\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"NFCOffTest\",\"displayname\":\"NFC Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT047\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"},{\"name\":\"QuickBatteryAutoTest\",\"displayname\":\"Battery\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT093\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"},{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"AccelerometerTest\",\"displayname\":\"Accelerometer\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT018\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"MicTest\",\"displayname\":\"Microphone (Primary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Microphone (Secondary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"LightSensorTest\",\"displayname\":\"Ambient light\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT084\"},{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"HardKeysTest\",\"displayname\":\"Hard Keys Test\",\"category\":\"Keys\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT151\"},{\"name\":\"SoftKeysTest\",\"displayname\":\"Soft Keys Test\",\"category\":\"Keys\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT152\"},{\"name\":\"CallTest\",\"displayname\":\"Call Test\",\"category\":\"System\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT153\"},{\"name\":\"DeadPixelTest\",\"displayname\":\"Dead Pixel Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT154\"},{\"name\":\"DiscolorationTest\",\"displayname\":\"Discoloration Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT155\"},{\"name\":\"ScreenBurnTest\",\"displayname\":\"Screen Burn Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT156\"},{\"name\":\"SPenTest\",\"displayname\":\"SPen Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT158\"},{\"name\":\"SPenHoveringTest\",\"displayname\":\"SPen Hovering Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT159\"},{\"name\":\"TSPHoveringTest\",\"displayname\":\"TSP Hovering Test\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT160\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"},{\"name\":\"FrontFlashTest\",\"displayname\":\"Front Flash Test\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT157\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"WallChargingTest\",\"displayname\":\"Charging\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT061\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"RAMMemoryTest\",\"displayname\":\"RAM Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT161\"},{\"name\":\"SDCardTest\",\"displayname\":\"SD Card Memory Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT162\"},{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"BarometerTest\",\"displayname\":\"Barometer\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT056\"},{\"name\":\"MagneticSensorTest\",\"displayname\":\"Magnetic Sensor\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT055\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"},{\"name\":\"GyroscopeSensorTest\",\"displayname\":\"Gyroscope\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT054\"},{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"AccelerometerTest\",\"displayname\":\"Accelerometer\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT018\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"MicTest\",\"displayname\":\"Microphone (Primary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Microphone (Secondary)\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"},{\"name\":\"RearCameraVideoTest\",\"displayname\":\"Rear camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT027\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"FrontCameraVideoTest\",\"displayname\":\"Front camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT029\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]},\"physicalTests\":[],\"marketingName\":\"OnePlus 8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyy HH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Valid store id\",\"sessionId\":165123}", ServerConfig.class);
//////          2  ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"Full Diagnostics\",\"description\":\"Run all checks on the device.\",\"autoTests\":[{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus 8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyy HH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Valid store id\",\"sessionId\":165123}", ServerConfig.class);
////  ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"FullDiagnostics\",\"description\":\"Runallchecksonthedevice.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"GenuineOS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"}],\"manualTests\":[{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyyHH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Validstoreid\",\"sessionId\":165123}",ServerConfig.class);
////        ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"FullDiagnostics\",\"description\":\"Runallchecksonthedevice.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"GenuineOS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"LastRestart\",\"displayname\":\"LastRestart\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Cameraflash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyyHH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Validstoreid\",\"sessionId\":165123}",ServerConfig.class);
////
////            ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"FullDiagnostics\",\"description\":\"Runallchecksonthedevice.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"GenuineOS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"LastRestart\",\"displayname\":\"LastRestart\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Cameraflash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"AccelerometerTest\",\"displayname\":\"Accelerometer\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT018\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Fingerprintsensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"OnePlus8T\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"KB2001_11_C.15\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME,MENU\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":4500,\"lastRestartThresholdDays\":30,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":25190000,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyyHH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"aman@zenro.co.jp\",\"countryemail\":\"aman@zenro.co.jp\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"aaa\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Validstoreid\",\"sessionId\":165123}",ServerConfig.class);
//             ServerConfig rObject = new Gson().fromJson("{\"data\":{\"category\":[{\"issueName\":\"BatteryCharging\",\"displayname\":\"Battery & Charging\",\"description\":\"Checks the devices battery health and charging capacity.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi Hardware \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"},{\"name\":\"LiveWallpaperTest\",\"displayname\":\"Live Wallpaper\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT039\"},{\"name\":\"ScreenTimeoutTest\",\"displayname\":\"Screen Timeout\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT037\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"NFCOffTest\",\"displayname\":\"NFC Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT047\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"QuickBatteryAutoTest\",\"displayname\":\"Quick Battery Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT093\"}],\"manualTests\":[{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"WallChargingTest\",\"displayname\":\"Charging\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT061\"}]},{\"issueName\":\"SystemCrash\",\"displayname\":\"Freeze & Crash\",\"description\":\"Checks storage space and identify reasons that cause the device to freeze or crash.\",\"autoTests\":[{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi Hardware \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"UnusedApp\",\"displayname\":\"Unused Apps\",\"category\":\"Apps\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT064\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"}],\"manualTests\":[]},{\"issueName\":\"Connectivity\",\"displayname\":\"Connectivity\",\"description\":\"Checks the status of devices capability to connect to a network, GPS etc.\",\"autoTests\":[{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi Hardware\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"BluetoothOnTest\",\"displayname\":\"Bluetooth Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT044\"},{\"name\":\"NFCOnTest\",\"displayname\":\"NFC Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT046\"},{\"name\":\"GPSOnTest\",\"displayname\":\"GPS Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT048\"},{\"name\":\"WLANOnTest\",\"displayname\":\"Wi-Fi Ready\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT042\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"}],\"manualTests\":[{\"name\":\"DimmingTest\",\"displayname\":\"Dimming\",\"category\":\"Connectivity\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT080\"}]},{\"issueName\":\"AudioVibrate\",\"displayname\":\"Audio & Vibrate\",\"description\":\"Checks the devices audio components.\",\"autoTests\":[{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi H\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"}],\"manualTests\":[{\"name\":\"MicTest\",\"displayname\":\"Primary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Secondary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"}]},{\"issueName\":\"WifiHardwaretest\",\"displayname\":\"Wifi Hardware\",\"description\":\"It Will check if Device Wifi Is Working or not\",\"autoTests\":[{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"}],\"manualTests\":[{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"RearCameraVideoTest\",\"displayname\":\"Rear camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT027\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"FrontCameraVideoTest\",\"displayname\":\"Front camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT029\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"}]},{\"issueName\":\"DisplayTouch\",\"displayname\":\"Display & Touch\",\"description\":\"Checks the devices display and touch functionality.\",\"autoTests\":[{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"}],\"manualTests\":[{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]},{\"issueName\":\"RunAllDiagnostics\",\"displayname\":\"Full Diagnostics\",\"description\":\"Run all checks on the device.\",\"autoTests\":[{\"name\":\"GyroscopeSensorTest\",\"displayname\":\"Gyroscope\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT054\"},{\"name\":\"MagneticSensorTest\",\"displayname\":\"Magnetic Sensor\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT055\"},{\"name\":\"GameRotationSensorTest\",\"displayname\":\"Game Rotation Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT123\"},{\"name\":\"GeomagneticRotationSensorTest\",\"displayname\":\"Geomagnetic Rotation Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT124\"},{\"name\":\"RotationVectorSensorTest\",\"displayname\":\"Rotation Vector Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT125\"},{\"name\":\"LinearAccelerationSensorTest\",\"displayname\":\"Linear Acceleration Sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT127\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"OPTIMIZABLE\",\"testCode\":\"PT034\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"UnusedApp\",\"displayname\":\"Unused Apps\",\"category\":\"Apps\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT064\"},{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"ScreenBrightnesTest\",\"displayname\":\"Brightness\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT038\"},{\"name\":\"LiveWallpaperTest\",\"displayname\":\"Live Wallpaper\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT039\"},{\"name\":\"ScreenTimeoutTest\",\"displayname\":\"Screen Timeout\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT037\"},{\"name\":\"BarometerTest\",\"displayname\":\"Barometer\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT056\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"NFCOffTest\",\"displayname\":\"NFC Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT047\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"},{\"name\":\"QuickBatteryAutoTest\",\"displayname\":\"Quick Battery Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT093\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"AccelerometerTest\",\"displayname\":\"Accelerometer\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT018\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"LightSensorTest\",\"displayname\":\"Ambient light\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT084\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"MicTest\",\"displayname\":\"Primary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Secondary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"},{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"RearCameraVideoTest\",\"displayname\":\"Rear camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT027\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"FrontCameraVideoTest\",\"displayname\":\"Front camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT029\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"WallChargingTest\",\"displayname\":\"Charging\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT061\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]}],\"checkMyDevice\":{\"issueName\":\"CheckMyDevice\",\"displayname\":\"asd.flow.name.checkmydevice\",\"description\":\"asd.flow.desc.checkmydevice\",\"autoTests\":[{\"name\":\"InternalStorageCapacityTest\",\"displayname\":\"Int. Storage Capacity\",\"category\":\"Storage\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT071\"},{\"name\":\"LastRestart\",\"displayname\":\"Last Restart \",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"PASS\",\"testCode\":\"PT034\"},{\"name\":\"SIMCardTest\",\"displayname\":\"SIM Card\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT016\"},{\"name\":\"GenuineOSTest\",\"displayname\":\"Genuine OS\",\"category\":\"OS\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT010\"},{\"name\":\"IMEITest\",\"displayname\":\"IMEI Test\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT032\"},{\"name\":\"BarometerTest\",\"displayname\":\"Barometer\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT056\"},{\"name\":\"MagneticSensorTest\",\"displayname\":\"Magnetic Sensor\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT055\"},{\"name\":\"BluetoothOffTest\",\"displayname\":\"Bluetooth Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT045\"},{\"name\":\"GPSOffTest\",\"displayname\":\"GPS Status\",\"category\":\"Settings\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT049\"},{\"name\":\"BluetoothToggleTest\",\"displayname\":\"Bluetooth\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT025\"},{\"name\":\"GyroscopeSensorTest\",\"displayname\":\"Gyroscope\",\"category\":\"Hardware\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT054\"}],\"manualTests\":[{\"name\":\"LCDTest\",\"displayname\":\"Display\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT082\"},{\"name\":\"WifiHardwaretest\",\"displayname\":\"Wifi Hardware\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT012\"},{\"name\":\"TouchTest\",\"displayname\":\"Touch\",\"category\":\"Display\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT011\"},{\"name\":\"AccelerometerTest\",\"displayname\":\"Accelerometer\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT018\"},{\"name\":\"ProximityTest\",\"displayname\":\"Proximity\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT017\"},{\"name\":\"SpeakerTest\",\"displayname\":\"Speaker\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT019\"},{\"name\":\"EarpieceTest\",\"displayname\":\"Earpiece\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT020\"},{\"name\":\"MicTest\",\"displayname\":\"Primary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT031\"},{\"name\":\"Mic2Test\",\"displayname\":\"Secondary Microphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT128\"},{\"name\":\"EarphoneTest\",\"displayname\":\"Earphone\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT076\"},{\"name\":\"EarphoneJackTest\",\"displayname\":\"Earjack\",\"category\":\"Audio\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT077\"},{\"name\":\"VibrationTest\",\"displayname\":\"Vibration\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT021\"},{\"name\":\"RearCameraPictureTest\",\"displayname\":\"Rear camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT026\"},{\"name\":\"RearCameraVideoTest\",\"displayname\":\"Rear camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT027\"},{\"name\":\"FrontCameraPictureTest\",\"displayname\":\"Front camera picture\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT028\"},{\"name\":\"FrontCameraVideoTest\",\"displayname\":\"Front camera video\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT029\"},{\"name\":\"CameraFlashTest\",\"displayname\":\"Camera flash\",\"category\":\"Camera\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT030\"},{\"name\":\"USBManualConnectionTest\",\"displayname\":\"USB connection\",\"category\":\"Others\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT060\"},{\"name\":\"FingerPrintSensorTest\",\"displayname\":\"Finger print sensor\",\"category\":\"Sensors\",\"severity\":\"LOW\",\"status\":\"NONE\",\"testCode\":\"PT085\"}]},\"tradeIn\":{\"issueName\":\"\",\"displayname\":\"\",\"description\":\"\",\"autoTests\":[],\"manualTests\":[]},\"physicalTests\":[],\"marketingName\":\"Pixel 3a\",\"certified\":false,\"autobrightnessAvl\":true,\"ownershipCheckProceed\":true,\"deviceSupported\":true,\"latestFirmware\":true,\"runAllManualTests\":false,\"latestFirmwareVersion\":\"SP1A.210812.015\",\"callTestNumber\":\"121\",\"pkeys\":\"VOLUME_UP,VOLUME_DOWN,POWER\",\"vkeys\":\"BACK,HOME\",\"iosBatteryHealthPer\":-1,\"sessionTimeoutInMins\":30,\"iosBatteryHealthStatus\":\"SKIPPED\",\"batteryDesignCapacity\":0,\"lastRestartThresholdDays\":7,\"unusedAppsThreshold\":\"5\",\"currentServerTime\":1635343958505,\"shortDateFormat\":\"dd/MM/yyyy\",\"longDateFormat\":\"dd/MM/yyyy HH:mm\",\"sohRange\":[79,90],\"fivePointCheck\":[],\"generateRAN\":false,\"enableRAPFeature\":false,\"storeemail\":\"\",\"countryemail\":\"\",\"sendSummaryToStoreAndCentral\":false,\"batteryConfig\":{\"sohThreshold\":80,\"avgSohThreshold\":60,\"validSohThreshold\":5,\"deepdiveConfig\":{\"percentDrop\":3,\"minBatteryLevel\":30},\"gldProfile\":\"\"},\"serverWARVersion\":\"SSD-3.2.20211014.3\",\"summaryDisplayElements\":[\"SuggestedFixes\",\"DeviceInfo\",\"BatteryTest\",\"TestResults\"],\"hybridTests\":[\"SpeakerTest\",\"EarpieceTest\",\"MicTest\",\"VibrationTest\",\"Mic2Test\"],\"checkIMEIStolenStatus\":true,\"enableEmailSummary\":true,\"enableCSAT\":true,\"enableIMEICapture\":true,\"enableCosmeticCheck\":false,\"enableCosmeticMirrorCheck\":false,\"enableTradeInFlow\":false,\"disableDiagIssuesSelection\":false,\"disableSkipManualTestsOption\":false,\"enableDiagTradeInFlow\":false},\"status\":\"SUCCESS\",\"message\":\"Valid store id\",\"sessionId\":252002}", ServerConfig.class);
//            return rObject;
//        }


        @Override
        protected void onPostExecute(ServerConfig serverConfig) {
            super.onPostExecute(serverConfig);
            try {
                if (serverConfig != null) {
                    if (serverConfig.getStatus().equalsIgnoreCase("SUCCESS")) {
                        //GlobalConfig.getInstance().setSessionId(Long.parseLong(serverConfig.getSessionId()));
                        if (serverConfig.getDiagConfiguration() != null) {


                            GlobalConfig.getInstance().setSessionId(Long.parseLong(serverConfig.getSessionId()));
                            globalConfig.addItemToList("Session ID : "+Long.parseLong(serverConfig.getSessionId()));
                            DLog.d(TAG, "session ID : "+ Long.parseLong(serverConfig.getSessionId()));
                            GlobalConfig.getInstance().setStoreID(storeid);
                            CommandServer.loadConfig(serverConfig.getDiagConfiguration(), false, getApplicationContext());
                            DLog.d(TAG, "entered into Success status");
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            decideAppFlow();
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("OfflineData", new Gson().toJson(serverConfig.getDiagConfiguration()));
                            DLog.d(TAG, "Payload - "+new Gson().toJson(serverConfig.getDiagConfiguration()));
                            editor.apply();
                            DLog.d(TAG, " enter into after all success");

                        }

                    } else {
                        if (serverConfig.getMessage().equalsIgnoreCase("Invalid store id")) {
                            progressDialog.dismiss();
                            //handler.sendEmptyMessage(0);
                            if (Util.gpsLoginRequired()) {
                                displayStoreIdList();
                            } else {
                                Toast.makeText(PinValidationActivity.this, getString(R.string.invalid_storeid), Toast.LENGTH_LONG).show();
                                showDialogue();
                            }
                        }
                    }

                } else {

                    if (PervacioTest.getInstance().isOfflineDiagnostics()) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String offlineData = sharedPreferences.getString("OfflineData", null);

                        if (offlineData != null) {
                            DiagConfiguration diagConfiguration = PervacioTest.getInstance().getObjectFromData(offlineData, new TypeToken<DiagConfiguration>() {
                            }.getType());
                            if (diagConfiguration != null) {
                                DLog.d(TAG, "Loading Offline Data........");
                                CommandServer.loadConfig(diagConfiguration, true, getApplicationContext());
                                decideAppFlow();
                                //  CommandServer.handleLaunchScreen("START_DIAGNOSTICS", PinValidationActivity.this);
                                //   PinValidationActivity.this.finish();
                            }

                        }


                    } else {
                        Toast.makeText(PinValidationActivity.this, R.string.try_again, Toast.LENGTH_LONG).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                DLog.d(TAG, "Pin Validate Exception6 = " + Log.getStackTraceString(e));
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    private boolean hasImei() {
        //For Android 9 and lower, IMEI is automatically obtained
        if ((android.os.Build.VERSION.SDK_INT < 29))
            return true;

        //Check for allowed features based on customer name
        if (ProductFlowUtil.isCountryGermany() || !ProductFlowUtil.enableCaptureIMEI()) {
            String imei = CommonUtil.getMACAddressInIMEIFormat(this);

            Util.saveImeiInPrefs(imei);
            com.pervacio.batterydiaglib.util.LogUtil.debug("hasImei(): MAC :" + imei);
            return true;
        } else if (!Util.retrivedIMEIForAndroid10()) {
            // IMEIReadChildActivity.startActivity(this);
            return false;
        }
        return true;
    }


    private void decideAppFlow() {
        DLog.d(TAG, "entered into decide app flow");

        ThemeUtil.setCustomer(GlobalConfig.getInstance().getCompanyName()); // set theme

        if (Util.showTermsAndConditions() && !isTermsAccepted()) {
            TermsAndConditionsActivity.startActivity(this);   // if terms not accepted start this activity
        } else if (hasImei()) {
            if (Util.isDeviceInfoScreenRequired()) {  // check whether deviceInfo screen is required or not
                CommandServer.handleLaunchScreen("DEVICE_INFO", PinValidationActivity.this);
            } else {
                     /*if(GlobalConfig.getInstance().isCheckIMEIStatus()) {
                                BGInedependentService.startAction(PinValidationActivity.this, BGInedependentService.ACTION_CHECK_IMEI_STATUS);
                            }*/
                if (GlobalConfig.getInstance().isVerification()) {
                    DLog.d(TAG, "entered into auto tests");

                    PervacioTest.getInstance().setSelectedCategory(PDConstants.VERIFY);
                    //PervacioTest.getInstance().initializeApps();


                    Intent intent = new Intent(PinValidationActivity.this, AutoTestActivity.class);
                    startActivity(intent);
                    finish();
                } else if (ProductFlowUtil.promptTradeInSelection()) {
//                    Intent intent = new Intent(PinValidationActivity.this, TradeInFlowSelectionUnusedActivity.class);
//                    startActivity(intent);
//                    PinValidationActivity.this.finish();
                } else {


                    PervacioTest.getInstance().setSelectedCategory(PDConstants.RUN_ALL_DIAGNOSTICS);
                    Intent intent = new Intent(PinValidationActivity.this, AutoTestActivity.class);
                    startActivity(intent);
                    finish();
//                    CommandServer.handleLaunchScreen("START_DIAGNOSTICS", PinValidationActivity.this);
//                    finish();
                }

            }
        }
    }


    private class GpsListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                locManager.removeUpdates(gpsListner);
                loggingHandler.removeCallbacks(logRunnable);
                DLog.d(TAG, "onLocationChanged " + longitude + " " + latitude);
                Geocoder gcd = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    DLog.e(TAG, "Exception: " + e.getMessage());
                    displayStoreIdList();
                }

                if (addresses != null && addresses.size() > 0) {
                    String countryCode = addresses.get(0).getCountryName();
                    DLog.d(TAG, "Actual CountryCode: " + countryCode);
                    if (countryCode != null) {
                        countryCode = countryCode.replace("México", "Mexico");
                    }
                    GlobalConfig.getInstance().setCompanyName(GlobalConfig.getInstance().getCompanyName() + countryCode);
                    storeId = globalConfig.getStoreID();
                    DLog.d(TAG, "Replaced CountryCode: " + countryCode);
                    PinValidationTask pinValidationTask = new PinValidationTask(storeId + "_" + countryCode);
                    pinValidationTask.execute();
                } else
                    DLog.d(TAG, "addresses is null");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private Handler loggingHandler = new Handler();
    private Runnable logRunnable = new Runnable() {
        @Override
        public void run() {
            loggingHandler.removeCallbacks(logRunnable);
            locManager.removeUpdates(gpsListner);
            displayStoreIdList();
        }
    };

    AlertDialog multipleStoreListDialog;

    private void displayStoreIdList() {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.multiple_storeid_list, null);

            multipleStoreListDialog = CommonUtil.DialogUtil.showAlertWithList(this, view, COUNTRY_LIST, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (multipleStoreListDialog != null) {
                        multipleStoreListDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }

                    GlobalConfig.getInstance().setCompanyName(GlobalConfig.getInstance().getCompanyName() + COUNTRY_LIST[which]);
                    storeId = globalConfig.getStoreID() + "_" + COUNTRY_LIST[which];
                    DLog.d(TAG, "Selected store id : " + storeId);
                }
            }, getString(R.string.submit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DLog.d(TAG, "ok button is clicked");
                    if (isOnline()) {
                        PinValidationTask pinValidationTask = new PinValidationTask(storeId);
                        pinValidationTask.execute();
                        if (multipleStoreListDialog != null)
                            multipleStoreListDialog.dismiss();
                    } else {
                        Toast.makeText(PinValidationActivity.this, getString(R.string.network_msz), Toast.LENGTH_LONG).show();
                    }
                }
            });
            multipleStoreListDialog.show();
            multipleStoreListDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        } catch (Exception e) {
            DLog.e(TAG, "Exception in showing custom dialog: " + e.getMessage());
        }
    }

    public void gpsLocationGranted(boolean result) {

        if (result) {
            if (this.locManager != null && locManager.isProviderEnabled("gps")) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            2);
                    return;
                }
                gpsListner = new GpsListener();
                this.locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 1000L, 10, gpsListner);
                this.locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2 * 1000L, 10, gpsListner);
                loggingHandler.postDelayed(logRunnable, 15000);
            }
        } else {
            displayStoreIdList();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DLog.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
        if (!isFinishing() && requestCode == 111 && (resultCode == 0 || resultCode == -1)) {
            if (NetworkUtil.isOnline()) {
                gpsLocationGranted(DeviceInfo.getInstance(this).isGPSEnabled());
            }
            super.onActivityResult(requestCode, resultCode, data);
        }


        switch (requestCode) {
            case TermsAndConditionsActivity.RC_TERMS_CONDITIONS:
          //  case IMEIReadActivity.RC_IMEI_READ:
                if (resultCode == Activity.RESULT_OK) {
                    decideAppFlow();
                    return;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    AppUtils.triggerUninstall(this, true, BuildConfig.APPLICATION_ID);
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!isFinishing() && requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                gpsLocationGranted(DeviceInfo.getInstance(this).isGPSEnabled());
            } else {
                displayStoreIdList();
            }
        }
    }
}

