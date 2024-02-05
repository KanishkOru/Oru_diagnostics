package com.oruphones.nativediagnostic.api;

import static androidx.core.app.ActivityCompat.startIntentSenderForResult;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.communication.api.PDAppsInfo;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.models.PDAppResolutionInfo;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;


import org.pervacio.onediaglib.atomicfunctions.AFBluetooth;
import org.pervacio.onediaglib.atomicfunctions.AFGPS;
import org.pervacio.onediaglib.atomicfunctions.AFNFC;
import org.pervacio.onediaglib.atomicfunctions.AFSettings;
import org.pervacio.onediaglib.diagtests.TestBluetooth;
import org.pervacio.onediaglib.diagtests.TestBrightness;
import org.pervacio.onediaglib.diagtests.TestLiveWallPaper;
import org.pervacio.onediaglib.diagtests.TestRamMemory;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.diagtests.TestWiFi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Pervacio on 22-08-2017.
 */

public class Resolution implements ResolutionName {
    public static final String RESULT_OPTIMIZED = "OPTIMIZED";
    public static final String RESULT_NOTOPTIMIZED = "NOTOPTIMIZED";
    public static final String SCREEN_OFF_SETTINGS = "screen_off_timeout";
    private static Resolution resolutionOBJ;
    private int SCREENTIMEOUTVALUE = 0;
    private static String TAG = Resolution.class.getSimpleName();
    private float startingBrightnessLevel = 0;
    private Handler handler;
    private String resolutionName;
    private ArrayList<PDAppResolutionInfo> pdAppResolutionInfos = new ArrayList<PDAppResolutionInfo>();
    private ArrayList<String> availableResolutionsList = new ArrayList<String>();
    private boolean appResolutionDone = false;
    private boolean memoryResolutionDone = false;
    private boolean fileResolutionDone = false;
    private boolean malwareFound = false;
    private boolean riskyFound = false;
    private boolean adwareFound = false;
    private boolean outdatedFound = false;
    private boolean unusedFound = false;
    private ArrayList<AppInfo> malwareAppsList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> riskyAppsList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> addwareAppsList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> outdatedAppsList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> unusedAppsList = new ArrayList<AppInfo>();
    private ArrayList<FileInfo> imageFileList = new ArrayList<FileInfo>();
    private ArrayList<FileInfo> audioFileList = new ArrayList<FileInfo>();
    private ArrayList<FileInfo> videoFileList = new ArrayList<FileInfo>();
    private ArrayList<FileInfo> duplicateFileList = new ArrayList<FileInfo>();
    private ArrayList<FileInfo> largeFileList = new ArrayList<FileInfo>();
    private ArrayList<PDStorageFileInfo> storageFileInfoList = new ArrayList<PDStorageFileInfo>();
    private ArrayList<PDAppsInfo> appsInfoList =new ArrayList<>();
    private ArrayList<AppInfo> foregroundAppList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> backgroundAppList = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> autostartAppList = new ArrayList<AppInfo>();
    private LocationManager locationManager;
    private BroadcastReceiver statusChangeReceiver;
    private TestBrightness mTestBrightness;
    private AFSettings afSettings = null;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean receiverRegisterd = false;
    private TestResult testResult;
    private TestBluetooth testBluetooh;
    private TestLiveWallPaper mTestLiveWallPaper;
    private GlobalConfig globalConfig;
    private AFNFC testNFC;
    private TestWiFi testWifi;
    private AppsUninstallReceiver appsUninstallReceiver;
    private AFBluetooth afBluetooth;
    public static final int EDIT_REQUEST_CODE = 300;
    private static Context mcontext;

    final static  String MIMETYPE_AUDIO = "audio";
    final static  String MIMETYPE_VIDEO = "video";
    final static String MIMETYPE_IMAGE = "image";
    final static  String MIMETYPE_OTHER = "other";

    private BroadcastReceiver wifiStateChangedReceiver
            = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch (extraWifiState) {

                case WifiManager.WIFI_STATE_DISABLED:
                    postMessageToHandler(0, "pass");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    postMessageToHandler(1, "NotImproved");
                    break;
                default:
                    break;
            }
        }

    };
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                boolean result = isBlutoothEnable();
               // Toast.makeText(context, String.valueOf(result), Toast.LENGTH_SHORT).show();
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (BLUETOOTH_OFF.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(0, "pass");
                        else if (BLUETOOTH_ON.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(1, "NotImproved");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (BLUETOOTH_OFF.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(1, "NotImproved");
                        else if (BLUETOOTH_ON.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(0, "pass");
                        break;
                    default:
                        break;
                }

            }
        }
    };


    private Resolution() {
        initializeTestObjects();
    }

    public static Resolution getInstance() {
        if (resolutionOBJ == null) resolutionOBJ = new Resolution();
        return resolutionOBJ;
    }

    public static void clearInstance() {
        resolutionOBJ = null;
    }

    public static boolean deleteDir(Context context, File dir) {
        if (dir.isDirectory()) {
            try {
                String[] children = dir.list();
                if (children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(context, new File(dir, children[i]));
                        if (!success) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        // The directory is now empty so delete it
        boolean status =  dir.delete();
        deleteFileFromMediaStore(context.getContentResolver(), dir);
        return status;
    }

    public static boolean deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        boolean deleted = false;
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                int delresult = contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                deleted = delresult > 0;
            }
        } else {
            deleted = true;
        }
        return deleted;
    }

    public static boolean isApplicationKilled(String packageName, Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
               /* if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE) {
                    return true;
                }*/
                return false;
            }
        }
        return true;
    }

    public ArrayList<PDStorageFileInfo> getStorageFileInfoList() {
        return storageFileInfoList;
    }

    public void setStorageFileInfoList(ArrayList<PDStorageFileInfo> storageFileList) {
        this.storageFileInfoList = storageFileList;
    }

    public ArrayList<String> getAvailableResolutionsList() {
        return availableResolutionsList;
    }

    public void setAvailableResolutionsList(ArrayList<String> availableResolutionsList) {
        this.availableResolutionsList = availableResolutionsList;
    }

    private void initializeTestObjects() {
        afSettings = new AFSettings();
//        SCREENTIMEOUTVALUE = afSettings.getScreenTimeOutValue();
        startingBrightnessLevel = afSettings.getScreenBrightnessValue();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        testBluetooh = new TestBluetooth();
        mTestLiveWallPaper = new TestLiveWallPaper();
        globalConfig = GlobalConfig.getInstance();
        mTestBrightness = new TestBrightness();
        testNFC = new AFNFC();
        testWifi = new TestWiFi();
        afBluetooth = new AFBluetooth();
    }

    public boolean isMemoryResolutionDone() {
        return memoryResolutionDone;
    }

    public void setMemoryResolutionDone(boolean memoryResolutionDone) {
        this.memoryResolutionDone = memoryResolutionDone;
    }

    public boolean isFileResolutionDone() {
        return fileResolutionDone;
    }

    public void setFileResolutionDone(boolean fileResolutionDone) {
        this.fileResolutionDone = fileResolutionDone;
    }

    public ArrayList<FileInfo> getImageFileList() {
        return imageFileList;
    }

    public void setImageFileList(ArrayList<FileInfo> imageFileList) {
        this.imageFileList = imageFileList;
    }

    public ArrayList<FileInfo> getAudioFileList() {
        return audioFileList;
    }
    public ArrayList<PDAppsInfo> getAppsInfoList() {
        return appsInfoList;
    }

    public void setAppsInfoList(ArrayList<PDAppsInfo> appsInfoList) {
        this.appsInfoList = appsInfoList;
    }

    public void setAudioFileList(ArrayList<FileInfo> audioFileList) {
        this.audioFileList = audioFileList;
    }

    public ArrayList<FileInfo> getVideoFileList() {
        return videoFileList;
    }

    public void setVideoFileList(ArrayList<FileInfo> videoFileList) {
        this.videoFileList = videoFileList;
    }

    public ArrayList<FileInfo> getDuplicateFileList() {
        return duplicateFileList;
    }

    public void setDuplicateFileList(ArrayList<FileInfo> duplicateFileList) {
        this.duplicateFileList = duplicateFileList;
    }

    public ArrayList<FileInfo> getLargeFileList() {
        return largeFileList;
    }

    public void setLargeFileList(ArrayList<FileInfo> largeFileList) {
        this.largeFileList = largeFileList;
    }

    public ArrayList<AppInfo> getForegroundAppList() {
        return foregroundAppList;
    }

    public void setForegroundAppList(ArrayList<AppInfo> foregroundAppList) {
        this.foregroundAppList = foregroundAppList;
    }

    public ArrayList<AppInfo> getBackgroundAppList() {
        return backgroundAppList;
    }

    public void setBackgroundAppList(ArrayList<AppInfo> backgroundAppList) {
        this.backgroundAppList = backgroundAppList;
    }

    public ArrayList<AppInfo> getAutostartAppList() {
        return autostartAppList;
    }

    public void setAutostartAppList(ArrayList<AppInfo> autostartAppList) {
        this.autostartAppList = autostartAppList;
    }

    public ArrayList<AppInfo> getMalwareAppsList() {
        return malwareAppsList;
    }

    public void setMalwareAppsList(ArrayList<AppInfo> malwareAppsList) {
        this.malwareAppsList = malwareAppsList;
    }

    public ArrayList<AppInfo> getRiskyAppsList() {
        return riskyAppsList;
    }

    public void setRiskyAppsList(ArrayList<AppInfo> riskyAppsList) {
        this.riskyAppsList = riskyAppsList;
    }

    public ArrayList<AppInfo> getAddwareAppsList() {
        return addwareAppsList;
    }

    public void setAddwareAppsList(ArrayList<AppInfo> addwareAppsList) {
        this.addwareAppsList = addwareAppsList;
    }

    public ArrayList<AppInfo> getOutdatedAppsList() {
        return outdatedAppsList;
    }

    public void setOutdatedAppsList(ArrayList<AppInfo> outdatedAppsList) {
        this.outdatedAppsList = outdatedAppsList;
    }

    public ArrayList<AppInfo> getUnusedAppsList() {
        return unusedAppsList;
    }

    public void clearResolutionsDataForApps(){
        getAvailableResolutionsList().clear();

        getAddwareAppsList().clear();
        getAppResolutionInfo().clear();
        getBackgroundAppList().clear();
        getForegroundAppList().clear();
        getMalwareAppsList().clear();
        getOutdatedAppsList().clear();
        getUnusedAppsList().clear();

        setAppResolutionDone(false);
        setMalwareFound(false);
        setAdwareFound(false);
        setRiskyFound(false);
        setOutdatedFound(false);
        setUnusedFound(false);
    }

    public boolean isMalwareFound() {
        return malwareFound;
    }

    public void setMalwareFound(boolean malwareFound) {
        this.malwareFound = malwareFound;
    }

    public boolean isRiskyFound() {
        return riskyFound;
    }

    public void setRiskyFound(boolean riskyFound) {
        this.riskyFound = riskyFound;
    }

    public boolean isAdwareFound() {
        return adwareFound;
    }

    public void setAdwareFound(boolean adwareFound) {
        this.adwareFound = adwareFound;
    }

    public ArrayList<PDAppResolutionInfo> getAppResolutionInfo() {
        return this.pdAppResolutionInfos;
    }

    public void setAppResolutionInfo(ArrayList<PDAppResolutionInfo> pdAppResolutionInfos) {
        this.pdAppResolutionInfos = pdAppResolutionInfos;
    }

    public boolean isAppResolutionDone() {
        return appResolutionDone;
    }

    public void setAppResolutionDone(boolean appResolutionDone) {
        this.appResolutionDone = appResolutionDone;
    }


    public boolean isOutdatedFound() {
        return outdatedFound;
    }

    public void setOutdatedFound(boolean outdatedFound) {
        this.outdatedFound = outdatedFound;
    }

    public boolean isUnusedFound() {
        return unusedFound;
    }

    public void setUnusedFound(boolean unusedFound) {
        this.unusedFound = unusedFound;
    }

    public void performSettingsResolution(String resolutionName, Handler handler, boolean applyResolution, Context applicationContext) {
        this.handler = handler;
        this.resolutionName = resolutionName;
        mcontext = applicationContext;
        switch (resolutionName) {
            case BLUETOOTH_ON:
                registerBTReceiver();
                if (!isBlutoothEnable()) {
                    if (changeBluetoothState(true, applicationContext)) {
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    }
                } else {
                    if (changeBluetoothState(false, applicationContext)) {
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                    }
                }
                break;
            case BLUETOOTH_OFF:
                registerBTReceiver();
                if (!isBlutoothEnable()) {
                    boolean bluetoothOff = changeBluetoothState(true, applicationContext);
                    if (bluetoothOff) {
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                    }
                } else {
                    final boolean disableBluetooth = changeBluetoothState(false, applicationContext);
                    if (disableBluetooth) {
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    }
                }
                break;
            case GPS_ON:
            case GPS_OFF:
                if (GPS_OFF.equalsIgnoreCase(resolutionName)) {
                    if (isGPSEnabled())
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                    else
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                } else if (GPS_ON.equalsIgnoreCase(resolutionName)) {
                    if (isGPSEnabled())
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    else
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                }
                statusChangeReceiver = new GPSStatusChangeReceiver();
                OruApplication.getAppContext().registerReceiver(
                        statusChangeReceiver,
                        new IntentFilter(
                                LocationManager.PROVIDERS_CHANGED_ACTION));
                break;
            case NFC_ON:
            case NFC_OFF:
                if (NFC_OFF.equalsIgnoreCase(resolutionName)) {
                    if (isNFCEnable())
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                    else
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                } else if (NFC_ON.equalsIgnoreCase(resolutionName)) {
                    if (isNFCEnable())
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    else
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                }
                statusChangeReceiver = new NFCStatusChangeReceiver();
                registerNFCReceiver();
                break;
            case SCREEN_TIMEOUT:
                if (applyResolution) {
                    try {
                        SCREENTIMEOUTVALUE = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), SCREEN_OFF_SETTINGS);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                    setScreenTimeOut(60000);
                    if (isScreenTimeoutImproved())
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                } else {
                    setScreenTimeOut(SCREENTIMEOUTVALUE);
                    if (!isScreenTimeoutImproved())
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                }
                break;
            case WIFI_ON:
                registerWifiReceiver();
                if (isWifiEnable()) {
                    boolean on = changeWifiState(applicationContext,true);
                    if (on) {
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);

                    }
                } else {
                    boolean off = changeWifiState(applicationContext,false);
                    if (off) {
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    }
                }
                break;
            case BRIGHTNESS:
                if (applyResolution) {
                    if (globalConfig.getAutobrightnessAvailable()) {
                        mTestBrightness.setScreenBrightnessAutoMode(1);
                    } else {
                        setScreenBrightnessValue(40.0F);
                    }
                    postMessageToHandler(0, RESULT_OPTIMIZED);
                } else {
                    if (globalConfig.getAutobrightnessAvailable()) {
                        mTestBrightness.setScreenBrightnessAutoMode(0);
                    } else {
                        setScreenBrightnessValue(startingBrightnessLevel);
                    }
                    postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                }
                break;
            case LIVEWALLPAPER:
                turnOffLiveWallPaper();
                break;
            case FREEMEMORY:
                break;
            default:
        }
    }

    private void setScreenTimeOut(int i) {
        try {
            Settings.System.putInt(OruApplication.getAppContext().getContentResolver(), SCREEN_OFF_SETTINGS, i);
        } catch (Exception e) {
        }
        int defTimeOut = 0;
        try {
            defTimeOut = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), SCREEN_OFF_SETTINGS);
        } catch (Settings.SettingNotFoundException e) {
        }
        DLog.e(TAG, Integer.toString(defTimeOut));

    }

    private boolean isBlutoothEnable() {
        boolean flag = false;
        if (afBluetooth != null && afBluetooth.isFeatureAvailable()) {
            return afBluetooth.getState();
        }
        return flag;

    }

    private boolean isGPSEnabled() {
        AFGPS afgps = new AFGPS();
        return afgps.getState();
    }

    public boolean setScreenBrightnessValue(float percentage) {
        try {
            DeviceInfo deviceInfo = DeviceInfo.getInstance(OruApplication.getAppContext().getApplicationContext());
            int minValue = deviceInfo.getMinimumScreenBrightnessSetting(OruApplication.getAppContext()); // initial values
            int maxValue = deviceInfo.getMaximumScreenBrightnessSetting(OruApplication.getAppContext()); // initial values

            int value = (int) (((percentage * (maxValue - minValue)) / 100) + minValue);
            Settings.System.putInt(OruApplication.getAppContext().getContentResolver(), "screen_brightness", value);
            return true;
        } catch (Exception var3) {

            return false;
        }
    }

    private void clear() {
        if (GPS_ON.equalsIgnoreCase(resolutionName) || GPS_OFF.equalsIgnoreCase(resolutionName)) {
            unregisterGPSReceiver();
        } else if (NFC_OFF.equalsIgnoreCase(resolutionName) || NFC_ON.equalsIgnoreCase(resolutionName)) {
            unregisterNFCReceiver();
        } else if (BLUETOOTH_OFF.equalsIgnoreCase(resolutionName) || BLUETOOTH_ON.equalsIgnoreCase(resolutionName)) {
            unregisterBTReceiver();
        } else if (WIFI_ON.equalsIgnoreCase(resolutionName))
            unregisterWifiReceiver();

    }

    public boolean changeBluetoothState(boolean aOn, Context context) {
       // Log.e("HACKYHERE","changeBluetoothState");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            if(context instanceof Activity) { // check if the context is an instance of Activity
                ((Activity) context).startActivityForResult(intent, 0); // replace 0 with your request code
            }
            else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
        else {
            testResult = testBluetooh.changeStateTest(aOn);
            if (testResult.getResultCode() == TestResult.RESULT_PASS) {
                return true;
            }
        }
        return false;
    }


    private boolean isScreenTimeoutImproved() {
        int defTimeOut = 0;
        try {
            defTimeOut = Settings.System.getInt(OruApplication.getAppContext().getContentResolver(), SCREEN_OFF_SETTINGS);
        } catch (Settings.SettingNotFoundException e) {

        }
        if (defTimeOut <= 60000) {
            return true;
        }
        return false;
    }

    private void registerBTReceiver() {

        OruApplication.getAppContext().registerReceiver(bluetoothReceiver, new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    private void unregisterBTReceiver() {
        try {
            if (bluetoothReceiver != null) {
                OruApplication.getAppContext().unregisterReceiver(bluetoothReceiver);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean disableBluetooth() {
        testResult = testBluetooh.changeStateTest(false);
        DLog.d(TAG, "Bluetooth" + testResult.getResultCode());
        if (testResult.getResultCode() == TestResult.RESULT_PASS) {
            return true;
        }
        return false;
    }

    private void unregisterGPSReceiver() {
        try {

            OruApplication.getAppContext().unregisterReceiver(statusChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnOffLiveWallPaper() {
        if (mTestLiveWallPaper.improveLiveWallPaperTest()) {
            if (!mTestLiveWallPaper.isLiveWallpaperEnabled()) {
                postMessageToHandler(0, RESULT_OPTIMIZED);
            } else {
                postMessageToHandler(1, RESULT_NOTOPTIMIZED);
            }
        } else {
            postMessageToHandler(1, RESULT_NOTOPTIMIZED);
        }

    }

    private void unregisterNFCReceiver() {
        try {

            OruApplication.getAppContext().unregisterReceiver(statusChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean changeWifiState(Context context, boolean aWifiOn) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_WIFI_SETTINGS);
            if(context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 1);
            }
            else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
                return true;
        }
        else{
            testResult = testWifi.changeStateTest(aWifiOn);
            if (testResult.getResultCode() == TestResult.RESULT_PASS) {
                return true;
            }
        }

        return false;
    }

    public boolean isWifiEnable() {
        boolean flag = false;
        if (testWifi != null && testWifi.isFeatureAvailable()) {
            return testWifi.getState();
        }
        return flag;
    }

    private void registerNFCReceiver() {

        OruApplication.getAppContext().registerReceiver(
                statusChangeReceiver,
                new IntentFilter(
                        NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));


    }

    public void performAppResolution(String resolutionName, List<AppInfo> appInfos, Handler handler) {
        this.resolutionName = resolutionName;
        this.handler = handler;
        switch (resolutionName) {
            case ADWAREAPPS:
            case MALWAREAPPS:
            case RISKYAPPS:
            case OUTDATEDAPPS:
            case UNUSEDAPPS:
                registerAppReceiver();
                for (AppInfo appInfo : appInfos) {
                    triggerUninstall(appInfo.getPackageName());
                }
                break;
            case AUTOSTART_APPS:
            case BACKGROUND_APPS:
            case FOREGROUND_APPS:
                for (AppInfo appInfo : appInfos) {
                    boolean killed = killApplication(appInfo.getPackageName());
                    if (killed) {
                        updateAppsList(appInfo);
                    } else
                        postMessageToHandler(0, RESULT_NOTOPTIMIZED);
                }
                updateavailableResolutionList(this.resolutionName, true);

                break;
            default:
                break;
        }
    }

    public void performStorageResolution(Activity activity, Context context, String resolutionName, List<FileInfo> fileList, Handler handler) {
        this.resolutionName = resolutionName;
        this.handler = handler;
//        switch (resolutionName) {
//            case IMAGES:
//            case MUSIC:
//            case VIDEO:
//            case DUPLICATE:
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.R) {
                    deleteFilesFromAndroidR(activity, context, fileList,resolutionName);
                }else {
                    List<String> deletedKeys = new ArrayList<>();
                    for (FileInfo fileInfo : fileList) {
                        File file = new File(fileInfo.getFilePath()).getAbsoluteFile();
                        boolean isDeleted = deleteDir( context, file);
                        if (isDeleted) {
                            updateFilesList(fileInfo);
                            deletedKeys.add(fileInfo.getKey());
                        }
                    }
                    updateavailableResolutionList(INTERNALSTORAGE, true);
                    // if (deletedKeys.size() > 0){
                    postMessageToHandler(0, TextUtils.join(",", deletedKeys));
                    // }
/*                else
                    postMessageToHandler(0, RESULT_NOTOPTIMIZED);*/
                }
//                break;
//            default:
//                break;
//        }
    }

    private boolean triggerUninstall(String pkg) {

        if (isPackageExisted(pkg)) {
            try {
                Uri packageUri = Uri.parse("package:" + pkg);
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                        packageUri);
                uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                OruApplication.getAppContext().startActivity(uninstallIntent);
            } catch (Exception e) {
                return false;
            }
            return isApplicationUninstalled(pkg, OruApplication.getAppContext());
        }
        return false;

    }
    public static boolean isApplicationUninstalled(String packageName,Context context){
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
               /* if (wappProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE) {
                    return true;
                }*/
                return false;
            }
        }
        return false;
    }
    private boolean killApplication(String packageName) {
        if (isPackageExisted(packageName)) {
            try {
                ActivityManager am = (ActivityManager) OruApplication.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(packageName);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return isApplicationKilled(packageName, OruApplication.getAppContext());
        }
        return false;
    }

    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = OruApplication.getAppContext().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updateFilesList(FileInfo fileInfo) {
        imageFileList.remove(fileInfo);
        videoFileList.remove(fileInfo);
        duplicateFileList.remove(fileInfo);
        audioFileList.remove(fileInfo);
        ArrayList<PDStorageFileInfo> pdStorageFileInfos = Resolution.getInstance().getStorageFileInfoList();
        PDStorageFileInfo deletedFileInfo = null;
        for(PDStorageFileInfo storageFileInfo : pdStorageFileInfos) {
            if(storageFileInfo.getKey().equalsIgnoreCase(fileInfo.getKey())){
                deletedFileInfo = storageFileInfo;
            }
        }
        if(deletedFileInfo!= null)
        Resolution.getInstance().getStorageFileInfoList().remove(deletedFileInfo);
    }

    private void updateAppsList(AppInfo appInfo) {
        switch (resolutionName) {
            case AUTOSTART_APPS:
            case BACKGROUND_APPS:
            case FOREGROUND_APPS:
                foregroundAppList.remove(appInfo);
                autostartAppList.remove(appInfo);
                backgroundAppList.remove(appInfo);
                postMessageToHandler(0, RESULT_OPTIMIZED);
                break;
            case RISKYAPPS:
            case MALWAREAPPS:
            case ADWAREAPPS:
            case OUTDATEDAPPS:
            case UNUSEDAPPS:
                foregroundAppList.remove(appInfo);
                autostartAppList.remove(appInfo);
                backgroundAppList.remove(appInfo);
                riskyAppsList.remove(appInfo);
                malwareAppsList.remove(appInfo);
                addwareAppsList.remove(appInfo);
                outdatedAppsList.remove(appInfo);
                unusedAppsList.remove(appInfo);
                postMessageToHandler(0, appInfo.getPackageName());
                break;
            default:
                break;
        }
        updateavailableResolutionList(resolutionName, true);
    }

    private void postMessageToHandler(int resultCode, String message) {
        updateavailableResolutionList(resolutionName, resultCode == 0);
        clear();
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = resultCode;
        bundle.putString("result", resultCode == 0 ? RESULT_OPTIMIZED : RESULT_NOTOPTIMIZED);
        bundle.putString("message", message);
        msg.setData(bundle);
        if (handler != null)
            handler.sendMessage(msg);

    }

    private boolean isNFCEnable() {
        boolean flag = testNFC.getState();
        DLog.d(TAG, "isNFCEnable()" + flag);
        return flag;

    }

    protected void registerAppReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        appsUninstallReceiver = new AppsUninstallReceiver();
        OruApplication.getAppContext().registerReceiver(appsUninstallReceiver, intentFilter);
    }

    private void unregisterAppreceiver() {
        if (appsUninstallReceiver != null) {
            OruApplication.getAppContext().unregisterReceiver(appsUninstallReceiver);
        }
    }

    private void registerWifiReceiver() {

        OruApplication.getAppContext().registerReceiver(wifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

    }

    private void unregisterWifiReceiver() {
        if (wifiStateChangedReceiver != null)
            OruApplication.getAppContext().unregisterReceiver(wifiStateChangedReceiver);

    }

    private void updateavailableResolutionList(String resolution, boolean result) {
        switch (resolution) {
            case INTERNALSTORAGE:
            case DUPLICATE:
            case IMAGES:
            case MUSIC:
            case VIDEO:
                /*boolean imagesFound, audioFound, videoFound, duplicateFound;
                imagesFound = audioFound = videoFound = duplicateFound = false;
                ArrayList<PDStorageFileInfo> pdStorageFileInfos = Resolution.getInstance().getStorageFileInfoList();
                for(PDStorageFileInfo storageFileInfo : pdStorageFileInfos) {
                    if(storageFileInfo.getFileType().equalsIgnoreCase(PDStorageFileInfo.FILE_TYPE_IMAGE))
                        imagesFound = true;
                    if(storageFileInfo.getFileType().equalsIgnoreCase(PDStorageFileInfo.FILE_TYPE_AUDIO))
                        audioFound = true;
                    if(storageFileInfo.getFileType().equalsIgnoreCase(PDStorageFileInfo.FILE_TYPE_VIDEO))
                        videoFound = true;
                    if(storageFileInfo.isDuplicate())
                        duplicateFound = true;
                }

                if (!imagesFound) {
                    availableResolutionsList.remove(IMAGES);
                }
                if (!audioFound) {
                    availableResolutionsList.remove(MUSIC);
                }
                if (!videoFound) {
                    availableResolutionsList.remove(VIDEO);
                }
                if (!duplicateFound) {
                    availableResolutionsList.remove(DUPLICATE);
                }*/
                if (duplicateFileList.size() == 0) {
                    availableResolutionsList.remove(resolution);
                }
                if (imageFileList.size() == 0) {
                    availableResolutionsList.remove(IMAGES);
                }
                if (audioFileList.size() == 0) {
                    availableResolutionsList.remove(MUSIC);
                }
                if (videoFileList.size() == 0) {
                    availableResolutionsList.remove(VIDEO);
                }

                if (duplicateFileList.size() == 0) {
                    availableResolutionsList.remove(DUPLICATE);
                }

                if (DeviceInfo.getInstance(OruApplication.getAppContext()).getInternalStorageStatus()) {
                    availableResolutionsList.remove(DUPLICATE);
                    availableResolutionsList.remove(MUSIC);
                    availableResolutionsList.remove(VIDEO);
                    availableResolutionsList.remove(IMAGES);
                    updateAutoTestResult(INTERNALSTORAGE, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                break;
            case ADWAREAPPS:
            case MALWAREAPPS:
            case RISKYAPPS:
            case OUTDATEDAPPS:
            case FOREGROUND_APPS:
            case BACKGROUND_APPS:
            case AUTOSTART_APPS:
            case UNUSEDAPPS:
                if (addwareAppsList.size() == 0) {
                    availableResolutionsList.remove(ADWAREAPPS);
                    updateAutoTestResult(ADWAREAPPS, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                if (malwareAppsList.size() == 0) {
                    availableResolutionsList.remove(MALWAREAPPS);
                    updateAutoTestResult(MALWAREAPPS, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                if (riskyAppsList.size() == 0) {
                    availableResolutionsList.remove(RISKYAPPS);
                    updateAutoTestResult(RISKYAPPS, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                if (outdatedAppsList.size() == 0) {
                    availableResolutionsList.remove(OUTDATEDAPPS);
                    updateAutoTestResult(OUTDATEDAPPS, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                if(unusedAppsList.size()==0) {
                    availableResolutionsList.remove(UNUSEDAPPS);
                    updateAutoTestResult(UNUSEDAPPS, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                if (foregroundAppList.size() == 0) {
                    availableResolutionsList.remove(FOREGROUND_APPS);
                }
                if (backgroundAppList.size() == 0) {
                    availableResolutionsList.remove(BACKGROUND_APPS);
                }
                if (autostartAppList.size() == 0) {
                    availableResolutionsList.remove(AUTOSTART_APPS);
                }
                if (getMemoryStatus()) {
                    availableResolutionsList.remove(AUTOSTART_APPS);
                    availableResolutionsList.remove(BACKGROUND_APPS);
                    availableResolutionsList.remove(FOREGROUND_APPS);
                    updateAutoTestResult(FREEMEMORY, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                }
                break;
            case BLUETOOTH_ON:
            case BLUETOOTH_OFF:
            case BRIGHTNESS:
            case GPS_ON:
            case GPS_OFF:
            case NFC_ON:
            case NFC_OFF:
            case SCREEN_TIMEOUT:
            case WIFI_ON:
            case LIVEWALLPAPER:
                if (result) {
                    availableResolutionsList.remove(resolutionName);
                    updateAutoTestResult(resolutionName, com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED);
                } else {
                    if (!availableResolutionsList.contains(resolutionName))
                        availableResolutionsList.add(resolutionName);
                    updateAutoTestResult(resolutionName, com.oruphones.nativediagnostic.models.tests.TestResult.CANBEIMPROVED);
                }
                break;
            case FIRMWARE:
                break;
            default:
                break;
        }
    }

    private void updateAutoTestResult(String testName, String result) {
        TestInfo testInfo = PervacioTest.getInstance().getAutoTestResult().get(testName);
        if (testInfo != null) {
            testInfo.setTestResult(result);
            PervacioTest.getInstance().getAutoTestResult().put(testInfo.getName(), testInfo);
        }
    }

    private boolean getMemoryStatus() {
        TestRamMemory testRamMemory = new TestRamMemory();
        long totalRamMemory = testRamMemory.getTotalRamMemory();
        long availableRamMemory = testRamMemory.getAvailableRamMemory();
        double memoryThreashHold = totalRamMemory * 0.20;
        if (availableRamMemory > memoryThreashHold) {
            return true;
        }
        return false;
    }

    public class GPSStatusChangeReceiver extends BroadcastReceiver {
        public GPSStatusChangeReceiver() {
            //Constructor for GPS State change Receiver
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LocationManager.PROVIDERS_CHANGED_ACTION)) {

                if (GPS_OFF.equalsIgnoreCase(resolutionName)) {
                    if (isGPSEnabled())
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                    else
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                } else if (GPS_ON.equalsIgnoreCase(resolutionName)) {
                    if (isGPSEnabled())
                        postMessageToHandler(0, RESULT_OPTIMIZED);
                    else
                        postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                }


            }
        }
    }

    public class NFCStatusChangeReceiver extends BroadcastReceiver {
        public NFCStatusChangeReceiver() {
            //Called when NFC Status is changed
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {

                final int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF);


                switch (state) {

                    case NfcAdapter.STATE_OFF:

                        if (NFC_ON.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                        else if (NFC_OFF.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(0, RESULT_OPTIMIZED);

                        break;
                    case NfcAdapter.STATE_ON:
                        if (NFC_ON.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(0, RESULT_OPTIMIZED);
                        else if (NFC_OFF.equalsIgnoreCase(resolutionName))
                            postMessageToHandler(1, RESULT_NOTOPTIMIZED);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    public class AppsUninstallReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String selPackage = intent.getData().getSchemeSpecificPart();
                if (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED)) {
                    DLog.d(TAG, " in receiver - Uninstalled " + selPackage);
                    AppInfo appInfo = new AppInfo();
                    appInfo.setPackageName(selPackage);
                    updateAppsList(appInfo);
                }
            }
        }
    }

    public static Uri getContentUri(ContentResolver contentResolver, File imageFile,String resolutionName) {
        String filePath;
        try {
            filePath = imageFile.getCanonicalPath();
        } catch (IOException e) {
            filePath = imageFile.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = contentResolver.query(uri,null,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{filePath},null,null);

        Uri baseUri = null;
        String mimeType = getFileMIMEType(imageFile);
        switch (resolutionName){
            case IMAGES:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case MUSIC:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                baseUri = getDuplicateFilesURI(mimeType);
                break;
        }

        if (baseUri!=null  && cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            return null;
        }
    }

    private void deleteFilesFromAndroidR(Activity activity, Context context, List<FileInfo> fileInfoList,String resolutionName){
        List<Uri> urisToModify = new ArrayList<>();
        for(FileInfo fileInfo : fileInfoList){
            File file = new File(fileInfo.getFilePath()).getAbsoluteFile();
            Uri uri = getContentUri(context.getContentResolver(),file,resolutionName);
            if(uri!=null) {
                urisToModify.add(uri);
            }
        }
        if(urisToModify.size()!=0) {
            PendingIntent editPendingIntent = MediaStore.createDeleteRequest(context.getContentResolver(), urisToModify);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    startIntentSenderForResult(activity, editPendingIntent.getIntentSender(),
                            EDIT_REQUEST_CODE, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateStorageResolutionForAndroidR(List<FileInfo> fileList){
        List<String> deletedKeys = new ArrayList<>();
        for (FileInfo fileInfo : fileList) {
                updateFilesList(fileInfo);
                deletedKeys.add(fileInfo.getKey());
        }
        updateavailableResolutionList(INTERNALSTORAGE, true);
        postMessageToHandler(0, TextUtils.join(",", deletedKeys));
    }


    private static  String  getFileMIMEType(File file) {

        File selected = file;
        Uri selectedUri = Uri.fromFile(selected);
        String fileExtension = getFileExtensionFromUrl(selectedUri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase());
        if (file.getAbsolutePath().endsWith(".ogg")) {
            mimeType = MIMETYPE_AUDIO;
        }
        //MimeTypeMap returning null for video type "flv"
        if (file.getAbsolutePath().endsWith(".flv")) {
            mimeType = MIMETYPE_VIDEO;
        }
        //MimeTypeMap returning null for video type "mkv" in below lollipop devices
        if ((Build.VERSION.SDK_INT) < Build.VERSION_CODES.LOLLIPOP && file.getAbsolutePath().endsWith(".mkv")) {
            mimeType = MIMETYPE_VIDEO;
        }
        if (file.getAbsolutePath().toLowerCase().contains("/android/data")) {
            mimeType = MIMETYPE_OTHER;
        }
        if (mimeType != null) {
            return (mimeType.split("/")[0]);

        }else{
            return "";
        }
    }

    private  static String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                int fragment = url.lastIndexOf('#');
                if (fragment > 0) {
                    url = url.substring(0, fragment);
                }

                int query = url.lastIndexOf('?');
                if (query > 0) {
                    url = url.substring(0, query);
                }

                int filenamePos = url.lastIndexOf('/');
                String filename =
                        0 <= filenamePos ? url.substring(filenamePos + 1) : url;

                // if the filename contains special characters, we don't
                // consider it valid for our matching purposes:
                if (!TextUtils.isEmpty(filename) &&
                        Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)" +
                                "\\%\\'\\~\\[\\]\\!\\@\\#\\$\\^\\&\\*\\=\\+\\;\\{\\}\\`]+", filename)) {
                    int dotPos = filename.lastIndexOf('.');
                    if (0 <= dotPos) {
                        return filename.substring(dotPos + 1);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return "";
    }

    private static Uri getDuplicateFilesURI(String mimeType){
        Uri baseUri = null;
        switch (mimeType){
            case MIMETYPE_IMAGE:
                baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case MIMETYPE_VIDEO:
                baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case MIMETYPE_AUDIO:
                baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                break;
        }
        return baseUri;
    }

}
