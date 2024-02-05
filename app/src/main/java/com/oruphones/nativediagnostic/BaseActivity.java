package com.oruphones.nativediagnostic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.Storage.StorageResolutionInfoPOJO;
import com.oruphones.nativediagnostic.api.AutoTest;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.history.History;
import com.oruphones.nativediagnostic.home.HomeActivity;
import com.oruphones.nativediagnostic.manualtests.ManualTestsTryActivity;
import com.oruphones.nativediagnostic.models.AbortReasons;
import com.oruphones.nativediagnostic.models.PDAppResolutionInfo;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.result.ResultsActivity;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.Constants;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.GIFMovieView;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.PreferenceHelper;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.ThemeUtil;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.util.monitor.UserInteractionMonitor;

import org.pervacio.onediaglib.atomicfunctions.AFGPS;
import org.pervacio.onediaglib.atomicfunctions.AFNFC;
import org.pervacio.onediaglib.audio.AudioUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public abstract class BaseActivity extends AppCompatActivity {



    protected boolean isKeyTestActivity;
    public static final int TEST_ICON = 1;
    public static final int TEST_TRY_IMAGE = 2;
    public static final int TEST_TRY_MESAGE = 3;
    public static final int TEST_RESULT_IMAGE = 4;
    public static final int TEST_RESULT_MESAGE = 5;
    public static final int TEST_INPROGESS_MESAGE = 6;
    public static final int TEST_TIMEOUT_MESAGE = 7;
    public static final int TEST_TIMEOUT_IMAGE = 8;


    public static final String TEST_NAME = "testname";
    public static final String TEST_RESULT = "result";
    public static final String TAG = "BaseActivity";
    public static long availableRam = -1;
    public static PreferenceHelper preferenceHelper;
    public static ArrayList<PDAppResolutionInfo> pdAppResolutionInfosFromServer;
    public static boolean isStorageDataPrepareCompleted = false;
    public static StorageResolutionInfoPOJO storageResolutionInfoPOJO = null;
    public static ArrayList<String> selectedManualTests = new ArrayList<String>();
    public static ArrayMap<String, String> selectedManualTestsResult = new ArrayMap<>();
    public static boolean manualStart = false;

    public static String mCurrentManualTest = null;
    public static Dialog myDialog = null;
    public static Context context;
    public static boolean isAssistedApp;
    public static boolean isTradeInApp;
    public static boolean needDebugSupport = false;
    public Toolbar mToolbar = null;
    public PervacioTest pervacioTest;
    public GlobalConfig globalConfig;


    public static boolean onCall = false;
    private static myPhoneStateChangeListener callStateListener;
    public static final int ROBOTO_LIGHT = 0;
    public static final int ROBOTO_MEDIUM = 1;
    public static final int ROBOTO_REGULAR = 2;
    public static final int ROBOTO_THIN = 3;
    public static final int AILERON_THIN = 4;
    public static final int AILERON_REGULAR = 5;
    public static final int AILERON_LIGHT = 6;
    public static final int OPENSANS_REGULAR = 7;
    public static final int SSF_MEDIUM = 25;
    public static final int OPENSANS_MEDIUM = 8;
    public static final int OPENSANS_LIGHT = 9;
    PowerManager.WakeLock mWakeLock;
    public static final int WRITE_SETTINGS = 1;
    public static final int USAGE_STATS = 2;
    public static final int ALL_FILES_ACCESS_PERMISSION = 3;
    public AlertDialog alertDialog = null;
    private AlertDialog settingsDialog;
    Dialog dialog = null;
    ProgressBar manual_Progressbar;
    int resultCode = 2222;
    public Dialog networkDialgoue;

    public boolean initServiceStarted = false;
    public boolean permissionsStarted = false;
    public boolean serviceInterrupted = false;
    private ProgressDialog progressDialog;
    public AirplaneOn airplaneMode = null;
    //    public static boolean IsOnDeviceApp = false;
    public static final String mBroadcastSessionExpired = "com.careondevice.sessionExpired";
    public static String summaryType = Constants.DIAGNOSTICS_RESOLUTIONS;
    public static boolean simcardavailable = true;
    public static boolean checkForGPS = false;
    public static boolean settings = false;
    public boolean permissionDenied = false;
    public boolean showPermisionUi = true;
    public static Activity mCurrentTestActivity;
    private static final int PERMISSIONS_REQUEST = 158;
    protected static UserInteractionMonitor mInteractionMonitor = UserInteractionMonitor.getInstance();
    protected int skip_msg_id = R.string.skip_status;

    protected List<String> TestList = new ArrayList<>();

    public static void showEarPhonePlugDiaglogue(String title, String message) {
        myDialog = new Dialog(context);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.cutom_alert_dialog);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView blAlertHead = (TextView) myDialog
                .findViewById(R.id.BL_alert_head);
        TextView blAlertText = (TextView) myDialog
                .findViewById(R.id.BL_alert_text);

        blAlertHead.setText(title);
        blAlertText.setText(message);
        Button blAlertYes = (Button) myDialog.findViewById(R.id.BL_alert_yes);
        Button blAlertNo = (Button) myDialog.findViewById(R.id.BL_alert_no);

        blAlertNo.setVisibility(View.GONE);
        blAlertYes.setText(context.getResources().getString(R.string.str_ok));

        blAlertYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
    }

    public void updateTestResult(String testName, String testResult) {
        updateTestResult(testName, testResult, true);
    }

    public void updateTestResult(String testName, String testResult, boolean sendResultToServer) {
        //this (isAssistedApp)check will come only when we are using assisted, because once test is complete we need to sedn the status to server.
        if (isAssistedApp && sendResultToServer) {
            PDTestResult pdTestResult = new PDTestResult();
            pdTestResult.setName(testName);
            pdTestResult.setStatus(testResult);
            DLog.d(TAG, "on update test result");
            CommandServer.getInstance(this).postEventData("TEST_RESULT", pdTestResult);
        }
        TestInfo testInfo = pervacioTest.getManualTestResult().get(testName);
        if (testInfo == null) {
            testInfo = new TestInfo(testName, globalConfig.getTestDisplayName(testName), testResult);
        }
        if (!TestResult.SKIPPED.equalsIgnoreCase(testResult)) {
            testInfo.setTestEndTime(System.currentTimeMillis());
        }
        testInfo.setTestResult(testResult);
        pervacioTest.getManualTestResult().put(testName, testInfo);
    }

    public class myPhoneStateChangeListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            // DLog.d();(TAG, "Call State Listener --  "+state);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    onCall = true;
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    onCall = false;
                    break;
                default:
                    break;
            }
        }
    }

    public static void setIsAssistedApp(String product) {
        BaseActivity.isAssistedApp = "ASSISTED".equalsIgnoreCase(product);
        BaseUnusedActivity.setIsAssistedApp(product);
    }

    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
//                    save error to file
                    String error = ex.toString();
                    String fileName = "oruError.txt";
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                        outputStream.write(error.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
        //  Toast.makeText(BaseActivity.this, "BaseActivity", Toast.LENGTH_SHORT).show();
        ThemeUtil.onActivityCreateSetTheme(this);
        //  CommandServer cs = new CommandServer(this,uiHandler);
        CommandServer.getInstance(getApplicationContext()).setUIHandler(
                uiHandler);
        CommandServer.getInstance(getApplicationContext()).setBaseUIHandler(uiHandler);
/*        if (savedInstanceState != null && !isAssistedApp) {
            Intent intent = new Intent(this, PinValidationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }*/
        pervacioTest = PervacioTest.getInstance();
        globalConfig = pervacioTest.getGlobalConfig();
        LayoutInflater _layoutInflator = (LayoutInflater) getSystemService
                (LAYOUT_INFLATER_SERVICE);
        View convertView = _layoutInflator.inflate(getLayoutResource(), null);
        wakeDeviceScreen();
        //  convertView.setBackgroundResource(R.color.white);
        int convertViewId = convertView.getId();

        int prodconfig=R.layout.diag_permission_prompt;
        setContentView(convertView);
        context = this;
//        TODO
        try {
            if (!isFullscreenActivity() && convertViewId != prodconfig) {
                mToolbar = (Toolbar) findViewById(R.id.toolbar);
                mToolbar.setTitleTextAppearance(this, R.style.textStyle_title);
                //  mToolbar.inflateMenu(R.menu.main_menu);
                setSupportActionBar(mToolbar);
                ActionBar actionBar = getSupportActionBar();
                preferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
                if ("Manual Tests Running".equalsIgnoreCase(PervacioTest.getInstance().getSessionStatus())) {
                    ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.manual_tests);
                    mToolbar.setBackgroundResource(R.drawable.toolbar_bg);
                    //  ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setTextColor(getResources().getColor(R.color.white));

                } else {
                    ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(getToolBarName());
                }
                setFontToView(((TextView) mToolbar.findViewById(R.id.toolbar_title)), OPENSANS_REGULAR);
//            actionBar.setTitle(getToolBarName());
                if (!isAssistedApp && ((setBackButton() && !Util.needToRemoveBackButton()) || setHomeButton())) {
                    int icon_id = R.drawable.ic_back;
                    if (setHomeButton()) {
                        icon_id = R.drawable.ic_home;
                    }
                    Drawable icon = ContextCompat.getDrawable(context, icon_id);
                    if (icon != null) {
                        icon.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
                        actionBar.setHomeAsUpIndicator(icon);
                        actionBar.setDisplayShowHomeEnabled(true);
                    }

                } else {
                    Drawable icon = getResources().getDrawable(R.drawable.logo_sprint);
                    actionBar.setHomeAsUpIndicator(icon);
                    actionBar.setDisplayShowHomeEnabled(false);
                }
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (setBackButton() && !Util.needToRemoveBackButton()) {
                            onBackPressed();
                        } else if (setHomeButton()) {
                            Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
            }
        } catch (Exception e) {
            DLog.e("crash","");
        }
//        if (callStateListener == null) {
//            callStateListener = new myPhoneStateChangeListener();
//            TelephonyManager telephonyManager = (TelephonyManager) this
//                    .getSystemService(Context.TELEPHONY_SERVICE);
//            telephonyManager.listen(callStateListener,
//                    PhoneStateListener.LISTEN_CALL_STATE);
//        }

    }

    protected void getServerData(Bundle data) {

    }


    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getServerData(msg.getData());
            if (msg.what == 700) {
                Bundle data = msg.getData();
                String resultData = null;
                PDTestResult testResult = null;
                if (data != null) {
                    resultData = data.getString("result");
                }
                if (resultData != null) {
                    testResult = (PDTestResult) PervacioTest.getInstance().getObjectFromData(resultData, new TypeToken<PDTestResult>() {
                    }.getType());
                }
                if (testResult != null) {
                    updateTestResult(testResult.getName(), testResult.getStatus(), false);
                }
            }

        }
    };

    protected abstract String getToolBarName();

    protected abstract boolean setBackButton();

    protected boolean setHomeButton() {
        return false;
    }

    protected boolean showExitButton() {
        return false;
    }

    protected boolean isFullscreenActivity() {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent about = new Intent(BaseActivity.this, AboutActivity.class);
            startActivity(about);
        } else if (id == R.id.exit) {
            updateHistory();
            Intent endingSession = new Intent(BaseActivity.this, EndingSessionActivity.class);
            startActivity(endingSession);
        } else if (id == R.id.reportAProblem) {
            RAPActivity.openForResult(BaseActivity.this, false);
        } else if (id == R.id.copyLogs) {
            BaseUtils.createAndCopyFile("data/user/0/org.pervacio.mobilediagnostics/files/ssd_log.txt");
        }

//        switch (id) {
//            case R.id.about:
//                Intent about = new Intent(BaseActivity.this, AboutActivity.class);
//                startActivity(about);
//                return true;
//            case R.id.exit:
//                updateHistory();
//                Intent endingSession = new Intent(BaseActivity.this, EndingSessionActivity.class);
//                startActivity(endingSession);
//                break;
//            case R.id.reportAProblem:
//                RAPActivity.openForResult(BaseActivity.this, false);
//                break;
//            case R.id.copyLogs:
//                BaseUtils.createAndCopyFile("data/user/0/org.pervacio.mobilediagnostics/files/ssd_log.txt");
//                break;
//
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!Util.isAdvancedTestFlow()) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            if (showExitButton() && Util.showExitInMenu()) {
                MenuItem exit = menu.findItem(R.id.exit);
                exit.setVisible(true);
            }

            if (GlobalConfig.getInstance().isEnableRAPFeature()) {
                MenuItem exit = menu.findItem(R.id.reportAProblem);
                exit.setVisible(true);
            }
            if (BuildConfig.DEBUG) {
                MenuItem copyLogs = menu.findItem(R.id.copyLogs);
                copyLogs.setVisible(true);

            }

        }*/
        return true;//super.onCreateOptionsMenu(menu);
    }

    protected abstract int getLayoutResource();


    //[SSD-796](Earphone test instruction text should be changed as per current requirement)
    public int getResourceID(String testName, int drawableType, boolean isSkippedDisabled) {
        String name = testName.replaceAll(" ", "_");
        String testNameWithSkippedDisabled = isSkippedDisabled ? name + "SkipDisable" : name;
        return getResourceID(testNameWithSkippedDisabled, drawableType);
    }

    public int getResourceID(String testName, int drawableType) {
        String name = testName.replaceAll(" ", "_");
        int value = 0;
        TypedArray typedArray = null;
        try {
            int arrayL = getResources().getIdentifier(name, "array", getPackageName());
            typedArray = getResources().obtainTypedArray(arrayL);
            value = typedArray.getResourceId(drawableType, 0);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != typedArray)
                typedArray.recycle();
            if(value == 0) return R.string.default_mobile_data;
            return value;
        }
    }

    public Class<?> getTestActivity(String test_name) throws ClassNotFoundException {
        if (TestUtil.manualTestClassList != null && TestUtil.manualTestClassList.size() > 0) {
            String classname = TestUtil.manualTestClassList.get(test_name);
            return Class.forName(classname);
        }
        return null;
    }

    public void startManualTest(String testName) {
        Log.d("#00 :", "BaseActivity + SMT" + selectedManualTestsResult.size());
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TestInfo testInfo = new TestInfo(testName, globalConfig.getTestDisplayName(testName), TestResult.SKIPPED);
            testInfo.setTestStartTime(System.currentTimeMillis());
            pervacioTest.getManualTestResult().put(testName, testInfo);
            mCurrentManualTest = testName;
            Class<?> _class = getTestActivity(testName);
            Intent actIntent = new Intent(BaseActivity.this, _class);
            actIntent.putExtra(TEST_NAME, mCurrentManualTest);
            actIntent.putExtra("startTest", true);
            actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(actIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    public void startManualTestWithResult(String testName, Activity activity, int resultCode) {
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TestInfo testInfo = new TestInfo(testName, globalConfig.getTestDisplayName(testName), TestResult.SKIPPED);
            testInfo.setTestStartTime(System.currentTimeMillis());
            pervacioTest.getManualTestResult().put(testName, testInfo);
            mCurrentManualTest = testName;
            Class<?> _class = getTestActivity(testName);
            Intent actIntent = new Intent(BaseActivity.this, _class);
            actIntent.putExtra(TEST_NAME, mCurrentManualTest);
            actIntent.putExtra("startTest", true);
            actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivityForResult(actIntent, resultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeDuplicates(ArrayList<String> list) {
        HashSet<String> uniqueElements = new HashSet<String>(list);
        list.clear();
        list.addAll(uniqueElements);
        list = sortTests(list);
    }

    private static ArrayList<String> sortTests(ArrayList<String> originalList) {

        Comparator<String> customComparator = (test1, test2) -> {
            int categoryOrder1 = getCategoryOrder(test1);
            int categoryOrder2 = getCategoryOrder(test2);


            if (test1.equals("EarphoneTest") && test2.equals("EarphoneJackTest")) {
                return -1;
            } else if (test1.equals("EarphoneJackTest") && test2.equals("EarphoneTest")) {
                return 1;
            }


            int categoryOrder = categoryOrder1 - categoryOrder2;
            return (categoryOrder == 0) ? test1.compareTo(test2) : categoryOrder;
        };

        Collections.sort(originalList, customComparator);
        return originalList;
    }

    private static int getCategoryOrder(String testName) {
        String[] categories = {"Call", "Camera", "Microphone", "Speaker", "Screen", "Sensor", "Earphone", "USB"};

        for (int i = 0; i < categories.length; i++) {
            if (testName.contains(categories[i])) {
                return i;
            }
        }

        if (testName.contains("Discoloration") ||
                testName.contains("LCDTest") || testName.contains("DeadPixelTest") ||
                testName.contains("Dimming") || testName.contains("ScreenBurnTest") || testName.contains("TouchTest")) {
            return 4;
        }
        if (testName.contains("Proximity") || testName.contains("FingerPrintSensorTest") || testName.contains("Accelerometer") || testName.contains("LightSensorTest") ||
                testName.contains("Ambient") || testName.contains("TouchTest") || testName.contains("Bluetooth")) {
            return 5;
        }
        if (testName.equalsIgnoreCase("EarphoneTest")) {
            return 6;
        }
        if (testName.contains("EarphoneJack")) {
            return 7;
        }
        if (testName.equalsIgnoreCase("WallChargingTest")) {
            return 7;
        }
        return categories.length;
    }


    public void startNextManualTest() {
        Boolean isNFCfeature = NfcAdapter.getDefaultAdapter(getApplicationContext())!=null;
        Log.d("#00 :", "BaseActivity : SNM" + selectedManualTestsResult.size());
        DLog.d("NFC Feature",String.valueOf(isNFCfeature));
        selectedManualTests.add(TestName.SPEAKERTEST);
        selectedManualTests.add(TestName.MICROPHONE2TEST);
        selectedManualTests.add(TestName.MICROPHONETEST);
        selectedManualTests.add(TestName.EARPIECETEST);
        selectedManualTests.add(TestName.VIBRATIONTEST);
        selectedManualTests.add(TestName.WIFICONNECTIVITYTEST);
        selectedManualTests.add(TestName.BLUETOOTH_TOGGLE);
        if (!isNFCfeature && selectedManualTests.contains(TestName.NFCTEST)){
            selectedManualTests.remove(TestName.NFCTEST);
        }
        removeDuplicates(selectedManualTests);
        PervacioTest.getInstance().setSessionStatus("Manual Tests Running");
        DLog.d(TAG, "startNextManualTest...............");
        if (selectedManualTests.size() > 0) {
            DLog.d(TAG, "selectedManualTests size..............." + selectedManualTests.size());
//             DLog.d();(TAG, "mCurrentManualTest........." + mCurrentManualTest);
            int currentTestIndex = selectedManualTests.indexOf(mCurrentManualTest);
//             DLog.d();(TAG, "currentTestIndex........." + currentTestIndex);
            if (currentTestIndex == (selectedManualTests.size() - 1)) {
                PervacioTest.getInstance().setSessionStatus("Manual Tests Completed");
                if (Util.isAdvancedTestFlow()) {
                    Intent intent = new Intent(BaseActivity.this, ManualTestEndActivity.class);
                    startActivity(intent);
                } else {
                    if (GlobalConfig.getInstance().isBuyerVerification() || GlobalConfig.getInstance().isFinalVerify()) {
                        HashMap<String, Object> testResultMap = new HashMap<>();
                        testResultMap.put("DiagResults", getTestDetails());
                        testResultMap.put("DiagSessionId", GlobalConfig.getInstance().getSessionId());
                        testResultMap.put("status", "Completed");
                        pervacioTest.updateSession();
                        Log.d(TAG, "oru test result: " + testResultMap);
                        try {
                            if (!globalConfig.getIsResultSubmitted()) {
                                ///MobiruFlutterActivity.mResult.success(testResultMap);
                                globalConfig.setIsResultSubmitted(true);
                                //  MobiruFlutterActivity.isResultSubmitted = true;
                            }
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        DLog.d(TAG, "launching ResultsActivity...");
                        Intent actIntent = new Intent(BaseActivity.this, ManualTestsTryActivity.class);
                        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        actIntent.putExtra(TEST_NAME, "EndTest");   // send next test
                        actIntent.putExtra(TEST_RESULT, "0"); // send current test result
                        startActivity(actIntent);
//                        ResultsActivity.start(BaseActivity.this);
//                        finish();
                    }
                }
            } else {

//                if(GlobalConfig.getInstance().isVerification() && selectedManualTests.size() == 1) {
//                    lastSellerTest();
//                }
//                else {

                String nextManualTest = selectedManualTests.get(++currentTestIndex);  //next manual test
                String currManualTest = selectedManualTests.get(currentTestIndex);    //current manual test
                String currManualTestResult = selectedManualTestsResult.get(currManualTest);  // result of current manual test


                if (globalConfig.isIsRetry()) {
                    nextManualTest = globalConfig.getLastCurrentTest();

                    if (globalConfig.getLastCurrentTest().equalsIgnoreCase("EndTest")) {
                        currManualTestResult = "0";
                    } else {
                        currManualTest = selectedManualTests.get(selectedManualTests.indexOf(nextManualTest) - 1);
                        currManualTestResult = selectedManualTestsResult.get(currManualTest);  // result of current manual test
                    }
                    globalConfig.setIsRetry(false);
                }
                DLog.d(TAG, "nextManualTest........." + nextManualTest);
                DLog.d(TAG, "currManualTest........." + currManualTest);
                DLog.d(TAG, "currManualTest........." + currManualTestResult);
                Intent actIntent = new Intent(BaseActivity.this, ManualTestsTryActivity.class);
                actIntent.putStringArrayListExtra("PassTest", new ArrayList<>(TestList));
                actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                actIntent.putExtra(TEST_NAME, nextManualTest);   // send next test
                actIntent.putExtra(TEST_RESULT, currManualTestResult); // send current test result
                startActivity(actIntent);
//                }
            }
        } else {
            if (GlobalConfig.getInstance().isVerification() || GlobalConfig.getInstance().isBuyerVerification() || GlobalConfig.getInstance().isFinalVerify()) {
                HashMap<String, Object> testResultMap = new HashMap<>();
                testResultMap.put("DiagResults", getTestDetails());
                testResultMap.put("DiagSessionId", GlobalConfig.getInstance().getSessionId());
                testResultMap.put("status", "Completed");
                pervacioTest.updateSession();
                try {
                    if (!globalConfig.getIsResultSubmitted()) {
                     //   MobiruFlutterActivity.mResult.success(testResultMap);
                        globalConfig.setIsResultSubmitted(true);
                        //  MobiruFlutterActivity.isResultSubmitted = true;
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DLog.d(TAG, "launching ResultsActivity...");
                ResultsActivity.start(BaseActivity.this);
                finish();
            }
        }
    }
    public void lastSellerTest() {
        HashMap<String, Object> testResultMap = new HashMap<>();
        testResultMap.put("DiagResults", getTestDetails());
        testResultMap.put("DiagSessionId", GlobalConfig.getInstance().getSessionId());
        testResultMap.put("status", "Completed");
        pervacioTest.updateSession();
        Log.d(TAG, "oru test result: " + testResultMap);
        try {
            if (!globalConfig.getIsResultSubmitted()) {
                //TMobiruFlutterActivity.mResult.success(testResultMap);
                globalConfig.setIsResultSubmitted(true);
                //  MobiruFlutterActivity.isResultSubmitted = true;
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartCurrentTest() {
        //  DLog.d();(TAG, "startNextManualTest...............");
        Intent actIntent = new Intent(BaseActivity.this, ManualTestsTryActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        actIntent.putExtra(TEST_NAME, mCurrentManualTest);
        startActivity(actIntent);
    }

    public void launchResultActivity(String testName, String result, Context context) {
        Intent actIntent = new Intent(context, ManualTestsTryActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        actIntent.putExtra(TEST_NAME, testName);
        actIntent.putExtra(TEST_RESULT, result);
        startActivity(actIntent);
        finish();
    }

    public void launchResultActivity(String testName) {
        if (isAssistedApp) {
            sendManualTestResultToServer(testName, "COMPLETED");
        }
        Intent actIntent = new Intent(BaseActivity.this, ManualTestsTryActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        actIntent.putExtra(TEST_NAME, testName);
        startActivity(actIntent);
        finish();
    }

    public void sendManualTestResultToServer(String testName, String result) {
        PDTestResult pdTestResult = new PDTestResult();
        pdTestResult.setName(testName);
        pdTestResult.setStatus(result);
        CommandServer.getInstance(this).postEventData("TEST_RESULT", pdTestResult);
    }

    public void manualTestResultDialog(final String testName, final String result) {
        manualTestResultDialog(testName, result, context);
    }

    public void manualTestResultDialog(final String testName, final String result, final Context cntxt) {
        Log.d("#00 :", "BaseActivity : MTD  " + selectedManualTestsResult.size());
//        selectedManualTestsResult.put(testName, result);
        Log.d("Selected", String.valueOf(selectedManualTestsResult));
        if (Util.isAdvancedTestFlow()) {
            if (TestResult.TIMEOUT.equalsIgnoreCase(result) && isUserDesisionRequiredOnTimeOut()) {
                manualTestResultDialog(testName, result, true, cntxt);
            } else {
                int delay = 0;
                if (mCurrentManualTest.equalsIgnoreCase(TestName.ACCELEROMETERTEST)) {
                    delay = 1000;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("#00 :", "BaseActivity : MTD : run " + selectedManualTestsResult.size());
                        launchResultActivity(testName, result, cntxt);
                    }
                }, delay);

            }
        } else {
            manualTestResultDialog(testName, result, true, cntxt);
        }
    }

    public void manualTestResultDialog(final String testName, final String result, boolean sendResultToServer, Context ctx) {
        manualTestResultDialog(testName, result, sendResultToServer, true, ctx);
    }

    public void manualTestResultDialog(final String testName, final String result, boolean sendResultToServer, boolean showMessage, Context ctx) {
        // View coordinatorLayout = getWindow().findViewById(R.id.activity_manual_test);
        String message = null;
        final boolean[] testRetryed = {false};
        if (TestResult.PASS.equalsIgnoreCase(result)) {
            updateTestResult(testName, TestResult.PASS, sendResultToServer);
            message = getResources().getString(R.string.pass_status);
        } else if (TestResult.FAIL.equalsIgnoreCase(result)) {
            message = getResources().getString(R.string.fail_status);
            updateTestResult(testName, TestResult.FAIL, sendResultToServer);
        } else if (TestResult.TIMEOUT.equalsIgnoreCase(result) || TestResult.SHOW_SUGGESTION.equalsIgnoreCase(result)) {
            message = getResources().getString(R.string.timeout_status);
            updateTestResult(testName, TestResult.FAIL, sendResultToServer);
        } else if (TestResult.ACCESSDENIED.equalsIgnoreCase(result)) {
            message = getResources().getString(R.string.accessdenied_status);
            updateTestResult(testName, TestResult.ACCESSDENIED, sendResultToServer);
        } else if (TestResult.SKIPPED.equalsIgnoreCase(result)) {
            message = getResources().getString(R.string.skip_status);
            updateTestResult(testName, TestResult.SKIPPED, sendResultToServer);
        } else if (TestResult.NOTSUPPORTED.equalsIgnoreCase(result)) {
            message = getResources().getString(R.string.not_supported_status);
            updateTestResult(testName, TestResult.NOTSUPPORTED, sendResultToServer);
        }
/*        if (coordinatorLayout == null) {
            coordinatorLayout = getWindow().getDecorView().getRootView();
        }*/
        manual_Progressbar = (ProgressBar) findViewById(R.id.manual_Progressbar);
        if (!isAssistedApp) {
            if (showMessage) {
                showResultMessage(message, result, testName, ctx);
                //launchResultActivity(testName, result, ctx);
            } else {
                resultHandler.sendEmptyMessageDelayed(resultCode, 0);
            }
        }

        /*if(testName.equalsIgnoreCase(PDConstants.ACCELEROMETERTEST) || testName.equalsIgnoreCase(PDConstants.TOUCHTEST)){
            showNetworkMessage(message, result, testName, context);
        }else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
            if (result == TestResult.TIMEOUT) {
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testRetryed[0] = true;
                        startManualTest(testName);
                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.blue));
            }
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setDuration(3000);
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (!testRetryed[0])
                        startNextManualTest();
                }

                @Override
                public void onShown(Snackbar sb) {
                    super.onShown(sb);
                    if (manual_Progressbar != null) {
                        manual_Progressbar.setVisibility(View.GONE);
                    }
                }
            });
            snackbar.show();
        }*/
    }

    public void displaySnackBarMessage(String message) {
        View coordinatorLayout = getWindow().findViewById(R.id.activity_manual_test);
        if (coordinatorLayout == null) {
            coordinatorLayout = getWindow().getDecorView().getRootView();
        }
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.setDuration(1000);
        snackbar.show();
    }


    /*public void manualTestResultDialog(final String testName, final String result,Context ctx) {
        final Dialog myDialog = new Dialog(ctx);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.manual_test_result_dialog);
        myDialog.setCancelable(false);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView resultDescription = (TextView) myDialog.findViewById(R.id.result_Description);
        TextView resultView = (TextView) myDialog.findViewById(R.id.result);
        TextView nextTest = (TextView) myDialog.findViewById(R.id.next_test);
        TextView returnTest = (TextView) myDialog.findViewById(R.id.return_test);
        LinearLayout resultLayout = (LinearLayout) myDialog.findViewById(R.id.resultLayout);
        if (result.equalsIgnoreCase(TestResult.PASS)) {
            updateTestResult(testName, TestResult.PASS);
            resultView.setText(getString(R.string.result_pass));
            resultDescription.setText(String.format("%s %s", getDisplayName(testName), getString(R.string.str_is_passed)));
            resultLayout.setBackgroundColor(getResources().getColor(R.color.manual_test_pass));
        } else if (result.equalsIgnoreCase(TestResult.FAIL)) {
            updateTestResult(testName, TestResult.FAIL);
            resultView.setText(getString(R.string.result_fail));
            resultDescription.setText(String.format("%s %s", getDisplayName(testName), getString(R.string.str_is_failed)));
            resultLayout.setBackgroundColor(getResources().getColor(R.color.manual_test_fail));
        } else if (result.equalsIgnoreCase(TestResult.TIMEOUT)) {
            updateTestResult(testName, TestResult.FAIL);
            returnTest.setVisibility(View.VISIBLE);
            resultView.setText(getString(R.string.result_timed_out));
            resultDescription.setText(String.format("%s %s", getDisplayName(testName), getString(R.string.str_is_timeout)));
            resultLayout.setBackgroundColor(getResources().getColor(R.color.thick_grey));
        }else if(result.equalsIgnoreCase(TestResult.ACCESSDENIED)){
            updateTestResult(testName,TestResult.ACCESSDENIED);
            resultView.setText(getString(R.string.result_access_denied));
            resultDescription.setText(String.format("%s %s", getDisplayName(testName), getString(R.string.str_is_access_denied)));
            resultLayout.setBackgroundColor(getResources().getColor(R.color.manual_test_fail));
        }
        nextTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
                startNextManualTest();
            }
        });
        returnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                startManualTest(testName);
            }
        });
        myDialog.show();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
    }*/

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public String getDisplayName(String testName) {
        return globalConfig.getTestDisplayName(testName);
    }


    // check internet connection
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) BaseActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) BaseActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }


    static GIFMovieView gifMovieView = null;

    public GIFMovieView getGIFMovieView(Context context, String testName) {
        DLog.d("gif", "testName= " + testName);
        InputStream stream = null;
        try {
            DLog.d("gif", "Gif Image Name= " + TestUtil.manualtestGifMap.get(testName));
            stream = context.getAssets().open(TestUtil.manualtestGifMap.get(testName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BaseActivity.gifMovieView != null) {
            BaseActivity.gifMovieView.destroyDrawingCache();
            BaseActivity.gifMovieView = null;
        }
        gifMovieView = new GIFMovieView(context, stream);
        gifMovieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return gifMovieView;
    }

    public void networkMessageDialog(final String title, final String message, final Context context) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.cutom_alert_dialog);
        myDialog.setCancelable(false);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView BL_alert_head = (TextView) myDialog
                .findViewById(R.id.BL_alert_head);
        TextView BL_alert_text = (TextView) myDialog
                .findViewById(R.id.BL_alert_text);
        BL_alert_head.setText(title);
        BL_alert_text.setText(message);
        Button BL_alert_ok = (Button) myDialog.findViewById(R.id.BL_alert_yes);
        Button BL_alert_no = (Button) myDialog.findViewById(R.id.BL_alert_no);
        BL_alert_ok.setText(R.string.str_ok);
        BL_alert_no.setVisibility(View.GONE);

        BL_alert_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
    }


    protected void removeSettingDialog(boolean makeItNull) {
        if (settingsDialog != null && settingsDialog.isShowing()) {
            settingsDialog.dismiss();
        }
        if (makeItNull)
            settingsDialog = null;
    }

    public void showAcessibilityDialogue() {
        if (settingsDialog == null || !settingsDialog.isShowing())
            settingsDialog = CommonUtil.DialogUtil.showAlert(this, getString(R.string.all_sounds_off), getString(R.string.all_sounds_off_instruction), getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
                }
            });
    }

    String resultToServer;

    public void showSettingsScreen(boolean flag, final String resolutionName) {
        String title = "";
        String message = "";
        resultToServer = TestResult.CANBEIMPROVED;
        if (flag) {
            if (ResolutionName.GPS_OFF.equalsIgnoreCase(resolutionName)) {
                if (!(new AFGPS().getState())) resultToServer = TestResult.OPTIMIZED;
                title = getResources().getString(R.string.gps_service_active);
                message = getResources().getString(R.string.disable_gps_services);
            } else if (ResolutionName.NFC_OFF.equalsIgnoreCase(resolutionName)) {
                if (!(new AFNFC().getState())) resultToServer = TestResult.OPTIMIZED;
                title = getResources().getString(R.string.nfc_services_active);
                message = getResources().getString(R.string.disable_nfc);
            }
        } else {
            if (ResolutionName.GPS_ON.equalsIgnoreCase(resolutionName)) {
                if ((new AFGPS().getState())) resultToServer = TestResult.OPTIMIZED;
                title = getResources().getString(R.string.gps_service_not_active);
                message = getResources().getString(R.string.enable_gps_services);
            } else if (ResolutionName.NFC_ON.equalsIgnoreCase(resolutionName)) {
                if ((new AFNFC().getState())) resultToServer = TestResult.OPTIMIZED;
                title = getResources().getString(R.string.nfc_services_not_active);
                message = getString(R.string.enable_nfc);
            }
        }

        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.cutom_alert_dialog);
        myDialog.setCancelable(true);
        //  myDialog.getWindow().getAttributes().;
        TextView blAlertHead = (TextView) myDialog
                .findViewById(R.id.BL_alert_head);

        TextView blAlertText = (TextView) myDialog
                .findViewById(R.id.BL_alert_text);
        blAlertHead.setText(title);
        blAlertText.setText(message);
        TextView btn_settings = (TextView) myDialog.findViewById(R.id.BL_alert_no);
        btn_settings.setText(getString(R.string.settings));
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (ResolutionName.GPS_OFF.equalsIgnoreCase(resolutionName) || ResolutionName.GPS_ON.equalsIgnoreCase(resolutionName)) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                } else {
                    intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                }
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                myDialog.dismiss();
            }
        });
        TextView btn_cancel = (TextView) myDialog.findViewById(R.id.BL_alert_yes);
        btn_cancel.setText(getString(R.string.cancel_settings));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                // Sending Result as Skipped to Server on Cancel button
                if (isAssistedApp) {
                    sendResultToServer(resolutionName, resultToServer);
                }
            }
        });
        myDialog.show();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
    }


//    public static float getCenterYScreenPosition(Context context) {
//        Point p = getCenterScreenPosition(context);
//        return p.y;
//    }//  w w w  .j a v  a 2 s . c om
//
//    public static Point getCenterScreenPosition(Context context) {
//        Point p = getScreenSize(context);
//        p.x = p.x / 2;
//        p.y = p.y / 2;
//        return p;
//    }
//
//    public static Point getScreenSize(Context context) {
//        Display display = ((WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE))
//                .getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size;
//    }


    public static boolean grantUsageStates(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager
                        .getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context
                        .getSystemService(Context.APP_OPS_SERVICE);
                int mode = 0;
                mode = appOpsManager.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                        applicationInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    protected void checkUsageStats() {
        if (grantUsageStates(this)) {
            startApp();
        } else {
            showWriteSettingRequest(USAGE_STATS);
            startWatchingPermission(AppOpsManager.OPSTR_GET_USAGE_STATS);
        }
    }


    public void setFontToView(TextView tv, int type) {
        Typeface tf = null;
        if (type == ROBOTO_LIGHT) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        } else if (type == ROBOTO_MEDIUM) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
        } else if (type == ROBOTO_REGULAR) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        } else if (type == ROBOTO_THIN) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        } else if (type == AILERON_THIN) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_thin.ttf");
        } else if (type == AILERON_LIGHT) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_light.ttf");
        } else if (type == AILERON_REGULAR) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/aileron_regular.ttf");
        } else if (type == OPENSANS_REGULAR) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/opensans_regular.ttf");
        } else if (type == SSF_MEDIUM) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/ssf_pro_medium.otf");
        } else if (type == OPENSANS_MEDIUM) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/opensans_medium.ttf");
        } else if (type == OPENSANS_LIGHT) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/opensans_light.ttf");
        } else {
            tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_regular.ttf");
        }
        if (tv != null)
            tv.setTypeface(tf);
    }


    public static byte[] gzip(String s) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            OutputStreamWriter osw = new OutputStreamWriter(gzip, Charset.forName("UTF-8"));
            osw.write(s);
            osw.close();
            byte[] bytes = bos.toByteArray();
            // String hexcodes=Hex.encodeHexString( bytes ) ;
            return bytes;
        } catch (Exception e) {
            DLog.e("prem", "GZip Exception" + e.getMessage());
        }
        return null;
    }

    public static String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                // DLog.d();("prem", "Line: " + nextLine);
                sb.append(nextLine);
            }
        } catch (IOException e) {
            DLog.e(TAG, "readStream exception: " + e.getMessage()+ e);
        }
        return sb.toString();
    }

    public void updateHistory() {
        History.getInstance().updateHistory(pervacioTest);
    }


    public boolean permissionCheck(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getApplicationContext())) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void showWriteSettingRequest(final int requestCode) {
        String title = getString(R.string.permission);
        String message = "";
        if (requestCode == USAGE_STATS) {
            message = getString(R.string.enable_usage_stats_permission);
        } else {
            message = getString(R.string.enable_permissions);
        }

        CommonUtil.DialogUtil.showAlert(this, title, message, getResources().getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (requestCode == WRITE_SETTINGS) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivityForResult(intent, WRITE_SETTINGS);
                } else {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivityForResult(intent, USAGE_STATS);
                }
            }
        });
    }


    /***** Retry alert  */

    public void showResultMessage(final String message, final String result, final String testName, final Context context) {
        selectedManualTestsResult.put(testName, result);
//        Log.d("SelectedTest",selectedManualTestsResult.get(0));
        DLog.d("Result", "TestResult:" + result + "messgage:" + message);
        try {
            if (manual_Progressbar != null) {
                manual_Progressbar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Util.isAdvancedTestFlow() && !TestResult.TIMEOUT.equalsIgnoreCase(result)) {
            Toast toast = Toast.makeText(context, getText(R.string.completed), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else {
            AlertDialog.Builder alertDialogBuilder = CommonUtil.DialogUtil.getAlert(this);
            if (Util.isAdvancedTestFlow()) {
                int messageId = R.string.time_out_msg_advance_flow;
                int possitveBtnId = R.string.str_yes;
                int negativeBtnId = R.string.str_no;
                alertDialogBuilder.setMessage(messageId);
                alertDialogBuilder.setPositiveButton(possitveBtnId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resultHandler.removeMessages(resultCode);
                                alertDialog.dismiss();
                                //startManualTest(testName);
                                resumeTest(testName);
                            }
                        });
                alertDialogBuilder.setNegativeButton(negativeBtnId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resultHandler.removeMessages(resultCode);
                                alertDialog.dismiss();
                                if (TestName.DEADPIXELTEST.equalsIgnoreCase(testName) ||
                                        TestName.SCREENBURNTEST.equalsIgnoreCase(testName) || TestName.DISCOLORATIONTEST.equalsIgnoreCase(testName)) {
                                    ManualTest.getInstance(mCurrentTestActivity).changeDisplayTestColor();
                                } else {
                                    manualTestResultDialog(mCurrentManualTest, TestResult.FAIL);
                                }
                            }
                        });
            } else {
                alertDialogBuilder.setTitle(R.string.alert);
                alertDialogBuilder.setMessage(message);
                if (TestResult.SHOW_SUGGESTION.equalsIgnoreCase(result)) {
                    //    alertDialogBuilder = showTestSuggestions(testName);
                } else if (result == TestResult.TIMEOUT) {
                    if (ODDUtils.suggestionTestMap.containsKey(testName)) {
                        Log.e(TAG, "Show suggestion");
                        alertDialogBuilder = showTestSuggestions(testName);
                    } else {
                        int positiveButtonID = R.string.btn_retry;
                        alertDialogBuilder.setPositiveButton(positiveButtonID,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        resultHandler.removeMessages(resultCode);
                                        alertDialog.dismiss();
                                        /*testRetryed[0] = true;*/
                                        if (testName.equalsIgnoreCase(TestName.EARPHONEJACKTEST) || testName.equalsIgnoreCase(TestName.CHARGINGTEST)) {
                                        }
                                        else {
                                            if (manual_Progressbar != null) {
                                                manual_Progressbar.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        startManualTest(testName);

                                    }
                                });
                        int negativeButtonID = R.string.str_skip;
                        if (ProductFlowUtil.isTradein() || globalConfig.isVerification()) {
                            negativeButtonID = R.string.fail_btn;
                        } else {
                            negativeButtonID = R.string.str_skip;
                        }

                        alertDialogBuilder.setNegativeButton(negativeButtonID,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resultHandler.removeMessages(resultCode);
                                        alertDialog.dismiss();
                                        if (ProductFlowUtil.isTradein() || globalConfig.isVerification()) {
                                            updateTestResult(testName, TestResult.FAIL);
                                        } else {
                                            updateTestResult(testName, TestResult.SKIPPED);
                                        }
                                        resultHandler.sendEmptyMessageDelayed(resultCode, 1000);
                                    }
                                });
                    }
                } else {
                    /*LayoutInflater layoutInflater = LayoutInflater.from(context);
                    final View view = layoutInflater.inflate(R.layout.test_result_dialog, null);
                    if (TestResult.PASS.equalsIgnoreCase(result)) {
                        ImageView imageView = view.findViewById(R.id.test_result_img);
                        TextView textView = view.findViewById(R.id.test_result_msg);
                        imageView.setImageResource(R.drawable.pass);
                        textView.setText(R.string.pass_status);
                    } else if (TestResult.SKIPPED.equalsIgnoreCase(result)) {
                        ImageView imageView = view.findViewById(R.id.test_result_img);
                        TextView textView = view.findViewById(R.id.test_result_msg);
                        imageView.setImageResource(R.drawable.skip);
                        textView.setText(skip_msg_id);
                    } else if (TestResult.NOTSUPPORTED.equalsIgnoreCase(result)) {
                        ImageView imageView = view.findViewById(R.id.test_result_img);
                        TextView textView = view.findViewById(R.id.test_result_msg);
                        imageView.setImageResource(R.drawable.danger);
                        textView.setText(skip_msg_id);
                    }
                    dialog = new Dialog(context);
                    dialog.setContentView(view);
                    dialog.show();

                    if (!TestResult.TIMEOUT.equalsIgnoreCase(result)) {
                        resultHandler.sendEmptyMessageDelayed(resultCode, 3000);
                    }*/
                    launchResultActivity(testName, result, context);
                    return;
                }
            }
            try {
                alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TestResult.TIMEOUT.equalsIgnoreCase(result) && !TestResult.SHOW_SUGGESTION.equalsIgnoreCase(result)) {
            resultHandler.sendEmptyMessageDelayed(resultCode, 3000);
        }
    }

    protected AlertDialog.Builder showTestSuggestions(final String testName) {
        AlertDialog.Builder dialogBuilder = CommonUtil.DialogUtil.getAlert(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.suggestion_pop_up, null);
        TextView msgView = view.findViewById(R.id.time_out_alrt_msg);
        TextView titleView = view.findViewById(R.id.time_out_alrt_title);
        Button fialBtn = view.findViewById(R.id.suggestion_fail_btn);
        Button retryBtn = view.findViewById(R.id.suggestion_retry_btn);
        ImageView imgView = view.findViewById(R.id.time_out_alrt_img);
        if (ODDUtils.suggestionTestMap.containsKey(testName)) {
            titleView.setText(R.string.suggestions);
        }
        int messageID = getResourceID(testName, TEST_TIMEOUT_MESAGE);
        msgView.setText(messageID);
        int imageId = getResourceID(testName, TEST_TIMEOUT_IMAGE);
        if (imageId != 0) {
            imgView.setImageResource(imageId);
            imgView.setVisibility(View.VISIBLE);
        }

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultHandler.removeMessages(resultCode);
                alertDialog.dismiss();
                if (testName.equalsIgnoreCase(TestName.EARPHONETEST)) {
                    // ManualTest.getInstance(BaseActivity.this).performTest(testName,resultHandler);
                }else{
                    startManualTest(testName);
                }

            }
        });

        fialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultHandler.removeMessages(resultCode);
                alertDialog.dismiss();
                if (testName.equalsIgnoreCase(TestName.EARPHONETEST)) {
                    manualTestResultDialog(testName,TestResult.FAIL,false,getApplicationContext());
                }else{

                    updateTestResult(testName, TestResult.FAIL);
                }
                resultHandler.sendEmptyMessageDelayed(resultCode, 0);
            }
        });
        dialogBuilder.setView(view);
        return dialogBuilder;
    }

    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            if (dialog != null) {
                dialog.dismiss();
            }
            DLog.d("Result", "ResultCode:" + msg.what);
            if (msg.what == 1111) {
                startManualTest(mCurrentManualTest);
            } else if (msg.what == 2222) {
                removeMessages(msg.what);
                startNextManualTest();
                finish();
            }

        }
    };


    /* public void showNetworkMessagenew(final String message, final String result, final String testName,final Context context) {
         int resultCode=2222;
          DLog.d();("Result","TestResult:"+result+"messgage:"+message);
         try {
             if (dialog != null && dialog.isShowing()) {
                 dialog.dismiss();
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         final Timer timer = new Timer();

         Dialog dialog = new Dialog(this,R.style.PauseDialog);
         dialog.setTitle(R.string.alert);
         dialog.setT
         if (result == TestResult.TIMEOUT) {
             int positiveButtonID;
             resultCode=1111;
             positiveButtonID = R.string.btn_retry;
             alertDialogBuilder.setPositiveButton(positiveButtonID,
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface arg0, int arg1) {
                             alertDialog.dismiss();
                             *//*testRetryed[0] = true;*//*

                            startManualTest(testName);
                        }
                    });
        }
        try {
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultHandler.sendEmptyMessageDelayed(resultCode,3000);
    }*/
    public View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    public static boolean isAirplaneModeOn(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public void showNetworkDialogue(final boolean proceedOffline, String title, String message, final int reqCode, Context context) {
        networkDialgoue = new Dialog(this);
        networkDialgoue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        networkDialgoue.setContentView(R.layout.cutom_alert_dialog);
        networkDialgoue.setCancelable(true);
        networkDialgoue.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView blAlertHead = (TextView) networkDialgoue
                .findViewById(R.id.BL_alert_head);
        TextView blAlertText = (TextView) networkDialgoue
                .findViewById(R.id.BL_alert_text);
        blAlertHead.setText(title);
        blAlertText.setText(message);
        final Button BL_alert_yes = (Button) networkDialgoue.findViewById(R.id.BL_alert_yes);

        Button BL_alert_no = (Button) networkDialgoue.findViewById(R.id.BL_alert_no);
        if (reqCode == 0) {

            if (proceedOffline && Util.isOfflineDiagSupported()) {
                BL_alert_no.setText(R.string.btn_go_offline);
                PervacioTest.getInstance().setOfflineDiagnostics(true);
            } else
                BL_alert_no.setText(R.string.btn_retry);
            BL_alert_yes.setText(R.string.btn_exit);
        } else if (reqCode == 1 || reqCode == 3) {
            BL_alert_yes.setText("OK");
            BL_alert_no.setVisibility(View.GONE);
        } else {
            BL_alert_yes.setText(R.string.action_cancel);
            BL_alert_no.setText(R.string.str_ok);
            if (!showPermisionUi) {
                BL_alert_no.setText(R.string.settings);
            }
        }

        BL_alert_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reqCode == 1) {
                    dismissDialogue();
                } else if (reqCode == 2) {
                    /*if (!permissionsStarted  ){
                        checkPermissionsAndProceed();
                        dismissDialogue();
                    }*/
                    if (!showPermisionUi) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PERMISSIONS_REQUEST);
                        dismissDialogue();
                    } else {
                        checkPermissionsAndProceed();
                        dismissDialogue();
                    }

                } else {
                    DLog.d("ani", " base . " + isOnline() + " proceedOffline " + proceedOffline + " che " + !permissionsStarted);
                    if (isOnline() || (proceedOffline && Util.isOfflineDiagSupported())) {
                        dismissDialogue();
                        if (!permissionsStarted && !isOnline()) {
                            checkPermissionsAndProceed();
                        }
                        if (proceedOffline && Util.isOfflineDiagSupported())
                            PervacioTest.getInstance().setOfflineDiagnostics(true);
                    } else {
                        dismissDialogue();
                        showNetworkDialogue(hasOfflineData(), getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), 0, BaseActivity.this);
                    }
                }

            }
        });
        BL_alert_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialogue();
                finish();
//                if (reqCode == 0) {
//                    //if( BL_alert_yes.getText().equals("EXIT")){
//                    //finish();
//                    //}
//                    if (Util.isAdvancedTestFlow()) {
//                        Intent intent = new Intent(BaseActivity.this, EndingSessionActivity.class);
//                        startActivity(intent);
//                    } else {
//                        finish();
//                    }
//
//                }
//                if (reqCode == 1) {
//                    Intent intent1 = null;
//                    try {
//                        try {
//                            dismissDialogue();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if ("SGH-M919".equalsIgnoreCase(Build.MODEL)) {
//                            intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//                            startActivity(intent1);
//                        } else {
//                            intent1 = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
//                            startActivity(intent1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (reqCode == 2) {
//                    Intent intent = new Intent(BaseActivity.this, EndingSessionActivity.class);
//                    intent.putExtra("Exit", true);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    dismissDialogue();
//                    finish();
//                } else {
//                    dismissDialogue();
//                }
            }
        });

        networkDialgoue.setCanceledOnTouchOutside(false);
        networkDialgoue.setCancelable(false);
        networkDialgoue.show();
    }

    public void promptPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!TextUtils.isEmpty(CommonUtil.PermissionUtil.isPermissionGrantedForMarshmallow(this, Util.isGetAccountsPermissionRequired()))) {
                if (!showPermisionUi) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, PERMISSIONS_REQUEST);
                    dismissDialogue();
                } else {
                    checkPermissionsAndProceed();
                }
            } else {
                startApp();
            }
        } else {
            startApp();
        }
    }

    public void checkPermissionsAndProceed() {
        permissionsStarted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, CommonUtil.PermissionUtil.permissionListForSSD(Util.isGetAccountsPermissionRequired()), 0);
        } else {
            //checkUsageStats();
            startApp();
        }
    }

    public void startApp() {
        DLog.d(TAG, "start app " + hasOfflineData() + !initServiceStarted + " " + globalConfig.isInitCompleted());
        /*if (!initServiceStarted) {
            PervacioTest.getInstance().initialize();
            initServiceStarted = true;
        }*/
        if (BaseActivity.needDebugSupport) {
            Intent intent = new Intent(BaseActivity.this, BaseUrlUpdateActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent;
/*                if(Build.VERSION.SDK_INT >= 29 && !Util.retrivedIMEIForAndroid10() && Util.retriveImeiManuallyInAndroid10()) {
                    intent = new Intent(BaseActivity.this, IMEIReadActivity.class);
                } else {*/
            if (isAssistedApp) {
                intent = new Intent(BaseActivity.this, PinGenerationActivity.class);
                startActivity(intent);
            } else {
                PinValidationActivity.startActivity(BaseActivity.this, false, false);
                finish();
            }
            //}

        }
        finish();
/*            if (!isAssistedApp && (isOnline()|| hasOfflineData()) && !initServiceStarted) {
            if (!globalConfig.isInitCompleted())
                PervacioTest.getInstance().initialize();
            initServiceStarted = true;
        }
        if (!globalConfig.isInitCompleted()) {
            progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!globalConfig.isInitCompleted() && !serviceInterrupted) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    if (!serviceInterrupted) {
                        if(BaseActivity.needDebugSupport) {
                            Intent intent = new Intent(BaseActivity.this, BaseUrlUpdateActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(BaseActivity.this, PinGenerationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }).start();
        } else {
            Intent intent = new Intent(BaseActivity.this, PinGenerationActivity.class);
            startActivity(intent);
            finish();
        }
        serviceInterrupted = false;*/
    }

    public boolean isTermsAccepted() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int status = sharedPreferences.getInt("TermsAndConditions", 0);
        return (status == 1);
    }

    public boolean hasOfflineData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String data = sharedPreferences.getString("OfflineData", null);
        return (data != null);
    }

    class ServiceInterrupt extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.sprint.network.interrupted") && !hasOfflineData()) {
                serviceInterrupted = true;
                initServiceStarted = false;
                permissionsStarted = false;
                showNetworkDialogue(hasOfflineData(), getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), 0, BaseActivity.this); // show pop-up
            }
        }
    }

    class AirplaneOn extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                Intent i = getIntent();// new Intent(context, TermsAndConditionsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                DLog.d("onReceive", "Status Changed");
                startActivity(i);
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
//    public static boolean isOnDeviceApp() {
//        return IsOnDeviceApp;
//    }

    protected void registerAppReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        AppsUninstallReceiver br = new AppsUninstallReceiver();
        registerReceiver(br, intentFilter);
    }

    public class AppsUninstallReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String selPackage = intent.getData().getSchemeSpecificPart();
//            ArrayList<String> uninstalledPackagesList = new ArrayList<String>(Arrays.asList(getStringFromPreference("uninstalledPackage").split(",")));
//            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REMOVED)) {
//                if (!selectedPackages.contains(selPackage) && !uninstalledPackagesList.contains(selPackage)) {
//                     DLog.d();("Apps", " in receiver - Uninstalled " + selPackage);
//                    getUpdatedAppResolutionPojoMap("RISKY", selPackage);
//                    refreshAdapter = true;
//                }
//            }
        }
    }

    protected void registerSessionReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSessionExpired);
        registerReceiver(sessionReceiver, mIntentFilter);
        IsSessionReceiverRegistered = true;
    }

    private boolean IsSessionReceiverRegistered = false;

    private BroadcastReceiver sessionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            DLog.d(TAG, "intent received intent Action " + intent.getAction());
            removeStickyBroadcast(intent);
            try {
//                bringActivityToTop();
            } catch (Exception e) {
                e.getMessage();
            }
            sessionExpiredDilogue("Alert", "Session Expired......!!!");
//            deleteCameraFolder();

        }
    };

//    private void deleteCameraFolder() {
//        try {
//            if (RearCameraPreviewActivity.filePath != null) {
//                rearCameraFolderDelete(RearCameraPreviewActivity.filePath);
//            } else {
//                rearCameraFolderDelete(Preview_Activity.videoPath);
//            }
//
//            if (FrontCameraPreviewActivity.filePath != null) {
//                frontCameraFolderDelete(FrontCameraPreviewActivity.filePath);
//            } else {
//                frontCameraFolderDelete(Preview_Activity.videoPath);
//            }
//
//
//        } catch (Exception e) {
//             DLog.d();(TAG, "Exception:" + e.getMessage());
//        }
//    }

    public void sessionExpiredDilogue(String title, String message) {
        final Dialog sessionDialog = new Dialog(BaseActivity.this);
        sessionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sessionDialog.setContentView(R.layout.cutom_alert_dialog);
        sessionDialog.setCancelable(false);
        sessionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView BL_alert_head = (TextView) sessionDialog
                .findViewById(R.id.BL_alert_head);
        TextView BL_alert_text = (TextView) sessionDialog
                .findViewById(R.id.BL_alert_text);
        Button BL_endsession = (Button) sessionDialog.findViewById(R.id.BL_alert_yes);
        Button BL_alert_no = (Button) sessionDialog.findViewById(R.id.BL_alert_no);

        BL_alert_head.setText(title);
        BL_alert_text.setText(message);

        BL_endsession.setText("End Session");
        BL_alert_no.setText("Start Over");

        BL_alert_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionDialog.dismiss();
                PinValidationActivity.startActivity(BaseActivity.this, false, true);

            }
        });
        BL_endsession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sessionDialog.dismiss();
                Intent in1 = new Intent(BaseActivity.this,
                        TermsAndConditionsActivity.class);
                in1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in1.putExtra("exit", true);
                startActivity(in1);
            }
        });
        sessionDialog.setCanceledOnTouchOutside(false);
        sessionDialog.show();
    }

//    public void bringActivityToTop() {
//        try {
//            task_id = getTask_id();
//            if ("LG-D855".equals(Build.MODEL)) {
//                activityManager.moveTaskToFront(task_id,
//                        ActivityManager.MOVE_TASK_WITH_HOME);
//                recentTasks = activityManager
//                        .getRunningTasks(Integer.MAX_VALUE);
//
//            } else if (recentTasks.get(0).id == task_id) {
//                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//                sendBroadcast(closeDialog);
//                activityManager.moveTaskToFront(task_id,
//                        ActivityManager.MOVE_TASK_WITH_HOME);
//                recentTasks = activityManager
//                        .getRunningTasks(Integer.MAX_VALUE);
//
//            } else {
//                if (recentTasks.get(0).id != task_id) {
//                    activityManager.moveTaskToFront(task_id,
//                            ActivityManager.MOVE_TASK_WITH_HOME);
//                    recentTasks = activityManager
//                            .getRunningTasks(Integer.MAX_VALUE);
//                }
//            }
//        } catch (Exception e) {
//        }
//    }

    public void checkSimAndGPS() {
//        ArrayList<String> selectedAutoTestList = BaseActivity.autoTestList.get(BaseActivity.selectedCatagory);
//        if (selectedAutoTestList.contains(PDConstants.SIMCARD) && isOnDeviceApp() && !isTradInMode() && !mDeviceInfo.getSIMState()) {
//            simcardavailable = false;
//        }
//        if (selectedAutoTestList.contains(PDConstants.GPSCOMPREHENSIVETEST) && isOnDeviceApp() && !new AFGPS().getState()) {
        checkForGPS = true;
//        }
    }

    private void dismissDialogue() {
        if (networkDialgoue != null) {
            networkDialgoue.dismiss();
        }
    }

    public boolean isPermissionsDenied() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean status = sharedPreferences.getBoolean("permissionsdenied", true);
        return status;
    }

    public void showDialogue(String title, String message, final int dialogueType) {
        dialog = new Dialog(BaseActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cutom_alert_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView BL_alert_head = (TextView) dialog
                .findViewById(R.id.BL_alert_head);
        TextView BL_alert_text = (TextView) dialog
                .findViewById(R.id.BL_alert_text);
        BL_alert_head.setText(title);
        BL_alert_text.setText(message);
        Button BL_alert_Yes = (Button) dialog.findViewById(R.id.BL_alert_yes);
        Button BL_alert_No = (Button) dialog.findViewById(R.id.BL_alert_no);
        BL_alert_Yes.setVisibility(View.VISIBLE);
        BL_alert_Yes.setText(R.string.str_yes);
        BL_alert_No.setText(R.string.str_no);
        if (dialogueType == 2) {
            BL_alert_Yes.setText(getResources().getString(R.string.settings));
            BL_alert_No.setVisibility(View.GONE);
        }
        if (dialogueType == 0) {
            BL_alert_No.setVisibility(View.GONE);
            BL_alert_Yes.setText(getResources().getString(R.string.str_results_skip));
            if (isAssistedApp) {
                BL_alert_Yes.setEnabled(false);
                BL_alert_Yes.setClickable(false);
//                BL_alert_Yes.setBackgroundResource(R.drawable.round_corners_disabled);
            }
        }
        BL_alert_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();
                if (dialogueType == 1) {
//                    putStringinPreference(Constants.SIM_SLOT_FAULT, Constants.Simcard_Yes);
                    if (checkForGPS) {
                        showDialogue(getResources().getString(R.string.gps_service_not_active), getResources().getString(R.string.enable_gps_services), 2);
                    } else {
                        Intent intent = new Intent(BaseActivity.this, AutoTest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else if (dialogueType == 0) {
//                    startActivity(new Intent(BaseActivity.this, EndingSessionActivity.class));
                } else if (dialogueType == 2) {
                    settings = true;
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }

        });

        BL_alert_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null)
                    dialog.dismiss();
                if (dialogueType == 1) {
//                    putStringinPreference(Constants.SIM_SLOT_FAULT, Constants.Simcard_No);
                    if (checkForGPS) {
                        showDialogue(getResources().getString(R.string.gps_service_not_active), getResources().getString(R.string.enable_gps_services), 2);
                    } else {
                        Intent intent = new Intent(BaseActivity.this, AutoTest.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissions(String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionDenied = true;
            if (!shouldShowRequestPermissionRationale(permission)) {
                DLog.d(TAG, " Asked shouldShowRequestPermissionRationale permission: " + permission);
                showPermisionUi = false;
            }
        } else {
            if (!permissionDenied) {
                permissionDenied = false;
            }
        }
    }

    public boolean checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!TextUtils.isEmpty(CommonUtil.PermissionUtil.isPermissionGrantedForMarshmallow(this, Util.isGetAccountsPermissionRequired()))) {
                return false;
            } /*else {
                return grantUsageStates(this);
            }*/
        }
        return true;
    }


    public void acceptPermissionDilaogueCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!TextUtils.isEmpty(CommonUtil.PermissionUtil.isPermissionGrantedForMarshmallow(this, Util.isGetAccountsPermissionRequired()))) {
                showNetworkDialogue(false, getResources().getString(R.string.accept_permissions), getResources().getString(R.string.accept_permissions_msg), 2, this);
            } else {
                startApp();
            }
        } else {
            startApp();
        }
    }

    public void sendResultToServer(String testName, String result) {
        PDTestResult pdTestResult = new PDTestResult();
        pdTestResult.setStatus(result);
        pdTestResult.setName(testName);
        CommandServer.getInstance(this).postEventData("RESOLUTION_STATUS", pdTestResult);
    }

    @Override
    public void onBackPressed() {
        if (!isAssistedApp && !Util.needToRemoveBackButton()) {
            if (exitOnBack()) {
                CommonUtil.DialogUtil.getAlert(BaseActivity.this, getString(R.string.alert), getString(R.string.are_you_sure_back),
                        getString(R.string.str_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (globalConfig.isIsRetry()) globalConfig.setIsRetry(false);

                                HashMap<String, Object> testResultMap = new HashMap<>();
                                if (!globalConfig.getIsResultSubmitted()) {
                                    //MobiruFlutterActivity.mResult.success(testResultMap);
                                    globalConfig.setIsResultSubmitted(true);
                                    //  MobiruFlutterActivity.isResultSubmitted = true;
                                }
                                globalConfig.setScrollPosition(0);
                                globalConfig.getTestStates().clear();
                                finish();
                                dialog.dismiss();
                            }
                        },
                        getString(R.string.str_no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                super.onBackPressed();
            }

        }
    }

    protected void BackAlert() {

    }

    public boolean permissionStatusCheck(String currenttestName) {
        switch (currenttestName) {
            case TestName.WIFICONNECTIVITYTEST:
                if (currenttestName.equalsIgnoreCase(TestName.WIFICONNECTIVITYTEST)) {
                    if (!(permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},
                                    0);
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case TestName.BLUETOOTHCONNECTIVITYTEST:
                if (currenttestName.equalsIgnoreCase(TestName.BLUETOOTHCONNECTIVITYTEST)) {
                    if (!(permissionCheck(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                                    0);
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case TestName.CALLTEST:
                if (currenttestName.equalsIgnoreCase(TestName.CALLTEST)) {
                    if (!(permissionCheck(Manifest.permission.READ_PHONE_STATE)) || !(permissionCheck(Manifest.permission.CALL_PHONE)) || !(permissionCheck(Manifest.permission.READ_CALL_LOG))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    0);
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case TestName.MICROPHONETEST:
            case TestName.MICROPHONE2TEST:
                if (currenttestName.equalsIgnoreCase(TestName.MICROPHONETEST) || currenttestName.equalsIgnoreCase(TestName.MICROPHONE2TEST)) {
                    if (!(permissionCheck(Manifest.permission.RECORD_AUDIO))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    0);
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case TestName.FRONTFLASHTEST:
            case TestName.CAMERAFLASHTEST:
            case TestName.FRONTCAMERAPICTURETEST:
            case TestName.REARCAMERAPICTURETEST:
                if ((!(permissionCheck(Manifest.permission.CAMERA)) || !(permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE)) && Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);
                    }
                } else {
                    return true;
                }
                break;
            case TestName.FRONTCAMERAVIDEOTEST:
            case TestName.REARCAMERAVIDEOTEST:
                if ((!(permissionCheck(Manifest.permission.CAMERA)) || !(permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE)) || !(permissionCheck(Manifest.permission.RECORD_AUDIO))) && Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                0);
                    }
                } else {
                    return true;
                }
                break;
            default:
                return true;
        }
        return false;
    }


    public boolean dndModePermissionCheck() {
        if (!CommonUtil.isNotificationPolicyAccessPermissionGranted(this)) {
            CommonUtil.DialogUtil.showAlert(this, getString(R.string.permission), getString(R.string.enable_dnd_permission), getResources().getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AudioUtils.launchNotificationRequestPermissionScreen(BaseActivity.this);
                }
            });
            return false;
        }
        return true;
    }

    private void wakeDeviceScreen() {
        PowerManager mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPM.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "org.pervacio.wirelessapp:");
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private boolean isUserDesisionRequiredOnTimeOut() {
        String testLsit[] = new String[]{TestName.DEADPIXELTEST, TestName.SCREENBURNTEST, TestName.DISCOLORATIONTEST, TestName.TOUCHTEST,
                TestName.REARCAMERAPICTURETEST, TestName.FRONTCAMERAPICTURETEST, TestName.REARCAMERAVIDEOTEST, TestName.FRONTCAMERAVIDEOTEST};
        return Arrays.asList(testLsit).contains(mCurrentManualTest);
    }

    public int getManuatestsCount() {
        return selectedManualTests.size();
    }

    public int getManualTestIndex(String testName) {
        return selectedManualTests.indexOf(testName);
    }

    public void resumeTest(String testName) {
        if (TestName.TOUCHTEST.equalsIgnoreCase(testName) || TestName.DEADPIXELTEST.equalsIgnoreCase(testName) ||
                TestName.SCREENBURNTEST.equalsIgnoreCase(testName) || TestName.DISCOLORATIONTEST.equalsIgnoreCase(testName)) {
            ManualTest.getInstance(mCurrentTestActivity).resumeTest();
        } else {
            startManualTest(testName);
        }
    }

    ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USAGE_STATS) {
            checkUsageStats();
        } else if (requestCode == WRITE_SETTINGS) {
            checkUsageStats();
        } else if (requestCode == PERMISSIONS_REQUEST) {
            arePermissionsGrantedInSettings();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            checkForPermission(permissions);
        }
    }

    public void arePermissionsGrantedInSettings() {
        String[] permissionLIst = CommonUtil.PermissionUtil.permissionListForSSD(Util.isGetAccountsPermissionRequired());
        checkForPermission(permissionLIst);
    }

    private void checkForPermission(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            checkPermissions(permissions[i]);
        }

        if (permissionDenied) {
            permissionDenied = false;
            if (!showPermisionUi) {
                showNetworkDialogue(false, getResources().getString(R.string.accept_permissions), getResources().getString(R.string.accept_permissions_msg3), 2, this);
            } else {
                showNetworkDialogue(false, getResources().getString(R.string.accept_permissions), getResources().getString(R.string.accept_permissions_msg2), 2, this);
            }
        } else {
            startApp();
        }
    }


    /* */
    protected String getText(@NonNull TextView tv) {
        return tv.getText().toString();
    }


    @Override
    protected void onResume() {
        mInteractionMonitor.onResume(disconnectCallback);
        super.onResume();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        mInteractionMonitor.resetUserInteractionTimer();
    }

    protected static Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            mInteractionMonitor.setIsEligibleForTimeOut(false, 0);
            // Show POP-up , Then close the app on button click , notify the sever about abort reason.
            Log.d("TimeOUT: ", "Time Out ");
            if (context != null)
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        if (!((Activity) context).isFinishing()) {
                            CommonUtil.DialogUtil.showAlert(context, context.getString(R.string.app_name), context.getString(R.string.result_timed_out), context.getString(R.string.btn_exit), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EndingSessionActivity.exit(context, AbortReasons.TIMED_OUT);
                                }
                            });
                        }
                    }
                });
        }
    };

    public void launchAppFromSettings() {
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    private void startWatchingPermission(final String permission) {
        final AppOpsManager manager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        manager.startWatchingMode(permission, getPackageName(), new AppOpsManager.OnOpChangedListener() {
            @Override
            public void onOpChanged(String op, String packageName) {

                PackageManager packageManager = getPackageManager();
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    DLog.e(TAG, "onOpChanged exception : "+ e);
                }
                int mode = -1;
                if (applicationInfo != null) {
                    mode = manager.checkOpNoThrow(permission, applicationInfo.uid, applicationInfo.packageName);
                }

                if (mode == AppOpsManager.MODE_ALLOWED) {
                    launchAppFromSettings();
                    manager.stopWatchingMode(this);
                }
            }
        });
    }

    private static ArrayList<Map<String, String>> getTestDetails() {
        ArrayList<Map<String, String>> pdCommandDetailsList = new ArrayList<>();
        HashMap testResult = PervacioTest.getInstance().getTestResult();
        testResult.putAll(PervacioTest.getInstance().getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());
        final long sessionID = GlobalConfig.getInstance().getSessionId();
        Iterator it = testResult.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            TestInfo testInfo = (TestInfo) pair.getValue();
            /*PDCommandDetails pdCommandDetails = new PDCommandDetails();
            pdCommandDetails.setCommandName(testInfo.getName());
            pdCommandDetails.setTestStatus(testInfo.getTestResult());
            pdCommandDetails.setMessage("");
            pdCommandDetails.setStartDateTime(testInfo.getTestStartTime());
            pdCommandDetails.setEndDateTime(testInfo.getTestEndTime());
            pdCommandDetails.setSessionId(sessionID);
            pdCommandDetailsList.add(pdCommandDetails);*/
            Map<String, String> testResultObj = new HashMap<>();
            testResultObj.put("commandName", testInfo.getName());
            testResultObj.put("testStatus", testInfo.getTestResult());
            testResultObj.put("displayName", testInfo.getDisplayName());
            testResultObj.put("startDateTime", String.valueOf(testInfo.getTestStartTime()));
            testResultObj.put("endDateTime", String.valueOf(testInfo.getTestEndTime()));

            pdCommandDetailsList.add(testResultObj);
        }

        return pdCommandDetailsList;
    }

    protected boolean exitOnBack() {
        return true;
    }
}

