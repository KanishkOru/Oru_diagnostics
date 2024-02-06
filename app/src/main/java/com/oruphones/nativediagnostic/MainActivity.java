package com.oruphones.nativediagnostic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.util.Constants;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.PreferenceHelper;
import com.oruphones.nativediagnostic.util.RemoteConfigHelper;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
//    public static MethodChannel.Result mResult;
    private GlobalConfig globalConfig;
    Button DiagBtn,Verfybtn;
    private static String TAG = "MainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalConfig = GlobalConfig.getInstance();
        setContentView(R.layout.activity_main);

        DiagBtn = findViewById(R.id.DiagnosticButton);
        Verfybtn = findViewById(R.id.VerificationButton);

      //  HashMap<String, Boolean> argumentsRunDiagnostics = (HashMap<String, Boolean>) call.arguments;
        DiagBtn.setOnClickListener(view -> {
            GlobalConfig.getInstance().setVerification(false);
            GlobalConfig.getInstance().setBuyerVerification(false);
            startNewActivity();
////            HashMap<String, Boolean> argumentsRunDiagnostics = (HashMap<String, Boolean>) call.arguments;
//            GlobalConfig.getInstance().setVerification(argumentsRunDiagnostics.get("isVerification"));
//            GlobalConfig.getInstance().setBuyerVerification(argumentsRunDiagnostics.get("isBuyerVerification"));
            GlobalConfig globalConfigRunDiagnostics = GlobalConfig.getInstance();
            if (globalConfigRunDiagnostics != null) {
                Object isFinalVerifyValue = true;
                if (isFinalVerifyValue != null) {
                    globalConfigRunDiagnostics.setFinalVerify(Boolean.TRUE.equals(isFinalVerifyValue));
                } else {
                    // Handle the case when "isFinalVerify" value is null
                }
            } else {
                // Handle the case when GlobalConfig.getInstance() returns null
            }
        });
        Verfybtn.setOnClickListener(view -> {
            GlobalConfig.getInstance().setVerification(true);
            GlobalConfig.getInstance().setBuyerVerification(false);
            GlobalConfig globalConfigRunDiagnostics = GlobalConfig.getInstance();
            if (globalConfigRunDiagnostics != null) {
                Object isFinalVerifyValue = false;
                if (isFinalVerifyValue != null) {
                    globalConfigRunDiagnostics.setFinalVerify(Boolean.FALSE.equals(isFinalVerifyValue));
                } else {
                    // Handle the case when "isFinalVerify" value is null
                }
            } else {
                // Handle the case when GlobalConfig.getInstance() returns null
            }
            startNewActivity();
//            GlobalConfig globalConfigRunDiagnostics = GlobalConfig.getInstance();
//            if (globalConfigRunDiagnostics != null) {
//                Object isFinalVerifyValue = argumentsRunDiagnostics.get("isFinalVerify");
//                if (isFinalVerifyValue != null) {
//                    globalConfigRunDiagnostics.setFinalVerify(Boolean.TRUE.equals(isFinalVerifyValue));
//                } else {
//                    // Handle the case when "isFinalVerify" value is null
//                }
//            } else {
//                // Handle the case when GlobalConfig.getInstance() returns null
//            }
        });




    }
    private void verificationType(){
//        GlobalConfig globalConfigRunDiagnostics = GlobalConfig.getInstance();
//        if (globalConfigRunDiagnostics != null) {
//            Object isFinalVerifyValue = argumentsRunDiagnostics.get("isFinalVerify");
//            if (isFinalVerifyValue != null) {
//                globalConfigRunDiagnostics.setFinalVerify(Boolean.TRUE.equals(isFinalVerifyValue));
//            } else {
//            }
//        } else {
//        }
        startNewActivity();
    }
    private void init(){
        new RemoteConfigHelper(this, this).startBackendSwitchListener(new RemoteConfigHelper.Callback() {
            @Override
            public void onBackendRatingsCountGap(Long RatingsCount) {
//                RatingsCount = Math.toIntExact(RatingsCountGap);
                DLog.d(TAG, String.valueOf(RatingsCount));
                PreferenceHelper.getInstance(getApplicationContext()).putIntegerCount(Constants.RATINGSCOUNTGAP, Math.toIntExact(RatingsCount));
            }
        });
    }
    private void startNewActivity() {
        try {
            DLog.d(TAG, "MobiruFlutter: Starting new activity - WelcomeScreenActivity.");
            Intent intent = new Intent(MainActivity.this, WelcomeScreenActivity.class);
            startActivity(intent);
            DLog.d(TAG, "MobiruFlutter: Activity started successfully.");
        } catch (Exception e) {
            DLog.e(TAG, "MobiruFlutter: Error in startNewActivity." + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            globalConfig.setScrollPosition(0);
            globalConfig.getTestStates().clear();

            if (!GlobalConfig.getInstance().getItemList().isEmpty()) {
               // DLog.d(TAG, "FinalResult" + GlobalConfig.getInstance().getItemList());
                //   copyToClipboard(GlobalConfig.getInstance().getItemList());
                globalConfig.clearItemList();
                globalConfig.clearAllTestIntegers();
            }
       DLog.d("MobiruFlutter: onResume called.");

        } catch (Exception e) {
            e.printStackTrace();
          //  DLog.e(TAG, "MobiruFlutter: Error in onResume." + e);
        }
    }


    



}