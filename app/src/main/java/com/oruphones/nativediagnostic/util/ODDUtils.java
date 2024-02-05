package com.oruphones.nativediagnostic.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.models.AccessoryDataSet;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.pervacio.batterydiaglib.core.ProfileManager;
import com.pervacio.batterydiaglib.core.test.QuickTestComputeEngine;
import com.pervacio.batterydiaglib.model.ActivityResultInfo;
import com.pervacio.batterydiaglib.model.BatteryDiagConfig;
import com.pervacio.batterydiaglib.model.BatteryInfo;
import com.pervacio.batterydiaglib.model.QuicktestInfo;
import com.pervacio.batterydiaglib.util.BatteryUtil;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ODDUtils {
    private static String TAG = ODDUtils.class.getSimpleName();
    /*private static HashMap<String, AccessoryDataSet> accessoryMapping = new HashMap();
    static {
        Context context = PervacioApplication.getAppContext();
        accessoryMapping.put(TestName.EARPHONETEST,new AccessoryDataSet(context.getString(R.string.accessory_wire_earphone),R.drawable.ic_wiredearphone,null));
        accessoryMapping.put(TestName.USBTEST,new AccessoryDataSet(context.getString(R.string.accessory_usb_cable),R.drawable.ic_usb_cable,null));
        accessoryMapping.put(TestName.CHARGINGTEST,new AccessoryDataSet(context.getString(R.string.accessory_wall_charger),R.drawable.ic_wall_charger,null));
    }

    public static AccessoryDataSet getAccessoryForPerformedTests(String testName){
        return accessoryMapping.get(testName);
    }*/

    /* -------------------- Accessory -----------------------------*/
    private static HashMap<String, AccessoryDataSet> accessoryMapping = new HashMap();
    static {
        Context context = PervacioApplication.getAppContext();
        accessoryMapping.put(TestName.EARPHONETEST,new AccessoryDataSet(2,context.getString(R.string.accessory_wire_earphone),R.drawable.ic_wiredearphone,null));
        accessoryMapping.put(TestName.EARPHONEJACKTEST,new AccessoryDataSet(2,context.getString(R.string.accessory_wire_earphone),R.drawable.ic_wiredearphone,null));
        accessoryMapping.put(TestName.USBTEST,new AccessoryDataSet(1,context.getString(R.string.accessory_usb_cable),R.drawable.ic_usb_cable,null));
        accessoryMapping.put(TestName.CHARGINGTEST,new AccessoryDataSet(0,context.getString(R.string.accessory_wall_charger),R.drawable.ic_wall_charger,null));
    }

    public static AccessoryDataSet getAccessoryForPerformedTests(String testName){
        return accessoryMapping.get(testName);
    }

    public static List<AccessoryDataSet> fetchAndShowAccessoryPopup(List<TestInfo> testInfos){
        List<AccessoryDataSet> dataSetList =  new ArrayList<>();
        for (TestInfo testInfo : testInfos){
            AccessoryDataSet accessoryDataSet = getAccessoryForPerformedTests(testInfo.getName());
            if(accessoryDataSet!=null && !dataSetList.contains(accessoryDataSet)){
                dataSetList.add(accessoryDataSet);
            }
        }
        Collections.sort(dataSetList);
        return dataSetList;

    }


    /* -------------------- Accessory -----------------------------*/
    public static final HashMap<String, Integer> resolutionNames = new HashMap<String, Integer>();
    static {
        resolutionNames.put(ResolutionName.IMAGES, R.string.resolution_images);
        resolutionNames.put(ResolutionName.MUSIC, R.string.resolution_music);
        resolutionNames.put(ResolutionName.VIDEO, R.string.resolution_videos);
        resolutionNames.put(ResolutionName.DUPLICATE, R.string.resolution_duplicate);
        resolutionNames.put(ResolutionName.FOREGROUND_APPS, R.string.resolution_running_apps);
        resolutionNames.put(ResolutionName.BACKGROUND_APPS, R.string.resolution_background_apps);
        resolutionNames.put(ResolutionName.AUTOSTART_APPS, R.string.resolution_autostart_apps);
        resolutionNames.put(ResolutionName.MALWAREAPPS, R.string.resolution_malware_apps);
        resolutionNames.put(ResolutionName.ADWAREAPPS, R.string.resolution_adware_apps);
        resolutionNames.put(ResolutionName.RISKYAPPS, R.string.resolution_risky_apps);
        resolutionNames.put(ResolutionName.OUTDATEDAPPS, R.string.resolution_outdated_apps);
        resolutionNames.put(ResolutionName.INTERNALSTORAGESUGGESTION, R.string.int_storage_msg); //Satya

    }

    public static final HashMap<String, Integer> resolutionImages = new HashMap<String, Integer>();
    static {
        resolutionImages.put(ResolutionName.MALWAREAPPS, R.drawable.apps_malware);
        resolutionImages.put(ResolutionName.ADWAREAPPS, R.drawable.apps_adware);
        resolutionImages.put(ResolutionName.RISKYAPPS, R.drawable.apps_risky);
        resolutionImages.put(ResolutionName.IMAGES, R.drawable.internalstorage_images);
        resolutionImages.put(ResolutionName.MUSIC, R.drawable.internalstorage_audio);
        resolutionImages.put(ResolutionName.VIDEO, R.drawable.internalstorage_video);
        resolutionImages.put(ResolutionName.DUPLICATE, R.drawable.internalstorage_duplicatefiles);
        resolutionImages.put(ResolutionName.FOREGROUND_APPS, R.drawable.memory_running);
        resolutionImages.put(ResolutionName.BACKGROUND_APPS, R.drawable.memory_background);
        resolutionImages.put(ResolutionName.AUTOSTART_APPS, R.drawable.memory_autostart);
        resolutionImages.put(ResolutionName.OUTDATEDAPPS, R.drawable.apps_outdated);
        resolutionImages.put(ResolutionName.FIRMWARE, R.drawable.firmware);
        resolutionImages.put(ResolutionName.SIM_CARD, R.drawable.simcard);

        resolutionImages.put(ResolutionName.LASTRESTART,R.drawable.lastrestart);
        resolutionImages.put(ResolutionName.UNUSEDAPPS,R.drawable.apps_unused);
        resolutionImages.put(TestName.GPS_ON,R.drawable.gps);
        resolutionImages.put(TestName.GPS_OFF,R.drawable.gps);

        resolutionImages.put(TestName.BLUETOOTH_ON,R.drawable.bluetooth);
        resolutionImages.put(TestName.BLUETOOTH_OFF,R.drawable.bluetooth);

        resolutionImages.put(TestName.NFC_ON,R.drawable.nfc);
        resolutionImages.put(TestName.NFC_OFF,R.drawable.nfc);

        resolutionImages.put(TestName.WIFI_ON,R.drawable.wifi);
        resolutionImages.put(TestName.WIFI_OFF,R.drawable.wifi);

        resolutionImages.put(TestName.LIVEWALLPAPER,R.drawable.livewallpaper);
        resolutionImages.put(TestName.BRIGHTNESS,R.drawable.brightness);
        resolutionImages.put(TestName.SCREEN_TIMEOUT,R.drawable.screentimeout);
        resolutionImages.put(TestName.SDCARD,R.drawable.firmware);
        resolutionImages.put(TestName.SDCARDCAPACITY,R.drawable.firmware);
        resolutionImages.put(TestName.QUICKBATTERYTEST,R.drawable.firmware);
        resolutionImages.put(TestName.INTERNALSTORAGE,R.drawable.storage);
       // resolutionImages.put(ResolutionName.INTERNALSTORAGESUGGESTION,R.drawable.storage);
    }

    public static QuickBatteryTestInfo setQuickBatteryInfo() {
        QuickBatteryTestInfo quickBatteryData = new QuickBatteryTestInfo();
        Integer[] quickBatterySohRange = GlobalConfig.getInstance().getSohRange();
        BatteryDiagConfig.BatteryDiagConfigBuilder batteryDiagConfigBuilder = new BatteryDiagConfig.BatteryDiagConfigBuilder(true);
        DLog.d(TAG, "medium range: " + quickBatterySohRange[0] + " good range: " + quickBatterySohRange[1]);
        if (!(quickBatterySohRange[0] <= 0 || quickBatterySohRange[1] <= 0)) {
            if (quickBatterySohRange[0] > quickBatterySohRange[1]) {
                batteryDiagConfigBuilder.setMediumSohThreshold(quickBatterySohRange[1]);
                batteryDiagConfigBuilder.setGoodSohThreshold(quickBatterySohRange[0]);
            } else {
                batteryDiagConfigBuilder.setMediumSohThreshold(quickBatterySohRange[0]);
                batteryDiagConfigBuilder.setGoodSohThreshold(quickBatterySohRange[1]);
            }
        }
        BatteryDiagConfig batteryDiagConfig = batteryDiagConfigBuilder.build();
        ProfileManager profileManager = new ProfileManager();
        profileManager.initializeProfile(OruApplication.getAppContext(), batteryDiagConfig);
        ActivityResultInfo activityResultInfo = new QuickTestComputeEngine(
                batteryDiagConfig, BatteryUtil.getBatteryCapacity(OruApplication.getAppContext()))
                .computeQuickTestSoh();
        quickBatteryData.setSOHFromCondition(activityResultInfo.isSOHFromCondition());
        BatteryInfo batteryInfo = profileManager.getBatteryProfile().getBatteryInfo();
        QuicktestInfo quicktestInfo = batteryInfo.getQuicktestInfo();
        long batteryFullChargeCapacity = DeviceInfo.getInstance(OruApplication.getAppContext()).getSamsungActualCapacity(quicktestInfo, batteryInfo.getCapacityMah());
        if (batteryFullChargeCapacity == -1) {
            batteryFullChargeCapacity = DeviceInfo.getInstance(OruApplication.getAppContext()).getDeviceActualCapacity(quicktestInfo);
        }
        DLog.d(TAG, "Quick bettery test results :: "+activityResultInfo.toString());
        String batteryHealthStatus = activityResultInfo.getTestResult().name();
        if(batteryHealthStatus.equalsIgnoreCase("GOOD")) {
            batteryHealthStatus = OruApplication.getAppContext().getString(R.string.vgood);
        } else if(batteryHealthStatus.equalsIgnoreCase("NORMAL")) {
            batteryHealthStatus = OruApplication.getAppContext().getString(R.string.good);
        } else if(batteryHealthStatus.equalsIgnoreCase("BAD")) {
            batteryHealthStatus = OruApplication.getAppContext().getString(R.string.bad);
        } else if ("UNSUPPORTED".equalsIgnoreCase(batteryHealthStatus)){
            batteryHealthStatus = OruApplication.getAppContext().getString(R.string.unsupported);
        }
        double soh = activityResultInfo.getSoh();
        quickBatteryData.setBatteryHealth(batteryHealthStatus);
        quickBatteryData.setBatterySOH(soh);
        quickBatteryData.setBatteryDesignCapacityQuick(batteryInfo.getCapacityMah());
        quickBatteryData.setBatteryFullChargeCapacity(batteryFullChargeCapacity);
        quickBatteryData.setCurrentBatteryLevel(batteryInfo.getCurrentBatteryLevel());
        return quickBatteryData;
    }
    public static HashMap<String, Boolean> suggestionTestMap = new HashMap<String, Boolean>();
    static{
        suggestionTestMap.put(TestName.AMBIENTTEST, false);
        suggestionTestMap.put(TestName.PROXIMITYTEST, false);
        suggestionTestMap.put(TestName.USBTEST, false);
        suggestionTestMap.put(TestName.SPEAKERTEST, false);
        suggestionTestMap.put(TestName.EARPIECETEST, false);
        suggestionTestMap.put(TestName.EARPHONETEST, false);
        suggestionTestMap.put(TestName.EARPHONEJACKTEST, false);
        suggestionTestMap.put(TestName.CHARGINGTEST, false);
        suggestionTestMap.put(TestName.MICROPHONETEST, false);
        suggestionTestMap.put(TestName.MICROPHONE2TEST, false);
    }

//    Assistant key common methods Starts

    public static String getTopAppName(Context context)
    {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try
        {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            {
                strName = getTopActivityPackageNameM(context);
            }
            else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
            {
                strName = getFGPackageForLollipopDevices(context);
            }
            else
            {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        }
        catch (Exception e)
        {
            DLog.e(TAG, e.getMessage());
        }
        return strName;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getTopActivityPackageNameM(Context context)
    {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        UsageEvents usageEvents = usageStatsManager.queryEvents(currentTimeMillis - 10 * 1000, currentTimeMillis);
        ArrayList<UsageEvents.Event> eventsList = new ArrayList<UsageEvents.Event>();
        while (usageEvents.hasNextEvent())
        {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            eventsList.add(event);
        }
        DLog.d(TAG, "getTopActivityPackageNameM: " + eventsList.size());
        if (eventsList.size() > 0)
        {
            for (int i = eventsList.size() - 1; i >= 0; i--)
            {
                UsageEvents.Event event = eventsList.get(i);
                DLog.d(TAG, "getTopActivityPackageNameM:==== " + event.getEventType());
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND )
                {
                    DLog.d(TAG, "getTopActivityPackageNameM:==== " + event.getPackageName());
                    return event.getPackageName();
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
                    DLog.d(TAG, ">= Q getTopActivityPackageNameM eventtype:==== " + event.getEventType() +" packname:: "+event.getPackageName());
                    if(event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        DLog.d(TAG, ">= Q getTopActivityPackageNameM:==== " + event.getPackageName());
                        return event.getPackageName();
                    }

                }
            }
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getFGPackageForLollipopDevices(Context ctx)
    {
        String recentPkg = "";
        try
        {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
            long milliSecs = 60 * 1000L;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (!queryUsageStats.isEmpty())
            {
                DLog.i(TAG, "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            for (int i = 0; i < queryUsageStats.size(); i++)
            {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"org.pervacio.pvadiag".equals(stats.getPackageName()))
                {
                    // Log.i(TAG, "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime)
                {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
        }
        catch (Exception e)
        {
            DLog.e(TAG, e.getMessage());
        }
        return recentPkg;
    }

    public static void bringAppTaskToForeground(Activity currentActivity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            try
            {
                int taskId = currentActivity.getTaskId();
                ActivityManager manager = (ActivityManager) currentActivity.getSystemService(Context.ACTIVITY_SERVICE);
                manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
                DLog.i(TAG, "bringAppTaskToForeground :  Bringing app to foreground.");
            }
            catch (Exception e)
            {
                // There is no real threat of a crash here, but adding a catch just in case.
                DLog.i(TAG, "bringAppTaskToForeground : Exception" + e);
            }
        }
    }

//    Finish Assistant Utils Work

}

