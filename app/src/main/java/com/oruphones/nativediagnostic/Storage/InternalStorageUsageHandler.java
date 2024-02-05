package com.oruphones.nativediagnostic.Storage;

/**
 * Created by Pervacio on 24-08-2017.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.AppDetails;
import com.oruphones.nativediagnostic.api.AppInfo;
import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.api.PDAppsInfo;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.util.AppResolutionPojo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.LPUtil;
import com.oruphones.nativediagnostic.util.StorageUtil;
import com.oruphones.nativediagnostic.util.Util;


import org.pervacio.onediaglib.internalstorage.FileData;
import org.pervacio.onediaglib.internalstorage.FileVO;
import org.pervacio.onediaglib.internalstorage.MD5;
import org.pervacio.onediaglib.utils.PackageInfoHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class InternalStorageUsageHandler {
    private static String TAG = InternalStorageUsageHandler.class.getSimpleName();
    public int totalVideoCount;
    public int totalAudioCount;
    public int totalImageCount;
    public int totalDupllicateFiles;
    public boolean AppSizeMapDone = false;
    private Set<String> addWareAppsList = new HashSet<String>();
    private Set<String> bandWidthconsumingAppsList = new HashSet<String>();
    private Set<String> riskyAppsList = new HashSet<String>();
    private Set<String> batteryConsumingApps = new HashSet<String>();
    private HashSet<String> whileListPkgs = new HashSet<String>();
    private HashSet<String> dangerousPermissionsLevel1 = new HashSet<String>();
    private HashSet<String> dangerousPermissionsLevel2 = new HashSet<String>();
    private HashSet<String> dangerousPermissionsLevel3 = new HashSet<String>();

    final String MIMETYPE_AUDIO = "audio";
    final String MIMETYPE_VIDEO = "video";
    final String MIMETYPE_IMAGE = "image";
    String fileName = "storagedetails.xml";
    String appsFileName = "installedapps.xml";
    String filePath = "/data/local/tmp/pva/";
    FileOutputStream fOut = null;
    File myFile = null;
    OutputStreamWriter myOutWriter = null;
    long applicationSize;
    long appCacheSize;
    long musicSize;
    long imageSize;
    long videoSize;
    long otherSize;
    boolean sizeDone = false;
    int calbackCount = 0;
    int numCallbacks = 0;
    private Map<Integer, List<FileVO>> duplicateFilesMap;
    private Map<Long, List<FileVO>> duplicateFilesMap2;
    private Map<Long, List<FileVO>> map;
    private int group;
    private int totalFileNo;
    private String internalStoragePath;
    private ArrayList<FileData> fileMusicList = new ArrayList<FileData>();
    private ArrayList<FileData> fileVideoList = new ArrayList<FileData>();
    private ArrayList<FileData> fileImageList = new ArrayList<FileData>();
    private ArrayList<FileData> fileOtherList = new ArrayList<FileData>();
    //private ArrayList<ApplicationData> appsList = new ArrayList<ApplicationData>();
    private HashMap<String, ApplicationData> appInfoMap = new HashMap<String, ApplicationData>();
    private HashMap<String, AppSize> appSizeMap = new HashMap<String, AppSize>();
    /* Akhilesh 05/12/2016 25722 begin >*/
    private ArrayList<String> musicfileExtension = new ArrayList<String>(Arrays.asList("mp3", "flac", "3ga", "zab", "cda", "arf", "wpl", "xspf", "avr", "sesx", "mpdp", "trm", "aa", "gp5", "ocdf", "bnk", "rec", "xwm",
            "mus", "moi", "aax", "ct3", "cs3", "dss", "wem", "mv3", "nwc", "nvf", "wv", "ca3", "ds2", "amr", "sib", "tsi", "xkr", "fsb", "ajp", "dvf", "nmf", "zvr", "m4a", "ram", "adts",
            "wrf", "alb", "wav", "cdfs", "oma", "aaf", "audionote", "sng", "ad4", "dcf", "br5", "fls", "asx", "vdj", "ses", "ytif", "aac", "2ch", "ove", "mka", "2", "wma", "nmsv", "mp4a",
            "elastik", "au", "caf", "br4", "pcm", "mgu", "m4r", "cdg", "vox", "vpl", "nki", "dkd", "mogg", "spx", "bmw", "thd", "i3pack", "voc", "ap4", "muk", "snd", "stem.mp4",
            "sdif", "ogg", "midi", "rpp", "ulaw", "kux", "gp4", "efa", "rns", "uax", "xwb", "kam", "sf2", "mtd", "gtp", "m4p", "kfn", "omf", "gog", "sdx", "tak", "mxl", "pbf", "aud", "svq",
            "rx2", "gpx", "sf", "band", "sgu", "sabs", "gig", "w02", "rip", "ngrr", "m4b", "kar", "mx6", "nsmp", "wax", "asf", "seq", "swa", "dlp", "mx5", "aif", "smf", "vsb", "rtm", "mmp",
            "sps", "sabl", "hma", "xpf", "abk", "ra", "shn", "rms", "logic", "tl", "cdo", "rfl", "vm", "aob", "acd", "adg", "sts", "h2p", "gpbank", "vm1", "dtshd", "cwp", "aiff"));
    private ArrayList<String> imagefileExtension = new ArrayList<String>(Arrays.asList("png", "jpg", "jpeg", "jfif", "jpeg 2000", "exif", "tiff", "gif", "bmp"));
    private ArrayList<String> videofileExtension = new ArrayList<String>(Arrays.asList("avi", "mp4", "dav", "mov", "arf", "mkv", "avc", "exo", "fbr", "dash", "flv", "3gp", "mks", "m4v", "3gpp", "mvc", "ogm", "mpeg4", "mpeg2", "mpeg1"));
    /* Akhilesh 05/12/2016 25722 end >*/
    private int numLookups;
    private int numLoadedPackages;
    private DuplicateFilesInfo duplicateFilesInfo;
    Resolution resolution;
    Context context;
    public InternalStorageUsageHandler(String filePath) {
        this.map = new TreeMap();
        this.duplicateFilesMap = new HashMap();
        this.duplicateFilesMap2 = new HashMap();
        this.totalFileNo = 0;
        this.group = 0;
        this.internalStoragePath = filePath;
        resolution = Resolution.getInstance();
    }

    public ArrayList<String> getPreInstalledAppInSony(Context context) {
        this.context = context;
        try {
            ArrayList<String> preInstalledApps = new ArrayList<String>();
            InputStream inputStream = context.getAssets().open(
                    "sony_preinstalled_apps.txt");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                preInstalledApps.add(line);
            }
            bufferedReader.close();
            return preInstalledApps;
        } catch (Exception e) {
        }
        return null;
    }

    public ArrayList<String> getBellWhitelistApps(Context context) {
        try {
            ArrayList<String> bellWhitelistApps = new ArrayList<String>();
            InputStream inputStream = context.getAssets().open(
                    "bell_whitelist_apps.txt");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                bellWhitelistApps.add(line);
            }
            bufferedReader.close();
            return bellWhitelistApps;
        } catch (Exception e) {
        }
        return null;
    }

    public boolean isbellWhitelistApp(Context context,
                                      String packageName) {
        ArrayList<String> bellWhitelistApps = getBellWhitelistApps(context);
        if (bellWhitelistApps != null && bellWhitelistApps.contains(packageName)) {
            return true;
        }
        return false;
    }

    public boolean isPreInstalledAppInSony(Context context,
                                           String packageName) {
        ArrayList<String> preInstalledApps = getPreInstalledAppInSony(context);
        if (preInstalledApps != null && preInstalledApps.contains(packageName)) {
            return true;
        }
        return false;
    }

    public boolean isSystemApp(Context context, String packageName) {
        try {
            return PackageInfoHelper.getInstance(context).isSystemApp(packageName);
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentTimeStamp() {
        try {

            Date date = new Date();
            return date.getTime() + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String encodeIcon(Drawable icon) {
        Drawable ic = icon;
        String img_str = null;
        if (ic != null) {
            BitmapDrawable bitDw = ((BitmapDrawable) ic);
            Bitmap bitmap = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
            byte[] bitmapByte = stream.toByteArray();
            img_str = Base64.encodeToString(bitmapByte, 0);
//			Log.i(TAG, "img_str : " + img_str);
        }
        return img_str;

    }

    public Map<Integer, List<FileVO>> findDuplicate() {
        DLog.d(TAG, "findDuplicate.......");
        populateSameSizedFiles(internalStoragePath);
        compareAndFindDuplicates();
        duplicateFilesInfo = getDuplicateFilesInfo();
        return this.duplicateFilesMap;
    }

    private void getInstalledApps(Context context) {
        DLog.d(TAG, "getInstalledApps................");
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packs = pm.getInstalledPackages(0);
            try {
                LPUtil lpUtil = new LPUtil();
                HashMap<String, Long> rcntAppsMap = null;
                HashMap<String, Long> rcntAppsMapFromScratch = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        rcntAppsMap = new LPUtil().getRecentlyUsedAppsWithTimestamp(context);
                        rcntAppsMapFromScratch = new LPUtil().getRecentlyUsedAppsFromScratch(context);
                    } catch (Exception ex) {
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (lpUtil.usageAccessGranted(context)) { // Check for permission for lollipip
                        batteryConsumingApps = lpUtil.getForeGroundAppBatteryConsuming(context);
                    }
                } else {
                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> list = am
                            .getRunningTasks(100);
                    for (int i = 0; i < list.size(); i++) {
                        batteryConsumingApps.add(list.get(i).baseActivity.getPackageName());
                    }
                }


                ApplicationInfo appInfo;
                ArrayList<PDAppsInfo> pdAppsInfos = new ArrayList<PDAppsInfo>();
                ArrayList<AppDetails> appDetailsList = new ArrayList<AppDetails>();
                try {
                    for (int i = 0; i < packs.size(); i++) {

                        PDAppsInfo pdAppsInfo = new PDAppsInfo();
                        ApplicationData applicationData = new ApplicationData();
                        AppResolutionPojo appResPojo = new AppResolutionPojo();
                        PackageInfo p = packs.get(i);
                        ApplicationInfo ai = p.applicationInfo;
                        String packageName = p.packageName;

                        // String type = "system";//commented for 5538 issue
                        String type = isSystemApp(context, ai.packageName) ? "system" : "user";
                        if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            type = "user";
                        }
                        applicationData.setAppName(p.applicationInfo.loadLabel(pm).toString());
                        applicationData.setPkgName(packageName);
                        applicationData.setVersion(p.versionName);
                        applicationData.setInstaller(pm.getInstallerPackageName(packageName));
                        if (rcntAppsMapFromScratch != null) {
                            boolean used = rcntAppsMapFromScratch.containsKey(packageName);
                            if (used) {
                                try {
                                    pdAppsInfo.setRcutimestamp(rcntAppsMapFromScratch.get(packageName).longValue());
                                } catch (Exception ex1) {
                                }
                            }

                        }
                        applicationData.setType(type);
                        double sizeApp = 0;
                        try {
                            if (!(OruApplication.getAppContext().getPackageName()).equalsIgnoreCase(packageName) && type.equalsIgnoreCase("user")) {
                                pdAppsInfo.setAppName(utf8(p.applicationInfo.loadLabel(pm).toString()));
                                pdAppsInfo.setPackageName(packageName);

                                pdAppsInfo.setVersion(p.versionName);
                                pdAppsInfo.setInstaller(pm.getInstallerPackageName(packageName));
                                if (rcntAppsMap != null) {
                                    //  boolean used = rcntAppsMap.containsKey(packageName);

                                    //  pdAppsInfo.setLastUsed(rcntAppsMap.get(packageName));
                                }

                              /*  if (rcntAppsMapFromScratch != null) {

                                    boolean used = rcntAppsMapFromScratch.containsKey(packageName);
                                    if (used) {

                                        try {
                                            pdAppsInfo.setRcutimestamp(rcntAppsMapFromScratch.get(packageName).longValue() );
                                        } catch (Exception ex1) {
                                        }
                                    }

                                }*/

//                                pdAppsInfo.setAppType(type);

                                String md5Digest = "";
                                try {
                                    if (type.equalsIgnoreCase("user")) {
                                        File file = new File(ai.sourceDir);
                                        md5Digest = MD5.calculateMD5(file);
//                                        pdAppsInfo.setMd5Digest(md5Digest);
                                    }
                                } catch (Exception ex1) {
                                    //ex1.printStackTrace();
                                }
                                if (type != null && !type.equals("system")) {
                                    if (ai.sourceDir != null) {
                                        try {
                                            pdAppsInfo.setSize(0);//setting defalut size as zero
                                            File file = new File(ai.sourceDir);

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

//                                                pdAppsInfo.setInstalledDate(p.firstInstallTime);

                                                pdAppsInfo.setUpdatedDate(p.lastUpdateTime);
                                            } else {
                                                long lastModified = file.lastModified();
//                                                pdAppsInfo.setInstalledDate(lastModified);
                                                pdAppsInfo.setUpdatedDate(lastModified);
                                            }

                                        } catch (Exception e) {
                                            //e.printStackTrace();

                                        }
                                    }
                                } else {
                                    pdAppsInfo.setSize(0); // Zero for System Apps
                                }

                                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                                    PackageInfo packageInfo = pm.getPackageInfo(ai.packageName, PackageManager.GET_PERMISSIONS);
                                    ArrayList<String> dPerms = new ArrayList<String>();
                                    String[] reqPerms = packageInfo.requestedPermissions;
                                    if (reqPerms != null) {
                                        for (int j = 0; j < reqPerms.length; j++) {
                                            try {
                                                PermissionInfo permInfo = pm.getPermissionInfo(reqPerms[j],
                                                        PackageManager.GET_META_DATA);
                                                if (permInfo.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) {
                                                    dPerms.add(reqPerms[j]);
                                                }
                                            } catch (Exception ex1) {

                                            }
                                        }
                                    }
                                    if (dPerms.size() > 0) {
                                        ArrayList<String> permisionList = new ArrayList<>();
                                        ArrayList<String> permisionLevelList = new ArrayList<>();
                                        for (int j = 0; j < dPerms.size(); j++) {
                                            permisionList.add(dPerms.get(j));
                                            permisionLevelList.add("dangerous");
                                        }
                                        //  pdAppsInfo.setPermision(permisionList);
                                        //  pdAppsInfo.setPermisionLevel(permisionLevelList);
                                    }
                                }
                                PackageInfo packageInfo = null;
                                try {
                                    packageInfo = pm.getPackageInfo(
                                            ai.packageName, PackageManager.GET_PERMISSIONS);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<String> dPerms = new ArrayList<String>();
                                String[] reqPerms = packageInfo.requestedPermissions;
                                boolean bandwidthconsumingapp = false;
                                if (reqPerms != null/* && selectedAutoTestList.contains(PDConstants.BANDWIDTHAPPS)*/) {
                                    for (int j = 0; j < reqPerms.length; j++) {
                                        try {
                                            PermissionInfo permInfo = pm
                                                    .getPermissionInfo(reqPerms[j],
                                                            PackageManager.GET_META_DATA);
                                            if (permInfo.name
                                                    .equalsIgnoreCase("android.permission.INTERNET")) {
                                                bandWidthconsumingAppsList
                                                        .add(packageInfo.packageName);
                                                bandwidthconsumingapp = true;
                                            }
                                        } catch (Exception ex1) {

                                        }
                                    }
                                }
                                //fOut.write(("\t\t<bandwidthconsumingapp>" + utf8(bandwidthconsumingapp + "") + "</bandwidthconsumingapp>\n").getBytes());
                                //  pdAppsInfo.setBandwidthconsumingapp(bandwidthconsumingapp ? PDConstants.PDTRUE : PDConstants.PDFALSE);
                                applicationData.setBandwidthconsumingapp(bandwidthconsumingapp);
                                boolean btryConsumingApps = false;
                                if (batteryConsumingApps.contains(packageName) /*&& selectedAutoTestList.contains(PDConstants.BATTERYCONSUMINGAPPS)*/) {
                                    btryConsumingApps = true;
                                }
                                //  pdAppsInfo.setBatteryconsumingapps(btryConsumingApps ? PDConstants.PDTRUE : PDConstants.PDFALSE);
                                applicationData.setBtryConsumingApps(btryConsumingApps);
                                if (!type.equalsIgnoreCase("SYSTEM")) {
                                    pdAppsInfos.add(pdAppsInfo);
                                }
                                AppDetails appDetails = new AppDetails();
                                appDetails.setPackageName(pdAppsInfo.getPackageName());
                                appDetails.setMd5Digest(md5Digest);
                                appDetailsList.add(appDetails);
                                appInfoMap.put(packageName, applicationData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<AppDetails> appDetailsListFromServer = PervacioTest.getInstance().getMalwareRiskyApps(context, appDetailsList);
                while (!AppSizeMapDone) {
                    Thread.sleep(50);
                }
                ArrayList<PDAppsInfo> pdAppsInfoList = new ArrayList<PDAppsInfo>();
                for (AppDetails appDetails : appDetailsListFromServer) {
                    for (PDAppsInfo pdAppsInfo :  pdAppsInfos) {
                        if (pdAppsInfo.getPackageName().equalsIgnoreCase(appDetails.getPackageName())) {
                            pdAppsInfo.setMalware(appDetails.isMalware());
                            pdAppsInfo.setAddware(appDetails.isAddware());
                            pdAppsInfo.setRiskyapp(appDetails.isRiskyapp());
//                            pdAppsInfo.setMalware(true);
//                            pdAppsInfo.setAddware(true);
//                            pdAppsInfo.setRiskyapp(true);
                            if(appDetails.isMalware())
                            Resolution.getInstance().setMalwareFound(appDetails.isMalware());
                            if(appDetails.isAddware())
                            Resolution.getInstance().setAdwareFound(appDetails.isAddware());
                            if(appDetails.isRiskyapp())
                            Resolution.getInstance().setRiskyFound(appDetails.isRiskyapp());

                            pdAppsInfo.setJustification(appDetails.getJustification());
                            double sizeApp = 0;
                            try {
                                AppSize appSize = appSizeMap.get(pdAppsInfo.getPackageName());
                                if (appSize != null)
                                    sizeApp = appSize.codeSize + appSize.getDataSize() + appSize.getCacheSize();
                            } catch (Exception e) {
                                e.printStackTrace();
                                sizeApp = 0;
                            }
                            pdAppsInfo.setSize((long) Util.BtoKB(sizeApp).longValue());
                            Drawable appIcon = null;
                            try {
                                appIcon = context.getApplicationContext().getPackageManager().getApplicationIcon(pdAppsInfo.getPackageName());
                            } catch (Exception e1) {
                                //Log.i(TAG, "Exception = " + e1.getMessage());
                            }
                            pdAppsInfo.setIcon(StorageUtil.encodeIcon(appIcon));
                            AppInfo info = getAppInfoFromPDAppInfo(pdAppsInfo);
                            info.setAppIcon(appIcon);
                            if(pdAppsInfo.isMalware())
                                Resolution.getInstance().getMalwareAppsList().add(info);
                            if(pdAppsInfo.isAddware())
                                Resolution.getInstance().getAddwareAppsList().add(info);
                            if(pdAppsInfo.isRiskyapp())
                                Resolution.getInstance().getRiskyAppsList().add(info);
                            pdAppsInfoList.add(pdAppsInfo);
                        }
                    }
                }
                resolution.setAppsInfoList(pdAppsInfoList);
                resolution.setAppResolutionDone(true);
            } catch (Exception e) {
                DLog.w(TAG, "App info Exception:" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            DLog.e(TAG, "Exception: " + e.toString());
            // LogUtil.logEvent("&EventName=InstalledApps&Status=Failed");
        }
    }

    private AppInfo getAppInfoFromPDAppInfo(PDAppsInfo pdAppsInfo) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pdAppsInfo.getAppName());
        appInfo.setAppSizeKB(pdAppsInfo.getSize() + "");
        appInfo.setPackageName(pdAppsInfo.getPackageName());
        return appInfo;
    }

    private void prepareAppSizeMap(Context context) {
        DLog.d(TAG, "prepareAppSizeMap................");
        boolean invocationException = false;
        PackageManager pm = context.getPackageManager();
        Method getPackageSizeInfo = null;
        try {
            getPackageSizeInfo = pm.getClass()
                    .getMethod("getPackageSizeInfo", String.class,
                            IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            DLog.e(TAG, "Exception in prepareAppSizeMap: " + e.getMessage());

        }
        List<PackageInfo> installedPackages = pm
                .getInstalledPackages(0);
        DLog.d(TAG, "installedPackages size................" + installedPackages.size());
        int i = 0;
        numCallbacks = installedPackages.size();
        for (final PackageInfo info : installedPackages) {
            //Log.d("prem", "processing package; "+ (++i) );
            if (info.applicationInfo == null) {
                //Log.d("diskusage", "inside Apps2SDLoader No applicationInfo");
                calbackCount++;
                appSizeMap.put(info.packageName, new AppSize(0, 0, 0));
                continue;
            }
            int flag = 0x40000; // ApplicationInfo.FLAG_EXTERNAL_STORAGE
            // boolean on_sdcard = (info.applicationInfo.flags & flag) != 0;
            // if (on_sdcard) {
            //changeNumLookups(1);
            final String pkg = info.packageName;
            final String name = pm.getApplicationLabel(info.applicationInfo)
                    .toString();
            //Log.d("prem", "processing package name; "+ pkg );
            sizeDone = false;
            try {
                getPackageSizeInfo.invoke(pm, pkg,
                        new IPackageStatsObserver.Stub() {
                            @Override
                            synchronized public void onGetStatsCompleted(PackageStats pStats,
                                                                         boolean succeeded) throws RemoteException {
                                //synchronized (InternalStorageUsageHandler.this) {
                                //numLoadedPackages++;
                                //changeNumLookups(-1);
                                calbackCount++;
                                if (succeeded) {
                                    appSizeMap.put(pStats.packageName, new AppSize(pStats.codeSize,
                                            pStats.dataSize, pStats.cacheSize));

                                    //applicationSize += pStats.codeSize + pStats.dataSize;
                                    //+ pStats.cacheSize;
                                    //appCacheSize += pStats.cacheSize;
                                } else {
                                    appSizeMap.put(pStats.packageName, new AppSize(0, 0, 0));
                                }
                                //Log.d("prem", pStats.packageName + " size===" + appSizeMap.get(pStats.packageName).getCodeSize());
                                if (calbackCount >= numCallbacks)
                                    AppSizeMapDone = true;
                                //InternalStorageUsageHandler.this.notify();
                            }
                            //}
                        });
            } catch (IllegalAccessException e) {
                DLog.e(TAG, "Exception in prepareAppSizeMap2: " + e.getMessage());
            } catch (InvocationTargetException e) {
                invocationException = true;
                DLog.e(TAG, "Exception in prepareAppSizeMap3: " + e.getMessage());
            }
            // }
            /*
             * else { synchronized (this) { numLoadedPackages++; } }
			 */
/*			try {
                Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
/*			while(!sizeDone){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}*/
        }
        DLog.d(TAG, "prepareAppSizeMap Completed................");
        if(invocationException)
           AppSizeMapDone = true;
    }

    public void getAppsSizeInfo(final Context context, boolean logCacheSize)
            throws Throwable {
        DLog.d(TAG, "getAppsSizeInfo................" + logCacheSize);
        new Thread() {
            @Override
            public void run() {
                try {
                    prepareAppSizeMap(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
/*        Thread findDuplicate = new Thread() {
            @Override
            public void run() {
                try {
                    findDuplicate();
                } catch (Exception e) {
                }
            }
        };
        findDuplicate.start();*/
        while (true) {
            synchronized (this) {
                if (numLookups != 0) {
                    wait();
                } else {
                    DLog.d(TAG, "before completed!!!!");
                    if (logCacheSize == true) {
                        //LogUtil.logEvent("&EventName=AppsCacheSize&Status=Success&Size=" +appCacheSize );
                        //Log.d(TAG, "Total AppCacheSize : "+appCacheSize);
                        getInstalledApps(context);
                        genInstalledAppsDataFile(context);
                    } else {
                        //getMalwareRiskyApps(context);
                        getInstalledApps(context);
                        //genInstalledAppsDataFile(context);
                        //findDuplicate(); //Moved to seperate thread above
                        //findDuplicate.join();
                        //saveDataToFile(context);
                    }
                    DLog.d(TAG, "after completed!!!!");  // satya  to check : after complete

                    Intent storageCompleted = new Intent();
                    BaseActivity.isStorageDataPrepareCompleted = true;
                    storageCompleted.setAction("com.careondevice.storageCompleted");
                    //context.sendBroadcast(storageCompleted);
                    //context.sendStickyBroadcast(storageCompleted);
                    DLog.i(TAG, "storageCompleted Broadcast sent ");
                    context.stopService(InternalStorageUsage.intent1);
                    break;
                }
            }
        }
    }

    private void genInstalledAppsDataFile(Context context) {


        try {
            //myFile = new File(path + fileName);
            myFile = new File(filePath + appsFileName);
            if (myFile.createNewFile() == false) {
                if (myFile.delete()) {
                    DLog.d(TAG, "File already existed and deleted");
                } else {
                    DLog.d(TAG, "File already existed cannot be deleted");
                }
            }

            fOut = new FileOutputStream(myFile);
            myOutWriter = new OutputStreamWriter(fOut);

            for (Map.Entry<String, ApplicationData> entry : appInfoMap.entrySet()) {
                String packagename = entry.getKey();
                ApplicationData appData = entry.getValue();
                //App icon begin
                String img_str = "";
                Drawable icon = null;
                try {
                    icon = context.getPackageManager().getApplicationIcon(appData.getPkgName());
                    img_str = encodeIcon(icon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // App icon end

                AppSize appSize = appSizeMap.get(packagename);

            }

            myOutWriter.close();
            fOut.close();

            //Runtime.getRuntime().exec("chmod 777 " +filePath+appsFileName);

            DLog.d(TAG, "***************DONE**************");

            //Log.d(TAG, "&EventName=InstalledApps&Status=Success&File=" + appsFileName);
        } catch (IOException e) {
            DLog.e(TAG, "DuplicateFileHandler, IOException : " + e.getMessage());
            DLog.e(TAG, "&EventName=InstalledApps&Status=Fail");
            e.printStackTrace();
        }


    }

    public void saveDataToFile(Context context) {
        DLog.d(TAG, "saveDataToFile...................");

        try {
            LinkedHashMap<String, ApplicationData> storageResolutionMapApps = new LinkedHashMap<>();
            ArrayList<FileInfo> imageFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> audioFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> videoFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> duplicateFileList = new ArrayList<FileInfo>();
            ArrayList<PDStorageFileInfo> pdStorageFileInfoList = new ArrayList<PDStorageFileInfo>();
            ArrayList<String> duplicateFilePathList = new ArrayList<String>();
            while (!AppSizeMapDone) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, ApplicationData> entry : appInfoMap.entrySet()) {
                String packagename = entry.getKey();
                ApplicationData appData = entry.getValue();
                try {
                    if (appData.getType() != null && appData.getType().equalsIgnoreCase("user")) {
                        String img_str = "";
                        Drawable icon = null;
                        try {
                            icon = context.getPackageManager().getApplicationIcon(appData.getPkgName());
                            img_str = encodeIcon(icon);
                            appData.setDrawable(icon);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AppSize appSize = appSizeMap.get(packagename);
                        if (appSize != null) {
                            appData.setAppSize(appSize);
                        } else {
                            DLog.d(TAG, "null package name=====" + packagename);
                            appData.setAppSize(new AppSize(0, 0, 0));
                        }
                        storageResolutionMapApps.put(appData.getPkgName(), appData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (Integer in : this.duplicateFilesMap.keySet()) {
                long size = 0;
                int totalduplicateFiles = 0;
                int cureentduplicateFile = 0;
                List<FileVO> fileVOs = this.duplicateFilesMap.get(in);
                totalduplicateFiles = fileVOs.size() - 1;
                for (FileVO fileVO : fileVOs) {
                    String fname = fileVO.getDisplayName();
                    String[] filePath = fileVO.getFilePath().split("/");
                    String fileNames = filePath[filePath.length - 1];
                    String fileExt = getFileExtension(fileNames).toLowerCase();
                    if (cureentduplicateFile != 0) {
                        if (musicfileExtension.indexOf(fileExt) != -1) {
                            duplicateFilePathList.add(fileVO.getFilePath());
                        }
                        if (imagefileExtension.indexOf(fileExt) != -1) {
                            duplicateFilePathList.add(fileVO.getFilePath());
                        }
                        if (videofileExtension.indexOf(fileExt) != -1) {
                            duplicateFilePathList.add(fileVO.getFilePath());
                        }
                    } else {
                        cureentduplicateFile++;
                    }
                }
                //resolution.setDuplicateFileList(duplicateFileList);
            }

            DLog.i(TAG, "Apps are added");
            int index = 0;
            for (FileData fileData : fileImageList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("image"+index++);
                pdStorageFileInfo.setName(fileData.getFileName());
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_IMAGE);
                pdStorageFileInfo.setFilePath(fileData.getFilePath());
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if(duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath()))
                    pdStorageFileInfo.setDuplicate(true);
                else
                    pdStorageFileInfo.setDuplicate(false);
                pdStorageFileInfoList.add(pdStorageFileInfo);
/*                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileData.fileName);
                fileInfo.setFilePath(fileData.filePath);
                fileInfo.setFileSize("" + Util.BtoKB(fileData.fileSizeInBytes));
                fileInfo.setCreatedDate("" + fileData.lastModifiedTime);
                imageFileList.add(fileInfo);*/
            }
            resolution.setImageFileList(imageFileList);
            index = 0;
            for (FileData fileData : fileMusicList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("audio"+index++);
                pdStorageFileInfo.setName(fileData.getFileName());
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_AUDIO);
                pdStorageFileInfo.setFilePath(fileData.getFilePath());
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if(duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath()))
                    pdStorageFileInfo.setDuplicate(true);
                else
                    pdStorageFileInfo.setDuplicate(false);
                pdStorageFileInfoList.add(pdStorageFileInfo);

/*                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileData.fileName);
                fileInfo.setFilePath(fileData.filePath);
                fileInfo.setFileSize("" + Util.BtoKB(fileData.fileSizeInBytes));
                fileInfo.setCreatedDate("" + fileData.lastModifiedTime);
                audioFileList.add(fileInfo);*/
            }
            resolution.setAudioFileList(audioFileList);
            index = 0;
            for (FileData fileData : fileVideoList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("video"+index++);
                pdStorageFileInfo.setName(fileData.getFileName());
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_VIDEO);
                pdStorageFileInfo.setFilePath(fileData.getFilePath());
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if(duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath()))
                    pdStorageFileInfo.setDuplicate(true);
                else
                    pdStorageFileInfo.setDuplicate(false);
                pdStorageFileInfoList.add(pdStorageFileInfo);

/*                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileData.fileName);
                fileInfo.setFilePath(fileData.filePath);
                fileInfo.setFileSize("" + Util.BtoKB(fileData.fileSizeInBytes));
                fileInfo.setCreatedDate("" + fileData.lastModifiedTime);
                videoFileList.add(fileInfo);*/
            }
            resolution.setVideoFileList(videoFileList);
            index = 0;
            for (FileData fileData : fileOtherList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("others"+index++);
                pdStorageFileInfo.setName(fileData.getFileName());
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_OTHER);
                pdStorageFileInfo.setFilePath(fileData.getFilePath());
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if(duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath()))
                    pdStorageFileInfo.setDuplicate(true);
                else
                    pdStorageFileInfo.setDuplicate(false);
                pdStorageFileInfoList.add(pdStorageFileInfo);
            }

            resolution.setStorageFileInfoList(pdStorageFileInfoList);

/*            for (Integer in : this.duplicateFilesMap.keySet()) {
                long size = 0;
                int totalduplicateFiles = 0;
                int cureentduplicateFile = 0;
                List<FileVO> fileVOs = this.duplicateFilesMap.get(in);
                totalduplicateFiles = fileVOs.size() - 1;
                for (FileVO fileVO : fileVOs) {
                    String fname = fileVO.getDisplayName();
                    String[] filePath = fileVO.getFilePath().split("/");
                    String fileNames = filePath[filePath.length - 1];
                    String fileExt = getFileExtension(fileNames).toLowerCase();
                    if (cureentduplicateFile != 0) {
                        if (musicfileExtension.indexOf(fileExt) != -1) {
                            fileNames = fileNames + "(" + cureentduplicateFile++ + "/" + totalduplicateFiles + ")";
                            size = fileVO.getLength();
                            size = fileVO.getLength();
                            FileInfo fileInfo = new FileInfo();
                            fileInfo.setFileName(fileNames);
                            fileInfo.setFilePath(fileVO.getFilePath());
                            fileInfo.setFileSize("" + Util.BtoKB(size));
                            fileInfo.setCreatedDate("" + fileVO.getLastModifiedTime());
                            duplicateFileList.add(fileInfo);
                        }
                        if (imagefileExtension.indexOf(fileExt) != -1) {
                            fileNames = fileNames + "(" + cureentduplicateFile++ + "/" + totalduplicateFiles + ")";
                            size = fileVO.getLength();
                            FileInfo fileInfo = new FileInfo();
                            fileInfo.setFileName(fileNames);
                            fileInfo.setFilePath(fileVO.getFilePath());
                            fileInfo.setFileSize("" + Util.BtoKB(size));
                            fileInfo.setCreatedDate("" + fileVO.getLastModifiedTime());
                            duplicateFileList.add(fileInfo);
                        }
                        if (videofileExtension.indexOf(fileExt) != -1) {
                            fileNames = fileNames + "(" + cureentduplicateFile++ + "/" + totalduplicateFiles + ")";
                            size = fileVO.getLength();
                            FileInfo fileInfo = new FileInfo();
                            fileInfo.setFileName(fileNames);
                            fileInfo.setFilePath(fileVO.getFilePath());
                            fileInfo.setFileSize("" + Util.BtoKB(size));
                            fileInfo.setCreatedDate("" + fileVO.getLastModifiedTime());
                            duplicateFileList.add(fileInfo);
                        }
                    } else {
                        cureentduplicateFile++;
                    }
                }
                resolution.setDuplicateFileList(duplicateFileList);
            }*/

            DLog.d(TAG, "***************DONE**************");
        } catch (Exception e) {
            DLog.e(TAG, "DuplicateFileHandler, IOException : " + e.getMessage());
            DLog.e(TAG, "&EventName=StorageDetails&Status=Fail");
            e.printStackTrace();
        }
        resolution.setFileResolutionDone(true);
    }

    private String getFileExtension(String fileName) {

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            //String ext = fileName.substring(fileName.lastIndexOf(".") + 1)
            //	Log.d(TAG,"Extension:"+fileName.substring(fileName.lastIndexOf(".") + 1));
            return fileName.substring(fileName.lastIndexOf(".") + 1);

        } else return "";
    }

    private long getMemorySize(String pathName, boolean total) {
        try {
            File path = new File(pathName);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            if (total) {
                return stat.getBlockCount() * blockSize;
            } else {
                return stat.getAvailableBlocks() * blockSize;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private void writeIntoFile(final String data) {

    }

    private void populateSameSizedFiles(String path) {
        try {
            File parent = new File(path);
            if (parent.isDirectory()) {
                File[] files = new File(path).listFiles(); // (this.fnf);
                if (files != null) {
                    for (File file : files) {
                        populateSameSizedFiles(file.getPath());
						/*< Akhilesh  05/12/2016 25722 begin*/
                        if (file.isDirectory())
                            continue;
						/*< Akhilesh 05/12/2016 25722 end >*/
                        fileSeparation(file);
                    }
                    return;
                }
                return;
            }
            this.totalFileNo++;
            if (this.map.containsKey(Long.valueOf(parent.length()))) {
                ((List) this.map.get(Long.valueOf(parent.length())))
                        .add(new FileVO(parent.getName(), path, parent.length(),
                                null, parent.lastModified(), parent.canWrite()));
                return;
            }
            this.map.put(Long.valueOf(parent.length()), new ArrayList());
            ((List) this.map.get(Long.valueOf(parent.length()))).add(new FileVO(
                    parent.getName(), path, parent.length(), null, parent.lastModified(), parent.canWrite()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compareAndFindDuplicates() {
        try {
            DLog.d(TAG, "TotalFileNumber : " + this.totalFileNo);
            Map<Checksum, FileVO> mapChksm = new HashMap();
            for (Long fileLength : this.map.keySet()) {
                List<FileVO> listSameLength = (List) this.map.get(fileLength);
                if (listSameLength.size() > 1) {
                    if (fileLength < 209715200 && fileLength > 10000) {

                        for (FileVO vo : listSameLength) {
                            byte[] chm = vo.getChecksum();
                            if (chm == null) {
                                chm = getChecksum(new File(vo.getFilePath()));
                                vo.setChecksum(chm);
                            }
                            Checksum chksm = new Checksum(chm);
                            if (mapChksm.get(chksm) != null) {
                                if (((FileVO) mapChksm.get(chksm)).getGroupId() == 0) {
                                    this.group++;
                                    ((FileVO) mapChksm.get(chksm))
                                            .setGroupId(this.group);
                                }
                                vo.setGroupId(this.group);
                                if (this.duplicateFilesMap.get(Integer
                                        .valueOf(this.group)) == null) {
                                    this.duplicateFilesMap.put(
                                            Integer.valueOf(vo.getGroupId()),
                                            new ArrayList());
                                    ((List) this.duplicateFilesMap.get(Integer
                                            .valueOf(vo.getGroupId())))
                                            .add((FileVO) mapChksm.get(chksm));
                                }
                                ((List) this.duplicateFilesMap.get(Integer.valueOf(vo
                                        .getGroupId()))).add(vo);
                            } else {
                                mapChksm.put(chksm, vo);
                            }
                        }
                        continue;
                    } else {
                        duplicateFilesMap2.put(fileLength, listSameLength);
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private DuplicateFilesInfo getDuplicateFilesInfo()  // actual
    {
        long aggregateDuplicateFileSize = 0;
        long duplicateFileSetCount = 0;
        long aggregateDuplicateFileCount = 0;
        for (Integer in : this.duplicateFilesMap.keySet()) {
            duplicateFileSetCount++;
            List<FileVO> fileVOs = this.duplicateFilesMap.get(in);
            for (FileVO fileVO : fileVOs) {
                aggregateDuplicateFileSize += fileVO.getLength();
                aggregateDuplicateFileCount++;
            }
        }
        for (Long in : this.duplicateFilesMap2.keySet()) {
            duplicateFileSetCount++;
            List<FileVO> fileVOs = this.duplicateFilesMap2.get(in);
            for (FileVO fileVO : fileVOs) {
                aggregateDuplicateFileSize += fileVO.getLength();
                aggregateDuplicateFileCount++;
            }
        }
        //Log.i(TAG,"Satya aggregateDuplicateFileSize "+aggregateDuplicateFileSize+" aggregateDuplicateFileCount "+aggregateDuplicateFileCount+" duplicateFileSetCount "+duplicateFileSetCount);
        return new DuplicateFilesInfo(aggregateDuplicateFileSize, aggregateDuplicateFileCount, duplicateFileSetCount);
    }

    private byte[] getChecksum(File file) throws Throwable {
        MessageDigest complete = MessageDigest.getInstance("SHA1");
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
                if (numRead == -1) {
                    break;
                }
            }
            return complete.digest();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    /*< Akhilesh  05/12/2016 25722 begin*/
    private boolean matchAudioFiles(List<String> aMusicfileExtension, String extension) {
        if (aMusicfileExtension.size() > 0) {
            for (String string : aMusicfileExtension) {
                if (extension.equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchVideoFiles(List<String> aVideofileExtension, String extension) {
        if (aVideofileExtension.size() > 0) {
            for (String string : aVideofileExtension) {
                if (extension.equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchImageFiles(List<String> aImagefileExtension, String extension) {
        if (aImagefileExtension.size() > 0) {
            for (String string : aImagefileExtension) {
                if (extension.equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addingVideoFilesInList(File selected) {
        try {
            String fileName = utf8(selected.getName());
            String filePath = utf8(selected.getAbsolutePath());
//            FileData fileData = new FileData(fileName, filePath,
//                    selected.length(), selected.lastModified(), selected.canWrite());
//            fileVideoList.add(fileData);
            videoSize += selected.length();
        } catch (Exception e) {
            DLog.e(TAG, "InternalStorage, Exception");
            e.printStackTrace();
        }
    }

    private void addingAudioFilesInList(File selected) {
        try {
            String fileName = utf8(selected.getName());
            String filePath = utf8(selected.getAbsolutePath());
//            FileData fileData = new FileData(fileName, filePath,
//                    selected.length(), selected.lastModified(), selected.canWrite());
            double fileSizeInKB = selected.length() / 1024;
            if (fileSizeInKB >= 10) {
//                fileMusicList.add(fileData);
                musicSize += selected.length();
            }
        } catch (Exception e) {
            DLog.e(TAG, "InternalStorage, Exception");
            e.printStackTrace();
        }
    }

    private void addingImageFilesInList(File selected) {
        try {
            String fileName = utf8(selected.getName());
            String filePath = utf8(selected.getAbsolutePath());
//            FileData fileData = new FileData(fileName, filePath,
//                    selected.length(), selected.lastModified(), selected.canWrite());
            double fileSizeInKB = selected.length() / 1024;
            if (fileSizeInKB >= 10 && !filePath.contains("DCIM/.thumbnails")) {
//                fileImageList.add(fileData);
                imageSize += selected.length();
            }
        } catch (Exception e) {
            DLog.e(TAG, "InternalStorage, Exception");
            e.printStackTrace();
        }
    }

    private void addingOthersFilesInList(File selected) {
        try {
            String fileName = utf8(selected.getName());

            String filePath = utf8(selected.getAbsolutePath());
//            FileData fileData = new FileData(fileName, filePath,
//                    selected.length(), selected.lastModified(), selected.canWrite());
//            fileOtherList.add(fileData);
            otherSize += selected.length();
        } catch (Exception e) {
            DLog.e(TAG, "InternalStorage, Exception");
            e.printStackTrace();
        }
    }

    private void fileSeparation(File file) {
        File selected = file;
        if (selected != null) {
            Uri selectedUri = Uri.fromFile(selected);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri
                    .toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension);
            if (mimeType != null) {
                switch (mimeType.split("/")[0]) {
                    case MIMETYPE_AUDIO:
                        addingAudioFilesInList(selected);
                        break;
                    case MIMETYPE_VIDEO:
                        addingVideoFilesInList(selected);
                        break;
                    case MIMETYPE_IMAGE:
                        addingImageFilesInList(selected);
                        break;
                    default:
                        addingOthersFilesInList(selected);
                        break;
                }
            } else {
                try {
                    String fileName = utf8(selected.getName());
				/*< Akhilesh  05/12/2016 25722 begin*/
                    String lExtensionIs = getFileExtension(fileName);
                    if (!TextUtils.isEmpty(lExtensionIs)) {
                        if (matchVideoFiles(videofileExtension, lExtensionIs)) {
                            addingVideoFilesInList(selected);
                        } else if (matchImageFiles(imagefileExtension, lExtensionIs)) {
                            addingImageFilesInList(selected);
                        } else if (matchAudioFiles(musicfileExtension, lExtensionIs)) {
                            addingAudioFilesInList(selected);
                        } else {
                            addingOthersFilesInList(selected);
                        }
                    } else {
                        addingOthersFilesInList(selected);
                    }
				/* Akhilesh 05/12/2016 25722 end >*/
                } catch (Exception e) {
                    DLog.e(TAG, "InternalStorage, Exception");
                    e.printStackTrace();
                }
            }
        }    // null check for file selected
    }

    /*< Akhilesh  05/12/2016 25722 end >*/
    private String utf8(String in) {
		/*if (in == null)
			return "";
		in = in.replaceAll("&", "&#x26;");
		in = in.replaceAll("<", "&#x60;");
		in = in.replaceAll(">", "&#x62;");*/
        return (in);
    }

    private boolean validateFolder(File file) {
        if (!file.isDirectory()) {
            return validateFile(file);
        }
        return false;
    }

    private boolean validateFile(File file) {
        return true;
    }

    private class Checksum {
        byte[] checksum;

        public Checksum(byte[] chksm) {
            this.checksum = chksm;
        }

        public byte[] getChecksum() {
            return this.checksum;
        }

        public void setChecksum(byte[] checksum) {
            this.checksum = checksum;
        }

        public boolean equals(Object o) {
            return MessageDigest
                    .isEqual(this.checksum, ((Checksum) o).checksum);
        }

        public int hashCode() {
            return Arrays.hashCode(this.checksum);
        }
    }

    public void setDuplicateFilesMap(Map<Integer, List<FileVO>> duplicateFilesMap) {
        this.duplicateFilesMap = duplicateFilesMap;
    }

    public void setFileMusicList(ArrayList<FileData> fileMusicList) {
        this.fileMusicList = fileMusicList;
    }

    public void setFileVideoList(ArrayList<FileData> fileVideoList) {
        this.fileVideoList = fileVideoList;
    }

    public void setFileImageList(ArrayList<FileData> fileImageList) {
        this.fileImageList = fileImageList;
    }

    public void setFileOtherList(ArrayList<FileData> fileOtherList) {
        this.fileOtherList = fileOtherList;
    }
}

/***************************************/