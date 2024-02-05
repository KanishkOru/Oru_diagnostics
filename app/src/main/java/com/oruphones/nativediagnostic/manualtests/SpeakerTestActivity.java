package com.oruphones.nativediagnostic.manualtests;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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


/**
 * Created by Pervacio on 19/09/2017.
 */

public class SpeakerTestActivity extends ManualTestsProgressBarActivity {
    private static String TAG = SpeakerTestActivity.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            DLog.d(TAG, "result:" + result + "Message:" + msg.what);

            if (result != null && (TestResult.TIMEOUT.equalsIgnoreCase(result) || TestResult.FAIL.equalsIgnoreCase(result) || TestResult.PASS.equalsIgnoreCase(result)))
                manualTestResultDialog(TestName.SPEAKERTEST, result, SpeakerTestActivity.this);

            switch (msg.what) {

                case 0:
                    break;

                case 8:
                    launchResultActivity(TestName.SPEAKERTEST);
                    break;

                case ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE:
                    showAcessibilityDialogue();
                    break;


                case ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ear_jack_unplug_earphones), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;


            }


        }
    };


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, SpeakerTestActivity.class);
        activity.startActivity(intent);
    }

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
        if (!(alertDialog != null && alertDialog.isShowing())) {
            if (dndModePermissionCheck()) {
                ManualTest.getInstance(this).performTest(TestName.SPEAKERTEST, handler);
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
        return getDisplayName(TestName.SPEAKERTEST);
    }

    @Override
    protected void stopButtonClicked() {
        ManualTest.getInstance(this).stopAudio();
    }
}
