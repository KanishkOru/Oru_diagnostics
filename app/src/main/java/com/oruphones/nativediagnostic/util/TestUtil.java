package com.oruphones.nativediagnostic.util;

import android.content.Context;
import android.view.View;


import com.oruphones.nativediagnostic.EndingSessionActivity;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.home.HomeActivity;
import com.oruphones.nativediagnostic.manualtests.AccelerometerTest;
import com.oruphones.nativediagnostic.manualtests.AmbientLightTestActivity;
import com.oruphones.nativediagnostic.manualtests.BatteryPerformanceTestActivity;
import com.oruphones.nativediagnostic.manualtests.BluetoothConnectivity;
import com.oruphones.nativediagnostic.manualtests.BluetoothTestActivity;
import com.oruphones.nativediagnostic.manualtests.CallTestActivity;
import com.oruphones.nativediagnostic.manualtests.CameraFlashTestActivity;
import com.oruphones.nativediagnostic.manualtests.CameraPictureTestActivity;
import com.oruphones.nativediagnostic.manualtests.CameraVideoTestActivity;
import com.oruphones.nativediagnostic.manualtests.DeviceChargingTestActivity;
import com.oruphones.nativediagnostic.manualtests.DimmingTestActivity;
import com.oruphones.nativediagnostic.manualtests.DisplayTestActivity;
import com.oruphones.nativediagnostic.manualtests.EarPhoneJackTestActivity;
import com.oruphones.nativediagnostic.manualtests.EarphoneTestActivity;
import com.oruphones.nativediagnostic.manualtests.EarpieceTestActivity;
import com.oruphones.nativediagnostic.manualtests.FingerPrintTestActivity;
import com.oruphones.nativediagnostic.manualtests.GPSManualTestActivity;
import com.oruphones.nativediagnostic.manualtests.GestureTest;
import com.oruphones.nativediagnostic.manualtests.KeysTestActivity;
import com.oruphones.nativediagnostic.manualtests.ManualTestsActivity;
import com.oruphones.nativediagnostic.manualtests.ManualTestsTryActivity;
import com.oruphones.nativediagnostic.manualtests.MicroPhoneTestActivity;
import com.oruphones.nativediagnostic.manualtests.NfcTestActivity;
import com.oruphones.nativediagnostic.manualtests.ProximitySensorTestActivity;
import com.oruphones.nativediagnostic.manualtests.SpeakerTestActivity;
import com.oruphones.nativediagnostic.manualtests.TouchTestActivity;
import com.oruphones.nativediagnostic.manualtests.USBConnectionTestActivity;
import com.oruphones.nativediagnostic.manualtests.VibrationTestActivity;
import com.oruphones.nativediagnostic.manualtests.WifiManualTestActivity;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.resolutions.AppResolutionsActivity;
import com.oruphones.nativediagnostic.resolutions.BluetoothResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.BrightnessResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.GpsResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.LivewallpaperResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.NfcResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.ResolutionsEducationalActivity;
import com.oruphones.nativediagnostic.resolutions.ScreenTimeOutResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.StorageResolutionsActivity;
import com.oruphones.nativediagnostic.resolutions.WifiResolutionActivity;
import com.oruphones.nativediagnostic.result.ResultsSummeryActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class TestUtil implements TestResult , TestName {

    public static HashMap<String, String[]> screen = new HashMap<String, String[]>();
    public static HashMap<String, Integer> camNum = new HashMap<String, Integer>();

    static {

//        screen.put("DEVICE_INFO", new String[] {DeviceInformationChildActivity.class.getName(), ""});
        screen.put("START_DIAGNOSTICS", new String[] {HomeActivity.class.getName(), "0"});
        screen.put("ISSUE_SELECTION", new String[] {HomeActivity.class.getName(), "1"});
        screen.put("HISTORY", new String[] {HomeActivity.class.getName(), "2"});
        screen.put("MANUAL_TESTS", new String[] {ManualTestsActivity.class.getName(), ""});
        screen.put("CMD_LAUNCH_MANUAL_TEST", new String[] {ManualTestsTryActivity.class.getName(), ""});

        //Manual Test mapping add here
        screen.put("AccelerometerTest", new String[] {AccelerometerTest.class.getName(), TestName.ACCELEROMETERTEST});
        screen.put("GPSManualTest", new String[] {GPSManualTestActivity.class.getName(), TestName.GPSMANUALTEST});
        screen.put("WifiManualTest", new String[]{WifiManualTestActivity.class.getName(), TestName.WIFICONNECTIVITYTEST});
        screen.put("BluetoothManualTest", new String[] {BluetoothConnectivity.class.getName(), TestName.BLUETOOTHCONNECTIVITYTEST});
        screen.put("CallTest", new String[] {CallTestActivity.class.getName(), TestName.CALLTEST});
        screen.put("FrontFlashTest", new String[] {CameraFlashTestActivity.class.getName(), TestName.FRONTFLASHTEST});
        screen.put("CameraFlashTest", new String[] {CameraFlashTestActivity.class.getName(), TestName.CAMERAFLASHTEST});
        screen.put("DimmingTest", new String[] {DimmingTestActivity.class.getName(), TestName.DIMMINGTEST});
        screen.put("EarphoneTest", new String[] {EarphoneTestActivity.class.getName(), TestName.EARPHONETEST});

        screen.put("FrontCameraPictureTest", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST});
        screen.put("FrontCameraPictureTest1", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST1});
        screen.put("FrontCameraPictureTest2", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST2});
        screen.put("FrontCameraPictureTest3", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST3});
        screen.put("FrontCameraPictureTest4", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST4});
        screen.put("FrontCameraPictureTest5", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST5});
        screen.put("FrontCameraPictureTest6", new String[] {CameraPictureTestActivity.class.getName(), TestName.FRONTCAMERAPICTURETEST6});

        screen.put("FrontCameraVideoTest", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST});
        screen.put("FrontCameraVideoTest1", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST1});
        screen.put("FrontCameraVideoTest2", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST2});
        screen.put("FrontCameraVideoTest3", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST3});
        screen.put("FrontCameraVideoTest4", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST4});
        screen.put("FrontCameraVideoTest5", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST5});
        screen.put("FrontCameraVideoTest6", new String[] {CameraVideoTestActivity.class.getName(), TestName.FRONTCAMERAVIDEOTEST6});

        screen.put("HardKeysTest", new String[] {KeysTestActivity.class.getName(), TestName.HARDKEYTEST});
        screen.put("LightSensorTest", new String[] {AmbientLightTestActivity.class.getName(), TestName.AMBIENTTEST});
        screen.put("MicTest", new String[] {MicroPhoneTestActivity.class.getName(), TestName.MICROPHONETEST});
        screen.put("Mic2Test", new String[] {MicroPhoneTestActivity.class.getName(), TestName.MICROPHONE2TEST});
        screen.put("ProximityTest", new String[] {ProximitySensorTestActivity.class.getName(), TestName.PROXIMITYTEST});

        screen.put("RearCameraPictureTest", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST});
        screen.put("RearCameraPictureTest1", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST1});
        screen.put("RearCameraPictureTest2", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST2});
        screen.put("RearCameraPictureTest3", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST3});
        screen.put("RearCameraPictureTest4", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST4});
        screen.put("RearCameraPictureTest5", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST5});
        screen.put("RearCameraPictureTest6", new String[] {CameraPictureTestActivity.class.getName(), TestName.REARCAMERAPICTURETEST6});

        screen.put("RearCameraVideoTest", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST});
        screen.put("RearCameraVideoTest1", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST1});
        screen.put("RearCameraVideoTest2", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST2});
        screen.put("RearCameraVideoTest3", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST3});
        screen.put("RearCameraVideoTest4", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST4});
        screen.put("RearCameraVideoTest5", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST5});
        screen.put("RearCameraVideoTest6", new String[] {CameraVideoTestActivity.class.getName(), TestName.REARCAMERAVIDEOTEST6});

        screen.put("ReceiverTest", new String[] {EarpieceTestActivity.class.getName(), TestName.EARPIECETEST});
        screen.put("SpeakerTest", new String[] {SpeakerTestActivity.class.getName(), TestName.SPEAKERTEST});
        screen.put("TouchTest", new String[] {TouchTestActivity.class.getName(), TestName.TOUCHTEST});
        screen.put("VibrationTest", new String[] {VibrationTestActivity.class.getName(), TestName.VIBRATIONTEST});
        screen.put("EarphoneJackTest", new String[] {EarPhoneJackTestActivity.class.getName(), TestName.EARPHONEJACKTEST});
        screen.put("EarpieceTest", new String[] {EarpieceTestActivity.class.getName(), TestName.EARPIECETEST});
        screen.put("LCDTest", new String[] {DisplayTestActivity.class.getName(),TestName.DISPLAYTEST });
        screen.put("SoftKeysTest", new String[] {KeysTestActivity.class.getName(), TestName.SOFTKEYTEST});
        screen.put("SPenTest", new String[] {TouchTestActivity.class.getName(), TestName.SPENTEST});
        screen.put("GestureTest", new String[] {GestureTest.class.getName(), TestName.GUESTURETEST});
        screen.put("USBManualConnectionTest", new String[] {USBConnectionTestActivity.class.getName(), TestName.USBTEST});
        screen.put("WallChargingTest", new String[] {DeviceChargingTestActivity.class.getName(), TestName.CHARGINGTEST});
        screen.put(TestName.FINGERPRINTSENSORTEST, new String[] {FingerPrintTestActivity.class.getName(), TestName.FINGERPRINTSENSORTEST});
        screen.put(TestName.BLUETOOTH_TOGGLE, new String[] {BluetoothTestActivity.class.getName(), TestName.BLUETOOTH_TOGGLE});
        screen.put("TESTS_RESULT",new String[]{ResultsSummeryActivity.class.getName(),""});

        //Resolution Test add here
        screen.put("BLUETOOTH_ON",new String[]{BluetoothResolutionActivity.class.getName(), ResolutionName.BLUETOOTH_ON});
        screen.put("BLUETOOTH_OFF",new String[]{BluetoothResolutionActivity.class.getName(),ResolutionName.BLUETOOTH_OFF});
        screen.put("NFC_ON",new String[]{NfcResolutionActivity.class.getName(),ResolutionName.NFC_ON});
        screen.put("NFC_OFF",new String[]{NfcResolutionActivity.class.getName(),ResolutionName.NFC_OFF});
        screen.put("GPS_ON",new String[]{GpsResolutionActivity.class.getName(),ResolutionName.GPS_ON});
        screen.put("GPS_OFF",new String[]{GpsResolutionActivity.class.getName(),ResolutionName.GPS_OFF});
        screen.put("WLAN_ON",new String[]{WifiResolutionActivity.class.getName(),ResolutionName.WIFI_ON});
        screen.put("SCREEN_TIMEOUT",new String[]{ScreenTimeOutResolutionActivity.class.getName(),ResolutionName.SCREEN_TIMEOUT});
        screen.put("SCREEN_BRIGHTNESS",new String[]{BrightnessResolutionActivity.class.getName(),ResolutionName.BRIGHTNESS});
        screen.put("LIVE_WALLPAPER",new String[]{LivewallpaperResolutionActivity.class.getName(),ResolutionName.LIVEWALLPAPER});
        screen.put("ADWARE_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.ADWAREAPPS});
        screen.put("MALWARE_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.MALWAREAPPS});
        screen.put("RISKY_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.RISKYAPPS});
        screen.put("OUTDATED_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.OUTDATEDAPPS});
        screen.put("UNUSED_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.UNUSEDAPPS});
        screen.put("RESOLUTION_BACKGROUND_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.BACKGROUND_APPS});
        screen.put("RESOLUTION_FOREGROUND_APPS",new String[]{AppResolutionsActivity.class.getName(),ResolutionName.FOREGROUND_APPS});
        screen.put("VIDEOS",new String[]{StorageResolutionsActivity.class.getName(), PDStorageFileInfo.FILE_TYPE_VIDEO});
        screen.put("AUDIOS",new String[]{StorageResolutionsActivity.class.getName(),PDStorageFileInfo.FILE_TYPE_AUDIO});
        screen.put("IMAGES",new String[]{StorageResolutionsActivity.class.getName(),PDStorageFileInfo.FILE_TYPE_IMAGE});
        screen.put("DUPLICATE_FILES",new String[]{StorageResolutionsActivity.class.getName(),ResolutionName.DUPLICATE});
        screen.put("FIRMWARE",new String[]{ResolutionsEducationalActivity.class.getName(),ResolutionName.FIRMWARE});
        screen.put("LAST_RESTART",new String[]{ResolutionsEducationalActivity.class.getName(), ResolutionName.LASTRESTART});
        screen.put("TEMP_FILES",new String[]{StorageResolutionsActivity.class.getName(),""});
        screen.put("END_SESSION",new String[]{EndingSessionActivity.class.getName(),""});
//        screen.put("EMAIL_SUMMARY",new String[]{EmailSummaryChildUnusedActivity.class.getName(),""});

        camNum.put(TestName.REARCAMERAPICTURETEST, 1);
        camNum.put(TestName.REARCAMERAPICTURETEST1, 1);
        camNum.put(TestName.REARCAMERAPICTURETEST2, 2);
        camNum.put(TestName.REARCAMERAPICTURETEST3, 3);
        camNum.put(TestName.REARCAMERAPICTURETEST4, 4);
        camNum.put(TestName.REARCAMERAPICTURETEST5, 5);
        camNum.put(TestName.REARCAMERAPICTURETEST6, 6);

        camNum.put(TestName.REARCAMERAVIDEOTEST, 1);
        camNum.put(TestName.REARCAMERAVIDEOTEST1, 1);
        camNum.put(TestName.REARCAMERAVIDEOTEST2, 2);
        camNum.put(TestName.REARCAMERAVIDEOTEST3, 3);
        camNum.put(TestName.REARCAMERAVIDEOTEST4, 4);
        camNum.put(TestName.REARCAMERAVIDEOTEST5, 5);
        camNum.put(TestName.REARCAMERAVIDEOTEST6, 6);

        camNum.put(TestName.FRONTCAMERAPICTURETEST, 1);
        camNum.put(TestName.FRONTCAMERAPICTURETEST1, 1);
        camNum.put(TestName.FRONTCAMERAPICTURETEST2, 2);
        camNum.put(TestName.FRONTCAMERAPICTURETEST3, 3);
        camNum.put(TestName.FRONTCAMERAPICTURETEST4, 4);
        camNum.put(TestName.FRONTCAMERAPICTURETEST5, 5);
        camNum.put(TestName.FRONTCAMERAPICTURETEST6, 6);

        camNum.put(TestName.FRONTCAMERAVIDEOTEST, 1);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST1, 1);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST2, 2);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST3, 3);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST4, 4);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST5, 5);
        camNum.put(TestName.FRONTCAMERAVIDEOTEST6, 6);
    }
    public static String getTestResult(int resultCode) {
        switch (resultCode) {
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS:
                return TestResult.PASS;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL:
                return TestResult.FAIL;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_FEATURE_NOT_EQUIPPED:
                return TestResult.NOTEQUIPPED;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT:
                return TestResult.TIMEOUT;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_UNKNOWN:
                return "UNKNOWNERROR";
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_TRIPPLE_TOUCH_PERFORMED:
                return "TRIPPLETOUCHPERFORMED";
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED:
                return TestResult.ACCESSDENIED;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_SETTING_VALUE_CHANGED:
                return "VALUECHANGED";
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED:
                return "USERINPUT";
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_CANBEIMPROVED:
                return TestResult.CANBEIMPROVED;
            default:
                return TestResult.NOTEQUIPPED;
        }
    }
    public static GIFMovieView getNewGIFMovieView(Context context, String name) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GIFMovieView gifMovieView = new GIFMovieView(context, stream);
        gifMovieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return gifMovieView;
    }
    public static HashMap<String, String> manualtestGifMap = new HashMap<String, String>();

    static {
        manualtestGifMap.put(DEADPIXELTEST, "display_deadpixel.gif");
        manualtestGifMap.put(DISCOLORATIONTEST, "display_discoloration.gif");
        manualtestGifMap.put(SCREENBURNTEST, "display_burn_in.gif");
        manualtestGifMap.put(DISPLAYTEST, "display.gif");
        manualtestGifMap.put(DIMMINGTEST, "dimming.gif");
        manualtestGifMap.put(TOUCHTEST, "touch.gif");
        manualtestGifMap.put(SPENTEST, "spen.gif");
        manualtestGifMap.put(CALLTEST, "call.gif");
        manualtestGifMap.put(ACCELEROMETERTEST, "accelerometer.gif");
        manualtestGifMap.put(GPSMANUALTEST, "gps_manual.gif");
        manualtestGifMap.put(AMBIENTTEST, "ambientlight.gif");
        manualtestGifMap.put(PROXIMITYTEST, "proximity.gif");
        manualtestGifMap.put(VIBRATIONTEST, "vibration.gif");
        manualtestGifMap.put(EARPHONETEST, "earphone.gif");
        manualtestGifMap.put(SPEAKERTEST, "speaker.gif");
        manualtestGifMap.put(FINGERPRINTSENSORTEST, "finger_print.gif");
        manualtestGifMap.put(EARPIECETEST, "receiver.gif");
        manualtestGifMap.put(EARPHONEJACKTEST, "earjack.gif");
        manualtestGifMap.put(BLUETOOTHCONNECTIVITYTEST, "bluetooth_toggle_hardware.gif");
        manualtestGifMap.put(BLUETOOTH_TOGGLE, "bluetooth_toggle_hardware.gif");
        manualtestGifMap.put(NFCTEST, "nfc_test_manual.gif");

        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST, "rearcamera.gif");
        //Multi Camera implementation
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST1, "rearcamera.gif");
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST2, "rearcamera.gif");
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST3, "rearcamera.gif");
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST4, "rearcamera.gif");
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST5, "rearcamera.gif");
        manualtestGifMap.put(TestName.REARCAMERAPICTURETEST6, "rearcamera.gif");

        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST, "frontcamera.gif");
        //Multi Camera implementation
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST1, "frontcamera.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST2, "frontcamera.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST3, "frontcamera.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST4, "frontcamera.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST5, "frontcamera.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAPICTURETEST6, "frontcamera.gif");

        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST, "rearcameravideo.gif");
        //Multi Camera implementation
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST1, "rearcameravideo.gif");
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST2, "rearcameravideo.gif");
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST3, "rearcameravideo.gif");
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST4, "rearcameravideo.gif");
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST5, "rearcameravideo.gif");
        manualtestGifMap.put(TestName.REARCAMERAVIDEOTEST6, "rearcameravideo.gif");

        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST, "frontcameravideo.gif");
        //Multi Camera implementation
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST1, "frontcameravideo.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST2, "frontcameravideo.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST3, "frontcameravideo.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST4, "frontcameravideo.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST5, "frontcameravideo.gif");
        manualtestGifMap.put(TestName.FRONTCAMERAVIDEOTEST6, "frontcameravideo.gif");
//        manualtestGifMap.put(TestName.BLUETOOTH_TOGGLE, "frontcameravideo.gif");

        manualtestGifMap.put(CAMERAFLASHTEST, "cameraflash.gif");
        manualtestGifMap.put(FRONTFLASHTEST, "frontcameraflash.gif");
        manualtestGifMap.put(USBTEST, "usbconnection.gif");
        manualtestGifMap.put(CHARGINGTEST, "charging.gif");
        manualtestGifMap.put(GUESTURETEST, "gesture.gif");
        manualtestGifMap.put(MICROPHONETEST, "microphone.gif");
        manualtestGifMap.put(MICROPHONE2TEST, "microphone.gif");
        manualtestGifMap.put(HARDKEYTEST, "hardkey.gif");
        manualtestGifMap.put(SOFTKEYTEST, "softkey.gif");
        manualtestGifMap.put(BATTERYPERFORMANCE, "batteryperformance.gif");
        manualtestGifMap.put(WIFI_MANUAL_TEST, "wifi_toggle_hardware.gif");
        manualtestGifMap.put(WIFICONNECTIVITYTEST, "wifi_toggle_hardware.gif");
    }

    public static HashMap<String, Integer> manualtestImageMap = new HashMap<String, Integer>();

    static {
        manualtestImageMap.put(DEADPIXELTEST, R.drawable.ic_test_display_deadpixel);
        manualtestImageMap.put(DISCOLORATIONTEST, R.drawable.ic_test_display_discoloration);
        manualtestImageMap.put(SCREENBURNTEST, R.drawable.ic_test_display_burn_in);
        manualtestImageMap.put(DISPLAYTEST, R.drawable.ic_test_display);
        manualtestImageMap.put(DIMMINGTEST, R.drawable.ic_test_dimming);
        manualtestImageMap.put(TOUCHTEST, R.drawable.ic_test_touch);
        manualtestImageMap.put(SPENTEST, R.drawable.ic_test_spen);
        manualtestImageMap.put(CALLTEST, R.drawable.ic_test_call);
        manualtestImageMap.put(ACCELEROMETERTEST, R.drawable.ic_test_accelerometer);
        manualtestImageMap.put(GPSMANUALTEST, R.drawable.ic_test_gps_manual);
        manualtestImageMap.put(AMBIENTTEST, R.drawable.ic_test_ambient_light);
        manualtestImageMap.put(PROXIMITYTEST, R.drawable.ic_test_proximity);
        manualtestImageMap.put(VIBRATIONTEST, R.drawable.ic_test_vibration);
        manualtestImageMap.put(EARPHONETEST, R.drawable.ic_test_earphone);
        manualtestImageMap.put(SPEAKERTEST, R.drawable.ic_test_speaker);
        manualtestImageMap.put(FINGERPRINTSENSORTEST, R.drawable.ic_test_fingerprint);
        manualtestImageMap.put(EARPIECETEST, R.drawable.ic_test_receiver);
        manualtestImageMap.put(EARPHONEJACKTEST, R.drawable.ic_test_earjack);
        manualtestImageMap.put(BLUETOOTHCONNECTIVITYTEST, R.drawable.ic_test_bluetooth_connectivity);
        manualtestImageMap.put(BLUETOOTH_TOGGLE, R.drawable.ic_test_bluetooth_connectivity);
        manualtestImageMap.put(TestName.NFCTEST,R.drawable.nfc_manual);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST, R.drawable.ic_test_rear_camera);
        //Multi Camera implementation
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST1, R.drawable.ic_test_rear_camera);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST2, R.drawable.ic_test_rear_camera);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST3, R.drawable.ic_test_rear_camera);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST4, R.drawable.ic_test_rear_camera);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST5, R.drawable.ic_test_rear_camera);
        manualtestImageMap.put(TestName.REARCAMERAPICTURETEST6, R.drawable.ic_test_rear_camera);

        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST, R.drawable.ic_test_frontcamera);
        //Multi Camera implementation
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST1, R.drawable.ic_test_frontcamera);
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST2, R.drawable.ic_test_frontcamera);
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST3, R.drawable.ic_test_frontcamera);
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST4, R.drawable.ic_test_frontcamera);
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST5, R.drawable.ic_test_frontcamera);
        manualtestImageMap.put(TestName.FRONTCAMERAPICTURETEST6, R.drawable.ic_test_frontcamera);

        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST, R.drawable.ic_test_rear_camera_video);
        //Multi Camera implementation
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST1, R.drawable.ic_test_rear_camera_video);
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST2, R.drawable.ic_test_rear_camera_video);
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST3, R.drawable.ic_test_rear_camera_video);
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST4, R.drawable.ic_test_rear_camera_video);
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST5, R.drawable.ic_test_rear_camera_video);
        manualtestImageMap.put(TestName.REARCAMERAVIDEOTEST6, R.drawable.ic_test_rear_camera_video);

        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST, R.drawable.ic_test_front_camera_video);
        //Multi Camera implementation
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST1, R.drawable.ic_test_front_camera_video);
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST2, R.drawable.ic_test_front_camera_video);
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST3, R.drawable.ic_test_front_camera_video);
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST4, R.drawable.ic_test_front_camera_video);
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST5, R.drawable.ic_test_front_camera_video);
        manualtestImageMap.put(TestName.FRONTCAMERAVIDEOTEST6, R.drawable.ic_test_front_camera_video);


        manualtestImageMap.put(CAMERAFLASHTEST, R.drawable.ic_test_camera_flash);
        manualtestImageMap.put(FRONTFLASHTEST, R.drawable.ic_test_front_camera_flash);
        manualtestImageMap.put(USBTEST, R.drawable.ic_test_usb_connection);
        manualtestImageMap.put(CHARGINGTEST, R.drawable.ic_test_charging);
        manualtestImageMap.put(GUESTURETEST, R.drawable.ic_test_gesture);
        manualtestImageMap.put(MICROPHONETEST, R.drawable.ic_test_microphone);
        manualtestImageMap.put(MICROPHONE2TEST, R.drawable.ic_test_microphone);
        manualtestImageMap.put(HARDKEYTEST, R.drawable.ic_test_hardkey);
        manualtestImageMap.put(SOFTKEYTEST, R.drawable.ic_test_soft_key);
        manualtestImageMap.put(BATTERYPERFORMANCE, R.drawable.ic_test_battery_performance);
        manualtestImageMap.put(WIFI_MANUAL_TEST, R.drawable.ic_test_wifi);
        manualtestImageMap.put(WIFICONNECTIVITYTEST, R.drawable.ic_test_wifi);
    }
    public static HashMap<String, String> manualTestClassList = new HashMap<String, String>();

    static {
        manualTestClassList = new HashMap<String, String>();
        manualTestClassList.put(DEADPIXELTEST, DisplayTestActivity.class.getName());
        manualTestClassList.put(DISCOLORATIONTEST, DisplayTestActivity.class.getName());
        manualTestClassList.put(SCREENBURNTEST, DisplayTestActivity.class.getName());
        manualTestClassList.put(DISPLAYTEST, DisplayTestActivity.class.getName());
        manualTestClassList.put(DIMMINGTEST, DimmingTestActivity.class.getName());
        manualTestClassList.put(TOUCHTEST, TouchTestActivity.class.getName());
        manualTestClassList.put(SPENTEST, TouchTestActivity.class.getName());
        manualTestClassList.put(CALLTEST, CallTestActivity.class.getName());
        manualTestClassList.put(ACCELEROMETERTEST, AccelerometerTest.class.getName());
        manualTestClassList.put(GPSMANUALTEST, GPSManualTestActivity.class.getName());
        manualTestClassList.put(AMBIENTTEST, AmbientLightTestActivity.class.getName());
        manualTestClassList.put(PROXIMITYTEST, ProximitySensorTestActivity.class.getName());
        manualTestClassList.put(VIBRATIONTEST, VibrationTestActivity.class.getName());
        manualTestClassList.put(EARPHONETEST, EarphoneTestActivity.class.getName());
        manualTestClassList.put(SPEAKERTEST, SpeakerTestActivity.class.getName());
        manualTestClassList.put(EARPIECETEST, EarpieceTestActivity.class.getName());
        manualTestClassList.put(EARPHONEJACKTEST, EarPhoneJackTestActivity.class.getName());
        manualTestClassList.put(BLUETOOTHCONNECTIVITYTEST, BluetoothConnectivity.class.getName());
        manualTestClassList.put(BLUETOOTH_TOGGLE, BluetoothTestActivity.class.getName());
        manualTestClassList.put(WIFICONNECTIVITYTEST, WifiManualTestActivity.class.getName());
        manualTestClassList.put(BATTERYPERFORMANCE, BatteryPerformanceTestActivity.class.getName());
        //manualTestClassList.put(SLEEPTEST, BluetoothConnectivity.class.getName()); // Dummy activity
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST, CameraPictureTestActivity.class.getName());
        //Multi Camera implementation
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST1, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST2, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST3, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST4, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST5, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAPICTURETEST6, CameraPictureTestActivity.class.getName());

        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST, CameraPictureTestActivity.class.getName());
        //Multi Camera implementation
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST1, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST2, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST3, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST4, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST5, CameraPictureTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAPICTURETEST6, CameraPictureTestActivity.class.getName());

        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST, CameraVideoTestActivity.class.getName());
        //Multi Camera implementation
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST1, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST2, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST3, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST4, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST5, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.REARCAMERAVIDEOTEST6, CameraVideoTestActivity.class.getName());

        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST, CameraVideoTestActivity.class.getName());
        //Multi Camera implementation
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST1, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST2, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST3, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST4, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST5, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.FRONTCAMERAVIDEOTEST6, CameraVideoTestActivity.class.getName());
        manualTestClassList.put(TestName.NFCTEST, NfcTestActivity.class.getName());

        manualTestClassList.put(CAMERAFLASHTEST, CameraFlashTestActivity.class.getName());
        manualTestClassList.put(FRONTFLASHTEST, CameraFlashTestActivity.class.getName());
        manualTestClassList.put(USBTEST, USBConnectionTestActivity.class.getName());
        manualTestClassList.put(CHARGINGTEST, DeviceChargingTestActivity.class.getName());
        manualTestClassList.put(GUESTURETEST, GestureTest.class.getName());
        manualTestClassList.put(MICROPHONETEST, MicroPhoneTestActivity.class.getName());
        manualTestClassList.put(MICROPHONE2TEST, MicroPhoneTestActivity.class.getName());
        manualTestClassList.put(HARDKEYTEST, KeysTestActivity.class.getName());
        manualTestClassList.put(SOFTKEYTEST, KeysTestActivity.class.getName());
        manualTestClassList.put(WIFI_MANUAL_TEST, WifiManualTestActivity.class.getName());
        manualTestClassList.put(FINGERPRINTSENSORTEST, FingerPrintTestActivity.class.getName());
    }




}
