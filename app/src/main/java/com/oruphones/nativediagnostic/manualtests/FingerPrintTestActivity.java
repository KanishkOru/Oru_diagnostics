package com.oruphones.nativediagnostic.manualtests;

import android.app.Activity;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.monitor.MonitorServiceDocomo;

import org.pervacio.onediaglib.diagtests.ISensorEventListener;
import org.pervacio.onediaglib.fingerprint.FingerPrintProvider;
import org.pervacio.onediaglib.fingerprint.TestFingerPrintSensor;

/**
 * Created by Pervacio on 19/09/2017.
 */

public class FingerPrintTestActivity extends MiddleActivity implements ISensorEventListener {
    private TestFingerPrintSensor testFingerPrintSensor;
    private boolean isListenerRegistered;
    public static final int RC_FP_MANUAL = 2008;
    private String mCurrentTestName = TestName.FINGERPRINTSENSORTEST;
    private ManualTest mManualTest;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, FingerPrintTestActivity.class);
        activity.startActivityForResult(intent, RC_FP_MANUAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FingerprintManager fingerprintManager = ContextCompat.getSystemService(context, FingerprintManager.class);

        isFingerPrintActivity = true;
        if (fingerprintManager != null) {
            if (FingerPrintProvider.hasFeature()) {
                testFingerPrintSensor = FingerPrintProvider.getInstance().getSensor();
            } else {
                updateResult(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED);
            }
        } else {
            manualTestResultDialog(mCurrentTestName, TestResult.FAIL);
        }
    }

    private void updateResult(int resultCode) {
        mManualTest.setLastPerformedTest(mCurrentTestName, resultCode);
        manualTestResultDialog(mCurrentTestName, TestUtil.getTestResult(resultCode));
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeSettingDialog(false);
        if (!(alertDialog != null && alertDialog.isShowing())) {
            try {

                init();
            }catch (Exception e){
                DLog.e(String.valueOf(e));
            }
        }
    }

    /* private void reInitialize() {
         testFingerPrintSensor.unRegisterSensorResultListener();
         testFingerPrintSensor = null;
         testFingerPrintSensor = FingerPrintProvider.getInstance().getSensor();

     }
 */
    private void init() {
        mManualTest = ManualTest.getInstance(this);
        if (testFingerPrintSensor == null) {
            return;
        }
        if (testFingerPrintSensor.isFingerPrintRegistered()) {
            //startTimer(30*1000);
            isListenerRegistered = true;
            testFingerPrintSensor.registerSensorResultListener(this);
        } else {
           stopCountdownTimer();
            showPopUp();
        }
    }


    @Override
    public void onSensorEventListner(Object object) {
        if ((int) object == TestFingerPrintSensor.SENSOR_FAILED) {
            updateResult(org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL);
        } else if ((int) object == TestFingerPrintSensor.TIME_OUT) {
            updateResult(mManualTest.checkIfAlreadyAttempted(mCurrentTestName, org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT) ? org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL : org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT);
        } else {
            if ((int) object == TestFingerPrintSensor.ERROR_TOO_MANY_ATTEMPTS) {
                Toast.makeText(getApplicationContext(), getString(R.string.finger_print_too_many_attempts), Toast.LENGTH_SHORT).show();
            }
            mCancel.setEnabled(false);
            mCancel.setAlpha(0.4f);
            updateResult(org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS);
        }
        if (testFingerPrintSensor != null) {
            testFingerPrintSensor.unRegisterSensorResultListener();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mManualTest.stopTest();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void showPopUp() {
        CommonUtil.DialogUtil.twoButtonDialog(this, getString(R.string.alert), getString(R.string.finger_print_pop_up_description),
                new String[]{getString(R.string.str_skip), getString(R.string.enrol),}, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manualTestResultDialog(TestName.FINGERPRINTSENSORTEST, TestResult.SKIPPED);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MonitorServiceDocomo.stopService(getApplicationContext());
                        testFingerPrintSensor.launchFingerprintSettingScreen(FingerPrintTestActivity.this);

                    }
                }
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (testFingerPrintSensor == null) {
            return;
        }
        if (isListenerRegistered) {
            testFingerPrintSensor.unRegisterSensorResultListener();
        }
    }


    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.FINGERPRINTSENSORTEST);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
