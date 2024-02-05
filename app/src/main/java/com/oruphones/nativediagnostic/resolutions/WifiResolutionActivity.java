package com.oruphones.nativediagnostic.resolutions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

public class WifiResolutionActivity extends BaseActivity {

    TextView wifiRecommondation, wifiStatusText ;
    private Button cancelBtn,doneBtn;
    private String mCurrentResoltion;
    private static String TAG = WifiResolutionActivity.class.getSimpleName();
    private SwitchCompat toggleSwitch;
    private BroadcastReceiver wifiStateChangedReceiver;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = null, message = null;
            PDTestResult testResult = null;
            if (bundle != null) {
                result = bundle.getString("result");
                message = bundle.getString("message");
            }
            if (result != null && isAssistedApp) {
                testResult = (PDTestResult) PervacioTest.getInstance().getObjectFromData(result, new TypeToken<PDTestResult>() {
                }.getType());

            }
            if (testResult != null && isAssistedApp) {
                boolean status = false;
                if ("OFF".equalsIgnoreCase(testResult.getStatus())) {
                    status = true;
                } else if ("ON".equalsIgnoreCase(testResult.getStatus())) {
                    status = false;
                }
                Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, status, getApplicationContext());
            }
            if (ResolutionName.WIFI_ON.equalsIgnoreCase(mCurrentResoltion)) {
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                   // wifiStatusText.setText(getResources().getString(R.string.wifi_status_on));
                    cancelBtn.setVisibility(View.GONE);
                    doneBtn.setVisibility(View.VISIBLE);
                    if (isAssistedApp){
                        sendResultToServer(TestName.WIFI_ON, "OPTIMIZED");
                        toggleSwitch.setChecked(true);
                    }
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                 //   wifiStatusText.setText(getResources().getString(R.string.wifi__status_off));
                    doneBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    if (isAssistedApp) {
                        sendResultToServer(TestName.WIFI_ON, "OPTIMIZABLE");
                        toggleSwitch.setChecked(false);
                    }
                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        wifiStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        wifiRecommondation = (TextView) findViewById(R.id.recommondation_text);
        wifiRecommondation.setText(getResources().getString(R.string.wifi_connectivity_recommondation));
//        wifiStatusText.setText(getResources().getString(R.string.wifi__status_off));
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        toggleSwitch.setChecked(false);
        cancelBtn = (Button) findViewById(R.id.cancel_tv);
        doneBtn=(Button)findViewById(R.id.accept_tv);
       cancelBtn.setOnClickListener(cancelClickListener);
       doneBtn.setOnClickListener(cancelClickListener);

      // checkWifiStatus(WifiResolutionActivity.this,wifiStatusText);
        CommandServer.getInstance(this).setUIHandler(handler);
       if(isAssistedApp)
           CommandServer.getInstance(getApplicationContext()).setUIHandler(handler);
        toggleSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(!isAssistedApp)
            Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, isChecked, getApplicationContext());
        });

        wifiStateChangedReceiver
                = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);
                switch (extraWifiState) {

                    case WifiManager.WIFI_STATE_DISABLED:
                       wifiStatusText.setText(getResources().getString(R.string.wifi__status_off));
                        toggleSwitch.setChecked(false);
                        doneBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        DLog.d(TAG, "wifi on");
                       wifiStatusText.setText(getResources().getString(R.string.wifi_status_on));
                        toggleSwitch.setChecked(true);
                        doneBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

        };

        registerReceiver(wifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        setFontToView(wifiRecommondation,OPENSANS_REGULAR);
        setFontToView(wifiStatusText,OPENSANS_REGULAR);

    }

//    public static void checkWifiStatus(Context context,TextView wifiStatusText) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        android.net.Network network = connectivityManager.getActiveNetwork();
//        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
//        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//            showToast(context, "WiFi is ON");
//            wifiStatusText.setText(R.string.wifi_status_on);
//        } else {
//            wifiStatusText.setText(R.string.wifi__status_off);
//        }
//    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.wifi_heading_text);
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.setting_resolution;
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String mssg = wifiStatusText.getText().toString();
//        toggleSwitch.setChecked(mssg.contains("ON"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateChangedReceiver);
    }
}
