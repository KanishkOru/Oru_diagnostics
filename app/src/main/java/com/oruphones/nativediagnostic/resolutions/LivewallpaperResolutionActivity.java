package com.oruphones.nativediagnostic.resolutions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.reflect.TypeToken;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDTestResult;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

/**
 * Created by Pervacio on 11/09/2017.
 */

public class LivewallpaperResolutionActivity extends BaseActivity {

    private TextView liveWallPaperStatus_tv, livewallpaperRecommendation;
    private SwitchCompat toggleSwitch;
    private static String TAG = LivewallpaperResolutionActivity.class.getSimpleName();
    private String mCurrentResoltion;
    private Button cancelBtn,doneBtn;


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
                    DLog.i(TAG, "true");
                } else if ("ON".equalsIgnoreCase(testResult.getStatus())) {
                    status = false;
                    DLog.i(TAG, "flase");
                }
                Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, status, getApplicationContext());
            }
            if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                liveWallPaperStatus_tv.setText(getResources().getText(R.string.livewallpaer_off));
              //  toggleSwitch.setEnabled(false);
                cancelBtn.setVisibility(View.GONE);
                doneBtn.setVisibility(View.VISIBLE);
                if (isAssistedApp){
                    sendResultToServer(TestName.LIVEWALLPAPER, "OPTIMIZED");
                    toggleSwitch.setChecked(false);
                }
            } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                Toast.makeText(LivewallpaperResolutionActivity.this, getResources().getString(R.string.live_wallpaper_msg), Toast.LENGTH_SHORT).show();
                liveWallPaperStatus_tv.setText(getResources().getText(R.string.livewallpaer_on));
               // toggleSwitch.setChecked(true);
                doneBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                if (isAssistedApp) {
                    sendResultToServer(TestName.LIVEWALLPAPER, "OPTIMIZABLE");
                    toggleSwitch.setChecked(true);
                }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.i(TAG, "onCreate");
        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        liveWallPaperStatus_tv = (TextView) findViewById(R.id.resolution_on_off_tv);
        livewallpaperRecommendation = (TextView) findViewById(R.id.recommondation_text);
        livewallpaperRecommendation.setText(getResources().getString(R.string.livewallpaper_recommondation_text));
        liveWallPaperStatus_tv.setText(getResources().getText(R.string.livewallpaer_on));
        cancelBtn = (Button) findViewById(R.id.cancel_tv);
        doneBtn=(Button)findViewById(R.id.accept_tv);
        cancelBtn.setOnClickListener(cancelClickListener);
        doneBtn.setOnClickListener(cancelClickListener);
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        setFontToView(livewallpaperRecommendation,OPENSANS_REGULAR);
        setFontToView(liveWallPaperStatus_tv,OPENSANS_REGULAR);
        CommandServer.getInstance(this).setUIHandler(handler);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isAssistedApp)
                Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, isChecked, getApplicationContext());
            }
        });

    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.livewallpaper_toolbar_text);
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
}
