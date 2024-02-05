package com.oruphones.nativediagnostic.manualtests;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


/**
 * Created by PERVACIO on 25-09-2017.
 */
public class GestureTest extends BaseActivity {

    private String mTestName;
    private TextView manualtest_name;
    private Button failtestBtn;
    private ImageView btnUp, btnDown, btnRight, btnLeft;
    private static String TAG = GestureTest.class.getSimpleName();
    LinearLayout gestureGifViewLayout;
    TextView prevTest;
    TextView nextTest;
    private int currentTestIndex;
    TextView testNumView;
    private LinearLayout testDescLayout;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result", "");
            DLog.d(TAG, "GESTURE :" + msg.what);
            if (result.equalsIgnoreCase(TestResult.TIMEOUT) || result.equalsIgnoreCase(TestResult.PASS) || result.equalsIgnoreCase(TestResult.FAIL)) {
                ManualTest.getInstance(GestureTest.this).stopTest();
                manualTestResultDialog(mTestName, result, GestureTest.this);
            } else {
                switch (msg.what) {
                    case ManualTestEvent.GESTURE_RIGHT:
                        changeOnSwipe(btnRight);
                        break;
                    case ManualTestEvent.GESTURE_LEFT:
                        changeOnSwipe(btnLeft);
                        break;
                    case ManualTestEvent.GESTURE_UP:
                        changeOnSwipe(btnUp);
                        break;
                    case ManualTestEvent.GESTURE_DOWN:
                        changeOnSwipe(btnDown);
                        break;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gestureview
        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        manualtest_name = (TextView) findViewById(R.id.test_name);
//        prevTest = findViewById(R.id.prev_test_view);
//        nextTest = findViewById(R.id.next_test_view);
//        testNumView = findViewById(R.id.test_num_view);
        currentTestIndex = selectedManualTests.indexOf(mTestName);
        failtestBtn = (Button) findViewById(R.id.failtest_btn);
        manualtest_name.setText(getDisplayName(mTestName));

        btnUp = (ImageView) findViewById(R.id.btnUp);
        btnDown = (ImageView) findViewById(R.id.btnDown);
        btnRight = (ImageView) findViewById(R.id.btnRight);
        btnLeft = (ImageView) findViewById(R.id.btnLeft);
        btnUp.setBackgroundResource(R.drawable.ic_checkblank);
        btnDown.setBackgroundResource(R.drawable.ic_checkblank);
        btnLeft.setBackgroundResource(R.drawable.ic_checkblank);
        btnRight.setBackgroundResource(R.drawable.ic_checkblank);
        testDescLayout = findViewById(R.id.manual_test_desc_ll);
        testDescLayout.setVisibility(View.GONE);
//        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex+1, selectedManualTests.size()));
        if(currentTestIndex > 0)
            prevTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(selectedManualTests.get(currentTestIndex - 1)));
        else
            prevTest.setText("");
        if(currentTestIndex < selectedManualTests.size() - 1)
            nextTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(selectedManualTests.get(currentTestIndex + 1)));
        else
            nextTest.setText("");
        /*gestureGifViewLayout = (LinearLayout) findViewById(R.id.gestureGifViewLayout);
        gestureGifViewLayout.removeAllViews();
        gestureGifViewLayout.addView(getGIFMovieView(getApplicationContext(), mTestName));*/
        setFontToView(manualtest_name, OPENSANS_LIGHT);
        failtestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualTest.getInstance(GestureTest.this).stopTest();
                manualTestResultDialog(mTestName, TestResult.FAIL, GestureTest.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(alertDialog != null && alertDialog.isShowing())) {
            ManualTest.getInstance(GestureTest.this).performTest(TestName.GUESTURETEST, handler);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //Disabling Options menu while test is in progress
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_gesture_test;
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    private void changeOnSwipe(ImageView b) {
        b.setBackgroundResource(R.drawable.ic_passed);
        b.setEnabled(false);
    }
}
