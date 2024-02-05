package com.oruphones.nativediagnostic.api;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.LPUtil;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.ThemeUtil;

import org.pervacio.onediaglib.diagtests.DiagTimer;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestKeys;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Pervacio on 03-10-2017.
 */
public class KeysTest extends BaseActivity implements TestListener, TestKeys.KeysResultListener {

    private final int RECENTAPPS = -1;
    private final int KEYCODE_LGDUAL = 100001;
    private final int KEYCODE_LGQUICK = 100002;
    private final int BIXBY_KEYEVENT = 1082;
    private static String TAG = KeysTest.class.getSimpleName();
    private static final int ASSISTANT_KEY=7551;

    private String mDevicekeys = "";
    private TestKeys mTestKeys;
    private ArrayList<String> mDevicekeysList;
    private Map<Integer, String> mKeys;
    private HashSet<Integer> mKeysClicked = new HashSet<Integer>();
    private String mTestName = "";
    private ManualTest manualTest;
    DiagTimer mDiagTimer;
    public static boolean AccessbilityServiceCalled = false;
    private final String bixbykeysupporteddevices[] = {"SM-G950W", "SM-G955W", "SM-G950U", "SM-G955U", "SM-G955F", "SM-G950F"};
    private ActivityManager activityManager;
    List<ActivityManager.RunningTaskInfo> recentTasks;
    private boolean mIsTestFinished=false;
    private boolean homePressed =false;
    private Timer mRelaunchTimer;
    private boolean promptedUsageStats;
    String[] assistantTopAppNames ={"com.google.android.googlequicksearchbox"};


    public void init(Activity context) {
        //  ThemeUtil.onActivityCreateSetTheme(this);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manualTest = ManualTest.getInstance(context);
        mTestKeys = TestKeys.getInstance();
        mDiagTimer = new DiagTimer(new ITimerListener() {
            @Override
            public void timeout() {
            }
        });
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onActivityCreateSetTheme(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manualTest = ManualTest.getInstance(this);
        mTestKeys = TestKeys.getInstance();
        mDiagTimer = new DiagTimer(new ITimerListener() {
            @Override
            public void timeout() {

            }
        });
        activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        //Back is disabled.
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.MODEL.equalsIgnoreCase("XT1563")) {
            disableKeyguard();
        }
        if (requireTopActivityName() && !grantUsageStates(this)) {
            if(promptedUsageStats) {
                mTestKeys.testFinished(false);
            } else {
                displayPermissionDialog("Permission Required!", "Please enable usage stats permission to continue diagnostics.", 1);
            }
        }
        if (mTestKeys.isAccebilityServiceRequired() && mTestName.equalsIgnoreCase(TestName.SOFTKEYTEST) && !AccessbilityServiceCalled) {
            if (mDiagTimer != null)
                mDiagTimer.setDisableTimer();
            displayPermissionDialog("Permission Required!", getResources().getString(R.string.enable_accessibility_service), 0);
        }
    }

    public static boolean grantUsageStates(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager
                    .getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context
                    .getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                        applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void displayPermissionDialog(String title, String message, final int requestCode) {
        CommonUtil.DialogUtil.showAlert(this, title, message, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int id) {
                if (requestCode == 1) {
                    promptedUsageStats = true;
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), requestCode);
                } else {
                    startActivityForResult(
                            new Intent(
                                    Settings.ACTION_ACCESSIBILITY_SETTINGS), requestCode);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mTestKeys != null&& !mIsTestFinished)
                mTestKeys.registerSystemDialogReceiver();
        }
        if(mRelaunchTimer != null)
            mRelaunchTimer.cancel();
        isTestPass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*unregisterReceivers();
        if (mTestKeys != null)
            mTestKeys.testFinished(false);*/
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DLog.d(TAG, "pressed Key " + mKeys.get(keyCode) + " ----> " + keyCode);
        if (mTestKeys != null && !mIsTestFinished)
            mTestKeys.keyDownEvent(keyCode);
        return true;
    }

    @Override
    public void onKeyResultStatus(int keyCode) {
        if (keyCode == RECENTAPPS && Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (mTestKeys != null)
                mTestKeys.unRegisterSystemDialogReceiver();
        }
        if (keyCode == 963) {
            if (mDiagTimer != null) {
                if(!BaseActivity.isAssistedApp)
                    mDiagTimer.setEnableTimer();
            }
            Intent intent = getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            updateStatus(keyCode);
        }

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
        //isTestPass();
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        DLog.d(TAG, "pressed dispatch " + mKeys.get(event.getKeyCode()) + " ----> " + event.getKeyCode());
        if (event.getKeyCode() != KeyEvent.KEYCODE_POWER&& mTestKeys!=null) {
            if (!mIsTestFinished&& mTestKeys!=null)
                mTestKeys.keyDispatchEvent(event.getKeyCode());
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onTestStart() {
        //Called when test is started.
    }

    @Override
    public void onTestEnd(TestResult testResult) {
        DLog.d(TAG, "on test end " + testResult.getResultCode() + " " + testResult.getResultDescription());
        if(!mIsTestFinished) {
            mIsTestFinished=true;
            Bundle bundle = new Bundle();
            if (testResult.getResultCode() == TestResult.RESULT_PASS) {
                bundle.putString("result", com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
            } else if (testResult.getResultCode() == TestResult.RESULT_ERROR_TIME_OUT) {
                bundle.putString("result", com.oruphones.nativediagnostic.models.tests.TestResult.TIMEOUT);
            }
            stopTest();
            if (manualTest != null) {
                //  manualTest.updateKeysTestResults(bundle);
                DLog.d(TAG, "calling keysresult:");
                manualTest.updateKeysTestResults(testResult.getResultCode(), testResult.getResultDescription());
            }
        }
    }

    private void isTestPass() {
        DLog.d(TAG, "is test pass " + mKeysClicked.size() + " " + mDevicekeysList.size());
        if (mKeysClicked.size() == mDevicekeysList.size()) {
            if (mTestKeys != null && !mIsTestFinished)
                mTestKeys.testFinished(true);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            //
        } else {
            //Log.d("BIXBYKEY", "Window focus Lost:  ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isBixbyKeyAvailable() && (getTopActivityName().contains("com.samsung.android.app.spage")|| getTopActivityName().contains("com.samsung.android.bixby.agent") || getTopActivityName().equals("com.samsung.android.bixby.onboarding.provision.ProvisionActivity"))) {
                // mTestKeys.keyDownEvent(BIXBY_KEYEVENT);
                reStartTimer();
                updateStatus(BIXBY_KEYEVENT);
                relaunchApp();
            }
            if(isAssistantKeyTestAvailable()){
                DLog.i(TAG,"Calling isAssistantKeyTestAvailable specialkeysDetection");
                specialkeysDetection();
            }
        }
    }

    private void reStartTimer() {
        if (mTestKeys != null) {
            DLog.d(TAG, "reStartTimer");
            mTestKeys.reStartTimer();
        }
    }

    private void acquireWakeLock() {
        // isTestPass();
        PowerManager mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPM.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "pvawake");
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    private void updateStatus(final int keyCode) {
        if (mDevicekeysList.contains(mKeys.get(keyCode)) && !mIsTestFinished) {
            mKeysClicked.add(keyCode);
            Bundle bundle = new Bundle();
            bundle.putString("result", "KeyEvent");
            bundle.putString("message", mKeys.get(keyCode));
            if (manualTest != null)
                manualTest.updateKeysTestResults(bundle);

            if (keyCode == KeyEvent.KEYCODE_POWER) {
                acquireWakeLock();
                isTestPass();
            } else if (keyCode == RECENTAPPS) {
                Toast.makeText(KeysTest.this, getResources().getString(R.string.relauch_msg), Toast.LENGTH_SHORT).show();
                homePressed =false;
                relaunchApp();
            } else if (keyCode == KeyEvent.KEYCODE_HOME) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(KeysTest.this, getResources().getString(R.string.relauch_msg), Toast.LENGTH_SHORT).show();
                        homePressed =true;
                        relaunchApp();
                    }
                };
                (new Handler()).postDelayed(runnable, 100);

            }else{
                isTestPass();
            }
        }
    }

    private void prepareKeyslist() {
        mKeys = new HashMap<Integer, String>();
        mKeys.put(RECENTAPPS, "RECENTAPPS");
        mKeys.put(888, "SHARE");
        mKeys.put(KeyEvent.KEYCODE_ENDCALL, "ENDCALL");
        mKeys.put(KeyEvent.KEYCODE_CAMERA, "CAMERA");
        mKeys.put(KeyEvent.KEYCODE_VOLUME_DOWN, "VOLUME_DOWN");
        mKeys.put(KeyEvent.KEYCODE_POWER, "POWER");
        mKeys.put(KeyEvent.KEYCODE_VOLUME_UP, "VOLUME_UP");
        mKeys.put(KeyEvent.KEYCODE_SEARCH, "SEARCH");
        mKeys.put(KeyEvent.KEYCODE_BACK, "BACK");
        mKeys.put(KeyEvent.KEYCODE_HOME, "HOME");
        mKeys.put(KeyEvent.KEYCODE_MENU, "MENU");
        mKeys.put(KeyEvent.KEYCODE_FOCUS, "FOCUS");
        mKeys.put(KeyEvent.KEYCODE_CALL, "CALL");
        mKeys.put(KeyEvent.KEYCODE_DPAD_CENTER, "OK");
        mKeys.put(KeyEvent.KEYCODE_A, "A");
        mKeys.put(KeyEvent.KEYCODE_B, "B");
        mKeys.put(KeyEvent.KEYCODE_C, "C");
        mKeys.put(KeyEvent.KEYCODE_D, "D");
        mKeys.put(KeyEvent.KEYCODE_E, "E");
        mKeys.put(KeyEvent.KEYCODE_F, "F");
        mKeys.put(KeyEvent.KEYCODE_G, "G");
        mKeys.put(KeyEvent.KEYCODE_H, "H");
        mKeys.put(KeyEvent.KEYCODE_I, "I");
        mKeys.put(KeyEvent.KEYCODE_J, "J");
        mKeys.put(KeyEvent.KEYCODE_K, "K");
        mKeys.put(KeyEvent.KEYCODE_L, "L");
        mKeys.put(KeyEvent.KEYCODE_M, "M");
        mKeys.put(KeyEvent.KEYCODE_N, "N");
        mKeys.put(KeyEvent.KEYCODE_O, "O");
        mKeys.put(KeyEvent.KEYCODE_P, "P");
        mKeys.put(KeyEvent.KEYCODE_Q, "Q");
        mKeys.put(KeyEvent.KEYCODE_R, "R");
        mKeys.put(KeyEvent.KEYCODE_S, "S");
        mKeys.put(KeyEvent.KEYCODE_T, "T");
        mKeys.put(KeyEvent.KEYCODE_U, "U");
        mKeys.put(KeyEvent.KEYCODE_V, "V");
        mKeys.put(KeyEvent.KEYCODE_W, "W");
        mKeys.put(KeyEvent.KEYCODE_X, "X");
        mKeys.put(KeyEvent.KEYCODE_Y, "Y");
        mKeys.put(KeyEvent.KEYCODE_Z, "Z");
        mKeys.put(KeyEvent.KEYCODE_1, "1");
        mKeys.put(KeyEvent.KEYCODE_2, "2");
        mKeys.put(KeyEvent.KEYCODE_3, "3");
        mKeys.put(KeyEvent.KEYCODE_4, "4");
        mKeys.put(KeyEvent.KEYCODE_5, "5");
        mKeys.put(KeyEvent.KEYCODE_6, "6");
        mKeys.put(KeyEvent.KEYCODE_7, "7");
        mKeys.put(KeyEvent.KEYCODE_8, "8");
        mKeys.put(KeyEvent.KEYCODE_9, "9");
        mKeys.put(KeyEvent.KEYCODE_0, "0");
        mKeys.put(KeyEvent.KEYCODE_DPAD_DOWN, "DPAD_DOWN");
        mKeys.put(KeyEvent.KEYCODE_DPAD_LEFT, "DPAD_LEFT");
        mKeys.put(KeyEvent.KEYCODE_DPAD_RIGHT, "DPAD_RIGHT");
        mKeys.put(KeyEvent.KEYCODE_DPAD_UP, "DPAD_UP");
        mKeys.put(KeyEvent.KEYCODE_ENTER, "ENTER");
        mKeys.put(KeyEvent.KEYCODE_SPACE, "SPACE");
        mKeys.put(KeyEvent.KEYCODE_COMMA, "COMMA");
        mKeys.put(KeyEvent.KEYCODE_ALT_LEFT, "ALT_LEFT");
        mKeys.put(KeyEvent.KEYCODE_SHIFT_LEFT, "SHIFT_LEFT");
        mKeys.put(KeyEvent.KEYCODE_SHIFT_RIGHT, "SHIFT_RIGHT");
        mKeys.put(KeyEvent.KEYCODE_ALT_RIGHT, "ALT_RIGHT");
        mKeys.put(KeyEvent.KEYCODE_PERIOD, "PERIOD");
        mKeys.put(KeyEvent.KEYCODE_DEL, "DEL");
        mKeys.put(KeyEvent.KEYCODE_APOSTROPHE, "APOSTROPHE");
        mKeys.put(KeyEvent.KEYCODE_TAB, "TAB");
        mKeys.put(KeyEvent.KEYCODE_FUNCTION, "FUNCTION");
        mKeys.put(KeyEvent.KEYCODE_BRIGHTNESS_DOWN, "VOICE_INPUT");
        mKeys.put(KeyEvent.KEYCODE_ZENKAKU_HANKAKU, "EMAIL");
        mKeys.put(KeyEvent.KEYCODE_VOICE_ASSIST, "DUAL");
        mKeys.put(KeyEvent.KEYCODE_PAIRING, "QUICK_BUTTON");
        mKeys.put(KeyEvent.KEYCODE_12, "RING");
        mKeys.put(KeyEvent.KEYCODE_SLEEP, "TALK");
        mKeys.put(361, "MULTIWINDOW");
        mKeys.put(362, "CAPTURE");
        if (Build.MODEL.equals("SGH-T589")) {
            mKeys.put(94, "BROWSER");
            mKeys.put(57, "ALT_LEFT");
            mKeys.put(65, "ENVELOPE");
            mKeys.put(95, "FACEBOOK");
            mKeys.put(84, "SEARCH");
            mKeys.put(96, "SMILEY");
            mKeys.put(63, "SYM");
            mKeys.put(77, "AT");
        }

        if (Build.MODEL.equals("STV100-3")) {
            mKeys.put(164, "MUTE");
        }
        if (Build.MODEL.equalsIgnoreCase("XP7700")) {
            mKeys.put(KeyEvent.KEYCODE_12, "RING");
            mKeys.put(KeyEvent.KEYCODE_WAKEUP, "WAKE");
        }

        if (Build.MODEL.equalsIgnoreCase("E6560C") && DeviceInfo.getInstance(this).get_model().contains("LMY47V")) {
            mKeys.put(260, "RING");
            mKeys.put(261, "TALK");
        }

//		if (Build.MODEL.equals("LG-D415")) {
        mKeys.put(532, "QUICK_BUTTON");

        if (Build.MODEL.equals("DROID4")) {
            mKeys.put(236, "CAPS");
            mKeys.put(63, "SYM");
            mKeys.put(69, "MINUS");
            mKeys.put(70, "EQUALS");
            mKeys.put(76, "SLASH");
        }
        //23405 - HARD KEYS test is missing keys, and SOFT KEYS is missing
        if (Build.MODEL.equals("MB632")) {
            mKeys.put(111, "VOICE_INPUT");
            mKeys.put(77, "AT");
        }
        // Issue 3275 fixed
        if (Build.MODEL.equals("SGH-T699")) {
            mKeys.put(220, "EMAIL");
            mKeys.put(229, "VOICE_INPUT");
            mKeys.put(235, "TEXT");
            mKeys.put(236, "QUESTION");
        }
        if (Build.MODEL.equals("HTC_Amaze_4G")) {
            mKeys.put(228, "CAMCORDER");
        }
        // Issue 12656 fixed
        if ("LG-D335".equalsIgnoreCase(Build.MODEL) || ("LG-D690".equalsIgnoreCase(Build.MODEL)) || ("LG-H818").equalsIgnoreCase(Build.MODEL)) {
            mKeys.put(KEYCODE_LGDUAL, "LGDUAL");
        }
        if ("LG-E988".equalsIgnoreCase(Build.MODEL)) {
            mKeys.put(KEYCODE_LGQUICK, "QUICK_BUTTON");
        }
        if ("STV100-3".equalsIgnoreCase(Build.MODEL)) {
            mKeys.put(KeyEvent.KEYCODE_VOLUME_MUTE, "MUTE");
            mKeys.put(KeyEvent.KEYCODE_SHIFT_LEFT, "SHIFT_LEFT");
            mKeys.put(KeyEvent.KEYCODE_SHIFT_RIGHT, "SHIFT_RIGHT");
            mKeys.put(KeyEvent.KEYCODE_4, "SPEAKER");
            mKeys.put(KeyEvent.KEYCODE_0, "MIC");
            mKeys.put(KeyEvent.KEYCODE_SYM, "SYM");
        }
        if("E6830".equalsIgnoreCase(Build.MODEL))
        {
            mKeys.put(KeyEvent.KEYCODE_SOFT_SLEEP,"SPEAKER");
            mKeys.put(KeyEvent.KEYCODE_CUT, "PROGRAMMABLE");
        }
        if (Build.MODEL.equalsIgnoreCase("SM-G870W")) {
            mKeys.put(1015, "ACTIVE");
        }
        if (isBixbyKeyAvailable())
            mKeys.put(BIXBY_KEYEVENT, "BIXBY");
        if(isGoogleAssistantKeyAvailable()) {
            mKeys.put(ASSISTANT_KEY, "ASSISTANT");
            DLog.i(TAG,"ASSISTANT is added to mKeys Map");
        }
    }


    public ArrayList<String> getKeysList() {
        ArrayList<String> keyslist = new ArrayList<>();
        if (mDevicekeys != null) {
            mDevicekeys = mDevicekeys.replaceAll(",,", ",");
            mDevicekeys = mDevicekeys.replaceAll(",,", ",");       //Required two times to get single comma seperated keys.
            mDevicekeys = mDevicekeys.replaceAll("\n", "");
            mDevicekeys = mDevicekeys.replaceAll("\t", "");
            mDevicekeys = mDevicekeys.replaceAll(" ", "");
            keyslist = new ArrayList<String>(Arrays.asList(mDevicekeys.split(",")));
/*        if (mTestName.equalsIgnoreCase(PDConstants.SOFTKEYTEST)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && conditionCheckForMenuKey()) {
                if (!ViewConfiguration.get(KeysTest.this).hasPermanentMenuKey() && keyslist.contains("MENU") && !keyslist.contains("RECENTAPPS")) {
                    keyslist.remove("MENU");
                    keyslist.add("RECENTAPPS");
                } else
                    return keyslist;
            } else
                return keyslist;
        }*/
        }
        return keyslist;
    }

    private void disableKeyguard() {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
            lock.disableKeyguard();
        } catch (Exception e) {
        }
    }

    private boolean conditionCheckForMenuKey() {
        String phoneModel = DeviceInfo.getInstance(this).get_model();
        ArrayList<String> menuKeyFalseDevices = new ArrayList(Arrays.asList("LG-D950"));
        if (menuKeyFalseDevices.contains(phoneModel)) {
            return false;
        }
        return true;
    }


    private void unregisterReceivers() {
        try {
            if(mTestKeys!=null) {
                DLog.d(TAG,"@@@@@@@@@@@@@@@");
                mTestKeys.removeKeysTestListener();
                mTestKeys.unRegisterSystemDialogReceiver();
                mTestKeys.unRegisterScreenOffReceiver();
//                mTestKeys.unRegisterSonyRecentAppReceiver();
                mTestKeys.unRegisterLgQuickButton();
                mTestKeys.unRegisterLgDualKey();
            }
        } catch (Exception e) {
        }
    }

    private void registerReceivers() {
        DLog.d(TAG,"@@@@@@@@@@@@@@@#");
        mTestKeys.addKeysTestListener(this);
        mTestKeys.setTestFinishListener(this);
        mTestKeys.registerScreenOffReceiver();
        mTestKeys.registerSystemDialogReceiver();
//        if (mTestKeys.isAccebilityServiceRequired()) {
//            mTestKeys.registerSonyRecentAppReceiver();
//        }
        mTestKeys.registerLgQuickButton();
        mTestKeys.registerLgDualKey();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.MODEL.equalsIgnoreCase("LG-E988")) {
            String QUICK_BUTTON = "com.lge.QuickClip.QuickClipActivity";
            if (getTopActivityName().equalsIgnoreCase(QUICK_BUTTON)) {
                mTestKeys.keyDownEvent(225);
                relaunchApp();
            }
        } else if (Build.MODEL.equalsIgnoreCase("LG-H818")) {
            String LG_DualKey = "com.lge.networksettings";
            if (getTopActivityName().equalsIgnoreCase(LG_DualKey)) {
                mTestKeys.keyDownEvent(KEYCODE_LGDUAL);
                relaunchApp();
            }
        } else if (Build.MODEL.equalsIgnoreCase("A37f")) {
            String recentKey = "com.coloros.recents";
            if (getTopActivityName().equalsIgnoreCase(recentKey)) {
                mTestKeys.keyDownEvent(RECENTAPPS);
            }
        }
    }


    private boolean requireTopActivityName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if ((Build.MODEL.equalsIgnoreCase("LG-H818") || Build.MODEL.equalsIgnoreCase("A37f")) && mTestName.equalsIgnoreCase(TestName.SOFTKEYTEST)) {
                return true;
            }
            if ((isBixbyKeyAvailable()|| Build.MODEL.equalsIgnoreCase("LG-E988") )&& mTestName.equalsIgnoreCase(TestName.HARDKEYTEST)) {
                return true;
            }
        }
        return false;
    }


    public String getTopActivityName() {
        final ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            String topActivityName = "";
            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    return LPUtil._getTopActivityPackageNameM(this);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    return LPUtil.getFGAppPackageName(this);
                } else {
                    topActivityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
                    return topActivityName;
                }
            } catch (Exception e) {
            }
            return "";
        }
        return "";
    }


    public void performTest(String testName) {
        mTestName = testName;
        mIsTestFinished=false;
        if (mTestName.equalsIgnoreCase(TestName.HARDKEYTEST)) {
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
            DLog.d("Key Test",mDevicekeys);
        } else {
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
        }
        mDevicekeysList = getKeysList();
        prepareKeyslist();
        registerReceivers();
    }

    public boolean isBixbyKeyAvailable() {
        //return Arrays.asList(bixbykeysupporteddevices).contains(Build.MODEL);
        return mDevicekeysList.contains("BIXBY");
    }

    public boolean isGoogleAssistantKeyAvailable() {
        return mDevicekeysList.contains("ASSISTANT");
    }


    public int getTask_id() {
        int taskid = 0;
        String ourPkgName = getApplicationContext().getPackageName();
        recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < recentTasks.size(); i++) {
            String pkgName = recentTasks.get(i).topActivity.getPackageName();
            if ("E6830".equalsIgnoreCase(Build.MODEL)) {
                if (ourPkgName.equals(pkgName)) {
                    taskid = recentTasks.get(i).id;
                    break;
                }
            } else {
                if (recentTasks.get(i).baseActivity.toShortString().indexOf(getPackageName()) > -1) {
                    taskid = recentTasks.get(i).id;
                    break;
                }
            }
        }
        return taskid;
    }



    public void relaunchApp() {
        final int task_id = getTask_id();
        mRelaunchTimer = new Timer();
        recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        mRelaunchTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    if ("LG-D855".equals(Build.MODEL)) {
                        activityManager.moveTaskToFront(task_id,
                                ActivityManager.MOVE_TASK_WITH_HOME);
                        recentTasks = activityManager
                                .getRunningTasks(Integer.MAX_VALUE);
                        mRelaunchTimer.cancel();
                    } else if (recentTasks.get(0).id == task_id) {
                        if("E6830".equalsIgnoreCase(Build.MODEL))
                            Thread.sleep(5000);
                        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(closeDialog);
                        activityManager.moveTaskToFront(task_id,
                                ActivityManager.MOVE_TASK_WITH_HOME);
                        recentTasks = activityManager
                                .getRunningTasks(Integer.MAX_VALUE);
                        //timer.cancel();
                    } else {
                        if (recentTasks.get(0).id != task_id) {
                            if("E6830".equalsIgnoreCase(Build.MODEL)&& homePressed)
                                Thread.sleep(5000);
                            activityManager.moveTaskToFront(task_id,
                                    ActivityManager.MOVE_TASK_WITH_HOME);
                            recentTasks = activityManager
                                    .getRunningTasks(Integer.MAX_VALUE);
                        } else {
                            mRelaunchTimer.cancel();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mRelaunchTimer.cancel();
                }

            }
        },500,100);
    }

    public void stopTest() {
        unregisterReceivers();
        if (mTestKeys != null) {
            mTestKeys.testFinished(false);
        }
    }
    public void resumeTest(String testName){
        if(mTestKeys!=null){
            DLog.d(TAG,"Calling resumeTest");
            registerReceivers();
            mTestKeys.reStartTimer();
            mIsTestFinished=false;
        }
    }

    Handler topActivityhandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            DLog.i(TAG, "topActivityhandler::" + ODDUtils.getTopAppName(KeysTest.this));
            /*if (Arrays.asList(bixbyTopAppNames).contains(ODDUtils.getTopAppName(HardKeysTestActivity.this)))
            {
                Log.i(TAG, "handleMessage: Entered bixby");
                topActivityhandler.removeMessages(0);
                ODDUtils.bringAppTaskToForeground(HardKeysTestActivity.this);


                changeBG(BIXBY_KEY);
            }
            else */
            if (Arrays.asList(assistantTopAppNames).contains(ODDUtils.getTopAppName(KeysTest.this)))
            {
                DLog.i(TAG, "handleMessage: Entered assistant");
                topActivityhandler.removeMessages(0);
                ODDUtils.bringAppTaskToForeground(KeysTest.this);
//            changeBG(ASSISTANT_KEY);
                updateStatus(ASSISTANT_KEY);
            }else{
                DLog.i(TAG, "handleMessage: Not bixby / Assistant ");
                try
                {
                    if (topActivityhandler != null)
                    {
                        topActivityhandler.sendEmptyMessageDelayed(0, 100);
                    }
                }
                catch (Exception e)
                {
                    DLog.e(TAG, "topActivityhandler: Exception" + e);
                }
            }
        }
    };

    //Detetecting Assistant key & Bixby key
    private void specialkeysDetection()
    {
        String topActivityName = ODDUtils.getTopAppName(KeysTest.this);
        DLog.i(TAG, "specialkeysDetection: " + topActivityName);
        if (Arrays.asList(assistantTopAppNames).contains(topActivityName))
        {
            ODDUtils.bringAppTaskToForeground(KeysTest.this);
            DLog.i(TAG,"Calling updateStatus(ASSISTANT_KEY);");
            updateStatus(ASSISTANT_KEY);
        }
        else
        {
            try
            {
                if (topActivityhandler != null)
                {
                    topActivityhandler.sendEmptyMessageDelayed(0, 500);
                }
            }
            catch (Exception e)
            {
                DLog.e(TAG, "specialkeysDetection: Exception " + e);
            }
        }
    }

    private boolean isAssistantKeyTestAvailable()
    {
        if( mKeys.get(ASSISTANT_KEY)!=null)
            return true;
        else
            return false;
    }

}
