package com.oruphones.nativediagnostic.manualtests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.StartLocationAlert;
import com.pervacio.batterydiaglib.util.NetworkUtil;

import org.pervacio.onediaglib.utils.AppUtils;

public class BluetoothTestActivity extends ManualTestsProgressBarActivity {
    private String mTestName;
    private TextView manualtest_name;
    LinearLayout bluetoothGifViewLayout;
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
                Toast.makeText(BluetoothTestActivity.this, getResources().getString(R.string.bluetooth_toast), Toast.LENGTH_SHORT).show();
            }
            manualTestResultDialog(mTestName, result, BluetoothTestActivity.this);
        }
    };

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        /*manualtest_name = (TextView)findViewById(R.id.test_name);
        manualtest_name.setText(getDisplayName(mTestName));
        bluetoothGifViewLayout= (LinearLayout)  findViewById(R.id.bluetoothGifViewLayout);
        bluetoothGifViewLayout.removeAllViews();
        bluetoothGifViewLayout.addView(getGIFMovieView(getApplicationContext(), mTestName));
        setFontToView(manualtest_name, ROBOTO_LIGHT);*/
        accessDenied=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            if(!accessDenied) {
                if (permissionStatusCheck(TestName.BLUETOOTHCONNECTIVITYTEST)) {
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
                    ManualTest.getInstance(BluetoothTestActivity.this).performTest(TestName.BLUETOOTHCONNECTIVITYTEST, handler);
                }
            } else {
                new StartLocationAlert(this,null);
            }
        }else{
            ManualTest.getInstance(BluetoothTestActivity.this).performTest(TestName.BLUETOOTHCONNECTIVITYTEST, handler);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case StartLocationAlert.RC_GPS_SETTINGS:
                if(resultCode!= Activity.RESULT_OK)
                    takeTest();
                else
                    manualTestResultDialog(mTestName, TestResult.FAIL, BluetoothTestActivity.this);
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
