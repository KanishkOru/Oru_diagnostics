package com.oruphones.nativediagnostic.util.monitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ForegroundMonitorTimerTask;

import java.util.HashSet;
import java.util.Timer;

public class MonitorServiceDocomo extends Service {

    private static String TAG = MonitorServiceDocomo.class.getSimpleName();
    private static HashSet<String> whileListPkgs = new HashSet<>();
    static {
        whileListPkgs.add("com.android.settings");
        whileListPkgs.add("com.android.phone");
        whileListPkgs.add("com.android.dialer"); /* Bug #4441 */
        whileListPkgs.add("com.sonymobile.nfclock");
        whileListPkgs.add("com.samsung.felicalock");
        whileListPkgs.add("jp.co.sharp.android.nfcsettings");
        whileListPkgs.add("com.sonymobile.gpssatellitesnotification");
        whileListPkgs.add("com.google.android.location");
        whileListPkgs.add("jp.co.nttdocomo.anshinmode");
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, MonitorServiceDocomo.class);
        context.stopService(intent);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, MonitorServiceDocomo.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        DLog.d(TAG, "MonitorService onCreate");
        super.onCreate();
        startMonitoringForegroundApp();
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String mBootCompleteReceiverString = "BootCompletedReceiver";
        if (intent != null) {
            DLog.d(TAG, "onStartCommand: intent != null");
            if (intent.hasExtra(mBootCompleteReceiverString)) {
                if (intent.getBooleanExtra(mBootCompleteReceiverString, false)) {
                    DLog.d(TAG, "onStartCommand: intent.getExtras().getBootCompletedReceiver : " + intent.getBooleanExtra(mBootCompleteReceiverString, false));
                    return START_STICKY;
                }
            } else {
                DLog.d(TAG, "onStartCommand intent.getExtras().getBootCompletedReceiver == null");
            }
        } else {
            DLog.d(TAG, "onStartCommand intent == null");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        DLog.d(TAG, "MonitorService onDestroy");
        super.onDestroy();
        try {
            cancelForegroundMonitor();
        } catch (Exception e) {
            DLog.e(TAG, "Exception : in cancelforegroungmonitor"+ e);
        }
    }




    private Timer foregroundMonitorTimer = null;

    private static final int MONITOR_INTERVAL = 1;


    private void scheduleForegroundMonitor() {
        try {
            foregroundMonitorTimer = new Timer();
            foregroundMonitorTimer.schedule(new ForegroundMonitorTimerTask(), MONITOR_INTERVAL * 1000L,
                    MONITOR_INTERVAL * 1000L);
        } catch (Exception e) {
            DLog.e(TAG, "scheduleForegroundMonitor Exception : "+e);
        }
    }

    private void cancelForegroundMonitor() {
        try {
            if (foregroundMonitorTimer != null) {
                foregroundMonitorTimer.cancel();
                foregroundMonitorTimer = null;
            }
        } catch (Exception e) {
            DLog.e(TAG, "cancelForegroundMonitor Exception : "+ e);
        }
    }


    private void startMonitoringForegroundApp() {
        scheduleForegroundMonitor();
    }

}