package com.oruphones.nativediagnostic.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;


import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LPUtil {

    private static String TAG = LPUtil.class.getSimpleName();

    public static HashSet<String> getRecentlyUsedApps(Context ctx) {
        //Log.i("LPU", "getRecentlyUsedApps");
        try {
            HashSet<String> set = new HashSet<String>();
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            long milliSecs = 5 * 24 * 60 * 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,
                            date.getTime() - milliSecs, date.getTime());
            //Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                set.add(stats.getPackageName());
               // Log.i("LPU", "PackageName: " + stats.getPackageName());
                Date lDate = new Date(stats.getLastTimeUsed());
                //Log.i("LPU", "LastTimeUsed: " + stats.getLastTimeUsed() + " "+ lDate);
            }
            return set;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, Long> getRecentlyUsedAppsWithTimestamp(Context ctx) {
        //Log.i("LPU", "getRecentlyUsedApps");
        try {
            HashMap<String, Long> map = new HashMap<String, Long>();
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            // int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            long milliSecs = 5 * 24 * 60 * 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,
                            date.getTime() - milliSecs, date.getTime());
            DLog.i(TAG, "queryUsageStats size: " + queryUsageStats.size());
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                map.put(stats.getPackageName(), stats.getLastTimeUsed());
               // Log.i("LPU", "PackageName: " + stats.getPackageName());
                Date lDate = new Date(stats.getLastTimeUsed());
                //Log.i("LPU", "LastTimeUsed: " + stats.getLastTimeUsed() + " " + lDate);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, Long> getRecentlyUsedAppsFromScratch(Context ctx) {
        //Log.i("LPU", "getRecentlyUsedAppsFromScratch");
        try {
            HashMap<String, Long> map = new HashMap<String, Long>();
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar beginCal = Calendar.getInstance();
            beginCal.set(Calendar.DATE, 1);
            beginCal.set(Calendar.MONTH, 0);
            beginCal.set(Calendar.YEAR, 2012);
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,
                            beginCal.getTimeInMillis(), date.getTime());
            DLog.i(TAG, "getRecentlyUsedAppsFromScratch size: "
                    + queryUsageStats.size());
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                map.put(stats.getPackageName(), stats.getLastTimeUsed());
                //Log.i("LPU", "PackageName: " + stats.getPackageName());
                Date lDate = new Date(stats.getLastTimeUsed());
               // Log.i("LPU", "LastTimeUsed: " + stats.getLastTimeUsed() + " " + lDate);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFGAppPackageName(Context ctx) {
        //Log.i("LPU", "getFGAppPackageName");
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager
                    .queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                            date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                DLog.i(TAG, "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0
                        && !ctx.getPackageName().equals(stats
                                .getPackageName())) {
                   // Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Set<String> getForeGroundAppBatteryConsuming(Context ctx) {
        Set<String> batteryConsumingApps = new HashSet<String>();
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);

            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.YEAR, -1);
            long startTime = calendar.getTimeInMillis();

            UsageEvents usageEvents = usageStatsManager.queryEvents(startTime,endTime);

            while(usageEvents.hasNextEvent()){
                UsageEvents.Event e = new UsageEvents.Event();
                usageEvents.getNextEvent(e);

                if (e != null){
                    if(e.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                      //  Log.d("LPUtil", "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
                        batteryConsumingApps.add(e.getPackageName());
                    }
                }
            }

            return batteryConsumingApps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryConsumingApps;
    }

    public static Set<String> getBackgroundAppBatteryConsuming(Context ctx) {
        Set<String> batteryConsumingApps = new HashSet<String>();
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx
                    .getSystemService(Context.USAGE_STATS_SERVICE);

            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            calendar.add(Calendar.YEAR, -1);
            long startTime = calendar.getTimeInMillis();

            UsageEvents usageEvents = usageStatsManager.queryEvents(startTime,endTime);

            while(usageEvents.hasNextEvent()){
                UsageEvents.Event e = new UsageEvents.Event();
                usageEvents.getNextEvent(e);

                if (e != null){
                    if(e.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                     //   Log.d("LPUtil", "Event: " + e.getPackageName() + "\t" + e.getTimeStamp());
                        batteryConsumingApps.add(e.getPackageName());
                    }
                }
            }

            return batteryConsumingApps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryConsumingApps;
    }

    public static String _getTopActivityPackageNameM(Context context) {
        UsageStatsManager _usageStatsManager = (UsageStatsManager) (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        UsageEvents usageEvents = _usageStatsManager.queryEvents(currentTimeMillis - 10 * 1000, currentTimeMillis);
        ArrayList<UsageEvents.Event> eventsList = new ArrayList<UsageEvents.Event>();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            eventsList.add(event);
        }
        if (eventsList.size() > 0) {
            for (int i = eventsList.size() - 1; i >= 0; i--) {
                UsageEvents.Event event = eventsList.get(i);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    return event.getPackageName();
                }
            }
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean usageAccessGranted(Context context) {
        AppOpsManager appOps = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
