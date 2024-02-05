package com.oruphones.nativediagnostic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oruphones.nativediagnostic.common.CustomButton;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.CustomProgressDialog;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.PreferenceUtil;
import com.oruphones.nativediagnostic.util.StartLocationAlert;

import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.diagtests.TestWiFi;
import org.pervacio.onediaglib.diagtests.TestWifiResult;


public class WifiManualTestUnusedActivity extends BaseUnusedActivity implements TestListener {

    private static String TAG = WifiManualTestUnusedActivity.class.getSimpleName();
    public static final int RC_WIFI_MANUAL = 2938;
    private CustomButton skip,wifiSetting;
    private  LinearLayout gifViewLayout;
    private TextView testName,testDescription;
    private TestWiFi mTestWiFi;
    private CustomProgressDialog mCustomProgressDialog;
    private boolean showCheckStatusProgress;
    final Handler handler = new Handler(Looper.getMainLooper());


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, WifiManualTestUnusedActivity.class);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, WifiManualTestUnusedActivity.class);
        activity.startActivityForResult(intent,RC_WIFI_MANUAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTestWiFi =  new TestWiFi();
        mCustomProgressDialog =  new CustomProgressDialog(this);
        //gifViewLayout = (LinearLayout)  findViewById(R.id.gifViewLayout);


       // testName =  findViewById(R.id.test_name);
       // testName.setText(R.string.wifi_connectivity_test);

       // testDescription =  findViewById(R.id.test_description);
        skip =  findViewById(R.id.cancel_tv);
        skip.setText(R.string.str_skip);
        wifiSetting= findViewById(R.id.accept_tv);
        wifiSetting.setText(R.string.wifi_setting);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        wifiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckStatusProgress =true;
                mTestWiFi.launchTheWifiSetting(WifiManualTestUnusedActivity.this);
                mCustomProgressDialog.setInfo(getString(R.string.wifi_connectivity_test),getString(R.string.wifi_check_status));
                mCustomProgressDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        waitForStatusCheck();
    }

    public Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            mCustomProgressDialog.hide();
            checkWifiStatus();
        }
    };
    private void waitForStatusCheck(){
        handler.removeCallbacks(delayRunnable);
        if(!mTestWiFi.getState() && showCheckStatusProgress){
            handler.postDelayed(delayRunnable, 1000);
        }else{
            checkWifiStatus();
        }
    }

    private void checkWifiStatus(){
        if(mTestWiFi.getState()){
            wifiSearching();
        }else{
            wifiOff();
        }
    }

    private void changeImage(String imageName){
        gifViewLayout.removeAllViews();
        gifViewLayout.addView(CommonUtil.getNewGIFMovieView(this,imageName));
    }
    private void wifiOff(){
        changeImage("wifi_test_off.gif");
        testDescription.setText(R.string.wifi_connectivity_off_message);
        skip.setEnabled(false);
        skip.setVisibility(View.VISIBLE);
        wifiSetting.setVisibility(View.VISIBLE);
    }

    private void wifiSearching(){
        changeImage("wifi_manual_test.gif");
        testDescription.setText(R.string.bluetooth_connectivity_text);
        skip.setVisibility(View.GONE);
        wifiSetting.setVisibility(View.GONE);
        if (DeviceInfo.getInstance(this).isGPSEnabled()) {
            mTestWiFi.scanTest(this);
        }else{
           new StartLocationAlert(this,null);
        }

    }


    @Override
    protected String getToolBarName() {
        return getString(R.string.wifi_connectivity_heading);
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_test_try;
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TestWiFi.RC_TEST_WIFI:
            case StartLocationAlert.RC_GPS_SETTINGS:
                checkWifiStatus();
                break;

        }
    }*/

    @Override
    public void onTestStart() {
        DLog.d(TAG, "Wifi Manual Test : STARTED");
    }

    @Override
    public void onTestEnd(TestResult testResult) {
        TestWifiResult testWifiResult = (TestWifiResult) testResult;
        DLog.d(TAG, "Wifi Manual Test : FINISHED " + testResult.toString());
        Toast.makeText(this,"Scan "+testWifiResult.scanListMap,Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        bundle.putInt(PreferenceUtil.EX_RESULT,testResult.getResultCode());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RC_WIFI_MANUAL,intent);
        finish();
    }
}

