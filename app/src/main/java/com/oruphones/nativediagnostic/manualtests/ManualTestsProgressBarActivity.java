package com.oruphones.nativediagnostic.manualtests;


import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTH_TOGGLE;
import static com.oruphones.nativediagnostic.models.tests.TestName.WIFICONNECTIVITYTEST;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;

import java.util.Arrays;

/**
 * Created by Pervacio on 18/09/2017.
 */
@Deprecated
public abstract class ManualTestsProgressBarActivity extends BaseActivity {
    private TextView mTestName;
    public TextView mTestDescription;
    private ImageView imageView, prevResultView;
    private static String TAG = ManualTestsProgressBarActivity.class.getSimpleName();
    TextView prevTest;
    TextView nextTest, nextManualTest;
    TextView testNumView;
    private int currentTestIndex = -1;
    private Intent intent;
    String mCurrentTest = null;
    //LinearLayout gifViewLayout;
    private Button mStopBtn;
    private ProgressBar mProgressBar;

    LinearLayout prev_tests_list, next_tests_list;
    LinearLayout curr_test_card;
    LayoutInflater layoutInflater;
    public TextView result_Display_Name, result_Observation,retry_Button, result_text_view;
    public ImageView result_image_view, test_image;

    private TextView test_tile_description;
    View prev_test_view[] = new View[selectedManualTests.size()];
    View next_test_view[] = new View[selectedManualTests.size()];

    ScrollView scrollView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.d(TAG, "ManualTestProgressBarActivity " + selectedManualTestsResult.size());

        intent = getIntent();
        mCurrentTest = intent.getStringExtra(TEST_NAME);
        mTestName = (TextView) findViewById(R.id.current_test_view);
        mTestDescription = (TextView) findViewById(R.id.test_desc_view);
        imageView = (ImageView) findViewById(R.id.manual_test_img);
        //gifViewLayout = (LinearLayout)  findViewById(R.id.sensor_gifViewLayout);
        //mStopBtn = (Button) findViewById(R.id.stoptest_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
        test_tile_description=(TextView) findViewById(R.id.testOneLineDecription);
        retry_Button= findViewById(R.id.retry_button_manual_test);
//        testNumView = findViewById(R.id.test_num_view);
//        prevTest = findViewById(R.id.prev_test_view);
//        nextTest = findViewById(R.id.next_test_view);
        currentTestIndex = selectedManualTests.indexOf(mCurrentTest);
        currentTestIndex = selectedManualTests.indexOf(mCurrentTest);
        //imageView = (ImageView) findViewById(R.id.imageView);
//        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex + 1, selectedManualTests.size()));
        curr_test_card = findViewById(R.id.curr_test_card_view);
      //  scrollView = (ScrollView) findViewById(R.id.scrollView3) ;

        layoutInflater = LayoutInflater.from(getApplicationContext());
        setPrevTestResultUI();
        nextTestUI();
//        autoScroll(scrollView);
        if (selectedManualTestsResult.size() > 2 && !globalConfig.isIsRetry()) {
            autoScroll(scrollView);
        }
        if (selectedManualTestsResult.size() > 2 && globalConfig.isIsRetry() && selectedManualTests.indexOf(mCurrentTest) - 1 > 1) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, prev_test_view[selectedManualTests.indexOf(mCurrentTest) - 1].getTop());
                }
            });
        }
//        prevResultView = findViewById(R.id.prev_manual_test_result_img);
//        nextManualTest = findViewById(R.id.next_manual_test);
//        if (currentTestIndex > 0) {
//            prevTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(selectedManualTests.get(currentTestIndex - 1)));
//            if (PervacioTest.getInstance().getGlobalConfig().getTestInfoByName(selectedManualTests.get(currentTestIndex - 1)).getTestResult() != null)
//                prevResultView.setImageResource(PervacioTest.getInstance().getGlobalConfig().getTestInfoByName(selectedManualTests.get(currentTestIndex - 1)).getTestResult().equalsIgnoreCase(TestResult.PASS) ? R.drawable.ic_passed : R.drawable.ic_failed);
//        } else {
//            prevTest.setText("");
//            prevResultView.setVisibility(View.INVISIBLE);
//        }
//        if(currentTestIndex < selectedManualTests.size() - 1)
//            nextTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex + 1)));
//        else
//            nextTest.setText("");

//        prevResultView = findViewById(R.id.prev_manual_test_result_img);
//        if (currentTestIndex > 0) {
////            String mTestResult = intent.getStringExtra(TEST_RESULT);
//            String mTestResult = BaseActivity.selectedManualTestsResult.get(0);
//            Log.d("ManualTestProgress", "mTestResult: " + mTestResult + " currentTestIndex: " + currentTestIndex + " selectedManualTests.size(): " + selectedManualTests.size());
//            prevTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(selectedManualTests.get(currentTestIndex - 1)));
//            if (mTestResult != null) {
//                if (mTestResult.equalsIgnoreCase(TestResult.PASS)) {
//                    prevResultView.setImageResource(R.drawable.ic_passed);
//                } else if (mTestResult.equalsIgnoreCase(TestResult.FAIL)) {
//                    prevResultView.setImageResource(R.drawable.ic_failed);
//                } else if (mTestResult.equalsIgnoreCase(TestResult.SKIPPED)) {
//                    prevResultView.setImageResource(R.drawable.ic_skipped);
//                }
//            }
//        } else {
//            prevTest.setText("");
//            prevResultView.setVisibility(View.INVISIBLE);
//        }
//        if (currentTestIndex < selectedManualTests.size() - 1)
//            nextTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex + 1)));
//        else {
//            nextTest.setText("");
//            nextManualTest.setVisibility(View.INVISIBLE);
//        }
        if (mCurrentTest != null) {
            mTestName.setText(getDisplayName(mCurrentTest));
            if(mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            }else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{
                test_tile_description.setText(getResourceID(mCurrentTest, TEST_RESULT_MESAGE));
            }
        }

        /*mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButtonClicked();
            }
        });*/

        if (mCurrentTest != null) {
            mTestName.setText(getDisplayName(mCurrentTest));
            mCurrentManualTest = mCurrentTest;
            if (TestName.AMBIENTTEST.equalsIgnoreCase(mCurrentTest) || TestName.PROXIMITYTEST.equalsIgnoreCase(mCurrentTest) || TestName.FINGERPRINTSENSORTEST.equalsIgnoreCase(mCurrentTest)) {


                if(mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
                {
                    mTestDescription.setText(getResources().getText(R.string.BluetoothToggleTest));
                }
                else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                    mTestDescription.setText(getResources().getText(R.string.WifiToggleTest));
                }
                else{
                    mTestDescription.setText(getResourceID(mCurrentTest, TEST_RESULT_MESAGE));
                }


            } else if (TestName.BLUETOOTHCONNECTIVITYTEST.equalsIgnoreCase(mCurrentTest)
                    || TestName.CAMERAFLASHTEST.equalsIgnoreCase(mCurrentTest)
                    || TestName.FRONTFLASHTEST.equalsIgnoreCase(mCurrentTest) || WIFICONNECTIVITYTEST.equalsIgnoreCase(mCurrentTest)) {

                if(mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
                {
                    mTestDescription.setText(getResources().getText(R.string.BluetoothToggleTest));
                }
                else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                    mTestDescription.setText(getResources().getText(R.string.WifiToggleTest));
                }
                else{
                    mTestDescription.setText(getResourceID(mCurrentTest, TEST_INPROGESS_MESAGE));
                }
            }
            else {
                if(mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
                {
                    mTestDescription.setText(getResources().getText(R.string.BluetoothToggleTest));
                }
                else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                    mTestDescription.setText(getResources().getText(R.string.WifiToggleTest));
                }
                else{
                    mTestDescription.setText(getResourceID(mCurrentTest, TEST_TRY_MESAGE));
                }
            }
            imageView.setImageResource(TestUtil.manualtestImageMap.get(mCurrentTest));
            /*gifViewLayout.removeAllViews();
            gifViewLayout.addView(getGIFMovieView(getApplicationContext(), mCurrentTest));*/
        }

        /*if(Util.isAdvancedTestFlow()) {
            mTestName.setVisibility(View.INVISIBLE);
            //mTestDescription.setVisibility(View.INVISIBLE);
            if(getResourceID(mCurrentTest, TEST_INPROGESS_MESAGE) != 0) {
                mTestDescription.setText(getResourceID(mCurrentTest, TEST_INPROGESS_MESAGE));
            } else {
                mTestDescription.setVisibility(View.INVISIBLE);
            }
            if(isStopBtnRequired()) {
                mStopBtn.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            mStopBtn.setVisibility(View.GONE);
        }*/
        /*setFontToView(mTestDescription,ROBOTO_LIGHT);
        setFontToView(mTestName,ROBOTO_LIGHT);*/
    }


    public void setPrevTestResultUI() {

        final Boolean[] checkList = {false};

        for (int i = 0; i < (selectedManualTestsResult.size()); i++) {

            prev_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            prev_tests_list = findViewById(R.id.prev_test_list_view);
            result_Observation = (TextView) prev_test_view[i].findViewById(R.id.result_test_observation);
            result_Display_Name = (TextView) prev_test_view[i].findViewById(R.id.result_test_name_result);
            result_image_view = (ImageView) prev_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) prev_test_view[i].findViewById(R.id.test_image);
            test_tile_description = (TextView) prev_test_view[i].findViewById(R.id.testOneLineDecription);
            retry_Button=prev_test_view[i].findViewById(R.id.retry_button_manual_test);
            result_text_view=prev_test_view[i].findViewById(R.id.test_result_text);
            String testName = selectedManualTests.get(i) == null ? "" : selectedManualTests.get(i);
            String testResult = selectedManualTestsResult.get(testName) == null ? "" : selectedManualTestsResult.get(testName);

            result_Display_Name.setText(getDisplayName(testName));
            if(testName.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            }
            else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }
            result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
            retry_Button.setVisibility(View.VISIBLE);
            if (testResult.equalsIgnoreCase(TestResult.PASS)) {
                //result_image_view.setImageResource(R.drawable.success_test);
                retry_Button.setText("Pass");
                result_text_view.setVisibility(View.GONE);
                retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
            } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
                retry_Button.setText("Fail");
                result_text_view.setVisibility(View.VISIBLE);
              //  result_image_view.setImageResource(R.drawable.fail_test);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (selectedManualTestsResult.size()); i++) {
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
                        if (!selectedManualTests.get(i).equalsIgnoreCase(mCurrentTest) && !checkList[0]) {
                            prev_tests_list.addView(prev_test_view[i]);
                        }
                        if (selectedManualTests.get(i).equalsIgnoreCase(mCurrentTest)) {
                            checkList[0] = true;
                        }
//                        prev_tests_list.addView(prev_test_view[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }



    void autoScroll(ScrollView scroll){
        Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DLog.d(TAG, "ManualTestProgressBarActivity + autoscroll " + selectedManualTestsResult.size());
                    scroll.setSmoothScrollingEnabled(true);
                    scroll.setNestedScrollingEnabled(false);
//                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
//                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
//                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));scrollView.fullScroll(View.)
                    if(selectedManualTestsResult.size()>2){
                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size()- 2].getTop());
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
            next_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            next_tests_list = findViewById(R.id.next_test_list_view);
            result_Display_Name = (TextView) next_test_view[i].findViewById(R.id.result_test_name_result);
            result_Observation = (TextView) next_test_view[i].findViewById(R.id.result_test_observation);
            result_image_view = (ImageView) next_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) next_test_view[i].findViewById(R.id.test_image);
            test_tile_description = (TextView) next_test_view[i].findViewById(R.id.testOneLineDecription);
            retry_Button = next_test_view[i].findViewById(R.id.retry_button_manual_test);
            result_text_view = next_test_view[i].findViewById(R.id.test_result_text);

            String testName = selectedManualTests.get(i);
            String testResult = selectedManualTestsResult.get(testName);
            result_Display_Name.setText(testName);
            //dont add this it will ruin ui |
            //  result_image_view.setImageResource(R.drawable.ic_not_equipped);
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
            if(testName.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            }
            else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }
            if(testResult != null) {
                result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
                retry_Button.setVisibility(View.VISIBLE);
                if (testResult.equalsIgnoreCase(TestResult.PASS)) {
                    //result_image_view.setImageResource(R.drawable.success_test);
                    retry_Button.setText("Pass");
                    retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
                } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
                    //result_image_view.setImageResource(R.drawable.fail_test);
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
    protected void onStop() {
        super.onStop();
        DLog.d(TAG, "onStop called..");
        try {
            if (alertDialog != null) {
                DLog.d(TAG, "alertDialog dismissed...");
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String getToolBarName();

    protected abstract void stopButtonClicked();

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_test_end;
    }

    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            super.onBackPressed();
        }
    }

    private boolean isStopBtnRequired() {
        String testLsit[] = new String[]{TestName.EARPIECETEST, TestName.SPEAKERTEST, TestName.VIBRATIONTEST};
        return Arrays.asList(testLsit).contains(mCurrentTest);
    }
}
