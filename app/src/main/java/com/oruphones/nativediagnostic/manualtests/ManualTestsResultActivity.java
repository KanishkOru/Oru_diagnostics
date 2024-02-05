package com.oruphones.nativediagnostic.manualtests;



import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTH_TOGGLE;
import static com.oruphones.nativediagnostic.models.tests.TestName.EARPHONETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.NFCTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.WIFICONNECTIVITYTEST;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.common.CustomButton;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.result.ResultsActivity;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;


/**
 * Created by Pervacio on 29/08/2017.
 */


public class ManualTestsResultActivity extends BaseActivity {
    Button mTestPass, mTestFail;
    String mCurrentTest = null;
    String mTestResult = null;
    private static String TAG = ManualTestsResultActivity.class.getSimpleName();
    TextView prevTest;
    TextView nextTest;
    TextView testNumView, nextManualTest;
    TextView testResultView;
    ImageView resultImage;
    private int currentTestIndex = -1;
    TextView mTestName, mTestDescription, test_tile_description, retry_Button, result_text_view;
    ImageView prevResultView, imageView;
    private Intent getIntent;
    LinearLayout numSlectionLayout, numSlectionLayout2, numSlectionLayout3;
    //    NumberSelectView firstNumberSelector;
//    NumberSelectView secondNumberSelector;
//    NumberSelectView thirdNumberSlector;
    CustomButton one, two, three, four, five, six, seven, eight;
    private EditText currentEdt;
    private LinearLayout resultViewLayout, manualTestComplete;
    Button continueBtn;

    LinearLayout prev_tests_list, next_tests_list;
    LinearLayout curr_test_card;
    LayoutInflater layoutInflater;
    public TextView result_Display_Name, result_Observation;
    public ImageView result_image_view, test_image;
    View prev_test_view[] = new View[selectedManualTests.size()];
    View next_test_view[] = new View[selectedManualTests.size()];
    public static ArrayMap<Integer, String> excludeNumbers = new ArrayMap<>();

    ScrollView scrollView2;
    ScrollView scrollView_prev;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DLog.d(TAG,"isRestry [ManualTestResult] : "+globalConfig.isIsRetry());
        DLog.d(TAG, "ManualTestResultActivity " + selectedManualTestsResult.size());


        getIntent = getIntent();
        mCurrentTest = getIntent().getStringExtra(TEST_NAME);
        mTestResult = getIntent.getStringExtra(TEST_RESULT);
        curr_test_card = findViewById(R.id.curr_test_card_view);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
       // scrollView_prev = (ScrollView) findViewById(R.id.scrollView_prev);

        if (mCurrentTest.equalsIgnoreCase("EndTest")) {
            continueBtn = (Button) findViewById(R.id.continueBtn);
            manualTestComplete = findViewById(R.id.manualTestComplete);
            manualTestComplete.setVisibility(View.VISIBLE);
            curr_test_card.setVisibility(View.GONE);
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ResultsActivity.start(ManualTestsResultActivity.this);
                    finish();
                }
            });
        }
        mTestPass = (Button) findViewById(R.id.accept_tv);
        mTestFail = (Button) findViewById(R.id.cancel_tv);
        mTestName = (TextView) findViewById(R.id.current_test_view);
        mTestDescription = (TextView) findViewById(R.id.test_desc_view);
        test_tile_description = (TextView) findViewById(R.id.testOneLineDecription);
        retry_Button = findViewById(R.id.retry_button_manual_test);
        /*mTestName = (TextView) findViewById(R.id.test_name);
        mTestDescription = (TextView) findViewById(R.id.test_description);*/
//        numSlectionLayout = findViewById(R.id.slect_num_views_layout);
//        numSlectionLayout2 = findViewById(R.id.slect_num_views_layout2);
//        numSlectionLayout3 = findViewById(R.id.slect_num_views_layout3);
////        firstNumberSelector = findViewById(R.id.first_slect_num);
////        secondNumberSelector = findViewById(R.id.second_slect_num);
////        thirdNumberSlector = findViewById(R.id.third_slect_num);
//        one = findViewById(R.id.one);
//        two = findViewById(R.id.two);
//        three = findViewById(R.id.three);
//        four = findViewById(R.id.four);
//        five = findViewById(R.id.five);
//        six = findViewById(R.id.six);
//        seven = findViewById(R.id.seven);
//        eight = findViewById(R.id.eight);
//        firstNumberSelector.addTextChangedListener(textWatcher);
//        secondNumberSelector.addTextChangedListener(textWatcher);
//        thirdNumberSlector.addTextChangedListener(textWatcher);
        imageView = (ImageView) findViewById(R.id.manual_test_img);
        if (!mCurrentTest.equalsIgnoreCase("EndTest")) {
            imageView.setImageResource(TestUtil.manualtestImageMap.get(mCurrentTest));
            //imageView = new ImageView(ManualTestsResultActivity.this);
            //gifViewLayout = (LinearLayout) findViewById(R.id.manualResultGiffViewLayout);
//        testNumView = findViewById(R.id.test_num_view);
//        prevTest = findViewById(R.id.prev_test_view);
//        nextTest = findViewById(R.id.next_test_view);
            currentTestIndex = selectedManualTests.indexOf(mCurrentTest);
        }
//        nextManualTest = findViewById(R.id.next_manual_test);
        //imageView = (ImageView) findViewById(R.id.imageView);
//        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex + 1, selectedManualTests.size()));

        layoutInflater = LayoutInflater.from(getApplicationContext());
        setPrevTestResultUI();
        nextTestUI();
//        autoScroll(scrollView);

//        if(currentTestIndex > 0)
//            prevTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex - 1)));
//        else
//            prevTest.setText("");
//        if(currentTestIndex < selectedManualTests.size() - 1)
//            nextTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex + 1)));
//        else
//            nextTest.setText("");

        if (mCurrentTest != null && !mCurrentTest.equalsIgnoreCase("EndTest")) {
            mTestName.setText(getDisplayName(mCurrentTest));
            if(mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                mTestDescription.setText(getResources().getText(R.string.BluetoothToggleTest));

            } else if (mCurrentTest.equalsIgnoreCase(NFCTEST)) {
                mTestDescription.setText(getResources().getText(R.string.nfc_test_tile_desc));
            } 
            else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST))
            {
                mTestDescription.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{

                mTestDescription.setText(getResourceID(mCurrentTest, TEST_RESULT_MESAGE));
            }

        }


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


        if (mTestResult != null && !mTestResult.equalsIgnoreCase("0")) {
//            setResultView();
            DLog.d(TAG, "ManualTestResultActivity # 1" + selectedManualTestsResult.size());
            startNextManualTest();
            DLog.d(TAG, "ManualTestResultActivity # 2" + selectedManualTestsResult.size());
            finish();
//            isRetry=false;
            DLog.d(TAG, "ManualTestResultActivity # 3" + selectedManualTestsResult.size());
        } else {
            DLog.d(TAG, "ManualTestResultActivity # 4" + selectedManualTestsResult.size());
            setResultPromptView();
            if(selectedManualTestsResult.size()>2 && !globalConfig.isIsRetry()){autoScroll(scrollView2);}
            if(selectedManualTestsResult.size()>2 && globalConfig.isIsRetry() && selectedManualTests.indexOf(mCurrentTest)-1 >1){
                scrollView2.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView2.scrollTo(0,prev_test_view[selectedManualTests.indexOf(mCurrentTest)-1].getTop());
                    }
                });
            }
        }
        excludeNumbers.clear();
        if (!mCurrentTest.equalsIgnoreCase("EndTest")) {
            checkNumberForSpeaker(one);
            checkNumberForSpeaker(two);
            checkNumberForSpeaker(three);
            checkNumberForSpeaker(four);
            checkNumberForSpeaker(five);
            checkNumberForSpeaker(six);
            checkNumberForSpeaker(seven);
            checkNumberForSpeaker(eight);
        }
    }


    public void checkNumberForSpeaker(View view) {
//    Button spButtons = new androidx.appcompat.widget.AppCompatButton() {
//        @Override
//        public void onClick(View v) {
//
//        }


//         else {
        //        view will be a button and check if the button text is contains to the testNumString or not
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String testNumString = "";
                        if (mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
                            testNumString = GlobalConfig.getInstance().getFlashTestNumString();
                        } else if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) || mCurrentTest.equalsIgnoreCase(EARPHONETEST)) {
                            testNumString = GlobalConfig.getInstance().getAudioTestNumString();
                        } else if (mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)) {
                            testNumString = GlobalConfig.getInstance().getVibrationTestNumString();
                        }
                        DLog.d(TAG, "Test NumString:  " + testNumString);
                        String finalTestNumString = testNumString;


                        final String[] result = {TestResult.FAIL};


                        Button button = (Button) v;
                        String buttonText = button.getText().toString();
                        try{
                            if (!buttonText.isEmpty()){
                                if (mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
                                    globalConfig.addItemToList("Flash Test User Input : "+buttonText);
                                } else if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) ){


                                    globalConfig.saveIntegerForTest("Speaker User Input" ,Integer.valueOf(buttonText));


                                }else if( mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) ){

                                    globalConfig.saveIntegerForTest("Earpiece User Input" ,Integer.valueOf(buttonText));

//                            }

                                }else if(mCurrentTest.equalsIgnoreCase(EARPHONETEST)) {

                                    globalConfig.saveIntegerForTest("Earphone User Input" ,Integer.valueOf(buttonText));



                                } else if (mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)) {
                                    globalConfig.addItemToList("Vibration User Input : "+buttonText);

                                }
                            }else{
                                globalConfig.addItemToList(mCurrentTest + "User Input : NULL");
                            }
                        }
                       catch (Exception e){
                            DLog.e(TAG,e);
                       }





                        DLog.d(TAG,  "Button Input: "+buttonText);


                        DLog.d(TAG, "buttonText: " + buttonText);
                        if (finalTestNumString.contains(buttonText)) { //&& !excludeNumbers.containsValue(buttonText)
                            int indx = excludeNumbers.size();
                            DLog.d(TAG, "indx: " + indx + " excludeNumbers: " + excludeNumbers);
                            excludeNumbers.put(indx, buttonText);
//                            check the sequence of the numbers
                            DLog.d(TAG, "excludeNumbers in if: " + finalTestNumString.charAt(indx) + ' ' + excludeNumbers.get(indx).charAt(0));
                            if (finalTestNumString.charAt(indx) != excludeNumbers.get(indx).charAt(0)) {
                                result[0] = TestResult.FAIL;
                                manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsResultActivity.this);
                            }
                            DLog.d(TAG, "buttonText: " + buttonText + " is in testNumString: " + finalTestNumString);
                            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.touch_color)));
                            button.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            DLog.d(TAG, "buttonText: " + buttonText + " is not in testNumString: " + finalTestNumString);
                            button.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            button.setTextColor(getResources().getColor(R.color.white));
                            manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsResultActivity.this);
                        }


                        DLog.d(TAG, "excludeNumbers: " + excludeNumbers.toString());
//                        if (excludeNumbers.size() == 3) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < excludeNumbers.size(); i++) {
                            stringBuilder.append(excludeNumbers.get(i));
                        }
                        if (finalTestNumString.equalsIgnoreCase(stringBuilder.toString())) {
                            DLog.d(TAG, "excludeNumbers into if: " + excludeNumbers);
                            String selectedNum = stringBuilder.toString();
//                            if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST)) {
//                                selectedNum = (excludeNumbers.get(0) + excludeNumbers.get(1) + excludeNumbers.get(2));
//                            }
                            if (selectedNum.equalsIgnoreCase(finalTestNumString)) {
                                result[0] = TestResult.PASS;
                            } else {
                                if (ODDUtils.suggestionTestMap.containsKey(mCurrentTest)) {
                                    if (!ODDUtils.suggestionTestMap.get(mCurrentTest)) {
                                        ODDUtils.suggestionTestMap.put(mCurrentTest, true);
                                        result[0] = TestResult.SHOW_SUGGESTION;
                                    } else {
                                        result[0] = TestResult.FAIL;
                                    }
                                } else {
                                    result[0] = TestResult.FAIL;
                                }
                            }
                            excludeNumbers.clear();
                            DLog.d(TAG, "selectedNum: " + result[0] + mCurrentTest);
                            manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsResultActivity.this);
                        }
                    }
                }
        );
//        }


    }


//    TextWatcher textWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            currentEdt = (EditText) getCurrentFocus();
//            TextView text = (TextView) getCurrentFocus();
//            Log.d(TAG, "onTextChanged: " + s.toString());
//
//            if (text != null && text.length() > 0) {
//                View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
//                if (next != null) {
//                    next.requestFocus();
//                } else {
//                    mTestPass.setVisibility(View.GONE);
//                    mTestFail.setVisibility(View.GONE);
//                    String result = TestResult.FAIL;
//                    try {
//                        if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) || mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)
//                                || mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
//                            StringBuilder stringBuilder = new StringBuilder();
//                            String testNumString = "";
//                            if (mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)) {
//                                stringBuilder.append(secondNumberSelector.getSelectedNumber());
//                                testNumString = GlobalConfig.getInstance().getVibrationTestNumString();
//                                Log.d(TAG, "Vibration Test NumString:  " + testNumString);
//                            } else if (mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
//                                stringBuilder.append(secondNumberSelector.getSelectedNumber());
//                                testNumString = GlobalConfig.getInstance().getFlashTestNumString();
//
//                                Log.d(TAG, "Flash Test NumString:  " + testNumString);
//                            } else {
//
////                                stringBuilder.append(firstNumberSelector.getSelectedNumber());
////                                stringBuilder.append(secondNumberSelector.getSelectedNumber());
////                                stringBuilder.append(thirdNumberSlector.getSelectedNumber());
////                                testNumString = GlobalConfig.getInstance().getAudioTestNumString();
////                                Log.d(TAG, "Audio Test NumString:  " + testNumString);
//                            }
//                            if (!mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST)) {
//                                String selectedNum = stringBuilder.toString();
//                                if (selectedNum.equalsIgnoreCase(testNumString)) {
//                                    result = TestResult.PASS;
//                                } else {
//                                    if (ODDUtils.suggestionTestMap.containsKey(mCurrentTest)) {
//                                        if (!ODDUtils.suggestionTestMap.get(mCurrentTest)) {
//                                            ODDUtils.suggestionTestMap.put(mCurrentTest, true);
//                                            result = TestResult.SHOW_SUGGESTION;
//                                        } else {
//                                            result = TestResult.FAIL;
//                                        }
//                                    } else {
//                                        result = TestResult.FAIL;
//                                    }
//                                }
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.d(TAG, "Exception:  " + Log.getStackTraceString(e));
//                        result = TestResult.FAIL;
//                    }
//                    manualTestResultDialog(mCurrentTest, result, false, ManualTestsResultActivity.this);
//                }
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    };


    void autoScroll(ScrollView scroll){
        try {
            scroll.post(new Runnable() {
                @Override
                public void run() {
                    DLog.d(TAG, "ManualTestTryActivity + autoScroll " + selectedManualTestsResult.size());
                    scroll.setSmoothScrollingEnabled(true);
                    scroll.setNestedScrollingEnabled(false);
//                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
//                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
//                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));scrollView.fullScroll(View.)
                    if(mCurrentTest.equalsIgnoreCase(selectedManualTests.get(selectedManualTests.size() - 1))) {
                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size() - 2].getBottom());
                    }
                    else {
                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size() - 2].getTop());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//        Handler handler = new Handler();
//        try {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("#00 :", "ManualTestResultActivity + autoScroll" + selectedManualTestsResult.size());
//                    scroll.setSmoothScrollingEnabled(true);
//                    scroll.setNestedScrollingEnabled(false);
////                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
////                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
////                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));scrollView.fullScroll(View.)
//                    if (selectedManualTestsResult.size() > 2) {
//                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size() - 2].getTop());
//                    }
//                }
//            }, 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public void setPrevTestResultUI() {

        int f = selectedManualTestsResult.size();
        DLog.d(TAG,"totalCountPrev01="+f);
        if(globalConfig.isIsRetry()){
            f = selectedManualTests.indexOf(globalConfig.getRetryTestName());
        }
        DLog.d(TAG,"totalCountPrev01="+f);


        for (int i = 0; i < f; i++) {
            prev_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            prev_tests_list = findViewById(R.id.prev_test_list_view);
            result_Observation = (TextView) prev_test_view[i].findViewById(R.id.result_test_observation);
            result_Display_Name = (TextView) prev_test_view[i].findViewById(R.id.result_test_name_result);
            result_image_view = (ImageView) prev_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) prev_test_view[i].findViewById(R.id.test_image);
            test_tile_description = (TextView) prev_test_view[i].findViewById(R.id.testOneLineDecription);
            retry_Button = prev_test_view[i].findViewById(R.id.retry_button_manual_test);
            result_text_view = prev_test_view[i].findViewById(R.id.test_result_text);
            //test_tile_description=(TextView) prev_test_view[i].findViewById(R.id.test_tile_description);
            // testViewTile=(RelativeLayout) ;

            String testName = selectedManualTests.get(i);
            String testResult = selectedManualTestsResult.get(testName);

            if(testName.equalsIgnoreCase(EARPHONETEST) && testResult.equalsIgnoreCase(TestResult.PASS)) {
                globalConfig.setEarPhoneTestResult(true);
            }


            result_Display_Name.setText(getDisplayName(testName));
            result_Observation.setText(CommonUtil.getMappedTestResult(testResult));

            if(testName.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            } else if (testName.equalsIgnoreCase(NFCTEST)) {
                mTestDescription.setText(getResources().getText(R.string.nfc_test_tile_desc));
            }
            else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST))
            {
                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }

            if (testResult == null) {
                testResult = TestResult.FAIL;
            }
            /** change result image according to result we get */
            retry_Button.setVisibility(View.VISIBLE);
            if (testResult.equalsIgnoreCase(TestResult.PASS)) {
                result_image_view.setImageResource(R.drawable.success_test);
                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
                retry_Button.setText("Pass");
                result_text_view.setVisibility(View.GONE);
                retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
             //   result_image_view.setImageResource(R.drawable.fail_test);
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

        int finalF= f;

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
                        prev_tests_list.addView(prev_test_view[i]);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String testName = selectedManualTests.get(i);
//                    prev_test_view[i].setOnClickListener(new View.OnClickListener() {
//
//                        String testResult =  selectedManualTestsResult.get(testName);;
//                        @Override
//                        public void onClick(View v) {
//                            if(testResult.equalsIgnoreCase(TestResult.PASS)||testResult.equalsIgnoreCase(TestResult.OPTIMIZED))
//                            {
//                                LogUtil.printLog("TEST RESULT","test : +"+mCurrentTest+"result :"+testResult);
//                            }else {
//                                showRetryDialog(testName);
//                            }
//                        }
//                    });

//                    Handler handler = new Handler();
//                    try {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView3);
//                                scrollView.setSmoothScrollingEnabled(true);
//                                scrollView.setNestedScrollingEnabled(false);
////                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200);
////                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));
//
//                                if(selectedManualTestsResult.size()>2){
//                                    scrollView.smoothScrollTo(0,prev_test_view[selectedManualTestsResult.size()-2].getTop());
//                                }
//                            }
//                        }, 0);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }


//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < (selectedManualTestsResult.size()); i++) {
////                    try {
//                    if (prev_test_view[i].getParent() != null) {
//                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
//                    }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                    }
////                    prev_tests_list.removeView(prev_test_view);
//                    try {
//                        prev_tests_list.addView(prev_test_view[i]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Handler handler = new Handler();
//                    try {
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
//                                scrollView.setSmoothScrollingEnabled(true);
//                                scrollView.setNestedScrollingEnabled(false);
////                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200);
//                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));
//                            }
//                        }, 0);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }


    void showRetryDialog(String testName) {
        globalConfig.setRetryTestName(testName);
        if(!globalConfig.isIsRetry()) globalConfig.setLastCurrentTest(mCurrentTest);
        globalConfig.setIsRetry(true);
        ManualTestsTryActivity.startActivity(ManualTestsResultActivity.this, mCurrentTest, testName);
    }
//
//

    public void nextTestUI() {


//        Toast.makeText(getApplicationContext(),"mTest"+selectedManualTests.size(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),"RTest"+selectedManualTestsResult.size(),Toast.LENGTH_SHORT).show(); // sidd

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
            result_Display_Name.setText(getDisplayName(testName));
            //dont add this it will ruin ui |
            //  result_image_view.setImageResource(R.drawable.ic_not_equipped);
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
            if(testName.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
            }else if (testName.equalsIgnoreCase(NFCTEST)) {
                mTestDescription.setText(getResources().getText(R.string.nfc_test_tile_desc));
            }
            else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST))
            {
                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
            }
            else{
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }
            if(testResult != null) {
                result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
                retry_Button.setVisibility(View.VISIBLE);
                if (testResult.equalsIgnoreCase(TestResult.PASS)) {
                    result_image_view.setImageResource(R.drawable.success_test);
                    retry_Button.setText("Pass");
                    result_text_view.setVisibility(View.GONE);
                    retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
                } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
              //      result_image_view.setImageResource(R.drawable.fail_test);
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
                    if (next_test_view[i] != null) {
                        if (next_test_view[i].getParent() != null) {
                            ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
                        }
                    }
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


    private void setResultPromptView() {
        mTestFail.setText(getResources().getString(R.string.str_no));
        mTestPass.setText(getResources().getString(R.string.str_yes));
        /*gifViewLayout.removeAllViews();
        gifViewLayout.addView(getGIFMovieView(getApplicationContext(), mCurrentTest));*/


        //imageView.setImageResource(getResourceID(mCurrentTest, TEST_RESULT_IMAGE));


        if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) || mCurrentTest.equalsIgnoreCase(EARPHONETEST)) {
            numSlectionLayout2.setVisibility(View.VISIBLE);
            numSlectionLayout3.setVisibility(View.VISIBLE);
//            firstNumberSelector.reuestForFocus();
            mTestFail.setText(R.string.fail_btn);
            mTestPass.setText(R.string.btn_retry);
        } else if (mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST) || mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
//            numSlectionLayout.setVisibility(View.VISIBLE);
//            firstNumberSelector.setVisibility(View.GONE);
//            thirdNumberSlector.setVisibility(View.GONE);
            numSlectionLayout2.setVisibility(View.VISIBLE);
            mTestFail.setText(R.string.fail_btn);
            mTestPass.setText(R.string.btn_retry);
        } else {
            numSlectionLayout2.setVisibility(View.GONE);
            numSlectionLayout3.setVisibility(View.GONE);
        }
        mTestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(EARPHONETEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) || mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)
                        || mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
                    startManualTest(mCurrentTest);
                    finish();
                } else {
                    mTestPass.setVisibility(View.GONE);
                    mTestFail.setVisibility(View.GONE);
                    String result = TestResult.PASS;
                    if (mCurrentTest.equalsIgnoreCase(TestName.DEADPIXELTEST) ||
                            mCurrentTest.equalsIgnoreCase(TestName.DISCOLORATIONTEST) || mCurrentTest.equalsIgnoreCase(TestName.SCREENBURNTEST))
                        result = TestResult.FAIL;
                    if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST) || mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)
                            || mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
                        String testNumString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST)) {
//                            stringBuilder.append(firstNumberSelector.getSelectedNumber());
//                            stringBuilder.append(secondNumberSelector.getSelectedNumber());
//                            stringBuilder.append(thirdNumberSlector.getSelectedNumber());
                            for (int i = 0; i < excludeNumbers.size(); i++) {
                                stringBuilder.append(excludeNumbers.get(i));
                            }
                            testNumString = GlobalConfig.getInstance().getAudioTestNumString();
                        } else if (mCurrentTest.equalsIgnoreCase(TestName.VIBRATIONTEST)) {
//                            stringBuilder.append(secondNumberSelector.getSelectedNumber());
                            for (int i = 0; i < excludeNumbers.size(); i++) {
                                stringBuilder.append(excludeNumbers.get(i));
                            }
                            testNumString = GlobalConfig.getInstance().getVibrationTestNumString(); //VibrationTest
                        } else if (mCurrentTest.equalsIgnoreCase(TestName.CAMERAFLASHTEST)) {
//                            Log.d("SatyaTest", "secondNumberSelector.getSelectedNumber() " + secondNumberSelector.getSelectedNumber());
//                            stringBuilder.append(secondNumberSelector.getSelectedNumber());
                            for (int i = 0; i < excludeNumbers.size(); i++) {
                                stringBuilder.append(excludeNumbers.get(i));
                            }
                            testNumString = GlobalConfig.getInstance().getFlashTestNumString(); //FlashTest
                        }
                        String selectedNum = stringBuilder.toString();
                        if (selectedNum.equalsIgnoreCase(testNumString)) {
                            result = TestResult.PASS;
                        } else {
                            if (ODDUtils.suggestionTestMap.containsKey(mCurrentTest)) {
                                if (!ODDUtils.suggestionTestMap.get(mCurrentTest)) {
                                    ODDUtils.suggestionTestMap.put(mCurrentTest, true);
                                    result = TestResult.SHOW_SUGGESTION;
                                } else {
                                    result = TestResult.FAIL;
                                }
                            } else {
                                result = TestResult.FAIL;
                            }
                        }
                    }
                    manualTestResultDialog(mCurrentTest, result, false, ManualTestsResultActivity.this);
//                    isRetry=false;
                }
            }
        });
        mTestFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestPass.setVisibility(View.GONE);
                mTestFail.setVisibility(View.GONE);
                String result = TestResult.FAIL;
                if (mCurrentTest.equalsIgnoreCase(TestName.DEADPIXELTEST) ||
                        mCurrentTest.equalsIgnoreCase(TestName.DISCOLORATIONTEST) || mCurrentTest.equalsIgnoreCase(TestName.SCREENBURNTEST))
                    result = TestResult.PASS;
                if (ODDUtils.suggestionTestMap.containsKey(mCurrentTest)) {
                    if (!ODDUtils.suggestionTestMap.get(mCurrentTest)) {
                        ODDUtils.suggestionTestMap.put(mCurrentTest, true);
                        result = TestResult.SHOW_SUGGESTION;
                    }
                }
                manualTestResultDialog(mCurrentTest, result, false, ManualTestsResultActivity.this);
//                isRetry=false;
            }
        });
        setFontToView(mTestFail, OPENSANS_MEDIUM);
        setFontToView(mTestPass, OPENSANS_MEDIUM);
        /*setFontToView(mTestDescription,ROBOTO_LIGHT);
        setFontToView(mTestName,ROBOTO_LIGHT);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setResultView() {
        resultViewLayout = findViewById(R.id.manual_test_result_ll);
        testResultView = findViewById(R.id.result_view);
        resultImage = findViewById(R.id.manual_test_result_img);
        resultViewLayout.setVisibility(View.VISIBLE);
        mTestPass.setText(R.string.str_next);
        mTestFail.setVisibility(View.GONE);
        mTestDescription.setVisibility(View.GONE);
        if (TestResult.PASS.equalsIgnoreCase(mTestResult)) {
            testResultView.setText(R.string.working);
            resultImage.setImageResource(R.drawable.success_test);
        } else {
            testResultView.setText(R.string.not_working);
        //    resultImage.setImageResource(R.drawable.fail_test);
        }
        mTestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextManualTest();
                finish();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DLog.d(TAG, "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DEL && currentEdt != null) {
            if (currentEdt.getText().length() == 1) {
                currentEdt.requestFocus();
                currentEdt.setText("");
            } else {
                currentEdt = (EditText) currentEdt.focusSearch(View.FOCUS_LEFT);
                if (currentEdt != null) {
                    currentEdt.requestFocus();
                    currentEdt.setText("");
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        DLog.d(TAG, "onStop called...");
        try {
            if (alertDialog != null) {
                DLog.d(TAG, "alertDialog dismissed...");
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getToolBarName() {
        return testDisplayName();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dummy;
    }


    @Override
    protected boolean setBackButton() {
        return !Util.needToRemoveBackButton();
    }


    private String testDisplayName() {
        if (getIntent == null)
            getIntent = getIntent();
        String testName = getIntent.getStringExtra(TEST_NAME);
        return getDisplayName(testName);
    }


    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            super.onBackPressed();
        }
    }
}
