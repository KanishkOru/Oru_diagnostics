package com.oruphones.nativediagnostic.resolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class NfcResolutionActivity extends BaseActivity {

    TextView nfcRecommondation, nfcStatusText;
    private Button nfcSettings,mCancel;
    private String mCurrentResoltion;
    private SwitchCompat toggleSwitch;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            String message = bundle.getString("message");
            if("NFC".equals(result)) {
                nfcSettings.setOnClickListener(nfcSettingsClickListener);
                nfcSettings.performClick();
                return;
            } else if (ResolutionName.NFC_OFF.equalsIgnoreCase(mCurrentResoltion)) {
                relaunch(result);
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_off));
                    nfcSettings.setText(R.string.done);
                    nfcSettings.setOnClickListener(cancelClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.NFC_OFF, "OPTIMIZED");
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_on));
                    nfcSettings.setText(R.string.go_to_nfc_setting);
                    nfcSettings.setOnClickListener(nfcSettingsClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.NFC_OFF, "OPTIMIZABLE");
                }
            } else if (ResolutionName.NFC_ON.equalsIgnoreCase(mCurrentResoltion)) {
                relaunch(result);
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_on));
                    nfcSettings.setText(R.string.done);
                    nfcSettings.setOnClickListener(cancelClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.NFC_ON, "OPTIMIZED");
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_off));
                    nfcSettings.setText(R.string.go_to_nfc_setting);
                    nfcSettings.setOnClickListener(nfcSettingsClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.NFC_ON, "OPTIMIZABLE");
                }
            }
        }
    };
    private View.OnClickListener nfcSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showSettingsScreen(ResolutionName.NFC_OFF.equalsIgnoreCase(mCurrentResoltion), mCurrentResoltion);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        nfcStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        nfcRecommondation = (TextView) findViewById(R.id.recommondation_text);
        nfcSettings = (Button) findViewById(R.id.accept_tv);
        mCancel = (Button) findViewById(R.id.cancel_tv);

        setFontToView(nfcRecommondation,OPENSANS_REGULAR);
        setFontToView(nfcStatusText,OPENSANS_REGULAR);

    }

    public void relaunch(String result) {
        Intent i = getIntent();
        i.putExtra("isFromRelaunch", true);
        i.putExtra("result", result);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Boolean isFromRelaunch = getIntent().getBooleanExtra("isFromRelaunch",false);
        mCancel.setVisibility(View.GONE);
        nfcSettings.setVisibility(View.VISIBLE);
        nfcSettings.setText(getResources().getString(R.string.go_to_nfc_setting));
        toggleSwitch.setVisibility(View.GONE);
        nfcSettings.setOnClickListener(nfcSettingsClickListener);

        CommandServer.getInstance(this).setUIHandler(handler);

        if(!isFromRelaunch) {
            Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, true, getApplicationContext());
        }else{
            String result = getIntent().getStringExtra("result");
            if(ResolutionName.NFC_ON.equalsIgnoreCase(mCurrentResoltion)){
                nfcRecommondation.setText(getResources().getString(R.string.nfc_on_recommondation_text));
                if(Resolution.getInstance().RESULT_OPTIMIZED.equals(result)) {
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_on));
                    nfcSettings.setText(R.string.done);
                    nfcSettings.setOnClickListener(cancelClickListener);
                }else{
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_off));
                    nfcSettings.setText(R.string.go_to_nfc_setting);
                    nfcSettings.setOnClickListener(nfcSettingsClickListener);
                }

            }else if(ResolutionName.NFC_OFF.equalsIgnoreCase(mCurrentResoltion)){
                nfcRecommondation.setText(getResources().getString(R.string.nfc_recommondation_text));
                if(Resolution.getInstance().RESULT_OPTIMIZED.equals(result)){
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_off));
                    nfcSettings.setText(R.string.done);
                    nfcSettings.setOnClickListener(cancelClickListener);
                }else{
                    nfcStatusText.setText(getResources().getString(R.string.nfc_status_on));
                    nfcSettings.setText(R.string.go_to_nfc_setting);
                    nfcSettings.setOnClickListener(nfcSettingsClickListener);
                }
            }
        }
        getIntent().putExtra("isFromRelaunch", false);
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
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

//    private void sendResultToServer(String testName, String result) {
//        PDTestResult pdTestResult = new PDTestResult();
//        pdTestResult.setStatus(result);
//        pdTestResult.setName(testName);
//        CommandServer.getInstance(this).postEventData("RESOLUTION_STATUS", pdTestResult);
//    }
}
