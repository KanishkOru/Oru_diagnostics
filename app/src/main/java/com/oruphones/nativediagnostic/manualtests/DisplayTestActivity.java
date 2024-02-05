package com.oruphones.nativediagnostic.manualtests;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


/**
 * Activity to Test Display(LCD) test.
 * <p/>
 * Created by Surya Polasanapalli on 16-09-2017.
 */

public class DisplayTestActivity extends BaseActivity {

    private String mTestName;
    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int resultCode=msg.what;
            String result = bundle.getString("result");
            String path = bundle.getString("message");;
//            DLog.d("Result Display code", String.valueOf(resultCode));
            if(result.equalsIgnoreCase(TestResult.TIMEOUT) || result.equalsIgnoreCase(TestResult.FAIL)){
                ManualTest.getInstance(DisplayTestActivity.this).stopTestTimer("D");
                PervacioTest.getInstance().setScreenTestTestImgPath(mTestName, path);
                manualTestResultDialog(mTestName, result,DisplayTestActivity.this);
            }else{
                ManualTest.getInstance(DisplayTestActivity.this).stopTestTimer("D");
                if(mTestName.equalsIgnoreCase(TestName.DISPLAYTEST)) {
                    DLog.d("Result Display code", String.valueOf(resultCode));
                    globalConfig.setCurentTestManual(TestName.DISPLAYTEST);
                    launchResultActivity(mTestName);
                }else {
                    PervacioTest.getInstance().setScreenTestTestImgPath(mTestName, path);
                    manualTestResultDialog(mTestName, result,DisplayTestActivity.this);
                }
            }

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
      //  DLog.d("DisplayTest On Stop called");

    //    ManualTest.getInstance(DisplayTestActivity.this).stopDeadPixelTest();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

     //   DLog.d("DisplayTest On destroy called");

    //    ManualTest.getInstance(DisplayTestActivity.this).stopDeadPixelTest();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(TestName.DISPLAYTEST.equalsIgnoreCase(mTestName)){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        mCurrentTestActivity = this;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !(TestName.DISPLAYTEST.equalsIgnoreCase(mTestName))) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            (new Handler()).postDelayed(
            new Runnable(){
                public void run(){
                    ManualTest.getInstance(DisplayTestActivity.this).performTest(mTestName, resultHandler);
                }
            }, 500);
        }

    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
            if (hasFocus) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && isNotchAvailable()) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    //| View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

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
    protected boolean isFullscreenActivity(){
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dummy;
    }

    private boolean isNotchAvailable() {
        int statusBarHeight = 0;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return  false;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        //AppUtils.printLog(TAG, "status_bar_height : "+statusBarHeight+" stndrd status bar height : "+getStandardStatubarHeightInPixel(), null, Log.ERROR);
        return statusBarHeight != getStandardStatubarHeightInPixel();
    }

    private int getStandardStatubarHeightInPixel(){
        float heightInDP = 24;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = heightInDP * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}

