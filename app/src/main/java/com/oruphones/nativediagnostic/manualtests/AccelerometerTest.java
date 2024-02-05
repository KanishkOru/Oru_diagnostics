package com.oruphones.nativediagnostic.manualtests;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;


public class AccelerometerTest extends MiddleActivity {


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result =   msg.getData().getString("result");
            if(result.equalsIgnoreCase(TestResult.PASS)){
                mCancel.setEnabled(false);
                mCancel.setAlpha(0.4f);
                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.PASS);
            } else if(result.equalsIgnoreCase(TestResult.FAIL)||result.equalsIgnoreCase(TestResult.TIMEOUT))
            {
                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.FAIL);
            }

            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    manualTestResultDialog(TestName.ACCELEROMETERTEST, result, AccelerometerTest.this);
                }
            },2500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_accelerometer);
        //TextView splashTextView= findViewById(R.id.splashAppTxt);
        //splashTextView.setVisibility(View.GONE);
//        TextView textView= findViewById(R.id.sprint_textview);
//        textView.setText(getString(R.string.rotate));
//        textView.setTextAppearance(this,R.style.appname_title);
//        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
//        textView.setTypeface(tf);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(this).performTest(TestName.ACCELEROMETERTEST, handler);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().getBooleanExtra("startTest", false)) {
            getIntent().putExtra("startTest", false);
            ManualTest.getInstance(this).performTest(TestName.ACCELEROMETERTEST,handler);
        }
    }
}