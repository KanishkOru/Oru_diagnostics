package com.oruphones.nativediagnostic.manualtests;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;


/**
 * Activity to test Camera Picture Test.
 * <p/>
 * Created by Surya Polasanapalli on 23-09-2017.
 */


public class CameraPictureTestActivity extends BaseActivity {

    private String mCurrentTest;
    private  Boolean isClicked ;
    private CountDownTimer countDownTimer;
    private FrameLayout preview;
    private TextView timer;
    private Handler continuousWarning;
    private boolean isTimerRunning = false;
    private boolean testStopped = false;
    private Button capturePictureBtn,recordVideoButton;
    private Boolean accessDenied=false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            String path = msg.getData().getString("message");
            if(mCurrentTest.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST) ||mCurrentTest.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST) ){
                Intent intent = new Intent(CameraPictureTestActivity.this, CameraTestResultActivity.class);
                intent.putExtra(TEST_NAME, mCurrentTest);
                intent.putExtra("path", path);
                intent.putExtra("result", result);
                startActivity(intent);
                finish();
            }else{
                if (TestResult.PASS.equalsIgnoreCase(result)) {
                    Intent intent = new Intent(CameraPictureTestActivity.this, CameraTestResultActivity.class);
                    intent.putExtra(TEST_NAME, mCurrentTest);
                    intent.putExtra("path", path);
                    intent.putExtra("result", result);
                    startActivity(intent);
                    finish();
                } else{
                    manualTestResultDialog(mCurrentTest, result, CameraPictureTestActivity.this);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTest = getIntent().getStringExtra(TEST_NAME);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        testStopped = false;
        capturePictureBtn = (Button) findViewById(R.id.camera_capture_btn);
        recordVideoButton=(Button) findViewById(R.id.camera_record_btn);
        recordVideoButton.setVisibility(View.GONE);
        capturePictureBtn.setVisibility(View.VISIBLE);
        timer = findViewById(R.id.timer_text_camera);
        accessDenied=false;
        isClicked = false;
        continuousWarning = new Handler();

        //startWarningToast();
        startBlinking();
        startTimer();
        capturePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualTest.getInstance(CameraPictureTestActivity.this).recordVideoOrCaptuePhoto();
                capturePictureBtn.setEnabled(false);
                isClicked = true;
            }
        });
    }

    private void toggleVisibility(String text) {
        TextView timertext = findViewById(R.id.Record_text);
        timertext.setText(text);
        if (timertext.getVisibility() == View.VISIBLE) {
            timertext.setVisibility(View.INVISIBLE);
        } else {
            timertext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBlinking();
    }

    void startTimer ()
    {
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timer.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }
            @Override
            public void onFinish() {
                timer.setText("00:00");
                isTimerRunning = false;
                isClicked = true;
            }
        }.start();
        isTimerRunning = true;
    }

    private void startBlinking() {
        continuousWarning.postDelayed(blinkRunnable, 0);
    }

    private void stopBlinking() {
        continuousWarning.removeCallbacks(blinkRunnable);
    }
    private Runnable blinkRunnable = new Runnable() {
        @Override
        public void run() {
            TextView timertext = findViewById(R.id.Record_text);
            // Check if the "Record" button is not touched
            if (!isClicked) {
                timertext.setTextColor(getResources().getColor(R.color.white));
                toggleVisibility(getResources().getString(R.string.picture_toast));
            }else{
                stopBlinking();
                timertext.setVisibility(View.GONE);


            }

            continuousWarning.postDelayed(this, 500);
        }
    };
    private void startWarningToast() {
        continuousWarning.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isClicked) {
                    TextView text = (TextView)findViewById(R.id.Record_text);
                    text.setText(R.string.picture_toast);
                    //  Toast.makeText(context, "Tap To Capture", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (continuousWarning!=null) {
                        continuousWarning.removeCallbacks(this);
                    }
                }
                // Schedule the next toast
                continuousWarning.postDelayed(this, 3000);
            }
        }, 1000);
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
        testStopped = false;
        if(!(alertDialog != null && alertDialog.isShowing())) {
            if(!accessDenied) {
                if (permissionStatusCheck(mCurrentTest)) {
                    ManualTest.getInstance(this).performCameraTest(mCurrentTest, preview, handler);
                }
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(!testStopped) {
            testStopped = true;
            //ManualTest.getInstance(this).stopTest();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!testStopped) {
            testStopped = true;
            //ManualTest.getInstance(this).stopTest();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAccessDenied = false;
        for(int i=0; i<grantResults.length;i++){
            if(grantResults[i] != 0 ){
                isAccessDenied = true;
                break;
            }
        }
        if(isAccessDenied){
            accessDenied=true;
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result", TestResult.ACCESSDENIED);
            msg.setData(bundle);
            if (handler != null)
                handler.sendMessage(msg);
        }
    }
}