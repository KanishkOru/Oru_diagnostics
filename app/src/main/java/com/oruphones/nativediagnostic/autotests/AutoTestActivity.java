package com.oruphones.nativediagnostic.autotests;

import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTH_TOGGLE;
import static com.oruphones.nativediagnostic.models.tests.TestName.EARPIECETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.FRONTCAMERAPICTURETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.MICROPHONE2TEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.MICROPHONETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.REARCAMERAPICTURETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.SPEAKERTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.VIBRATIONTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.WIFICONNECTIVITYTEST;
import static com.oruphones.nativediagnostic.models.tests.TestResult.CANBEIMPROVED;
import static com.oruphones.nativediagnostic.models.tests.TestResult.FAIL;
import static com.oruphones.nativediagnostic.models.tests.TestResult.OPTIMIZED;
import static com.oruphones.nativediagnostic.models.tests.TestResult.PASS;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.AutoTest;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoTestActivity extends BaseActivity {

    //    private ProgressBar progressBar;

    private AutoTest autoTest;
    private int totalAutoTest=0;
    private ArrayList<TestInfo> arraytest = new ArrayList<>();
    private int totalAutoTests;
    private boolean isFirstTime = true;
    private static String TAG = AutoTestActivity.class.getSimpleName();
    private int currentHighlightedPosition = -1;

    private int autoTestCompleted =0;
    //    private TextView analysingTV;
//    private TextView percentageView;
//    private LinearLayout continueLayout;
//    private TextView checkingTestName, goToResults, addManualTests;
    private CardView camera_preview;
    public LinearLayout layout_result_test;
    private RecyclerView recyclerView;
    private AutotestAdapter adapter;
    private ZoomRecyclerLayout layoutManager;
    public View list_item = null;
    public View list_item2 = null;
    LayoutInflater inflater;
    public TextView result_Display_Name, result_Observation,progress_Message,autotest_Remaining_Message;
    public FrameLayout result_image_view;
    public ImageView auto_test_img;
    public TextView result_Display_Name2, result_Observation2,autoTestCount,cancel_Auto_Test,retry_Button;
    public ImageView result_image_view2, autoTest_success_img_With_Remaining_Msg;

    ProgressBar progressBar;
    ArrayList<TestInfo> testList = null;
    PervacioTest pervacioTest;
    private long lastUpdatedTime = 0;


    private Button start_Manual_Test;
    private GetTests getTestsTask = null;
    int testRetryCount = 0;

    private Intent serviceIntent;  // Assume you started a service from this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        analysingTV = (TextView) findViewById(R.id.analysing);
//        percentageView = findViewById(R.id.percentage);
//        checkingTestName = (TextView) findViewById(R.id.checkingtestName);
//        goToResults = (TextView) findViewById(R.id.cancel_tv);
//        goToResults.setVisibility(View.GONE);
//        addManualTests = (TextView) findViewById(R.id.accept_tv);
//        addManualTests.setText(getString(R.string.addManualtests));
//        addManualTests.setVisibility(View.GONE);
//        goToResults.setText(getString(R.string.gotoresults));
//        continueLayout = (LinearLayout) findViewById(R.id.layout_next);
        progressBar = findViewById(R.id.progressBar);
        camera_preview = (CardView) findViewById(R.id.camera_preview);
       // layout_result_test = (LinearLayout) findViewById(R.id.detailed_test_running);
        autoTestCount = (TextView) findViewById(R.id.auto_test_count);
        progress_Message = (TextView) findViewById(R.id.autotest_progress_message);
        autotest_Remaining_Message=(TextView) findViewById(R.id.autotest_remaining_message);
        autoTest_success_img_With_Remaining_Msg = (ImageView) findViewById(R.id.autotest_successimg_with_testt_remaining);
        start_Manual_Test = (Button) findViewById(R.id.start_manual_test);
        cancel_Auto_Test = (TextView) findViewById(R.id.cancel_auto_test);

        serviceIntent = new Intent(this, AutoTest.class);
        startService(serviceIntent);

//        layout_result_test.removeAllViews();
        inflater = LayoutInflater.from(getApplicationContext());
        View list_item = inflater.inflate(R.layout.resolution_list_view, null);
        View list_item2 = inflater.inflate(R.layout.resolution_list_view, null);

        //restart();
        start_Manual_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextManualTest();
                finish();
            }
        });

        cancel_Auto_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //setFontToView(analysingTV,ROBOTO_REGULAR);
        //setFontToView(checkingTestName,ROBOTO_LIGHT);


//        setFontToView(goToResults,ROBOTO_MEDIUM);
//        setFontToView(addManualTests,ROBOTO_MEDIUM);


        //         TODO: 25/3/21 Replace
//        goToResults.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ResultsActivity.start(AutoTestActivity.this);
//
//            }
//        });
//        addManualTests.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (globalConfig.getManualTestList(PervacioTest.getInstance().getSelectedCategory()).size() > 0) {
//                    selectedManualTests.clear();
//                    Intent manualTestSelection = new Intent(AutoTestActivity.this, ManualTestsActivity.class);
//                    manualTestSelection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(manualTestSelection);
//                    finish();
//                } else {
//                    ResultsActivity.start(AutoTestActivity.this);
//                }
//            }
//        });
//        globalConfig = GlobalConfig.getInstance();



        testList = globalConfig.getAutoTestList(PervacioTest.getInstance().getSelectedCategory());
        autoTest = AutoTest.getInstance(this);
        recyclerView = findViewById(R.id.recyclerView);

        // Create an instance of your ResultAdapter and set it to the RecyclerView
        adapter = new AutotestAdapter(getApplicationContext(), testList);
        recyclerView.setAdapter(adapter);

        layoutManager = new ZoomRecyclerLayout(this, RecyclerView.HORIZONTAL, false);
        layoutManager.scaleView(true);
        layoutManager.setScrollSpeed(200f);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);
        pervacioTest = PervacioTest.getInstance();
        totalAutoTest=testList.size()*2;
        autoTestCount.setText(String.valueOf(totalAutoTest));
        totalAutoTests=totalAutoTest;
        progressBar.setProgress(0);
        DLog.d(TAG, "AutoList- "+testList);
        start_Manual_Test.setEnabled(false);
        addInitialUI();
        TestList.clear();
        performTests();
    }




    @Override
    public void onPause() {
        super.onPause();

        autoTest.detect_stop(true);


        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        getTestsTask.cancel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        autoTest.detect_stop(false);
        restart();


       // finish();

        // Start a new instance of the activity
       // Intent intent = new Intent(this, AutoTestActivity.class);
       // startActivity(intent);



    //  try {
    //      if (getTestsTask.isCancelled()) {
    //          performTests();
    //      }
    //  } catch (Exception e) {
    //      e.printStackTrace();
    //  }




    }

    private void restart() {


//        if (isFirstTime) {
//            isFirstTime = false;
//        }
//        else {
//            // Your logic when the activity is maximized or brought to the foreground
//            stopService(serviceIntent);
//            // Finish the current activity
//            finish();
//            // Start a new instance of the activity
//            Intent intent = new Intent(this, PinValidationActivity.class);
//            startActivity(intent);
//        }
        if(globalConfig.getisFirstTime()) {
            globalConfig.setisFirstTime(false);
        } else {
            stopService(serviceIntent);
            globalConfig.setisFirstTime(true);
            startApp();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       autoTest.onDestroy();
        // Unbind or release any other resources if needed
    }


    @Override
    protected String getToolBarName() {
        /*String selectedCatagory = globalConfig.getCategoryDisplayName(PervacioTest.getInstance().getSelectedCategory());
        if (selectedCatagory == null || "".equalsIgnoreCase(selectedCatagory))
            return getString(R.string.full_diagnostics);
        else
            return selectedCatagory;*/
        return getResources().getString(R.string.auto_tests_title);
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            if (!isAssistedApp) {
                super.onBackPressed();

                /*Intent intent = new Intent(AutoTestActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                CommonUtil.DialogUtil.getAlert(AutoTestActivity.this, getString(R.string.alert), getString(R.string.are_you_sure_back), getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AutoTest.BTToggleCompleted = true;
                        AutoTest.WifiToggleCompleted = true;
                        AutoTest.GPSTestCompleted = true;
                        try {
                            if (getTestsTask != null && getTestsTask.getStatus() == AsyncTask.Status.RUNNING) {
                                getTestsTask.cancel(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pervacioTest.updateSession();
                        if (PervacioTest.getInstance().getAutoTestResult() != null && PervacioTest.getInstance().getAutoTestResult().size() > 0)
                            updateHistory();

                        HashMap<String, Object> testResultMap = new HashMap<>();
                        testResultMap.put("DiagSessionId", GlobalConfig.getInstance().getSessionId());
                        testResultMap.put("status", "Incomplete");
                        pervacioTest.updateSession();
                        try {
                            if (!globalConfig.getIsResultSubmitted()) {
                                //MobiruFlutterActivity.mResult.success(testResultMap);
                                globalConfig.setIsResultSubmitted(true);
                                //  MobiruFlutterActivity.isResultSubmitted = true;
                            }
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                }, getString(R.string.str_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
//                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected int getLayoutResource() {
//        return R.layout.activity_auto_test;
        return R.layout.activity_test_dynamic;
    }

    public void performTests() {
        getTestsTask = new GetTests();
        getTestsTask.execute();
    }


    private void newUi(String name, String result, Boolean initial, int pos,String currTest) {
//        ArrayList<TestInfo> testList = globalConfig.getAutoTestList(PervacioTest.getInstance().getSelectedCategory());
//        boolean resultHasBatteryPerformance = false;
//        for (TestInfo testInfo : testList) {
//            if (TestName.BATTERYPERFORMANCE.equalsIgnoreCase(testInfo.getName())) {
//                resultHasBatteryPerformance = true;
//            }
//        }
//        for (TestInfo testInfo : testList) {
//            if (resultHasBatteryPerformance) {
//                if (BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_PASS ||
//                        BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_FAIL) {
//                    if (TestName.QUICKBATTERYTEST.equalsIgnoreCase(testInfo.getName())) {
//                        continue;
//                    }
//                }
//            }
        list_item = inflater.inflate(R.layout.results_text, null);
        result_Observation = (TextView) list_item.findViewById(R.id.result_test_observation);
        result_Display_Name = (TextView) list_item.findViewById(R.id.result_test_name_result);
        result_image_view = (FrameLayout) list_item.findViewById(R.id.result_image);
//        auto_test_img = (ImageView) list_item.findViewById(R.id.auto_test_icon) ;

        AnimatedGifUtils.setImageRes(result_image_view,getApplicationContext(),currTest);
        DLog.d(TAG + "#999" ," testname : : "+currTest);




//        result_image_view.post(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.printLog(TAG + "#999" ," testname : : "+currTest);
//                AnimatedGifUtils.setImageRes(result_image_view,getApplicationContext(),currTest);
//
//            }
//        });

        result_Display_Name.setText(name);
        result_Observation.setText(CommonUtil.getMappedTestResult(result));

        if (TestResult.CANBEIMPROVED.equals(result)) {
            if (GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaO2UK")) {
                result_image_view.setBackgroundResource(R.drawable.results_optimizable_amber);
            } else {
                //removed optimized icon because it was not in ui
                result_image_view.setBackgroundResource(R.drawable.icon_success);
            }
            updateUI(name, result, pos);
        } else if (PASS.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.icon_success);
            updateUI(name, result, pos);
        } else if (FAIL.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.icon_fail);
            updateUI(name, result, pos);
        } else if (TestResult.OPTIMIZED.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.icon_success);
            updateUI(name, result, pos);
        } else if (TestResult.NOTEQUIPPED.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.ic_not_equipped);
            updateUI(name, result, pos);
        } else if (TestResult.SKIPPED.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.ic_skipped);
            updateUI(name, result, pos);
        } else if (TestResult.ACCESSDENIED.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.ic_error);
            updateUI(name, result, pos);
        } else if (TestResult.NOTSUPPORTED.equals(result)) {
            result_image_view.setBackgroundResource(R.drawable.ic_notsupported);
            updateUI(name, result, pos);
        } else {
            result_image_view.setBackgroundResource(R.drawable.blank_circle);
            if (initial) {
                updateUI(name, result, pos);
            }
        }

//        }
    }

    private void addInitialUI() {
        int numTests = testList.size();
        for (int i = 0; i < numTests; i++) {
            lastUpdatedTime = System.currentTimeMillis();
            final TestInfo testInfo = testList.get(i);
            testInfo.setTestStartTime(System.currentTimeMillis());
            newUi(testInfo.getDisplayName(), "", true, i,testInfo.getName());
            progress_Message.setText(R.string.progress_message3);
            start_Manual_Test.setAlpha((float) 0.6);
        }

    }

    private void updateProgress(int testCount, int totalCount) {

        int progress = (int) (((float) testCount / totalCount )* 100); // Calculate progress percentage
        if (progress>50)
        {
            progress_Message.setText(R.string.progress_message4);
        }
        else if (progress>70)
        {
            progress_Message.setText(R.string.progress_message);
        }
        progressBar.setProgress(progress);
    }


    private void updateUI(String name, String result, int pos) {
        DLog.d(TAG, "n: " + name + " r: " + result + " p: " + pos);
//        runOnUiThread(new Runnable() {
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public void run() {
//                try {
//                    // if (name.equals("Front camera") || name.equals("Front camera video") ||
//                    //        name.equals("Rear camera") || name.equals("Rear camera video")) {
//                    camera_preview.setVisibility(View.GONE);
//                    // }
//
//                    if (result.equals("")) {
//                        layout_result_test.removeView(list_item);
//                        layout_result_test.addView(list_item, pos);
//                    } else {
//                        try {
//                            layout_result_test.removeViewAt(pos);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            DLog.e(TAG, "Error removing view at position " + pos + ": " + e.getMessage());
//                        }
//                        layout_result_test.removeView(list_item);
//                        layout_result_test.addView(list_item, pos);
//                    }
//
//                    Handler handler = new Handler();
//                    try {
//                        CustomHorizontalScrollView sv = findViewById(R.id.scrollview);
//                        sv.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    // Scroll to center item
//                                    int centerOffset = (sv.getWidth() - list_item.getWidth()) / 2; // Adjust this value to control the centering offset
//                                    int x = (list_item.getLeft() - centerOffset);
//                                    sv.smoothScrollTo(x + centerOffset + centerOffset / 6, 0);
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            int totalItems = layout_result_test.getChildCount();
//                                            // Reset all items to their default scale and opacity
//                                            for (int i = 0; i < totalItems; i++) {
//                                                View item = layout_result_test.getChildAt(i);
//                                                item.setScaleX(1.0f);
//                                                item.setScaleY(1.0f);
//                                                item.setAlpha(1.0f);
//                                            }
//
//                                            // Scale down and reduce opacity of all items except the center item
//                                            for (int i = 0; i < totalItems; i++) {
//                                                View item = layout_result_test.getChildAt(i);
//                                                if (i != pos + 1) {
//                                                    item.setScaleX(0.8f); // Adjust this value to control the size of smaller items
//                                                    item.setScaleY(0.8f); // Adjust this value to control the size of smaller items
//                                                    item.setAlpha(0.5f); // Adjust this value to control the opacity of smaller items
//                                                }
//                                            }
//                                        }
//                                    }, 100); // Delay the scaling and opacity adjustment for 100 milliseconds for a smoother transition
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    DLog.e(TAG, "Error in scroll runnable: " + e.getMessage());
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        DLog.e(TAG, "Error in post runnable: " + e.getMessage());
//                    }
//
//                    int remainingAutoTest = Math.abs(--totalAutoTest);
//                    autoTestCount.setText(String.valueOf(remainingAutoTest));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    DLog.e(TAG, "Error in run method: " + e.getMessage());
//                }
//            }

//            public void run() {
//
//
//
////                if (name.equals("Front camera") || name.equals("Front camera video") ||
////                        name.equals("Rear camera") || name.equals("Rear camera video")) {
//                camera_preview.setVisibility(View.GONE);
////                }
//
//                if (result.equals("")) {
//                    layout_result_test.removeView(list_item);
//                    layout_result_test.addView(list_item, pos);
//                } else {
//                    try {
//                        layout_result_test.removeViewAt(pos);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    layout_result_test.removeView(list_item);
//                    layout_result_test.addView(list_item, pos);
//                }
//
//                Handler handler = new Handler();
//                try {
//                    CustomHorizontalScrollView sv = findViewById(R.id.scrollview);
//                    sv.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Scroll to center item
//                            int centerOffset = (sv.getWidth() - list_item.getWidth()) / 2; // Adjust this value to control the centering offset
//                            int x =( list_item.getLeft() - centerOffset);
//                            sv.smoothScrollTo(x+centerOffset+centerOffset/6, 0);
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    int totalItems = layout_result_test.getChildCount();
//                                    //       View currentTest = layout_result_test.getChildAt(pos);
//                                    // Reset all items to their default scale and opacity
//                                    for (int i = 0; i < totalItems; i++) {
//                                        View item = layout_result_test.getChildAt(i);
//                                        item.setScaleX(1.0f);
//                                        item.setScaleY(1.0f);
//                                        item.setAlpha(1.0f);
//                                    }
//
//                                    // Scale down and reduce opacity of all items except the center item
//                                    for (int i = 0; i < totalItems; i++) {
//                                        View item = layout_result_test.getChildAt(i);
//                                        if (i != pos+1) {
//                                            item.setScaleX(0.8f); // Adjust this value to control the size of smaller items
//                                            item.setScaleY(0.8f); // Adjust this value to control the size of smaller items
//                                            item.setAlpha(0.5f); // Adjust this value to control the opacity of smaller items
//                                        }
//                                    }
//                                }
//                            }, 100); // Delay the scaling and opacity adjustment for 100 milliseconds for a smoother transition
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                int remainingAutoTest = Math.abs(--totalAutoTest);
//                autoTestCount.setText(String.valueOf(remainingAutoTest));
//            }
//        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    camera_preview.setVisibility(View.GONE);
                    TestInfo newData = new TestInfo(name, result);
                    if (result.equals("")) {
                        arraytest.add(pos, newData);
                        adapter.notifyItemInserted(pos);
                    } else {
                        arraytest.set(pos, newData);
                        adapter.notifyItemChanged(pos);
                    }

                    try {
                        View listItem = layoutManager.findViewByPosition(pos);
                        if (listItem != null) {
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // Scroll to center item
                                        int centerPosition = layoutManager.getPosition(listItem);
//                                        int centerOffset = (recyclerView.getWidth() - listItem.getWidth()) / 2;
                                        recyclerView.smoothScrollToPosition(centerPosition+1);
                                        adapter.setHighlightedPosition(centerPosition+1);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        DLog.e(TAG, "Error in post runnable: " + e.getMessage());
                                    }
                                }
                            });
                        }

                        int remainingAutoTest = Math.abs(--totalAutoTest);
                        autoTestCount.setText(String.valueOf(remainingAutoTest));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public class GetTests extends AsyncTask<Void, Pair<TestInfo, Integer>, String> {
        @Override
        protected void onPreExecute() {
//            analysingTV.setText(getResources().getString(R.string.analysing));
            pervacioTest.startSession();
            //          testList.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            DLog.d(TAG, "Selected Catagory:" + PervacioTest.getInstance().getSelectedCategory());
            String result = null;
            DLog.d(TAG, "doInBackground test list: " + testList);
            int numTests = testList.size();
            for (int i = 0; i < numTests; i++) {
                if (pervacioTest.getAutoTestResult().get(testList.get(i).getName()) != null
                        && (pervacioTest.getAutoTestResult().get(testList.get(i).getName()).getTestResult().equalsIgnoreCase(PASS)
                        || pervacioTest.getAutoTestResult().get(testList.get(i).getName()).getTestResult().equalsIgnoreCase(FAIL)
                        || pervacioTest.getAutoTestResult().get(testList.get(i).getName()).getTestResult().equalsIgnoreCase(OPTIMIZED)
                        || pervacioTest.getAutoTestResult().get(testList.get(i).getName()).getTestResult().equalsIgnoreCase(CANBEIMPROVED))) {
                    continue;
                }
                lastUpdatedTime = System.currentTimeMillis();
                final TestInfo testInfo = testList.get(i);
                testInfo.setTestStartTime(System.currentTimeMillis());
                // Pair progress = new Pair(testInfo.getDisplayName(), i * (100 / numTests));
                Pair progress = new Pair<>(testInfo, i * (100 / numTests));
                publishProgress(progress);
                result = autoTest.performTest(testInfo.getName(), camera_preview);

                DLog.d(TAG, "testInfo.getName() : : "+testInfo.getName() + result);
//                testRetryCount++;x
                DLog.d(TAG, "doInBackground testRetryCount: " + testRetryCount);

//
//                if (testInfo.getName())

                if (testInfo.getName().equals("VibrationTest")&& result.equals(FAIL)){
                    globalConfig.addItemToList("Vibration Auto Test: Fail");
                    //TestList.add(testInfo.getName());
                }


                while (testRetryCount < 3 && result.equalsIgnoreCase(FAIL) && !testInfo.getName().equals("VibrationTest") ) {
                    testRetryCount = testRetryCount + 1;
                    DLog.d(TAG, "doInBackground2 testRetryCount: " + testRetryCount);
                    if (testRetryCount==3 && result.equals(FAIL)){
                        globalConfig.addItemToList(testInfo.getName()+"Auto : Fail");

                    }
                    DLog.d(TAG, "testInfo.getName() testRetryCount  "+ testInfo.getName());
                    result = autoTest.performTest(testInfo.getName(), camera_preview);
                }
                testRetryCount = 0;
                if(result.equalsIgnoreCase(FAIL)){
                    TestList.add(testInfo.getName());
                    DLog.d("FailedAutoTest",testInfo.getName() + result);
                }
                if (result != TestResult.PASS) {
//                    set speaker and other tests as manual test if any of the test fails
                    if (testInfo.getName().equalsIgnoreCase(BLUETOOTH_TOGGLE) || testInfo.getName().equalsIgnoreCase(SPEAKERTEST) || testInfo.getName().equalsIgnoreCase(EARPIECETEST) || testInfo.getName().equalsIgnoreCase(MICROPHONETEST) || testInfo.getName().equalsIgnoreCase(MICROPHONE2TEST) || testInfo.getName().equalsIgnoreCase(VIBRATIONTEST) || testInfo.getName().equalsIgnoreCase(REARCAMERAPICTURETEST) || testInfo.getName().equalsIgnoreCase(FRONTCAMERAPICTURETEST) || testInfo.getName().equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                        HashMap<String, ArrayList> manualTestList = globalConfig.getManualTestMap();
                        DLog.d(TAG, "doInBackground manualTestList before: " + manualTestList);
//                        manualTestList.put(testInfo.getName(), testInfo);
//                        get all keys from manualTestList except checkMyDevice
                        Set<String> keys = manualTestList.keySet();
                        String category = "RunAllDiagnostics";
                        for (String key : keys) {
                            if (!key.equalsIgnoreCase("checkMyDevice")) {
                                category = key;
                            }
                        }
//                        get RunAllDiagnostics list from manualTestList and add the testInfo to it
//                        if(manualTestList.containsKey("VerifyDevice")){
//                        }
//                        ArrayList<TestInfo> checkMyDeviceList = manualTestList.get("RunAllDiagnostics");
                        ArrayList<TestInfo> checkMyDeviceList = manualTestList.get(category);
                        if (checkMyDeviceList == null) {
                            checkMyDeviceList = new ArrayList<TestInfo>();
                        }
                        checkMyDeviceList.add(testInfo);
                        manualTestList.put(category, checkMyDeviceList);
                        DLog.d(TAG, "doInBackground testInfo: " + testInfo);
                        globalConfig.setManualTestMap(manualTestList);
                        DLog.d(TAG, "doInBackground manualTestList after: " + manualTestList);
//                        ManualTestNewAdapter manualTestAdapter;
//                        BaseActivity.selectedManualTests.clear();
//                        BaseActivity.selectedManualTests.addAll(manualTestAdapter.getSelectedTest());
                        testInfo.setTestResult(result);
                    } else {
                        testInfo.setTestResult(result);
                    }
                } else {

                    testInfo.setTestResult(result);
                }
//                Handler handler2 = new Handler();
//                handler2.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        newUi();
//                    }
//                }, 80);
                testInfo.setTestAdditionalInfo(AutoTest.testAdditionalInfoMap.get(testInfo.getName()));
                testInfo.setTestEndTime(System.currentTimeMillis());
                pervacioTest.getAutoTestResult().put(testInfo.getName(), testInfo);
                newUi(testInfo.getDisplayName(), result, false, i,testInfo.getName());

                DLog.d(TAG, "Test Name:" + testInfo.getDisplayName() + "  Result:" + result);
                //progress = new Pair(testInfo.getDisplayName(), (i + 1) * (100 / numTests));
                progress = new Pair<>(testInfo, (i + 1) * (100 / numTests));
                lastUpdatedTime = System.currentTimeMillis() - lastUpdatedTime;
                if (lastUpdatedTime < 1000) {
                    try {
                        Thread.sleep(1000 - lastUpdatedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress(progress);
                if (isCancelled()) break;
                DLog.d(TAG, "Test Name:" + testInfo.getDisplayName() + "  Result:" + result);

            }
            return result;
        }




        @SafeVarargs
        @Override
        protected final void onProgressUpdate(Pair<TestInfo, Integer>... values) {
            super.onProgressUpdate(values);

            //updates the progress
            autoTestCompleted++;
            updateProgress(autoTestCompleted,totalAutoTests);

            TestInfo testInfo = values[0].first;
            int progress = values[0].second;
            if (progress <= 0) {
                progress = 1;
            }
            //analysingTV.setText(getResources().getString(R.string.analysing) + " " + progress + "%");
//            percentageView.setText(progress + "%");
            if (testInfo != null && !TextUtils.isEmpty(testInfo.getName())) {
                String testText = testInfo.getDisplayName();
                testText = String.format("%s %s", getString(R.string.checking), testText);
                /*switch (testInfo.getName()){
                    case ResolutionName
                            .UNUSEDAPPS:
                        testText =  getString(R.string.resolution_unused_app_identify);
                        break;
                    default:

                }*/
//                checkingTestName.setText(testText);
            }

        }




        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            DLog.d(TAG, "Result:" + str);
            //analysingTV.setText(getResources().getString(R.string.analysing) + " 100%");
//            percentageView.setText("100%");
//            checkingTestName.setText(getString(R.string.auto_tests_completed));
//            progressBar.setVisibility(View.GONE);

            start_Manual_Test.setAlpha(1.0F);
            start_Manual_Test.setEnabled(true);
            progress_Message.setText(R.string.progress_message2);
            autotest_Remaining_Message.setText(R.string.all_test_completed_msg);
            autoTestCount.setVisibility(View.INVISIBLE);
            autoTest_success_img_With_Remaining_Msg.setVisibility(View.VISIBLE);
            cancel_Auto_Test.setVisibility(View.INVISIBLE);

//3 seconds delay added to handle not showing manual test after running autotests and same in seller verification
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing() && !isDestroyed()){
                        DLog.d(TAG,"Manual test handler");
                        startNextManualTest();
                        finish();
                    }


                }
            },2500);
//            continueLayout.setVisibility(View.GONE);
            //continueLayout.setVisibility(View.VISIBLE);
//            if (globalConfig.getManualTestList(PervacioTest.getInstance().getSelectedCategory()).size() == 0) {
//                addManualTests.setVisibility(View.GONE);
//            }
            PervacioTest.getInstance().setSessionStatus("Auto Test Completed");
            pervacioTest.updateSession();
            //added this code so that after auto test perform it will go to manual test list, without asking "go to manual test" and "result"

            if (!isAssistedApp) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (globalConfig.getManualTestList(PervacioTest.getInstance().getSelectedCategory()).size() > 0) {
                            selectedManualTests.clear();
                            /*if(!globalConfig.isTradeIn()) {
                                Intent manualTestSelection = new Intent(AutoTestActivity.this, ManualTestsActivity.class);
                                manualTestSelection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(manualTestSelection);F
                                finish();
                            } else {*/
                            mCurrentManualTest = null;
                            ArrayList<TestInfo> manualTestList = new ArrayList<>();
                            try {
                                manualTestList = globalConfig.getManualTestList(PervacioTest.getInstance().getSelectedCategory());
                            } catch (Exception e) {
                                DLog.e(TAG, "Exception:" + e.getMessage());
                            }
                            for (TestInfo manualtest : manualTestList) {
                                if (!(pervacioTest.getAutoTestResult().get(manualtest.getName()) != null && pervacioTest.getAutoTestResult().get(manualtest.getName()).getTestResult().equalsIgnoreCase(PASS)))
                                    BaseActivity.selectedManualTests.add(manualtest.getName());
//                                    BaseActivity.selectedManualTestsResult.add("Previous Test");
//                                    Log.d("ManualTestResult",manualtest.getTestResult());
                                DLog.d(TAG, manualtest.getName());
                                pervacioTest.getAutoTestResult().remove(manualtest.getName());
                            }
//                            startNextManualTest();
//                            finish();
                        }
//                        } else {


                        //ResultsActivity.start(AutoTestActivity.this);
//                        }
                    }
                }, 500);
            } else {
                ArrayList<PDTestResult> ResultList = new ArrayList<>();
                for (Map.Entry<String, TestInfo> mResult : pervacioTest.getAutoTestResult().entrySet()) {
                    PDTestResult pdTestResult = new PDTestResult();
                    pdTestResult.setName(mResult.getValue().getName());
                    pdTestResult.setStatus(mResult.getValue().getTestResult());
                    ResultList.add(pdTestResult);
                }
                CommandServer.getInstance(AutoTestActivity.this).postEventData("APPS_INFO", Resolution.getInstance().getAppsInfoList());
                CommandServer.getInstance(AutoTestActivity.this).postEventData("STORAGE_INFO", Resolution.getInstance().getStorageFileInfoList());
                CommandServer.getInstance(AutoTestActivity.this).postEventData("AUTO_TESTS_RESULT", ResultList);
            }

        }
    }
}