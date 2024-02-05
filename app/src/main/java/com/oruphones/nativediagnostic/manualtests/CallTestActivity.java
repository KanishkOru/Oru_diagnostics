package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;


/**
 * Created by Surya Polasanapalli on 17/09/2017.
 */
public class CallTestActivity extends ManualTestsProgressBarActivity {

    //TextView mTestName, mTestDescription;
    //ImageView imageView;
    //LinearLayout callGiffViewLayout;
    private String testResult;
    private Boolean accessDenied=false;
    private boolean isTestStarted = false;
    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //String result = msg.getData().getString("result");
            Bundle bundle = msg.getData();
            String result =   bundle.getString("result","");
            if(result!=null){
                isTestStarted = false;
                if (TestResult.USERINPUT.equalsIgnoreCase(result)){
                    globalConfig.setCurentTestManual(TestName.CALLTEST);
                    launchResultActivity(TestName.CALLTEST);
                }
                else
                    manualTestResultDialog(mCurrentManualTest, result, CallTestActivity.this);}
        }
    };
    private TextView mStartTest, mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mStartTest = (TextView) findViewById(R.id.button2);
        mCancel = (TextView) findViewById(R.id.button1);
        mTestName = (TextView) findViewById(R.id.test_name);
        mTestDescription = (TextView) findViewById(R.id.test_description);
        mStartTest.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);*/
        accessDenied=false;
        //imageView = (ImageView) findViewById(R.id.imageView);
        /*callGiffViewLayout = (LinearLayout)  findViewById(R.id.callGiffViewLayout);
        callGiffViewLayout.removeAllViews();
        callGiffViewLayout.addView(getGIFMovieView(getApplicationContext(),TestName.CALLTEST));
        mTestName.setText(getDisplayName(TestName.CALLTEST));*/
        mCurrentManualTest = TestName.CALLTEST;
        mTestDescription.setText(getResourceID(TestName.CALLTEST, TEST_TRY_MESAGE));
        //imageView.setImageResource(getResourceID(PDConstants.CALLTEST, TEST_TRY_IMAGE));
        //setFontToView(mTestDescription,ROBOTO_LIGHT);
        //setFontToView(mTestName,ROBOTO_LIGHT);
        /*
       added in oncreate so, performtest() func will call only once for assisted .
         */
        if(isAssistedApp) {
            if(BaseActivity.onCall){
                testResult=TestResult.PASS;
                updateResultToHandler(testResult);
            } else if(!accessDenied){
                    if (permissionStatusCheck(TestName.CALLTEST)) {
                    ManualTest.getInstance(this).performTest(TestName.CALLTEST, resultHandler);
                }
            }
        } else {
/*            if (!(alertDialog != null && alertDialog.isShowing())) {
                ManualTest.getInstance(this).performTest(TestName.CALLTEST, resultHandler);*/
              isTestStarted = false;
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.CALLTEST);
    }

    @Override
    protected void stopButtonClicked() {

    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    /*@Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_test_perform;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if(!isAssistedApp && !isTestStarted ) {
            if (!(alertDialog != null && alertDialog.isShowing())) {
                isTestStarted = true;
                ManualTest.getInstance(this).performTest(TestName.CALLTEST, resultHandler);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != 0) {
            accessDenied = true;
            testResult = TestResult.ACCESSDENIED;
            updateResultToHandler(testResult);
        } else {
            ManualTest.getInstance(this).performTest(TestName.CALLTEST, resultHandler);
        }
    }
    private void updateResultToHandler(String testResult){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("result", testResult);
        msg.setData(bundle);
        if (resultHandler != null)
            resultHandler.sendMessage(msg);
    }
}
