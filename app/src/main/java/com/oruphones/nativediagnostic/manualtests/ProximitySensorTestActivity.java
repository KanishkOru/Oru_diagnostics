package com.oruphones.nativediagnostic.manualtests;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;


/**
 * Created by Pervacio on 18/09/2017.
 */

public class ProximitySensorTestActivity extends MiddleActivity{

        private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                    manualTestResultDialog(TestName.PROXIMITYTEST, result, ProximitySensorTestActivity.this);
                }
            },2500);
//            manualTestResultDialog(TestName.PROXIMITYTEST, result, ProximitySensorTestActivity.this);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(this).performTest(TestName.PROXIMITYTEST, handler);
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

//    @Override
//    protected String getToolBarName() {
//        return getDisplayName(TestName.PROXIMITYTEST);
//    }
//
//    @Override
//    protected void stopButtonClicked() {
//
//    }
}
