package com.oruphones.nativediagnostic.resolutions;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DeviceInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by PERVACIO on 16-11-2017.
 */
public class ResolutionsEducationalActivity extends BaseActivity {

    String result,resolutionName;
    private Button doneBtn;
    private TextView recommondation_text;
    private   String lastRestartDate="",lastRestartHrs="";
    private GlobalConfig mGlobalConfig;
    private DeviceInfo  mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolutionName= getIntent().getStringExtra(TEST_NAME);
        recommondation_text = (TextView)findViewById(R.id.recommondation_text);
        doneBtn=(Button)findViewById(R.id.done_btn);
        setFontToView(recommondation_text,OPENSANS_REGULAR);
        recommondation_text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlobalConfig = GlobalConfig.getInstance();
        mDeviceInfo = DeviceInfo.getInstance(this);

        Bundle bundle = getIntent().getExtras();
        long lastRestartValue= mGlobalConfig.getCurrentServerTime()-GlobalConfig.getInstance().getLastRestartFromDevice();
        long lastrestartinhrs = mGlobalConfig.getLastRestartFromDevice();

        lastRestartHrs= String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(lastrestartinhrs),
                TimeUnit.MILLISECONDS.toMinutes(lastrestartinhrs) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(lastrestartinhrs)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(lastrestartinhrs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lastrestartinhrs)));
        Date date = new Date(lastRestartValue);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        lastRestartDate=formatter.format(date);


        if (bundle != null) {
            result = bundle.getString("TestResult");
        }
        if (TestResult.ACCESSDENIED.equalsIgnoreCase(result)) {
            recommondation_text.setText(R.string.access_denied_text);
        } else if (TestResult.FAIL.equalsIgnoreCase(result)) {
            recommondation_text.setText(R.string.failed_text);
        } else {
            if (TestName.FIRMWARE.equalsIgnoreCase(resolutionName)) {
                recommondation_text.setText(getString(R.string.firmware_text, mDeviceInfo.getFirmwareVersion()/*, mGlobalConfig.getLatestFirmwareVersion()*/));
            }

            if (TestName.SIMCARD.equalsIgnoreCase(resolutionName))
                recommondation_text.setText(R.string.sim_card_resolution_text);
            else if (TestName.LastRestart.equalsIgnoreCase(resolutionName)) {
                String str = getResources().getString(R.string.last_restart);
                String str1 = str.replace("**", lastRestartDate);
                String lastRestartText = str1.replace("##", "");
                recommondation_text.setText(lastRestartText);
            }
            else if (TestName.QUICKBATTERYTEST.equalsIgnoreCase(resolutionName)) {
                String str = getResources().getString(R.string.quick_battery_suggestion_one);
                recommondation_text.setText(str);
                TextView recommended_text_one = (TextView) findViewById(R.id.recommondation_text1);
                TextView recommended_text_two = (TextView) findViewById(R.id.recommondation_text2);
                setFontToView(recommended_text_one,OPENSANS_LIGHT);
                setFontToView(recommended_text_two,OPENSANS_LIGHT);
                recommended_text_one.setVisibility(View.VISIBLE);
                recommended_text_one.setText(R.string.quick_battery_suggestion_two);
                recommended_text_two.setVisibility(View.VISIBLE);
                recommended_text_two.setText(R.string.quick_battery_suggestion_three);
            }
        }
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.result_details;
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }
}
