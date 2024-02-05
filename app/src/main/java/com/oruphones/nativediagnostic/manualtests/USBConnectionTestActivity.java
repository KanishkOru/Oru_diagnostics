package com.oruphones.nativediagnostic.manualtests;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

/**
 * Created by pervacio on 19-09-2017.
 */

public class USBConnectionTestActivity extends MiddleActivity {

    ProgressBar progressBar;
    private static String TAG = USBConnectionTestActivity.class.getSimpleName();
    private boolean testStarted = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            if(msg.what == ManualTestEvent.USB_NOT_CONNECTED){
                Toast.makeText(USBConnectionTestActivity.this, getResources().getString(R.string.please_connect_usb), Toast.LENGTH_SHORT).show();
                return;
            }

            if(result != null && result.equalsIgnoreCase(TestResult.PASS)){
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
                    DLog.d(TAG, TestName.USBTEST+" Result: "+result);
                    manualTestResultDialog(TestName.USBTEST, result, USBConnectionTestActivity.this);
                }
            },2500);

        }
    };


//    private void initView(){
//        if(progressBar!=null){
//            progressBar.setVisibility(View.INVISIBLE);
//        }
//        testStarted = false;
//        Log.d("USBManualConnectionTest", "initView....");
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        progressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
//        initView();
        DLog.d(TAG, "onCreate....");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DLog.d(TAG, "onResume....");
        if(!(alertDialog != null && alertDialog.isShowing())) {
            testStarted = true;
            ManualTest.getInstance(this).performTest(TestName.USBTEST, handler);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.USBTEST);
    }


}
