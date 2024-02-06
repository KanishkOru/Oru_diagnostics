package com.oruphones.nativediagnostic.util;

import static org.pervacio.onediaglib.utils.AppUtils.printLog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.oneDiagLib.TestAutoSensor;
import com.oruphones.nativediagnostic.oneDiagLib.TestFlash;
import com.oruphones.nativediagnostic.oneDiagLib.TestVibration;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.pervacio.batterydiaglib.core.ProfileManager;
import com.pervacio.batterydiaglib.core.ResultCodes;
import com.pervacio.batterydiaglib.core.test.QuickTestComputeEngine;
import com.pervacio.batterydiaglib.model.ActivityResultInfo;
import com.pervacio.batterydiaglib.model.BatteryDiagConfig;
import com.pervacio.batterydiaglib.model.BatteryInfo;
import com.pervacio.batterydiaglib.model.QuicktestInfo;
import com.pervacio.batterydiaglib.util.BatteryUtil;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.gesture.Sgesture;


import org.pervacio.onediaglib.atomicfunctions.AFBluetooth;
import org.pervacio.onediaglib.atomicfunctions.AFGPS;
import org.pervacio.onediaglib.atomicfunctions.AFNFC;
import org.pervacio.onediaglib.atomicfunctions.AFWiFi;
import org.pervacio.onediaglib.diagtests.SdCardInsertionTest;
import org.pervacio.onediaglib.diagtests.TestCameraPicture;
import org.pervacio.onediaglib.diagtests.TestDeviceRoot;
import org.pervacio.onediaglib.diagtests.TestLTE;
import org.pervacio.onediaglib.diagtests.TestRamMemory;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.diagtests.TestSdCardResult;
import org.pervacio.onediaglib.diagtests.TestSim;
import org.pervacio.onediaglib.fingerprint.FingerPrintProvider;
import org.pervacio.onediaglib.utils.CameraUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Venkatesh Pendlikal on 18-11-2015.
 */
public class DeviceInfo {
    private static DeviceInfo deviceInfo = null;
    private Context context = null;
    private String _make = null;
    private String _model = null;
    private String _imei = null;
    private String _platform = null;
    private String _version = null;
    private String _rooted = null;
    private String _completeStorage = null;
    private String _availableStorage = null;
    private String _availableMemory = null;
    private String _totalMemory = null;
    private String _carrierName = null;
    private String _serialnumber = null;
    private String _deviceLanguage = null;

    private boolean _isDualSim;
    ActivityManager.MemoryInfo memoryInfo = null;
    ActivityManager activityManager = null;
    StorageStatus storageStatus = null;
    TestAutoSensor testAutoSensor;
    public static long availableRam = -1;

    public static final String PREFS_FILE_NAME = "PVADIAGPrefsFile";
    public static final String PREF_SDCARD_PATH = "SDCardPath";

    public static final String MANUFACTURE_SAMSUNG = "samsung";

    private static String TAG = DeviceInfo.class.getSimpleName();
    private TestDeviceRoot mtestDeviceRoot;


    public static DeviceInfo getInstance(Context context) {
        if (deviceInfo == null) deviceInfo = new DeviceInfo(context);
        deviceInfo.setContext(context);
        return deviceInfo;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private DeviceInfo(Context context) {
        this.context = context;
        memoryInfo = new ActivityManager.MemoryInfo();
        activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);

        storageStatus = getStorageInfo(Build.MANUFACTURER);
        mtestDeviceRoot = new TestDeviceRoot();
        //cscVer = getCSCVersion(this.context);
        //cscCode = getCSCCode(this.context);
    }

    // String csVer = getCSCVersion(this.context);
    //String ccCode = getCSCCode(this.context);


    // String cscVer ;
    // String cscCode ;

    //    private String getCSCCode() {
    //        Context ctx = this.context;
    //        String csccode = getProp(ctx, "ro.csc.sales_code");
    //        if (csccode == null || csccode.length() == 0
    //                || csccode.equalsIgnoreCase("unknown")) {
    //            csccode = getProp(ctx, "ril.sales_code");
    //        }
    //        if (csccode == null || csccode.equalsIgnoreCase("unknown")) {
    //            csccode = "";
    //        }
    //        return csccode;
    //    }

    //    private String getCSCVersion(Context ctx) {
    //        String cscver = getProp(ctx, "ril.official_cscver");
    //        if (cscver == null || cscver.equalsIgnoreCase("unknown")) {
    //            cscver = "";
    //        }
    //        return cscver;
    //    }

    private String getCSCCode(Context ctx) {
        String csccode = getProp(ctx, "ro.csc.sales_code");
        if (csccode == null || csccode.length() == 0
                || csccode.equalsIgnoreCase("unknown")) {
            csccode = getProp(ctx, "ril.sales_code");
        }
        if (csccode == null || csccode.equalsIgnoreCase("unknown")) {
            csccode = "";
        }
        return csccode;
    }

    public String get_carrierName() {
        try {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService
                    (Context.TELEPHONY_SERVICE));
            _carrierName = telephonyManager.getNetworkOperatorName();
        } catch (Exception e) {
            _carrierName = "";
        }
        return _carrierName;
    }

    public String get_serialnumber() {

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            _serialnumber = (String) get.invoke(c, "sys.serialnumber", "Error");
            if (_serialnumber.equals("Error")) {
                _serialnumber = (String) get.invoke(c, "ril.serialnumber", "Error");

            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        if (_serialnumber.equals("Error"))
            _serialnumber = Build.SERIAL;

        return _serialnumber;
    }


    public String get_make() {
        String manufacturer = "";

        try {
            manufacturer = Build.MANUFACTURER;
            if (manufacturer != null || manufacturer.length() == 0) {
                manufacturer = getProp(context, "ro.product.manufacturer");
            } else if (manufacturer == null || manufacturer.length() == 0) {
                manufacturer = getBuildFieldUsingReflection("MANUFACTURER");
            } else if (manufacturer == null || manufacturer.length() == 0) {
                manufacturer = "Unknown";
            }
        } catch (Exception ex) {
            DLog.e(TAG + " " + getClass().getEnclosingMethod().getName(), ex.getMessage());
        }

        _make = manufacturer;
        return _make;
    }

    public String get_model() {
        if (Build.MODEL.equalsIgnoreCase("2014818")) {
            return "HM 2LTE-IN";
        }
        return Build.MODEL;
    }

    public boolean getInternalStorageStatus() {
        storageStatus = getStorageInfo(Build.MANUFACTURER);
        double tempStorage = storageStatus.getInternalTotalStorage() * 0.20;
        DLog.i("DeviceInfo", "Total Memory of 20% " + tempStorage + " Free Memory " +
                storageStatus.getInternalFreeStorage());
        PervacioTest ptest = new PervacioTest();
        ptest.initialize();
        if (tempStorage <= storageStatus.getInternalFreeStorage())
            return true;
        return false;
    }

    public long getInternalTotalStorage() {
        return storageStatus.getInternalTotalStorage();
    }

    public long getInternalFreeStorage() {
        return storageStatus.getInternalFreeStorage();
    }

    public String getDeviceUniqueId() {
        String uniqueId = get_imei();
        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = CommonUtil.getMACAddressInIMEIFormat(context);
        }
        return uniqueId;
    }

    public String get_imei() {
        if (Build.VERSION.SDK_INT >= 29) {
            return Util.getImeiFromPrefs();
        }
        try {
            TelephonyManager _telephonyMgr = (TelephonyManager) context
                    .getSystemService(context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT < 20) {
                _imei = getSingleSimIMEINo(_telephonyMgr);
            } else {
                if (Build.VERSION.SDK_INT == 20 && Build.VERSION.RELEASE.equalsIgnoreCase("l")) {
                    _imei = getSingleSimIMEINo(_telephonyMgr);
                } else {
                    try {
                        _imei = getMutiSimeIMEINo(_telephonyMgr);
                    } catch (Exception e) {
                        _imei = getSingleSimIMEINo(_telephonyMgr);
                    }
                }
            }
        } catch (Exception e) {
            _imei = null;
            e.printStackTrace();
        }
        if (_imei == null)
            _imei = "";
        return _imei;
    }

    @TargetApi(23)
    private String getMutiSimeIMEINo(TelephonyManager telephonyManager) {
        String imei = "";
        int phoneCount = 0;
        try {
            phoneCount = telephonyManager.getPhoneCount();
        } catch (Exception e) {
            phoneCount = 1;
        }
        switch (phoneCount) {
            case 1:
                imei = getSingleSimIMEINo(telephonyManager);
                break;
            case 2:
                try {
                    //Look for dual sim imei number.
                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
                    String imsiSIM1 = telephonyInfo.getImsiSIM1();
                    String imsiSIM2 = telephonyInfo.getImsiSIM2();
                    if (imsiSIM1 == null && imsiSIM2 == null) {
                        try {
                            //imei = getBuildFieldUsingReflection("SERIAL");
                            imei = "";
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                    } else {
                        if (imsiSIM1 == null) imsiSIM1 = "";
                        if (imsiSIM2 == null) imsiSIM2 = "";
                        imei = imsiSIM1 + "," + imsiSIM2;
                    }

                } catch (Exception ex) {
                    imei = getSingleSimIMEINo(telephonyManager);
                }
                break;
            case 3:
            default:
                break;
        }
        if (imei == null)
            imei = "";
        return imei;
    }

    public boolean is_isDualSim() {
        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
        return telephonyInfo.isSIM2Ready();
    }


    public String getSingleSimIMEINo(TelephonyManager telephonyManager) {
        String imei = "";
        try {
            imei = telephonyManager.getDeviceId();
        } catch (Exception ex) {
            ex.getMessage();
        }
        if (imei == null || imei.length() == 0) {
            try {
                imei = "";
            } catch (Exception e) {

            }
        }
        return imei;
    }

    public String getSerialNumber() {
        String _serialnumber = "";

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            _serialnumber = (String) get.invoke(c, "sys.serialnumber", "Error");
            if (_serialnumber.equals("Error")) {
                _serialnumber = (String) get.invoke(c, "ril.serialnumber", "Error");
            }
        } catch (Exception var3) {
            printLog(TAG, "Exception in get_serialnumber  : ", var3, 6);
        }

        if (_serialnumber.equals("Error")) {
            _serialnumber = Build.SERIAL;
        }

        if (_serialnumber.equalsIgnoreCase("unknown")) {
            DLog.i(TAG, "getSerialNumber unknown case Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT == 28) {
                DLog.i(TAG, "getSerialNumber OS 9");
                _serialnumber = Build.getSerial();
            }
            DLog.i(TAG, "getSerialNumber unknown case _serialnumber " + _serialnumber);
        }
        printLog(TAG, "**get_serialnumber serial_no** : " + _serialnumber, (Throwable) null, 6);

        if (!TextUtils.isEmpty(_serialnumber) && !Build.UNKNOWN.equalsIgnoreCase(_serialnumber)) {
            return _serialnumber;
        }

        return "";
    }

    public String get_platform() {
        return _platform = "Android";
    }

    public String getApiLevel() {
        int apiLevel = Build.VERSION.SDK_INT;
        String api_level = "" + apiLevel;
        DLog.i("TAG", "Api level is " + api_level);
        return api_level;
    }

    private String readFirmware() {
        // SystemProperties.get("gsm.version.baseband")
        try {
            Class class1 = Class.forName("android.os.SystemProperties");
            Method declaredMethod = class1.getDeclaredMethod("get", String.class);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, "gsm.version.baseband");
            return invoke.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*public  String getFirmwareVersion()
    {
     String firmwareVersion;
        if (get_make().equalsIgnoreCase("samsung"))
        {
            String buildName = Build.DISPLAY;
            firmwareVersion = buildName.substring((buildName.indexOf(".")+1), buildName.length());
        }
        else if (get_make().equalsIgnoreCase("htc"))
        {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService
                    (Context.TELEPHONY_SERVICE));


           String deviceSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
            firmwareVersion=deviceSoftwareVersion;
           // firmwareVersion=Build.DISPLAY;            //For Marshmallow devicesoftwareversion method is not working in htc.
        }
        else
        {
            firmwareVersion=readFirmware();
        }
     return firmwareVersion;
    }*/

    private int getAndroidSDK_INT() {
        try {
            Field sdkField = Build.VERSION.class
                    .getDeclaredField("SDK_INT");
            sdkField.setAccessible(true);
            int value = (int) sdkField.getInt(Build.VERSION.class);
            return value;
        } catch (Exception ex) {
            DLog.e(TAG, "Exception in getAndroidSDK_INT :" + ex.getMessage());
        }
        return 3;// for CUPCAKE
    }

    private String getBuildNumber(Context ctx, String manufacturer) {
        try {
            if ("TCT".equalsIgnoreCase(manufacturer)) {
                if ("ONE TOUCH 6012A".equalsIgnoreCase(Build.MODEL)) {
                    String buildNumber = getProp(ctx, "ro_def_software_version");
                    if (buildNumber != null && buildNumber.length() > 0) {
                        return buildNumber;
                    }
                }
                String buildNumber = getProp(ctx, "ro_def_build_number");
                if (buildNumber != null && buildNumber.length() > 0) {
                    return buildNumber;
                }
            } else if ("TCL ALCATEL ONETOUCH".equalsIgnoreCase(manufacturer)) {
                String buildNumber = getProp(ctx, "def.tctfw.build.number");
                if (buildNumber != null && buildNumber.length() > 0) {
                    return buildNumber;
                }
            } else if ("TCL".equalsIgnoreCase(manufacturer)) {
                if (Build.MODEL.equalsIgnoreCase("ALCATEL ONETOUCH 6050A")) {
                    if (getAndroidSDK_INT() == Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        return "7DAG-UCG3";
                    } else if (getAndroidSDK_INT() == Build.VERSION_CODES.KITKAT) {
                        return "7DSO-UCO7";
                    }
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, "Exception in getBuildNumber: " + ex.getMessage());
        }
        return Build.DISPLAY;
    }

    public String getFirmwareVersion() {
        String manufacturer = get_make();
        String buildNumber = getBuildNumber(this.context, manufacturer);
        if (manufacturer.equalsIgnoreCase("Samsung")) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(
                        "/system/build.prop"));
                String hidVerProperty = "ro.build.hidden_ver=";
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith(hidVerProperty)) {
                        String hidVer = line.substring(hidVerProperty
                                .length());
                        if (hidVer.trim().length() > 0) {
                            hidVer = hidVer.trim();
                            if ("samsung".equalsIgnoreCase(manufacturer)
                                    && "Verizon"
                                    .equalsIgnoreCase(Build.BRAND)) {
                                if (hidVer.contains("_")) {
                                    String[] sr = hidVer.split("_");
                                    if (sr.length == 2
                                            && sr[0].length() >= 10
                                            && sr[1].length() == 3) {
                                        hidVer = sr[0];
                                    }
                                } else if (buildNumber.contains(".")) {
                                    String[] sr = buildNumber.split("\\.");
                                    if (sr.length == 2
                                            && sr[1].length() >= 10
                                            && hidVer.startsWith(sr[1])) {
                                        hidVer = sr[1];
                                    }
                                }
                            }
                            buildNumber = hidVer;
                        }
                        break;
                    }
                }
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String baseband = getBaseband();
        String swVersion = buildNumber;
        if ("Samsung".equalsIgnoreCase(manufacturer)
                && "Galaxy Nexus".equalsIgnoreCase(Build.MODEL)) {
            swVersion = buildNumber + "_" + baseband;
        } else if ("HTC".equalsIgnoreCase(manufacturer)) {
            String prodVersion = getProp(context, "ro.product.version");
            if (prodVersion != null && prodVersion.length() > 0) {
                swVersion = prodVersion;
            }
        } else if ("LGE".equalsIgnoreCase(manufacturer)) {
            String lgeswVersion = getProp(context, "ro.lge.swversion");
            if (lgeswVersion != null && lgeswVersion.length() > 0) {
                swVersion = lgeswVersion;
            }
        } else if ("TCT".equalsIgnoreCase(manufacturer)) {
            String tctswVersion = getProp(context,
                    "ro.def.philips.software.svn");
            if (tctswVersion != null && tctswVersion.length() > 0) {
                swVersion = tctswVersion;
                buildNumber = tctswVersion;
            }
        }
        return swVersion;
    }

    public String getOSVersionName() {
        int sdkLevel = Build.VERSION.SDK_INT;
        String versionNumber;
        switch (sdkLevel) {
            case 34:
                versionNumber = "14";
                break;
            case 33:
                versionNumber = "13";
                break;
            case 32:
            case 31:
                versionNumber = "12";
                break;
            case 30:
                versionNumber = "11";
                break;
            case 29:
                versionNumber = "10";
                break;
            case 28:
                versionNumber = "9";
                break;
            case 27:
                versionNumber = "8.1";
                break;
            case 26:
                versionNumber = "8.0";
                break;
            case 25:
                versionNumber = "7.1";
                break;
            case 24:
                versionNumber = "7.0";
                break;
            case 23:
                versionNumber = "6";
                break;
            case 22:
                versionNumber = "5.1";
                break;
            case 21:
                versionNumber = "5.0";
                break;
            case 20:
                versionNumber = "4.4W";
                break;
            case 19:
                versionNumber = "4.4";
                break;
            case 18:
                versionNumber = "4.3";
                break;
            case 17:
                versionNumber = "4.2";
                break;
            case 16:
                versionNumber = "4.1";
                break;
            case 15:
                versionNumber = "4.0.3";
                break;
            case 14:
                versionNumber = "4.0.1";
                break;
            case 13:
                versionNumber = "3.2";
                break;
            case 12:
                versionNumber = "3.1";
                break;
            case 11:
                versionNumber = "3.0";
                break;
            case 10:
                versionNumber = "2.3.3";
                break;
            case 9:
                versionNumber = "2.3.0";
                break;
            case 8:
                versionNumber = "2.2";
                break;
            case 7:
                versionNumber = "2.1";
                break;
            case 6:
                versionNumber = "2.0.1";
                break;
            case 5:
                versionNumber = "2.0";
                break;
            case 4:
                versionNumber = "1.6";
                break;
            case 3:
                versionNumber = "1.5";
                break;
            case 2:
                versionNumber = "1.1";
                break;
            case 1:
                versionNumber = "1.0";
                break;
            default:
                versionNumber = "Unknown Version";
                break;
        }

        return versionNumber;


//        String firmware_status;
//        switch (apiVersion) {
//            case 1:
//                firmware_status = "Base";
//                break;
//            case 2:
//                firmware_status = "Base_1_1";
//                break;
//            case 3:
//                firmware_status = "Cupcake";
//                break;
//            case 4:
//                firmware_status = "Donut";
//                break;
//            case 10000:
//                firmware_status = "Current Development";
//                break;
//            case 5:
//                firmware_status = "Eclair";
//                break;
//            case 6:
//                firmware_status = "Eclair_0_1";
//                break;
//            case 7:
//                firmware_status = "Eclair_Mr1";
//                break;
//            case 8:
//                firmware_status = "Froyo";
//                break;
//            case 9:
//                firmware_status = "Gingerbread";
//                break;
//            case 10:
//                firmware_status = "Gingerbread_Mr1";
//                break;
//            case 11:
//                firmware_status = "Honeycomb";
//                break;
//            case 12:
//                firmware_status = "Honeycomb_Mr1";
//                break;
//            case 13:
//                firmware_status = "Honeycomb_Mr2";
//                break;
//            case 14:
//                firmware_status = "Ice Cream Sandwich";
//                break;
//            case 15:
//                firmware_status = "Ice Cream Sandwich_Mr1";
//                break;
//            case 16:
//                firmware_status = "Jelly Bean";
//                break;
//            case 17:
//                firmware_status = "Jelly Bean_Mr1";
//                break;
//            case 18:
//                firmware_status = "Jelly Bean_Mr2";
//                break;
//            case 19:
//                firmware_status = "Kitkat";
//                break;
//            case 20:
//                firmware_status = "Kitkat Watch";
//                break;
//            case 21:
//                firmware_status = "Lollipop";
//                break;
//            case 22:
//                firmware_status = "Lollipop_Mr1";
//                break;
//            case 23:
//                firmware_status = "Marshmallow";
//                break;
//            case 24:
//                firmware_status = "N";
//                break;
//            case 25:
//                firmware_status = "Nougat 7.1";
//                break;
//            case 26:
//                firmware_status = "Oreo 8.0.0";
//                break;
//            case 27:
//                firmware_status = "Oreo 8.1.0";
//                break;
//            case 28:
//                firmware_status = "Pie 9";
//                break;
//            case 29:
//                firmware_status = "Android 10";
//                break;
//            case 30:
//                firmware_status = "Android 11";
//                break;
//            case 31:
//                firmware_status = "Android 12";
//                break;
//            case 32:
//                firmware_status = "Android 12";
//                break;
//            case 33:
//                firmware_status = "Android 12";
//                break;
//            case 34:
//                firmware_status = "Android 14";
//                break;
//            default:
//                firmware_status = "Unknown Version";
//
//        }
//        Log.i("TAG", "Firmware is " + firmware_status);
//        return firmware_status;
    }

    public String get_version() {
        int sdkLevel = Build.VERSION.SDK_INT;
//        StringBuilder builder = new StringBuilder();
//        builder.append("").append((Build.VERSION.RELEASE).replaceAll("[^\\d.]", ""));
//
//        Field[] fields = Build.VERSION_CODES.class.getFields();
//        for (Field field : fields) {
//            String fieldName = field.getName();
//            int fieldValue = -1;
//
//            try {
//                fieldValue = field.getInt(new Object());
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//
//            if (fieldValue == Build.VERSION.SDK_INT) {
//                Log.d(TAG, "FIELD NAME " + fieldName);
//                Log.d(TAG, "FIELD Value " + fieldValue);
//                if (fieldName.equalsIgnoreCase("m")) {  // This is bug solved by Marshmallow Version
//                    fieldName = "MARSHMALLOW";
//                    //return (builder.append(" ").append(fieldName)).toString();
//                    return builder.toString();
//                } else if (fieldName.equalsIgnoreCase("l")) {  // This is bug solved by Marshmallow
//                    // Version
//                    fieldName = "LOLLIPOP";
//                    //return (builder.append(" ").append(fieldName)).toString();
//                    return builder.toString();
//                } else if (fieldName.equalsIgnoreCase("LOLLIPOP_MR1")) {  // This is bug solved by Marshmallow Version
//                    fieldName = "LOLLIPOP";
//                    //return (builder.append(" ").append(fieldName)).toString();
//                    return builder.toString();
//                }
//            }
//        }
//        return _version = builder.toString();
        String versionNumber;
        switch (sdkLevel) {
            case 34:
                versionNumber = "14";
                break;
            case 33:
                versionNumber = "13";
                break;
            case 32:
            case 31:
                versionNumber = "12";
                break;
            case 30:
                versionNumber = "11";
                break;
            case 29:
                versionNumber = "10";
                break;
            case 28:
                versionNumber = "9";
                break;
            case 27:
                versionNumber = "8.1";
                break;
            case 26:
                versionNumber = "8.0";
                break;
            case 25:
                versionNumber = "7.1";
                break;
            case 24:
                versionNumber = "7.0";
                break;
            case 23:
                versionNumber = "6";
                break;
            case 22:
                versionNumber = "5.1";
                break;
            case 21:
                versionNumber = "5.0";
                break;
            case 20:
                versionNumber = "4.4W";
                break;
            case 19:
                versionNumber = "4.4";
                break;
            case 18:
                versionNumber = "4.3";
                break;
            case 17:
                versionNumber = "4.2";
                break;
            case 16:
                versionNumber = "4.1";
                break;
            case 15:
                versionNumber = "4.0.3";
                break;
            case 14:
                versionNumber = "4.0.1";
                break;
            case 13:
                versionNumber = "3.2";
                break;
            case 12:
                versionNumber = "3.1";
                break;
            case 11:
                versionNumber = "3.0";
                break;
            case 10:
                versionNumber = "2.3.3";
                break;
            case 9:
                versionNumber = "2.3.0";
                break;
            case 8:
                versionNumber = "2.2";
                break;
            case 7:
                versionNumber = "2.1";
                break;
            case 6:
                versionNumber = "2.0.1";
                break;
            case 5:
                versionNumber = "2.0";
                break;
            case 4:
                versionNumber = "1.6";
                break;
            case 3:
                versionNumber = "1.5";
                break;
            case 2:
                versionNumber = "1.1";
                break;
            case 1:
                versionNumber = "1.0";
                break;
            default:
                versionNumber = "Unknown Version";
        }
        return versionNumber;
    }

    public String getBuildVERSIONSDK() {
        return unknownIfNullORLenZero(Build.VERSION.SDK);
    }

    public String getBuildDISPLAY() {
        return unknownIfNullORLenZero(Build.DISPLAY);
    }

    public final String getBuildSerial() {
        return unknownIfNullORLenZero(Build.SERIAL);
    }

    public String getBatteryTechnology() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        String tech = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        return tech;
    }

    public String unknownIfNullORLenZero(String paramString) {
        if ((paramString == null) || (paramString.length() == 0))
            return "unknown";
        return paramString;
    }


    public String getBuildFIRMWARE() {
        String manu = "";

        if (Build.VERSION.SDK_INT < 4)
            manu = "unknown";
        manu = unknownIfNullORLenZero(Build.MANUFACTURER);
        //model = unknownIfNullORLenZero(Build.MODEL);
        // brand = unknownIfNullORLenZero(Build.BRAND);

        String buildNumber = Build.DISPLAY;
        if (manu.equalsIgnoreCase("Samsung")) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(
                        "/system/build.prop"));
                String hidVerProperty = "ro.build.hidden_ver=";
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith(hidVerProperty)) {
                        String hidVer = line.substring(hidVerProperty.length());
                        if (hidVer.trim().length() > 0) {
                            buildNumber = hidVer;
                        }
                        break;
                    }
                }
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String baseband = getBaseband();
        String swVersion = buildNumber;
        if ("Samsung".equalsIgnoreCase(manu)
                && "Galaxy Nexus".equalsIgnoreCase(Build.MODEL)) {
            swVersion = buildNumber + "_" + baseband;
        } else if ("HTC".equalsIgnoreCase(manu)) {
            String prodVersion = getProp(context, "ro.product.version");
            if (prodVersion != null && prodVersion.length() > 0) {
                swVersion = prodVersion;
            }
        } else if ("LGE".equalsIgnoreCase(manu)) {
            String lgeswVersion = getProp(context, "ro.lge.swversion");
            if (lgeswVersion != null && lgeswVersion.length() > 0) {
                swVersion = lgeswVersion;
            }
        }
        return unknownIfNullORLenZero(swVersion);
    }


    public static String getOSBuildNumber() {
        String osBuildNumber = Build.FINGERPRINT;
        final String forwardSlash = "/";
        String osReleaseVersion = Build.VERSION.RELEASE + forwardSlash;
        try {
            osBuildNumber = osBuildNumber.substring(osBuildNumber.indexOf(osReleaseVersion));  //"5.1.1/LMY48Y/2364368:user/release-keys�?
            osBuildNumber = osBuildNumber.replace(osReleaseVersion, "");  //"LMY48Y/2364368:user/release-keys�?
            osBuildNumber = osBuildNumber.substring(0, osBuildNumber.indexOf(forwardSlash)); //"LMY48Y"
        } catch (Exception e) {
            DLog.e(TAG, "Exception while parsing - " + e.getMessage());
        }

        DLog.i(TAG, "Build number is" + osBuildNumber);
        return osBuildNumber;
    }

    public String get_rooted() {
        if (mtestDeviceRoot.isDeviceRooted()) {
            return "Yes";
        }
        return "No";
    }

    public String getDeviceLanguage() {
        _deviceLanguage = Locale.getDefault().getLanguage();
        return _deviceLanguage;
    }
/*
    public boolean getLiveWallpaperSetting() {

        try {
            WallpaperManager wm = WallpaperManager.getInstance(this.context);
            WallpaperInfo wi = wm.getWallpaperInfo();
            String value = "Off";
            if (wi != null) { // It's Livewallpaper
                value = "On";
                return  true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDeviceRooted() {

        try {
                String buildTags = Build.TAGS;
                if (buildTags != null && buildTags.contains("test-keys")) {
                    return true;
                }
                String[] rootApks = new String[] { "superuser.apk", "z4root.apk",
                        "superoneclick.apk", "androot.apk", "z4mod-1.apk",
                        "cwmmanager.apk" };
                HashSet hs = new HashSet();
                hs.addAll(Arrays.asList(rootApks));
                    try {
                        File appsFolder = new File("/system/app/");
                        if(appsFolder != null) {
                            File[] fileList = appsFolder.listFiles();
                            if (fileList != null) {
                                for (int i = 0; i < fileList.length; i++) {
                                    if (fileList[i].isFile()) {
                                        String name = fileList[i].getName().toLowerCase();
                                        if (name.endsWith(".apk") && hs.contains(name)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    try {
                        Process proc = Runtime.getRuntime().exec("su");
                        if (proc != null) {
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
*/

    public String get_completeStorage() {
        return _completeStorage = storageStatus.TotalStorage + "";
    }

    public String get_AvaialableStorage() {
        return _availableStorage = storageStatus.FreeStorage + "";
    }


    public StorageStatus getStorageInfo(String manu) {
        LinkedHashMap<String, String> mountedPaths = getMountPoints();
        StorageStatus storageStatus = new StorageStatus();
        String internalPath = "";
        String usbStoragePath = "";
        String sdCardPath = "";
        boolean fuseFS = false;
        if (mountedPaths.size() > 0) {
            LinkedHashMap<String, String> voldMountPoints = getVoldMountPoints(mountedPaths);
            if (voldMountPoints.size() > 0) {
                Iterator<String> iter = voldMountPoints.keySet().iterator();
                while (iter.hasNext()) {
                    String path = iter.next();
                    String mediaType = voldMountPoints.get(path);
                    if ("SD".equalsIgnoreCase(mediaType) || "SDIO".equalsIgnoreCase(
                            mediaType)/* for samsung M919 - 4.3 */) {
                        sdCardPath = path;
                    } else {
                        usbStoragePath = path;
                        String fsType = mountedPaths.get(path);
                        if ("fuse".equalsIgnoreCase(fsType)) {
                            fuseFS = true;
                        }
                    }
                }
            }
        }
        if (!fuseFS) {
            internalPath = Environment.getDataDirectory().getAbsolutePath();
        }
        String model = get_model();
        String androidVersion = Build.VERSION.RELEASE;
        if ("SGH-M919".equalsIgnoreCase(model) && "4.3".equalsIgnoreCase(androidVersion)) {
            // workaround for GS4 4.3 sdcard issue
            if (sdCardPath.length() == 0 && usbStoragePath.equalsIgnoreCase("/storage/extSdCard")) {
                sdCardPath = usbStoragePath;
                usbStoragePath = "";
            }
        }
        // check sdcard path from devices
        if (sdCardPath.length() == 0) {
            File fileCur = null;
            for (String sPathCur : Arrays.asList("ext_card", "external_sd", "external_SD",
                    "ext_sd", "external", "extSdCard",
                    "externalSdCard", "sdcard0", "sdcard1",
                    "sdcard", "sdcard-ext")) // external sdcard
            {
                fileCur = new File("/storage/", sPathCur);
                if (!fileCur.exists())
                    fileCur = new File("/mnt/", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sdCardPath = fileCur.getAbsolutePath();
                    break;
                }
            }
        }
        if ("SHARP".equalsIgnoreCase(manu)) {
            usbStoragePath = "/internal_sd";
        }
        if ("Sony".equalsIgnoreCase(manu)) {
            usbStoragePath = "/mnt/int_storage";
        }
        long internalTotal = 0, internalFree = 0;
        long usbTotal = 0, usbFree = 0;
        long sdTotal = 0, sdFree = 0;
        if (internalPath.length() > 0) {
            internalTotal = getMemorySize(internalPath, true);
            internalFree = getMemorySize(internalPath, false);
            internalTotal += getMemorySize(usbStoragePath, true);
            internalFree += getMemorySize(usbStoragePath, false);

            storageStatus.setInternalFreeStorage(internalFree);
            storageStatus.setInternalTotalStorage(internalTotal);
            storageStatus.setSDCardFreeStorage(sdFree);
            storageStatus.setSDCardTotalStorage(sdTotal);
        }
        if (sdCardPath.length() == 0
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StorageUtil strgUtil = new StorageUtil();
            sdCardPath = strgUtil.getSDCardPathFromStorageManager(context);
        }
        if (sdCardPath.length() > 0) {
            sdTotal = getMemorySize(sdCardPath, true);
            sdFree = getMemorySize(sdCardPath, false);
            if (sdTotal > 0) {
                storageStatus.setSDCardTotalStorage(sdTotal);
                storageStatus.setSDCardFreeStorage(sdFree);

                setSDCardPathToDB(sdCardPath);


            }
        }

        storageStatus.TotalStorage = (internalTotal + sdTotal) / 1024;
        storageStatus.FreeStorage = (internalFree + sdFree) / 1024;

        return storageStatus;
    }

    public boolean getSDCardStorageStatus() {
        boolean status = false;
        try {
            double tempStorage = storageStatus.getSDCardTotalStorage() * 0.20;

            DLog.d(TAG, "Total Memory of 20% " + tempStorage + " Free Memory " + tempStorage);
            DLog.d(TAG, "Total Memory of 20% " + tempStorage + " Free Memory " + storageStatus
                    .getSDCardFreeStorage());

            if (tempStorage <= storageStatus.getSDCardFreeStorage()) {
                return status = true;
            } else {
                return status = false;
            }
        } catch (Exception e) {
            DLog.e(TAG, "Exception:" + e.getMessage());
        }
        return status;
    }

    private boolean setSDCardPathToDB(String sdPath) {
        try {
            SharedPreferences settings = context.getSharedPreferences(PREFS_FILE_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_SDCARD_PATH, sdPath);
            editor.commit();
        } catch (Exception e) {
        }
        return true;
    }

    private long getMemorySize(String pathName, boolean total) {
        try {
            File path = new File(pathName);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            if (total) {
                //return stat.getBlockCount() * blockSize / 1048576L; //this is in MB
                return stat.getBlockCount() * blockSize;  //this is in BYTES
            } else {
                //return stat.getAvailableBlocks() * blockSize / 1048576L;  //this is in MB
                return stat.getAvailableBlocks() * blockSize; //this is in BYTES
            }
        } catch (Exception e) {
        }
        return 0;
    }

    LinkedHashMap<String, String> getMountPoints() {
        LinkedHashMap<String, String> mountPoints = new LinkedHashMap<String, String>();
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (!line.startsWith("/dev/block/vold/")) {
                        line = line.replaceAll("//", "/");
                    }
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] fields = line.split("\\s+");
                        if (fields.length > 2) {
                            String mntPath = fields[1];
                            String fsType = fields[2];
                            //                            Log.i("PVA", "mntpath: " + mntPath + "
                            // " + fsType);
                            if (!mountPoints.containsKey(mntPath)) {
                                mountPoints.put(mntPath, fsType);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mountPoints;
    }

    LinkedHashMap<String, String> getVoldMountPoints(LinkedHashMap<String, String> mountedPoints) {
        LinkedHashMap<String, String> voldMountPoints = new LinkedHashMap<String, String>();
        try {
            File voldFile = new File("/system/etc/internal_sd.fstab");// for
            // Huawei device(U8686-Prism II)
            if (!voldFile.exists()) {
                voldFile = new File("/system/etc/vold.fstab.nand");// ALCATEL
                // ONE TOUCH, Fierce/5020T
            }
            if (!voldFile.exists()) {
                voldFile = new File("/system/etc/vold.fstab");
            }
            if (voldFile.exists()) {
                Scanner scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        String[] fields = line.split("\\s+");
                        if (fields.length > 4) {
                            String mntPath = fields[2];
                            if (mntPath.contains(":")) {
                                mntPath = mntPath.substring(0, mntPath.indexOf(":"));
                            }
                            if (!mountedPoints.containsKey(mntPath)) {
                                try {
                                    File file = new File(mntPath);
                                    if (!file.getAbsolutePath().equals(file.getCanonicalPath())) {
                                        mntPath = file.getCanonicalPath();
                                    }
                                } catch (Exception e) {
                                }
                            }
                            if (mountedPoints.containsKey(mntPath)) {
                                String mediaType = "";
                                for (int i = 4; i < fields.length; i++) {
                                    if (fields[i].contains("mmc_host")) {
                                        String type = getMediaPathType("/sys/" + fields[i]);
                                        if (type != null && type.length() > 0) {
                                            mediaType = type;
                                            break;
                                        }
                                    }
                                }
                                if (voldMountPoints.containsKey(mntPath)) {
                                    String type = voldMountPoints.get(mntPath);
                                    if (!"SD".equalsIgnoreCase(type) && mediaType.length() > 0) {
                                        voldMountPoints.put(mntPath, mediaType);
                                    }
                                } else {
                                    voldMountPoints.put(mntPath, mediaType);
                                }
                            }
                        }
                    }
                }
            } else {
                voldFile = new File("/system/etc/vold.conf");
                if (voldFile.exists()) {
                    Scanner scanner = new Scanner(voldFile);
                    boolean structStarted = false;
                    String mntPath = "";
                    String mediaPath = "";
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        line = line.trim();
                        if (structStarted) {
                            if (line.endsWith("}")) {
                                if (mntPath.length() > 0 && mediaPath.length() > 0
                                        && mountedPoints.containsKey(mntPath)) {
                                    String mediaType = "";
                                    String type = getMediaPathType("/sys/" + mediaPath);
                                    if (type != null && type.length() > 0) {
                                        mediaType = type;
                                    }
                                    voldMountPoints.put(mntPath, mediaType);
                                }
                                structStarted = false;
                                mntPath = "";
                                mediaPath = "";
                            } else if (line.startsWith("media_path") || line.startsWith
                                    ("mount_point")) {
                                String[] fields = line.split("\\s+");
                                if (fields.length > 1) {
                                    if (fields[0].equals("media_path")) {
                                        mediaPath = fields[1];
                                    } else {
                                        mntPath = fields[1];
                                    }
                                }
                            }
                        } else if (line.startsWith("volume_") && line.endsWith("{")) {
                            structStarted = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voldMountPoints;
    }

    String getMediaPathType(String mediaPath) {
        String type = "";
        try {
            if (mediaPath.endsWith("mmc_host")) {
                try {
                    File mmcHost = new File(mediaPath);
                    if (mmcHost != null && mmcHost.exists() && mmcHost.isDirectory()) {
                        File[] mmcFolders = mmcHost.listFiles();
                        String mmcSubFolder = "";
                        int mmcSubfoldersCount = 0;
                        if (mmcFolders != null) {
                            for (int i = 0; i < mmcFolders.length; i++) {
                                String subFolderName = mmcFolders[i].getName();
                                if (mmcFolders[i].isDirectory()
                                        && subFolderName.startsWith("mmc")) {
                                    mmcSubFolder = subFolderName;
                                    mmcSubfoldersCount++;
                                }
                            }
                        }
                        if (mmcSubfoldersCount == 1) {
                            mediaPath += "/" + mmcSubFolder;
                        }
                    }
                } catch (Exception ex) {
                }
            }
            File mmc = new File(mediaPath);
            if (!mmc.exists()) {
                int lastSlash = mediaPath.lastIndexOf('/');
                if (lastSlash >= 0) {
                    mediaPath = mediaPath.substring(0, lastSlash);
                }
                mmc = new File(mediaPath);
            }
            if (mmc != null) {
                if (mmc.isDirectory()) {
                    File[] subMmcs = mmc.listFiles();
                    for (int j = 0; j < subMmcs.length; j++) {
                        String subMmcName = subMmcs[j].getName();
                        if (subMmcName.startsWith("mmc")) {
                            String typeFileName = mediaPath + "/" + subMmcName
                                    + "/" + "type";
                            File typeFile = new File(typeFileName);
                            if (typeFile.exists()) {
                                StringBuilder sb = getFileContents(typeFile);
                                type = sb.toString().trim();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    StringBuilder getFileContents(File typeFile) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(typeFile));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb;
    }

    public String get_availableMemory() {
        MemoryStatus memoryStatus = getMemoryDetails();
        return memoryStatus.availableMemory + " MB";
    }

    public String get_totalMemory() {
        MemoryStatus memoryStatus = getMemoryDetails();
        return memoryStatus.totalMemory + " MB";
    }

    public boolean getMemoryStatus() {
        TestRamMemory testRamMemory = new TestRamMemory();
        long totalRamMemory = testRamMemory.getTotalRamMemory();
        long availableRamMemory = 0;
        if (availableRam != -1) {
            availableRamMemory = availableRam;
        } else {
            availableRamMemory = testRamMemory.getAvailableRamMemory();
            availableRam = availableRamMemory;
        }
        double memoryThreashHold = totalRamMemory * 0.20;

        DLog.i(TAG, "available mem :" + availableRamMemory + " , memoryThreashHold : " +
                memoryThreashHold + " Total Ram Memory " + totalRamMemory);
        if (availableRamMemory > memoryThreashHold) {
            return true;
        }
        return false;
    }


    public long getTotalRamMemory() {
        TestRamMemory testRamMemory = new TestRamMemory();
        return (testRamMemory.getTotalRamMemory() * 1024); //send in BYTES
    }

    public int getTotalRamInHighestUnits() {
        String ramSizeString = new StorageUtil().readableFileSize(getTotalRamMemory());
        DLog.d(TAG, "getTotalRamInHighestUnits  ramSizeString : " + ramSizeString);
        int ramSizeNumber = 0;
        if (!TextUtils.isEmpty(ramSizeString)) {
            ramSizeString = ramSizeString.replaceAll(",", ".");
            String storageString = ramSizeString.indexOf('G') != -1 ? ramSizeString.substring(0, ramSizeString.indexOf('G')) : ramSizeString;
            double ramDouble = Double.valueOf(storageString.trim());
            ramSizeNumber = (int) Math.ceil(ramDouble);
        }
        return ramSizeNumber;
    }

    public String getTotalRamWithUnits() {
        String unitString = "";
        String ramSizeString = new StorageUtil().readableFileSize(getTotalRamMemory());
        DLog.d(TAG, "getTotalRamWithUnits  ramSizeString : " + ramSizeString);
        int ramSizeNumber = 0;
        if (!TextUtils.isEmpty(ramSizeString)) {
            ramSizeString = ramSizeString.replaceAll(",", ".");
            String storageString = ramSizeString.indexOf('G') != -1 ? ramSizeString.substring(0, ramSizeString.indexOf('G')) : ramSizeString;
            unitString = ramSizeString.indexOf('G') != -1 ? ramSizeString.substring(ramSizeString.indexOf('G')) :
                    ramSizeString.indexOf('M') != -1 ? ramSizeString.substring(ramSizeString.indexOf('M')) : ramSizeString;
            double ramDouble = Double.valueOf(storageString.trim());
            ramSizeNumber = (int) Math.ceil(ramDouble);
            if (ramSizeNumber > 4 && ramSizeNumber % 2 != 0) {
                ramSizeNumber = ramSizeNumber + 1;
            }
        }
        return ramSizeNumber + " " + unitString;
    }

    public long getAvailableRamMemory() {
        TestRamMemory testRamMemory = new TestRamMemory();
        long availableRamMemory = 0;
        if (availableRam != -1) {
            availableRamMemory = availableRam;
        } else {
            availableRamMemory = testRamMemory.getAvailableRamMemory();
            availableRam = availableRamMemory;
        }
        return (availableRamMemory * 1024); //send in BYTES
    }

    private MemoryStatus getMemoryDetails() {
        MemoryStatus memoryStatus = new MemoryStatus();
        if (Build.VERSION.SDK_INT < 16) {

        } else if (Build.VERSION.SDK_INT > 16) {
            memoryStatus.availableMemory = memoryInfo.availMem / 1048576L;
            memoryStatus.totalMemory = memoryInfo.totalMem / 1048576L;
        }
        return memoryStatus;
    }

    public String getBaseband() {
        String baseband = getBuildFieldUsingReflection("RADIO");
        if (baseband == null || baseband.length() == 0 || baseband.equalsIgnoreCase("unknown")) {
            baseband = getProp(context, "gsm.version.baseband");
        }
        if (baseband == null || baseband.equalsIgnoreCase("unknown")) {
            baseband = "";
        }
        return baseband;
    }

    private String getBuildFieldUsingReflection(String field) {
        String value = "";
        try {
            Field buildField = Build.class.getDeclaredField(field);
            buildField.setAccessible(true);
            value = (String) buildField.get(Build.class);
        } catch (Exception ex) {
        }
        return value;
    }

    private String getProp(Context ctx, String key) {
        String ret = "";
        try {
            ClassLoader cl = ctx.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = new Object[1];
            params[0] = new String(key);
            ret = (String) get.invoke(SystemProperties, params);
        } catch (Exception e) {
            e.printStackTrace();
            ret = "";
        }
        return ret;
    }

    public float getScreenBrightnessLevel(Context context) {
        float brightnesslevel = 0;
        try {
            float brightnessValue = Settings.System.getInt(context.getContentResolver(),
                    Settings.System
                            .SCREEN_BRIGHTNESS, -1);
            if (brightnessValue == -1) {

            } else {
                String model = get_model();
                int minValue = getMinimumScreenBrightnessSetting(context); // initial values
                int maxValue = getMaximumScreenBrightnessSetting(context); // initial values
                if (model.equalsIgnoreCase("SC-05D")) {
                    minValue = 30;
                    maxValue = 205;
                }
                if (model.startsWith("SH") && model.contains("E")) {
                    minValue = 30;
                }
                //brightnesslevel= (float) (((brightnessValue - minValue) * 100.0 / (maxValue -
                // minValue)) / 100.0);
                DLog.d(TAG, "min values : " + minValue);
                DLog.d(TAG, "max values : " + maxValue);
                brightnesslevel = ((brightnessValue - minValue) * 100f / (maxValue - minValue));
            }
        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
        }
        return brightnesslevel;
    }

    public int getMinimumScreenBrightnessSetting(Context context) {
        try {
            final Resources res = Resources.getSystem();
            int id = res.getIdentifier("config_screenBrightnessSettingMinimum", "integer",
                    "android"); // API17+
            if (id == 0)
                id = res.getIdentifier("config_screenBrightnessDim", "integer", "android"); // lower
            if (id != 0) {
                try {
                    return res.getInteger(id);
                } catch (Resources.NotFoundException e) {
                    DLog.e(TAG, e.getMessage());
                }
            }
            return 0;

        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
            return 0;
        }

    }

    public int getMaximumScreenBrightnessSetting(Context context) {
        try {
            final Resources res = Resources.getSystem();
            final int id = res.getIdentifier("config_screenBrightnessSettingMaximum", "integer",
                    "android"); // API17+
            if (id != 0) {
                try {
                    return res.getInteger(id);
                } catch (Resources.NotFoundException e) {
                    DLog.e(TAG, e.getMessage());
                }
            }
            return 255;
        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
            return 255;
        }
    }

    public boolean getScreenBrightnessAutoMode() {
        boolean flag = false;
        float brightnesslevel = getScreenBrightnessLevel(context);
        DLog.d(TAG, "brightness value : " + brightnesslevel);
        try {
            int brightnessMode = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            flag = true;
            if (Build.MODEL.equalsIgnoreCase("LG-D690")) {
                if (brightnesslevel <= 40.0) {
                    return false;
                } else {
                    return true;
                }
            }
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                flag = false;
                DLog.d(TAG, "Auto mode : " + flag);
            } else if (brightnesslevel <= 40.0) {
                flag = false;
                DLog.d(TAG, "brightness level : " + flag);
            }
            return flag;
        } catch (Exception e) {
            DLog.e(TAG + " " + getClass().getEnclosingMethod().getName(), e.getMessage());
            return false;
        }
    }

    public boolean getSDCardStatus() {
        boolean flag = false;
        String sdCardPath = getSDCardPath();
        if (sdCardPath != null && sdCardPath.length() > 0) {
            if (sdCardPath.equalsIgnoreCase("not present")) {
                return false; // Motorola Nexus 6
            }
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public String getSDCardStoragePath() {
        String sdCardStoragePath = "";
        String sdCardPath = getSDCardPath();
        if (sdCardPath != null && sdCardPath.length() > 0) {
            sdCardStoragePath = sdCardPath;
        }
        return sdCardStoragePath;
    }


    public boolean getSIMState() {
        TestSim testSim = new TestSim();
        TestResult testResult = testSim.checkCurrentSimState();
        if (testResult.getResultCode() == TestResult.RESULT_PASS) {
            return true;
        }
        return false;
    }

    public boolean getWifiEnable() {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(context.WIFI_SERVICE);
        boolean flag = false;
        if (wifiManager != null) {

            if (wifiManager.isWifiEnabled()) {
                DLog.e(TAG, "on");
                flag = true;
                DLog.i(TAG, "getWifiEnable  " + flag);
            }
        }
        return flag;
    }


    private String getSDCardPath() {
        boolean extSDPresent = false;
        StringBuilder debugInfo = new StringBuilder();
        String make = Build.MANUFACTURER;
        String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_SHARED.equals(externalStorageState)) {
            disableUsbMassStorage();
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
            }
            externalStorageState = Environment.getExternalStorageState();
        }
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)
                || Environment.MEDIA_MOUNTED_READ_ONLY
                .equals(externalStorageState)) {
            try {
                extSDPresent = true;
                File path = Environment.getExternalStorageDirectory();
                debugInfo.append("\nExternalStorageDirectory: "
                        + path.getName());
                DLog.i(TAG, "ExternalStorageDirectory: " + path.getName());
                debugInfo.append("\nAbsolutePath: " + path.getAbsolutePath());
                DLog.i(TAG, "AbsolutePath: " + path.getAbsolutePath());
                debugInfo.append("\nCanonicalPath: " + path.getCanonicalPath());
                DLog.i(TAG, "CanonicalPath: " + path.getCanonicalPath());
                if (make.equalsIgnoreCase("motorola")) {
                    try {
                        File motExtPath = new File("/mnt/sdcard-ext");
                        if (motExtPath.exists()) {
                            path = motExtPath;
                        }
                    } catch (Exception e) {
                    }
                }
                File[] files = path.listFiles();
                File extSDFolder = null;
                String[] extSDFolderStrings = new String[]{"external_sd"};
                if (make.equalsIgnoreCase("Samsung")) {
                    extSDFolderStrings = new String[]{"external_sd", "sd"};
                } else if (make.equalsIgnoreCase("LGE")) {
                    extSDFolderStrings = new String[]{"_ExternalSD"};
                }
                HashSet<String> extSDFolderNameSet = new HashSet<String>();
                for (int n = 0; n < extSDFolderStrings.length; n++) {
                    extSDFolderNameSet.add(extSDFolderStrings[n]);
                }
                for (int f = 0; f < files.length; f++) {
                    if (extSDFolderNameSet.contains(files[f].getName())
                            && files[f].isDirectory()) {
                        extSDPresent = false;
                        extSDFolder = files[f];
                    }
                    debugInfo.append("\n"
                            + (files[f].isDirectory() ? "Dir: " : "File: ")
                            + files[f].getName());
                }
                if (extSDFolder != null) {
                    files = extSDFolder.listFiles();
                    if (files.length > 0) {
                        extSDPresent = true;
                    }
                }
                if (extSDPresent) {
                    extSDPresent = isExternalSDPresent();
                    debugInfo.append("\n" + "isExternalStorageRemovable: "
                            + extSDPresent);
                }
                if (extSDPresent) {
                    return (extSDFolder != null) ? extSDFolder.getPath() : path
                            .getPath();
                }
            } catch (Exception e) {
                DLog.e(TAG, e.getMessage());
                return "not present";
            }
        }
        return null;
    }

    private void disableUsbMassStorage() {
        DLog.i(TAG, "disableUsbMassStorage");
        try {
            StorageManager sm = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            if (sm != null) {
                Class c = Class.forName(sm.getClass().getName());
                if (c != null) {
                    Field fld = c.getDeclaredField("mMountService");
                    if (fld != null)
                        fld.setAccessible(true);

                    DLog.i(TAG, "disabled UsbMassStorage");
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
        }
    }


    public boolean isExternalSDPresent() {
        try {
            boolean present = false;
            String mmcHostPath = "/sys/class/mmc_host/";
            File mmcHost = new File(mmcHostPath);
            if (mmcHost != null && mmcHost.exists()) {
                File[] mmcs = mmcHost.listFiles();
                if (mmcs != null) {
                    for (int i = 0; i < mmcs.length && !present; i++) {
                        String mmcName = mmcs[i].getName();
                        String mmcPath = mmcHostPath + mmcName + "/";
                        File mmc = new File(mmcPath);
                        if (mmc != null && mmc.isDirectory()) {
                            File[] subMmcs = mmc.listFiles();
                            if (subMmcs != null) {
                                for (int j = 0; j < subMmcs.length; j++) {
                                    String subMmcName = subMmcs[j].getName();
                                    if (subMmcName.startsWith(mmcName)) {
                                        String typeFileName = mmcPath + subMmcName
                                                + "/" + "type";
                                        File typeFile = new File(typeFileName);
                                        if (typeFile != null && typeFile.exists()) {
                                            StringBuilder sb = getFileContents(typeFile);
                                            if (sb.toString().trim().equals("SD")) {
                                                present = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            DLog.i(TAG, "isExternalSDPresent:" + present);
            return present;
        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
        }
        return true;
    }

    public int getBatteryData() {
        try {
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));

            int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    -1);

            return health;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBatteryLevel() {
        try {
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));

            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,
                    -1);

            return level;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    class MemoryStatus {
        private long totalMemory;
        private long availableMemory;

        public long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public long getAvailableMemory() {
            return availableMemory;
        }

        public void setAvailableMemory(long availableMemory) {
            this.availableMemory = availableMemory;
        }
    }

    class StorageStatus {
        private long TotalStorage = 0, FreeStorage = 0;
        private long internalTotalStorage = 0, internalFreeStorage = 0;
        private long sdTotal = 0, sdFree = 0;

        public long getInternalTotalStorage() {
            return internalTotalStorage;
        }

        public void setInternalTotalStorage(long internalTotalStorage) {
            this.internalTotalStorage = internalTotalStorage;
        }

        public long getInternalFreeStorage() {
            return internalFreeStorage;
        }

        public void setInternalFreeStorage(long internalFreeStorage) {
            this.internalFreeStorage = internalFreeStorage;
        }

        public long getTotalStorage() {
            return TotalStorage;
        }

        public void setTotalStorage(long totalStorage) {
            TotalStorage = totalStorage;
        }

        public long getFreeStorage() {
            return FreeStorage;
        }

        public void setFreeStorage(long freeStorage) {
            FreeStorage = freeStorage;
        }

        public void setSDCardTotalStorage(long sdTotal) {
            this.sdTotal = sdTotal;
        }

        public long getSDCardTotalStorage() {
            return sdTotal;
        }

        public void setSDCardFreeStorage(long sdFree) {
            this.sdFree = sdFree;
        }

        public long getSDCardFreeStorage() {
            return sdFree;
        }

    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public boolean isLighSensorAvailable() {

        boolean isSensorAvailable = false; //keep the result in here.
        Sensor sensor = null;
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensormanager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            // Success! That sensor exists.
            isSensorAvailable = true;
            // sensor= sensormanager.getDefaultSensor(sensortype);
            DLog.d(TAG, "sensor is available");
        } else {
            // Failure! sensor not available.
            isSensorAvailable = false;
            DLog.e(TAG, "sensor does not exist");
        }
        return isSensorAvailable;
    }

    public boolean isProximitySensorAvailable() {

        String[] virtualSensorModels = {"SM-A315", "SM-A515"};
        boolean virtualSensor = false;
        for (String model : virtualSensorModels) {
            if (Build.MODEL.contains(model)) {
                virtualSensor = true;
                break;
            }
        }
        boolean isSensorAvailable = false; //keep the result in here.
        Sensor sensor = null;
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensormanager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null && !virtualSensor) {
            // Success! That sensor exists.
            isSensorAvailable = true;
            // sensor= sensormanager.getDefaultSensor(sensortype);
            DLog.d(TAG, "PROXIMITY sensor is available");
        } else {
            // Failure! sensor not available.
            isSensorAvailable = false;
            DLog.e(TAG, "PROXIMITY sensor does not exist");
        }
        return isSensorAvailable;
    }

    public boolean isCameraFlashAvailable() {
        boolean cameraFlashAvailable = false;
        try {
            cameraFlashAvailable = context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH);
        } catch (Exception e) {
        }
        return cameraFlashAvailable;
    }

    public boolean isMicrophoneAvailable() {
        boolean available;
        MediaRecorder recorder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                return true;
        }

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(new File(context.getCacheDir(), "MediaUtil#micAvailTestFile")
                    .getAbsolutePath());
            available = true;
            recorder.prepare();
        } catch (IOException exception) {
            available = false;
        }
        recorder.release();
        return available;
    }


    public boolean isAccelerometerSensorAvailable() {

        boolean isSensorAvailable = false; //keep the result in here.
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // Success! That sensor exists.
            isSensorAvailable = true;
            // sensor= sensormanager.getDefaultSensor(sensortype);
            DLog.d(TAG, "ACCELEROMETER sensor is available");
        } else {
            // Failure! sensor not available.
            isSensorAvailable = false;
            DLog.e(TAG, "ACCELEROMETER sensor does not exist");
        }
        return isSensorAvailable;
    }


    public boolean isSDCardFeatureAvailable() {
        SdCardInsertionTest sdCardInsertionTest = new SdCardInsertionTest();
        TestSdCardResult sdCardResult = sdCardInsertionTest.performSdCardInsertionTest();
        if (sdCardResult==null)
                return false;


        return (sdCardResult.getResultCode() == TestSdCardResult.RESULT_NO_SDCARD) ? false : true;

    }

    public boolean isAirGeastureFeatureAvailable() {

        String deviceManufacturer = null;
        int version = Build.VERSION.SDK_INT;
        deviceManufacturer = Build.MANUFACTURER;
        if (!deviceManufacturer.equalsIgnoreCase(MANUFACTURE_SAMSUNG))
            return false;

        if (version <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }

        // Sgesture initialize
        try {
            Sgesture mGesture = new Sgesture();
            mGesture.initialize(context);
            if (!mGesture.isFeatureEnabled(Sgesture.TYPE_HAND_PRIMITIVE)) {
                // The Samsung Mobile SDK has Gesture APIs, which doesnt' mean
                // they'd work on any device that can run the SDK, you should always
                // check if the device supports the SGesture with
                // SGesture.isFeatureEnabled.
                return false;
            }
            // The Sgesture.initialize() method:
            // initializes the Gesture package
            // checks if the device is a Samsung device
            // checks if the Samsung device supports the Gesture package
            // checks if the Gesture libraries are installed on the device
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (SsdkUnsupportedException e) {
            DLog.e(TAG, "SsdkUnsupportedException: " + e.getMessage());
            return false;
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean isSpenSupported() {
        File path = new File("/sys/class/sec/sec_epen");
        return path.isDirectory();
    }

    public String getSimCountryCode() {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = manager.getSimCountryIso().toUpperCase();
        if (countryCode != null) {
            return countryCode;
        }
        return "";
    }

    public boolean isSensorAvailable(int sensorType) {
        boolean isSensorAvailable = false; //keep the result in here.
        Sensor sensor = null;
        SensorManager sensormanager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        try {
            if (sensormanager.getDefaultSensor(sensorType) != null) {
                isSensorAvailable = true;
            } else {
                isSensorAvailable = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSensorAvailable;
    }

    public boolean isGPSAvailable() {
        AFGPS afgps = new AFGPS();
        return afgps.isFeatureAvailable();
    }

    public static boolean isFingerPrintSensorAvailable() {
        return FingerPrintProvider.hasFeature();
    }

    private String getEsnImei(Context context) {
        String esn = "";

        int phoneCount = 1;
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT < 20) {
            phoneCount = 1;
        } else {
            if (Build.VERSION.SDK_INT == 20 && Build.VERSION.RELEASE.equalsIgnoreCase("l")) {
                phoneCount = 1;
            } else {
                try {
                    phoneCount = mTelephonyMgr.getPhoneCount();
                } catch (Exception e) {
                    phoneCount = 1;
                }
            }
        }
        if (phoneCount > 1) {
            try {
                TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
                String imsiSIM1 = telephonyInfo.getImsiSIM1();
                String imsiSIM2 = telephonyInfo.getImsiSIM2();
                if (imsiSIM1 == null) imsiSIM1 = "";
                if (imsiSIM2 == null) imsiSIM2 = "";
                if (imsiSIM1 == null && imsiSIM2 == null)
                    esn = "";
                else
                    esn = imsiSIM1 + "," + imsiSIM2;
            } catch (Exception e) {
                esn = getSingleSimIMEINo(mTelephonyMgr);
                DLog.d("SATYAM", "Dual Sim Exception:" + e.getMessage());
            }
        } else {
            esn = getSingleSimIMEINo(mTelephonyMgr);
        }
        if (Build.VERSION.SDK_INT >= 29 && !TextUtils.isEmpty(Util.getImeiFromPrefs())) {
            esn = get_imei();
        }
        if (esn == null || "".equalsIgnoreCase(esn)) {
            esn = context.getResources().getString(R.string.not_available);
        }
        return esn;
    }

    /*
    This API will used to send data to server, and setting it to DeviceInformation,
    then will send it to server as a json object.
    */
    public DeviceInformation getDeviceInfromationDataFromDeviceInfo(GlobalConfig globalConfig, Context mContext) {
        DeviceInformation deviceInformation = new DeviceInformation();
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int batteryScale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPercentage = 100 * batteryLevel / (int) batteryScale;
        String batteryTechnology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        int temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        boolean onCall = false;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
              if (tm!=null)
                 {
                   int callState = tm.getCallState();
                    if (callState == TelephonyManager.CALL_STATE_RINGING || callState == TelephonyManager.CALL_STATE_OFFHOOK)
                      onCall = true;
                  }
              else {
                  DLog.e(TAG,"ERROR IN LINE NO 2073 , NO PERMISSION DETECTED FOR PHONE STATE");
              }
        if (deviceInfo == null)
            deviceInfo = DeviceInfo.getInstance(mContext);
        deviceInformation.setImei(getEsnImei(mContext));
        deviceInformation.setAvlInternalStorage(storageStatus.getInternalFreeStorage()); //data in MB
        deviceInformation.setTotalInternalStorage(storageStatus.getInternalTotalStorage()); //data in MB
        deviceInformation.setAvlRAM(getAvailableRamMemory()); //data in MB
        deviceInformation.setTotalRAM(getTotalRamMemory()); //data in MB
        deviceInformation.setSerialno(getSerialNumber());
        deviceInformation.setMake(capitalizeFirstLetter(get_make()));
        deviceInformation.setModel(get_model());


        // String mAndroidVersion = Build.VERSION.RELEASE;
        // mDeviceInfo.setPlatformVersion(mAndroidVersion);
        deviceInformation.setOsVersion(get_version());

        deviceInformation.setPlatform(get_platform());
        deviceInformation.setFirmware(getFirmwareVersion());

        deviceInformation.setApilevel(getApiLevel());


        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
      if (manager!=null) deviceInformation.setCountryCode(manager.getSimCountryIso().toUpperCase());
      else deviceInformation.setCountryCode("NOT FOUND");
        deviceInformation.setBuildnumber(Build.DISPLAY);
        if (get_rooted().equalsIgnoreCase("Yes")) {
            deviceInformation.setGenuineOS(false);
        } else {
            deviceInformation.setGenuineOS(true);
        }


        deviceInformation.setDeviceStorageCapacity(StorageUtil.getStorageCapacityBytes(context));
        deviceInformation.setBatteryType(batteryTechnology);
        deviceInformation.setBatteryLevel(batteryPercentage);
        deviceInformation.setCarriers(get_carrierName());
        deviceInformation.setDeviceLocale(getDeviceLanguage());

        deviceInformation.setAppSubMode(globalConfig.getSubMode());
        deviceInformation.setStoreId(globalConfig.getStoreID());
        deviceInformation.setLastRestart(SystemClock.elapsedRealtime());
        globalConfig.setLastRestartFromDevice(SystemClock.elapsedRealtime());


        getQuickBatteryInfo(context, deviceInformation);
        try {
            String app_ver = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            deviceInformation.setAppVersion(app_ver);
        } catch (PackageManager.NameNotFoundException e) {
            deviceInformation.setAppVersion(BuildConfig.VERSION_NAME);
        }

        //Camera info for multi camera devices
        deviceInformation.setAvailableRearCams(CameraUtil.getAvailableCams(CameraUtil.FACING_REAR));
        DLog.d(TAG, "Rear Cameras available..." + CameraUtil.getAvailableCams(CameraUtil.FACING_REAR));
        deviceInformation.setAvailableFrontCams(CameraUtil.getAvailableCams(CameraUtil.FACING_FRONT));
        DLog.d(TAG, "Front Cameras available..." + CameraUtil.getAvailableCams(CameraUtil.FACING_FRONT));

        deviceInformation.setAirplaneMode(CommonUtil.isAirplaneModeOn(context));
        deviceInformation.setWifi(NetworkUtil.isWifiStatusOn());
        deviceInformation.setMobileData(NetworkUtil.isMobileDataOn());
        deviceInformation.setRoamingMobileData(NetworkUtil.isDataRoamingOn(context));
        deviceInformation.setConnectedNetworkType(NetworkUtil.getNetworkType(context));
        deviceInformation.setDefaultMobileData(NetworkUtil.getDefaultMobileDataOperator(context));
        deviceInformation.setSimSlot1(NetworkUtil.getNetworkOperator(context, 0, false));
        deviceInformation.setSimSlot2(NetworkUtil.getNetworkOperator(context, 1, false));
        deviceInformation.setSim1ICCID(NetworkUtil.getNetworkOperator(context, 0, true));
        deviceInformation.setSim2ICCID(NetworkUtil.getNetworkOperator(context, 1, true));

//now get unavailable features in device. which is required to send the Server
        String _unavailableFeature = "";
        if (!TestFlash.isFrontCameraFlashAvailable())
            _unavailableFeature = _unavailableFeature + TestName.FRONTFLASHTEST + ",";
        if (!deviceInfo.isCameraFlashAvailable())
            _unavailableFeature = _unavailableFeature + TestName.CAMERAFLASHTEST + ",";
        if (!TestCameraPicture.hasFeature(TestCameraPicture.FACING_FRONT))
            _unavailableFeature = _unavailableFeature + TestName.FRONTCAMERAPICTURETEST + ",";

        //    if(pdDeviceFeatures.getCallTestAvailable().equalsIgnoreCase("true"))                    //TelephonyManager
//            _unavailableFeature = _unavailableFeature+PDConstants.SDCARD + ",";
        if (!isVibrationFeatureAvailalbe())                                     //Vibration
            _unavailableFeature = _unavailableFeature + TestName.VIBRATIONTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_GYROSCOPE))
            _unavailableFeature = _unavailableFeature + TestName.GYROSCOPESENSORTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD))
            _unavailableFeature = _unavailableFeature + TestName.MAGNETICSENSORTEST + ",";
        if (!deviceInfo.isProximitySensorAvailable())
            _unavailableFeature = _unavailableFeature + TestName.PROXIMITYTEST + ",";
        if (!deviceInfo.isAccelerometerSensorAvailable())
            _unavailableFeature = _unavailableFeature + TestName.ACCELEROMETERTEST + ",";

        if (!deviceInfo.isLighSensorAvailable())
            _unavailableFeature = _unavailableFeature + TestName.AMBIENTTEST + ",";

        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_PRESSURE))
            _unavailableFeature = _unavailableFeature + TestName.BAROMETERTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_GAME_ROTATION_VECTOR))
            _unavailableFeature = _unavailableFeature + TestName.GAMEROTATIONVECTORSENSORTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR))
            _unavailableFeature = _unavailableFeature + TestName.GEOMAGNETICROTATIONVECTORSENSORTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_ROTATION_VECTOR))
            _unavailableFeature = _unavailableFeature + TestName.ROTATIONVETORSENSORTEST + ",";
        if (!deviceInfo.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION))
            _unavailableFeature = _unavailableFeature + TestName.LINEARACCELERATIONSENSORTEST + ",";

        if (!new AFWiFi().isFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.WIFI_TOGGLE + ",";
        if (!new AFBluetooth().isFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.BLUETOOTHCONNECTIVITYTEST + ",";

        if (!new AFGPS().isFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.GPS_TOGGLE + ",";
        if (!isFingerPrintSensorAvailable())
            _unavailableFeature = _unavailableFeature + TestName.FINGERPRINTSENSORTEST + ",";
        if (!TestCameraPicture.hasFeature(TestCameraPicture.FACING_REAR))
            _unavailableFeature = _unavailableFeature + TestName.REARCAMERAPICTURETEST + ",";




/*


   if (!deviceInfo.isSoftKeyTestAvailable())           //it is not required because server know whether device
                                                              // have softkey/hardkey or not.
            _unavailableFeature = _unavailableFeature + TestName.mSOFTKEYTEST + ",";*/
        /*if (!deviceInfo.isSDCardFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.mSDCARD + ",";*/


        try {
            if (!deviceInfo.isSDCardFeatureAvailable()) {
                //Log.i("SatyaTest", "getDeviceInfromationDataFromDeviceInfo deviceInfo.isSDCardFeatureAvailable(): " + deviceInfo.isSDCardFeatureAvailable());
                _unavailableFeature = _unavailableFeature + TestName.mSDCARDSLOT + ",";
            }
        } catch (Exception e) {
            _unavailableFeature = _unavailableFeature + TestName.mSDCARDSLOT + ",";
        }

/*        if (onCall && !am.isWiredHeadsetOn() && !am.isSpeakerphoneOn()) //see correct or not api used
            _unavailableFeature = _unavailableFeature + TestName.mRECEIVERTEST + ",";*/
//        if(pdDeviceFeatures.getDeviceCharging().equalsIgnoreCase("true"))                   //get Device Charging test
//            _unavailableFeature = _unavailableFeature+PDConstants.FINGERPRINTSENSORTEST + ",";


        if (!deviceInfo.getSIMState()) //call test check
            _unavailableFeature = _unavailableFeature + TestName.CALLTEST + ",";
        if (!isLTEFeatureAvailable())                    //TestLTE.hasFeature()
            _unavailableFeature = _unavailableFeature + TestName.LTETEST + ",";
        if (!isSIMAvailableAvailable())                    //TestLTE.hasFeature()
            _unavailableFeature = _unavailableFeature + TestName.SIMCARD + ",";
        if (!getIsAmbientTemperatureSensorAvailable())                    //TestLTE.hasFeature()
            _unavailableFeature = _unavailableFeature + TestName.mAMBIENTTEMPERATURESENSOR + ",";
        if (!isHumidityFeatureAvailable())                    //TestLTE.hasFeature()
            _unavailableFeature = _unavailableFeature + TestName.RELATIVEHUMIDITYTEST + ",";
        if (!isNFCFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.NFC_TOGGLE + ",";
        if (!isSpenSupported())
            _unavailableFeature = _unavailableFeature + TestName.SPENTEST + ",";
        if (!isAirGeastureFeatureAvailable())
            _unavailableFeature = _unavailableFeature + TestName.GUESTURETEST + ",";


//logic to remove the last comma from _unavailableFeature string.
        if (_unavailableFeature.length() > 0) {
            _unavailableFeature.trim();
            _unavailableFeature = _unavailableFeature.substring(0, _unavailableFeature.length() - 1);
            deviceInformation.setUnavailableFeatures(_unavailableFeature.split(","));
        }

        return deviceInformation;
    }

    private void getQuickBatteryInfo(Context context, DeviceInformation deviceInformation) {
        BatteryDiagConfig batteryDiagConfig = new BatteryDiagConfig.BatteryDiagConfigBuilder(true)
                .build();
        ProfileManager profileManager = new ProfileManager();
        profileManager.initializeProfile(context, batteryDiagConfig);
        ActivityResultInfo activityResultInfo = new QuickTestComputeEngine(
                batteryDiagConfig, BatteryUtil.getBatteryCapacity(context))
                .computeQuickTestSoh();

        String batteryHealthStatus = activityResultInfo.getTestResult().name();
        if (activityResultInfo.getTestResult() == ResultCodes.TestResult.UNSUPPORTED) {
            batteryHealthStatus = "NA";
        }
        deviceInformation.setBatteryHealth(batteryHealthStatus);
        deviceInformation.setBatterySOH(activityResultInfo.getSoh());

        BatteryInfo batteryInfo = profileManager.getBatteryProfile().getBatteryInfo();
        deviceInformation.setBatteryDesignCapacityQuick(batteryInfo.getCapacityMah());
        deviceInformation.setBatteryLevel(batteryInfo.getCurrentBatteryLevel());
        deviceInformation.setBatteryType(batteryInfo.getTechnology());
        deviceInformation.setBatteryCharging(batteryInfo.getBatteryCharging());
        deviceInformation.setBatteryPlugged(batteryInfo.getBatteryPlugged());
        deviceInformation.setBatteryVoltage(batteryInfo.getBatteryVoltage());
        deviceInformation.setBatteryTemperature(batteryInfo.getBatteryTemperature());

        QuicktestInfo quicktestInfo = batteryInfo.getQuicktestInfo();
        long batteryFullChargeCapacity = getSamsungActualCapacity(quicktestInfo, batteryInfo.getCapacityMah());
        if (batteryFullChargeCapacity == -1) {
            batteryFullChargeCapacity = getDeviceActualCapacity(quicktestInfo);
        }
        deviceInformation.setBatteryFullChargeCapacity(batteryFullChargeCapacity);
    }

    public long getDeviceActualCapacity(QuicktestInfo quicktestInfo) {
        long actualCapacity = quicktestInfo.getBatteryChargeFull();
        if (actualCapacity == -1) {
            actualCapacity = quicktestInfo.getBmsChargeFull();
        }
        //If current is in microamps, need to convert to mili
        if (actualCapacity > 10000) {
            actualCapacity = actualCapacity / 1000;
        }
        return actualCapacity;
    }

    public long getSamsungActualCapacity(QuicktestInfo quicktestInfo, long designCapacity) {
        if (quicktestInfo.getFgFullCapNom() > 500 && quicktestInfo.getFgFullCapNom() < com.pervacio.batterydiaglib.core.Constants.MAX_ASSUMED_DESIGN_CAPACITY_QUICKTEST_MAH) {
            long actualCapacity = quicktestInfo.getFgFullCapNom();
            if ((quicktestInfo.getFgFullCapNom() < (com.pervacio.batterydiaglib.core.Constants.MAX_ASSUMED_DESIGN_CAPACITY_QUICKTEST_MAH / 2))
                    && (quicktestInfo.getFgFullCapNom() * 2 < designCapacity)) {
                actualCapacity = quicktestInfo.getFgFullCapNom() * 2;
            }
            return actualCapacity;

        }
        return -1;
    }

    public boolean isGPSEnabled() {
        boolean status = false;
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locManager != null) {
            status = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        if (status && getLocationMode() == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
            return true;
        }
        return false;
    }


    public boolean isVibrationFeatureAvailalbe() {
        TestVibration testVibration = TestVibration.getInstance();
        return testVibration.isVibrationFutureAvaible();
    }

//    public boolean isAmbientFeatureAvailable() {
//        testAutoSensor = TestAutoSensor.getInstance("", 0, 0);
//        testAutoSensor.isFutureAvailable()
//    }

    public boolean isHumidityFeatureAvailable() {
        testAutoSensor = TestAutoSensor.getInstance("", 0, 0);
        return testAutoSensor.isFutureAvailable("humidity");
    }

    public boolean isNFCFeatureAvailable() {
        AFNFC afnfc = new AFNFC();
        return afnfc.isFeatureAvailable();
    }

    public boolean isLTEFeatureAvailable() {
        return TestLTE.hasFeature();
    }


    public boolean isSIMAvailableAvailable() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) ? true : false;
    }

    public boolean getIsAmbientTemperatureSensorAvailable() {
        testAutoSensor = TestAutoSensor.getInstance("", 0, 0);
        return testAutoSensor.isFutureAvailable("temperature");
    }

    /*

    private long getBlocks(String folder) {
        long blocks = 0;
        try {
            StatFs stat = new StatFs(folder);
            blocks = stat.getBlockCount();
        } catch (Exception e) {
            Log.e(TAG, "getBlocks: Exception " + e);
        }
        return blocks;
    }

    private long getBlockSize() {
        long blockSize = 1;
        try {
            StatFs stat = new StatFs("/system");
            blockSize = stat.getBlockSize();
        } catch (Exception e) {
            Log.e(TAG, "getBlockSize: Exceptoin" + e);
        }
        return blockSize;
    }


    private String readableFileSize(long size) {
        String readableSize="";
        try {
            if(size <= 0) return readableSize;
            final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
            int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
            DecimalFormat formatter = new DecimalFormat("#,##0.##");
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);
            readableSize=formatter.format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readableSize;
    }*/


    /*
   0 = LOCATION_MODE_OFF
   1 = LOCATION_MODE_SENSORS_ONLY
   2 = LOCATION_MODE_BATTERY_SAVING
   3 = LOCATION_MODE_HIGH_ACCURACY
   */
    public int getLocationMode() {
        int mode;
        try {
            mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Exception e) {
            return Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
        }
        return mode;
    }

    //Returns the connected Wifi Network name if any.
    public String getConnectedNetworkName() {
        String networkName = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) OruApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting()) {
                networkName = netInfo.getExtraInfo();
                if (networkName != null && networkName.startsWith(("\"")) && networkName.endsWith("\"")) {
                    networkName = networkName.substring(1, networkName.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return networkName;
    }

    public boolean getMobileDataStatus() {

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API

        }
        return mobileDataEnabled;
    }

    public boolean getRoaming() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.isNetworkRoaming();
    }

    public boolean isAirplaneModeOn() {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public void getAPNInfo(Activity activity) {

        /*TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PersistableBundle pr = telephony.getCarrierConfig();
        HashMap<String, Object> map = new HashMap<>();
        for(String string : pr.keySet()){
            if(string.contains("apn"))
            map.put(string,pr.get(string));
        }
        Log.d("",map.toString());
        ///configManager.getConfig().get(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL)
       */

        //        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        ///String imsiSIM1 = telephony.getDeviceId();;
//
//        telephony.getNetworkOperatorName();
//        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService(Context.CARRIER_CONFIG_SERVICE);
//        configManager.getConfig();
//        TelephonyProviderTest
        getSettingsFromApnsFile(activity);


    }

    private boolean getSettingsFromApnsFile(Context context) {
        FileReader reader = null;
        boolean sawValidApn = false;

        try {
            reader = new FileReader("/etc/apns-conf.xml");
            StringBuilder stringBuilder = new StringBuilder();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(reader);

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simOperator = telephonyManager.getSimOperator();
            if (TextUtils.isEmpty(simOperator)) {
                DLog.w(TAG, "unable to get sim operator - so unable to get mms config");
                return false;
            }

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
                    HashMap<String, String> attributes = new HashMap<String, String>();
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        attributes.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
                    }

                    if (attributes.containsKey("mcc") && attributes.containsKey("mnc") && simOperator.equals(attributes.get("mcc") + attributes.get("mnc"))) {

                        String mmsc = attributes.get("carrier");
                        stringBuilder.append("carrier").append(" : ").append(attributes.get("carrier")).append(",");
                        stringBuilder.append("apn").append(" : ").append(attributes.get("apn")).append("\n\n");

                        /*if (isValidApnType(attributes.get("type"), PhoneConstants.APN_TYPE_MMS)) {
                            sawValidApn = true;

                            String mmsc = attributes.get("mmsc");
                            if (mmsc == null) {
                                eventType = xpp.next();
                                continue;
                            }
                        }*/

                    }
                }
                eventType = xpp.next();
            }
            //AppUtils.toast(stringBuilder.toString());
        } catch (Exception e) {
            // AppUtils.toast(stringBuilder.toString());
            DLog.e(TAG, "unable to get mmsc config from apns-conf file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
        return sawValidApn;
    }
}


 /*    public   String[] getEmailIds() {
            ArrayList<String> mailsList = new ArrayList<String>();
            AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account[] acList = manager.getAccounts();
            for (int i = 0; i < acList.length; i++) {
                String accountName = acList[i].name;

                try {
                    if (Patterns.EMAIL_ADDRESS.matcher(accountName).matches()) {
                        if (!mailsList.contains(accountName)) {
                            mailsList.add(accountName);
                            Log.e("email", "accountName=" + accountName);
                        }
                    }
                } catch (Exception e) {
                    // AppU4tils.printLog(TAG, e.toString(), e, LogType.EXCEPTION);
                }
            }
            String[] emailIds = new String[mailsList.size()];
            for (int i = 0; i < mailsList.size(); i++)
                emailIds[i] = mailsList.get(i);
            return emailIds;
        }*/

 /* public boolean getDeviceSecuritySettings(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.d(TAG,"getDeviceSecuritySettings KeyguardManager Method...");
                KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                Log.d(TAG, "KeyguardManager KeyguardSecure Status : " + km.isKeyguardSecure());
                Log.d(TAG, "KeyguardManager KeyguardLocked Status : " + km.isKeyguardLocked());
                return km.isKeyguardSecure();
            } else {
                Log.d(TAG,"getDeviceSecuritySettings Reflection Method...");
                Class<?> clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
                Constructor<?> constructor = clazz.getConstructor(Context.class);
                constructor.setAccessible(true);
                Object utils = constructor.newInstance(context);
                Method method = clazz.getMethod("isSecure");
                return (Boolean) method.invoke(utils);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception in getting Device Security Settings : " + e.getMessage());
        }
        return false;
    }*/


  /*public String getBatteryHealthStatus() {
        int code = getBatteryData();
        //String healthStatus = "";
        if(code == BatteryManager.BATTERY_HEALTH_COLD || code == BatteryManager.BATTERY_HEALTH_GOOD)
            return PDConstants.PDPASS;
        else if (code == BatteryManager.BATTERY_HEALTH_DEAD)
            return PDConstants.PDFAIL;
        else
            return PDConstants.PDCANBEIMPROVED;
*//*        switch (code) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthStatus = "Cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthStatus = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthStatus = "Good";
                return true;

            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthStatus = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthStatus = "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthStatus = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthStatus = "Unspecified Failure";
                break;
            default:
                break;
        }
        return false;*//*
    }*/

 /*public boolean getBlutoothEnable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        boolean flag = false;
        if (mBluetoothAdapter != null) {

            if (mBluetoothAdapter.isEnabled()) {
                Log.e("isBlutoothEnable", "on");
                flag = true;
            }
        }
        return flag;
    }*/



   /* public boolean getNFCSetting() {
        boolean flag = false;
        try {
            // Froyo won't support NFC
            if (Build.VERSION.SDK_INT > 9) {
                final NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(context);
                if (mAdapter.isEnabled()) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return flag;
        }
        return flag;
    }*/

    /*public String getScreenTimeoutValue() {

        try {
            int value = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
            //BaseActivity.setScrntimeoutVal(value);
            if (value == -2) { // Htc one M7 and M8 if the device in auto sleep mode
                return PDConstants.PDPASS;
            }
            else if (value < 0) {
                return PDConstants.PDCANBEIMPROVED;
            }
            else if (value > 60 * 1000) {
                return PDConstants.PDCANBEIMPROVED;
            }
            else {
                return PDConstants.PDPASS;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return PDConstants.PDPASS;
    }*/

    /*public boolean getAirplaneModeSetting() {
        boolean isEnabled = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isEnabled = Settings.System.getInt(this.context.getContentResolver(), Settings
                                .Global.AIRPLANE_MODE_ON,
                        0) == 1;
            }
            else {
                isEnabled = Settings.System.getInt(this.context.getContentResolver(), Settings
                                .System.AIRPLANE_MODE_ON,
                        0) == 1;
            }

        } catch (Exception e) {
        }
        return isEnabled;
    }
*/

    /* public boolean getWifiHotspotSetting() {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiApControl wifiApControl = WifiApControl
                    .getApControl(wifiManager);
            // Check if Wifi tethering is supported or not
            boolean isSupported = WifiApControl.isApSupported();
            int state = wifiApControl.getWifiApState();
            String value = "Off";
            if (wifiApControl.isWifiApEnabled()) {
                value = "On";
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }*/

