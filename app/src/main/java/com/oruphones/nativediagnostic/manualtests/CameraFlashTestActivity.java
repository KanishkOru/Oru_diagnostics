package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestResult;


public class CameraFlashTestActivity extends ManualTestsProgressBarActivity {

   private String mTestName;
   private FrameLayout layoutFrame;
    /*private TextView manualtest_name,test_description;
    LinearLayout gifViewLayout;*/
    private boolean accessDenied=false;
    //private Button mStopBtn;
    //private ProgressBar mProgressBar;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.getData().getString("result");
            if (TestResult.ACCESSDENIED.equalsIgnoreCase(result)) {
                manualTestResultDialog(mTestName, result, CameraFlashTestActivity.this);
            } else{
                launchResultActivity(mTestName);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
       // layoutFrame = (FrameLayout) findViewById(R.id.layout_frame);
        /*manualtest_name = (TextView) findViewById(R.id.test_name);
        test_description = (TextView) findViewById(R.id.test_description);

        mStopBtn = (Button) findViewById(R.id.stoptest_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
        manualtest_name.setText(getDisplayName(mTestName));
        gifViewLayout = (LinearLayout)  findViewById(R.id.cameraGifViewLayout);
        gifViewLayout.removeAllViews();
        gifViewLayout.addView(getGIFMovieView(getApplicationContext(), mTestName));
        setFontToView(manualtest_name,ROBOTO_LIGHT);
        setFontToView(test_description,ROBOTO_LIGHT);*/
        accessDenied=false;
        /*if(Util.isAdvancedTestFlow()) {
            manualtest_name.setVisibility(View.INVISIBLE);
            mStopBtn.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }*/
        /*Button stopButton = (Button)findViewById(R.id.stoptest_btn);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualTest.getInstance(CameraFlashTestActivity.this).stopFlashTest();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            if(!accessDenied) {
                if (permissionStatusCheck(mTestName)) {
                    ManualTest.getInstance(this).performCameraTest(mTestName, layoutFrame, handler);
                }
            }
        }
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected void stopButtonClicked() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
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
