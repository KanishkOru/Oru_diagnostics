package com.oruphones.nativediagnostic.oneDiagLib;


import android.content.SharedPreferences;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ITimerListener;

import java.util.Timer;
import java.util.TimerTask;

public class DiagTimer {
    private ITimerListener iTimerListener;
    public static final int AUTOTEST_TIMEOUT = 10000;
    public static int MANUALTEST_TIMEOUT = 30000;
    private static final int DISPATCH_EVENT = 0;
    private static final String TAG = DiagTimer.class.getName();
    private SharedPreferences sharedPreferences = OruApplication.getAppContext().getSharedPreferences("PervacioPref", 0);
    private static final String PREF_NAME = "PervacioPref";
    private static final String ENABLE_TIMER = "enableTimer";
    private Timer timer;

    public void setDisableTimer() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean("enableTimer", true);
        editor.commit();
    }

    public void setEnableTimer() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean("enableTimer", false);
        editor.commit();
    }

    public DiagTimer(ITimerListener iTimerListener) {
        this.iTimerListener = iTimerListener;
    }

    public void startTimer(int time) {
        this.timer = new Timer();
        this.timer.schedule(new NextTask(), (long)time);
    }

    public void restartTimer(int time) {
        this.stopTimer();
        this.startTimer(time);
    }

    public void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }

    }

    class NextTask extends TimerTask {
        NextTask() {
        }

        public void run() {
            if (!DiagTimer.this.sharedPreferences.getBoolean("enableTimer", false)) {
                DLog.d(TAG, "handleMessage");
                DiagTimer.this.iTimerListener.timeout();
                DiagTimer.this.stopTimer();
            }

        }
    }
}
