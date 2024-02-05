package com.oruphones.nativediagnostic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.oruphones.nativediagnostic.controller.CustomerRatingBaseActivity;
import com.oruphones.nativediagnostic.models.CSATData;
import com.oruphones.nativediagnostic.util.CustomProgressDialog;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.webservices.NetworkResponseListener;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;


public class CustomerRatingDialogActivity extends CustomerRatingBaseActivity {

    private static String TAG = CustomerRatingDialogActivity.class.getSimpleName();
    private long sessionid = -1;
    private CustomProgressDialog mProgressDialog;


    public static void startActivity(Activity activity, Long sessionID) {
        Intent intent = new Intent(activity, CustomerRatingDialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(EX_SESSION_ID, sessionID);
        bundle.putString(EX_TYPE, null);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private void initValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionid = bundle.getLong(EX_SESSION_ID);
        }
        mProgressDialog = new CustomProgressDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();

    }



    @Override
    public  void submitFeedBack(CSATData feedBackData) {
        mProgressDialog.setInfo(R.string.connecting_to_server, R.string.please_wait);
        mProgressDialog.show();

        feedBackData.setSessionId(sessionid);
        ODDNetworkModule nm = ODDNetworkModule.getInstance();
        DLog.d(TAG, "Submit CSAT : " + feedBackData.toString());
        nm.submitCSAT(feedBackData,new NetworkResponseListener() {

            @Override
            public void onResponseReceived(Object objct) {
                JsonObject resultJson = (JsonObject) objct;

                mProgressDialog.hide();
                if (resultJson != null && "PASS".equalsIgnoreCase(resultJson.get("status").getAsString())) {
                    DLog.d(TAG, "Submit CSAT response : " + resultJson.toString());
                    Intent intent1 = new Intent(getApplicationContext(), EndingSessionActivity.class);
                    intent1.putExtra("ExitDelay", true);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    finish();
                } else {
                    DLog.d(TAG, "Submit CSAT response : " + resultJson);
                    Toast.makeText(CustomerRatingDialogActivity.this, getResources().getString(R.string.internet_unvailable), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError() {
                DLog.e(TAG, "Submit CSAT response : Error ");
                mProgressDialog.hide();
                Toast.makeText(CustomerRatingDialogActivity.this, getResources().getString(R.string.internet_unvailable), Toast.LENGTH_LONG).show();
            }
        });
    }
}

