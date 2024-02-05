package com.oruphones.nativediagnostic.api;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.oruphones.nativediagnostic.BaseActivity.context;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.oruphones.nativediagnostic.AdvancedFrequencyy;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.oneDiagLib.TestAutoSensor;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DataPassListener;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.pervacio.batterydiaglib.util.BatteryUtil;


import org.pervacio.onediaglib.advancedtest.audio.AdvancedAudioTest;
import org.pervacio.onediaglib.advancedtest.cameraautomation.AutoTestCameraPicture;
import org.pervacio.onediaglib.atomicfunctions.AFBluetooth;
import org.pervacio.onediaglib.atomicfunctions.AFGPS;
import org.pervacio.onediaglib.atomicfunctions.AFNFC;
import org.pervacio.onediaglib.atomicfunctions.AFSettings;
import org.pervacio.onediaglib.atomicfunctions.AFWiFi;
import org.pervacio.onediaglib.diagtests.DiagTimer;
import org.pervacio.onediaglib.diagtests.ISensorEventListener;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.SdCardCapacityTest;
import org.pervacio.onediaglib.diagtests.SdCardInsertionTest;
import org.pervacio.onediaglib.diagtests.TestBluetooth;
import org.pervacio.onediaglib.diagtests.TestBrightness;
import org.pervacio.onediaglib.diagtests.TestCameraPicture;
import org.pervacio.onediaglib.diagtests.TestGPS;
import org.pervacio.onediaglib.diagtests.TestGoogleAccounts;
import org.pervacio.onediaglib.diagtests.TestIMEI;
import org.pervacio.onediaglib.diagtests.TestLTE;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestLiveWallPaper;
import org.pervacio.onediaglib.diagtests.TestQuickBattery;
import org.pervacio.onediaglib.diagtests.TestRamMemory;
import org.pervacio.onediaglib.diagtests.TestSdCardResult;
import org.pervacio.onediaglib.diagtests.TestSecurityLock;
import org.pervacio.onediaglib.diagtests.TestSim;
import org.pervacio.onediaglib.diagtests.TestWiFi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pervacio on 10-08-2017.
 */
public class AutoTest implements TestName, TestResult, TestListener, ISensorEventListener, DataPassListener {
    private static final int BLUETOOTH_REQUEST_CODE = 103;
    private static final int WIFI_PERMISSION_REQUEST_CODE = 104;
    private DataPassListener dataPassListener;

    private String TAG = AutoTest.class.getName();
    private Resolution resolution = null;
    private final String HEADSET_ACTION = "android.intent.action.HEADSET_PLUG";
    private boolean isReceiverRegistered = false;
    private AFBluetooth aFBluetooth = null;
    private DeviceInfo deviceInfo = null;
    private AFSettings afSettings = null;
    private TestBrightness testBrightness;
    private TestLiveWallPaper testLiveWallPaper;
    private TestQuickBattery testQuickBattery;
    private TestAutoSensor testAutoSensor;
    private boolean sensorTestDone;
    private boolean midTestCheck = false;
    private boolean lteTestDone;
    private AFGPS afGPS = null;
    private BluetoothAdapter bluetoothAdapter;
    private AFWiFi afWifi;
    private AFNFC afNFC;
    private TestBluetooth testBlueooth;
    private TestWiFi testWIFI;
    private TestGPS testGPS;
    public static boolean BTToggleCompleted = false;
    public static boolean WifiToggleCompleted = false;
    public static boolean GPSTestCompleted = false;
    public static boolean audioTestsCompleted = false;
    public static boolean frontCameraPictureTestCompleted = false;
    public static boolean rearCameraPictureTestCompleted = false;
    public static boolean vibrationTestCompleted = false;
    private org.pervacio.onediaglib.diagtests.TestResult testResult;
    private GlobalConfig globalConfig = null;
    private DiagTimer diagTimer = null;
    private Activity activity;
    private PervacioTest pervacioTest;
    private static AutoTest autoTest;

    public boolean force_break = false;
    private String currentTest;
    public static Map<String, String> testAdditionalInfoMap = new HashMap<>();
    private boolean micTestPerformed;
    //	private String mic2TestResult = TestResult.NONE;
//    private String mic1TestResult = TestResult.NONE;
    private FrameLayout idFrameLayout;
    /*
        public AutoTest() {
        }
    */
    private boolean earJackState = false;
    private boolean blueToothState = false;
    Handler handler2 = new Handler();
    boolean sleepStarted = false;

    BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            receiver for check headset jack
            try {
                if (intent.getAction() != null && intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    int headsetType = intent.getIntExtra("name", -1);

                    if (state == 1) {
                        earJackState = true;
//                    showHeadphoneConnectedDiaDLog(true);
                        handleHeadsetConnection(context, headsetType);
                    } else {
                        // Headset is disconnected
                        earJackState = false;
//                    showHeadphoneConnectedDiaDLog(false);
                        handleHeadsetDisconnection(context);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                earJackState = true;
            }

//            if (intent.getAction() != null &&  intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
//                int state = intent.getIntExtra("state", -1);
//                switch (state) {
//                    case 0:
//                        earJackState = false;
//                        showHeadphoneConnectedDiaDLog(false);
//                        if (sleepStarted) {
////                            Thread.currentThread().interrupt();
//                            sleepStarted = false;
//                        }
//                        break;
//                    case 1:
//                        earJackState = true;
//                        showHeadphoneConnectedDiaDLog(true);
//                        break;
//                    default:
//                        earJackState = false;
//                        showHeadphoneConnectedDiaDLog(false);
//                }
//            }

//            to detect usb based earphones
//            if (intent.getAction() != null && intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
//                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//                AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
//                for (AudioDeviceInfo device : audioDevices) {
//                    if (device.getType() == AudioDeviceInfo.TYPE_USB_DEVICE) {
//                        // USB audio device detected (possibly earphones)
////                        Toast.makeText(context, "USB audio device detected", Toast.LENGTH_SHORT).show();
//                        earJackState = true;
//                        break;
//                    }
//                    else {
//                        earJackState = false;
//                        break;
//                    }
//                }
//            }
        }
    };

    private void handleHeadsetConnection(Context context, int headsetType) {
        try {
            if (headsetType == 0) {
                earJackState = true;
            } else {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {

                    boolean usbAudioConnected = false;
                    for (AudioDeviceInfo device : audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS | AudioManager.GET_DEVICES_OUTPUTS)) {
                        if (device.getType() == AudioDeviceInfo.TYPE_USB_DEVICE) {
                            usbAudioConnected = true;
                            break;
                        }
                    }

                    if (usbAudioConnected) {
                        earJackState = true;
                    } else {
                        earJackState = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            earJackState = true;
        }
    }

    private void handleHeadsetDisconnection(Context context) {
        earJackState = false;
    }
    public void initializeReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter(HEADSET_ACTION);
            context.registerReceiver(mHeadsetReceiver, filter);
            isReceiverRegistered = true;
        }
    }

    private void showHeadphoneConnectedDiaDLog(boolean show) {
//        final Toast[] toast = new Toast[1];
        if (show) {
//            new CountDownTimer(5000, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    toast[0] = Toast.makeText(activity, "Please disconnect the headphone and try again" + "\n\n" + "Time remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT);
////                    toast[0].setGravity(Gravity.CENTER, 0, 0);
//                    toast[0].show();
//
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            toast[0].cancel();
//                        }
//                    }, 1000);
//                }
//
//                @Override
//                public void onFinish() {
////                    Toast.makeText(activity, "Please disconnect the headphone and try again", Toast.LENGTH_SHORT).show();
//                }
//            }.start();
        }
    }
    boolean vibration_result;
    private AutoTest(Activity activity) {
        dataPassListener = (DataPassListener) this;

        this.activity = activity;
        initTestObjects();
    }


    public static void showSnackbar(Activity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
//        snackbar.setActionTextColor(activity.getColor(R.color.white));
//        snackbar.setBackgroundTint(activity.getColor(R.color.red));
//        snackbar.show();
        if (snackbar != null) {
            snackbar.setActionTextColor(activity.getColor(R.color.white));
            snackbar.setBackgroundTint(activity.getColor(R.color.red));
            snackbar.show();
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }
    public static AutoTest getInstance(Activity activity) {
        if (autoTest == null) autoTest = new AutoTest(activity);
        autoTest.initTestObjects();
        return autoTest;
    }

    private void initTestObjects() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pervacioTest = PervacioTest.getInstance();
        deviceInfo = DeviceInfo.getInstance(activity.getApplicationContext());
        aFBluetooth = new AFBluetooth();
        afSettings = new AFSettings();
        afGPS = new AFGPS();
        afWifi = new AFWiFi();
        afNFC = new AFNFC();
        testWIFI = new TestWiFi();
        testGPS = new TestGPS();
        testBlueooth = new TestBluetooth();
        testBrightness = new TestBrightness();
        testLiveWallPaper = new TestLiveWallPaper();
        testQuickBattery = new TestQuickBattery();
        sensorTestDone = false;
        lteTestDone = false;
        globalConfig = pervacioTest.getGlobalConfig();
        resolution = Resolution.getInstance();
        pervacioTest.getAutoTestResult().clear();
        pervacioTest.getManualTestResult().clear();
        pervacioTest.getTestResult().clear();
        resolution.getAvailableResolutionsList().clear();
        diagTimer = new DiagTimer(new ITimerListener() {
            @Override
            public void timeout() {
            }
        });
        if (!BaseActivity.isAssistedApp)
            diagTimer.setEnableTimer();
    }


    public String performTest(String testName, final ViewGroup frameLayout) {

        String resultToReturn = NOTEQUIPPED;
        currentTest = testName;
        rearCameraPictureTestCompleted = false;
        frontCameraPictureTestCompleted = false;
        switch (testName) {

            case ACCELEROMETERTEST:
                resultToReturn = performSensorTest(testName, "accelerometer", globalConfig.getMinAccelerometerValue(), globalConfig.getMaxAccelerometerValue());

//                accelerometerTest = TestAccelerometer.getInstance();
//                accelerometerTest.registerSensorEventListener();
//                if (testResult!=null){
//                    if (testResult.getResultCode()==0) {
//                        resultToReturn = FAIL;
//                    }else if (testResult.getResultCode()==1){
//                        resultToReturn = PASS;
//                    }else{
//                        resultToReturn = NOTEQUIPPED;
//                    }
//                }
                break;
            case ROOTED:
                if (deviceInfo.get_rooted() != null && deviceInfo.get_rooted().equalsIgnoreCase("Yes")) {
                    resultToReturn = FAIL;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case SCREEN_TIMEOUT:
                int screenvalue = afSettings.getScreenTimeOutValue();
                if (screenvalue > 0 && screenvalue <= 60000 || screenvalue == -2) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = CANBEIMPROVED;
                }
                break;
            case BRIGHTNESS:
                if (!globalConfig.getAutobrightnessAvailable()) {
                    testBrightness.setScreenBrightnessAutoMode(0);
                }
                int brightnessMode = testBrightness.getScreenBrightnessAutoMode();
                Float brightnessValue = testBrightness.getScreenBrightnessValue();
                if ((globalConfig.getAutobrightnessAvailable() && brightnessMode == 1) || brightnessValue <= 40.0F) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = CANBEIMPROVED;
                }
                break;
            case LIVEWALLPAPER:
                if (testLiveWallPaper.isLiveWallpaperEnabled()) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case BLUETOOTH_OFF:
                if (pervacioTest.isFeatureAvailable(testName)) {
                    if (aFBluetooth.getState()) {
                        resultToReturn = CANBEIMPROVED;
                    } else {
                        resultToReturn = PASS;
                    }
                } else {
                    resultToReturn = NOTEQUIPPED;
                }
                break;
            case BLUETOOTH_ON:
                if (pervacioTest.isFeatureAvailable(testName)) {
                    if (aFBluetooth.getState()) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = CANBEIMPROVED;
                    }
                } else {
                    resultToReturn = NOTEQUIPPED;
                }
                break;
            case WIFICONNECTIVITYTEST:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    WifiToggleCompleted = false;
                    new Thread() {
                        public void run() {
                            testResult = testWIFI.toggleWiFiTest(500);
                            WifiToggleCompleted = true;
                        }
                    }.start();

                    while (!WifiToggleCompleted) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = FAIL;
                    }
                } else {
                    resultToReturn = hasAvailableWifiNetworks()?PASS:FAIL;
                }
                break;
            case BLUETOOTH_TOGGLE:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                    BTToggleCompleted = false;
                    new Thread() {
                        public void run() {
                            testResult = testBlueooth.toggleBluetoothTest(500);
                            BTToggleCompleted = true;
                        }
                    }.start();
                    while (!BTToggleCompleted) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (testResult != null && testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = FAIL;
                    }
                }
                else {
                    resultToReturn = hasPairedDevices()?PASS:FAIL;
                }
                break;

            case BLUETOOTHCOMPREHENSIVETEST:
                BTToggleCompleted = false;
                new Thread() {
                    public void run() {
                        try {
                            testBlueooth.scanTest(AutoTest.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                while (!BTToggleCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                boolean performBTToggleTest = false;
                if (testResult.getResultCode() != org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    if (testResult.getResultDescription() != null && (testResult.getResultDescription().equalsIgnoreCase("Bluetooth turn on failed") || testResult.getResultDescription().equalsIgnoreCase("Bluetooth feature not avilable"))) {
                        BTToggleCompleted = true;
                    } else {
                        BTToggleCompleted = false;
                        performBTToggleTest = true;
                    }
                }
                if (performBTToggleTest) {
                    new Thread() {
                        public void run() {
                            testResult = testBlueooth.toggleBluetoothTest(500);
                            BTToggleCompleted = true;
                        }
                    }.start();
                }
                while (!BTToggleCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                PDTestResult bluetooth_test = new PDTestResult();
                bluetooth_test.setName(PDConstants.BLUETOOTHCOMPREHENSIVETEST);
                if (testResult != null && testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;
            case GPS_OFF:
                if (!pervacioTest.isFeatureAvailable(testName)) {
                    resultToReturn = NOTEQUIPPED;
                }
                if (afGPS.getState()) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case GPS_ON:
                if (!pervacioTest.isFeatureAvailable(testName)) {
                    resultToReturn = NOTEQUIPPED;
                }
                if (!afGPS.getState()) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case GPSCOMPREHENSIVETEST:
                GPSTestCompleted = false;
                boolean isWifiStateChanged = false;
                DLog.d(TAG, "GPS Mode = " + deviceInfo.getLocationMode());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        resultToReturn = ACCESSDENIED;
                        DLog.d(TAG, "GPS Permission Denide");
                        break;
                    }
                }
                if (!afGPS.getState()) {
                    resultToReturn = SKIPPED;
                    DLog.d(TAG, "GPS is off hence skipped...........");
                } else {
                    if (!isOnline() && !testWIFI.getState()) {
                        testWIFI.setState(true);
                        DLog.d(TAG, "GPS Enabling WIFI...........");
                        isWifiStateChanged = true;
                    }
/*                    (new Thread() {
                        @Override
                        public void run() {*/
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DLog.d(TAG, "GPS Location test Started...........");
                            testGPS.gpsWithStatusTest(AutoTest.this, true);
                        }
                    });

/*                        }
                    }).start();*/
                    while (true) {
                        //DLog statement added as break point. Do not remove it.
                        DLog.d(TAG, "in while gps result obtained" + GPSTestCompleted);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (GPSTestCompleted) {
                            break;
                        }
                    }
                    DLog.d(TAG, "GPS ResultCode: " + testResult.getResultCode());
                    if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FEATURE_NOT_EQUIPPED) {
                        resultToReturn = NOTEQUIPPED;
                    } else if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = FAIL;
                    }
                }
                break;
            case WIFI_OFF:
                if (!pervacioTest.isFeatureAvailable(testName)) {
                    resultToReturn = NOTEQUIPPED;
                }
                if (afWifi.getState()) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case WIFI_ON:
                if (!pervacioTest.isFeatureAvailable(testName)) {
                    resultToReturn = NOTEQUIPPED;
                }
                if (afWifi.getState()) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case TestName.FIRMWARE:
                if (globalConfig.isLatestFirmware()) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = CANBEIMPROVED;
                }
               /* if (PDConstants.PDCANBEIMPROVED.equalsIgnoreCase(globalConfig.getLatestFirmware())) {
                resultToReturn = CANBEIMPROVED;
                } else if (PDConstants.PDPASS.equalsIgnoreCase(globalConfig.getFirmWare())){
                    resultToReturn = PASS;
                }*/
                break;
            case LastRestart:
                long lastRestartTime = SystemClock.elapsedRealtime();
                if (TimeUnit.MILLISECONDS.toDays(lastRestartTime) > GlobalConfig.getInstance().getLastRestartThresholdVal())
                    resultToReturn = CANBEIMPROVED;
                else
                    resultToReturn = PASS;
                break;
            case WIFI_TOGGLE:
                String connectedSSIDName;
                if (testWIFI.isConnected()) {
                    connectedSSIDName = testWIFI.getConnctedSSIDName();
                    if (!TextUtils.isEmpty(connectedSSIDName)) {
                        if (connectedSSIDName.startsWith("\"") && connectedSSIDName.endsWith("\"")) {
                            connectedSSIDName = connectedSSIDName.substring(1, connectedSSIDName.length() - 1);
                        }
                        resultToReturn = PASS;
                    } else {
                        if (wifiToogle()) {
                            resultToReturn = PASS;
                        } else {
                            resultToReturn = FAIL;
                        }
                    }
                } else {


                    if (wifiToogle())
                    {
                        resultToReturn = PASS;
                    }
                    else
                    {
                        resultToReturn = FAIL;
                    }


                }
                break;
            case WIFICOMPREHENSIVETEST:
                WifiToggleCompleted = false;
                if (testWIFI.isWiFiHotSpotOn()) {
                    WifiToggleCompleted = true;
                } else {
                    new Thread() {
                        public void run() {
                            testWIFI.scanTest(AutoTest.this);
                        }
                    }.start();
                }
                while (!WifiToggleCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                boolean performWifiToggleTest = false;
                if (testResult.getResultCode() != org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    if (testResult.getResultDescription() != null && (testResult.getResultDescription().equalsIgnoreCase("WIFI turn on failed") || testResult.getResultDescription().equalsIgnoreCase("WIFI feature not avilable"))) {
                        WifiToggleCompleted = true;
                    } else {
                        WifiToggleCompleted = false;
                        performWifiToggleTest = true;
                    }
                }
                if (performWifiToggleTest) {
                    new Thread() {
                        public void run() {
                            testResult = testWIFI.toggleWiFiTest(500);
                            WifiToggleCompleted = true;
                        }
                    }.start();
                }
                while (!WifiToggleCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;
            case NFC_OFF:
                if (pervacioTest.isFeatureAvailable(testName)) {
                    if (!afNFC.getState()) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = CANBEIMPROVED;
                    }
                } else {
                    resultToReturn = NOTEQUIPPED;
                }
                break;
            case NFC_ON:
                if (pervacioTest.isFeatureAvailable(testName)) {
                    if (afNFC.getState()) {
                        resultToReturn = PASS;
                    } else {
                        resultToReturn = CANBEIMPROVED;
                    }
                } else {
                    resultToReturn = NOTEQUIPPED;
                }
                break;
            case BATTERYCONDITION:
                org.pervacio.onediaglib.diagtests.TestResult qbtestResult = testQuickBattery.quickBatteryTest();
                if (qbtestResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;
            case SIMCARD:
                TestSim testSim = new TestSim();
                testResult = testSim.checkCurrentSimState();
                DLog.d(TAG, "SIMCARDRESULT:" + testResult.getResultCode());
                if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;
            case FREEMEMORY:
                TestRamMemory testRamMemory = new TestRamMemory();
                long totalRamMemory = testRamMemory.getTotalRamMemory();
                long availableRamMemory = testRamMemory.getAvailableRamMemory();

                double memoryThreashHold = totalRamMemory * 0.20;
                if (availableRamMemory > memoryThreashHold) {
                    resultToReturn = PASS;
                } else {
                    boolean memoryAppsFound = false;
                    while (!Resolution.getInstance().isMemoryResolutionDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (resolution.getForegroundAppList() != null && resolution.getForegroundAppList().size() > 0) {
                        memoryAppsFound = true;
                    } else if (resolution.getBackgroundAppList() != null && resolution.getBackgroundAppList().size() > 0) {
                        memoryAppsFound = true;
                    } else if (resolution.getAutostartAppList() != null && resolution.getAutostartAppList().size() > 0) {
                        memoryAppsFound = true;
                    }
                    if (memoryAppsFound)
                        resultToReturn = CANBEIMPROVED;
                    else
                        resultToReturn = PASS;
                }
                break;
            case INTERNALSTORAGE:
                if (deviceInfo.getInternalStorageStatus()) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = CANBEIMPROVED;
                }
                break;
            case SDCARD:
                //BaseActivity.hasSDCardSlot = sdCardSlotDetection.isSdCardSlot(_deviceInfo.get_model());
                SdCardInsertionTest sdCardInsertionTest = new SdCardInsertionTest();
                TestSdCardResult sdCardResult = sdCardInsertionTest.performSdCardInsertionTest();
                if (sdCardResult.getResultCode() == TestSdCardResult.RESULT_NO_SDCARD) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case SDCARDCAPACITY:
                SdCardInsertionTest sdCardInsertionTest1 = new SdCardInsertionTest();
                TestSdCardResult sdCardResult1 = sdCardInsertionTest1.performSdCardInsertionTest();
                if (sdCardResult1.getResultCode() == TestSdCardResult.RESULT_NO_SDCARD) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    SdCardCapacityTest sdCardCapacityTest = new SdCardCapacityTest();
                    TestSdCardResult testSdCardResult = sdCardCapacityTest.performSdCardCapacityTest();

                    if (testSdCardResult.getSdcardAvailableSpace() < 0.20 * (testSdCardResult.getSdcardTotalSpace())) {
                        resultToReturn = CANBEIMPROVED;

                    } else {
                        resultToReturn = PASS;
                    }
                }
                break;
            case GYROSCOPESENSORTEST:
                resultToReturn = performSensorTest(testName, "gyroscope", globalConfig.getGyroscopeSensorMinValue(), globalConfig.getGyroscopeSensorMaxValue());
                break;
            case MAGNETICSENSORTEST:
                resultToReturn = performSensorTest(testName, "magneticsensor", globalConfig.getMagneticSensorMinValue(), globalConfig.getMagneticSensorMaxValue());
                break;
            case BAROMETERTEST:
                resultToReturn = performSensorTest(testName, "barometer", globalConfig.getbarometerMinValue(), globalConfig.getbarometerMaxValue());
                break;
            case RELATIVEHUMIDITYTEST:
                resultToReturn = performSensorTest(testName, "humidity", globalConfig.getMinHumidityValue(), globalConfig.getMaxHumidityValue());
                break;
            case GAMEROTATIONVECTORSENSORTEST:
                resultToReturn = performSensorTest(testName, "game_rotation_vector", globalConfig.getMinHumidityValue(), globalConfig.getMaxHumidityValue());
                break;
            case GEOMAGNETICROTATIONVECTORSENSORTEST:
                resultToReturn = performSensorTest(testName, "geomagnetic_rotation_vector", globalConfig.getMinHumidityValue(), globalConfig.getMaxHumidityValue());
                break;
            case ROTATIONVETORSENSORTEST:
                resultToReturn = performSensorTest(testName, "rotation_vector", globalConfig.getMinHumidityValue(), globalConfig.getMaxHumidityValue());
                break;
            case LINEARACCELERATIONSENSORTEST:
                resultToReturn = performSensorTest(testName, "linear_acceleration", globalConfig.getMinHumidityValue(), globalConfig.getMaxHumidityValue());
                break;
            case IMEITest:
                if (CommonUtil.PermissionUtil.hasRuntimePermission(Manifest.permission.READ_PHONE_STATE) || CommonUtil.PermissionUtil.hasRuntimePermission(Manifest.permission.READ_PHONE_NUMBERS)) {
                    DeviceInfo deviceInfo = DeviceInfo.getInstance(activity);
                    org.pervacio.onediaglib.diagtests.TestResult testResults = new TestIMEI().getIMEIStatus(deviceInfo.get_imei());
                    DLog.d(TAG, testName + "-" + testResults.getResultCode());
                    if (testResults.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS)
                        resultToReturn = PASS;
                    else if (testResults.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED)
                        resultToReturn = ACCESSDENIED;
                    else
                        resultToReturn = FAIL;


                } else {
                    resultToReturn = ACCESSDENIED;
                }
                break;
            case PDConstants.LTETEST:
                resultToReturn = performLTETest();
                break;
            case MALWAREAPPS:
                while (!resolution.isAppResolutionDone()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (resolution.isMalwareFound() && resolution.getMalwareAppsList().size() != 0) {
                    resultToReturn = CANBEIMPROVED;
                } else if (globalConfig.isAppsAccessDenied()) {
                    resultToReturn = ACCESSDENIED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case RISKYAPPS:
                while (!resolution.isAppResolutionDone()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (resolution.isRiskyFound() && resolution.getRiskyAppsList().size() != 0) {
                    resultToReturn = CANBEIMPROVED;
                } else if (globalConfig.isAppsAccessDenied()) {
                    resultToReturn = ACCESSDENIED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case ADWAREAPPS:
                while (!resolution.isAppResolutionDone()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (resolution.isAdwareFound() && resolution.getAddwareAppsList().size() != 0) {
                    resultToReturn = CANBEIMPROVED;
                } else if (globalConfig.isAppsAccessDenied()) {
                    resultToReturn = ACCESSDENIED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case OUTDATEDAPPS:
                while (!resolution.isAppResolutionDone()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (resolution.isOutdatedFound() && resolution.getOutdatedAppsList().size() != 0) {
                    resultToReturn = CANBEIMPROVED;
                } else if (globalConfig.isAppsAccessDenied()) {
                    resultToReturn = ACCESSDENIED;
                } else {
                    resultToReturn = PASS;
                }
                break;
            case UNUSEDAPPS:
                /*while (!resolution.isAppResolutionDone()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!BaseActivity.grantUsageStates(activity)) {
                    resultToReturn = ACCESSDENIED;
                } else if (resolution.isUnusedFound() && resolution.getUnusedAppsList().size()!=0) {
                    resultToReturn = CANBEIMPROVED;
                } else {
                    resultToReturn = PASS;
                }*/
                resultToReturn = PASS;
                break;
            case QUICKBATTERYTEST:
                DLog.d(TAG,"QUICK BATTERY TEST RUNNING");
                QuickBatteryTestInfo quickBatteryData = PervacioTest.getInstance().getQuickBatteryTestInfo();
                if (quickBatteryData.getBatteryHealth().equalsIgnoreCase(OruApplication.getAppContext().getString(R.string.unsupported))) {
                    resultToReturn = ACCESSDENIED;
                } else if (quickBatteryData.getBatteryHealth().equalsIgnoreCase(OruApplication.getAppContext().getString(R.string.bad))) {
                    resultToReturn = FAIL;
                } else if (quickBatteryData.getBatteryHealth().equalsIgnoreCase(OruApplication.getAppContext().getString(R.string.good))) {
                    resultToReturn = PASS;
                } else if (quickBatteryData.getBatteryHealth().equalsIgnoreCase(OruApplication.getAppContext().getString(R.string.vgood))) {
                    resultToReturn = PASS;
                }

                DLog.d(TAG,"QUICK BATTERY TEST RESULT  ".concat(resultToReturn));
                if (ACCESSDENIED.equalsIgnoreCase(resultToReturn)) {
                    String batteryHealth = BatteryUtil.getBatteryHealthByAndroidAPI(OruApplication.getAppContext());
                    DLog.d(TAG,"QUICK BATTERY TEST , HEALTH FROM ANDROID API ".concat(batteryHealth));
                    if ("BATTERY HEALTH GOOD".equalsIgnoreCase(batteryHealth)) {
                        resultToReturn = PASS;
                    } else if ("BATTERY HEALTH UNKNOWN".equalsIgnoreCase(batteryHealth)) {
                        resultToReturn = SKIPPED;
                    } else {
                        resultToReturn = FAIL;
                    }
                    DLog.d(TAG,"QUICK BATTERY TEST , FINAL RESULT ".concat(resultToReturn));
                }
                break;

            case GOOGLE_LOCK_TEST:
                TestGoogleAccounts testGoogleAccounts = new TestGoogleAccounts();
                org.pervacio.onediaglib.diagtests.TestResult result = testGoogleAccounts.checkGoogleAccountStatus();
                if (result.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = PASS;
                } else if (result.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED) {
                    resultToReturn = ACCESSDENIED;
                } else if (result.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL) {
                    resultToReturn = FAIL;
                }
                break;

            case DEVICE_SECURITY_LOCK_TEST:
                TestSecurityLock mTestSecurityLock = new TestSecurityLock();
                org.pervacio.onediaglib.diagtests.TestResult keyguardSecurityLock = mTestSecurityLock.getKeyguardSecurityLock();
                if (keyguardSecurityLock.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                    resultToReturn = FAIL;
                } else if (keyguardSecurityLock.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL) {
                    resultToReturn = PASS;
                }
                break;
            case VIBRATIONTEST:

                vibration_result=true;

                if (force_break) {
                    vibration_result=false;
                    break;
                }

                for(int i=0;i<3;i++) {
                    DLog.d(TAG, "pause for 1 sec before recording start");

                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    //Recording before vibration
                    try {


                        if (force_break) {
                            vibration_result=false;
                            break;
                        }

                        // Pause the execution for 3 seconds (3000 milliseconds)
                        Thread.sleep(1000);
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }

                    }
                    catch (InterruptedException e) {
                        // Handle interrupted exception if necessary
                    }

                    if (force_break) {
                        vibration_result=false;
                        break;
                    }

                    DLog.d(TAG , "performTest: " + "before start");
                    startRecording("start");
                    DLog.d(TAG, "performTest: " + "after start");
                    if (force_break) {
                        vibration_result=false;
                        break;
                    }

                    DLog.d(TAG, "pause for 2 sec before recording vibration");

                    try {
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                        // Pause the execution for 3 seconds (3000 milliseconds)
                        Thread.sleep(2300);

                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        // Handle interrupted exception if necessary
                    }

                    DLog.d(TAG, "performTest: " + "before vibration");

                    // Vibration Starting
                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrateForSeconds(3);

                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    DLog.d(TAG, "performTest: " + "after vibration");


                    DLog.d(TAG, "pause for 2 sec before recording end");

                    try {
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                        // Pause the execution for 3 seconds (3000 milliseconds)
                        Thread.sleep(3000);
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        // Handle interrupted exception if necessary
                    }
                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    DLog.d(TAG, "performTest: " + "before end");

                    //Recording before

                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    startRecording("end");
                    if (force_break) {
                        vibration_result=false;
                        break;
                    }
                    DLog.d(TAG, "performTest: " + "after end");

                    try {
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                        // Pause the execution for 3 seconds (3000 milliseconds)
                        Thread.sleep(1000);
                        if (force_break) {
                            vibration_result=false;
                            break;
                        }
                    } catch (InterruptedException e) {
                        // Handle interrupted exception if necessary
                    }

                    DLog.d(TAG, "performTest: " + "after end delay");

                    DLog.d(TAG, "testing interface : total ar = "+ total.toString() +"\ntotalg = "+ totalg.toString());

                }



                for(int i=0;i<total.size();i++)
                {
                    DLog.d(TAG, "iterating total : total ar = "+ total.get(i).toString());

                    if(total.get(i)<2)
                    {
                        DLog.d(TAG, "iterating total : total ar lesssss triggered  = "+ total.get(i).toString());

                        vibration_result=false;
                        break;
                    }

                }

                DLog.d(TAG, "iterating total : total ar After loop  = ");


                vibrationTestCompleted = true;
                if(vibration_result)
                {
                    resultToReturn = PASS;
                }

                else {
                    resultToReturn = FAIL;
                }


                DLog.d(TAG, "stopRecording()(): ");

                break;
            case SPEAKERTEST:
                initializeReceiver();
            case EARPIECETEST:
            case MICROPHONETEST:
            case MICROPHONE2TEST:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                    if (isBluetoothConnected()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Turn off the Bluetooth or Disconnect the Connected Device", Toast.LENGTH_SHORT).show();
                            }
                        });

                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (isBluetoothConnected()) {
                            resultToReturn = FAIL;
                        } else {
                            resultToReturn = performTest(testName, frameLayout);
                        }
                    }
                    else if(earJackState){
                        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        activity.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(earJackState) {
                                        Toast.makeText(activity, "Please remove the earphones first", Toast.LENGTH_SHORT).show();
                                    }
//                                    final Toast toast = Toast.makeText(activity, "\nPlease remove the earphone first\n", Toast.LENGTH_LONG);
//                                    View view = toast.getView();
//                                    view.setBackgroundColor(Color.parseColor("#d04040"));
//                                    view.setPadding(28, 10, 28, 10);
//                                    view.animate().alpha(1.0f).setDuration(1500);
////                                set rounded corners to view
//                                    view.setClipToOutline(true);
//                                    view.setOutlineProvider(new ViewOutlineProvider() {
//                                        @Override
//                                        public void getOutline(View view, Outline outline) {
//                                            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
//                                        }
//                                    });
//                                    toast.show();
                                } catch (Exception e) {
                                }
                            }
                        });
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (earJackState) {
                            resultToReturn = FAIL;
                        } else {
                            resultToReturn = performTest(testName, frameLayout);
                        }
                    }
                    else {
                        audioTestsCompleted = false;
                        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        activity.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                        if (testName != TestName.SPEAKERTEST || (testName == TestName.SPEAKERTEST && am.getRingerMode() != 0)) {
                            if (!earJackState) {
                                String audioTestResult = getAudioTestResult(testName);
                                if (audioTestResult != null && audioTestResult.equalsIgnoreCase(NONE)) {
                                    AdvancedAudioTest advancedAudioTest = new AdvancedAudioTest(activity, testName);
                                    advancedAudioTest.setTestFinishListener(AutoTest.this);
                                    advancedAudioTest.audioTest();
                                    //resultToReturn = PASS;
                                    while (!audioTestsCompleted) {
                                        try {
                                            Thread.sleep(100);
                                            if (earJackState) {
                                                midTestCheck = true;
                                                break;
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS == testResult.getResultCode() && !midTestCheck) {
                                        resultToReturn = PASS;
                                    } else {
                                        resultToReturn = FAIL;
                                    }
                                    saveMicTestsResults(testResult.getTestAdditionalInfo());
                                } else {
                                    resultToReturn = audioTestResult;
                                }
                            } else {
//                        show toast with 5 seconds counter
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(earJackState) {
                                                Toast.makeText(activity, "Please remove earphones first", Toast.LENGTH_SHORT).show();
                                            }
//                                            final Toast toast = Toast.makeText(activity, "\nPlease remove the earphone first\n", Toast.LENGTH_LONG);
//                                            View view = toast.getView();
//                                            view.setBackgroundColor(Color.parseColor("#d04040"));
//                                            view.setPadding(28, 10, 28, 10);
//                                            view.animate().alpha(1.0f).setDuration(1500);
////                                set rounded corners to view
//                                            view.setClipToOutline(true);
//                                            view.setOutlineProvider(new ViewOutlineProvider() {
//                                                @Override
//                                                public void getOutline(View view, Outline outline) {
//                                                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
//                                                }
//                                            });
//                                            toast.show();
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
//                        int i = 0;
//                        while (i < 5000) {
//                            try {
//                                sleepStarted = true;
////                                Thread.sleep(11000);
//                                TimeUnit.MILLISECONDS.sleep(1);
//                                i++;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                                Thread.currentThread().interrupt();
//                            }
//                        }

//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                Toast.makeText(activity, "Nishant", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                                if (earJackState) {
                                    resultToReturn = FAIL;
                                } else {
                                    resultToReturn = performTest(testName, frameLayout);
                                }
//                            }
//                        });
                            }
                        } else {
                            resultToReturn = SKIPPED;
                        }
                        DLog.d(TAG, "performTest nishant: " + resultToReturn);

//                activity.unregisterReceiver(mHeadsetReceiver);
//                unregister mHeadsetReceiver only if it is registered
                        if (activity != null && mHeadsetReceiver != null && mHeadsetReceiver.isOrderedBroadcast()) {
                            activity.unregisterReceiver(mHeadsetReceiver);
                            isReceiverRegistered = false;
                        }
                    }
                } else {
                    toggleBluetooth(false);
                    audioTestsCompleted = false;
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    activity.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(earJackState) {
                                    Toast.makeText(activity, "Please remove earphones first", Toast.LENGTH_SHORT).show();
                                }
                                //showSnackbar(activity, context.getString(R.string.earphone_remove));
//                                final Toast toast = Toast.makeText(activity, "\nPlease remove the earphone first\n", Toast.LENGTH_LONG);
//                                View view = toast.getView();
//                                view.setBackgroundColor(Color.parseColor("#d04040"));
//                                view.setPadding(28, 10, 28, 10);
//                                view.animate().alpha(1.0f).setDuration(1500);
////                                set rounded corners to view
//                                view.setClipToOutline(true);
//                                view.setOutlineProvider(new ViewOutlineProvider() {
//                                    @Override
//                                    public void getOutline(View view, Outline outline) {
//                                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
//                                    }
//                                });
//                                toast.show();
                            } catch (Exception e) {
                            }
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (testName != TestName.SPEAKERTEST || (testName == TestName.SPEAKERTEST && am.getRingerMode() != 0)) {
                        if (!earJackState) {
                            String audioTestResult = getAudioTestResult(testName);
                            if (audioTestResult != null && audioTestResult.equalsIgnoreCase(NONE)) {
                                AdvancedAudioTest advancedAudioTest = new AdvancedAudioTest(activity, testName);
                                advancedAudioTest.setTestFinishListener(AutoTest.this);
//                        advancedAudioTest.startTest();
                                advancedAudioTest.audioTest();
                                //resultToReturn = PASS;
                                while (!audioTestsCompleted) {
                                    try {
                                        Thread.sleep(100);
                                        if (earJackState) {
                                            midTestCheck = true;
                                            break;
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS == testResult.getResultCode() && !midTestCheck) {
                                    resultToReturn = PASS;
                                } else {
                                    resultToReturn = FAIL;
                                }
                                saveMicTestsResults(testResult.getTestAdditionalInfo());
                            } else {
                                resultToReturn = audioTestResult;
                            }
                        } else {
//                        show toast with 5 seconds counter
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(earJackState) {
                                            Toast.makeText(activity, "Please remove earphones first", Toast.LENGTH_SHORT).show();
                                        }
//                                        showSnackbar(activity, context.getString(R.string.earphone_remove));
//                                        DebugDLogUtil.e("try running");
//                                        final Toast toast = Toast.makeText(activity, "\nPlease remove the earphone first\n", Toast.LENGTH_LONG);
//                                        View view = toast.getView();
//                                        view.setBackgroundColor(Color.parseColor("#d04040"));
//                                        view.setPadding(28, 10, 28, 10);
//                                        view.animate().alpha(1.0f).setDuration(1500);
////                                set rounded corners to view
//                                        view.setClipToOutline(true);
//                                        view.setOutlineProvider(new ViewOutlineProvider() {
//                                            @Override
//                                            public void getOutline(View view, Outline outline) {
//                                                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
//                                            }
//                                        });
//                                        toast.show();
                                    } catch (Exception e) {
                                        DLog.e(TAG,e.getMessage());
                                    }
                                }
                            });
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                        int i = 0;
//                        while (i < 5000) {
//                            try {
//                                sleepStarted = true;
////                                Thread.sleep(11000);
//                                TimeUnit.MILLISECONDS.sleep(1);
//                                i++;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                                Thread.currentThread().interrupt();
//                            }
//                        }

//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                Toast.makeText(activity, "Nishant", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                            if (earJackState) {
                                resultToReturn = FAIL;
                            } else {
                                resultToReturn = performTest(testName, frameLayout);
                            }
//                            }
//                        });
                        }
                    } else {
                        resultToReturn = SKIPPED;
                    }
                    DLog.d(TAG, "performTest nishant: " + resultToReturn);

//                activity.unregisterReceiver(mHeadsetReceiver);
//                unregister mHeadsetReceiver only if it is registered
                    if (activity != null && mHeadsetReceiver != null && mHeadsetReceiver.isOrderedBroadcast()) {
                        activity.unregisterReceiver(mHeadsetReceiver);
                        isReceiverRegistered = false;
                    }
                }

                midTestCheck = false;
                break;
            case FRONTCAMERAPICTURETEST:

                Window window = activity.getWindow();
                WindowManager.LayoutParams layout = window.getAttributes();
                Float brightness = layout.screenBrightness;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DLog.d(TAG, "AutoTestCameraPicture FRONTCAMERAPICTURETEST test Started...........");

                        if (force_break) {
                            vibration_result=false;

                        }
                        layout.screenBrightness = 1F;
                        window.setAttributes(layout);
                        frameLayout.setVisibility(View.VISIBLE);
                        AutoTestCameraPicture testCameraPicture = new AutoTestCameraPicture();
                        testCameraPicture.setTestFinishListener(AutoTest.this);
                        testCameraPicture.setSaveToFile(true);
                        testCameraPicture.startCamera(TestCameraPicture.FACING_FRONT, frameLayout);
                    }
                });


                while (!frontCameraPictureTestCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DLog.d(TAG, "AutoTestCameraPicture test Started...........frontCameraPictureTestCompleted " + frontCameraPictureTestCompleted + "after while , testResult.getResultCode()" + testResult.getResultCode());

                if (frontCameraPictureTestCompleted) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.screenBrightness = (layout.BRIGHTNESS_OVERRIDE_NONE);
                            window.setAttributes(layout);
                        }
                    });
                }
                if (org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS == testResult.getResultCode()) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;
                
            case REARCAMERAPICTURETEST:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DLog.d(TAG, "AutoTestCameraPicture REARCAMERAPICTURETEST test Started...........");
                        frameLayout.setVisibility(View.VISIBLE);
                        AutoTestCameraPicture testCameraPicture = new AutoTestCameraPicture();
                        testCameraPicture.setTestFinishListener(AutoTest.this);
                        testCameraPicture.setSaveToFile(true);
                        testCameraPicture.startCamera(TestCameraPicture.FACING_REAR, frameLayout);
                    }
                });
                DLog.d(TAG, "AutoTestCameraPicture REARCAMERAPICTURETEST test Started.....2...... rearCameraPictureTestCompleted " + rearCameraPictureTestCompleted);
                while (!rearCameraPictureTestCompleted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DLog.d(TAG, "AutoTestCameraPicture test Started...........rearCameraPictureTestCompleted " + rearCameraPictureTestCompleted + "after while , testResult.getResultCode()" + testResult.getResultCode());

                if (org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS == testResult.getResultCode()) {
                    resultToReturn = PASS;
                } else {
                    resultToReturn = FAIL;
                }
                break;

            default:
                resultToReturn = NOTEQUIPPED;
        }
        DLog.d(TAG, testName + "-" + resultToReturn);
        if (CANBEIMPROVED.equalsIgnoreCase(resultToReturn)) {
            prepareResolutionList(testName);
        }

        return resultToReturn;
    }

    private Vibrator vibrator;

    private void startRecording(String status) {
        DLog.d(TAG, "startRecording: start "+status);


        AdvancedFrequencyy af= new AdvancedFrequencyy(context,status);

        af.startTest();
        af.setTestFinishListener(this);
        af.setValueListener(dataPassListener);

        DLog.d(TAG, "startRecording: end "+status);




    }

    void stop()
    {

    }

    private void vibrateForSeconds(int i) {

        DLog.d(TAG, "in vibrateForSeconds");


        if (vibrator.hasVibrator()) {
            DLog.d(TAG, "vibrator.hasVibrator passed");

            // Vibrate for the specified duration (in milliseconds)
            DLog.d(TAG, "vibrateForSeconds: 3");
            vibrator.vibrate(VibrationEffect.createOneShot(i * 1000L, VibrationEffect.DEFAULT_AMPLITUDE));
            DLog.d(TAG, "vibrateForSeconds: 3 done");
            startRecording("vibration");

        }
    }

    private void prepareResolutionList(String testName) {
        Resolution resolution = Resolution.getInstance();
        if (INTERNALSTORAGE.equalsIgnoreCase(testName)) {
            resolution.getAvailableResolutionsList().add(testName);
            /*while(!Resolution.getInstance().isFileResolutionDone()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
//            if(resolution.getImageFileList() != null && resolution.getImageFileList().size()>0)
//                resolution.getAvailableResolutionsList().add(ResolutionName.IMAGES);
//            if(resolution.getAudioFileList() != null && resolution.getAudioFileList().size()>0)
//                resolution.getAvailableResolutionsList().add(ResolutionName.MUSIC);
//            if(resolution.getVideoFileList() != null && resolution.getVideoFileList().size()>0)
//                resolution.getAvailableResolutionsList().add(ResolutionName.VIDEO);
//            if(resolution.getDuplicateFileList()!= null && resolution.getDuplicateFileList().size()>0)
//                resolution.getAvailableResolutionsList().add(ResolutionName.DUPLICATE);
            /*if(resolution.getStorageFileInfoList() != null) {
                if(!DeviceInfo.getInstance(OruApplication.getAppContext()).getInternalStorageStatus()) {
                    resolution.getAvailableResolutionsList().add(org.pervacio.wirelessapp.models.tests.ResolutionName.INTERNALSTORAGESUGGESTION);
                }
                ArrayList<PDStorageFileInfo> fileInfos = resolution.getStorageFileInfoList();
                for(PDStorageFileInfo fileInfo : fileInfos) {
                    if(PDStorageFileInfo.FILE_TYPE_IMAGE.equals(fileInfo.getFileType())
                            && !resolution.getAvailableResolutionsList().contains(ResolutionName.IMAGES)) {
                        resolution.getAvailableResolutionsList().add(ResolutionName.IMAGES);
                    } else if(PDStorageFileInfo.FILE_TYPE_AUDIO.equals(fileInfo.getFileType())
                            && !resolution.getAvailableResolutionsList().contains(ResolutionName.MUSIC)) {
                        resolution.getAvailableResolutionsList().add(ResolutionName.MUSIC);
                    } else if(PDStorageFileInfo.FILE_TYPE_VIDEO.equals(fileInfo.getFileType())
                            && !resolution.getAvailableResolutionsList().contains(ResolutionName.VIDEO)) {
                        resolution.getAvailableResolutionsList().add(ResolutionName.VIDEO);
                    }
                    if(fileInfo.isDuplicate() && !resolution.getAvailableResolutionsList().contains(ResolutionName.DUPLICATE)) {
                        resolution.getAvailableResolutionsList().add(ResolutionName.DUPLICATE);
                    }
                }
            }*/
        } else if (FREEMEMORY.equalsIgnoreCase(testName)) {
            while (!Resolution.getInstance().isMemoryResolutionDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (resolution.getForegroundAppList() != null && resolution.getForegroundAppList().size() > 0)
                resolution.getAvailableResolutionsList().add(ResolutionName.FOREGROUND_APPS);
            if (resolution.getBackgroundAppList() != null && resolution.getBackgroundAppList().size() > 0)
                resolution.getAvailableResolutionsList().add(ResolutionName.BACKGROUND_APPS);
            if (resolution.getAutostartAppList() != null && resolution.getAutostartAppList().size() > 0)
                resolution.getAvailableResolutionsList().add(ResolutionName.AUTOSTART_APPS);

        } else {
            if (testName != null && !testName.equalsIgnoreCase(PDConstants.SDCARDCAPACITY) && !testName.equalsIgnoreCase(PDConstants.SDCARDREADWRITE)) {
                if ((!isBluetoothConnected()&&testName.equalsIgnoreCase(BLUETOOTH_ON)) ){

                }else if((!isNFCOn()&&testName.equalsIgnoreCase(NFC_ON))){

                }else{
                    DLog.d( "Adding Resolution " , testName);
                    resolution.getAvailableResolutionsList().add(testName);
                }

            }
        }
    }



    private Boolean isNFCOn(){

        if (NfcAdapter.getDefaultAdapter(context.getApplicationContext())!=null){
            return NfcAdapter.getDefaultAdapter(context.getApplicationContext()).isEnabled();
        }else{
            return false;
        }

    }

    private String performSensorTest(final String sensorName, final float sensorMinValue, final float sensorMaxValue) {
        sensorTestDone = false;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testAutoSensor = TestAutoSensor.getInstance(sensorName, sensorMinValue, sensorMaxValue);
                DLog.d(TAG, "TestAutoSensor value is " + testAutoSensor);
                if (testAutoSensor != null) {
                    testAutoSensor.setTestFinishListener(AutoTest.this);
                    testAutoSensor.registerSensorEventListener();
                    testAutoSensor.registerSensorResultListener(AutoTest.this);
                }
            }
        });

        while (!sensorTestDone) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FEATURE_NOT_EQUIPPED) {
            return NOTEQUIPPED;
        } else if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
            return PASS;
        } else {
            return FAIL;
        }
    }

    private String performSensorTest(final String testName, final String sensorName, final float sensorMinValue, final float sensorMaxValue) {
        sensorTestDone = false;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testAutoSensor = TestAutoSensor.getInstance(sensorName, sensorMinValue, sensorMaxValue);
                DLog.d(TAG, "TestAutoSensor value is " + testAutoSensor + " sensorName " + sensorName);
                if (testAutoSensor != null) {
                    testAutoSensor.setTestFinishListener(AutoTest.this);
                    testAutoSensor.registerSensorEventListener();
                    testAutoSensor.registerSensorResultListener(AutoTest.this);
                }
            }
        });

        while (!sensorTestDone) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FEATURE_NOT_EQUIPPED) {
            return NOTEQUIPPED;
        } else if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
//            testResult.getResultDescription()
            String additionalInfo = testResult.getTestAdditionalInfo();
            DLog.d(TAG, "additionalInfo " + additionalInfo + " testName " + testName);
            testAdditionalInfoMap.put(testName, additionalInfo);
            return PASS;
        } else {
            return FAIL;
        }
    }

    private String performLTETest() {
        String result = NOTEQUIPPED;
        final boolean[] letSupport = {false};
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TestLTE testLTE = new TestLTE();
                if (testLTE.hasFeature()) {
                    letSupport[0] = true;
                    testLTE.setTestListener(AutoTest.this);
                    testLTE.startTest();
                } else {
                    lteTestDone = true;
                }
            }
        });
        while (!lteTestDone) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!letSupport[0]) {
            return result;
        }
        if (this.testResult != null && (this.testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS)) {
            result = PASS;
        } else {
            result = FAIL;
        }
        return result;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

/*    public boolean pervacioTest.isFeatureAvailable(String testName) {
        switch (testName) {
            case BLUETOOTH_OFF:
            case BLUETOOTH_ON:
            case BLUETOOTHCOMPREHENSIVETEST:
                return (aFBluetooth != null && aFBluetooth.pervacioTest.isFeatureAvailable());
            case GPS_OFF:
            case GPS_ON:
            case GPSCOMPREHENSIVETEST:
                return (afGPS != null && afGPS.pervacioTest.isFeatureAvailable());
            case WIFI_OFF:
            case WIFI_ON:
            case WIFICOMPREHENSIVETEST:
                return (afWifi != null && afWifi.pervacioTest.isFeatureAvailable());
            case NFC_OFF:
            case NFC_ON:
                return (afNFC != null && afNFC.pervacioTest.isFeatureAvailable());
            case SDCARD:
            case SDCARDCAPACITY:
            return globalConfig.isDeviceHasSDCardSlot();
            case GYROSCOPESENSORTEST:
                 return deviceInfo.isSensorAvailable(Sensor.TYPE_GYROSCOPE);
            case BAROMETERTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_PRESSURE);
            case MAGNETICSENSORTEST:
                return deviceInfo.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD);
            default:
                return true;
        }
    }*/

    DataPassListener dpl = new DataPassListener() {
        @Override
        public void onDataPass(Double TotalAvg, Double TotalAvgG) {


            DLog.d(TAG, "onDataPass: "+"hello");
        }
    };
    @Override
    public void onTestStart() {

        DLog.d(TAG, "onDatapass :  onTestStart Autotest");

    }

    @Override
    public void onSensorEventListner(Object o) {

    }

    @Override
    public void onTestEnd(org.pervacio.onediaglib.diagtests.TestResult testResult) {
        DLog.d(TAG, "onTestEnd name : " + testResult.getTestName());
        String testName = "";
        if (testResult != null) {
            testName = testResult.getTestName();
        }
        if (testName == null || testName.isEmpty()) {
            testName = currentTest;
        }
        //DLog.d(AutoTestTAG, "onTestEnd testName " + testName + " " + testResult.getResultCode());
        this.testResult = testResult;

        switch (testName) {
            case TestAutoSensor.ACCELEROMETERTEST:
            case TestAutoSensor.GYROSCOPE:
            case TestAutoSensor.MAGNETICSENSOR:
            case TestAutoSensor.BAROMETER:
            case TestAutoSensor.GAME_ROTATION_VECTOR:
            case TestAutoSensor.GEOMAGNETIC_ROTATION_VECTOR:
            case TestAutoSensor.ROTATION_VECTOR:
            case TestAutoSensor.LINEAR_ACCELERATION:
                testAutoSensor.unRegisterOnSensorEventListener();
                testAutoSensor.unRegisterSensorResultListener();
                sensorTestDone = true;
                break;
            case "gpsTest":
                GPSTestCompleted = true;
                break;
            case "bluetoothTest":
                //mTestBlueooth.diaspatchTest();
                BTToggleCompleted = true;
                break;
            case "wifiTest":
                //testWifi.diaspatchTest();
                WifiToggleCompleted = true;
                break;
            case PDConstants.LTETEST:
                lteTestDone = true;
                break;
            case SPEAKERTEST:
            case EARPIECETEST:
            case MICROPHONETEST:
            case MICROPHONE2TEST:
                audioTestsCompleted = true;
                break;
            case VIBRATIONTEST:
                DLog.d(TAG, "onTestEnd VIBRATIONTEST case");
                vibrationTestCompleted = true;

                break;
            case FRONTCAMERAPICTURETEST:
                DLog.d(TAG, "onTestEnd FRONTCAMERAPICTURETEST case");
                frontCameraPictureTestCompleted = true;
                break;
            case REARCAMERAPICTURETEST:
                DLog.d(TAG, "onTestEnd REARCAMERAPICTURETEST case");
                rearCameraPictureTestCompleted = true;
                break;
            default:
                break;
        }
    }





    private boolean wifiToogle() {
        WifiToggleCompleted = false;
        new Thread() {
            public void run() {
                testResult = testWIFI.toggleWiFiTest(500);
                WifiToggleCompleted = true;
            }
        }.start();

        while (!WifiToggleCompleted) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
            return true;
        } else {
            return false;
        }
    }

    private String getAudioTestResult(String testName) {
        if (MICROPHONE2TEST.equalsIgnoreCase(testName)) {
            if (TestResult.NONE.equalsIgnoreCase(globalConfig.getMic2TestResult())) {
                micTestPerformed = true;
                globalConfig.setMic2TestResult(testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS ? TestResult.PASS : TestResult.FAIL);
//            return mic2TestResult;
                return globalConfig.getMic2TestResult();
            } else if (MICROPHONETEST.equalsIgnoreCase(testName)) {
            /*TestInfo speakerResult = pervacioTest.getAutoTestResult().get(SPEAKERTEST);
            TestInfo receiverResult = pervacioTest.getAutoTestResult().get(EARPIECETEST);
            if(speakerResult != null && speakerResult.getTestResult().equalsIgnoreCase(PASS)) {
                return PASS;
            }
            if(receiverResult != null) {
                return  receiverResult.getTestResult().equalsIgnoreCase(PASS) ? PASS : FAIL;
            } else {
                micTestPerformed = true;
                return NONE;
            }*/
                if (TestResult.NONE.equalsIgnoreCase(globalConfig.getMic1TestResult())) {
                    micTestPerformed = true;
//            return mic1TestResult;
                    globalConfig.setMic1TestResult(testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS ? TestResult.PASS : TestResult.FAIL);
                    return globalConfig.getMic1TestResult();
                } else if (EARPIECETEST.equalsIgnoreCase(testName)) {
                    //TestInfo micResult = pervacioTest.getAutoTestResult().get(MICROPHONETEST);
//                    if(globalConfig.getMic1TestResult().equalsIgnoreCase(TestResult.PASS)) {
//                        return TestResult.PASS;
//                    }else
                    if (micTestPerformed) {
//                return mic1TestResult.equalsIgnoreCase(PASS) ? PASS : FAIL;
                        globalConfig.setMic1TestResult(testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS ? TestResult.PASS : TestResult.FAIL);
                        return globalConfig.getMic1TestResult();
                    } else {
                        return NONE;
                    }
                }
                return NONE;
            }
        }
        return NONE;
    }

    public boolean isBluetoothConnected() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            }
            int a2dpState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            int headsetState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            int healthState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);

            if (a2dpState == BluetoothProfile.STATE_CONNECTED ||
                    headsetState == BluetoothProfile.STATE_CONNECTED ||
                    healthState == BluetoothProfile.STATE_CONNECTED) {
                // Bluetooth is connected to at least one device
                return true;
            } else {
                // Bluetooth is not connected to any device
                return false;
            }
        } else {
            // Bluetooth is either not supported or not enabled
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public boolean hasPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_REQUEST_CODE);
            return false;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return !pairedDevices.isEmpty();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean hasAvailableWifiNetworks() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_PERMISSION_REQUEST_CODE);
            return false;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Check if connected to a Wi-Fi network
        if (wifiInfo != null && wifiInfo.getNetworkId() != -1) {
            return true;
        }

        return false;
    }


    public void toggleBluetooth(boolean enable) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2)
        {
            try{
                if (enable && !bluetoothAdapter.isEnabled()) {

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)
                            != PackageManager.PERMISSION_GRANTED) {

                        DLog.e(TAG,"DENIED");

                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_REQUEST_CODE);
                    }

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                            != PackageManager.PERMISSION_GRANTED) {
                        DLog.e(TAG,"DENIED");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_REQUEST_CODE);
                    }
                    bluetoothAdapter.enable();
                } else if (!enable && bluetoothAdapter.isEnabled()) {
                    DLog.e(TAG,"DISABLED");
                    bluetoothAdapter.disable();
                }
            }
            catch (Exception e)
            {
                DLog.e(TAG,e.getMessage());
            }
        }
    }


    private void saveMicTestsResults(String testAdditionalInfo) {
        StringTokenizer st = new StringTokenizer(testAdditionalInfo, ",");
        while (st.hasMoreTokens()) {
            String messagePair = st.nextToken();
            if (messagePair.contains(":")) {
                int indexOfAssignment = messagePair.indexOf(":");
                String key = messagePair.substring(0, indexOfAssignment);
                String value = messagePair.substring(indexOfAssignment + 1);

                switch (key) {
                    case "Mic1Test":
//                        mic1TestResult = value;
                        globalConfig.setMic1TestResult(value);
                        break;
                    case "Mic2Test":
//                        mic2TestResult = value;
                        globalConfig.setMic2TestResult(value);
                        break;
                    default:
                        break;
                }

            }
        }
    }



    ArrayList<Double> total = new ArrayList<>();
    ArrayList<Double> totalg = new ArrayList<>();


    @Override
    public void onDataPass(Double TotalAvg, Double TotalAvgG) {

        DLog.d(TAG, "onDataPass: Autotest \n "+TotalAvg+"\n"+TotalAvgG);
        total.add(TotalAvg);
        totalg.add(TotalAvgG);

    }


    public void detect_stop(boolean value) {
        this.force_break = value;

        DLog.d(TAG, "setValueListener: "+force_break);
    }
    public void onDestroy() {

        if(isReceiverRegistered){
            activity.unregisterReceiver(mHeadsetReceiver);
            isReceiverRegistered = false;
        }

    }

}