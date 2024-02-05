package com.oruphones.nativediagnostic.manualtests;


import android.content.Intent;
import android.hardware.SensorPrivacyManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;


/**
 * Created by Pervacio on 22/09/2017.
 */

public class MicroPhoneTestActivity extends BaseActivity {

    private static String TAG = MicroPhoneTestActivity.class.getSimpleName();

    LinearLayout tipsContainer;
    private Intent intent;
    String mCurrentTest = null;
    private Button mButtonRecord;
    private Button mButtonPlay;
    private boolean isPlaying=false;
    private ProgressBar progressBar;
    private boolean accessDenied=false;
    private LinearLayout mGifLayout;
    private LinearLayout mResultBtnLayout;
    private Button mTestPass, mTestFail, mRetest;
    private TextView test_title, testDescrption,testTimer;
    TextView prevTest;
    TextView nextTest;
    TextView testNumView;
    LinearLayout curr_test_card;
    private  Boolean isClicked ;
    private Handler continuousWarning;
    LayoutInflater layoutInflater;

    private CardView timerCardView;
    private TextView timeLeftTextView;
    private Handler ToastHandler = new Handler();
    View next_test_view[] = new View[selectedManualTests.size()];
    View prev_test_view[] = new View[selectedManualTests.size()];
    LinearLayout prev_tests_list, next_tests_list;
    public TextView result_Display_Name, result_Observation;
    public ImageView result_image_view, test_image;
    ImageView imageView;
    private CountDownTimer countDownTimer;
    private int currentTestIndex = -1;
    private boolean isRecording = false;
    private boolean isTimerRunning = false;
    private boolean showOptionsMenu = false;

    private Runnable blinkRunnable = new Runnable() {
        @Override
        public void run() {

            // Check if the "Record" button is not touched
            if (isRecording) {
                toggleVisibility(getResources().getString(R.string.recording));
            }else{

                toggleVisibility(getResources().getString(R.string.record_toast));
            }

            ToastHandler.postDelayed(this, 500); // Set the blinking interval (3 seconds)
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        mCurrentTest = intent.getStringExtra(TEST_NAME);
        DLog.d(TAG,"onCreate mCurrentTest: "+mCurrentTest);
        progressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
        mButtonRecord = (Button)findViewById(R.id.camera_record_btn_new);
        mButtonPlay = (Button)findViewById(R.id.camera_play_btn_new);
        //mGifLayout = (LinearLayout) findViewById(R.id.sensor_pngViewLayout_new);
        mResultBtnLayout = (LinearLayout) findViewById(R.id.result_btn_layout);
        mTestPass = (Button) findViewById(R.id.accept_tv);
        mTestFail = (Button) findViewById(R.id.cancel_tv);
        mRetest = (Button) findViewById(R.id.retest_tv);
        test_title = (TextView) findViewById(R.id.current_test_view);
        testDescrption = (TextView) findViewById(R.id.test_desc_view);
        tipsContainer = findViewById(R.id.microphone_test_tips_for_global_access);
        timerCardView = findViewById(R.id.Timer_view);
        timeLeftTextView = findViewById(R.id.timeLeftTextView);


//        testNumView = findViewById(R.id.test_num_view);
//        prevTest = findViewById(R.id.prev_test_view);
//        nextTest = findViewById(R.id.next_test_view);
        imageView = findViewById(R.id.manual_test_img);
        currentTestIndex = selectedManualTests.indexOf(mCurrentTest);
        progressBar.setVisibility(View.GONE);
        mButtonRecord.setVisibility(View.VISIBLE);
        mButtonPlay.setVisibility(View.GONE);
        imageView.setImageResource(TestUtil.manualtestImageMap.get(mCurrentTest));
        testTimer = findViewById(R.id.timer_microphone);
        isClicked = false;
        continuousWarning = new Handler();
//        //mGifLayout.removeAllViews();
//        //mGifLayout.addView(getGIFMovieView(getApplicationContext(), mCurrentTest));
////        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex+1, selectedManualTests.size()));
//        if(currentTestIndex > 0)
//            prevTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex - 1)));
//        else
//            prevTest.setText("");
//        if(currentTestIndex < selectedManualTests.size() - 1)
//            nextTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex + 1)));
//        else
//            nextTest.setText("");
        mTestFail.setText(getResources().getString(R.string.str_no));
        mTestPass.setText(getResources().getString(R.string.str_yes));
        if(Util.isAdvancedTestFlow()) {
            test_title.setVisibility(View.GONE);
        }
        curr_test_card = findViewById(R.id.curr_test_card_view);
        layoutInflater = LayoutInflater.from(getApplicationContext());
      //  setPrevTestResultUI();
       // nextTestUI();
        startTimer();
        test_title.setText(getDisplayName(mCurrentTest));
        testDescrption.setText(getResourceID(mCurrentTest, TEST_INPROGESS_MESAGE));
     //   customSnackBar(getString(R.string.record_toast));
    // Toast.makeText(getApplicationContext(),getResources().getString(R.string.record_toast),Toast.LENGTH_SHORT).show();
        accessDenied=false;

        SensorPrivacyManager sensorPrivacyManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            sensorPrivacyManager = getApplicationContext().getSystemService(SensorPrivacyManager.class);
            boolean supportsMicrophoneToggle = sensorPrivacyManager
                    .supportsSensorToggle(SensorPrivacyManager.Sensors.MICROPHONE);
            if (supportsMicrophoneToggle)tipsContainer.setVisibility(View.VISIBLE);
            else tipsContainer.setVisibility(View.GONE);
        }
        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording) {
                    stopBlinking();
                    timerCardView.setVisibility(View.INVISIBLE);
                    ManualTest.getInstance(MicroPhoneTestActivity.this).stopAudioCapture();
                } else {
                    if(!accessDenied) {
                        if (permissionStatusCheck(mCurrentTest)) {
                            if(mCurrentTest.equalsIgnoreCase(TestName.MICROPHONETEST)) {
                                DLog.d(TAG,"calling performMicrophoneTest mCurrentTest "+mCurrentTest+" micNumber 1");
                                ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONETEST, handler, "Record",1);
                            }else if(mCurrentTest.equalsIgnoreCase(TestName.MICROPHONE2TEST)){
                                DLog.d(TAG,"calling performMicrophoneTest mCurrentTest "+mCurrentTest+" micNumber 2");
                                ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONE2TEST, handler, "Record",2);
                             }else{
                                DLog.d(TAG,"calling performMicrophoneTest mCurrentTest "+mCurrentTest);
                                Toast.makeText(MicroPhoneTestActivity.this,"Not Valid Mic Test",Toast.LENGTH_LONG).show();
                            }
                            mButtonRecord.setBackground(getResources().getDrawable(R.drawable.button_camera_click));
                            isRecording = true;
                        }
                    }
                }
            }
        });
        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dndModePermissionCheck()) {
                    ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONETEST, handler, "Play");
                }

            }
        });
        mTestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultBtnLayout.setVisibility(View.GONE);
                String result = TestResult.PASS;
                ManualTest.getInstance(MicroPhoneTestActivity.this).stopTest();
                manualTestResultDialog(mCurrentTest, result,false, true, MicroPhoneTestActivity.this);
            }
        });
        mTestFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultBtnLayout.setVisibility(View.GONE);
                String result = TestResult.FAIL;
                ManualTest manualTest = ManualTest.getInstance(MicroPhoneTestActivity.this);
                manualTest.stopTest();
                if(!manualTest.checkIfAlreadyAttempted(mCurrentTest)) {
                    manualTest.setMicReperformed(true);
                    result = TestResult.SHOW_SUGGESTION; //SSD-1335 Show Suggestion on first attempt of fail
                }
                manualTestResultDialog(mCurrentTest, result,false, true, MicroPhoneTestActivity.this);
            }
        });
        mRetest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultBtnLayout.setVisibility(View.GONE);
                String result = TestResult.FAIL;
                ManualTest.getInstance(MicroPhoneTestActivity.this).stopTest();
                ManualTest.getInstance(MicroPhoneTestActivity.this).cleanMicrophoneState();
                restartCurrentTest();
            }
        });
        showOptionsMenu = false;
        invalidateOptionsMenu() ;
        startBlinking();
       // startWarningToast();
        /*playRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = playRecordBtn.getText().toString();
                if (getResources().getString(R.string.play).contains(input)){
                    ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONETEST, handler, getResources().getString(R.string.play));
                    progressBar.setVisibility(View.VISIBLE);
                    playRecordBtn.setVisibility(View.GONE);
                }
                else if (getResources().getString(R.string.record).equalsIgnoreCase(input)) {
                    ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONETEST, handler, getResources().getString(R.string.record));
                }


            }
        });*/
    }

//    private void customSnackBar(String text) {
//        View rootView = findViewById(android.R.id.content);
//
//        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG);
//        View snackbarView = snackbar.getView();
//
//        // Inflate custom Snackbar layout
//        View customSnackbarLayout = getLayoutInflater().inflate(R.layout.custom_snackbar_layout, null);
//
//        // Replace the default Snackbar layout with the custom layout
//        snackbarView.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
//        ((Snackbar.SnackbarLayout) snackbarView).addView(customSnackbarLayout, 0);
//
//        // Set text or perform other customizations
//        TextView snackbarText = customSnackbarLayout.findViewById(R.id.snackbarText);
//        snackbarText.setText(text);
//        // Set a fixed width for the Snackbar
//
//        ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
//        layoutParams.width = (int) getResources().getDimension(R.dimen.custom_snackbar_width);
//        snackbarView.setLayoutParams(layoutParams);
//
//        snackbar.show();
//    }

    private void customSnackBar(String text) {
        View rootView = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        // Set a fixed width for the Snackbar
        ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
        layoutParams.width = (int) getResources().getDimension(R.dimen.custom_snackbar_width);
        snackbarView.setLayoutParams(layoutParams);

        snackbar.show();
    }

    private void startBlinking() {
        ToastHandler.postDelayed(blinkRunnable, 0);
    }

    private void stopBlinking() {
        ToastHandler.removeCallbacks(blinkRunnable);
    }

    private void toggleVisibility(String text) {
        timeLeftTextView.setText(text);
        if (timerCardView.getVisibility() == View.VISIBLE) {
            timerCardView.setVisibility(View.INVISIBLE);
        } else {
            timerCardView.setVisibility(View.VISIBLE);
        }
    }

    void startTimer ()
    {
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                testTimer.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }
            @Override
            public void onFinish() {
                testTimer.setText("00:00");
                isTimerRunning = false;
                isClicked = true;
            }
        }.start();
        isTimerRunning = true;
    }

    private void startWarningToast() {
        continuousWarning.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isClicked) {
                    // Only show the toast if isRecording is false

                    customSnackBar(getString(R.string.record_toast));
                }
                else {
                    if (continuousWarning!=null) {
                        continuousWarning.removeCallbacks(this);
                    }
                }
                // Schedule the next toast
                continuousWarning.postDelayed(this, 3000);
            }
        }, 1000);
    }

    private void stopWarningToast() {
        if (continuousWarning != null) {
            continuousWarning.removeCallbacksAndMessages(null);
        }
    }


    public void setPrevTestResultUI() {

        for (int i = 0; i < (selectedManualTestsResult.size()); i++) {

            prev_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
            prev_tests_list = findViewById(R.id.prev_test_list_view);
            result_Observation = (TextView) prev_test_view[i].findViewById(R.id.result_test_observation);
            result_Display_Name = (TextView) prev_test_view[i].findViewById(R.id.result_test_name_result);
            result_image_view = (ImageView) prev_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) prev_test_view[i].findViewById(R.id.test_image);
            String testName = selectedManualTests.get(i);
            String testResult = selectedManualTestsResult.get(testName);
            result_Display_Name.setText(testName);
            result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
            if (testResult.equalsIgnoreCase(TestResult.PASS)) {
                result_image_view.setImageResource(R.drawable.ic_passed);
            } else if (testResult.equalsIgnoreCase(TestResult.FAIL)) {
              //  result_image_view.setImageResource(R.drawable.ic_failed);
            } else if (testResult.equalsIgnoreCase(TestResult.SKIPPED)) {
                result_image_view.setImageResource(R.drawable.ic_skipped);
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
                        prev_tests_list.addView(prev_test_view[i]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Handler handler = new Handler();
                    try {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
//                                scrollView.setSmoothScrollingEnabled(true);
//                                scrollView.setNestedScrollingEnabled(false);
//                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200);
                            }
                        }, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
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
            result_image_view = (ImageView) next_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) next_test_view[i].findViewById(R.id.test_image);

            String testName = selectedManualTests.get(i);
            result_Display_Name.setText(testName);
          //  result_image_view.setImageResource(R.drawable.ic_not_equipped);
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
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



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            DLog.d(TAG,"RESULT:"+result +"Message:"+msg.what);
            if (result != null&& (TestResult.TIMEOUT.equalsIgnoreCase(result)||TestResult.FAIL.equalsIgnoreCase(result)||TestResult.ACCESSDENIED.equalsIgnoreCase(result))) {
//                manualTestResultDialog(TestName.MICROPHONETEST, result, MicroPhoneTestActivity.this);
                manualTestResultDialog(mCurrentTest, result, MicroPhoneTestActivity.this);
            }
            switch (msg.what) {
                case 6:
                    manualTestResultDialog(mCurrentManualTest, result, MicroPhoneTestActivity.this);
                    break;
                case 0:
                case 8:
                    //launchResultActivity(TestName.MICROPHONETEST);
                    break;
                case ManualTestEvent.AUDIO_RECORDING_DONE:
                    //displaySnackBarMessage(getString(R.string.recording_done));
                    //mTestDescription.setText(getString(R.string.microphone_play_message));
                    // playRecordBtn.setText(getResources().getString(R.string.play));
                    testDescrption.setVisibility(View.VISIBLE);
                    testDescrption.setText(getResourceID(mCurrentTest, TEST_RESULT_MESAGE));
                    //Toast.makeText(getApplicationContext(),getResources().getString(R.string.play_toast),Toast.LENGTH_SHORT).show();
                    mButtonRecord.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    mResultBtnLayout.setVisibility(View.VISIBLE);
                    playRecordedAudio();
                    //mButtonPlay.setVisibility(View.VISIBLE);
                    break;

                case ManualTestEvent.AUDIO_PLAY_STARTED:
                    //progressBar.setVisibility(View.VISIBLE);
                    mButtonRecord.setVisibility(View.GONE);
                    mButtonPlay.setVisibility(View.GONE);
                    isPlaying=true;
                    showOptionsMenu = true;
                    invalidateOptionsMenu() ;
                    break;

                case ManualTestEvent.EVENT_ERROR_AUDIO_SOURCE:
                    Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.audio_source_error), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.EVENT_ERROR_INSUFFICIENT_STORAGE:
                    Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.insufficient_storage), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.EVENT_ERROR_MEDIA_RECORDER_PREPARE:
                    Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.media_recorder_error), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.EVENT_ERROR_MEDIA_RECORDER_START:
                    Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.microphone_not_started), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.EVENT_ERROR_MEDIA_SERVER_DIED:
                    Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.media_server_died), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE:
                    showAcessibilityDialogue();
                    break;


                case ManualTestEvent.AUDIO_RECORDING_TOAST:

                    testDescrption.setVisibility(View.GONE);
                   // Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.recording), Toast.LENGTH_LONG).show();
               //     displaySnackBarMessage(getString(R.string.recording));
                    //mButtonRecord.setVisibility(View.GONE);
                    //mButtonPlay.setVisibility(View.GONE);
                    //progressBar.setVisibility(View.VISIBLE);
                    break;

                case ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ear_jack_unplug_earphones), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    //  Toast.makeText(MicroPhoneTestActivity.this, getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    break;


            }

        }


    };

    private void playRecordedAudio() {
        if(dndModePermissionCheck()) {
            ManualTest.getInstance(MicroPhoneTestActivity.this).performMicrophoneTest(TestName.MICROPHONETEST, handler, "Play");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ManualTest.getInstance(MicroPhoneTestActivity.this).initMicrophoneTest(TestName.MICROPHONETEST, handler);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            if(isPlaying)
                //mButtonPlay.setVisibility(View.VISIBLE);
                playRecordedAudio();
            else {
                isRecording = false;
                mButtonRecord.setBackground(getResources().getDrawable(R.drawable.button_camera_selector));
                //mButtonRecord.setVisibility(View.VISIBLE);
            }
        }
        removeSettingDialog(false);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(!showOptionsMenu) {
            menu.clear();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected String getToolBarName() {
        fetchIntent();
        if(mCurrentTest.equalsIgnoreCase(TestName.MICROPHONETEST))
        {
            return getDisplayName(TestName.MICROPHONETEST);
        }
        else{
            return getDisplayName(TestName.MICROPHONE2TEST);
        }
    }

    private void fetchIntent(){
        if(getIntent()!=null){
            mCurrentTest = getIntent().getStringExtra(TEST_NAME);
        }
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_record_play;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBlinking();
        //stopWarningToast();
        removeSettingDialog(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAccessDenied = false;
        for(int i=0; i<grantResults.length;i++){
            DLog.d(TAG,"grantResults+i+:"+grantResults[i]);
            if(grantResults[i] != 0 ){
                isAccessDenied = true;
                break;
            }
        }
        if(isAccessDenied){
            accessDenied=true;
            Message msg = new Message();
            msg.what= 26;
            Bundle bundle = new Bundle();
            bundle.putString("result", TestResult.ACCESSDENIED);
            msg.setData(bundle);
            if (handler != null)
                handler.sendMessage(msg);
        }
    }
}
