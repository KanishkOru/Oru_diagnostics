package com.oruphones.nativediagnostic.manualtests;


import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTH_TOGGLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.AboutActivity;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.KeysTest;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;


import org.pervacio.onediaglib.diagtests.TestKeys;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Activity to test Device Keys
 * <p/>
 * Created by Surya Polasanapalli on 04-10-2017.
 */

public class KeysTestActivity extends KeysTest {

    private static String TAG = KeysTestActivity.class.getSimpleName();
    private TestKeys mTestKeys;
    private TextView testName;
    private TextView endTest;
    private ImageView mImageView;
    private ArrayList<String> mDevicekeysList;
    private GridView keysLayout;
    private ArrayList<keyPOJO> keysPOJOArray = new ArrayList<keyPOJO>();
    private KeyTestAdapter keyTestAdapter;
    private String mTestName = "";
    //    private GlobalConfig globalConfig;
    private String mDevicekeys;
    private BaseActivity baseAcvity;
    //LinearLayout keysGifViewLayout;
    private LinearLayout testDescLayout;
    static TextView txtTimer;
    TextView prevTest;
    TextView nextTest;
    TextView testNumView, test_desc, test_tile_description, retry_Button, result_text_view;
    AlertDialog alertDialog = null;
    private boolean isTimerExpired = false;
    int resultCode = 2222;
    private int currentTestIndex = -1;
    PowerManager.WakeLock mWakeLock;


    private Handler timerHandler;
    private long timerDuration = 30000; // 30 seconds in milliseconds
    private long timerInterval = 1000; // 1 second interval
    private CountDownTimer countDownTimer;


    Intent getIntent;
    String mCurrentTest;
    LinearLayout prev_tests_list, next_tests_list;
    LinearLayout curr_test_card;
    LayoutInflater layoutInflater;
    public TextView result_Display_Name, result_Observation;
    public ImageView result_image_view, test_image;
    View prev_test_view[] = new View[selectedManualTests.size()];
    View next_test_view[] = new View[selectedManualTests.size()];

  //  ScrollView scrollView_key;

    @SuppressLint("HandlerLeak")
    private Handler keysHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                    if (baseAcvity == null) {
                        try {
                            baseAcvity = (BaseActivity) OruApplication.getAppContext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    baseAcvity.updateTestResult(testResult.getName(), testResult.getStatus(), false);
                }
                stopTest();
                return;
            }
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            if (result != null && !result.isEmpty()) {
                if (result.equalsIgnoreCase("KeyEvent")) {
                    String key = bundle.getString("message");
                    changeBG(key);
                } else {
                    if (baseAcvity != null) {
                        if (result.equalsIgnoreCase(TestResult.PASS)) {
                            baseAcvity.manualTestResultDialog(mTestName, TestResult.PASS, true, KeysTestActivity.this);
                            finish();
                        } else if (result.equalsIgnoreCase(TestResult.TIMEOUT)) {
                            baseAcvity.manualTestResultDialog(mTestName, TestResult.TIMEOUT,false, KeysTestActivity.this);
                        } else if (result.equalsIgnoreCase(TestResult.FAIL)) {
                            baseAcvity.manualTestResultDialog(mTestName, TestResult.FAIL, true, KeysTestActivity.this);
                            finish();
                        }

                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        CommandServer.getInstance(getApplicationContext()).setUIHandler(keysHandler);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DLog.d(TAG, "isRestry [KeyTest] : " + globalConfig.isIsRetry());

        DLog.d(TAG, "KeyTestActivity" + selectedManualTestsResult.size());


        setContentView(R.layout.keys_test_activity);
        getIntent = getIntent();
        mCurrentTest = getIntent.getStringExtra(TEST_NAME);


        endTest = (TextView) findViewById(R.id.btn_mCancel);
       TextView testName =  (TextView) findViewById(R.id.txtTestName);
       testName.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(mCurrentTest));
       test_desc = (TextView) findViewById(R.id.txtTestDec);
       test_desc.setText(getResources().getString(R.string.hardKey_Desc));
       LinearLayout animatedGif = findViewById(R.id.animatedGIFll);

       AnimatedGifUtils.addToView(animatedGif,getApplicationContext(),mCurrentTest);

//        test_tile_description = (TextView) findViewById(R.id.testOneLineDecription);
        mTestKeys = TestKeys.getInstance();
      //  scrollView_key = (ScrollView) findViewById(R.id.scrollView_key);
        txtTimer = findViewById(R.id.txtTimer);

        //mToolbar.setBackgroundResource(R.drawable.toolbar_bg);
        try {
            baseAcvity = (BaseActivity) BaseActivity.context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        if (null == mTestName) {
            mTestName = TestName.SOFTKEYTEST;
        }
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
        mToolbar.setTitleTextAppearance(this, R.style.textStyle_title);
        mToolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        if (Util.isAdvancedTestFlow()) {
            endTest.setVisibility(View.INVISIBLE);
        }
        if (!Util.needToRemoveBackButton()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable icon = getResources().getDrawable(R.drawable.ic_back);
            actionBar.setHomeAsUpIndicator(icon);
        } else {
            mToolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        }
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(mTestName));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAssistedApp && !Util.needToRemoveBackButton()) {
                    ManualTest.getInstance(KeysTestActivity.this).stopTest();
                 //   finish();

                    mTestKeys.testFinished(false);
                    manualTestResultDialog(mTestName, TestResult.FAIL, KeysTestActivity.this);
                    finish();
//                    CommonUtil.DialogUtil.getAlert(KeysTestActivity.this, getString(R.string.alert), getString(R.string.are_you_sure_back),
//                            getString(R.string.str_yes),
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    if(globalConfig.isIsRetry())globalConfig.setIsRetry(false);
//
//                                    HashMap<String, Object> testResultMap = new HashMap<>();
//                                    if (MobiruFlutterActivity.isResultSubmitted == false) {
//
//                                        MobiruFlutterActivity.mResult.success(testResultMap);
//                                        MobiruFlutterActivity.isResultSubmitted = true;
//                                    }
//                                    finish();
//                                    dialog.dismiss();
//                                }
//                            },
//                            getString(R.string.str_no),
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).show();



                }
            }
        });


        // Initialize Handler for the timer
//        timerHandler = new Handler(getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//
//                // Update the timer display
//                long seconds = timerDuration / 1000;
//                @SuppressLint("DefaultLocale") String timerText = String.format("%02d:%02d:%02d",
//                        seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
//                txtTimer.setText(timerText);
//
//                // Decrease the timer duration
//                timerDuration -= timerInterval;
//
//                // Check if the timer has reached 0
//                if (timerDuration < 0) {
//                    txtTimer.setText("00:00:00");
//                    isTimerExpired = true;
//                    // You can choose to do something here or leave it as is
//                } else {
//                    // Continue updating the timer
//                    sendEmptyMessageDelayed(0, timerInterval);
//                }
//            }
//        };

        // Start the timer by sending an initial message to the handler
//        timerHandler.sendEmptyMessage(0);
        endTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //manualTestResultDialog(mTestName, result, CameraTestResultActivity.this);
                // Stop the timer when the test is finished


                mTestKeys.testFinished(false);
                manualTestResultDialog(mTestName, TestResult.FAIL, KeysTestActivity.this);
                finish();


            }
        });
        InitUI();

        curr_test_card = findViewById(R.id.curr_test_card_view);
        layoutInflater = LayoutInflater.from(getApplicationContext());


//        setPrevTestResultUI();
//        nextTestUI();
//
//        if (selectedManualTestsResult.size() > 2 && !globalConfig.isIsRetry()) {
//         //   autoScroll(scrollView_key);
//        }
//        if (selectedManualTestsResult.size() > 2 && globalConfig.isIsRetry() && selectedManualTests.indexOf(mCurrentTest) - 1 > 1) {
//            scrollView_key.post(new Runnable() {
//                @Override
//                public void run() {
//                    scrollView_key.scrollTo(0, prev_test_view[selectedManualTests.indexOf(mCurrentTest) - 1].getTop());
//                }
//            });
//        }

        mDevicekeysList = getKeysList();
        DLog.i(TAG, "mDevicekeysList " + mDevicekeysList);
        prepareKeysPojo();
        keysLayout = (GridView) findViewById(R.id.id_keys_grid);
        if (TestName.SOFTKEYTEST.equalsIgnoreCase(mTestName)) {
            keysLayout.setNumColumns(1);
        } else {
            keysLayout.setNumColumns(1);
        }
        keyTestAdapter = new KeyTestAdapter(this);
        keysLayout.setAdapter(keyTestAdapter);
        ManualTest.getInstance(this).performTest(mTestName, keysHandler);
        wakeDeviceScreen();
    }


    public void setPrevTestResultUI() {


        int f = selectedManualTestsResult.size();
        DLog.d(TAG, "totalCountPrev01=" + f);
        if (globalConfig.isIsRetry()) {
            DLog.d(TAG, "testNamePrev=" + globalConfig.getRetryTestName());
            f = selectedManualTests.indexOf(globalConfig.getRetryTestName());
        }
        DLog.d(TAG, "totalCountPrev02=" + f);

        for (int i = 0; i < f; i++) {

            prev_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            prev_tests_list = findViewById(R.id.prev_test_list_view);     // list above current test view

            /*******************************************************  set/ connect every view in list to its component , name , test img, result img , tests observation */
            result_Observation = (TextView) prev_test_view[i].findViewById(R.id.result_test_observation);
            result_Display_Name = (TextView) prev_test_view[i].findViewById(R.id.result_test_name_result);
            result_image_view = (ImageView) prev_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) prev_test_view[i].findViewById(R.id.test_image);
            test_tile_description = (TextView) prev_test_view[i].findViewById(R.id.testOneLineDecription);
            result_text_view = prev_test_view[i].findViewById(R.id.test_result_text);
            retry_Button=prev_test_view[i].findViewById(R.id.retry_button_manual_test);

            String testName = selectedManualTests.get(i);
            String testResult = selectedManualTestsResult.get(testName);  /** calling func from BaseActivity to get specific test result */

//          handle testResult for null
            if (testResult == null) {    /**  if null make it fail */
                testResult = TestResult.FAIL;
            }

            result_Display_Name.setText(getDisplayName(testName));
            result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
            if (testName.equalsIgnoreCase(BLUETOOTH_TOGGLE)) {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            } else {
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }

            /** change result image according to result we get */
            retry_Button.setVisibility(View.VISIBLE);
            if (testResult.equalsIgnoreCase(TestResult.PASS)) {
            //    result_image_view.setImageResource(R.drawable.success_test);
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
                result_text_view.setVisibility(View.GONE);
                retry_Button.setText("Pass");
                retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
               // result_image_view.setImageResource(R.drawable.fail_test);
                retry_Button.setText("Fail");
                result_text_view.setVisibility(View.VISIBLE);
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
            } else if (testResult.equalsIgnoreCase(TestResult.SKIPPED)) {
                result_image_view.setImageResource(R.drawable.ic_skipped);
                result_text_view.setVisibility(View.VISIBLE);
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
            } else {
                result_image_view.setImageResource(R.drawable.ic_skipped);
                result_text_view.setVisibility(View.VISIBLE);
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
            }
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));


        }

        int finalF = f;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < finalF; i++) {
//                    try {
                    if (prev_test_view[i].getParent() != null) {
                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
                    }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    }
//                    prev_tests_list.removeView(prev_test_view);
                    try {
                        prev_tests_list.addView(prev_test_view[i]);  /**  adding our /view row of test to linearLayout view / list of previous tests */
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Handler handler = new Handler();
//                    try {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
//                                scrollView.setSmoothScrollingEnabled(true);
//                                scrollView.setNestedScrollingEnabled(false);
////                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
////                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
//
//                                if(selectedManualTestsResult.size()>2){
//                                    scrollView.smoothScrollTo(0,prev_test_view[selectedManualTestsResult.size()-2].getTop());
//                                }
//                            }
//                        }, 0);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
                    String testName = selectedManualTests.get(i);
//                    prev_test_view[i].setOnClickListener(new View.OnClickListener() {
//
//                        String testResult = selectedManualTestsResult.get(testName);
//                        ;
//
//                        @Override
//                        public void onClick(View v) {
//                            if (testResult.equalsIgnoreCase(TestResult.PASS) || testResult.equalsIgnoreCase(TestResult.OPTIMIZED)) {
//                                LogUtil.printLog("TEST RESULT", "test : +" + mCurrentTest + "result :" + testResult);
//                            } else {
//                                showRetryDialog(testName);
//                            }
//                        }
//                    });


                }
            }


        });
    }

    void showRetryDialog(String testName) {
        globalConfig.setRetryTestName(testName);
        if (!globalConfig.isIsRetry()) globalConfig.setLastCurrentTest(mCurrentTest);
        globalConfig.setIsRetry(true);
        ManualTestsTryActivity.startActivity(KeysTestActivity.this, mCurrentTest, testName);
    }


    void autoScroll(ScrollView scroll) {
        Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    DLog.d(TAG, "KeyTestActivity + autoscroll " + selectedManualTestsResult.size());

                    scroll.setSmoothScrollingEnabled(true);
                    scroll.setNestedScrollingEnabled(false);
//                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
//                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
//                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));scrollView.fullScroll(View.)
                    if (selectedManualTestsResult.size() > 2) {
                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size() - 2].getTop());
                    }
                }
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void nextTestUI() {
        final boolean[] currTest = {false};
        for (int i = 0; i < (selectedManualTests.size()); i++) {
//            try {
//                if (next_test_view != null)
//                    if (next_test_view[i].getParent() != null) {
//                        ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
////                prev_tests_list.removeAllViews();
//                    }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            String testName = selectedManualTests.get(i);
            next_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            next_tests_list = findViewById(R.id.next_test_list_view);
            result_Display_Name = (TextView) next_test_view[i].findViewById(R.id.result_test_name_result);
            result_Observation = (TextView) next_test_view[i].findViewById(R.id.result_test_observation);
            result_image_view = (ImageView) next_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) next_test_view[i].findViewById(R.id.test_image);
            retry_Button = next_test_view[i].findViewById(R.id.retry_button_manual_test);
            String testResult = selectedManualTestsResult.get(testName);
//            result_text_view = next_test_view[i].findViewById(R.id.test_result_text);
            test_tile_description = (TextView) next_test_view[i].findViewById(R.id.testOneLineDecription);

            if (testName.equalsIgnoreCase(BLUETOOTH_TOGGLE)) {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            } else {
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }
            result_Display_Name.setText(getDisplayName(testName));
//            result_image_view.setImageResource(R.drawable.ic_not_equipped);
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
            if(testResult != null) {
                result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
                retry_Button.setVisibility(View.VISIBLE);
                if (testResult.equalsIgnoreCase(TestResult.PASS)) {
               //     result_image_view.setImageResource(R.drawable.success_test);
                    retry_Button.setText("Pass");
                    result_text_view.setVisibility(View.GONE);
                    retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
                } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
                   // result_image_view.setImageResource(R.drawable.fail_test);
                    retry_Button.setText("Fail");
                    result_text_view.setVisibility(View.VISIBLE);
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
                } else if (testResult.equalsIgnoreCase(TestResult.SKIPPED)) {
                    result_image_view.setImageResource(R.drawable.ic_skipped);
                    result_text_view.setVisibility(View.VISIBLE);
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
                } else {
                    result_image_view.setImageResource(R.drawable.ic_skipped);
                    result_text_view.setVisibility(View.VISIBLE);
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
                }
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (selectedManualTests.size()); i++) {
//                    if (prev_test_view[i].getParent() != null) {
//                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
//                    }
//                    prev_tests_list.removeView(prev_test_view);
                    if (currTest[0]) {
                        try {
                            next_tests_list.addView(next_test_view[i]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (selectedManualTests.get(i).equalsIgnoreCase(mCurrentTest)) {
                        currTest[0] = true;
                    }
                }
            }
        });
    }


    @Override
    protected String getToolBarName() {
        return testDisplayName();
    }

    private String testDisplayName() {
        if (getIntent == null)
            getIntent = getIntent();
        String testName = getIntent.getStringExtra(TEST_NAME);
        return getDisplayName(testName);
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    public void changeBG(final String pressedKey) {
        int position = mDevicekeysList.indexOf(pressedKey);
        for (int j = 0; j < keysPOJOArray.size(); j++) {
            if (keysPOJOArray.get(j).getKeyName().equalsIgnoreCase(keyName(position))) {
                keysPOJOArray.get(position).setChecked(true);
            }
        }
        keysLayout.smoothScrollToPosition(position);
        keyTestAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("startTest", false)) {
            getIntent().putExtra("startTest", false);
            ManualTest.getInstance(this).performTest(mTestName, keysHandler);
        }
    }

    public static void startTheTimer() {
        txtTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(30 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                txtTimer.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }

            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                txtTimer.setText("00:00:00");
            }
        }.start();

    }

    private void InitUI() {
//        testName = (TextView) findViewById(R.id.current_test_view);
//        testNumView = findViewById(R.id.test_num_view);
        currentTestIndex = BaseActivity.selectedManualTests.indexOf(mTestName);
//        mImageView = (ImageView) findViewById(R.id.manual_test_img);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
//        testName.setTypeface(tf);
      //  testName.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(mTestName));
        //keysGifViewLayout = (LinearLayout) findViewById(R.id.keysGifViewLayout);
        testDescLayout = findViewById(R.id.manual_test_desc_ll);
//            testDescLayout.setVisibility(View.GONE);
//        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex + 1, BaseActivity.selectedManualTests.size()));
//            if (currentTestIndex > 0)
//                prevTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(BaseActivity.selectedManualTests.get(currentTestIndex - 1)));
//            else
//                prevTest.setText("");
//            if (currentTestIndex < BaseActivity.selectedManualTests.size() - 1)
//                nextTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(BaseActivity.selectedManualTests.get(currentTestIndex + 1)));
//            else
//                nextTest.setText("");
        if (TestName.HARDKEYTEST.equalsIgnoreCase(mTestName)) {
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
            //mImageView.setImageResource(R.drawable.hardkey);
        } else {
            //mImageView.setImageResource(R.drawable.softkey);
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
        }
    //    mImageView.setImageResource(TestUtil.manualtestImageMap.get(mTestName));

        if (mCurrentTest != null) {
//            mTestName.setText(getDisplayName(mCurrentTest));
            mCurrentManualTest = mCurrentTest;
            int resourceID = getResourceID(mCurrentTest, TEST_TRY_MESAGE, Util.disableSkip());
            if (resourceID == 0) {
                resourceID = getResourceID(mCurrentTest, TEST_TRY_MESAGE);
            }
//            test_desc.setText(resourceID);
            //imageView.setImageResource(getResourceID(mCurrentTest, TEST_TRY_IMAGE));
        }
        if (TestName.HARDKEYTEST.equalsIgnoreCase(mCurrentTest)) {
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
        } else {
            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
        }
        if (TestName.SOFTKEYTEST.equalsIgnoreCase(mCurrentTest)) {
            if (mDevicekeys != null && mDevicekeys.contains("HOME")) {
                TestUtil.manualtestGifMap.put(TestName.SOFTKEYTEST, "softkeyhome.gif");
            }
        }
        /*if(TestName.SOFTKEYTEST.equalsIgnoreCase(mTestName)){
            if(!TextUtils.isEmpty(mDevicekeys) && mDevicekeys.contains("HOME")){
                TestUtil.manualtestGifMap.put(TestName.SOFTKEYTEST,"softkeyhome.gif");
            }else{
                TestUtil.manualtestGifMap.put(TestName.SOFTKEYTEST,"softkey.gif");
            }
        }*/
        /*keysGifViewLayout.removeAllViews();
        keysGifViewLayout.addView(baseAcvity.getGIFMovieView(getApplicationContext(), mTestName));*/
     /*   if (baseAcvity != null) {
            baseAcvity.setFontToView(testName, BaseActivity.OPENSANS_LIGHT);
        }*/
    }

    public String keyName(int position) {
        String keyname = "";
        try {
            if (position < mDevicekeysList.size()) {
                keyname = mDevicekeysList.get(position).toLowerCase();
                keyname = keyname.replace("_", " ");
            }

            String[] tokens = keyname.split("\\s");
            keyname = "";
            for (int i = 0; i < tokens.length; i++) {
                char capLetter = Character.toUpperCase(tokens[i].charAt(0));
                keyname += " " + capLetter + tokens[i].substring(1);
            }
            keyname = keyname.trim();
        } catch (Exception e) {
        }
        return keyname;
    }

    public ArrayList<String> getKeysList() {
        if (mDevicekeys == null) {
            if (TestName.HARDKEYTEST.equalsIgnoreCase(mTestName)) {
                mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
            } else {
                mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
                DLog.i(TAG, "getKeysList mDevicekeys : " + mDevicekeys);
            }
            return (new ArrayList<String>());
        }

        mDevicekeys = mDevicekeys.replaceAll(",,", ",");       //Required two times to get single comma seperated keys.
        mDevicekeys = mDevicekeys.replaceAll("\n", "");
        mDevicekeys = mDevicekeys.replaceAll("\t", "");
        mDevicekeys = mDevicekeys.replaceAll(" ", "");
        ArrayList<String> keyslist = new ArrayList<String>(Arrays.asList(mDevicekeys.split(",")));
        return keyslist;
    }



    class KeyTestAdapter extends BaseAdapter {

        private Context context;

        public KeyTestAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return keysPOJOArray.size();
        }

        @Override
        public Object getItem(int position) {
            return keysPOJOArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView name;
            ImageView imageView;

            ViewHolder(View v) {
                this.imageView = (ImageView) v.findViewById(R.id.id_checkbox_keys);
                this.name = (TextView) v.findViewById(R.id.key_name);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater _layoutInflator = (LayoutInflater) context.getSystemService
                        (context.LAYOUT_INFLATER_SERVICE);
                if (TestName.SOFTKEYTEST.equalsIgnoreCase(mTestName)) {
                    convertView = _layoutInflator.inflate(R.layout.keys_grid_child_softkeys, null);
                } else {
                    convertView = _layoutInflator.inflate(R.layout.keys_grid_child, null);
                }
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //holder.name.setText(keyName(position));
            addKeysTextUI(holder.name, position);
            if (keysPOJOArray.get(position).isChecked()) {
                holder.imageView.setImageResource(R.drawable.ic_passed);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_checkblank);
            }
            return convertView;
        }
    }

    public void prepareKeysPojo() {
        keysPOJOArray.clear();
        for (int i = 0; i < mDevicekeysList.size(); i++) {
            keyPOJO keyPOJO = new keyPOJO();
            keyPOJO.setKeyName(keyName(i));
            keyPOJO.setChecked(false);
            keysPOJOArray.add(i, keyPOJO);
        }
    }

    class keyPOJO {
        private boolean checked = false;
        private String keyName;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if(!Util.isAdvancedTestFlow()) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }*/
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.keys_test_activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void manualTestResultDialog(final String testName, final String result, Context ctx) {
        View coordinatorLayout = getWindow().findViewById(R.id.activity_manual_test);
        String message = null;
        final boolean[] testRetryed = {false};
        if (result == TestResult.PASS) {
            baseAcvity.updateTestResult(testName, TestResult.PASS);
            message = getResources().getString(R.string.pass_status);
        } else if (result == TestResult.FAIL) {
            message = getResources().getString(R.string.fail_status);
            baseAcvity.updateTestResult(testName, TestResult.FAIL);
        } else if (result == TestResult.TIMEOUT) {
            message = getResources().getString(R.string.timeout_status);
            baseAcvity.updateTestResult(testName, TestResult.FAIL);
        }
        if (coordinatorLayout == null) {
            coordinatorLayout = getWindow().getDecorView().getRootView();
        }
        if (!isAssistedApp) {
            showResultMessage2(message, result, testName, ctx);
        }

       /* Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        if (result == TestResult.TIMEOUT) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    testRetryed[0] = true;
                    baseAcvity.startManualTest(testName);
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
                    baseAcvity.startNextManualTest();
            }

            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
            }
        });
        snackbar.show();*/
    }

    public void showResultMessage2(final String message, final String result, final String testName, final Context context) {

        DLog.d(TAG, "TestResult:" + result + "messgage:" + message);

        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!TestResult.TIMEOUT.equalsIgnoreCase(result)) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            final View view = layoutInflater.inflate(R.layout.test_result_dialog, null);
            if (TestResult.PASS.equalsIgnoreCase(result)) {
                ImageView imageView = view.findViewById(R.id.test_result_img);
                TextView textView = view.findViewById(R.id.test_result_msg);
                imageView.setImageResource(R.drawable.pass);
                textView.setText(R.string.pass_status);
            }
            Dialog dialog = new Dialog(context);
            dialog.setContentView(view);
            dialog.show();
            resultHandler.sendEmptyMessageDelayed(resultCode, 3000);
            return;
        }


//        alertDialog = CommonUtil.DialogUtil.showAlert(this, getString(R.string.alert), message, getString(R.string.btn_retry), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                resultHandler.removeMessages(resultCode);
//                alertDialog.dismiss();
//                Log.d("KeysTestActivity", "alertDialogBuilder");
//                startTheTimer();
//                ManualTest.getInstance(KeysTestActivity.this).resumeTest(testName);
//            }
//        });
//        alertDialog.show();

        String[] btnString = {"CANCEL", "RETRY"};

        CommonUtil.DialogUtil.twoButtonDialog(this, getString(R.string.alert), message, btnString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel btn
                DLog.d(TAG, "alertDialogBuilder");
                selectedManualTestsResult.put(mCurrentTest, TestResult.SKIPPED);
                updateTestResult(mCurrentTest, TestResult.SKIPPED);
//                isRetry=false;
                startNextManualTest();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //retry btn
                resultHandler.removeMessages(resultCode);
                DLog.d(TAG, "alertDialogBuilder");
                startTheTimer();
                ManualTest.getInstance(KeysTestActivity.this).resumeTest(testName);
            }
        });

    }

    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            DLog.d(TAG, "ResultCode:" + msg.what);
            if (msg.what == 1111) {
                baseAcvity.startManualTest(baseAcvity.mCurrentManualTest);
            } else if (msg.what == 2222) {
                baseAcvity.startNextManualTest();
                finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void wakeDeviceScreen() {
        PowerManager mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPM.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "org.pervacio.wirelessapp");
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    private void addKeysTextUI(TextView textView, int position) {
        String finalValue = mDevicekeysList.get(position);
        String appendKeyName = finalValue.toLowerCase();
        String key_value = "key_" + appendKeyName;
        int value = getResources().getIdentifier(key_value, "string", this.getPackageName());
        if (value != 0) {
            finalValue = getString(value);
        }
        textView.setText(finalValue);
    }

    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            super.onBackPressed();
        }
    }
}
