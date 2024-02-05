package com.oruphones.nativediagnostic.resolutions;



import static com.oruphones.nativediagnostic.api.Resolution.SCREEN_OFF_SETTINGS;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import com.oruphones.nativediagnostic.models.tests.TestResult;


/**
 * Created by Pervacio on 11/09/2017.
 */

public class ScreenTimeOutResolutionActivity extends BaseActivity {

    TextView screenTimeOutRecommondation, screTimeOutStatusText;
    private String mCurrentResoltion;
    private SwitchCompat toggleSwitch;
    private Button cancelBtn, doneBtn;

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
                    status = false;
                } else if ("ON".equalsIgnoreCase(testResult.getStatus())) {
                    status = true;
                }
                performResolution(status);
            }


            if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                screTimeOutStatusText.setText(getResources().getString(R.string.screentimeout_improved));
                cancelBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                if (isAssistedApp) {
                    sendResultToServer(TestName.SCREEN_TIMEOUT, TestResult.OPTIMIZED);
                    toggleSwitch.setChecked(true);
                }

            } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                screTimeOutStatusText.setText(getResources().getString(R.string.screen_timeout_status));
                doneBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                if (isAssistedApp) {
                    sendResultToServer(TestName.SCREEN_TIMEOUT, TestResult.CANBEIMPROVED);
                    toggleSwitch.setChecked(false);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        screTimeOutStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        screenTimeOutRecommondation = (TextView) findViewById(R.id.recommondation_text);
        screenTimeOutRecommondation.setText(getResources().getString(R.string.screen_timeout_recommondation_text));
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        screTimeOutStatusText.setText(getResources().getString(R.string.screen_timeout_status));
        toggleSwitch.setChecked(false);
        cancelBtn = (Button) findViewById(R.id.cancel_tv);
        doneBtn = (Button) findViewById(R.id.accept_tv);
        cancelBtn.setOnClickListener(cancelClickListener);
        doneBtn.setOnClickListener(cancelClickListener);

        setFontToView(screenTimeOutRecommondation, OPENSANS_REGULAR);
        setFontToView(screTimeOutStatusText, OPENSANS_REGULAR);
        CommandServer.getInstance(this).setUIHandler(handler);

    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.screen_timeout_toolbar_text);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BaseActivity.WRITE_SETTINGS) {
            if(permissionCheck(Manifest.permission.WRITE_SETTINGS))  {
                int defTimeOut = 0;
                try {
                    defTimeOut = Settings.System.getInt(getApplicationContext().getContentResolver(), SCREEN_OFF_SETTINGS);
                } catch (Settings.SettingNotFoundException e) {
                }
                if (defTimeOut <= 60000) {
                    sendResultToServer(TestName.SCREEN_TIMEOUT, TestResult.OPTIMIZED);
                } else {
                    performResolution(true);
                }
            } else {
                sendResultToServer(TestName.SCREEN_TIMEOUT, TestResult.CANBEIMPROVED);
            }
        }
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }
}