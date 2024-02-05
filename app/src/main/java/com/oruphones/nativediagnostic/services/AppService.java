package com.oruphones.nativediagnostic.services;

import static org.pervacio.onediaglib.internalstorage.InternalStorageUsageHandler.isSystemApp;
import static org.pervacio.onediaglib.internalstorage.LPUtil.getRecentlyUsedAppsFromAllIntervals;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IntentService;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.Storage.AppSize;
import com.oruphones.nativediagnostic.api.AppDetails;
import com.oruphones.nativediagnostic.api.AppInfo;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.api.PDAppsInfo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.LPUtil;
import com.oruphones.nativediagnostic.util.StorageUtil;
import com.oruphones.nativediagnostic.util.Util;


import org.pervacio.onediaglib.internalstorage.ApplicationData;
import org.pervacio.onediaglib.internalstorage.MD5;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Pervacio on 6-9-2017.
 */
public class AppService extends IntentService {
    private static String TAG = AppService.class.getSimpleName();
    public AppService() {
        super("AppService");
    }
    private Set<String> batteryConsumingApps = new HashSet<String>();
    private Set<String> bandWidthconsumingAppsList = new HashSet<String>();

/*    private ArrayList<String> onlineTestList = new ArrayList<String>();
    {
    onlineTestList.add(PDConstants.MALWAREAPPS);
    onlineTestList.add(PDConstants.ADWAREAPPS );
    onlineTestList.add(PDConstants.RISKYAPPS);
    }*/
    @Override
    protected void onHandleIntent(Intent intent) {
         DLog.d(TAG, "Init service started.........");
        getInstalledApps(getApplicationContext());
        //getRamDetailsAdvanced(getApplicationContext());
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasUsageStatsPermission(Context context) {
        //http://stackoverflow.com/a/42390614/878126
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        boolean granted = false;
        if (mode == AppOpsManager.MODE_DEFAULT)
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        else
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }


    boolean AppSizeMapDone = false;
    boolean sizeDone = false;
    int calbackCount = 0;
    int numCallbacks = 0;
    private HashMap<String, AppSize> appSizeMap = new HashMap<String, AppSize>();
    private HashMap<String, ApplicationData> appInfoMap = new HashMap<String, ApplicationData>();

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
            if (info.applicationInfo == null) {
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
            sizeDone = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        @SuppressLint("WrongConstant")
                        final StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
                        final StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

                        final List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                        final UserHandle user = Process.myUserHandle();
                        Long appBytes = 0l;
                        Long dataBytes = 0l;
                        Long cacheBytes = 0l;
                        if (hasUsageStatsPermission(this)) {
                            for (StorageVolume storageVolume : storageVolumes) {
                                //final String uuidStr = storageVolume.getUuid();
                              //  final UUID uuid = uuidStr == null ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
                                try {
                                    final StorageStats storageStats = storageStatsManager.queryStatsForPackage(StorageManager.UUID_DEFAULT, pkg, user);
                                   /*  LogUtil.printLog(TAG, "getAppBytes:" + storageStats.getAppBytes()
                                            + " getCacheBytes:" + storageStats.getCacheBytes()
                                            + " getDataBytes:" + storageStats.getDataBytes());*/

                                    appBytes += storageStats.getAppBytes();
                                    dataBytes += storageStats.getDataBytes();
                                    cacheBytes += storageStats.getCacheBytes();

                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                    invocationException = true;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    invocationException = true;
                                }
                            }
                        }
                        calbackCount++;
                        appSizeMap.put(pkg, new AppSize(appBytes, dataBytes, cacheBytes));
                        if (calbackCount >= numCallbacks)
                            AppSizeMapDone = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                         DLog.e(TAG, "Exception  : " + e.getMessage());
                    }
               // }
            } else {
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
                                    // LogUtil.printLog("prem", pStats.packageName + " size===" + appSizeMap.get(pStats.packageName).getCodeSize());
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

    private void getInstalledApps(Context context) {
        prepareAppSizeMap(context);
         DLog.d(TAG, "getInstalledApps................");
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packs = pm.getInstalledPackages(0);
            try {
                LPUtil lpUtil = new LPUtil();
//                HashMap<String, Long> rcntAppsMap = null;
//                HashMap<String, Long> rcntAppsMapFromScratch = null;
                HashMap<String, Long> rcntAppsMapAll = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
//                        rcntAppsMap = new LPUtil().getRecentlyUsedAppsWithTimestamp(context);
//                        rcntAppsMapFromScratch = new LPUtil().getRecentlyUsedAppsFromScratch(context);
                        rcntAppsMapAll = getRecentlyUsedAppsFromAllIntervals(context);
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
                final Set<PDAppsInfo> pdAppsInfos = new TreeSet<>();
                ArrayList<AppDetails> appDetailsList = new ArrayList<AppDetails>();
                try {
                    for (int i = 0; i < packs.size(); i++) {

                        PDAppsInfo pdAppsInfo = new PDAppsInfo();
                        ApplicationData applicationData = new ApplicationData();
                        //AppResolutionPojo appResPojo = new AppResolutionPojo();
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
//                        if (rcntAppsMapFromScratch != null) {
//                            boolean used = rcntAppsMapFromScratch.containsKey(packageName);
//                            if (used) {
//                                try {
//                                    pdAppsInfo.setRcutimestamp(rcntAppsMapFromScratch.get(packageName).longValue());
//                                } catch (Exception ex1) {
//                                }
//                            }
//
//                        }

                        if (rcntAppsMapAll != null) {
                            boolean unused = false;
                            //TODO hardcoded 5 to be chsnged dynamically from diag config
                            long unusedAppTimeFrameValue = GlobalConfig.getInstance().getUnusedAppsThresholdVal() * 24L * 3600L * 1000L;;
                            if (rcntAppsMapAll.containsKey(packageName)) {
                                if ((GlobalConfig.getInstance().getCurrentServerTime() - p.firstInstallTime) > unusedAppTimeFrameValue) {
                                    long lastUsedMillis = rcntAppsMapAll.get(packageName);
                                    long lastFiveDaysMillis = GlobalConfig.getInstance().getCurrentServerTime() - unusedAppTimeFrameValue;
                                    if (lastUsedMillis < lastFiveDaysMillis) {
                                        unused = true;
                                    }
                                }
//                                pdAppsInfo.setLastUsed(used ? "true" : "false");
                                pdAppsInfo.setUnused(unused);
                                pdAppsInfo.setRcutimestamp(rcntAppsMapAll.get(packageName).longValue());
                            } else {
                                if ((GlobalConfig.getInstance().getCurrentServerTime() - p.firstInstallTime) > unusedAppTimeFrameValue) {
                                    unused = true;
                                }
//                                pdAppsInfo.setLastUsed(used ? "true" : "false");
                                pdAppsInfo.setUnused(unused);
                                pdAppsInfo.setRcutimestamp(0l);
                            }
                            if(unused) {
                                Resolution.getInstance().setUnusedFound(unused);
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
/*                                ArrayList<String> dPerms = new ArrayList<String>();
                                String[] reqPerms = packageInfo.requestedPermissions;
                                boolean bandwidthconsumingapp = false;
                                if (reqPerms != null*//* && selectedAutoTestList.contains(PDConstants.BANDWIDTHAPPS)*//*) {
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
                                }*/
                                //fOut.write(("\t\t<bandwidthconsumingapp>" + utf8(bandwidthconsumingapp + "") + "</bandwidthconsumingapp>\n").getBytes());
                                //  pdAppsInfo.setBandwidthconsumingapp(bandwidthconsumingapp ? PDConstants.PDTRUE : PDConstants.PDFALSE);
                                //applicationData.setBandwidthconsumingapp(bandwidthconsumingapp);
/*                                boolean btryConsumingApps = false;
                                if (batteryConsumingApps.contains(packageName) *//*&& selectedAutoTestList.contains(PDConstants.BATTERYCONSUMINGAPPS)*//*) {
                                    btryConsumingApps = true;
                                }*/
                                //  pdAppsInfo.setBatteryconsumingapps(btryConsumingApps ? PDConstants.PDTRUE : PDConstants.PDFALSE);
                                //applicationData.setBtryConsumingApps(btryConsumingApps);
                                if (!type.equalsIgnoreCase("SYSTEM")) {
                                    pdAppsInfos.add(pdAppsInfo);
                                }
                                AppDetails appDetails = new AppDetails();
                                appDetails.setPackageName(pdAppsInfo.getPackageName());
                                appDetails.setMd5Digest(md5Digest);
                                appDetails.setUpdatedDate(pdAppsInfo.getUpdatedDate());
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

                DLog.d(TAG, "Unused apps Done ");

                //Getting status of application from web api
                 List<AppDetails> appDetailsListFromServer = PervacioTest.getInstance().getMalwareRiskyApps(context, appDetailsList);
                while (!AppSizeMapDone) {
                    Thread.sleep(50);
                }
                DLog.d(TAG, "Unused apps Web call Done ");
                // Creating respective hash map
                final HashMap<String, AppDetails> packageAppDetailsHashMap = new HashMap<>();
                for (AppDetails appDetails : appDetailsListFromServer) {
                    packageAppDetailsHashMap.put(appDetails.getPackageName(), appDetails);
                }

                DLog.d(TAG, "Unused apps Mapping Done ");
                //Checking device app with webroot SDK for Malware detection.
//                Webroot.startProtectionScan(new ProtectionEvent(){
//                    @Override
//                    public void onProgress(@NonNull ProtectionProgress result) {
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull ProtectionAlert result) {
//                        LogUtil.printLog("AppService", "Webroot  Done ");
//                        for (Detection detection : result.getRequiresRemediation()){
//                            AppDetails appDetails = packageAppDetailsHashMap.get(detection.getApplicationPackageName());
//                            if(appDetails!=null){
//                                appDetails.setMalware(true);
//                                packageAppDetailsHashMap.put(detection.getApplicationPackageName(),appDetails);
//                            }
//                        }
//                        filterApp(packageAppDetailsHashMap,pdAppsInfos);
//                    }
//
//                    @Override
//                    public void onFail(@NonNull final ProtectionFail result) {
//                        LogUtil.printLog("AppService", "Webroot  Fails ");
//                        filterApp(packageAppDetailsHashMap,pdAppsInfos);
//                    }
//                });

            } catch (Exception e) {
                DLog.e(TAG, "App info Exception:" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            DLog.e(TAG, "Exception: " + e.toString());
            // LogUtil.logEvent("&EventName=InstalledApps&Status=Failed");
        }

    }


    private void filterApp(HashMap<String, AppDetails> packageAppDetailsHashMap, Set<PDAppsInfo> pdAppsInfos){
        try {
            Context context = getApplicationContext();
            ArrayList<PDAppsInfo> pdAppsInfoList = new ArrayList<PDAppsInfo>();
            for (PDAppsInfo pdAppsInfo : pdAppsInfos) {
                AppDetails appDetails = packageAppDetailsHashMap.get(pdAppsInfo.getPackageName());
                if (appDetails != null && pdAppsInfo.getPackageName().equalsIgnoreCase(appDetails.getPackageName())) {
                    pdAppsInfo.setMalware(appDetails.isMalware());
                    pdAppsInfo.setAddware(appDetails.isAddware());
                    pdAppsInfo.setRiskyapp(appDetails.isRiskyapp());
                    pdAppsInfo.setOutdated(appDetails.isOutdated());

                    if (appDetails.isMalware())
                        Resolution.getInstance().setMalwareFound(appDetails.isMalware());
                    if (appDetails.isAddware())
                        Resolution.getInstance().setAdwareFound(appDetails.isAddware());
                    if (appDetails.isRiskyapp())
                        Resolution.getInstance().setRiskyFound(appDetails.isRiskyapp());
                    if (appDetails.isOutdated())
                        Resolution.getInstance().setOutdatedFound(appDetails.isOutdated());

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
                        // LogUtil.printLog(TAG, "Exception = " + e1.getMessage());
                    }
                    pdAppsInfo.setIcon(StorageUtil.encodeIcon(appIcon));
                    AppInfo info = new AppInfo();
                    info.setAppName(pdAppsInfo.getAppName());
                    info.setAppSizeKB(pdAppsInfo.getSize() + "");
                    info.setInstalledDate(pdAppsInfo.getUpdatedDate());
                    info.setPackageName(pdAppsInfo.getPackageName());
                    info.setAppIcon(appIcon);
                    if (pdAppsInfo.isMalware())
                        Resolution.getInstance().getMalwareAppsList().add(info);
                    if (pdAppsInfo.isAddware())
                        Resolution.getInstance().getAddwareAppsList().add(info);
                    if (pdAppsInfo.isRiskyapp())
                        Resolution.getInstance().getRiskyAppsList().add(info);
                    if (pdAppsInfo.isOutdated())
                        Resolution.getInstance().getOutdatedAppsList().add(info);
                    if (pdAppsInfo.isUnused())
                        Resolution.getInstance().getUnusedAppsList().add(info);
                    pdAppsInfoList.add(pdAppsInfo);
                }
            }
            Resolution.getInstance().setAppsInfoList(pdAppsInfoList);
            Resolution.getInstance().setAppResolutionDone(true);
        } catch (Exception e) {
            DLog.e(TAG, "Exception: " + e.toString());
            // LogUtil.logEvent("&EventName=InstalledApps&Status=Failed");
        }
    }


    private String utf8(String in) {
        if (in == null)
            return "";
        in = in.replaceAll("&#x26;", "&");
        in = in.replaceAll("&#x60;", "<");
        in = in.replaceAll("&#x62;", ">");
        return (in);
    }

/*    public long parseRAMDataLine(String line, String prefix) {
        try {
            if (line.startsWith(prefix)) {
                Scanner scanner = new Scanner(line);
                String pre = scanner.next();
                String value = scanner.next();
                return Long.parseLong(value);
            }
        } catch (Exception e) {
        }
        return -1;
    }

    private long getAppMemory(Context context) {
        long memUsedbyApps = 0;
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
                    .getRunningAppProcesses();
            if (Build.MODEL.equalsIgnoreCase("Nexus 4")
                    || (Build.MODEL.equalsIgnoreCase("XT1033")) && (runningAppProcesses != null && runningAppProcesses.size() == 1)) {
                runningAppProcesses = ProcessManagerUtils.getRunningApplicationList();
            }
            Map<Integer, String> pidMap = new TreeMap<Integer, String>();
            Map<Integer, String> processType = new TreeMap<Integer, String>();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                String processTypeString = "";
                boolean foregoundProcesessBoll;
                if (runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    processTypeString = "foreground";
                } else if (runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_BACKGROUND
                        || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_PERCEPTIBLE
                        || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_SERVICE) {
                    processTypeString = "background";
                }
                processType.put(runningAppProcessInfo.pid,
                        processTypeString);
                pidMap.put(runningAppProcessInfo.pid,
                        runningAppProcessInfo.processName);

*//*                 LogUtil.printLog("Ambi", " runningAppProcessInfo : "
                        + runningAppProcessInfo.importance + "");
                 LogUtil.printLog("Ambi", "pkgName : "
                        + runningAppProcessInfo.processName);*//*
            }
            Collection<Integer> keys = pidMap.keySet();

             LogUtil.printLog(TAG, " keys size : " + keys.size());
            for (int key : keys) {
                int pids[] = new int[1];
                pids[0] = key;
                android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager
                        .getProcessMemoryInfo(pids);
                for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
                     LogUtil.printLog(TAG, String.format(
                            "** MEMINFO in pid : %d : [%s]\n", pids[0],
                            pidMap.get(pids[0])));
                    // AppInfo appInfo = new AppInfo();
                    String pkgName = pidMap.get(pids[0]);
                    PackageManager pm = context.getPackageManager();
                    String appName = "";
                    try {
                        appName = (String) pm.getApplicationLabel(pm
                                .getApplicationInfo(pkgName,
                                        PackageManager.GET_META_DATA));
                    } catch (Exception e) {
                         LogUtil.printLog(TAG, e.getMessage(), null, AppUtils.LogType.EXCEPTION);

                    }
                    boolean autostat = false;
                    boolean backgroundapp = false;
                    boolean foregroundapp = false;
                    int TotalPss = pidMemoryInfo.getTotalPss();
                    if (!pkgName.equalsIgnoreCase(OruApplication.getAppContext().getPackageName())) {
                        memUsedbyApps = memUsedbyApps + TotalPss;
                    }
                    int TotalPrivateDirty = pidMemoryInfo
                            .getTotalPrivateDirty();
                    int TotalSharedDirty = pidMemoryInfo
                            .getTotalSharedDirty();
                     LogUtil.printLog(TAG, "pkgName : " + pkgName);
                     LogUtil.printLog(TAG, "pidMemoryInfo.getTotalPss() : "
                            + pidMemoryInfo.getTotalPss());
                     LogUtil.printLog(TAG, "pidMemoryInfo.getTotalPrivateDirty() : "
                            + pidMemoryInfo.getTotalPrivateDirty());
                     LogUtil.printLog(TAG, "pidMemoryInfo.getTotalSharedDirty() : "
                            + pidMemoryInfo.getTotalSharedDirty());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return memUsedbyApps;
    }

    private void getRamDetailsAdvanced(Context context) {
        ArrayList<AppInfo> foregroundAppList = new ArrayList();
        ArrayList<AppInfo> backgroundAppList = new ArrayList();
        ArrayList<AppInfo> autostartAppList  = new ArrayList();
        AppInfo appInformaion;
*//*
        memoryResolutionAppInfoPOJOsArrayList = new ArrayList<MemoryResolutionAppInfoPOJO>();
        memoryResolutionInfoPOJO = new MemoryResolutionInfoPOJO();*//*

        PDMemoryResolutionInfo pdMemoryResolutionInfo = new PDMemoryResolutionInfo();
*//*
        pdMemoryResolutionAppInfosList = new ArrayList<PDMemoryResolutionAppInfo>();
*//*
        String xmlFileName = "ramdetails.xml";
        String xmlFilePath = context.getFilesDir() + "/" + xmlFileName;
        try {
            File xmlFile = new File(xmlFilePath);
            if (xmlFile.exists()) {
                xmlFile.delete();
            } else {
                xmlFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString() + "");
        }
        long totalRAM = -1, availRAMfromProc = -1, availRAMFromApp;
        long memUsedbyApps;
        long memUsedbyOs;
        RandomAccessFile reader = null;
        try {

            reader = new RandomAccessFile("/proc/meminfo", "r");
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("MemTotal:")) {
                    long value = parseRAMDataLine(line, "MemTotal:");
                    if (value >= 0) {
                        totalRAM = value;
                    }
                } else if (line.startsWith("MemFree:")) {
                    long value = parseRAMDataLine(line, "MemFree:");
                    if (value >= 0) {
                        availRAMfromProc = value;
                        // LogUtil.printLog("PDIAG", "freeRAM:" + availRAMfromProc);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            long availableRamMemory;
            if (BaseActivity.availableRam != -1) {
                availableRamMemory = BaseActivity.availableRam;
            } else {
                availableRamMemory = new TestRamMemory().getAvailableRamMemory();
                BaseActivity.availableRam = availableRamMemory;
            }
            pdMemoryResolutionInfo.setTotalram(totalRAM);
            pdMemoryResolutionInfo.setAvailableram(availableRamMemory);
            memUsedbyApps = getAppMemory(context);
            memUsedbyOs = totalRAM - (memUsedbyApps + availRAMfromProc);
            pdMemoryResolutionInfo.setMemUsedbyApps(memUsedbyApps);
            pdMemoryResolutionInfo.setMemusedbyos(memUsedbyOs);
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(ACTIVITY_SERVICE);

            try {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
                        .getRunningAppProcesses();
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        && (runningAppProcesses != null && runningAppProcesses.size() == 1)) {
                    runningAppProcesses = ProcessManagerUtils.getRunningApplicationList();
                }
                Map<Integer, String> pidMap = new TreeMap<Integer, String>();
                Map<Integer, String> processType = new TreeMap<Integer, String>();
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    String processTypeString = "";
                    boolean foregoundProcesessBoll;
                    if (runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_FOREGROUND
                            || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        processTypeString = "foreground";
                    } else if (runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_BACKGROUND
                            || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_PERCEPTIBLE
                            || runningAppProcessInfo.importance == runningAppProcessInfo.IMPORTANCE_SERVICE) {
                        processTypeString = "background";
                    }
                    processType.put(runningAppProcessInfo.pid,
                            processTypeString);
                    pidMap.put(runningAppProcessInfo.pid,
                            runningAppProcessInfo.processName);
                     LogUtil.printLog("Ambi", " runningAppProcessInfo : "
                            + runningAppProcessInfo.importance + "");
                     LogUtil.printLog("Ambi", "pkgName : "
                            + runningAppProcessInfo.processName);
                }
                Collection<Integer> keys = pidMap.keySet();
                 LogUtil.printLog(TAG, " keys size : " + keys.size());
                for (int key : keys) {
                    int pids[] = new int[1];
                    pids[0] = key;
                    android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager
                            .getProcessMemoryInfo(pids);
                    for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
                         LogUtil.printLog(TAG, String.format(
                                "** MEMINFO in pid : %d : [%s]\n", pids[0],
                                pidMap.get(pids[0])));
                        // AppInfo appInfo = new AppInfo();
                        String pkgName = pidMap.get(pids[0]);
                        PackageManager pm = context.getPackageManager();
                        String appName = "";
                        try {
                            appName = (String) pm.getApplicationLabel(pm
                                    .getApplicationInfo(pkgName,
                                            PackageManager.GET_META_DATA));
                        } catch (Exception e) {
                            appName = pkgName;
                             LogUtil.printLog(TAG, e.getMessage(), null, AppUtils.LogType.EXCEPTION);

                        }

                        //String apptype = "system";
                        String apptype = isSystemApp(context, pkgName) ? "system" : "user";
                        boolean isSystemApp= isSystemApp(context,pkgName);
                        int TotalPss = pidMemoryInfo.getTotalPss();
                        memUsedbyApps = memUsedbyApps + TotalPss;
                        int TotalPrivateDirty;
                        int TotalSharedDirty;
                         LogUtil.printLog(TAG, "pkgName : " + pkgName);
                         LogUtil.printLog(TAG, "pidMemoryInfo.getTotalPss() : "
                                + pidMemoryInfo.getTotalPss());
                         LogUtil.printLog(TAG, "pidMemoryInfo.getTotalPrivateDirty() : "
                                + pidMemoryInfo.getTotalPrivateDirty());
                         LogUtil.printLog(TAG, "pidMemoryInfo.getTotalSharedDirty() : "
                                + pidMemoryInfo.getTotalSharedDirty());
                        if (!pkgName.equalsIgnoreCase(OruApplication.getAppContext().getPackageName())&& !isSystemApp) {
                            PDMemoryResolutionAppInfo pdMemoryResolutionAppInfo = new PDMemoryResolutionAppInfo();
                            pdMemoryResolutionAppInfo.setAppname(utf8(appName));
                            pdMemoryResolutionAppInfo.setPackagename(pkgName);
                            ArrayList<String> permissions = new ArrayList<String>();
                            try {
                                PackageInfo packageInfo = pm.getPackageInfo(pkgName,
                                        PackageManager.GET_PERMISSIONS);
                                String[] requestedPermissions = packageInfo.requestedPermissions;
                                boolean internetPermission;
                                boolean combinationPermission;
                                String prmsn;
                                boolean autoStartApp = false;
                                if (requestedPermissions != null) {
                                    for (int j = 0; j < requestedPermissions.length; j++) {
                                        prmsn = requestedPermissions[j];
                                        if ("android.permission.RECEIVE_BOOT_COMPLETED".equalsIgnoreCase(prmsn)) {
                                            autoStartApp = true;
                                            break;
                                        }

                                    }
                                }
                                pdMemoryResolutionAppInfo.setAutostartapp(autoStartApp ? PDConstants.PDTRUE : PDConstants.PDFALSE);
                                // fOut.write(("\t\t<riskyapp>" + utf8(riskyapp+"")+ "</riskyapp>\n").getBytes());

                                // pdAppResolutionInfo.setRiskyapp(riskyapp?PDConstants.PDTRUE:PDConstants.PDFALSE);


                                ApplicationInfo appInfo = packageInfo.applicationInfo;
                                if (appInfo.sourceDir != null) {
                                    try {
                                        File file = new File(appInfo.sourceDir);
                                         LogUtil.printLog(("Auto test", "file length" + file.length());
                                        double sizevalue = (double) file.length();
                                         LogUtil.printLog(("Auto test", "converted size" + sizevalue);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                                            pdMemoryResolutionAppInfo.setInstalledDate("" + packageInfo.firstInstallTime);// Satya added install time to send to server
                                        } else {
                                            long lastModified = file.lastModified();
                                            pdMemoryResolutionAppInfo.setInstalledDate("" + lastModified);// Satya added install time to send to server
                                        }
                                    } catch (Exception e) {
                                    }
                                }


                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            pdMemoryResolutionAppInfo.setProcessname(pkgName);
                            pdMemoryResolutionAppInfo.setusedRAMKB(TotalPss + "");
                            pdMemoryResolutionAppInfo.setProcesstype(processType.get(pids[0]) + "");
                            pdMemoryResolutionAppInfo.setApptype(apptype + "");
*//*                            pdMemoryResolutionAppInfosList.add(pdMemoryResolutionAppInfo);
                            memoryResolutionAppInfoPOJOsArrayList.add(memoryResolutionAppInfoPOJO);*//*
                            appInformaion = new AppInfo();
                            appInformaion.setAppName(pdMemoryResolutionAppInfo.getAppname());
                            appInformaion.setPackageName(pdMemoryResolutionAppInfo.getPackagename());
                            Drawable appIcon = null;
                            try {
                                appIcon = context.getApplicationContext().getPackageManager().getApplicationIcon(pdMemoryResolutionAppInfo.getPackagename());
                            } catch (Exception e1) {
                                // LogUtil.printLog(TAG, "Exception = " + e1.getMessage());
                            }
                            appInformaion.setAppIcon(appIcon);
                            appInformaion.setUsedRamKB(pdMemoryResolutionAppInfo.getusedRAMKB());
                            appInformaion.setInstalledDate(pdMemoryResolutionAppInfo.getInstalledDate());
                             LogUtil.printLog(TAG, "Running apps - " + appInformaion.getAppName());
                            if ("foreground".equalsIgnoreCase(pdMemoryResolutionAppInfo.getProcesstype())) {
                                //resolution.setMalwareFound(true);
                                foregroundAppList.add(appInformaion);
                                 LogUtil.printLog(TAG, "foreground apps - " + appInformaion.getAppName());
                            }
                            *//*if (pdMemoryResolutionAppInfo.getProcesstype().equalsIgnoreCase("foreground")) {
                                //resolution.setMalwareFound(true);
                                foregroundAppList.add(appInformaion);
                                 LogUtil.printLog(TAG, "foreground apps - " + appInformaion.getAppName());
                            }*//*
                            if ("background".equalsIgnoreCase(pdMemoryResolutionAppInfo.getProcesstype())) {
                                //resolution.setRiskyFound(true);
                                backgroundAppList.add(appInformaion);
                                 LogUtil.printLog(TAG, "background apps - " + appInformaion.getAppName());
                            }
                           *//* if (pdMemoryResolutionAppInfo.getProcesstype().equalsIgnoreCase("background")) {
                                //resolution.setRiskyFound(true);
                                backgroundAppList.add(appInformaion);
                                 LogUtil.printLog(TAG, "background apps - " + appInformaion.getAppName());
                            }*//*
                            if("TRUE".equalsIgnoreCase(pdMemoryResolutionAppInfo.getAutostartapp())){
                                autostartAppList.add(appInformaion);
                                 LogUtil.printLog(TAG, "Autostart apps - " + appInformaion.getAppName());
                            }
                            *//*if (pdMemoryResolutionAppInfo.getAutostartapp().equalsIgnoreCase("TRUE")) {
                                //resolution.setAdwareFound(true);
                            }*//*
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Resolution.getInstance().setForegroundAppList(foregroundAppList);
            Resolution.getInstance().setBackgroundAppList(backgroundAppList);
            Resolution.getInstance().setAutostartAppList(autostartAppList);
            Resolution.getInstance().setMemoryResolutionDone(true);
        }
    }

    private void sendInterrupt(){
        Intent intent = new Intent();
        intent.setAction("com.sprint.network.interrupted");
        sendBroadcast(intent);
    }*/
 }