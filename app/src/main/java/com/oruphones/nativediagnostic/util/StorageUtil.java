package com.oruphones.nativediagnostic.util;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.IMountService;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.atomicfunctions.AFSdcard;
import org.pervacio.onediaglib.internalstorage.StorageUtils;
import org.pervacio.onediaglib.utils.AppUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public final class StorageUtil {
    private static String TAG = StorageUtil.class.getSimpleName();
    public static final String STRING_UNKNOWN = "Unknown";

    public static final String MANUFACTURE_SAMSUNG = "samsung";
    private static final String STORAGE_PATH = "/storage/extSdCard";
    private static final String STORAGE_EXTERNAL_SD_PATH = "/storage/external_SD";
    private static final String STORAGE_EXT_SD = "/storage/ext_sd";
    private static final String MODEL_D01G = "d-01G";
    private static final String MODEL_ALCATEL_ONE_TOUCH_6050A = "ALCATEL ONETOUCH 6050A";
    private static final String MODEL_ALCATEL_ONE_TOUCH_6040A = "ALCATEL ONE TOUCH 6040A";
    private static final String MODEL_ONE_TOUCH_6012A = "ONE TOUCH 6012A";
    public static final String MANUFACTURER_SHARP = "SHARP";


    /*SSD */
    private static final String TEST_FILE_NAME = "pvatest.txt";
    private static final String TEST_FILE_CONTENT = "TestFileContent";

    private static AFSdcard afSdcard = new AFSdcard();

    public static String getProp(Context ctx, String key) {
        String ret = "";
        try {
            ClassLoader cl = ctx.getClassLoader();
            Class mSystemProperties = cl
                    .loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = mSystemProperties.getMethod("get", paramTypes);
            Object[] params = new Object[1];
            params[0] = key;
            ret = (String) get.invoke(mSystemProperties, params);
        } catch (Exception e) {
            DLog.e(TAG, "getProp Exception : ", e);
            ret = "";
        }
        return ret;

    }



    public static String getBuildFieldUsingReflection(String field) {
        String value = "";
        try {
            Field buildField = Build.class.getDeclaredField(field);
            buildField.setAccessible(true);
            value = (String) buildField.get(Build.class);
        } catch (Exception ex) {
            DLog.e(TAG, "getBuildFieldUsingReflection Exception : ", ex);
        }
        return value;
    }


    public static String getManufacturer(Context context){
        String manu = "";

        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        try {
            manu = getProp(context, "ro.product.manufacturer");
        } catch (Exception ex) {
            DLog.e(TAG, "getDeviceInfo Exception 1 : ", ex);
        }
        if (manu == null || manu.length() == 0) {
            manu = getBuildFieldUsingReflection("MANUFACTURER");
        }
        if (manu == null || manu.length() == 0) {
            manu = STRING_UNKNOWN;
        }
        return manu;
    }


   public static LinkedHashMap<String, String> getMountPoints() {
        LinkedHashMap<String, String> mountPoints = new LinkedHashMap<>();
        Scanner scanner = null;
        try {
            File mountFile = new File("/proc/mounts");
            if (mountFile.exists()) {
                scanner = new Scanner(mountFile);
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
                            DLog.d(TAG, "mntpath: " + mntPath + " " + fsType);
                            if (!mountPoints.containsKey(mntPath)) {
                                mountPoints.put(mntPath, fsType);
                            }
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            DLog.e(TAG, "getMountPoints error : ", e);
        } finally {
            try {
                if (scanner != null) {
                    scanner.close();
                }
            } catch (Exception e) {
                DLog.e(TAG, "getMountPoints error : "+e);
            }
        }
        return mountPoints;
    }


   public static LinkedHashMap<String, String> getVoldMountPoints(LinkedHashMap<String, String> mountedPoints) {
        LinkedHashMap<String, String> voldMountPoints = new LinkedHashMap<>();
        Scanner scanner = null;
        try {
            File voldFile = new File("/system/etc/internal_sd.fstab");// for Huawei device(U8686-Prism II)
            if (!voldFile.exists()) {
                voldFile = new File("/system/etc/vold.fstab.nand");// ALCATEL ONE TOUCH Fierce/5020T
            }
            if (!voldFile.exists()) {
                voldFile = new File("/system/etc/vold.fstab");
            }
            if (voldFile.exists()) {
                scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        String[] fields = line.split("\\s+");
                        if (fields.length > 4) {
                            String mntPath = fields[2];
                            if (mntPath.contains(":")) {
                                mntPath = mntPath.substring(0,
                                        mntPath.indexOf(':'));
                            }
                            if (!mountedPoints.containsKey(mntPath)) {
                                String newPath = getAbsolutePath(mntPath);
                                if(!TextUtils.isEmpty(newPath)){
                                    mntPath = newPath;
                                }
                            }
                            if (mountedPoints.containsKey(mntPath)) {
                                String mediaType = "";
                                for (int i = 4; i < fields.length; i++) {
                                    if (fields[i].contains("mmc_host")) {
                                        String type = getMediaPathType("/sys/"
                                                + fields[i]);
                                        if (type != null && type.length() > 0) {
                                            mediaType = type;
                                            break;
                                        }
                                    }
                                }
                                if (voldMountPoints.containsKey(mntPath)) {
                                    String type = voldMountPoints.get(mntPath);
                                    if (!"SD".equalsIgnoreCase(type)
                                            && mediaType.length() > 0) {
                                        voldMountPoints.put(mntPath, mediaType);
                                    }
                                } else {
                                    voldMountPoints.put(mntPath, mediaType);
                                }
                            }
                        }
                    }
                }
                scanner.close();
            } else {
                voldFile = new File("/system/etc/vold.conf");
                if (voldFile.exists()) {
                    scanner = new Scanner(voldFile);
                    boolean structStarted = false;
                    String mntPath = "";
                    String mediaPath = "";
                    while (scanner.hasNext()) {
                        String line = scanner.nextLine();
                        line = line.trim();
                        if (structStarted) {
                            if (line.endsWith("}")) {
                                if (mntPath.length() > 0
                                        && mediaPath.length() > 0
                                        && mountedPoints.containsKey(mntPath)) {
                                    String mediaType = "";
                                    String type = getMediaPathType("/sys/"
                                            + mediaPath);
                                    if (type != null && type.length() > 0) {
                                        mediaType = type;
                                    }
                                    voldMountPoints.put(mntPath, mediaType);
                                }
                                structStarted = false;
                                mntPath = "";
                                mediaPath = "";
                            } else if (line.startsWith("media_path")
                                    || line.startsWith("mount_point")) {
                                String[] fields = line.split("\\s+");
                                if (fields.length > 1) {
                                    if (fields[0].equals("media_path")) {
                                        mediaPath = fields[1];
                                    } else {
                                        mntPath = fields[1];
                                    }
                                }
                            }
                        } else if (line.startsWith("volume_")
                                && line.endsWith("{")) {
                            structStarted = true;
                        }
                    }
                    scanner.close();
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "getVoldMountPoints Exception 1 : ", e);
        } finally {
            try {
                if (scanner != null) {
                    scanner.close();
                }
            } catch (Exception e) {
                DLog.e(TAG, "getVoldMountPoints Exception 2 : ", e);
            }
        }
        return voldMountPoints;
    }


    public static String getStorageInfoWRD(Context context, String manufacturer, StringBuilder builder) {
        StringBuilder storageLogs = new StringBuilder();

        LinkedHashMap<String, String> mountedPaths = getMountPoints();
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
                    if ("SD".equalsIgnoreCase(mediaType)
                            || "SDIO".equalsIgnoreCase(mediaType)/*for samsung M919 - 4.3*/) {
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

        storageLogs.append(" mountedPaths 1: ").append(sdCardPath).append(" : ").append(usbStoragePath);
        if (!fuseFS) {
            internalPath = Environment.getDataDirectory().getAbsolutePath();
        }
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        if (("SGH-M919".equalsIgnoreCase(model) && "4.3".equalsIgnoreCase(androidVersion)) && (sdCardPath.length() == 0 && usbStoragePath.equalsIgnoreCase(STORAGE_PATH))) {
            // workaround for GS4 4.3 sdcard issue
            sdCardPath = usbStoragePath;
            usbStoragePath = "";
        }

        //workaround for Samsung 4.4.2 sdcard issue
        if (sdCardPath.length() == 0
                &&MANUFACTURE_SAMSUNG.equalsIgnoreCase(manufacturer)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 23) {
            if (usbStoragePath.equalsIgnoreCase(STORAGE_PATH)) {
                usbStoragePath = "";
            }
            sdCardPath = STORAGE_PATH;
        }
        //work around for LGE 4.4.2 sdcard issue
        if (sdCardPath.length() == 0 && "LGE".equalsIgnoreCase(manufacturer) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (usbStoragePath.equalsIgnoreCase(STORAGE_EXTERNAL_SD_PATH)) {
                usbStoragePath = "";
            }
            sdCardPath = STORAGE_EXTERNAL_SD_PATH;
        }
        //work around for HTC 4.4.2 sdcard issue
        if (sdCardPath.length() == 0 && "HTC".equalsIgnoreCase(manufacturer) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (usbStoragePath.equalsIgnoreCase(STORAGE_EXT_SD)) {
                usbStoragePath = "";
            }
            sdCardPath = STORAGE_EXT_SD;
        }

        if (sdCardPath.length() == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if ("SH-01D".equalsIgnoreCase(model) ||
                    "SH-06D".equalsIgnoreCase(model) ||
                    "SH-06DNERV".equalsIgnoreCase(model) ||
                    "SH-07D".equalsIgnoreCase(model) ||
                    "F-12D".equalsIgnoreCase(model)) {
                sdCardPath = "/mnt/sdcard";
            }
        }
        if ("SH-02G".equalsIgnoreCase(model)
                || "F-03G".equalsIgnoreCase(model)
                || MODEL_D01G.equalsIgnoreCase(model)
                || "F-06F".equalsIgnoreCase(model)
                || "SH-01G".equalsIgnoreCase(model)
                || "SO-01G".equalsIgnoreCase(model)
                || "F-02G".equalsIgnoreCase(model)
                || "SO-04F".equalsIgnoreCase(model)
                || "SO-02E".equalsIgnoreCase(model)
                || "SH-04F".equalsIgnoreCase(model)
                || "SH-06F".equalsIgnoreCase(model)
                || "SO-01F".equalsIgnoreCase(model)
                || "SH-04G".equalsIgnoreCase(model)
                || "SH-05G".equalsIgnoreCase(model)
                || "SO-03F".equalsIgnoreCase(model)
                || "SO-05G".equalsIgnoreCase(model)
                || "SO-02F".equalsIgnoreCase(model)
                || "F-04G".equalsIgnoreCase(model)
                || "SO-02G".equalsIgnoreCase(model)
                || "dtab01".equalsIgnoreCase(model) || "F-05F".equalsIgnoreCase(model)
                || "SH-03G".equalsIgnoreCase(model) || "SO-04G".equalsIgnoreCase(model)
                || "F-01H".equalsIgnoreCase(model) || "F-01F".equalsIgnoreCase(model)
                || "SH-01F".equalsIgnoreCase(model) || "SO-01H".equalsIgnoreCase(model)
                || "SO-03G".equalsIgnoreCase(model) || "SO-05F".equalsIgnoreCase(model)
                || "F-02F".equalsIgnoreCase(model) || "SO-02H".equalsIgnoreCase(model)
                || "SH-02H".equalsIgnoreCase(model) || "SH-01H".equalsIgnoreCase(model)
                || "F-02H".equalsIgnoreCase(model) || "SO-03H".equalsIgnoreCase(model)
                || "d-01H".equalsIgnoreCase(model) || "d-02H".equalsIgnoreCase(model)
                || "DM-01H".equalsIgnoreCase(model)
                || "c6603".equalsIgnoreCase(model)
                || "D6603".equalsIgnoreCase(model)
                || "D5106".equalsIgnoreCase(model)
                || "D5803".equalsIgnoreCase(model)
                || "XT1563".equalsIgnoreCase(model)
                || "E2306".equalsIgnoreCase(model)
                || "Z850".equalsIgnoreCase(model)
                || "D6503".equalsIgnoreCase(model)
                || "MotoG3".equalsIgnoreCase(model)
                || "XT1045".equalsIgnoreCase(model)
        ) {
            sdCardPath = "/storage/sdcard1";
        }

        storageLogs.append(" Step 2: ").append(sdCardPath).append(" : ").append(usbStoragePath);



        if (AppUtils.VersionUtils.hasMarshmallow()) {
            sdCardPath = getSDCardPath(context);
        }

        if (AppUtils.VersionUtils.hasNougat() && TextUtils.isEmpty(sdCardPath)) {
            sdCardPath = getSDCardPathAboveN(context);
        }

        storageLogs.append(" Step 2.4: ").append(sdCardPath).append(" : ").append(usbStoragePath);



        storageLogs.append(" Step 3: ").append(sdCardPath).append(" : ").append(usbStoragePath);

        if ("7040T".equalsIgnoreCase(model) || MODEL_ALCATEL_ONE_TOUCH_6040A.equalsIgnoreCase(model) || MODEL_ONE_TOUCH_6012A.equalsIgnoreCase(model)) {
            sdCardPath = StorageUtils.getsdcardPath(context);
        }
        if (MANUFACTURER_SHARP.equalsIgnoreCase(manufacturer)) {
            usbStoragePath = "/internal_sd";
        }
        if ("Sony".equalsIgnoreCase(manufacturer)) {
            usbStoragePath = "/mnt/int_storage";
        }
        if ("SM-G870W".equalsIgnoreCase(model)) {
            sdCardPath = STORAGE_PATH;
        }
        if ("LG-E450B".equalsIgnoreCase(model)) {
            sdCardPath = STORAGE_EXTERNAL_SD_PATH;
        }
        if ("MotoE2(4G-LTE)".equalsIgnoreCase(model)) {
            sdCardPath = getSDCardPath(context);
        }
        if ("LG-M151".equalsIgnoreCase(Build.MODEL) || "LG-K210".equalsIgnoreCase(Build.MODEL)) {
            sdCardPath = afSdcard.getSdcardPath();
        }

        storageLogs.append(" Step 4: ").append(sdCardPath).append(" : ").append(usbStoragePath);

        if (TextUtils.isEmpty(sdCardPath)) {
            sdCardPath = getSDCardPath(context);
        }
        storageLogs.append(" Step 5: ").append(sdCardPath).append(" : ").append(usbStoragePath);

        String expandStoragePath = afSdcard.getExpandedStoragePath();

        boolean sdAdoptedAsInternal = false;
        long expandTotal = 0;
        long expandFree = 0;
        long internalTotal = 0;
        long internalFree = 0;
        long sdTotal = 0;
        long sdFree = 0;

        expandTotal = afSdcard.getExpandedStorageTotalSize();
        expandFree = afSdcard.getExpandedStorageAvailableSize();
        sdTotal = AppUtils.getMemorySize(sdCardPath, true);
        sdFree = AppUtils.getMemorySize(sdCardPath, false);

        storageLogs.append(" Step 6: ").append(sdCardPath).append(" : expandStoragePath ").append(expandStoragePath)
                .append(" expandTotal :").append(expandTotal).append(" expandFree ").append(expandFree)
                .append(" sdTotal :").append(sdTotal).append(" sdFree ").append(sdFree);


        if (internalPath.length() > 0) {
            internalTotal = getStorageCapacityMB(context);

            internalFree = AppUtils.getMemorySize(internalPath, false);
            internalFree += AppUtils.getMemorySize(usbStoragePath, false);



            builder.append("\"InternalMemoryTotal\":\"" + internalTotal + "\",");
            builder.append("\"InternalMemoryAvailable\":\"" + internalFree + "\",");
        }

        String mSDCardAdoptedInternal = "\"SDCardAdoptedAsInternal\":\"";
        String mSDCardTotal = "\"SDCardTotal\":\"";
        String mSDCardAvailable = "\"SDCardAvailable\":\"";
        if (sdTotal > 0) {
            builder.append(mSDCardAdoptedInternal + sdAdoptedAsInternal + "\",");
            builder.append("\"SDCardPath\":\"" + sdCardPath + "\",");
            builder.append(mSDCardTotal + sdTotal + "\",");
            builder.append(mSDCardAvailable + sdFree + "\",");
        } else if (expandTotal > 0) {
            sdAdoptedAsInternal = true;
            builder.append(mSDCardAdoptedInternal + sdAdoptedAsInternal + "\",");
            builder.append("\"SDCardPath\":\"" + expandStoragePath + "\",");
            builder.append(mSDCardTotal + expandTotal + "\",");
            builder.append(mSDCardAvailable + expandFree + "\",");
        } else {
            builder.append(mSDCardAdoptedInternal + sdAdoptedAsInternal + "\",");
            builder.append("\"SDCardPath\":\"\",");
            builder.append(mSDCardTotal + 0 + "\",");
            builder.append(mSDCardAvailable + 0 + "\",");
        }

        DLog.d(TAG, " getStorageInfoWRD  steps: "+storageLogs.toString() );

        return builder.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getSDCardPathAboveN(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            if(storageManager==null){
                DLog.d(TAG, "storageManager is null : ");
                return "null";

            }
            List<StorageVolume> volumeList = storageManager.getStorageVolumes();
            for (StorageVolume storageVolume : volumeList) {
                boolean removable =  storageVolume.isRemovable();
                boolean emulated =  storageVolume.isEmulated();
                boolean mounted =  false;

                DLog.d(TAG, "getSDCardPath StorageVolume  : "+storageVolume.toString());
                String state = storageVolume.getState();
                if (!TextUtils.isEmpty(state) && ("mounted".equals(state) || "mounted_ro".equals(state))) {
                    mounted = true;
                }
                if (removable && !emulated && mounted) {
                    Field privateStringField  =  StorageVolume.class.getDeclaredField("mPath");
                    privateStringField.setAccessible(true);
                    Object pathObject = privateStringField.get(storageVolume);
                    String path = "";
                    if (pathObject != null) {
                        path = pathObject.toString();
                    }
                    if (!TextUtils.isEmpty(path) && !path.toUpperCase(Locale.US).contains("USB")) {
                        return path;
                    }
                }

            }
        } catch (Exception e) {
            DLog.e(TAG, "getSDCardPath Exception : ", e);
        }
        return "";
    }


    public static String getSDCardPath(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            if(storageManager==null){
                DLog.d(TAG, "storageManager is null : ");
                return "";
            }
            Method method = storageManager.getClass().getDeclaredMethod("getVolumeList");
            method.setAccessible(true);
            Object[] volumes = (Object[]) method.invoke(storageManager);
            for (Object object : volumes) {
                Class<? extends Object> svCls = object.getClass();
                Object removableObject = AppUtils.getDeclaredFieldValue("mRemovable", svCls, object);
                boolean removable = false;
                if (removableObject != null) {
                    removable = (Boolean) removableObject;
                }
                Object emulatedObject = AppUtils.getDeclaredFieldValue("mEmulated", svCls, object);
                boolean emulated = false;
                if (emulatedObject != null) {
                    emulated = (Boolean) emulatedObject;
                }
                boolean mounted = false;
                Object state = AppUtils.getDeclaredFieldValue("mState", svCls, object);
                if (state != null && ("mounted".equals(state) || "mounted_ro".equals(state))) {
                    mounted = true;
                }
                if (removable && !emulated && mounted) {
                    Object pathObject = AppUtils.getDeclaredFieldValue("mPath", svCls, object);
                    String path = "";
                    if (pathObject != null) {
                        path = pathObject.toString();
                    }
                    if (!TextUtils.isEmpty(path) && !path.toUpperCase(Locale.US).contains("USB")) {
                        return path;
                    }
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "getSDCardPath Exception : ", e);
        }
        return "";
    }


    public static String readableFileSize(long size) {
        String readableSize = "";
        if (size <= 0) return readableSize;
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        readableSize = new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        return readableSize;
    }

    private static long getStorageCapacityMB(Context context){
        DLog.i(TAG,"In getStorageCapacityMB......");
        long capacity =0;
        String storage_Capacity = getStorageCapacityString(context);;
        storage_Capacity = storage_Capacity.trim();

        String[] splitStr = storage_Capacity.split("\\s+");
        DLog.i(TAG,"System returned storage in GB: "+splitStr[0]);
        String capacityStr = splitStr[0];
        if(capacityStr.contains("GB")) {
            DLog.i(TAG,"System returned storage in GB: contans GB");
            capacityStr = capacityStr.substring(0, capacityStr.indexOf("GB")).trim();
        }
        DLog.i(TAG,"System returned storage in GB Corrected: "+capacityStr);
        DLog.i(TAG,"System returned storage in GB Corrected: capacityStr.length() "+capacityStr.length());
        DLog.i(TAG,"System returned storage in GB Corrected: capacityStr. type is  "+capacityStr.getClass().getName());

        int index=0;
        for (char character : capacityStr.toCharArray()) {
            DLog.i(TAG,"System returned storage character : "+character+" ,  index : "+index);
            index++;
        }
        capacity = (long) Double.parseDouble(capacityStr);
        capacity = capacity *1024;
        DLog.i(TAG,"getStorageCapacityMB: "+capacity);
        return capacity;
    }

    public static long getStorageCapacityBytes(Context context){
        return getStorageCapacityMB(context) *1024 *1024;
    }

    public static String getStorageCapacityString(Context context) {
        /*String storageCapacity = "0";
        long storage = ;
        if (Build.VERSION.SDK_INT >= 26) {
            if (storage % 1000 == 0)
                storageCapacity = Formatter.formatShortFileSize(context, storage);
            else
                storageCapacity = readableFileSize(storage);
        } else {
            storageCapacity = Formatter.formatFileSize(context, storage);
        }
        String out = "";
        if(!TextUtils.isEmpty(storageCapacity)) {
            storageCapacity = storageCapacity.replaceAll(",", ".");
            out = (storageCapacity.indexOf('.') != -1 ? storageCapacity.substring(0, storageCapacity.indexOf('.'))
                    : (storageCapacity.indexOf('G') != -1 ? storageCapacity.substring(0, storageCapacity.indexOf('G')) : storageCapacity));
        } else {
            out = "00";
        }
        Log.i(TAG," getStorageCapacityString storageCapacity "+storageCapacity);*/
        return getStorageCapacityString(context,getStorageCapacity(context));
    }
    public static String getStorageCapacityString(Context context,long storage) {
        String storageCapacity = "0";
        if (Build.VERSION.SDK_INT >= 26) {
            if (storage % 1000 == 0)
                storageCapacity = Formatter.formatShortFileSize(context, storage);
            else
                storageCapacity = readableFileSize(storage);
        } else {
            storageCapacity = Formatter.formatFileSize(context, storage);
        }
        String out = "";
        if(!TextUtils.isEmpty(storageCapacity)) {
            storageCapacity = storageCapacity.replaceAll(",", ".");
            out = (storageCapacity.indexOf('.') != -1 ? storageCapacity.substring(0, storageCapacity.indexOf('.'))
                    : (storageCapacity.indexOf('G') != -1 ? storageCapacity.substring(0, storageCapacity.indexOf('G')) : storageCapacity));
        } else {
            out = "00";
        }
        DLog.i(TAG," getStorageCapacityString storageCapacity "+storageCapacity);
        return storageCapacity;
    }

    private static long getStorageCapacity(Context context) {
        long totalMem = 0l;
        if (Build.VERSION.SDK_INT >= 26) {
            DLog.i(TAG,"enter getStorageCapacity Build.VERSION.SDK_INT >= 26 case");
            try {
                if(Build.MODEL.contains("SM-G950F") || Build.MODEL.contains("SM-G955F") ||
                        Build.MODEL.contains("SM-J600G") ||
                        Build.MODEL.contains("SM-J600F") ||
                        Build.MODEL.contains("SM-J600G") ||
                        Build.MODEL.contains("SM-J600FN") ||
                        Build.MODEL.contains("SM-J600GF") ||
                        Build.MODEL.contains("SM-J600GT") ||
                        Build.MODEL.contains("SM-J600L"))
                {
                    totalMem = 64 * BaseUtils.MB_IN_BYTES;
                } else {
                    totalMem = getStorageCapacityForOreoDevices(context);
                }
            } catch (Exception ex) {
                ex.getMessage();
                DLog.e(TAG, "getInfo: 4 Exception " + ex.getMessage());
            }
        } else {
            long systemBlocks = getBlocks("/system");
            long cacheBlocks = getBlocks("/cache");
            long dataBlocks = getBlocks("/data");
            long totalBytes = (systemBlocks + cacheBlocks + dataBlocks) * getBlockSize();
            long ceil = 1;
            while (ceil < totalBytes) {
                ceil *= 2;
            }
            if (ceil > 1) {
                totalBytes = ceil;
            }
            totalMem = totalBytes;
        }


        DLog.i(TAG,"getStorageCapacity totalMem "+totalMem);

        return totalMem;
    }

    public int getStorageCapacityinHeighstUnits(Context context) {
        String storage = getStorageCapacityString(context);
        int storageNumber = 0;
        if(!TextUtils.isEmpty(storage)) {
            storage = storage.replaceAll(",", ".");
            String storageString = (storage.indexOf('.') != -1 ? storage.substring(0, storage.indexOf('.'))
                    : (storage.indexOf('G') != -1 ? storage.substring(0, storage.indexOf('G')) : storage));
            storageNumber = Integer.valueOf(storageString.trim());
        } else {
            storageNumber = 0;
        }
        return storageNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getStorageCapacityForOreoDevices(Context ctx) {
        StorageStatsManager storageStatsManager = (StorageStatsManager) ctx.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) ctx.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
        long capacity = 0l;

        StringBuilder storageCap = new StringBuilder();
        storageCap.append("MODEL:   ").append(Build.MODEL).append( " ");
        storageCap.append(" UUID_DEFAULT:   ").append(StorageManager.UUID_DEFAULT).append( " ");
        DLog.d(TAG,storageCap.toString());

        for (StorageVolume storageVolume : storageVolumes) {
            String uuidStr = storageVolume.getUuid();
            storageCap.append("uuidStr ").append(uuidStr).append( " ");

            UUID uuid;
            try{
                uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
            }catch (IllegalArgumentException ex){
                uuid = StorageManager.UUID_DEFAULT;
            }

            storageCap.append(" uuid ").append(uuid);
            try {
                capacity = storageStatsManager.getTotalBytes(uuid);
            } catch (Exception e) {
                storageCap.append(" Exception ").append(e.getMessage());
                e.printStackTrace();
            }
        }

        DLog.d(TAG,storageCap.toString());

        return capacity;
    }


    private static long getBlocks(String folder) {
        long blocks = 0;
        try {
            StatFs stat = new StatFs(folder);
            blocks = stat.getBlockCount();
        } catch (Exception e) {
            DLog.e(TAG, "getBlocks: Exception " + e);
        }
        return blocks;
    }

    private static long getBlockSize() {
        long blockSize = 1;
        try {
            StatFs stat = new StatFs("/system");
            blockSize = stat.getBlockSize();
        } catch (Exception e) {
            DLog.e(TAG, "getBlockSize: Exceptoin" + e);
        }
        return blockSize;
    }


    private static String getAbsolutePath(String mPath) {
        String newPath = "";
        try {
            File file = new File(mPath);
            if (!file.getAbsolutePath().equals(
                    file.getCanonicalPath())) {
                newPath = file.getCanonicalPath();
            }
        } catch (Exception e) {
            DLog.e(TAG, "Exception in getting path ", e);
        }
        return newPath;
    }



   private static String getMediaPathType(String mediaPath) {
        String type = "";
        if (mediaPath.endsWith("mmc_host")) {
            try {
                File mmcHost = new File(mediaPath);
                if (mmcHost.exists() && mmcHost.isDirectory()) {
                    File[] mmcFolders = mmcHost.listFiles();
                    String mmcSubFolder = "";
                    int mmcSubfoldersCount = 0;
                    for (int i = 0; i < mmcFolders.length; i++) {
                        String subFolderName = mmcFolders[i].getName();
                        if (mmcFolders[i].isDirectory()
                                && subFolderName.startsWith("mmc")) {
                            mmcSubFolder = subFolderName;
                            mmcSubfoldersCount++;
                        }
                    }
                    if (mmcSubfoldersCount == 1) {
                        mediaPath += "/" + mmcSubFolder;
                    }
                }
            } catch (Exception ex) {
                DLog.e(TAG, "getMediaPathType Exception 1 : ", ex);
            }
        }
        try {
            File mmc = new File(mediaPath);
            if (!mmc.exists()) {
                int lastSlash = mediaPath.lastIndexOf('/');
                if (lastSlash >= 0) {
                    mediaPath = mediaPath.substring(0, lastSlash);
                }
                mmc = new File(mediaPath);
            }
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
        } catch (Exception e) {
            DLog.e(TAG, "getMediaPathType Exception 2: ", e);
        }
        return type;
    }


   private static StringBuilder getFileContents(File typeFile) {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = null;
        BufferedReader in = null;
        try {
            fileReader = new FileReader(typeFile);
            in = new BufferedReader(fileReader);
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception ex) {
            DLog.e(TAG, "getFileContents Exception : ", ex);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception in fileReader closing: ", e);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                DLog.e(TAG, "Exception in in closing: ", e);
            }
        }
        return sb;
    }


    /*SSD ------------------- */

    // TODO: 18/12/20 commented on 17 DEC
    /*public boolean checkSDCard(Context context) {
        boolean success = false;
        String sdCardPath = getSDCardPathFromDB(context);
        *//*
         * String sdCardPath = getSDCardPath(context); if(sdCardPath.length()==0
         * && android.os.Build.VERSION.SDK_INT >=
         * android.os.Build.VERSION_CODES.GINGERBREAD) { sdCardPath =
         * getSDCardPathFromStorageManager(context); }
         *//*
        Log.i(TAG, "sd path: " + sdCardPath);
        if (sdCardPath != null && sdCardPath.length() > 0) {
            boolean status = writeFile(sdCardPath);
            if (status) {
                success = true;
                status = readFile(sdCardPath);
                if (!status) {
                    success = false;
                }
                status = deleteFile(sdCardPath);
                if (!status) {
                    success = false;
                }
            } else {
                success = false;
            }
        }
        return success;
    }

    private String getSDCardPathFromDB(Context context) {

        try {
            SharedPreferences settings = context.getSharedPreferences(DeviceInfo.PREFS_FILE_NAME, Context.MODE_PRIVATE);
            return settings.getString(DeviceInfo.PREF_SDCARD_PATH, null);
        } catch (Exception e) {
        }
        return null;
    }*/

    public boolean checkInternalStorage(Context context) {
        boolean success = false;
        String path = context.getFilesDir().getAbsolutePath();
        DLog.i(TAG, "files path: " + path);
        if (path != null && path.length() > 0) {
            boolean status = writeFile(path);
            if (status) {
                success = true;
                status = readFile(path);
                if (!status) {
                    success = false;
                }
                status = deleteFile(path);
                if (!status) {
                    success = false;
                }
            } else {
                success = false;
            }
        }
        return success;
    }

    private boolean writeFile(String path) {
        try {
            File testFile = new File(path + "/" + TEST_FILE_NAME);
            if (!testFile.exists()) {
                testFile.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(testFile));
            bufferedWriter.write(TEST_FILE_CONTENT);
            bufferedWriter.close();
            DLog.i(TAG, "written data to " + testFile.getPath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean readFile(String path) {
        try {
            File testFile = new File(path + "/" + TEST_FILE_NAME);
            BufferedReader in = new BufferedReader(new FileReader(testFile));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            DLog.i(TAG, "Read data from " + testFile.getPath() + ":" + sb.toString());
            if (sb.toString().trim().equals(TEST_FILE_CONTENT)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteFile(String path) {
        try {
            File testFile = new File(path + "/" + TEST_FILE_NAME);
            testFile.delete();
            DLog.i(TAG, "Deleted file " + testFile.getPath());
            if (!testFile.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    private String getVolumeState(Context context, String mountPoint) {
        DLog.i("PVA", "getVolumeState()");
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class c = Class.forName(sm.getClass().getName());
            Field fld = c.getDeclaredField("mMountService");
            fld.setAccessible(true);
            IMountService mountService = (IMountService) fld.get(sm);
            String state = mountService.getVolumeState(mountPoint);
            DLog.i("PVA", mountPoint + " state: " + state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String parseKeyInPairs(String content, String key) {
        try {
            String pattern = key + "=";
            int idx = content.indexOf(pattern);
            if (idx >= 0) {
                String value = content.substring(idx + pattern.length());
                int endIdxComma = value.indexOf(',');
                int endIdxRightBrace = value.indexOf(']');
                int endIdx = -1;
                if (endIdxComma >= 0 && endIdxRightBrace >= 0) {
                    if (endIdxComma < endIdxRightBrace) {
                        endIdx = endIdxComma;
                    } else {
                        endIdx = endIdxRightBrace;
                    }
                } else if (endIdxComma >= 0) {
                    endIdx = endIdxComma;
                } else if (endIdxRightBrace >= 0) {
                    endIdx = endIdxRightBrace;
                }
                if (endIdx >= 0) {
                    value = value.substring(0, endIdx);
                }
                return value;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private long getMemorySize(String pathName, boolean total) {
        try {
            File path = new File(pathName);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            if (total) {
                return stat.getBlockCount() * blockSize / 1048576L;
            } else {
                return stat.getAvailableBlocks() * blockSize / 1048576L;
            }
        } catch (Exception e) {
        }
        return 0;
    }




    public String getSDCardPathFromStorageManager(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumeListsMethod = StorageManager.class.getDeclaredMethod("getVolumeList");
            if (getVolumeListsMethod != null) {
                DLog.i(TAG, "getVolumeListsMethod is no null");
                Object[] volumes = (Object[]) getVolumeListsMethod.invoke(storageManager);
                if (volumes != null) {
                    DLog.i(TAG, "volumes are not null");
                    for (int i = 0; i < volumes.length; i++) {
                        DLog.i(TAG, "volume" + (i + 1) + ": " + volumes[i].toString());
                        String path = parseKeyInPairs(volumes[i].toString(), "mPath");
                        if (path != null && path.length() > 0) {
                            String removable = parseKeyInPairs(volumes[i].toString(), "mRemovable");
                            String emulated = parseKeyInPairs(volumes[i].toString(), "mEmulated");
                            if ("true".equalsIgnoreCase(removable) && !"true".equalsIgnoreCase(emulated)) {
                                String subSystem = parseKeyInPairs(volumes[i].toString(), "mSubSystem");
                                if ("sd".equalsIgnoreCase(subSystem)) {
                                    return path;
                                }
                                if (subSystem != null && subSystem.length() > 0 && !"sd".equalsIgnoreCase(subSystem)) {
                                    continue;
                                }
                                /*
                                 * String description =
                                 * parseKeyInPairs(volumes[i].toString(),
                                 * "mDescription"); if(description != null &&
                                 * description.startsWith("SD")) {//Observed in
                                 * Sharp devices as 'SD Card', but, it is
                                 * language dependent return path; }
                                 */
                                String state = parseKeyInPairs(volumes[i].toString(), "mState");
                                if (state == null || state.length() == 0) {
                                    state = getVolumeState(context, path);
                                }
                                if ("mounted".equals(state) || "mounted_ro".equals(state)) {
                                    long sdTotal = getMemorySize(path, true);
                                    if (sdTotal > 0) {
                                        return path;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    DLog.i(TAG, "volumes are null");
                }
            } else {
                DLog.i(TAG, "getVolumeListsMethod is null");
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static String encodeIcon(Drawable drawableImage) {
        Drawable drawable = drawableImage;
        String strImage = null;
        Bitmap bitmap ;
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            } else if (drawable instanceof LayerDrawable
                    || (AppUtils.VersionUtils.hasLollipop() && drawable instanceof VectorDrawable)) {
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            } else {
                BitmapDrawable bitDw = ((BitmapDrawable) drawable);
                bitmap = bitDw.getBitmap();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(OruApplication.getAppContext().getResources().getString(R.string.icon_quality)), stream);
            byte[] bitmapByte = stream.toByteArray();
            strImage = Base64.encodeToString(bitmapByte, 0);
        }
        return strImage;
    }


    public static boolean saveSummaryFileToStorageWireless(Context context, String fileData) {
        FileOutputStream fos = null;
        String statusString = "fail";
        boolean saveStatus = false;
        try {
            byte[] decodedData = Base64.decode(fileData, Base64.DEFAULT);
            String outputPath = "" + Environment.getExternalStorageDirectory() + File.separator + BuildConfig.APPLICATION_ID;
            String outputFilePath = outputPath + "/" + "summaryImage"+System.currentTimeMillis()+".jpg";
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File externalFile = new File(outputFilePath);
            fos = new FileOutputStream(externalFile.getPath());
            fos.write(decodedData);
            refreshAndroidGallery(context, Uri.fromFile(externalFile));
            statusString = "success";
            saveStatus = true;
        } catch (Exception e) {
            DLog.e(TAG, "Exception in photoCallback", e);
            saveStatus = false;
        } finally {
            DLog.e(TAG,"&EventName=SaveSummaryFile="+ "&Status=" + statusString);
            if (fos != null) {
                try { fos.close(); } catch (IOException e) { DLog.e("Avdhoot", "Exception in close", e); }
            }
            return saveStatus;
        }
    }

    /**
     * Refreshing the android gallery after the file is saved Or deleted.
     *
     * @param context
     * @param fileUri
     */
    public static void refreshAndroidGallery(Context context, Uri fileUri) {
        try {
            if (fileUri != null) {
                Context mContext = context.getApplicationContext();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(fileUri);
                    mContext.sendBroadcast(mediaScanIntent);
                } else {
                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                }
            }

        } catch (Exception e) {
            DLog.e(TAG, "Exception in Refreshing Android Gallery: " + e.getMessage());
        }
    }

}
