package com.oruphones.nativediagnostic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.CustomProgressDialog;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.webservices.NetworkResponseListener;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;


public class RANErrorActivity extends BaseActivity {

    public static final int RC_RAN = 2200;
    public static final String EXT_RAN_NUMBER = "ran_number";
    private TextView ranTextFailed, ranNumber;
    private ODDNetworkModule mNetworkModule;
    private CustomProgressDialog mProgressDialog;

    public static void openForResult(Activity activity, boolean finishCurrent) {
        Intent intent = new Intent(activity, RANErrorActivity.class);
        activity.startActivityForResult(intent, RC_RAN);
        if (finishCurrent) {
            activity.finish();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkModule = ODDNetworkModule.getInstance();
        mProgressDialog = new CustomProgressDialog(this);
        initView();
        updateView();
    }


    private void updateView() {
        StringBuilder failedTest = new StringBuilder();
        for (TestInfo testInfo : PervacioTest.getInstance().getResults(TestResult.FAIL)) {
            failedTest.append(testInfo.getDisplayName()).append("\n");
        }
        ranTextFailed.setText(failedTest.toString());
        showRan();
    }

    private void showRan() {

        if (!isOnline()) {
            Util.DialogUtil.twoButtonDialog(RANErrorActivity.this, getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), new String[]{getString(R.string.action_cancel), getString(R.string.btn_retry)},
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRan();
                        }
                    }
            );
            return;
        }

        mProgressDialog.show();
        mNetworkModule.generateRAN(new NetworkResponseListener<String>() {
            @Override
            public void onResponseReceived(String responseBody) {
                mProgressDialog.hide();
                if (ranNumber != null) {
                    ranNumber.setText(responseBody);
                }
            }

            @Override
            public void onError() {
                mProgressDialog.hide();
                AppUtils.toast(getString(R.string.unknown_error));
            }
        });


    }


    private void setResultBack(String ranNumber) {
        Intent data = new Intent();
        data.putExtra(EXT_RAN_NUMBER, ranNumber);
        setResult(RESULT_OK, data);
        finish();
    }

    private void initView() {
        ranNumber = findViewById(R.id.ranNumber);
        ranTextFailed = findViewById(R.id.ranTextFailed);
        findViewById(R.id.ranNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultBack(getText(ranNumber));
            }
        });
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.ran_title);
    }


    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_ranerror;
    }


}
