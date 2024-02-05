package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


public class EarphoneTestActivity extends ManualTestsProgressBarActivity {
    private static String TAG = EarphoneTestActivity.class.getSimpleName();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            DLog.d(TAG,"RESULT:"+result+"Message:"+msg.what);

            DLog.d(TAG, "Earphone result: " + result);
            if (result != null&& (TestResult.TIMEOUT.equalsIgnoreCase(result)||TestResult.FAIL.equalsIgnoreCase(result)||TestResult.PASS.equalsIgnoreCase(result)))
                manualTestResultDialog(TestName.EARPHONETEST, result, EarphoneTestActivity.this);

            switch (msg.what) {
                case 0:
                    break;
                case 8:

                    if (TestResult.USERINPUT.equalsIgnoreCase(result))
                        launchResultActivity(TestName.EARPHONETEST);

                    break;

                case ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE:
                    showAcessibilityDialogue();

                    break;
                case ManualTestEvent.AUDIO_SHOW_EARPHONE_DIALOGUE:
                    if (myDialog == null) {
                        showEarPhonePlugDiaglogue(getResources().getString(R.string.connect_earphones_dialog), "");
                    }

                    break;

                case ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ear_jack_unplug_earphones), Toast.LENGTH_SHORT).show();
                    break;
                case ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_earphones_toast), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        removeSettingDialog(false);

        if(!(alertDialog != null && alertDialog.isShowing())) {
            if(dndModePermissionCheck()) {
                ManualTest.getInstance(this).performTest(TestName.EARPHONETEST, handler);
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        ManualTest.getInstance(this).stopTest();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.EARPHONETEST);
    }

    @Override
    protected void stopButtonClicked() {

    }


}
