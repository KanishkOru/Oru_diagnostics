package com.oruphones.nativediagnostic.resolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestName;
public class GpsResolutionActivity extends BaseActivity {

    private TextView gpsRecommondation, gpsStatusText,  noteText ;
    private Button mCancel,gpsSettings;
    private String mCurrentResoltion;
    private SwitchCompat toggleSwitch;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            String message = bundle.getString("message");
            if("GPS".equals(result)) {
                gpsSettings.setOnClickListener(gpsSettingsClickListener);
                gpsSettings.performClick();
                return;
            } else if (ResolutionName.GPS_OFF.equalsIgnoreCase(mCurrentResoltion)) {
                relaunch(result);
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                    gpsStatusText.setText(getResources().getString(R.string.gps_off));
                    gpsSettings.setText(R.string.done);
                    gpsSettings.setOnClickListener(cancelClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.GPS_OFF, "OPTIMIZED");
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                    gpsStatusText.setText(getResources().getString(R.string.gps_on));
                    gpsSettings.setText(R.string.go_to_gps_setting);
                    gpsSettings.setOnClickListener(gpsSettingsClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.GPS_OFF, "OPTIMIZABLE");
                }
            } else if (ResolutionName.GPS_ON.equalsIgnoreCase(mCurrentResoltion)) {
                relaunch(result);
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                    gpsStatusText.setText(getResources().getString(R.string.gps_on));
                    gpsSettings.setText(R.string.done);
                    gpsSettings.setOnClickListener(cancelClickListener);
                    mCancel.setOnClickListener(cancelClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.GPS_ON, "OPTIMIZED");
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                    gpsStatusText.setText(getResources().getString(R.string.gps_off));
                    gpsSettings.setText(R.string.go_to_gps_setting);
                    gpsSettings.setOnClickListener(gpsSettingsClickListener);
                    if (isAssistedApp)
                        sendResultToServer(TestName.GPS_ON, "OPTIMIZABLE");
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        gpsStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        gpsRecommondation = (TextView) findViewById(R.id.recommondation_text);
        gpsSettings = (Button) findViewById(R.id.accept_tv);
        noteText = (TextView) findViewById(R.id.note_tv);
        mCancel = (Button) findViewById(R.id.cancel_tv);

        setFontToView(gpsRecommondation, OPENSANS_REGULAR);
        setFontToView(noteText, OPENSANS_LIGHT);
        setFontToView(gpsStatusText, OPENSANS_REGULAR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean isFromRelaunch = getIntent().getBooleanExtra("isFromRelaunch",false);
        mCancel.setVisibility(View.GONE);
        noteText.setVisibility(View.VISIBLE);
        noteText.setText(getResources().getString(R.string.note_text));
        gpsSettings.setText(getResources().getString(R.string.go_to_gps_setting));
        toggleSwitch.setVisibility(View.GONE);
        gpsSettings.setVisibility(View.VISIBLE);
        CommandServer.getInstance(this).setUIHandler(handler);
        //gpsRecommondation.setText(getResources().getString(R.string.gps_recommondation_text));
        gpsStatusText.setText(getResources().getString(R.string.gps_on));
        //gpsSettings.setText(R.string.go_to_gps_setting);

        gpsSettings.setOnClickListener(gpsSettingsClickListener);
        if(!isFromRelaunch) {
            Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, false, getApplicationContext());
        }else{
            String result = getIntent().getStringExtra("result");
            if(ResolutionName.GPS_ON.equalsIgnoreCase(mCurrentResoltion)){
                gpsRecommondation.setText(getResources().getString(R.string.gps_on_recommondation_text));
                if(Resolution.getInstance().RESULT_OPTIMIZED.equals(result)) {
                    gpsStatusText.setText(getResources().getString(R.string.gps_on));
                    gpsSettings.setText(R.string.done);
                    gpsSettings.setOnClickListener(cancelClickListener);
                    mCancel.setOnClickListener(cancelClickListener);
                }else{
                    gpsStatusText.setText(getResources().getString(R.string.gps_off));
                    gpsSettings.setText(R.string.go_to_gps_setting);
                    gpsSettings.setOnClickListener(gpsSettingsClickListener);
                }
            }else if(ResolutionName.GPS_OFF.equalsIgnoreCase(mCurrentResoltion)){
                gpsRecommondation.setText(getResources().getString(R.string.gps_recommondation_text));
                if(Resolution.getInstance().RESULT_OPTIMIZED.equals(result)){
                    gpsStatusText.setText(getResources().getString(R.string.gps_off));
                    gpsSettings.setText(R.string.done);
                    gpsSettings.setOnClickListener(cancelClickListener);
                }else{
                    gpsStatusText.setText(getResources().getString(R.string.gps_on));
                    gpsSettings.setText(R.string.go_to_gps_setting);
                    gpsSettings.setOnClickListener(gpsSettingsClickListener);
                }

            }
        }
        getIntent().putExtra("isFromRelaunch", false);
    }

    private View.OnClickListener gpsSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showSettingsScreen(ResolutionName.GPS_OFF.equalsIgnoreCase(mCurrentResoltion), mCurrentResoltion);
        }
    };

    public void relaunch(String result) {
        Intent i = getIntent();
        i.putExtra("isFromRelaunch", true);
        i.putExtra("result", result);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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

}
