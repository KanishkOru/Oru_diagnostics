package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;


/**
 * Activity to test Dimming Test.
 * <p/>
 * Created by Surya Polasanapalli on 16-09-2017.
 */


public class DimmingTestActivity extends BaseActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            if (result != null && (result.equalsIgnoreCase(TestResult.TIMEOUT) || result.equalsIgnoreCase(TestResult.FAIL))) {
                manualTestResultDialog(TestName.DIMMINGTEST, result, DimmingTestActivity.this);
            }else{
                globalConfig.setCurentTestManual(TestName.DIMMINGTEST);
                launchResultActivity(TestName.DIMMINGTEST);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity(){
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dimming_test;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(this).performTest(TestName.DIMMINGTEST, handler);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

