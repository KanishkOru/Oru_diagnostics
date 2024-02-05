package com.oruphones.nativediagnostic.manualtests;

import android.content.Intent;
import android.hardware.SensorPrivacyManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestResult;


/**
 * Activity to test Camera Video Tests.
 * <p/>
 * Created by Surya Polasanapalli on 23-09-2017.
 */


public class CameraVideoTestActivity extends BaseActivity {

    Handler preventBlankCaptureHandler;
    LinearLayout tipsContainer;
    private String mCurrentTest;
    private FrameLayout preview;
    private Handler continuousWarning;
    private boolean isTimerRunning = false;
    private TextView timerText;
    private CountDownTimer countDownTimer;
    boolean supportsCameraToggle;
    private Button recordVideoButton;
    private Boolean accessDenied = false;
    private Handler ToastHandler = new Handler();
    private boolean isRecording = false, isRecorded = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            if (TestResult.PASS.equalsIgnoreCase(result)) {
                String path = msg.getData().getString("message");
                Intent intent = new Intent(CameraVideoTestActivity.this, CameraTestResultActivity.class);
                intent.putExtra(TEST_NAME, mCurrentTest);
                intent.putExtra("path", path);
                startActivity(intent);
                finish();
            } else {
                manualTestResultDialog(mCurrentTest, result, CameraVideoTestActivity.this);
            }
        }
    };


    private void toggleVisibility(String text) {
        TextView timertext = findViewById(R.id.Record_text);
        timertext.setText(text);
        if (timertext.getVisibility() == View.VISIBLE) {
            timertext.setVisibility(View.INVISIBLE);
        } else {
            timertext.setVisibility(View.VISIBLE);
        }
    }
    private void startBlinking() {
        ToastHandler.postDelayed(blinkRunnable, 0);
    }

    private void stopBlinking() {
        ToastHandler.removeCallbacks(blinkRunnable);
    }

    private Runnable blinkRunnable = new Runnable() {
        @Override
        public void run() {
            TextView timertext = findViewById(R.id.Record_text);
            // Check if the "Record" button is not touched
            if (!isRecording) {
                timertext.setTextColor(getResources().getColor(R.color.white));
                toggleVisibility(getResources().getString(R.string.record_toast));
            }else{
                timertext.setTextColor(getResources().getColor(R.color.red));

                toggleVisibility(getResources().getString(R.string.recording));
            }

            ToastHandler.postDelayed(this, 500); // Set the blinking interval (3 seconds)
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTest = getIntent().getStringExtra(TEST_NAME);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        recordVideoButton = (Button) findViewById(R.id.camera_record_btn);
        tipsContainer = findViewById(R.id.tips_section);
        timerText = findViewById(R.id.timer_text_camera);
        accessDenied = false;
        continuousWarning = new Handler();
        SensorPrivacyManager sensorPrivacyManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            sensorPrivacyManager = getApplicationContext().getSystemService(SensorPrivacyManager.class);
            supportsCameraToggle = sensorPrivacyManager.supportsSensorToggle(SensorPrivacyManager.Sensors.CAMERA);
            if (supportsCameraToggle) tipsContainer.setVisibility(View.VISIBLE);
            else tipsContainer.setVisibility(View.GONE);
        }

        startTimer();
        //startWarningToast();
        startBlinking();
        recordVideoButton.setOnClickListener(v -> {
            if (isRecording) {
                TextView timertext = findViewById(R.id.Record_text);
                if (!isRecorded)
                    timertext.setTextColor(getResources().getColor(R.color.green));
                toggleVisibility("Please Wait..");
                  //  Toast.makeText(CameraVideoTestActivity.this,"Please Wait..",Toast.LENGTH_SHORT).show();
                    //Snackbar.make(preview, "Please Wait..", Snackbar.LENGTH_LONG).show();
                preventBlankCapture();
            } else {

                isRecording = true;
                ManualTest.getInstance(CameraVideoTestActivity.this).recordVideoOrCaptuePhoto();
              //  Toast.makeText(CameraVideoTestActivity.this,"Recording",Toast.LENGTH_SHORT).show();
              //  Snackbar.make(preview, "Recording", Snackbar.LENGTH_LONG).show();
                recordVideoButton.setBackground(getResources().getDrawable(R.drawable.button_camera_click));
            }
        });
    }

    private void startWarningToast() {
        continuousWarning.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRecording) {
                    // Only show the toast if isRecording is false
                    Snackbar.make(preview, "Tap to Record", Snackbar.LENGTH_LONG).show();
                } else {
                    if (continuousWarning != null) {
                        continuousWarning.removeCallbacks(this);
                    }
                }
                // Schedule the next toast
                continuousWarning.postDelayed(this, 5000);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBlinking();
    }

    void preventBlankCapture() {
        preventBlankCaptureHandler = new Handler();
        handler.postDelayed(() -> {
            ManualTest.getInstance(CameraVideoTestActivity.this).stopVideoCapture();
            recordVideoButton.setVisibility(View.GONE);
            isRecorded = true;
        }, 3000);
    }

    void startTimer() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timerText.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                isTimerRunning = false;
                isRecording = true;
            }
        }.start();
        isTimerRunning = true;
    }

    void stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            timerText.setText("00:00");
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
        return R.layout.camerapicture_activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordVideoButton.setEnabled(true);
        isRecording = false;
        recordVideoButton.setBackground(getResources().getDrawable(R.drawable.button_camera_selector));
        if (!(alertDialog != null && alertDialog.isShowing())) {
            if (!accessDenied) {
                if (permissionStatusCheck(mCurrentTest)) {
                    ManualTest.getInstance(this).performCameraTest(mCurrentTest, preview, handler);
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //ManualTest.getInstance(this).stopTest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAccessDenied = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != 0) {
                isAccessDenied = true;
                break;
            }
        }
        if (isAccessDenied) {
            accessDenied = true;
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result", TestResult.ACCESSDENIED);
            msg.setData(bundle);
            if (handler != null)
                handler.sendMessage(msg);
        }
    }
}

