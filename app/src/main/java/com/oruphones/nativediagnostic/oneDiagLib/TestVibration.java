package com.oruphones.nativediagnostic.oneDiagLib;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.Pair;
import android.util.SparseArray;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.audio.AudioUtils;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.utils.AppUtils;
import org.pervacio.onediaglib.utils.AppUtils.VersionUtils;

import java.util.Random;

public class TestVibration implements ITimerListener {
    private static String TAG = TestVibration.class.getSimpleName();
    private static TestVibration mVibrationManager;
    private int mDefault_VIB_FEEDBACK_MAGNITUDE = -1;
    private int mDefault_VIB_RECVCALL_MAGNITUDE = -1;
    private int mDefault_VIB_NOTIFICATION_MAGNITUDE = -1;
    public String VIB_FEEDBACK = "VIB_FEEDBACK_MAGNITUDE";
    public String VIB_RECVCALL = "VIB_RECVCALL_MAGNITUDE";
    public String VIB_NOTIFICATION = "VIB_NOTIFICATION_MAGNITUDE";
    public String SEM_VIB_NOTIFICATION = "SEM_VIBRATION_NOTIFICATION_INTENSITY";
    private static long[] BASIC_PATTERN = new long[]{0L, 1000L, 500L, 1000L, 500L, 1000L};
    private static final int MAX_VIBRATION_INTENSITY = 10000;
    private static long VIBRATION_PATTERN_TIME = 4000L;
    private DiagTimer diagTimer = new DiagTimer(this);
    private Vibrator mVibrator;
    private Handler mHandler;
    private long[] vibrationPattern;
    private SparseArray<Pair<Integer, String>> defaultSoundModesFujitsu;
    private int initialHapticFeedbackStatus = -1;
    private int initialInterruptionFilter = -1;
    private NotificationPolicyChangeReceiver notificationPolicyChangeReceiver;
    private VibrationIntensityObserver vibrationIntensityObserver = null;
    private SparseArray<Pair<Integer, String>> defaultVibrationSettings = null;
    private boolean isHybtidTest = false;
    private static int randonmNum = 0;
    private TestListener mTestFinishListener;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            TestResult result = new TestResult();
            result.setResultCode(0);
            if (TestVibration.this.isHybtidTest) {
                TestResult.setTestAdditionalInfo("" + TestVibration.randonmNum);
                DLog.d(TAG, "Vibration Number: " + TestResult.getTestAdditionalInfo());
            }

            DLog.d(TAG, "Vibration Test Result: " + result.getResultCode());
            TestVibration.this.mTestFinishListener.onTestEnd(result);
        }
    };

    private TestVibration() {
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
        this.mVibrator = (Vibrator) OruApplication.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.mHandler = new Handler();
    }

    public void setIsHybtidTest(boolean isHybtidTest) {
        this.isHybtidTest = isHybtidTest;
    }

    public void initHybtidTest() {
        randonmNum = this.getRandomNumbers(5);
        BASIC_PATTERN = new long[randonmNum * 2];

        for(int i = 0; i < randonmNum * 2; ++i) {
            if (i == 0) {
                BASIC_PATTERN[i] = 0L;
            } else {
                BASIC_PATTERN[i] = 1000L;
            }
        }

        VIBRATION_PATTERN_TIME = (long)(randonmNum * 2) * 1000L - 500L;
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        globalConfig.addItemToList("Vibration System Output : "+randonmNum);
        DLog.d(TAG, "TestVibration"+"randonmNum: " + randonmNum);
        String pattern = "BASIC_PATTERN: [";

        for(int i = 0; i < BASIC_PATTERN.length; ++i) {
            pattern = pattern + BASIC_PATTERN[i] + ",";
        }

        DLog.d(TAG, pattern + "]");
        DLog.d(TAG, "VIBRATION_PATTERN_TIME: " + VIBRATION_PATTERN_TIME);
    }

    public boolean getIsHybtidTest() {
        return this.isHybtidTest;
    }

    public static TestVibration getInstance() {
        if (mVibrationManager == null) {
            mVibrationManager = new TestVibration();
        }

        return mVibrationManager;
    }

    public void setTestFinishListener(TestListener mTestFinishListener) {
        this.mTestFinishListener = mTestFinishListener;
    }

    public void testFinished(boolean testResut) {
        TestResult result = new TestResult();
        result.setResultCode(testResut ? 0 : 1);
        if (this.mTestFinishListener != null) {
            if (this.diagTimer != null) {
                this.diagTimer.stopTimer();
            }

            this.mTestFinishListener.onTestEnd(result);
        }

    }

    public void startVibration() {
        if (this.isHybtidTest) {
            this.initHybtidTest();
        }

        if (VersionUtils.hasMarshmallow()) {
            if (this.initialInterruptionFilter == -1) {
                this.initialInterruptionFilter = AudioUtils.getInterruptionFilter(OruApplication.getAppContext());
            }

            DLog.d(TAG, "Initial Notification Interruption Filter = " + this.initialInterruptionFilter);
            if (this.initialInterruptionFilter != 1) {
                this.registerReceivers();
                boolean opState = AudioUtils.setInterruptionFilter(OruApplication.getAppContext(), 1);
                if (!opState) {
                    this.unregisterReceivers();
                    this.doVibrate();
                }
            } else {
                this.doVibrate();
            }
        } else {
            this.doVibrate();
        }

    }

    private void doVibrate() {
        if (Build.MANUFACTURER.equalsIgnoreCase("SHARP")) {
            this.initialHapticFeedbackStatus = this.getHapticFeedbackStatus();
            this.setHapticFeedbackStatus(0);
        }

        this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
        this.mVibrator.vibrate(BASIC_PATTERN, -1);
        this.mHandler.postDelayed(this.mRunnable, VIBRATION_PATTERN_TIME);
    }

    @TargetApi(23)
    private void registerReceivers() {
        if (this.notificationPolicyChangeReceiver == null) {
            this.notificationPolicyChangeReceiver = new NotificationPolicyChangeReceiver();
            IntentFilter intentFilter = new IntentFilter("android.app.action.INTERRUPTION_FILTER_CHANGED");
            intentFilter.setPriority(1000);
            OruApplication.getAppContext().registerReceiver(this.notificationPolicyChangeReceiver, intentFilter);
        }

    }

    private void unregisterReceivers() {
        if (this.notificationPolicyChangeReceiver != null) {
            OruApplication.getAppContext().unregisterReceiver(this.notificationPolicyChangeReceiver);
            this.notificationPolicyChangeReceiver = null;
        }

    }

    public void stopVibration() {
        this.unregisterReceivers();
        this.resetInterruptionFilter();
        this.diagTimer.stopTimer();
        this.mVibrator.cancel();
        this.mHandler.removeCallbacks(this.mRunnable);
    }

    public void unRegisterVibrationTest() {
        mVibrationManager = null;
    }

    public void getCurrentVibrationIntensity() {
        try {
            if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                this.mDefault_VIB_FEEDBACK_MAGNITUDE = System.getInt(OruApplication.getAppContext().getContentResolver(), this.VIB_FEEDBACK);
                this.mDefault_VIB_RECVCALL_MAGNITUDE = System.getInt(OruApplication.getAppContext().getContentResolver(), this.VIB_RECVCALL);
                this.mDefault_VIB_NOTIFICATION_MAGNITUDE = System.getInt(OruApplication.getAppContext().getContentResolver(), this.VIB_NOTIFICATION);
            }
        } catch (Settings.SettingNotFoundException var2) {
            DLog.e(TAG, "getCurrentVibrationIntensity  VIBRATION INTENSITY  NOT FOUND");
        }

    }

    public void setCurrentVibrationIntensity() {
        try {
            if (this.initialHapticFeedbackStatus != -1) {
                this.setHapticFeedbackStatus(this.initialHapticFeedbackStatus);
                this.initialHapticFeedbackStatus = -1;
            }

            if (Build.MANUFACTURER.equals("FUJITSU") && this.defaultSoundModesFujitsu != null) {
                AudioUtils.setMannerModeForFujitsu(OruApplication.getAppContext(), this.defaultSoundModesFujitsu);
                AudioUtils.broadCastMannerState(OruApplication.getAppContext(), this.defaultSoundModesFujitsu);
                this.defaultSoundModesFujitsu = null;
            }

            if (Build.MANUFACTURER.equalsIgnoreCase("Samsung") && !VersionUtils.hasMarshmallow()) {
                if (this.mDefault_VIB_FEEDBACK_MAGNITUDE != -1) {
                    System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_FEEDBACK, this.mDefault_VIB_FEEDBACK_MAGNITUDE);
                }

                if (this.mDefault_VIB_RECVCALL_MAGNITUDE != -1) {
                    System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_RECVCALL, this.mDefault_VIB_RECVCALL_MAGNITUDE);
                }

                if (this.mDefault_VIB_NOTIFICATION_MAGNITUDE != -1) {
                    System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_NOTIFICATION, this.mDefault_VIB_NOTIFICATION_MAGNITUDE);
                }
            }
        } catch (Exception var2) {
            DLog.e(TAG, "setCurrentVibrationIntensity VIBRATION INTENSITY  NOT FOUND");
        }

    }

    public void setMaxVibrationIntensity() {
        try {
            if (Build.MANUFACTURER.equalsIgnoreCase("FUJITSU")) {
                this.defaultSoundModesFujitsu = AudioUtils.getMannerModeForFujitsu(OruApplication.getAppContext());
                AudioUtils.disableMannerModeForFujitsu(OruApplication.getAppContext(), this.defaultSoundModesFujitsu);
                Thread.sleep(500L);
            }

            if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                try {
                    DLog.d(TAG, "Samsung setMaxVibrationIntensity ");
                    System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_NOTIFICATION, 10000);
                    if (!VersionUtils.hasMarshmallow()) {
                        System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_FEEDBACK, 10000);
                        System.putInt(OruApplication.getAppContext().getContentResolver(), this.VIB_RECVCALL, 10000);
                    }
                } catch (Exception var6) {
                    DLog.e(TAG, "Exception setMaxVibrationIntensity "+ var6);
                }

                if (VersionUtils.hasMarshmallow() && !AppUtils.isPermissionGranted("android.permission.WRITE_SECURE_SETTINGS")) {
                    this.defaultVibrationSettings = this.getVibrationSettings();
                    if (this.defaultVibrationSettings != null) {
                        Pair<Integer, String> pair = (Pair)this.defaultVibrationSettings.get(1);
                        if (pair != null) {
                            int deviceDefaultVibration = (Integer)pair.first;
                            String keyUri = (String)pair.second;
                            DLog.d(TAG, " deviceDefaultVibration  " + deviceDefaultVibration);
                            if (deviceDefaultVibration == 0) {
                                DLog.d(TAG, " DeviceVibrationZero  ");
                                TestResult result = new TestResult();
                                result.setResultCode(102);
                                result.setResultDescription("show vibration alert");
                                if (this.mTestFinishListener != null) {
                                    this.mTestFinishListener.onTestEnd(result);
                                }

                                this.vibrationIntensityObserver = new VibrationIntensityObserver(new Handler());
                                OruApplication.getAppContext().getContentResolver().registerContentObserver(Uri.parse("content://settings/system/" + keyUri), true, this.vibrationIntensityObserver);
                                Intent intent = new Intent("android.settings.SOUND_SETTINGS");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                OruApplication.getAppContext().startActivity(intent);
                            }
                        }
                    }
                }
            }

            this.initialHapticFeedbackStatus = this.getHapticFeedbackStatus();
            this.setHapticFeedbackStatus(0);
        } catch (Exception var7) {
            DLog.e(TAG, "setMaxVibrationIntensity  VIBRATION INTENSITY  NOT FOUND", var7);
        }

    }

    private void setHapticFeedbackStatus(int hapticFeedback) {
        if (hapticFeedback != -1) {
            try {
                System.putInt(OruApplication.getAppContext().getContentResolver(), "haptic_feedback_enabled", hapticFeedback);
            } catch (Exception var3) {
                DLog.e(TAG, "Exception while setting haptic feedback. Value = " + hapticFeedback);
            }
        }

    }

    private int getHapticFeedbackStatus() {
        return System.getInt(OruApplication.getAppContext().getContentResolver(), "haptic_feedback_enabled", -1);
    }

    public boolean isVibrationFutureAvaible() {
        try {
            if (VERSION.SDK_INT >= 11) {
                return this.mVibrator.hasVibrator();
            }

            if (this.mVibrator != null) {
                return true;
            }

            TestResult result = new TestResult();
            result.setResultCode(2);
            result.setResultDescription("featureNotAvailable");
            if (this.mTestFinishListener != null) {
                this.mTestFinishListener.onTestEnd(result);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return false;
    }

    public void timeout() {
        TestResult result = new TestResult();
        result.setResultCode(3);
        if (this.diagTimer != null) {
            this.diagTimer.stopTimer();
        }

        if (this.mTestFinishListener != null) {
            this.mTestFinishListener.onTestEnd(result);
        }

        this.diagTimer.stopTimer();
        this.mVibrator.cancel();
        this.mHandler.removeCallbacks(this.mRunnable);
    }

    private SparseArray<Pair<Integer, String>> getVibrationSettings() {
        SparseArray<Pair<Integer, String>> sparseArray = new SparseArray();
        Pair<Integer, String> pair = null;
        int mVibrationIntensity = System.getInt(OruApplication.getAppContext().getContentResolver(), this.SEM_VIB_NOTIFICATION, -1);
        pair = new Pair(mVibrationIntensity, this.SEM_VIB_NOTIFICATION);
        sparseArray.put(1, pair);
        if (mVibrationIntensity == -1) {
            mVibrationIntensity = System.getInt(OruApplication.getAppContext().getContentResolver(), this.VIB_NOTIFICATION, -1);
            pair = new Pair(mVibrationIntensity, this.VIB_NOTIFICATION);
            sparseArray.put(1, pair);
        }

        return sparseArray;
    }

    @TargetApi(23)
    private void resetInterruptionFilter() {
        if (this.initialInterruptionFilter != -1 && this.initialInterruptionFilter != 1) {
            AudioUtils.setInterruptionFilter(OruApplication.getAppContext(), this.initialInterruptionFilter);
        }

    }

    public int getRandomNumbers(int range) {
        return (new Random()).nextInt(range - 1) + 1;
    }

    private class NotificationPolicyChangeReceiver extends BroadcastReceiver {
        private NotificationPolicyChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            DLog.d(TAG, "NotificationPolicyChangeReceiver.onReceive(): current interruption filter = " + AudioUtils.getInterruptionFilter(OruApplication.getAppContext()));
            TestVibration.this.doVibrate();
        }
    }

    private class VibrationIntensityObserver extends ContentObserver {
        private VibrationIntensityObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            DLog.d(TAG, " VibrationIntensityObserver onChange  ");
            Pair<Integer, String> pair = (Pair) TestVibration.this.defaultVibrationSettings.get(1);
            String namespaceKey = (String)pair.second;
            int vibrationIntensity = System.getInt(OruApplication.getAppContext().getContentResolver(), namespaceKey, -1);
            DLog.d(TAG, " vibrationIntensity onChange  " + vibrationIntensity);
            if (vibrationIntensity > 0) {
                if (TestVibration.this.vibrationIntensityObserver != null) {
                    OruApplication.getAppContext().getContentResolver().unregisterContentObserver(TestVibration.this.vibrationIntensityObserver);
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }

                TestResult result = new TestResult();
                result.setResultCode(101);
                result.setResultDescription("relaunchApp");
                if (TestVibration.this.mTestFinishListener != null) {
                    TestVibration.this.mTestFinishListener.onTestEnd(result);
                }
            }

        }
    }
}
