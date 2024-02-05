package com.oruphones.nativediagnostic.resolutions;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestName;

public class BluetoothResolutionActivity extends BaseActivity {

    private TextView bluetoothRecommondation, bluetoothStatusText, noteText;
    private Button cancelBtn, doneBtn;
    private String mCurrentResoltion;
    private SwitchCompat toggleSwitch;

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
            if (ResolutionName.BLUETOOTH_OFF.equalsIgnoreCase(mCurrentResoltion)) {

                bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_on_text));
                setBtnVisible(getResources().getString(R.string.action_cancel));
                if (isAssistedApp){
                    sendResultToServer(TestName.BLUETOOTH_OFF, "OPTIMIZABLE");
                    toggleSwitch.setChecked(true);
                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {

                    bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_off_text));
                    setBtnVisible(getResources().getString(R.string.done));
                    if (isAssistedApp){
                        sendResultToServer(TestName.BLUETOOTH_OFF, "OPTIMIZED");
                        toggleSwitch.setChecked(false);
                    }
                }
            } else if (ResolutionName.BLUETOOTH_ON.equalsIgnoreCase(mCurrentResoltion)) {
                if (Resolution.getInstance().RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                    bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_on_text));
                    setBtnVisible(getResources().getString(R.string.done));
                    if (isAssistedApp) {
                        sendResultToServer(TestName.BLUETOOTH_ON, "OPTIMIZED");
                        toggleSwitch.setChecked(true);
                    }

                } else if (Resolution.getInstance().RESULT_NOTOPTIMIZED.equalsIgnoreCase(message)) {
                    bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_off_text));
                    setBtnVisible(getResources().getString(R.string.action_cancel));
                    if (isAssistedApp) {
                        sendResultToServer(TestName.BLUETOOTH_ON, "OPTIMIZABLE");
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
        bluetoothStatusText = (TextView) findViewById(R.id.resolution_on_off_tv);
        bluetoothRecommondation = (TextView) findViewById(R.id.recommondation_text);
        cancelBtn = (Button) findViewById(R.id.cancel_tv);
        doneBtn =(Button)findViewById(R.id.accept_tv);
        toggleSwitch = (SwitchCompat) findViewById(R.id.toggle_switch);
        noteText = (TextView) findViewById(R.id.note_tv);
        cancelBtn.setOnClickListener(cancelClickListener);
        doneBtn.setOnClickListener(cancelClickListener);
        setFontToView(bluetoothRecommondation,OPENSANS_REGULAR);
        setFontToView(bluetoothStatusText,OPENSANS_REGULAR);

        noteText.setVisibility(View.VISIBLE);
        noteText.setText(getResources().getString(R.string.bluetooth_note_text));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        updateUI(BluetoothStatus());



        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isAssistedApp)
                    Resolution.getInstance().performSettingsResolution(mCurrentResoltion, handler, isChecked, getApplicationContext());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        updateUI(true);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        updateUI(false);
                        break;
                }
            }
        }
    };

    private void updateUI(Boolean IsbluetoothOn){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IsbluetoothOn){
                    bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_on_text));
                    bluetoothRecommondation.setText(getResources().getString(R.string.bluetooth_recommondation_text));
                    toggleSwitch.setChecked(true);
                    cancelBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.GONE);
                }else{
                    bluetoothStatusText.setText(getResources().getString(R.string.bluetooth_status_off_text));
                    bluetoothRecommondation.setText(getResources().getString(R.string.bluetooth_on_recommondation_text));
                    toggleSwitch.setChecked(false);
                    doneBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.GONE);

                }
            }
        });

    }


    private Boolean BluetoothStatus(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter!=null){
            if (bluetoothAdapter.isEnabled()){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }



    }

    private void setBtnVisible(String btn) {
        if (getResources().getString(R.string.action_cancel).equalsIgnoreCase(btn)) {
            cancelBtn.setVisibility(View.VISIBLE);
            doneBtn.setVisibility(View.GONE);
        } else {
            doneBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(BluetoothStatus());
        CommandServer.getInstance(getApplicationContext()).setUIHandler(handler);
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.setting_resolution;
    }
}