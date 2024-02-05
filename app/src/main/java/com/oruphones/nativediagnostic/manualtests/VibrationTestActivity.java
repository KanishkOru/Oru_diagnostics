package com.oruphones.nativediagnostic.manualtests;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.diagtests.TestResult;


/**
 * Created by pervacio on 18-09-2017.
 */

public class VibrationTestActivity extends ManualTestsProgressBarActivity {


    private static String TAG = VibrationTestActivity.class.getSimpleName();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            DLog.d(TAG, "VibrationTestActivity : ");

            super.handleMessage(msg);
            int resultCode = msg.what;
            if (resultCode == ManualTestEvent.VIBRATION_INTENSITY_ZERO) {
                DLog.d(TAG, "VibrationTestActivity  ManualTestEvent.VIBRATION_INTENSITY_ZERO: ");

                Toast.makeText(VibrationTestActivity.this, getResources().getString(R.string.increase_vibration_intensity), Toast.LENGTH_SHORT).show();
            }  else if (resultCode == TestResult.RESULT_PERMISSION_NOT_GRANTED) {
                DLog.d(TAG, "VibrationTestActivity  RESULT_PERMISSION_NOT_GRANTED: ");

                manualTestResultDialog(TestName.VIBRATIONTEST, com.oruphones.nativediagnostic.models.tests.TestResult.ACCESSDENIED, VibrationTestActivity.this);
            } else if (resultCode == TestResult.RESULT_PASS || resultCode==TestResult.RESULT_FAIL) {
                DLog.d(TAG, "VibrationTestActivity  resultCode == TestResult.RESULT_PASS || resultCode==TestResult.RESULT_FAIL: ");

                launchResultActivity(TestName.VIBRATIONTEST);
            } else if(resultCode== ManualTestEvent.RELUANCH_VIBRATION_TEST){
                DLog.d(TAG, "VibrationTestActivity  ManualTestEvent.RELUANCH_VIBRATION_TEST: ");

                Intent intent =new Intent(VibrationTestActivity.this,VibrationTestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(TEST_NAME, mCurrentTest);
                startActivity( intent);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(this).performTest(TestName.VIBRATIONTEST, handler);
        }

    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.VIBRATIONTEST);
    }

    @Override
    protected void stopButtonClicked() {
        ManualTest.getInstance(this).stopVibration();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }
}
