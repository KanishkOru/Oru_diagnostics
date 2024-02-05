package com.oruphones.nativediagnostic;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.EditText;

import com.google.gson.Gson;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;
//import org.pervacio.wirelessapp.R;

public class PinGenerationActivity extends BaseActivity {
    EditText _ediText;
    private static String TAG = PinGenerationActivity.class.getSimpleName();
    DeviceInformation deviceInformation;

    // public static ClientFacade facade = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _ediText = (EditText) findViewById(R.id.Session_Id);

        //   Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/aileron_light.ttf");
        //  _ediText.setTypeface(mFont);
        CommandServer.getInstance(this).stopAllThreads();
        CommandServer.getInstance(this).reset();
         DLog.d(TAG, "createSession..............");

        try {
            deviceInformation = DeviceInfo.getInstance(getApplicationContext()).getDeviceInfromationDataFromDeviceInfo(GlobalConfig.getInstance(),PinGenerationActivity.this);
            String body = ((new Gson()).toJson(deviceInformation));
            JSONObject deviceInformationData = new JSONObject(body);
            JSONObject eventData = new JSONObject();
            eventData.put("eventname","DEVICE_INFO");
            eventData.put("eventdata",deviceInformationData);
            CommandServer.getInstance(this).connect(eventData.toString(), "dcs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected void getServerData(Bundle data) {
        String pin =  data.getString("result");
         DLog.d(TAG, pin);
        try {
            _ediText.setTextColor(getResources().getColor(R.color.blue_pre));
            _ediText.setText(pin);

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
             DLog.e(TAG, e.toString());
        }


    }
    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.pingeneration_layout;
    }
}

