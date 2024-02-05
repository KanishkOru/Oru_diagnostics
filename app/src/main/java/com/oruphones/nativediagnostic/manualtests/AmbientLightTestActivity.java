package com.oruphones.nativediagnostic.manualtests;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;

/**
 * Created by Pervacio on 18/09/2017.
 */

public class AmbientLightTestActivity extends MiddleActivity {


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result =   msg.getData().getString("result");

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
                    manualTestResultDialog(TestName.AMBIENTTEST, result, AmbientLightTestActivity.this);
                }
            },2500);


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
            ManualTest.getInstance(this).performTest(TestName.AMBIENTTEST, handler);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.AMBIENTTEST);
    }

}
