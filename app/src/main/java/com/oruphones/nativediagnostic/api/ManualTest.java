package com.oruphones.nativediagnostic.api;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Intent.ACTION_POWER_CONNECTED;
import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static org.pervacio.onediaglib.diagtests.TestVibrationResult.RESULT_RELAUCH_APP;
import static org.pervacio.onediaglib.diagtests.TestVibrationResult.RESULT_SHOW_VIBRATION_ALERT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Outline;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.manualtests.KeysTestActivity;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.oneDiagLib.AudioPlayTestListener;
import com.oruphones.nativediagnostic.oneDiagLib.AudioPlayer;
import com.oruphones.nativediagnostic.oneDiagLib.DeadPixelTest;
import com.oruphones.nativediagnostic.oneDiagLib.HeadsetPlugStateListener;
import com.oruphones.nativediagnostic.oneDiagLib.TestFlash;
import com.oruphones.nativediagnostic.oneDiagLib.TestListener;
import com.oruphones.nativediagnostic.oneDiagLib.TestUsbConnection;
import com.oruphones.nativediagnostic.oneDiagLib.TestVibration;
import com.oruphones.nativediagnostic.oneDiagLib.TouchTest;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DevelopmentTools.ORUPERMISSIONCODES;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.BuildConfig;

import org.pervacio.onediaglib.advancedtest.cameraautomation.SemiAutoTestCameraPicture;
import org.pervacio.onediaglib.atomicfunctions.AFUsbConnection;
import org.pervacio.onediaglib.audio.AudioPlayInfo;
import org.pervacio.onediaglib.audio.AudioRecordInfo;
import org.pervacio.onediaglib.audio.AudioRecordTestListener;
import org.pervacio.onediaglib.audio.AudioRecorder;
import org.pervacio.onediaglib.audio.HeadsetJackTest;
import org.pervacio.onediaglib.diagtests.CallTest;
import org.pervacio.onediaglib.diagtests.DiagTimer;
import org.pervacio.onediaglib.diagtests.ISensorEventListener;
import org.pervacio.onediaglib.diagtests.ISensors;
import org.pervacio.onediaglib.diagtests.ITestCameraPicture;
import org.pervacio.onediaglib.diagtests.ITestCameraVideo;
import org.pervacio.onediaglib.diagtests.ScreenDiscolourTest;
import org.pervacio.onediaglib.diagtests.ScreenTestResults;
import org.pervacio.onediaglib.diagtests.TestAccelerometer;
import org.pervacio.onediaglib.diagtests.TestAirGesture;
import org.pervacio.onediaglib.diagtests.TestBluetooth;
import org.pervacio.onediaglib.diagtests.TestBluetoothResults;
import org.pervacio.onediaglib.diagtests.TestCameraPicture;
import org.pervacio.onediaglib.diagtests.TestCameraResult;
import org.pervacio.onediaglib.diagtests.TestDimming;
import org.pervacio.onediaglib.diagtests.TestDisplay;
import org.pervacio.onediaglib.diagtests.TestLightSensor;
import org.pervacio.onediaglib.diagtests.TestProximitySensor;
import org.pervacio.onediaglib.diagtests.TestWiFi;
import org.pervacio.onediaglib.diagtests.TestWifiResult;
import org.pervacio.onediaglib.utils.CameraUtil;
import static com.oruphones.nativediagnostic.util.TestUtil.camNum;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pervacio on 04-09-2017.
 */
public class ManualTest implements TestListener, ISensorEventListener, TestName, TestResult, AudioPlayTestListener, HeadsetPlugStateListener, HeadsetJackTest.HeadsetJackStateListener, AudioRecordTestListener, CallTest.CallTestListener {
    private static ManualTest manualTest;
    private final float lightThreshold = 25;
    private final int allSoundOffUnchecked = 0;
    ISensors sensorListener;
    private static String TAG = ManualTest.class.getSimpleName();
    private Activity activity = null;
    private PervacioTest pervacioTest;
    private TestDisplay mTestDisplay;
    private DeadPixelTest mDeadPixelTest;
    private ScreenDiscolourTest mScreenDiscolourTest;
    private Handler handler;
    private String testName = "";
    private TestDimming mTestDimming = null;
    private TestVibration testVibration;
    private AudioPlayer mAudioPlayer;
    private AudioPlayInfo mAudioPlayInfo;
    private TouchTest mTouchview;
    private CallTest mCallTest;
    private boolean isSensorRegistered = false;
    private TestAirGesture testAirGesture;
    private boolean isTestRunning = false;
    private GlobalConfig globalConfig;
    private TestBluetooth mTestBluetooth;
    private TestWiFi mTestWifi;
    private TestBluetoothResults mTestBluetoothResults;
    private TestWifiResult mTestWifiResults;
    private String mDetectedDevice = "";
    private HeadsetJackTest mHeadsetJackTest;
    private KeysTest keysInstance;
    private boolean earJackState = false;
    Handler handler2 = new Handler();
    BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            receiver for check headset jack
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        earJackState = false;
                        showHeadphoneConnectedDialog(false);
                        break;
                    case 1:
                        earJackState = true;
                        showHeadphoneConnectedDialog(true);
                        break;
                    default:
                        earJackState = false;
                        showHeadphoneConnectedDialog(false);
                }
            }
        }
    };

    private int initialCallVol = -1;
    private boolean isPaused = false;
    private boolean isPlayed = false;
    private boolean pluginState = true;
    private int passCount = 0;
    private FrameLayout layoutFrame;
    private TestFlash mTestFlashInstance;
    private Handler mTestHandler = new Handler();
    private int flashCount = 0;
    private ITestCameraPicture mTestCamera;
    private SemiAutoTestCameraPicture semiAutoTestCameraPicture;
    private ITestCameraVideo mTestVideo;
    private AFUsbConnection afUsbConnection;
    private TestUsbConnection mTestUSB;
    private String folderAudioPath = "sprint";
    private String audioFileName = "pvc_audio.mp3";
    private Boolean mRecordDone = false;
    private boolean mIsRecorderInitialized = false;
    private String recordedFilePath;
    private SharedPreferences preferences;
    private AudioManager audioManager;
    private AudioRecorder mAudioRecorder;
    private AudioRecordInfo mAudioRecordInfo;
    private boolean manualTestDone = false;
    private boolean timeout = false;
    private ArrayList<String> deletableFiles = new ArrayList<>();
    AudioManager am;
    private int[] excludeNumbers = new int[3];

    private org.pervacio.onediaglib.diagtests.TestResult lastPerformedTest = new org.pervacio.onediaglib.diagtests.TestResult();
    private boolean mIsOrientationChanged;
    private int mOrientationChangeCount = 0;
    private boolean isup, isdown, isright, isleft;
    private boolean isMicrophonePlayed = false;
    private boolean deviceVibration = false;
    private boolean isonCall = false;
    private boolean isVideoRecording = false;
    private boolean isChargerConnected_ = false;
    private boolean reperformedMicTest = false;

    private ManualTest(Activity activity) {
        this.activity = activity;
        pervacioTest = PervacioTest.getInstance();
        globalConfig = pervacioTest.getGlobalConfig();
        am = (AudioManager) OruApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        initTestObjects();
        TelephonyManager telephonyManager = (TelephonyManager) OruApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new myPhoneStateChangeListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    public static ManualTest getInstance(Activity activity) {
        if (manualTest == null) {
            manualTest = new ManualTest(activity);
        } else {
            manualTest.updateActivity(activity);
        }
        return manualTest;
    }

    private void updateActivity(Activity activity) {
        this.activity = activity;
        manualTestDone = false;
    }

    private void initTestObjects() {

    }

    public void recordVideoOrCaptuePhoto() {
        if (cameraRunnable != null)
            mTestHandler.removeCallbacks(cameraRunnable);
        mTestHandler.postDelayed(cameraRunnable, 500);
    }

    public void stopVideoCapture() {
        if (cameraRunnable != null)
            mTestHandler.removeCallbacks(cameraRunnable);
        mTestHandler.postDelayed(cameraRunnable, 500);
    }

    public void stopAudioCapture() {
        if (null != mAudioRecorder) {
            mAudioRecorder.endRecording();
        }
    }

    public void performCameraTest(String testName, FrameLayout layoutFrame, Handler handler) {
        clean();
        this.testName = testName;
        this.handler = handler;
        this.layoutFrame = layoutFrame;
        if (testName != null && (!testName.equalsIgnoreCase(TestName.CAMERAFLASHTEST) || !testName.equalsIgnoreCase(TestName.FRONTFLASHTEST))) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        boolean grantCamera = permissionCheck(Manifest.permission.CAMERA);
        boolean grantWrite_External = permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        boolean grantWrite_External = true;
        boolean grantRecord_Audio = permissionCheck(Manifest.permission.RECORD_AUDIO);

        if (testName != null && (testName.equalsIgnoreCase(TestName.CAMERAFLASHTEST) ||
                testName.equalsIgnoreCase(TestName.FRONTFLASHTEST) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST1) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST2) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST3) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST4) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST5) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST6) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST1) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST2) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST3) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST4) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST5) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST6))) {
            if ((!grantCamera && grantWrite_External) || (grantCamera && !grantWrite_External) || (!grantCamera && !grantWrite_External)) {
                DLog.d(TAG, "PermissionCHeck");
                org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
                onTestEnd(testResult);
            } else {
                DLog.d(TAG, "PermissionCHeck1");
                callGrantPermissionDenied(testName);
            }
        } else if (testName != null && (testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST1) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST2) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST3) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST4) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST5) ||
                testName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST6) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST1) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST2) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST3) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST4) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST5) ||
                testName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST6))) {
            if ((!grantCamera && !grantWrite_External && !grantRecord_Audio) || (!grantCamera && !grantWrite_External && grantRecord_Audio) || (!grantCamera && grantWrite_External && !grantRecord_Audio) || (!grantCamera && grantWrite_External && grantRecord_Audio) || (grantCamera && !grantWrite_External && !grantRecord_Audio) || (grantCamera && !grantWrite_External && grantRecord_Audio)
                    || (grantCamera && grantWrite_External && !grantRecord_Audio)) {
                DLog.d(TAG, "PermissionCHeck");
                org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
                onTestEnd(testResult);
            } else {
                DLog.d(TAG, "PermissionCHeck1");
                callGrantPermissionDenied(testName);
            }
        }
    }

    public void initMicrophoneTest(String testName, Handler handler) {
        this.testName = testName;
        this.handler = handler;
        mTestHandler.postDelayed(timeOutRunnable, 30000);
    }

    public void performMicrophoneTest(String testName, Handler handler, String action) {
        this.testName = testName;
        this.handler = handler;
        mTestHandler.removeCallbacks(timeOutRunnable);
        mTestHandler.postDelayed(timeOutRunnable, 15000);
        if (!(permissionCheck(Manifest.permission.RECORD_AUDIO))) {
            org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
            testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
            onTestEnd(testResult);
            return;
        }
        if (mHeadsetJackTest != null)
            mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mHeadsetJackTest = new HeadsetJackTest(activity);
        mHeadsetJackTest.setHeadsetJackStateListener(this);
        mHeadsetJackTest.registerHeadsetStateEventReceiver();

        if ("Record".equalsIgnoreCase(action)) {
            cleanMicrophoneState();
            if (!AudioPlayer.isHeadsetConnected(activity)) {
                initRecorder(1);
            }
        } else {
            if (!AudioPlayer.isHeadsetConnected(activity))
                checkAllSoundOffEnabled();
        }
    }

    public void performMicrophoneTest(String testName, Handler handler, String action, int micNumber) {
        this.testName = testName;
        this.handler = handler;
        mTestHandler.removeCallbacks(timeOutRunnable);
        mTestHandler.postDelayed(timeOutRunnable, 15000);
        if (!(permissionCheck(Manifest.permission.RECORD_AUDIO))) {
            org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
            testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
            onTestEnd(testResult);
            return;
        }
        if (mHeadsetJackTest != null)
            mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mHeadsetJackTest = new HeadsetJackTest(activity);
        mHeadsetJackTest.setHeadsetJackStateListener(this);
        mHeadsetJackTest.registerHeadsetStateEventReceiver();

        if ("Record".equalsIgnoreCase(action)) {
            cleanMicrophoneState();
            if (!AudioPlayer.isHeadsetConnected(activity)) {
                initRecorder(micNumber);
            }
        } else {
            if (!AudioPlayer.isHeadsetConnected(activity))
                checkAllSoundOffEnabled();
        }
    }

    public void performTest(String testName, Handler handler) {

        clean();
        this.testName = testName;
        this.handler = handler;
        DLog.d(TAG, "Test Name:" + testName);

        DLog.d(TAG, "performTest: " + testName);


        DiagTimer.MANUALTEST_TIMEOUT = 30 * 1000;
        switch (testName) {
            case ACCELEROMETERTEST:
                if (Util.isAdvancedTestFlow()) {
                    DiagTimer.MANUALTEST_TIMEOUT = 10 * 1000;
                }
                sensorListener = TestAccelerometer.getInstance();
                TestAccelerometer.getInstance().setActivityInstance(activity);
                registerSensorListeners();
                break;
            case NFCTEST:
                if (NfcAdapter.getDefaultAdapter(activity.getApplicationContext())==null){
                    updateKeysTestResults(1, TestResult.NOTEQUIPPED);
                }
                break;
            case DEADPIXELTEST:
                mDeadPixelTest = new DeadPixelTest(DeadPixelTest.ScreenTest.DEADPIXEL);
                activity.setContentView(mDeadPixelTest);
                mDeadPixelTest.setOnTestCompleteListener(ManualTest.this);
                break;
            case DISCOLORATIONTEST:
//                mScreenDiscolourTest = new ScreenDiscolourTest(ScreenDiscolourTest.ScreenTest.SCREEN_DISCOLOR,
//                        new int[]{Color.BLACK, Color.WHITE}, Color.YELLOW);
                mDeadPixelTest = new DeadPixelTest(DeadPixelTest.ScreenTest.SCREEN_DISCOLOR);
                activity.setContentView(mDeadPixelTest);
                mDeadPixelTest.setOnTestCompleteListener(ManualTest.this);
                break;
            case SCREENBURNTEST:
                /*mScreenDiscolourTest = new ScreenDiscolourTest(ScreenDiscolourTest.ScreenTest.SCREEN_BURN,
                        new int[]{Color.BLACK, Color.WHITE}, Color.YELLOW);*/
                mDeadPixelTest = new DeadPixelTest(DeadPixelTest.ScreenTest.SCREEN_BURN);
                activity.setContentView(mDeadPixelTest);
                mDeadPixelTest.setOnTestCompleteListener(ManualTest.this);
                break;
            case DISPLAYTEST:
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                mTestDisplay = new TestDisplay();
                mTestDisplay.setDisplayText(true, activity.getResources().getString(R.string.tap_toproceed));
                activity.setContentView(mTestDisplay);
                mTestDisplay.setOnTestCompleteListener(ManualTest.this);
                break;
            case AMBIENTTEST:
                sensorListener = TestLightSensor.getInstance(lightThreshold);
                registerSensorListeners();
                mTestHandler.postDelayed(timeOutRunnable, DiagTimer.MANUALTEST_TIMEOUT);
                break;
            case PROXIMITYTEST:
                if (Util.isAdvancedTestFlow()) {
                    DiagTimer.MANUALTEST_TIMEOUT = 10 * 1000;
                }
                sensorListener = TestProximitySensor.getInstance();
                registerSensorListeners();
                break;
            case BLUETOOTH_TOGGLE:
                DLog.e(TAG, "REACHED");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter == null) {
                            DLog.d("Bluetooth Test",testName + "Fail");
                            postMessageToHandler(1, TestResult.FAIL);
                        } else {
                            if (!bluetoothAdapter.isEnabled()) {
                                // Bluetooth is currently off, turning it on
                                DLog.d("Bluetooth Test",testName + "turn on");
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                Toast.makeText(activity, R.string.enable_bluetooth_permission, Toast.LENGTH_SHORT).show();
                                activity.startActivityForResult(enableBtIntent, ORUPERMISSIONCODES.BLUETOOTH_STATE_PERMISSION);
                            } else {
//                                // Bluetooth is currently on, turning it off
//                                bluetoothAdapter.disable();

                                DLog.d("Bluetooth Test",testName + "passed");
                                Toast.makeText(activity, R.string.enable_bluetooth_permission2, Toast.LENGTH_SHORT).show();
                                postMessageToHandler(0, TestResult.PASS);
                            }
                        }
                    }
                });
                break;
            case VIBRATIONTEST:

                DLog.d(TAG, "performTest case VIBRATIONTEST : " + testName);

                testVibration = TestVibration.getInstance();
                testVibration.setIsHybtidTest(true);
                testVibration.setTestFinishListener(this);


                try {
                    try {

                        DLog.d(TAG, "performTest changeVibrationIntensity : " + testName);

                        changeVibrationIntensity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!deviceVibration) {
                        DLog.d(TAG, "performTest if (!deviceVibration) : " + testName);
                        testVibration.startVibration();
                    }

                 /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isDeviceVibrationZero()) {
                        //activity.startActivityForResult(new Intent(Settings.ACTION_SOUND_SETTINGS), 0);
                        postMessageToHandler(ManualTestEvent.VIBRATION_INTENSITY_ZERO, "");
                    } else {

                    }*/
                     /*}else {
                        postMessageToHandler(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED, ACCESSDENIED);
                    }*/
                } catch (Exception e) {
                    DLog.e(TAG, "Exception:" + e.getMessage());
                }
                break;
            case EARPHONETEST:
                globalConfig.setCurrentTest(testName);
                if (!AudioPlayer.isHeadsetConnected(OruApplication.getAppContext()) && !earJackState) {
                    earJackState = true;
                    handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST);
                }
                try {
                    Runnable checkAllSoundOffRunnable = new Runnable() {
                        private final long startTime = System.currentTimeMillis();
                        private final long timeout = 15000;

                        @Override
                        public void run() {
                            if (System.currentTimeMillis() - startTime < timeout) {
                                if (!AudioPlayer.isHeadsetConnected(OruApplication.getAppContext())) {
                                    checkAllSoundOffEnabled();
                                    handler.postDelayed(this, 3000);
                                } else {
                                    earJackState = false;
                                    checkAllSoundOffEnabled();
                                }
                            }
                        }
                    };
                    checkAllSoundOffRunnable.run();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacksAndMessages(checkAllSoundOffRunnable);
                        }
                    }, 15000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTestHandler.postDelayed(timeOutRunnable, 15000);
                break;
            case SPEAKERTEST:
                globalConfig.setCurrentTest(testName);
                checkAllSoundOffEnabled();
                mTestHandler.postDelayed(timeOutRunnable, 25000);
                break;
            case EARPIECETEST:
                globalConfig.setCurrentTest(testName);
                checkAllSoundOffEnabled();
                mTestHandler.postDelayed(timeOutRunnable, 25000);
                break;
            case EARPHONEJACKTEST:
                Boolean k = globalConfig.getEarPhoneTestResult();
                if (k) {
                    globalConfig.setEarPhoneTestResult(false);
                    handler.sendEmptyMessage(9);
                    Toast.makeText(activity, "Tested Skipped as Earphone test Passed", Toast.LENGTH_SHORT).show();
//                    mTestHandler.postDelayed(timeOutRunnable, 1500);
                } else {
                    mHeadsetJackTest = new HeadsetJackTest(activity);
                    mHeadsetJackTest.setHeadsetJackStateListener(this);
                    mHeadsetJackTest.registerHeadsetStateEventReceiver();
                    if (!AudioPlayer.isHeadsetConnected(OruApplication.getAppContext()) && !earJackState) {
                        earJackState = true;
                        handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST);
                    } else {
                        handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST);
                    }
                    mTestHandler.postDelayed(timeOutRunnable, 15000);
                }
                break;
            case TOUCHTEST:
                if (Util.isAdvancedTestFlow()) {
                    mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_FINGER, TouchTest.DOCOMO_FULL_SCREEN_PATTERN, 7, 15, true);
                } else {
                    mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_FINGER, TouchTest.DOCOMO_FULL_SCREEN_PATTERN, 7, 15, true);
                }
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                mTouchview.setTouchBoxColor(activity.getResources().getColor(R.color.touch_color));
                activity.setContentView(mTouchview);
                mTouchview.setTestFinishListener(this);
                break;
            case SPENTEST:
                mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_SPEN, TouchTest.SQUARE_PLUS_PATTERN, true);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                mTouchview.setTouchBoxColor(activity.getResources().getColor(R.color.touch_color));
                activity.setContentView(mTouchview);
                mTouchview.setTestFinishListener(this);
                break;
            case TSPHOVERINGTEST:
                mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_TSP_HOVERING, TouchTest.SQUARE_STAR_PATTERN, true);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                mTouchview.setTouchBoxColor(activity.getResources().getColor(R.color.touch_color));
                activity.setContentView(mTouchview);
                mTouchview.setTestFinishListener(this);
                break;
            case SPENHOVERINGTEST:
                mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_SPEN_HOVERING, TouchTest.SQUARE_PLUS_PATTERN, true);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                mTouchview.setTouchBoxColor(activity.getResources().getColor(R.color.touch_color));
                activity.setContentView(mTouchview);
                mTouchview.setTestFinishListener(this);
                break;
            case CALLTEST:
                mCallTest = new CallTest(activity.getApplicationContext(), globalConfig.getCallTestNumber(), ManualTest.this);
                if (!(permissionCheck(Manifest.permission.READ_PHONE_STATE))) {
                    org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                    testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
                    onTestEnd(testResult);
                } else {
                    mCallTest.startCall();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 2000);
                }
                break;
            case DIMMINGTEST:
                mTestDimming = new TestDimming(activity.getWindow(), (ViewGroup) activity.getWindow().getDecorView(), true, false);
                mTestDimming.setOnTestCompleteListener(ManualTest.this);
                break;
            case GUESTURETEST:
                registerSensorListeners();
                break;
            case WIFICONNECTIVITYTEST:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager == null) {
                            postMessageToHandler(1, TestResult.FAIL);
                        } else {
                            if (!wifiManager.isWifiEnabled()) {
                                // WiFi is currently off, turning it on
                                Intent enableWifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                Toast.makeText(activity, R.string.enable_wifi_permission, Toast.LENGTH_SHORT).show();
                                activity.startActivityForResult(enableWifiIntent, ORUPERMISSIONCODES.WIFI_STATE_PERMISSION);
                            } else {
                                // WiFi is currently on
//                                if (hasAtLeastTwoSavedNetworks(wifiManager)) {
//                                    // At least two saved networks are available
//                                    Toast.makeText(activity, "Passed as WiFi is On and has at least 2 saved networks", Toast.LENGTH_SHORT).show();
//                                    postMessageToHandler(0, TestResult.PASS);
//                                } else {
//                                    // Less than two saved networks available
//                                    Toast.makeText(activity, "Failed as WiFi is On but has less than 2 saved networks", Toast.LENGTH_SHORT).show();
//                                    postMessageToHandler(1, TestResult.FAIL);
//                                }
                                Toast.makeText(activity, R.string.enable_wifi_permission2, Toast.LENGTH_SHORT).show();
                                postMessageToHandler(0, TestResult.PASS);
                            }
                        }
                    }
                });
                break;

            case BLUETOOTHCONNECTIVITYTEST:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter == null) {
                            DLog.d("Bluetooth Test",testName + "Fail");
                            postMessageToHandler(1, TestResult.FAIL);
                        } else {
                            if (!bluetoothAdapter.isEnabled()) {
                                // Bluetooth is currently off, turning it on
                                DLog.d("Bluetooth Test",testName + "turn on");
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                Toast.makeText(activity, R.string.enable_bluetooth_permission, Toast.LENGTH_SHORT).show();
                                activity.startActivityForResult(enableBtIntent, ORUPERMISSIONCODES.BLUETOOTH_STATE_PERMISSION);
                            } else {
//                                // Bluetooth is currently on, turning it off
//                                bluetoothAdapter.disable();

                                DLog.d("Bluetooth Test",testName + "passed");
                                Toast.makeText(activity, R.string.enable_bluetooth_permission2, Toast.LENGTH_SHORT).show();
                                postMessageToHandler(0, TestResult.PASS);
                            }
                        }
                    }
                });
                break;

            case USBTEST:
                afUsbConnection = new AFUsbConnection();
                if (afUsbConnection.isUSBConnected()) {
                    org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                    testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
                    onTestEnd(testResult);
                } else {
                    mTestUSB = new TestUsbConnection();
                    mTestUSB.setOnTestCompletedListener(this);
                    mTestUSB.USBConnectionManualTest();
                    handler.sendEmptyMessage(ManualTestEvent.USB_NOT_CONNECTED);
                }
                break;
            case CHARGINGTEST:
                afUsbConnection = new AFUsbConnection();
                if (isChargerConnected() && (!afUsbConnection.isUSBConnected())) {
                    DLog.i(TAG, "CHARGINGTEST case isChargerConnected_ true afUsbConnection.isUSBConnected() false");
                    org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                    testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
                    onTestEnd(testResult);
                } else {
                    DLog.i(TAG, "CHARGINGTEST else case isChargerConnected_" + isChargerConnected_ + " afUsbConnection.isUSBConnected() " + afUsbConnection.isUSBConnected());
                    activity.registerReceiver(batteryChangedReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
/*                    activity.registerReceiver(chargerConnectedReciever, new IntentFilter(ACTION_POWER_CONNECTED));
                    activity.registerReceiver(chargerConnectedReciever, new IntentFilter(ACTION_POWER_DISCONNECTED));*/
                    mTestHandler.postDelayed(timeOutRunnable, DiagTimer.MANUALTEST_TIMEOUT);
                    handler.sendEmptyMessage(ManualTestEvent.CHARGING_CHARGER_NOT_CONNECTED);
                    isChargerConnected_ = false;
                }
                break;
            case HARDKEYTEST:
            case SOFTKEYTEST:
                KeysTestActivity.startTheTimer();
                DLog.d(TAG, "Hard Key Test - WORKING");
                if (Util.isAdvancedTestFlow()) {
                    DiagTimer.MANUALTEST_TIMEOUT = 10 * 1000;
                }
                if (activity != null) {
                   keysInstance = (KeysTest) activity;
                    keysInstance.performTest(testName);
                }
                break;
            default:
                break;
        }
    }


    private void checkAllSoundOffEnabled() {
        int isAllSoundOff = isAllSoundsOffEnabled();
        if (isAllSoundOff == allSoundOffUnchecked) {
            if (MICROPHONETEST.equalsIgnoreCase(testName) || MICROPHONE2TEST.equalsIgnoreCase(testName)) {
                recordedFilePath = getStringFromPreference("recordedfilepath");
            }
            if (EARPHONETEST.equalsIgnoreCase(testName) || EARPIECETEST.equalsIgnoreCase(testName)) {
                if (EARPIECETEST.equalsIgnoreCase(testName)) {
//                    activity.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                    if (!earJackState) {
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DLog.d(TAG, "initAudioPlayer: 3 seconds delay");
                                initAudioPlayer(testName);
                            }
                        }, 2000);
                    }
                } else {
                    if (activity != null && mHeadsetReceiver != null && mHeadsetReceiver.isOrderedBroadcast()) {
                        activity.unregisterReceiver(mHeadsetReceiver);
                    }
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DLog.d(TAG, "initAudioPlayer: 3 seconds delay");
                            initAudioPlayer(testName);
                        }
                    }, 2000);
                }
            } else {
                if (SPEAKERTEST.equalsIgnoreCase(testName) || EARPIECETEST.equalsIgnoreCase(testName)) {
//                    activity.registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                    if (!earJackState) {
                        initAudioPlayer(testName);
//                    } else {
//                        show headphone connected dialog
//                        showHeadphoneConnectedDialog(earJackState);
                    }
                } else {
                    initAudioPlayer(testName);
                }

            }

            if (activity != null && mHeadsetReceiver != null && mHeadsetReceiver.isOrderedBroadcast()) {
                activity.unregisterReceiver(mHeadsetReceiver);
            }

        } else {
            handler.sendEmptyMessage(ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE);
        }
    }

    private void showHeadphoneConnectedDialog(boolean show) {
//        final Toast[] toast = new Toast[1];
        if (show) {
            final Toast toast = Toast.makeText(activity, "\nPlease remove the earphone first\n", Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundColor(Color.parseColor("#d04040"));
            view.setPadding(28, 10, 28, 10);
            view.animate().alpha(1.0f).setDuration(1500);
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 20);
                }
            });
            view.setClipToOutline(true);
            toast.show();
//            new CountDownTimer(15000, 1000) {
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

    private void initRecorder(int micNumber) {
        mAudioRecordInfo = new AudioRecordInfo();
        mAudioRecordInfo.filePath = getAudioFile();
        mAudioRecordInfo.maxDuration = getAudioRecordDuration();
        mAudioRecordInfo.micNumber = micNumber;
        mAudioRecorder = new AudioRecorder(mAudioRecordInfo, this);
        mAudioRecorder.initialize(new Handler());
    }

    private String getStringFromPreference(String key) {
        return preferences.getString(key, "");
    }

    private String getAudioFile() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/ORUphones/" + audioFileName);
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, audioFileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ORUphones");
            Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            DLog.d(TAG, "New method getAudioFile: " + uri);
            try {
                OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file.getAbsolutePath();
        } else {
            File mediaStorageDir = new File(activity.getFilesDir(), folderAudioPath);
            File audioFile = null;
            try {
                if (!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();
                }
                audioFile = new File(mediaStorageDir, audioFileName);
                audioFile.createNewFile();
                return audioFile.getAbsolutePath();
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        }
        return null;
    }

    private void putStringinPreference(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(key, value);
        editor.commit();
    }

    //    a function to get single random number from 1 to 8 excluding the numbers given in the array
    private int getRandomNumber(int[] excludeNumbers) {
        int randomNumber = 0;
        boolean isNumberFound = false;
        while (!isNumberFound) {
            randomNumber = (int) (Math.random() * 8) + 1;
            isNumberFound = true;
            for (int i = 0; i < excludeNumbers.length; i++) {
                if (randomNumber == excludeNumbers[i]) {
                    isNumberFound = false;
                    break;
                }
            }
        }
        return randomNumber;
    }
//
//    //    a function to call the audio file player for 1 to 8 only
//    private void callAudioFile(int random) {
//        switch (random) {
//            case 1:
//                playAudioFile(R.raw.s1);
//                break;
//            case 2:
//                playAudioFile(R.raw.s2);
//                break;
//            case 3:
//                playAudioFile(R.raw.s3);
//                break;
//            case 4:
//                playAudioFile(R.raw.s4);
//                break;
//            case 5:
//                playAudioFile(R.raw.s5);
//                break;
//            case 6:
//                playAudioFile(R.raw.s6);
//                break;
//            case 7:
//                playAudioFile(R.raw.s7);
//                break;
//            case 8:
//                playAudioFile(R.raw.s8);
//                break;
//        }
//    }
//
//    //    function to play the audio file
//    private void playAudioFile(int audioFile) {
////        initialize the mMediaPlayer
//        MediaPlayer mMediaPlayer = MediaPlayer.create(activity, audioFile);
//
//        Uri fileUri = null;
//        fileUri = Uri.parse("android.resource://" + activity.getApplicationContext().getPackageName() + "/" + audioFile);
//        if (fileUri != null) {
//            try {
//                if (mMediaPlayer != null) {
//                    mMediaPlayer.reset();
//                    mMediaPlayer.setDataSource(activity, fileUri);
//                    mMediaPlayer.prepare();
//                    mMediaPlayer.start();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //    a function to run callAudioFile after getting the random number, using 1.5 seconds delay by runnable
//    private void playAudioFiles() {
//        excludeNumbers[0] = 0;
//        excludeNumbers[1] = 0;
//        excludeNumbers[2] = 0;
//        int random = getRandomNumber(excludeNumbers);
//        excludeNumbers[0] = random;
//        callAudioFile(random);
//
//        mTestHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int random = getRandomNumber(excludeNumbers);
//                excludeNumbers[1] = random;
//                callAudioFile(random);
//            }
//        }, 1500);
//
//        mTestHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int random = getRandomNumber(excludeNumbers);
//                excludeNumbers[2] = random;
//                callAudioFile(random);
//            }
//        }, 3000);
//
//        Log.d(TAG, "excludeNumbers[0]:" + excludeNumbers[0] + " excludeNumbers[1]:" + excludeNumbers[1] + " excludeNumbers[2]:" + excludeNumbers[2]);
//    }


    private void initAudioPlayer(String testName) {
        mAudioPlayInfo = new AudioPlayInfo();
        Uri fileUri = null;
//        playAudioFiles();
        if (Util.isAdvancedTestFlow()) {
            fileUri = Uri.parse("android.resource://" + activity.getApplicationContext().getPackageName() + "/" + R.raw.muta_go_get_ring_long);
        } else {
            fileUri = Uri.parse("android.resource://" + activity.getApplicationContext().getPackageName() + "/" + R.raw.muta_go_get_ring_short);
        }

        if (testName != null && (testName.equalsIgnoreCase(SPEAKERTEST) || testName.equalsIgnoreCase(EARPIECETEST) || testName.equalsIgnoreCase(EARPHONETEST))) {
            mAudioPlayInfo.isAudioAdvanceTest = true;
        }
        if (testName != null && (testName.equalsIgnoreCase(EARPHONETEST) || testName.equalsIgnoreCase(SPEAKERTEST) || testName.equalsIgnoreCase(MICROPHONETEST) || testName.equalsIgnoreCase(MICROPHONE2TEST))) {
            mAudioPlayInfo.enableSpeaker = true;
        } else if (testName != null && testName.equalsIgnoreCase(EARPIECETEST)) {
            mAudioPlayInfo.enableSpeaker = false;
        }

        mAudioPlayInfo.audioSource = fileUri;
        if (testName != null && (testName.equalsIgnoreCase(MICROPHONETEST) || testName.equalsIgnoreCase(MICROPHONE2TEST))) {
            mAudioPlayInfo.audioSource = Uri.parse(recordedFilePath);
        }
      /*   mAudioPlayer = new AudioPlayer(activity.getApplicationContext(), mAudioPlayInfo, this);
        audioManager = (AudioManager) activity.getSystemService(AUDIO_SERVICE);
       if (testName.equalsIgnoreCase(EARPHONETEST)) {
            initialCallVol = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            mAudioPlayer.setVolume((int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) * 0.6));
        } else {
            initialCallVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioPlayer.setAudioVolumeMode(AudioPlayer.AudioVolumeMode.MAXIMUM);AudioPlayer(activity.getApplicationContext(), mAudioPlayInfo, this);
        }*/
        mAudioPlayer = new AudioPlayer(activity.getApplicationContext(), mAudioPlayInfo, this);
        audioManager = (AudioManager) activity.getSystemService(AUDIO_SERVICE);


        //As per customer requirement, for ear-phone test volume needs to be set to 60% instead of 100%
        if (testName != null && (testName.equalsIgnoreCase(EARPHONETEST)))
//            mAudioPlayer.setVolume((int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) * 0.6));
            mAudioPlayer.setAudioVolumeMode(AudioPlayer.AudioVolumeMode.MAXIMUM);
        else
            mAudioPlayer.setAudioVolumeMode(AudioPlayer.AudioVolumeMode.MAXIMUM);

        mAudioPlayer.setHeadsetPlugStateListener(this);
        if (!earJackState) {

            mAudioPlayer.initialize(new Handler());
        }
    }


    @Override
    public void onTestStart() {
        DLog.d(TAG, "onTestStart............. " + testName);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onTestEnd(org.pervacio.onediaglib.diagtests.TestResult testResult) {
        DLog.d(TAG, "onTestEnd............." + testName + " - result " + testResult.getResultCode());
        if (manualTestDone || activity == null || activity.isFinishing())
            return;
        int resultCode = testResult.getResultCode();
        String message = "";
        if (testResult instanceof TestResult) {
            message = testResult.getResultDescription();
        }
        if (resultCode == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED) {
            postMessageToHandler(resultCode, ACCESSDENIED);
            return;
        }
        if (CALLTEST.equalsIgnoreCase(testName)) {
            //  if (CALLTEST.equalsIgnoreCase(testName)&&testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
            //Event is handling in OnCall Disconnected.
            switch (testResult.getResultCode()) {
                case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS:
                    break;
                case org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED:
                    break;
                default:
                    resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                    break;
            }
        } else if (EARPHONEJACKTEST.equalsIgnoreCase(testName) && testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED) {
            mTestHandler.removeCallbacks(timeOutRunnable);
            resultCode = 0;
        } else if (ACCELEROMETERTEST.equalsIgnoreCase(testName) && (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS)) {
            return;
        } else if (BLUETOOTHCONNECTIVITYTEST.equalsIgnoreCase(testName)) {
            mTestBluetoothResults = (TestBluetoothResults) testResult;
            String messageToPrint = "## B_CONN_TEST: CODE " + testResult.getResultCode() + " : " + mTestBluetoothResults.getResultDescription();
            DLog.d(TAG, messageToPrint);

            if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                Pair<String, String> result = mTestBluetoothResults.getBluetoothScanResults();
                mDetectedDevice = result.first;
                String deviceAddress = result.second;

                DLog.d(TAG, "## BLUETOOTHCONNECTIVITYTEST: DEVICE " + mDetectedDevice);

                message = !TextUtils.isEmpty(mDetectedDevice) ? mDetectedDevice : deviceAddress;

                AppUtils.toast(OruApplication.getAppContext().getString(R.string.devicename_toast, message));

            }
            dispatchTest();
        }
        else if (WIFICONNECTIVITYTEST.equalsIgnoreCase(testName)) {
            mTestWifiResults = (TestWifiResult) testResult;
            String messageToPrint = "## W_CONN_TEST: CODE " + testResult.getResultCode() + " : " + mTestWifiResults.getResultDescription();
            DLog.d(TAG, messageToPrint);
            if (testResult.getResultCode() == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS) {
                HashMap<String, String> scanListMap = mTestWifiResults.scanListMap;

//                iterate through the scanListMap
                for (Map.Entry<String, String> entry : scanListMap.entrySet()) {
                    String ssid = entry.getKey();
                    String bssid = entry.getValue();

                    DLog.d(TAG, "## WIFICONNECTIVITYTEST: SSID " + ssid + ", BSSID " + bssid);
                }
            }
            dispatchTest2();
        }else if (EARPIECETEST.equalsIgnoreCase(testName) || SPEAKERTEST.equalsIgnoreCase(testName) || EARPHONEJACKTEST.equalsIgnoreCase(testName) || EARPHONETEST.equalsIgnoreCase(testName)) {
            if (mAudioPlayer != null)
                mAudioPlayer.setVolume(initialCallVol);
            mTestHandler.removeCallbacks(timeOutRunnable);
            GlobalConfig.getInstance().setAudioTestNumString(testResult.getTestAdditionalInfo());
            resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED;
        }
        else if (VIBRATIONTEST.equalsIgnoreCase(testName))
        {
            GlobalConfig.getInstance().setVibrationTestNumString(testResult.getTestAdditionalInfo());
            DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) testResult.getTestAdditionalInfo() "+testResult.getTestAdditionalInfo().toString());
            if (testResult.getResultCode() == RESULT_SHOW_VIBRATION_ALERT) {
                DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) RESULT_SHOW_VIBRATION_ALERT ");

                deviceVibration = true;
            } else if (testResult.getResultCode() == RESULT_RELAUCH_APP) {
                DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) RESULT_RELAUCH_APP ");

                deviceVibration = false;
            }
            try {
                DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) try ");

                if (testVibration != null) {
                    DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) testVibration != null ");

                    testVibration.stopVibration();
                    try {
                        DLog.d(TAG, "VIBRATIONTEST.equalsIgnoreCase(testName) testVibration.setCurrentVibrationIntensity ");

                        testVibration.setCurrentVibrationIntensity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    testVibration.unRegisterVibrationTest();
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        } else if (REARCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                REARCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST6.equalsIgnoreCase(testName)) {
            try {
                //mTestCamera.stopCamera();
                TestCameraResult testCameraResult = (TestCameraResult) testResult;
                DLog.d(TAG, "onTestEnd camera case testCameraResult.getResultCode() " + testCameraResult.getResultCode() + "  testCameraResult.getPath() " + testCameraResult.getPath());
                switch (testCameraResult.getResultCode()) {
                    case TestCameraResult.RESULT_PASS:
                        String mPath = testCameraResult.getPath();
                        resultCode = testCameraResult.getResultCode();
                        deletableFiles.add(mPath);
                        message = mPath;
                        //stopTest();
                        break;

                    case TestCameraResult.RESULT_ERROR_TIME_OUT:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT;
                        break;

                    default:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                        //stopTest();
                        break;
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
                return;
            }
        } else if (CAMERAFLASHTEST.equalsIgnoreCase(testName) /*|| FRONTFLASHTEST.equalsIgnoreCase(testName)*/) {
            try {
                flashCount = 0;
                TestCameraResult testCameraResult = (TestCameraResult) testResult;
                GlobalConfig.getInstance().setFlashTestNumString(testResult.getTestAdditionalInfo());
                DLog.d("testResult.getTestAdditionalInfo() " + testResult.getTestAdditionalInfo());
                DLog.d( "FlashTest" + "Count" + flashCount);
                switch (testCameraResult.getResultCode()) {
                    case TestCameraResult.RESULT_EXCEPTION_IN_PREVIEW:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                        message = testCameraResult.getResultDescription();
                        stopTest();
                        break;
                    default:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                        stopTest();
                        break;
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
                return;
            }
        } else if (FRONTFLASHTEST.equalsIgnoreCase(testName)) {
            try {
                flashCount = 0;
                TestCameraResult testCameraResult = (TestCameraResult) testResult;
                switch (testCameraResult.getResultCode()) {
                    case TestCameraResult.RESULT_EXCEPTION_IN_PREVIEW:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                        message = testCameraResult.getResultDescription();
                        stopTest();
                        break;
                    default:
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL;
                        stopTest();
                        break;
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
                return;
            }
        } else if (MICROPHONETEST.equalsIgnoreCase(testName) || MICROPHONE2TEST.equalsIgnoreCase(testName)) {

            switch (testResult.getResultCode()) {
                case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS:
                    if (mRecordDone) {
                        mTestHandler.removeCallbacks(timeOutRunnable);
                        resultCode = ManualTestEvent.AUDIO_RECORDING_DONE;
                    } else if (isMicrophonePlayed) {
                        cleanMicrophoneState();
                        resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED;
                    }
                    if (mHeadsetJackTest != null)
                        mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
                    break;

                default:
                    break;
            }
        } else if (DEADPIXELTEST.equalsIgnoreCase(testName) || DISCOLORATIONTEST.equalsIgnoreCase(testName) ||
                SCREENBURNTEST.equalsIgnoreCase(testName)) {
            ScreenTestResults screenTestResults = (ScreenTestResults) testResult;
            message = screenTestResults.getPath();
            //deletableFiles.add(message);

        } else if (ACCELEROMETERTEST.equalsIgnoreCase(testName)) {
            if (isSensorRegistered) {
                unregisterSensorListeners();
            }
        }
        if (testName != null && !testName.isEmpty()) {
            postMessageToHandler(resultCode, message);
        }
    }


    private void registerSensorListeners() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    isSensorRegistered = true;
                    if (GUESTURETEST.equalsIgnoreCase(testName)) {
                        testAirGesture = TestAirGesture.getInstance();
                        testAirGesture.registerSensorResultListener(ManualTest.this);
                        testAirGesture.setTestFinishListener(ManualTest.this);
                        testAirGesture.RegisterEvents();
                    } else if (sensorListener != null) {
                        sensorListener.registerSensorEventListener();
                        sensorListener.registerSensorResultListener(ManualTest.this);
                        sensorListener.setTestFinishListener(ManualTest.this);
                    }
                } catch (Exception e) {
                    DLog.e(TAG, "Exception:" + e.getMessage());
                }
            }
        });
    }

    private void unregisterSensorListeners() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    isSensorRegistered = false;
                    if (GUESTURETEST.equalsIgnoreCase(testName)) {
                        testAirGesture.unRegisterEvents();
                        testAirGesture.stopGestureTest(true);
                    } else if (sensorListener != null) {
                        sensorListener.unRegisterOnSensorEventListener();
                        sensorListener.unRegisterSensorResultListener();
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }

                } catch (Exception e) {
                    DLog.e(TAG, "Exception:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onSensorEventListner(Object objResult) {
        if (ACCELEROMETERTEST.equalsIgnoreCase(testName)) {
            if (objResult instanceof Boolean) {
                mIsOrientationChanged = (boolean) objResult;
                if (mIsOrientationChanged) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    postMessageToHandler(0, TestResult.PASS);

                }
            }
        } else if (GUESTURETEST.equalsIgnoreCase(testName)) {
            String str = (String) objResult;
            Message msg = new Message();
            msg.obj = str;
            if ("right".equalsIgnoreCase(str)) {
                isright = true;
                handler.sendEmptyMessage(ManualTestEvent.GESTURE_RIGHT);
            } else if ("left".equalsIgnoreCase(str)) {
                isleft = true;
                handler.sendEmptyMessage(ManualTestEvent.GESTURE_LEFT);
            } else if ("up".equalsIgnoreCase(str)) {
                isup = true;
                handler.sendEmptyMessage(ManualTestEvent.GESTURE_UP);
            } else if ("down".equalsIgnoreCase(str)) {
                isdown = true;
                handler.sendEmptyMessage(ManualTestEvent.GESTURE_DOWN);
            }
            if (isright && isleft && isup && isdown) {
                org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
                onTestEnd(testResult);
            }
        }
    }

    public void resumeTest(String testName) {
        if (HARDKEYTEST.equalsIgnoreCase(testName) || SOFTKEYTEST.equalsIgnoreCase(testName)) {
            if (keysInstance != null) {
                DLog.d(TAG, "Calling resumetest");
                this.testName = testName;
                keysInstance.resumeTest(testName);
            }
        }
    }

    public void resumeTest() {
        if (TOUCHTEST.equalsIgnoreCase(testName) || SPENTEST.equalsIgnoreCase(testName)) {
            if (mTouchview != null)
                mTouchview.resumeTest();
        } else if (REARCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                REARCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST6.equalsIgnoreCase(testName)) {
            performCameraTest(testName, layoutFrame, handler);
        } else if (CAMERAFLASHTEST.equalsIgnoreCase(testName) || FRONTFLASHTEST.equalsIgnoreCase(testName)) {
            if (mTestFlashInstance != null) {
                performCameraTest(testName, layoutFrame, handler);
            }
        } else if (DEADPIXELTEST.equalsIgnoreCase(testName) || SCREENBURNTEST.equalsIgnoreCase(testName) ||
                DISCOLORATIONTEST.equalsIgnoreCase(testName)) {
            if (mDeadPixelTest != null) {
                mDeadPixelTest.resumeTest();
            }
        }
    }

    public void changeDisplayTestColor() {
        if (mDeadPixelTest != null) {
            mDeadPixelTest.changeScreenColor();
        }
    }

    @Override
    public void onInitialized(AudioPlayer audioPlayer) {


        if (EARPHONETEST.equalsIgnoreCase(testName)) {
            if (AudioPlayer.isHeadsetConnected(this.activity)) {
                mAudioPlayer.start();
                handler.sendEmptyMessage(ManualTestEvent.AUDIO_PLAY_STARTED);
            }
        } else if ((EARPIECETEST.equalsIgnoreCase(testName) || SPEAKERTEST.equalsIgnoreCase(testName)) || MICROPHONETEST.equalsIgnoreCase(testName) || MICROPHONE2TEST.equalsIgnoreCase(testName)) {
            if (!(AudioPlayer.isHeadsetConnected(this.activity))) {
                if (isonCall && ((EARPIECETEST.equalsIgnoreCase(testName) && !am.isSpeakerphoneOn())
                        || (SPEAKERTEST.equalsIgnoreCase(testName) && am.isSpeakerphoneOn()))) {
                    postMessageToHandler(0, TestResult.PASS);
                    return;
                }
                mAudioPlayer.start();
                handler.sendEmptyMessage(ManualTestEvent.AUDIO_PLAY_STARTED);
            }
        }


    }

    @Override
    public void onPlayStarted(AudioPlayer audioPlayer, int i) {

        DLog.i(TAG, "on playstarted");
        //   mAudioPlayer.start();
        isMicrophonePlayed = true;
        isPlayed = true;
        mRecordDone = false;
    }

    @Override
    public void onPlayPaused(AudioPlayer audioPlayer) {
        DLog.i(TAG, "on play pause");
        if (mAudioPlayer != null) {
            mAudioPlayer.pause();
        }
        isPaused = true;

    }

    @Override
    public void onPlayResumed(AudioPlayer audioPlayer) {
        DLog.i(TAG, "on play resume ");
        mAudioPlayer.resume();
    }

    @Override
    public void onPlayStopped(AudioPlayer audioPlayer) {
        DLog.i(TAG, "on play stopp ");
        if (mAudioPlayer != null)
            mAudioPlayer.stop();


    }


    @Override
    public void onHeadsetPlugStateChange(boolean headsetState) {
        mTestHandler.removeCallbacks(timeOutRunnable);
        if (headsetState) {

            switch (this.testName) {
                case SPEAKERTEST:
                    handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST);
                    mTestHandler.postDelayed(timeOutRunnable, 15000);
                    if (isPlayed) {
                        mAudioPlayer.pause();
                        isPaused = true;
                    }
                    break;

                case EARPHONETEST:
                    if (isonCall) {
                        postMessageToHandler(0, TestResult.PASS);
                    } else {
                        earJackState = false;
                        if (!isPlayed)
                            mAudioPlayer.start();
                        else if (isPaused)
                            mAudioPlayer.resume();
                    }
                    break;

                case EARPIECETEST:
                    handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST);
                    mTestHandler.postDelayed(timeOutRunnable, 15000);
                    if (isPlayed) {
                        mAudioPlayer.pause();
                        isPaused = true;
                    }
                    break;

                case EARPHONEJACKTEST:

                    passCount++;
                    pluginState = false;
                    earJackState = false;
                    handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST);
                    mTestHandler.postDelayed(timeOutRunnable, 15000);

                    break;

                case MICROPHONETEST:
                case MICROPHONE2TEST:
                    handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST);
                    mTestHandler.postDelayed(timeOutRunnable, 15000);
                    isPaused = true;
                    if (mIsRecorderInitialized)
                        mAudioRecorder.stop();
                    if (isPlayed && mAudioPlayer != null) {
                        mAudioPlayer.pause();
                    }

                    break;

                default:
                    break;
            }


        } else {


            switch (testName) {

                case EARPIECETEST:
                    if (isonCall && !am.isSpeakerphoneOn()) {
                        postMessageToHandler(0, TestResult.PASS);
                        break;
                    } else {
                        if (!isPlayed) {
                            mAudioPlayer.start();
                            isPlayed = true;
                        } else if (isPaused) {
                            mAudioPlayer.resume();
                        }
                    }
                    break;

                case EARPHONETEST:
                    if (!earJackState)
                        handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST);
                    mTestHandler.postDelayed(timeOutRunnable, 15000);
                    if (isPlayed) {
                        mAudioPlayer.pause();
                        isPaused = true;
                    }
                    break;

                case SPEAKERTEST:
                    if (isonCall && am.isSpeakerphoneOn()) {
                        postMessageToHandler(0, TestResult.PASS);
                        break;
                    } else {
                        if (!isPlayed) {
                            mAudioPlayer.start();
                            isPlayed = true;
                        } else if (isPaused) {
                            mAudioPlayer.resume();
                        }
                    }
                    break;


                case EARPHONEJACKTEST:
                    if (pluginState) {
                        mTestHandler.postDelayed(timeOutRunnable, 15000);
                        if (!earJackState)
                            handler.sendEmptyMessage(ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST);
                    } else
                        passCount++;

                    break;

                case MICROPHONETEST:
                case MICROPHONE2TEST:
                    if (isPaused && mAudioPlayer != null)
                        mAudioPlayer.resume();
                    if (isPaused && mAudioRecorder != null)
                        mAudioRecorder.start();
                    break;

                default:
                    break;
            }

        }
        if (testName != null && testName.equalsIgnoreCase(EARPHONEJACKTEST) && passCount > 1) {
            org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
            testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
            mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
            onTestEnd(testResult);
        }
    }


    @Override
    public void onHeadsetButtonEvent(KeyEvent keyEvent) {

    }


    private void clean() {
        this.testName = "";
        mTestDisplay = null;
        flashCount = 0;
        mTestDimming = null;
        testVibration = null;
        mTouchview = null;
        mCallTest = null;
        testAirGesture = null;
        mTestFlashInstance = null;
        mTestCamera = null;
        mTestVideo = null;
        isVideoRecording = false;
        mTestBluetooth = null;
        mTestWifi = null;
        isPaused = false;
        isPlayed = false;
        passCount = 0;
        earJackState = false;
        // mAudioPlayer = null;
        mAudioPlayInfo = null;
        mIsOrientationChanged = false;
        mOrientationChangeCount = 0;
        pluginState = true;
        isleft = isright = isdown = isup = false;
        if (mHeadsetJackTest != null)
            mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
        if (isSensorRegistered) {
            unregisterSensorListeners();
        }
        unregisterBatteryReceiver();
        mTestHandler.removeCallbacks(timeOutRunnable);
        if (mTestCamera != null)
            mTestCamera.stopTimer();
        if (mTestVideo != null)
            mTestVideo.stopTimer();
    }

    private void dispatchTest() {
        DLog.d(TAG, "on dispatchTest ");
        try {
            mTestBluetooth.diaspatchTest();
            // mBluetoothConnectivityTest.interrupt();

        } catch (Exception e) {
            DLog.e(TAG, "Exception:" + e.getMessage());
        }
    }
    private void dispatchTest2() {
        DLog.d(TAG, "on dispatchTest ");
        try {
            mTestWifi.diaspatchTest();
            // mBluetoothConnectivityTest.interrupt();

        } catch (Exception e) {
            DLog.e(TAG, "Exception:" + e.getMessage());
        }
    }

    private int isAllSoundsOffEnabled() {
        int isAllSoundOff = 0;

        try {
            if ("LGE".equalsIgnoreCase(Build.MANUFACTURER) || "LG".equalsIgnoreCase(Build.MANUFACTURER)) {
                if (isTurnoffAllSoundsEnabledLG(activity)) {
                    isAllSoundOff = 1;
                }
                DLog.i(TAG, "is all sound off value is" + isAllSoundOff);
            } else {
                isAllSoundOff = Settings.System.getInt(activity.getContentResolver(),
                        "all_sound_off");
            }
        } catch (Settings.SettingNotFoundException e) {

            DLog.e(TAG, "Exception:" + e.getMessage());

        }
        return isAllSoundOff;
    }

    private boolean isTurnoffAllSoundsEnabledLG(Context context) {

        try {
            String enabledServices = Settings.Secure.getString(context.getContentResolver(),
                    "enabled_accessibility_services");
            if (!TextUtils.isEmpty(enabledServices)) {
                return enabledServices
                        .contains("com.android.settingsaccessibility.turnoffallsounds.TurnOffAllSoundsService");
            }
        } catch (Exception e) {
            DLog.e(TAG, "Exception:" + e.getMessage());
        }
        return false;
    }

    @Override
    public void onInitialized(AudioRecorder audioRecorder) {
        mIsRecorderInitialized = true;
        mAudioRecorder.start();
    }

    @Override
    public void onStartRecord(AudioRecorder audioRecorder) {

        handler.sendEmptyMessage(ManualTestEvent.AUDIO_RECORDING_TOAST);
        mRecordDone = false;
    }

    @Override
    public void onStopRecord(AudioRecorder audioRecorder) {
        String path = getAudioFile();
        putStringinPreference("recordedfilepath", path);
        deletableFiles.add(path);
        mRecordDone = true;
        isMicrophonePlayed = false;
    }

    @Override
    public void onCallStarted() {

    }

    @Override
    public void onCallOffhook() {

    }

    @Override
    public void onCallDisconnected(int i) {
/*        int resultCode = 1;
        if (i > 0) {
            resultCode = 0;
        }
        if(i == -1)
            resultCode = org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED;
        postMessageToHandler(resultCode,"");*/
    }



/*    private Thread mBluetoothConnectivityTest = new Thread() {
        @Override
        public void run() {
            try {
                mTestBluetooth = new TestBluetooth();
                mTestBluetooth.scanTest(ManualTest.this);
            } catch (Exception e) {
                Log.d(TAG, "Exception:" + e.getMessage());
            }
        }
    };*/

    private Runnable cameraFlashOn = new Runnable() {
        @Override
        public void run() {
            if (mTestFlashInstance != null)
                mTestFlashInstance.turnOnFlash();
//            mTestHandler.postDelayed(cameraFlashOff, 5000);
        }
    };

    private Runnable cameraFlashOff = new Runnable() {
        @Override
        public void run() {
            if (mTestFlashInstance != null)
                mTestFlashInstance.turnOffFlash();
            stopTest();
            postMessageToHandler(8, USERINPUT);

        }
    };

    Runnable cameraRunnable = new Runnable() {
        @Override
        public void run() {
            mTestHandler.removeCallbacks(cameraRunnable);

            if (    //REARCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                    REARCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                            REARCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                            REARCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                            REARCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                            REARCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                            REARCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                            //FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                            FRONTCAMERAPICTURETEST6.equalsIgnoreCase(testName)) {
                if (null != mTestCamera) {
                    mTestCamera.capture();
/*                    if (null != mTestCamera.diagTimer)
                        mTestCamera.diagTimer.stopTimer();*/
                }
            } else if (REARCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                    REARCAMERAVIDEOTEST6.equalsIgnoreCase(testName) ||

                    FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                    FRONTCAMERAVIDEOTEST6.equalsIgnoreCase(testName)) {
                if (null != mTestVideo) {
                    if (isVideoRecording) {
                        mTestVideo.stopRecordingVideo(); //mTestVideo.stopCamera();
                    } else {
                        isVideoRecording = true;
                        mTestVideo.startVideoRecording();
                        //if (null != mTestVideo.diagTimer)
                        mTestVideo.stopTimer();
                    }

                }
            } else if (REARCAMERAPICTURETEST.equalsIgnoreCase(testName) || FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName)) {
                semiAutoTestCameraPicture.capture();
            }
        }
    };



    public void stopTestTimer(String test){
        if (test.equals("D")){
            if (mDeadPixelTest!=null){
                mDeadPixelTest.stopTimer();
            }
        } else if (test.equals("T")) {
            mTouchview = new TouchTest(TouchTest.TYPE_TOUCH_TEST_FINGER, TouchTest.DOCOMO_FULL_SCREEN_PATTERN, 7, 15, true);

            mTouchview.stopCountTimer();
        }
    }

    public void stopTest() {

        mTestHandler.removeCallbacks(timeOutRunnable);
        if (REARCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAPICTURETEST6.equalsIgnoreCase(testName)) {
            mTestHandler.removeCallbacks(cameraRunnable);
/*            if (null != mTestCamera) {
               mTestCamera.stopCamera();
               mTestCamera.diagTimer.stopTimer();
            }*/
        } else if (REARCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST6.equalsIgnoreCase(testName)) {
            mTestHandler.removeCallbacks(cameraRunnable);
            if (null != mTestVideo) {
                mTestVideo.stopCamera();
                mTestVideo.stopTimer();
            }
        } else if (AMBIENTTEST.equalsIgnoreCase(testName) || PROXIMITYTEST.equalsIgnoreCase(testName)) {
            if (isSensorRegistered) {
                unregisterSensorListeners();
            }
        } else if (CAMERAFLASHTEST.equalsIgnoreCase(testName) || FRONTFLASHTEST.equalsIgnoreCase(testName)) {
            if (null != mTestFlashInstance) {
                mTestFlashInstance.turnOffFlash();
                mTestFlashInstance.release();
                mTestFlashInstance.setTestListener(null);
                mTestHandler.removeCallbacks(cameraFlashOn);
                mTestHandler.removeCallbacks(cameraFlashOff);
            } else if (HARDKEYTEST.equalsIgnoreCase(testName) || SOFTKEYTEST.equalsIgnoreCase(testName)) {
                if (keysInstance != null) {
                    keysInstance.stopTest();
                }
            } else if (GUESTURETEST.equalsIgnoreCase(testName)) {
                if (null != testAirGesture) {
                    unregisterSensorListeners();
                }
            }
        } else if (EARPHONETEST.equalsIgnoreCase(testName) || EARPIECETEST.equalsIgnoreCase(testName) || SPEAKERTEST.equalsIgnoreCase(testName)) {

            if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {

                if (isPlayed && !timeout) {
                    mAudioPlayer.pause();
                }
                timeout = false;
            }
            clean();
        } else if (MICROPHONETEST.equalsIgnoreCase(testName) || MICROPHONE2TEST.equalsIgnoreCase(testName)) {
            if (mHeadsetJackTest != null)
                mHeadsetJackTest.unregisterHeadsetStateEventReceiver();
            if (mIsRecorderInitialized && mAudioRecorder != null) {
                mAudioRecorder.stop();
            }
            if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
                if (isPlayed && mIsRecorderInitialized) {
                    mAudioPlayer.pause();
                }
            }
            clean();
        } else if (EARPHONEJACKTEST.equalsIgnoreCase(testName)) {
            clean();
        } else if (CHARGINGTEST.equalsIgnoreCase(testName)) {
            unregisterBatteryReceiver();
        } else if (VIBRATIONTEST.equalsIgnoreCase(testName)) {
            try {
                if (testVibration != null) {
                    testVibration.stopVibration();
                    try {
                        testVibration.setCurrentVibrationIntensity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    testVibration.unRegisterVibrationTest();
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        } else if (BLUETOOTHCONNECTIVITYTEST.equalsIgnoreCase(testName)) {
            dispatchTest();
        }
        else if (WIFICONNECTIVITYTEST.equalsIgnoreCase(testName)) {
            dispatchTest2();
        }else if (USBTEST.equalsIgnoreCase(testName)){
            mTestUSB.stopUSBConnectionTest();
        } else if(SCREENBURNTEST.equals(testName) || DEADPIXELTEST.equals(testName) || DISCOLORATIONTEST.equals(testName)){
            DLog.d(TAG, "DisplayTest StopTest called");
            mDeadPixelTest.stopTimer();
        } else if (TOUCHTEST.equalsIgnoreCase(testName)) {
            mTouchview.stopCountTimer();
        }

    }


    private BroadcastReceiver batteryChangedReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            afUsbConnection = new AFUsbConnection();
            int chargePlug = intent.getIntExtra(
                    BatteryManager.EXTRA_PLUGGED, -1);
            boolean isChargingFromACSource = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
            final int invalidCharger = intent.getIntExtra(
                    "invalid_charger", 0);
            if (invalidCharger == 0 && isChargingFromACSource && (afUsbConnection.isUSBConnected() == false)) {
                DLog.i(TAG, "batteryChangedReciever isCharging true isChargerConnected_ true");
                Toast.makeText(activity, activity.getResources().getString(R.string.device_charging), Toast.LENGTH_SHORT).show();
                org.pervacio.onediaglib.diagtests.TestResult testResult = new org.pervacio.onediaglib.diagtests.TestResult();
                testResult.setResultCode(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
                unregisterBatteryReceiver();
                onTestEnd(testResult);
            }
        }
    };

    private void unregisterBatteryReceiver() {
        if (batteryChangedReciever != null) {
            try {
                activity.unregisterReceiver(batteryChangedReciever);
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        }
        mTestHandler.removeCallbacks(timeOutRunnable);
    }

    private BroadcastReceiver chargerConnectedReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            afUsbConnection = new AFUsbConnection();
            String action = intent.getAction();
            DLog.i(TAG, "chargerConnectedReciever onReceive action " + action);
            if (action != null && action.equalsIgnoreCase(ACTION_POWER_CONNECTED) && (afUsbConnection.isUSBConnected() == false)) {
                isChargerConnected_ = true;
                DLog.i(TAG, "chargerConnectedReciever onReceive ACTION_POWER_CONNECTED isChargerConnected_ " + isChargerConnected_);
            } else if (action != null && action.equalsIgnoreCase(ACTION_POWER_DISCONNECTED)) {
                isChargerConnected_ = false;
                DLog.i(TAG, "chargerConnectedReciever onReceive ACTION_POWER_DISCONNECTED isChargerConnected_ " + isChargerConnected_);
            }

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ORUPERMISSIONCODES.BLUETOOTH_STATE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth was enabled successfully
                DLog.d("Bluetooth Test","PASS");
                postMessageToHandler(0, TestResult.PASS);
            } else {
                Toast.makeText(activity, R.string.failed_blutooth_test, Toast.LENGTH_SHORT).show();
                // User declined to enable Bluetooth
                DLog.d("Bluetooth Test","Fail");
                postMessageToHandler(0, TestResult.FAIL);
            }
        }
        if (requestCode == ORUPERMISSIONCODES.WIFI_STATE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                // Wifi was enabled successfully
                postMessageToHandler(0, TestResult.PASS);
            } else {
                Toast.makeText(activity, R.string.failed_blutooth_test, Toast.LENGTH_SHORT).show();
                // User declined to enable Wifi
                postMessageToHandler(0, TestResult.FAIL);
            }
        }
    }

    private void unregisterChargerConnectedReceiver() {
        if (chargerConnectedReciever != null) {
            try {
                activity.unregisterReceiver(chargerConnectedReciever);
                isChargerConnected_ = false;
                DLog.i(TAG, "unregisterChargerConnectedReceiver");
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        }
    }

    private boolean isChargerConnected() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = activity.registerReceiver(null, ifilter);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
        return usbCharge;
    }


    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            mTestHandler.removeCallbacks(timeOutRunnable);
            if (!BaseActivity.isAssistedApp) {
                timeout = true;
                postMessageToHandler(org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT, "TIMEDOUT");
            }
        }
    };

    public void finishTest(int resultCode, String message) {
        postMessageToHandler(resultCode, message);
    }

    public void updateKeysTestResults(int resultCode, String message) {
        DLog.d(TAG, "resultCode:" + resultCode + "message:" + message);
        postMessageToHandler(resultCode, message);
    }


    public void updateKeysTestResults(Bundle bundle) {
        if (handler != null) {
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public boolean checkIfAlreadyAttempted(String testName) {
        boolean alreadyAttempted = false;
        if (TestName.MICROPHONETEST.equalsIgnoreCase(testName) || TestName.MICROPHONE2TEST.equalsIgnoreCase(testName)) {
            alreadyAttempted = reperformedMicTest;
        }
        return alreadyAttempted;
    }

    public boolean checkIfAlreadyAttempted(String testName, int resultCode) {
        return lastPerformedTest != null && testName != null && testName.equalsIgnoreCase(lastPerformedTest.getTestName()) && resultCode == lastPerformedTest.getResultCode()
                && !Util.isAdvancedTestFlow();
    }

    public void setLastPerformedTest(String testName, int resultCode) {
        if (lastPerformedTest == null)
            lastPerformedTest = new org.pervacio.onediaglib.diagtests.TestResult();
        lastPerformedTest.setTestName(testName);
        lastPerformedTest.setResultCode(resultCode);
    }

    private void postMessageToHandler(int resultCode, String message) {
        if (resultCode == org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT) {
            if (checkIfAlreadyAttempted(testName, resultCode)) {
                resultCode = 1;
            }
        }
        setLastPerformedTest(testName, resultCode);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = resultCode;
        bundle.putString("result", TestUtil.getTestResult(resultCode));
        bundle.putString("message", message);
        msg.setData(bundle);
        if (handler != null)
            handler.sendMessage(msg);
        if (
//                REARCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                REARCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                        REARCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                        REARCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                        REARCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                        REARCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                        REARCAMERAPICTURETEST6.equalsIgnoreCase(testName) ||

//                FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST1.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST2.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST3.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST4.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST5.equalsIgnoreCase(testName) ||
                        FRONTCAMERAPICTURETEST6.equalsIgnoreCase(testName)) {
            try {
                mTestCamera.stopCamera();
            } catch (Exception e) {
                DLog.e(TAG, "Exception stopCamera: ");
            }
        }

        if (REARCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                REARCAMERAVIDEOTEST6.equalsIgnoreCase(testName) ||

                FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST1.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST2.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST3.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST4.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST5.equalsIgnoreCase(testName) ||
                FRONTCAMERAVIDEOTEST6.equalsIgnoreCase(testName)) {
            try {
                mTestVideo.stopCamera();
            } catch (Exception e) {
                DLog.e(TAG, "Exception stopCamera: ");
            }
        }
        if (isTestDone(resultCode)) {
            clean();
            cleanMicrophoneState();
        }
    }

    private boolean isTestDone(int resultCode) {
        if (resultCode == org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED
                || resultCode == org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS || resultCode == org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL) {
            return true;
        }
        return false;
    }

    public void manualTestDone() {
        //manualTest=null;
        manualTestDone = true;
        lastPerformedTest = null;
        clean();
        deleteFiles();
    }

    public boolean permissionCheck(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            try {
                String[] children = dir.list();
                if (children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    private void deleteFiles() {
        for (String filepath : deletableFiles) {
            if (filepath != null) {
                File file = new File(filepath).getAbsoluteFile();
                if (file != null)
                    deleteDir(file);
            }
        }
    }


    private void changeVibrationIntensity() {

        DLog.d(TAG, "performTest in changeVibrationIntensity : "+testName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity.getApplicationContext()) && isDeviceVibrationZero()) {
                DLog.d(TAG, "performTest in changeVibrationIntensity !Settings.System.canWrite(activity.getApplicationContext()) && isDeviceVibrationZero(): "+testName);

                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return;
            } else {
                DLog.d(TAG, "performTest in changeVibrationIntensity ELSE !Settings.System.canWrite(activity.getApplicationContext()) && isDeviceVibrationZero(): "+testName);

                testVibration.getCurrentVibrationIntensity();
                testVibration.setMaxVibrationIntensity();
            }
        } else {
            DLog.d(TAG, "performTest in changeVibrationIntensity ELSE  "+testName);

            testVibration.getCurrentVibrationIntensity();
            testVibration.setMaxVibrationIntensity();
        }
        return;
    }

    private boolean isDeviceVibrationZero() {
        boolean status = false;
        if ("samsung".equalsIgnoreCase(Build.MANUFACTURER) && ((!"SM-J500F".equalsIgnoreCase(Build.MODEL)) && (!"SM-A710F".equalsIgnoreCase(Build.MODEL)))) {
            int mDefaultVIBNOTIFICATIONMAGNITUDE = 2;
            int mDefaultVIBFEEDBACKMAGNITUDE = 0;
            int mDefaultVIBRECVCALLMAGNITUDE = 0;
            try {
                mDefaultVIBNOTIFICATIONMAGNITUDE = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), "VIB_NOTIFICATION_MAGNITUDE");
                mDefaultVIBFEEDBACKMAGNITUDE = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), "VIB_FEEDBACK_MAGNITUDE");
                mDefaultVIBRECVCALLMAGNITUDE = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), "VIB_RECVCALL_MAGNITUDE");
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
                return false;

            }
            DLog.e(TAG, "VIB_NOTIFICATION_MAGNITUDE ---> " + mDefaultVIBNOTIFICATIONMAGNITUDE);
            if (mDefaultVIBNOTIFICATIONMAGNITUDE != 0)
                status = false;
            else
                status = true;
        }
        return status;
    }

    public void cleanMicrophoneState() {
        isMicrophonePlayed = false;
        mRecordDone = false;
        mAudioPlayer = null;
        mIsRecorderInitialized = false;
        mTestHandler.removeCallbacks(timeOutRunnable);
    }

    public void callGrantPermissionDenied(String testName) {
        switch (testName) {
            case FRONTFLASHTEST:
                mTestFlashInstance = new TestFlash(layoutFrame);
                mTestFlashInstance.setTestListener(this);
                mTestFlashInstance.setFrontFlashTest(true);
                mTestFlashInstance.init();
                mTestFlashInstance.setFlashAdvanceTest(true);
                mTestHandler.post(cameraFlashOn);
                break;
            case CAMERAFLASHTEST:
                mTestFlashInstance = new TestFlash(layoutFrame);
                mTestFlashInstance.setTestListener(this);
                mTestFlashInstance.init();
                mTestFlashInstance.setFlashAdvanceTest(true);
                mTestHandler.post(cameraFlashOn);
                break;
//            case REARCAMERAPICTURETEST:
            case REARCAMERAPICTURETEST1:
            case REARCAMERAPICTURETEST2:
            case REARCAMERAPICTURETEST3:
            case REARCAMERAPICTURETEST4:
            case REARCAMERAPICTURETEST5:
            case REARCAMERAPICTURETEST6:
                mTestCamera = CameraUtil.getTestCameraPicture();
                mTestCamera.setSaveToFile(true);
                mTestCamera.setTestListener(this);
                mTestCamera.stopTimer();
                mTestCamera.startCamera(CameraUtil.FACING_REAR, CameraUtil.getCameraID(TestCameraPicture.FACING_REAR, camNum.get(testName)), layoutFrame);
                DLog.d(TAG, "Cam NUm= " + camNum.get(testName) + " Camera ID= " + CameraUtil.getCameraID(TestCameraPicture.FACING_REAR, camNum.get(testName)));
                Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.picture_toast), Toast.LENGTH_SHORT).show();
                break;
            case REARCAMERAPICTURETEST:
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    semiAutoTestCameraPicture = new SemiAutoTestCameraPicture();
                }*/
                semiAutoTestCameraPicture = new SemiAutoTestCameraPicture();
                semiAutoTestCameraPicture.setSaveToFile(true);
                semiAutoTestCameraPicture.setTestFinishListener(this);
                semiAutoTestCameraPicture.startCamera(CameraUtil.FACING_REAR, layoutFrame);
            //    Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.picture_toast), Toast.LENGTH_SHORT).show();
                break;
//            case FRONTCAMERAPICTURETEST:
            case FRONTCAMERAPICTURETEST1:
            case FRONTCAMERAPICTURETEST2:
            case FRONTCAMERAPICTURETEST3:
            case FRONTCAMERAPICTURETEST4:
            case FRONTCAMERAPICTURETEST5:
            case FRONTCAMERAPICTURETEST6:
                mTestCamera = CameraUtil.getTestCameraPicture();
                mTestCamera.setSaveToFile(true);
                mTestCamera.setTestListener(this);
                mTestCamera.stopTimer();
                mTestCamera.startCamera(CameraUtil.FACING_FRONT, CameraUtil.getCameraID(CameraUtil.FACING_FRONT, camNum.get(testName)), layoutFrame);
             //   Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.picture_toast), Toast.LENGTH_SHORT).show();
                break;

            case FRONTCAMERAPICTURETEST:
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    semiAutoTestCameraPicture = new SemiAutoTestCameraPicture();
                }*/
                semiAutoTestCameraPicture = new SemiAutoTestCameraPicture();
                semiAutoTestCameraPicture.setSaveToFile(true);
                semiAutoTestCameraPicture.setTestFinishListener(this);
                semiAutoTestCameraPicture.startCamera(CameraUtil.FACING_FRONT, layoutFrame);
               // Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.picture_toast), Toast.LENGTH_SHORT).show();
                break;
            case REARCAMERAVIDEOTEST:
            case REARCAMERAVIDEOTEST1:
            case REARCAMERAVIDEOTEST2:
            case REARCAMERAVIDEOTEST3:
            case REARCAMERAVIDEOTEST4:
            case REARCAMERAVIDEOTEST5:
            case REARCAMERAVIDEOTEST6:
                mTestVideo = CameraUtil.getTestCameraVideo();
                mTestVideo.setVideoDuration(5000);
                mTestVideo.setTestListener(this);
                mTestVideo.stopTimer();
                //mTestVideo.startCam(TestCameraPicture.FACING_REAR, TestCameraPicture.getCameraID(TestCameraPicture.FACING_REAR,camNum.get(testName)), layoutFrame);
                mTestVideo.startCam(CameraUtil.FACING_REAR, CameraUtil.getCameraID(CameraUtil.FACING_REAR, camNum.get(testName)), layoutFrame);
//                Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.record_toast), Toast.LENGTH_SHORT).show();
                break;
            case FRONTCAMERAVIDEOTEST:
            case FRONTCAMERAVIDEOTEST1:
            case FRONTCAMERAVIDEOTEST2:
            case FRONTCAMERAVIDEOTEST3:
            case FRONTCAMERAVIDEOTEST4:
            case FRONTCAMERAVIDEOTEST5:
            case FRONTCAMERAVIDEOTEST6:
                mTestVideo = CameraUtil.getTestCameraVideo();
                mTestVideo.setVideoDuration(5000);
                mTestVideo.setTestListener(this);
                mTestVideo.stopTimer();
                //mTestVideo.startCam(TestCameraPicture.FACING_FRONT, TestCameraPicture.getCameraID(TestCameraPicture.FACING_FRONT,camNum.get(testName)),  layoutFrame);
                mTestVideo.startCam(CameraUtil.FACING_FRONT, CameraUtil.getCameraID(CameraUtil.FACING_FRONT, camNum.get(testName)), layoutFrame);
//                Toast.makeText(OruApplication.getAppContext(), activity.getResources().getString(R.string.record_toast), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public class myPhoneStateChangeListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            //Log.d(TAG, "Call State Listener --  "+state);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isonCall = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isonCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    isonCall = false;
                    break;
                default:
                    break;
            }
        }
    }

    public void stopAudio() {
        if (mAudioPlayer != null)
            mAudioPlayer.stop();

    }

    public void stopVibration() {
        if (testVibration != null) {
            testVibration.stopVibration();
        }
        try {
            testVibration.setCurrentVibrationIntensity();
            testVibration.unRegisterVibrationTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testName != null && !testName.isEmpty()) {
            postMessageToHandler(org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL, USERINPUT);
        }

    }

    public void stopFlashTest() {
        stopTest();
        postMessageToHandler(8, USERINPUT);
    }

    private int getAudioRecordDuration() {
        if (BuildConfig.FLAVOR_flav.equalsIgnoreCase("tf_germany") ||
                GlobalConfig.getInstance().getCompanyName() != null && GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaGermany")) {
            return 5 * 1000;
        }
        return 10 * 1000;
    }

    public void setMicReperformed(boolean performed) {
        reperformedMicTest = performed;
    }
}

