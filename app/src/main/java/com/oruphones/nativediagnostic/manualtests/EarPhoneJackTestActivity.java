package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


public class EarPhoneJackTestActivity extends MiddleActivity {
    ProgressBar progressBar;
    private static String TAG = EarPhoneJackTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        progressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
//        progressBar.setVisibility(View.INVISIBLE);



    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            DLog.d(TAG,"result:"+msg.what);
            if (result != null && (result.equalsIgnoreCase(TestResult.TIMEOUT) || result.equalsIgnoreCase(TestResult.FAIL))) {
                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.FAIL);

                Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manualTestResultDialog(TestName.EARPHONEJACKTEST, result, EarPhoneJackTestActivity.this);
                    }
                },2500);
//                manualTestResultDialog(TestName.EARPHONEJACKTEST, result, EarPhoneJackTestActivity.this);
            }else{
            switch (msg.what) {
                case 8:
                    if (TestResult.USERINPUT.equalsIgnoreCase(result))
                        mCancel.setEnabled(false);
                    mCancel.setAlpha(0.4f);
                        AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(),TestResult.PASS);
                    Handler handle = new Handler();
                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            manualTestResultDialog(TestName.EARPHONEJACKTEST, "PASS", EarPhoneJackTestActivity.this);
                        }
                    },2500);
//                        manualTestResultDialog(TestName.EARPHONEJACKTEST, "PASS", EarPhoneJackTestActivity.this);

                    break;
                case 9:
                    manualTestResultDialog(TestName.EARPHONEJACKTEST, "PASS", EarPhoneJackTestActivity.this);
                    break;
                case ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST:
                    Toast.makeText(EarPhoneJackTestActivity.this, getResources().getText(R.string.connect_earaphones), Toast.LENGTH_LONG).show();
                    break;

                case ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.ear_jack_unplug_earphones), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;

            }
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(this).performTest(TestName.EARPHONEJACKTEST, handler);
        }

    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.EARPHONEJACKTEST);
    }

//    @Override
//    protected void stopButtonClicked() {
//
//    }


}
