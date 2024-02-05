package com.oruphones.nativediagnostic.manualtests;



import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTH_TOGGLE;
import static com.oruphones.nativediagnostic.models.tests.TestName.NFCTEST;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.Util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public  abstract class MiddleActivity extends BaseActivity {

    Intent getIntent;
    LinearLayout mGIFMovieViewContainer;
    public  String curTest;
    int c=30;
    public Button mCancel ;
    private CountDownTimer countDownTimer;
    private static String TAG = MiddleActivity.class.getSimpleName();
    private long timeRemaining;

    protected boolean isFingerPrintActivity;


    /** that need to be done
     * want to extend baseActivity and remove AppCompatActivity
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent = getIntent();

        TextView txtTimer = findViewById(R.id.txtTimer);
        TextView mTestName = findViewById(R.id.txtTestName);
        TextView mTestDescription = findViewById(R.id.txtTestDec);
        mCancel = findViewById(R.id.btn_mCancel);



        if (savedInstanceState != null) {
            timeRemaining = savedInstanceState.getLong("timeRemaining", 0);
        }
        curTest = getIntent.getStringExtra(TEST_NAME);

        DLog.d(TAG,curTest);
        if (curTest != null) {
            mTestName.setText(getDisplayName(curTest));

            int resourceID = getResourceID(curTest, TEST_TRY_MESAGE, Util.disableSkip());
            if (resourceID == 0) {
                resourceID = getResourceID(curTest, TEST_TRY_MESAGE);
            }
            if(curTest.equalsIgnoreCase(BLUETOOTH_TOGGLE))
            {
                mTestDescription.setText("BLUETOOTH TEST");
            }else if (curTest.equalsIgnoreCase(NFCTEST)) {
                mTestDescription.setText(getResources().getText(R.string.nfc_test_desc));
            }
            else{
                mTestDescription.setText(resourceID);
            }
        }




        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextAppearance(this, R.style.textStyle_title);
        //  mToolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.manual_tests);
        mToolbar.setBackgroundResource(R.drawable.toolbar_bg_white);
       // ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setTextColor(getResources().getColor(R.color.white));
        setFontToView(((TextView) mToolbar.findViewById(R.id.toolbar_title)), SSF_MEDIUM);


        int icon_id = R.drawable.ic_back;
        Drawable icon = ContextCompat.getDrawable(context, icon_id);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.b_black), PorterDuff.Mode.SRC_ATOP);


        if (icon != null) {
            actionBar.setTitle("");
            actionBar.setHomeAsUpIndicator(icon);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualTestResultDialog(curTest, TestResult.FAIL,MiddleActivity.this);
            }
        });




        mGIFMovieViewContainer = findViewById(R.id.animatedGIFll);

//        Toast.makeText(getApplicationContext(),"fiel"+curTest,Toast.LENGTH_SHORT).show();

        AnimatedGifUtils.addToView(mGIFMovieViewContainer,getApplicationContext(),curTest);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualTestResultDialog(curTest, TestResult.FAIL,MiddleActivity.this);
                // add code for nextManualTest()

            }
        });



//        startManualTest(curTest);


        if(curTest.equalsIgnoreCase(TestName.EARPHONEJACKTEST)){
            c=c/2;
        }

//        new CountDownTimer(c*1000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                // Used for formatting digit to be in 2 digits only
//                NumberFormat f = new DecimalFormat("00");
//                long hour = (millisUntilFinished / 3600000) % 24;
//                long min = (millisUntilFinished / 60000) % 60;
//                long sec = (millisUntilFinished / 1000) % 60;
//                txtTimer.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
//            }
//            // When the task is over it will print 00:00:00 there
//            public void onFinish() {
//                txtTimer.setText("00:00:00");
//            }
//        }.start();
    }

//    private void startOrResumeTimer() {
//        if (timeRemaining > 0) {
//            // Resume the timer with the remaining time
//            startTimer(timeRemaining);
//        } else {
//            // Start a new timer
//            startTimer(c * 1000);
//        }
//    }



    protected void startTimer(long duration) {


        DLog.d(TAG,curTest);

        TextView txtTimer = findViewById(R.id.txtTimer);
        countDownTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                timeRemaining = millisUntilFinished;

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                txtTimer.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {

                if (isFingerPrintActivity){
                    txtTimer.setText("00:00:00");

                    Handler handle = new Handler();


                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            manualTestResultDialog(curTest, TestResult.FAIL,MiddleActivity.this);
                        }
                    },1000);

                }else{
                    if (curTest.equalsIgnoreCase(NFCTEST)){
                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                manualTestResultDialog(curTest, TestResult.TIMEOUT,MiddleActivity.this);
                            }
                        },1000);
                    }
                    txtTimer.setText("00:00:00");
                }

            }
        }.start();
    }

//    private void pauseTimer() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
//    }

   protected void stopCountdownTimer(){
       if (countDownTimer != null) {
           countDownTimer.cancel();
       }
   }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the timer when the activity comes back to the foreground
        //   startOrResumeTimer();
        startTimer(c * 1000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        //  pauseTimer();
    }

    //    private Handler handler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String result =   msg.getData().getString("result");
////            manualTestResultDialog(TestName.PROXIMITYTEST, result, MiddleActivity.this);
//            if(result.equalsIgnoreCase(TestResult.PASS)){
//
//               AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.PASS);
//               Handler handle = new Handler();
//               handle.postDelayed(new Runnable() {
//                   @Override
//                   public void run() {
//                       selectedManualTestsResult.put(curTest, TestResult.PASS);
//                       updateTestResult(curTest, TestResult.PASS);
//                       startNextManualTest();
//                   }
//               },2000);
//            }
//            else if(result.equalsIgnoreCase(TestResult.FAIL)||result.equalsIgnoreCase(TestResult.TIMEOUT))
//            {
//                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.FAIL);
//                Handler handle = new Handler();
//                handle.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        selectedManualTestsResult.put(curTest, TestResult.FAIL);
//                        updateTestResult(curTest, TestResult.FAIL);
//                        startNextManualTest();
//                    }
//                },2000);
//
//            }
//        }
//    };

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//            ManualTest.getInstance(MiddleActivity.this).performTest(curTest, handler);
//    }

    @Override
    protected String getToolBarName() {
        return "";
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_middle;
    }

}