package com.oruphones.nativediagnostic.manualtests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.StartLocationAlert;
import com.pervacio.batterydiaglib.util.NetworkUtil;

import org.pervacio.onediaglib.utils.AppUtils;

public class WifiManualTestActivity extends ManualTestsProgressBarActivity{
    private String mTestName;
    private Boolean accessDenied=false;
    private String testResult;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result =   bundle.getString("result");
            String message =  bundle.getString("message");

            if(TextUtils.isEmpty(message)){
                Toast.makeText(WifiManualTestActivity.this, "No nearby network detected", Toast.LENGTH_SHORT).show();
            }
            manualTestResultDialog(mTestName, result, WifiManualTestActivity.this);
        }
    };

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        accessDenied=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            if(!accessDenied) {
                if (permissionStatusCheck(TestName.WIFICONNECTIVITYTEST)) {
                    takeTest();

                }
            }
        }

    }

    private void takeTest(){
        if(AppUtils.VersionUtils.hasQ())
        {
            if (DeviceInfo.getInstance(this).isGPSEnabled()) {
                if (NetworkUtil.isOnline()) {
                    ManualTest.getInstance(WifiManualTestActivity.this).performTest(TestName.WIFICONNECTIVITYTEST, handler);
                }
            } else {
                new StartLocationAlert(this,null);
            }
        }else{
            ManualTest.getInstance(WifiManualTestActivity.this).performTest(TestName.WIFICONNECTIVITYTEST, handler);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case StartLocationAlert.RC_GPS_SETTINGS:
                if(resultCode!= Activity.RESULT_OK) {
                    manualTestResultDialog(mTestName, TestResult.FAIL, WifiManualTestActivity.this);
                }
                else{
//                manualTestResultDialog(mTestName, TestResult.FAIL, WifiManualTestActivity.this);
                    takeTest();
            }
                break;
        }
        super.onActivityResult( requestCode,  resultCode,  data);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }

    /*protected int getLayoutResource() {
        return R.layout.activity_bluetooth_connectivity;
    }*/

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected void stopButtonClicked() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] != 0){
            accessDenied=true;
            testResult= TestResult.ACCESSDENIED;
            updateResultToHandler(testResult);
        }
    }
    private void updateResultToHandler(String testResult){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("result", testResult);
        msg.setData(bundle);
        if (handler != null)
            handler.sendMessage(msg);
    }
}