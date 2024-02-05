package com.oruphones.nativediagnostic.resolutions;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.tests.TestName;

// Created by Pervacio on 11/09/2017.

public class BrightnessResolutionActivity extends BaseActivity {


    private TextView brightnessStatusText, brightnessRecomendationText;
    private SwitchCompat toggleSwitch;
    private Button cancelBtn, doneBtn;
    private String mCurrentResoltion;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = null, message = null;
            if (bundle != null) {
                result = bundle.getString("result");
                message = bundle.getString("message");
            }
            PDTestResult testResult = null;
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
                performResolution(status);
            }
            if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                cancelBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                brightnessStatusText.setText(getResources().getString(R.string.brightness_automode));
                if (isAssistedApp) {
                    sendResultToServer(TestName.BRIGHTNESS, "OPTIMIZED");
                    toggleSwitch.setChecked(false);
                }
            } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                doneBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                if (isAssistedApp) {
                    sendResultToServer(TestName.BRIGHTNESS, "OPTIMIZABLE");
                    toggleSwitch.setChecked(true);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        brightnessStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        brightnessRecomendationText = (TextView) findViewById(R.id.recommondation_text);
        cancelBtn = (Button) findViewById(R.id.cancel_tv);
        doneBtn = (Button) findViewById(R.id.accept_tv);
        cancelBtn.setOnClickListener(cancelClickListener);
        doneBtn.setOnClickListener(cancelClickListener);
        brightnessStatusText.setText(getResources().getString(R.string.auto_brightness_off));
        brightnessRecomendationText.setText(getResources().getString(R.string.brightness_recommendation));
        toggleSwitch.setChecked(false);
        setFontToView(brightnessRecomendationText, OPENSANS_REGULAR);
        setFontToView(brightnessStatusText, OPENSANS_REGULAR);
        CommandServer.getInstance(this).setUIHandler(handler);

    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.brightness_toolbar_text);
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
    protected void onResume() {
        super.onResume();
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isAssistedApp)
                    performResolution(isChecked);

            }
        });
    }

    private void performResolution(boolean isChecked) {
        if ((permissionCheck(Manifest.permission.WRITE_SETTINGS))) {
            Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, isChecked, getApplicationContext());
        } else {
            toggleSwitch.setChecked(false);
            showWriteSettingRequest(1);
        }
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }
}
