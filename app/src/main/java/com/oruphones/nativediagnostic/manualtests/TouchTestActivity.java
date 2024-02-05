package com.oruphones.nativediagnostic.manualtests;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.diagtests.TestResult;

/**
 * Activity to test Touch test.
 * <p/>
 * Created by Surya Polasanapalli  on 17-09-2017.
 */

public class TouchTestActivity extends BaseActivity {

    private String mTestName;
    private static String TAG = TouchTestActivity.class.getSimpleName();
    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int resultCode = msg.what;


            if (resultCode == 0) {
                ManualTest.getInstance(TouchTestActivity.this).stopTestTimer("T");
                manualTestResultDialog(mTestName, com.oruphones.nativediagnostic.models.tests.TestResult.PASS, TouchTestActivity.this);
            } else if (resultCode == TestResult.RESULT_TRIPPLE_TOUCH_PERFORMED) {
                showAlert(getString(R.string.alert), getString(R.string.fail_warning));
            } else if (resultCode == TestResult.RESULT_ERROR_TIME_OUT) {
                ManualTest.getInstance(TouchTestActivity.this).stopTestTimer("T");
                manualTestResultDialog(mTestName, com.oruphones.nativediagnostic.models.tests.TestResult.TIMEOUT, TouchTestActivity.this);
            }else if(resultCode==TestResult.RESULT_FAIL){
                ManualTest.getInstance(TouchTestActivity.this).stopTestTimer("T");
                manualTestResultDialog(mTestName, com.oruphones.nativediagnostic.models.tests.TestResult.FAIL, TouchTestActivity.this);
            }

        }
    };



    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        DLog.d(TAG, "TouchTestActivity" + selectedManualTestsResult.size());
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        ManualTest.getInstance(this).performTest(mTestName, resultHandler);
        mCurrentTestActivity = this;
//        showSlowTestDialog(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
//    public static void showSlowTestDialog(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Tip !");
//        builder.setMessage("Please perform the test slowly to ensure accurate results.");
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().getBooleanExtra("startTest", false)) {
            getIntent().putExtra("startTest", false);
            ManualTest.getInstance(this).performTest(mTestName, resultHandler);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P && isNotchAvailable()) {
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
        return true;
    }

    @Override
    protected boolean isFullscreenActivity(){
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dummy;
    }

    @Override
    protected void onStop() {
        super.onStop();

      //  ManualTest.getInstance(TouchTestActivity.this).stopTestTimer("T");
//        ManualTest.getInstance(TouchTestActivity.this).stopTest();
    }

    public void showAlert(String title, String msg) {
        CommonUtil.DialogUtil.getAlert(context,title,msg,getResources().getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ManualTest.getInstance(TouchTestActivity.this).finishTest(TestResult.RESULT_FAIL, getResources().getString(R.string.fail_status));
            }
        },getResources().getString(R.string.action_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        ManualTest.getInstance(TouchTestActivity.this).resumeTest();
                    }
                }).show();

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