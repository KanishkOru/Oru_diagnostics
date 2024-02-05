package com.oruphones.nativediagnostic.result;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.EndingSessionActivity;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.QuickBatteryTestInfo;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.RANErrorActivity;
import com.oruphones.nativediagnostic.SessionIdEndScreen;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.api.TransactionResponse;
import com.oruphones.nativediagnostic.communication.api.SummaryType;
import com.oruphones.nativediagnostic.models.tests.BatteryPerformanceResult;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.resolutions.AppResolutionsActivity;
import com.oruphones.nativediagnostic.resolutions.BluetoothResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.BrightnessResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.GpsResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.LivewallpaperResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.NfcResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.ResolutionsEducationalActivity;
import com.oruphones.nativediagnostic.resolutions.ScreenTimeOutResolutionActivity;
import com.oruphones.nativediagnostic.resolutions.StorageResolutionsActivity;
import com.oruphones.nativediagnostic.resolutions.WifiResolutionActivity;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.ResultComparator;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;
import com.pervacio.batterydiaglib.api.BatteryTest;
import com.pervacio.batterydiaglib.api.BatteryTestResult;
import com.pervacio.batterydiaglib.core.test.QuickTestComputeEngine;
import com.pervacio.batterydiaglib.model.ActivityResultInfo;
import com.pervacio.batterydiaglib.model.BatteryDiagConfig;
import com.pervacio.batterydiaglib.util.BatteryUtil;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pervacio on 11-09-2017.
 */

/**
 * @deprecated
 * @see ResultsSummeryActivity .
 *
 */
@Deprecated
public class ResultsActivity extends BaseActivity {
    private ArrayList<TestInfo> resultList;
    private ArrayList<String> resolutionList;
    private static String TAG = ResultsActivity.class.getSimpleName();
    private Button done_tv,send_summary;
    private TextView title_suggestedFixes, title_detailedResults, quick_battery_heading, battery_result_heading, five_point_check_result_heading,notes_result_heading,notes_message;
    private TextView batteryHealth, designCapacity, actualCapacity, soh, charging_level, temperature_value, testNotSupported, battery_result_status;
    private TextView make_value, model_value, imei_value, firmware_value, os_version_value, ranValue,start_time_value, sessin_id_value;
    /*RAN*/
    private RelativeLayout ranContainer;

    int resolutionCount = 0;
    private TextView result_Display_Name, result_Observation;
    private LinearLayout layout_resolution_test, layout_result_test, layout_battery_info, layout_five_point_check,layout_battery_health, layout_detailedResults, layout_info, layout_fix_result_info;
    private View list_item = null;
    private View divider_resolution_item = null;
    LayoutInflater inflater;
    private TextView resolutionName, resolutionStatus;
    private ImageView resolutionActionImage, mResolutionNextImg;
    private LinearLayout suggestedFixesLayout,ll_internet_unavailable_retry;
    private ImageButton ib_retry ;
    private ProgressBar mResultUploadProgress;
    HashMap testResult;
    private int RESULT_UPLOAD_REQ = 1;
    private int SUMMARY_DOWNLOAD_REQ = 2;
    private int NET_UNAVAILABLE = 3;
    private int ERROR_TO_CONNECT_SEREVER = 4;
    private boolean isResultUploadSuccess = false;
    private int saveImageRetryCount = 0;
    ProgressDialog progressDialogServerConnect;
    private final int MailSummary = 0;
    private final int ImageSummary = 1;
    private final int ExitSummary = 2;
    private boolean isTransactionUpdateRequired = false;
    private RelativeLayout design_capacity_layout, actual_capacity_layout, charging_level_layout, soh_layout;
    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean setHomeButton() {
        return true;
    }

    @Override
    protected boolean showExitButton() {
        return true;
    }

    public static void start(Context context){
        ResultsSummeryActivity.openActivity(context);
        /*Intent manualTestSelection = new Intent(context, ResultsActivity.class);
        context.startActivity(manualTestSelection);*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(getApplicationContext());
        layout_fix_result_info = (LinearLayout) findViewById(R.id.layout_fix_result_info);
        layout_detailedResults = (LinearLayout) findViewById(R.id.layout_detailedResults);
        layout_info = (LinearLayout) findViewById(R.id.layout_info);
        layout_result_test = (LinearLayout) findViewById(R.id.detailed_test_result);
        layout_battery_info = (LinearLayout)findViewById(R.id.battery_test_result);
        design_capacity_layout = (RelativeLayout) findViewById(R.id.design_capacity_layout);
        actual_capacity_layout = (RelativeLayout) findViewById(R.id.actual_capacity_layout);
        charging_level_layout  = (RelativeLayout) findViewById(R.id.charging_level_layout);
        soh_layout             = (RelativeLayout) findViewById(R.id.soh_layout);
        layout_five_point_check = (LinearLayout)findViewById(R.id.five_point_check_result);
        layout_resolution_test = (LinearLayout) findViewById(R.id.layout_resolution);
        layout_battery_health = (LinearLayout) findViewById(R.id.battery_health_check_layout);
        done_tv = (Button) findViewById(R.id.done_tv);
        send_summary = (Button) findViewById(R.id.send_summary);
        title_detailedResults = (TextView) findViewById(R.id.text_detailedResults);
        title_suggestedFixes = (TextView) findViewById(R.id.text_suggestedFixes);

        battery_result_heading = (TextView) findViewById(R.id.text_battery_results);
        quick_battery_heading = (TextView) findViewById(R.id.text_quick_battery);
        five_point_check_result_heading = (TextView) findViewById(R.id.five_point_check_result_heading);

        notes_result_heading = (TextView) findViewById(R.id.notes_result_heading);
        notes_message = (TextView)findViewById(R.id.notes_message);
        TextView device_info_summary_title = (TextView) findViewById(R.id.device_info_summary_title);
        batteryHealth = (TextView) findViewById(R.id.battery_health_value);
        designCapacity = (TextView) findViewById(R.id.design_capacity_value);
        actualCapacity = (TextView) findViewById(R.id.actual_capacity_value);
        charging_level = (TextView) findViewById(R.id.charging_level_value);
        temperature_value = (TextView) findViewById(R.id.temperature_value);

        soh = (TextView) findViewById(R.id.soh_value);
        testNotSupported = (TextView) findViewById(R.id.test_not_supported);
        battery_result_status = findViewById(R.id.text_battery_status);

        make_value = (TextView) findViewById(R.id.device_info_make_value);
        model_value = (TextView) findViewById(R.id.device_info_model_value);
        imei_value = (TextView) findViewById(R.id.device_info_imei_value);
        firmware_value = (TextView) findViewById(R.id.device_info_firmware_value);
        os_version_value = (TextView) findViewById(R.id.device_info_os_version_value);
        ranValue = (TextView) findViewById(R.id.device_info_ran_value);
        ranContainer =  findViewById(R.id.device_info_ran_container);
        start_time_value = (TextView) findViewById(R.id.device_info_start_time_value);
        sessin_id_value = findViewById(R.id.session_Id_value);
        mResultUploadProgress = findViewById(R.id.result_upload_progress);
        ll_internet_unavailable_retry = findViewById(R.id.internet_unavailable_retry);
        ib_retry = findViewById(R.id.iv_retry);
        mResultUploadProgress.setVisibility(View.GONE);
        setFontToView(title_detailedResults, ROBOTO_LIGHT);
        setFontToView(title_suggestedFixes, ROBOTO_LIGHT);
        setFontToView(battery_result_heading, ROBOTO_LIGHT);
        setFontToView(five_point_check_result_heading, ROBOTO_LIGHT);
        setFontToView(notes_result_heading, ROBOTO_LIGHT);
        setFontToView(notes_message, ROBOTO_REGULAR);
        setFontToView(device_info_summary_title, ROBOTO_LIGHT);
        setFontToView(quick_battery_heading, ROBOTO_LIGHT);

        //Show RAN ON Screen 
        boolean showRanScreen  =!PervacioTest.getInstance().getResults(TestResult.FAIL).isEmpty()
                && GlobalConfig.getInstance().isGenerateRAN();
        if (showRanScreen) {
            RANErrorActivity.openForResult(ResultsActivity.this, false);
        }
        ranContainer.setVisibility(showRanScreen?View.VISIBLE:View.GONE);

        if (Util.showInfoAtMiddle()){
            layout_fix_result_info.removeView(layout_detailedResults);
            layout_fix_result_info.removeView(layout_info);

            //layout_detailedResults.setBottom(R.id.layout_info);
            layout_fix_result_info.addView(layout_info, 1);
            layout_fix_result_info.addView(layout_detailedResults, 2);
        }

        if (Util.enableSendToRepair()) {
            done_tv.setText(R.string.send_to_repair);
        }

        done_tv.setEnabled(false);
        done_tv.setClickable(true);
        if(!PervacioTest.getInstance().getNotesMessage().isEmpty()) {
            notes_message.setVisibility(View.VISIBLE);
            notes_result_heading.setVisibility(View.VISIBLE);
            notes_message.setText(PervacioTest.getInstance().getNotesMessage());
        }
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GlobalConfig.getInstance().setSelectedCategory(PDConstants.RUN_ALL_DIAGNOSTICS);
                if(Util.enableSendToRepair()) {

               //     WebViewUnusedActivity.startActivity(ResultsActivity.this, DeviceInfo.getInstance(ResultsActivity.this).get_imei()

//                    );
                } else {
                    if(Util.showSessionIdEndScreen() && isResultUploadSuccess) {
                        updateHistory();
                        if(isTransactionUpdateRequired) {
                            uploadResultToSerever(ExitSummary);
                        }
                        else {
                        Intent intent = new Intent(ResultsActivity.this, SessionIdEndScreen.class);
                        startActivity(intent);
                        }
                    } else {
                        goHome();
                    }
                }
            }
        });

        if (Util.needToRemoveSendSummaryButton()) {
            send_summary.setVisibility(View.GONE);
        }

        if(Util.sendSummaryEmail()) {
            send_summary.setText(R.string.email_summary);
        }

        send_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.sendSummaryEmail()) {
                    if(isTransactionUpdateRequired) {
                      uploadResultToSerever(MailSummary);
                    } else {
                        //EmailSummaryChildUnusedActivity.startActivity(ResultsActivity.this);

                    }
                } else {
                    if(isTransactionUpdateRequired) {
                        uploadResultToSerever(ImageSummary);
                    } else {
                        getSummaryImage();
                    }

                }
            }
        });

        if (Util.needToSkipResolutions()) {
            title_suggestedFixes.setVisibility(View.GONE);
            layout_resolution_test.setVisibility(View.GONE);
        }

        if(isAssistedApp) {
            done_tv.setEnabled(false);
        }

        ManualTest.getInstance(this).manualTestDone();
        PervacioTest.getInstance().setSessionStatus("Success");
        uploadResultToSerever(-1);

        /*https://pervacio.atlassian.net/browse/SSD-714

        if(isOnline()){
            ll_internet_unavailable_retry.setVisibility(View.GONE);
        }else{
            ll_internet_unavailable_retry.setVisibility(View.VISIBLE);
            send_summary.setEnabled(false);
        }
        * */
        ll_internet_unavailable_retry.setVisibility(View.GONE);
        send_summary.setEnabled(isOnline());

        ib_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    ll_internet_unavailable_retry.setVisibility(View.GONE);
                    send_summary.setEnabled(true);
                }else{
                    progressDialogServerConnect = new ProgressDialog(ResultsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                    progressDialogServerConnect.setTitle(getResources().getString(R.string.connecting_to_server));
                    progressDialogServerConnect.setMessage(getResources().getString(R.string.please_wait));
                    progressDialogServerConnect.setCancelable(false);
                    progressDialogServerConnect.show();

                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            progressDialogServerConnect.dismiss();
                            t.cancel();
                        }
                    }, 2000);
                }
            }
        });
    }

    private int requestFrom = -1;
    private void uploadResultToSerever(int request) {
        requestFrom = request;
        if(isOnline()) {
            mResultUploadProgress.setVisibility(View.VISIBLE);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mTestResultsUploadedReceiver, new IntentFilter("org.pervacio.wirelessapp.TEST_RESULTS_UPDATED"));
            pervacioTest.updateSession();
        } else {
            showErrorDialog(requestFrom, RESULT_UPLOAD_REQ, NET_UNAVAILABLE);
        }
    }

    private void showErrorDialog(final int requestFrom, final int reqCode, final int errorCode ) {
        DLog.e(TAG, "showErrorDialog: reqCode : " +reqCode+" errorCode :: "+errorCode);

        int titleId;
        int messageId;
        int possitveBtnId;
        int negativeBtnId;

        DialogInterface.OnClickListener negativeListener = null;
        if(errorCode == NET_UNAVAILABLE) {
            titleId= R.string.internet_not_available;
            messageId = R.string.network_msz;
            possitveBtnId = R.string.btn_retry;
            negativeBtnId = R.string.action_cancel;
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    done_tv.setEnabled(true);
                    alertDialog.dismiss();
                }
            };
        } else {
            titleId= R.string.server_not_rechable;
            messageId = R.string.server_unavailable_msg;
            possitveBtnId = R.string.str_ok;
            negativeBtnId = R.string.action_cancel;
        }

        alertDialog =   CommonUtil.DialogUtil.getAlert(this,getString(titleId),getString(messageId),getString(possitveBtnId),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(errorCode == NET_UNAVAILABLE) {
                    alertDialog.dismiss();
                    done_tv.setEnabled(true);
                    if(reqCode == RESULT_UPLOAD_REQ) {
                        uploadResultToSerever(requestFrom);
                    } else {
                        uploadResultToSerever(requestFrom);
                        //getSummaryImage();
                    }

                }else {
                    alertDialog.dismiss();
                }
            }
        },getString(negativeBtnId),negativeListener);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Remove the handler
        mInteractionMonitor.setIsEligibleForTimeOut(false,globalConfig.getUserInteractionSessionTimeOut());

        resolutionList = new ArrayList<String>();
        resolutionList.addAll(Resolution.getInstance().getAvailableResolutionsList());

        HashMap manualTestResult = PervacioTest.getInstance().getManualTestResult();
        if(manualTestResult!=null) {
            Iterator mit = manualTestResult.entrySet().iterator();
            while (mit.hasNext()) {
                Map.Entry pair = (Map.Entry) mit.next();
                TestInfo testInfo = (TestInfo) pair.getValue();
                String result = testInfo.getTestResult();
               /* if (TestResult.FAIL.equalsIgnoreCase(result) || TestResult.ACCESSDENIED.equalsIgnoreCase(result))
                    resolutionList.add(testInfo.getName());*/
            }
        }

        updateDeviceInfo();
        resultList = new ArrayList<TestInfo>();
        testResult = PervacioTest.getInstance().getTestResult();
        testResult.putAll(PervacioTest.getInstance().getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());
        Iterator it = testResult.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            TestInfo testInfo = (TestInfo) pair.getValue();
            resultList.add(testInfo);
        }
        Collections.sort(resultList, new ResultComparator());
        layout_result_test.removeAllViews();
        if(!Util.needToSkipResolutions()) {
            layout_resolution_test.removeAllViews();
        }
        layout_five_point_check.removeAllViews();
        layout_battery_health.removeAllViews();
        resolutionCount = resolutionList.size();
        if(!Util.needToSkipResolutions()) {
        if (resolutionCount > 0) {
            for (final String resolution : resolutionList) {
                list_item = inflater.inflate(R.layout.resolution_list_view, null);
                suggestedFixesLayout = (LinearLayout) list_item.findViewById(R.id.suggested_fixes_ll);
                suggestedFixesLayout.setGravity(Gravity.CENTER_VERTICAL);
                resolutionName = (TextView) list_item.findViewById(R.id.resolution_name);
                resolutionActionImage = (ImageView) list_item.findViewById(R.id.resolution_image);
                resolutionStatus=(TextView)list_item.findViewById(R.id.resolution_status);

                if(resolution.equalsIgnoreCase(ResolutionName.INTERNALSTORAGESUGGESTION)) {
                    resolutionStatus.setVisibility(View.GONE);
                    resolutionActionImage.setVisibility(View.GONE);

                }else {
                    resolutionStatus.setVisibility(View.VISIBLE);
                }


                resolutionName.setGravity(Gravity.CENTER_VERTICAL);

                String resolutionDispayName = GlobalConfig.getInstance().getTestDisplayName(resolution);
                if (resolutionDispayName != null && !resolutionDispayName.equalsIgnoreCase("")) {
                    resolutionName.setText(resolutionDispayName);
                } else {
                    resolutionName.setText(ODDUtils.resolutionNames.get(resolution));
                }
                if(ODDUtils.resolutionImages.containsKey(resolution))
                resolutionActionImage.setImageDrawable(AppCompatResources.getDrawable(context, ODDUtils.resolutionImages.get(resolution)));
                if(!isAssistedApp) {
                    list_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startResolution(resolution);
                        }
                    });
                }
                layout_resolution_test.addView(list_item);
            }
        } else {
            String strEmptyMessage = getResources().getString(R.string.all_test_complete);
            String strEmptyMessageSubText = getResources().getString(R.string.no_issues_found);
            for (int i = 0; i < 2; ++i) {
                list_item = inflater.inflate(R.layout.resolution_list_view, null);
                divider_resolution_item = (View) list_item.findViewById(R.id.divider_resoltuon_list);
                resolutionName = (TextView) list_item.findViewById(R.id.resolution_name);
                mResolutionNextImg = list_item.findViewById(R.id.resolution_nxt);
                mResolutionNextImg.setVisibility(View.GONE);
                if (!Util.needToSkipResolutions()) {
                    suggestedFixesLayout = (LinearLayout) list_item.findViewById(R.id.suggested_fixes_ll);
                    suggestedFixesLayout.setGravity(Gravity.CENTER);
                }
                resolutionName.setGravity(Gravity.CENTER);
                if (i == 0) {
                    setFontToView(resolutionName, ROBOTO_REGULAR);
                    resolutionName.setPadding(0, 30, 55, 0);
                    resolutionName.setText(strEmptyMessage);
                    divider_resolution_item.setVisibility(View.INVISIBLE);
                } else {
                    setFontToView(resolutionName, ROBOTO_LIGHT);
                    resolutionName.setPadding(0, 0, 55, 30);
                    resolutionName.setTextSize(14);
                    resolutionName.setText(strEmptyMessageSubText);
                    divider_resolution_item.setVisibility(View.INVISIBLE);
                }
                layout_resolution_test.addView(list_item);
            }
        }
        }
        boolean resultHasBatteryPerformance = false;
        for (TestInfo testInfo : resultList) {
            if(TestName.BATTERYPERFORMANCE.equalsIgnoreCase(testInfo.getName())) {
                resultHasBatteryPerformance = true;
            }
        }
            for (TestInfo testInfo : resultList) {
                if (resultHasBatteryPerformance) {
                    if (BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_PASS ||
                            BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_FAIL) {
                        if (TestName.QUICKBATTERYTEST.equalsIgnoreCase(testInfo.getName())) {
                            continue;
                        }
                    }
                }
            list_item = inflater.inflate(R.layout.results_text, null);
            result_Observation = (TextView) list_item.findViewById(R.id.result_test_observation);
            result_Display_Name = (TextView) list_item.findViewById(R.id.result_test_name_result);
            ImageView result_image_view = (ImageView) list_item.findViewById(R.id.result_image);
            result_Display_Name.setText(testInfo.getDisplayName());
            result_Observation.setText(CommonUtil.getMappedTestResult(testInfo.getTestResult()));

            if (TestResult.CANBEIMPROVED.equals(testInfo.getTestResult())) {
                if(GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaO2UK") ){
                    result_image_view.setImageResource(R.drawable.results_optimizable_amber);
                }else {
                    result_image_view.setImageResource(R.drawable.ic_canbeimproved);
                }
            } else if (TestResult.PASS.equals(testInfo.getTestResult())) {
                result_image_view.setImageResource(R.drawable.ic_passed);
            } else if (TestResult.FAIL.equals(testInfo.getTestResult())) {
                result_image_view.setImageResource(R.drawable.ic_failed);
            } else if (TestResult.OPTIMIZED.equals(testInfo.getTestResult())) {
                result_image_view.setImageResource(R.drawable.ic_passed);
            } else if (TestResult.NOTEQUIPPED.equals(testInfo.getTestResult())) {
                result_image_view.setImageResource(R.drawable.ic_not_equipped);
            } else if (TestResult.SKIPPED.equals(testInfo.getTestResult())) {
                result_image_view.setImageResource(R.drawable.ic_skipped);
            } else if(TestResult.ACCESSDENIED.equals(testInfo.getTestResult())){
                result_image_view.setImageResource(R.drawable.ic_error);
            } else if(TestResult.NOTSUPPORTED.equals(testInfo.getTestResult())){
                result_image_view.setImageResource(R.drawable.ic_notsupported);
            }else {
                result_image_view.setImageResource(R.drawable.ic_not_equipped);
            }
            layout_result_test.addView(list_item);
        }
        //getBatteryResults();
        if (Util.isFivePointCheckRequired()) {
            updateFivePointCheckResults();
        } else {
            updatePhysicalDamageCheckResults();
        }
        DLog.d(TAG,"QuickBatteryRequired:"+ ProductFlowUtil.isQuickBatteryRequired() );
        setBatteryResultLog(resultHasBatteryPerformance);
        if(resultHasBatteryPerformance){
            battery_result_heading.setVisibility(View.GONE);
            battery_result_status.setVisibility(View.GONE);
            layout_battery_health.setVisibility(View.GONE);
            setBatteryPerformanceResults();
        } else if (ProductFlowUtil.isQuickBatteryRequired()) {
            battery_result_heading.setVisibility(View.GONE);
            battery_result_status.setVisibility(View.GONE);
            layout_battery_health.setVisibility(View.GONE);
            getBatteryResults();
        } else if(Util.isBatteryQuestionaireRequired()) {
            updateBatteryCheckResults();
        } else {
            battery_result_heading.setVisibility(View.GONE);
            battery_result_status.setVisibility(View.GONE);
            layout_battery_health.setVisibility(View.GONE);
            quick_battery_heading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != Activity.RESULT_OK && data!=null){

            switch (requestCode){
                case RANErrorActivity.RC_RAN:
                     Util.ranNumber = data.getStringExtra(RANErrorActivity.EXT_RAN_NUMBER);
                     ranValue.setText(data.getStringExtra(RANErrorActivity.EXT_RAN_NUMBER));
                     uploadResultToSerever(-1);
                    break;
            }
        }
    }



    private void startResolution(String resolution) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        String result = TestResult.CANBEIMPROVED;
        if(testResult.containsKey(resolution)) {
            TestInfo testInfo = (TestInfo) testResult.get(resolution);
            result = testInfo.getTestResult();
        }
        if(TestResult.FAIL.equalsIgnoreCase(result)){
            intent = new Intent(this, ResolutionsEducationalActivity.class);
            bundle.putString("TestResult", result);
            intent.putExtras(bundle);

        } else if(TestResult.ACCESSDENIED.equalsIgnoreCase(result)){
            intent = new Intent(this, ResolutionsEducationalActivity.class);
            bundle.putString("TestResult", result);
            intent.putExtras(bundle);

        } else {
            isTransactionUpdateRequired = true;
            switch (resolution) {
                case ResolutionName.DUPLICATE:
                case ResolutionName.IMAGES:
                case ResolutionName.MUSIC:
                case ResolutionName.VIDEO:
                    intent = new Intent(this, StorageResolutionsActivity.class);
                    break;
                case ResolutionName.ADWAREAPPS:
                case ResolutionName.MALWAREAPPS:
                case ResolutionName.RISKYAPPS:
                case ResolutionName.FOREGROUND_APPS:
                case ResolutionName.BACKGROUND_APPS:
                case ResolutionName.AUTOSTART_APPS:
                case ResolutionName.UNUSEDAPPS:
                case ResolutionName.OUTDATEDAPPS:
                    intent = new Intent(this, AppResolutionsActivity.class);
                    break;
                case ResolutionName.BLUETOOTH_ON:
                case ResolutionName.BLUETOOTH_OFF:
                    intent = new Intent(this, BluetoothResolutionActivity.class);
                    break;
                case ResolutionName.BRIGHTNESS:
                    intent = new Intent(this, BrightnessResolutionActivity.class);
                    break;
                case ResolutionName.GPS_ON:
                case ResolutionName.GPS_OFF:
                    intent = new Intent(this, GpsResolutionActivity.class);
                    break;
                case ResolutionName.NFC_ON:
                case ResolutionName.NFC_OFF:
                    intent = new Intent(this, NfcResolutionActivity.class);
                    break;
                case ResolutionName.SCREEN_TIMEOUT:
                    intent = new Intent(this, ScreenTimeOutResolutionActivity.class);
                    break;
                case ResolutionName.WIFI_ON:
                    intent = new Intent(this, WifiResolutionActivity.class);
                    break;
                case ResolutionName.LIVEWALLPAPER:
                    intent = new Intent(this, LivewallpaperResolutionActivity.class);
                    break;
                case Resolution.FIRMWARE:
                case Resolution.SIM_CARD:
                case ResolutionName.LASTRESTART:
                    intent = new Intent(this, ResolutionsEducationalActivity.class);
                    break;
                case Resolution.QUICKBATTERYTEST:
                    intent = new Intent(this, ResolutionsEducationalActivity.class);
                    break;
                default:
                    isTransactionUpdateRequired = false;
                    break;
            }
        }
        if (intent != null) {
            intent.putExtra(TEST_NAME, resolution);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBackPressed() {
        if(!isAssistedApp && !Util.needToRemoveBackButton())
        goHome();
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.result_text);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_result;
    }

    private void goHome(){
        updateHistory();
        if(isTransactionUpdateRequired) {
            uploadResultToSerever(ExitSummary);
        }
        else {
            Intent intent = new Intent(ResultsActivity.this, EndingSessionActivity.class);
            startActivity(intent);
        }/*        Intent intent = new Intent(ResultsActivity.this, EndingSessionActivity.class);
        startActivity(intent);*/
    }

    private void setBatteryResultLog(boolean hasBatteryPerformance) {
        DLog.d(TAG,"  ");
        DLog.d(TAG, "================= All Battery Test Results =================");
        DLog.d(TAG, "  ");

        DLog.d(TAG,"::::::::::::::Battery Api Test Result::::::::::::::");
        DLog.d(TAG, "Test Result: "+BatteryUtil.getBatteryHealthByAndroidAPI(this));
        DLog.d(TAG, "Result Info: \n"+BatteryUtil.getBatteryStatFromApi(this).toString());
        DLog.d(TAG,"::::::::::::::BatteryApiTest Result End::::::::::::::");
        DLog.d(TAG,"  ");

        DLog.d(TAG,"::::::::::::::Quick Battery Test Result::::::::::::::");
        BatteryDiagConfig batteryDiagConfig = new BatteryDiagConfig.BatteryDiagConfigBuilder(true).build();
        ActivityResultInfo activityResultInfo = new QuickTestComputeEngine(
                batteryDiagConfig, BatteryUtil.getBatteryCapacity(context)).computeQuickTestSoh();
        DLog.d(TAG, "Test Result: "+activityResultInfo.getTestResult().name());
        DLog.d(TAG, "Quick Test Possible: "+ BatteryTest.getInstance().isQuickTestPossible(this));
        DLog.d(TAG, "Result Info: \n"+activityResultInfo.toString());
        DLog.d(TAG,"::::::::::::::QuickBatteryTest Result End::::::::::::::");
        DLog.d(TAG,"  ");

        if(hasBatteryPerformance) {
             BatteryPerformanceResult batteryTestResult = BatteryPerformanceResult.getInstance();
             DLog.d(TAG, "::::::::::::::Battery Performance Test Result::::::::::::::");
             DLog.d(TAG, "Test Result: " + batteryTestResult.getBatteryResult());
             DLog.d(TAG, "Battery Health: " + batteryTestResult.getBatteryHealth());
             DLog.d(TAG, "Result Info: ");
             DLog.d(TAG, "Result Code: " + batteryTestResult.getResultCode());
             DLog.d(TAG, "Error Code: " + batteryTestResult.getErrorCode());
             DLog.d(TAG, "BatterySoHByE: " + batteryTestResult.getBatterySohByE());
             DLog.d(TAG, "BatterySoHByT: " + batteryTestResult.getBatterySohByT());
             DLog.d(TAG, "BatterySoH: " + batteryTestResult.getBatterySOH());
             DLog.d(TAG, "BatteryCalculatedCapacity: " + batteryTestResult.getBatteryCalculatedCapacity());
             DLog.d(TAG, "BatteryDesignCapacity: " + batteryTestResult.getBatteryDesignCapacity());
             DLog.d(TAG, "BatteryConfig: " + batteryTestResult.getBatteryConfig());
             DLog.d(TAG, "TestProfile: " + batteryTestResult.getBatteryTestProfile());
             DLog.d(TAG, "::::::::::::::BatteryPerformanceTest Result End::::::::::::::");
         } else {
            DLog.d(TAG, "::::::::::::::BatteryPerformanceTest Result::::::::::::::");
            DLog.d(TAG, "Test Result: NA");
         }
        DLog.d(TAG,"  ");

        DLog.d(TAG, "================= All Battery Test Results =================");
        DLog.d(TAG,"  ");
    }

    private void setBatteryPerformanceResults() {
        quick_battery_heading.setText(R.string.battery_performance_results);
        if(BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_PASS ||
            BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_FAIL) {
             layout_battery_info.setVisibility(View.VISIBLE);
             design_capacity_layout.setVisibility(View.VISIBLE);

             if(BatteryPerformanceResult.getInstance().getBatterySohByE() > BatteryPerformanceResult.getInstance().getBatterySohByT())
             actual_capacity_layout.setVisibility(View.VISIBLE);

             charging_level_layout.setVisibility(View.VISIBLE);
             if(BatteryTestResult.healthVeryGood.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth()))
                 batteryHealth.setText(R.string.vgood);
             else if(BatteryTestResult.healthGood.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth()))
                 batteryHealth.setText(R.string.good);
             else if(BatteryTestResult.healthBad.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth()))
                 batteryHealth.setText(R.string.bad);
             else
                 batteryHealth.setText(R.string.unknown);
             soh.setText((int)BatteryPerformanceResult.getInstance().getBatterySOH()+"%");
             designCapacity.setText("" + (int) BatteryPerformanceResult.getInstance().getBatteryDesignCapacity());
             actualCapacity.setText("" + (int) BatteryPerformanceResult.getInstance().getBatteryCalculatedCapacity());
             charging_level.setText("" + BatteryPerformanceResult.getInstance().getCurrentBatteryLevel(this));
             temperature_value.setText(""+BatteryUtil.getBatteryTemperature(this));
         } else {
             getBatteryResults();
         }
    }
        private void getBatteryResults() {
            quick_battery_heading.setText(R.string.quick_battery_results);
            DLog.d(TAG,"getBatteryResults...");
        String batteryHealthFromApi = BatteryUtil.getBatteryHealthByAndroidAPI(OruApplication.getAppContext());
        QuickBatteryTestInfo quickBatteryData = PervacioTest.getInstance().getQuickBatteryTestInfo();

        if(quickBatteryData == null) {
            layout_battery_info.setVisibility(View.VISIBLE);
            design_capacity_layout.setVisibility(View.VISIBLE);
            charging_level_layout.setVisibility(View.VISIBLE);
            //batteryHealth.setText(quickBatteryData.getBattryHealth());
            designCapacity.setText(""+BatteryUtil.getBatteryCapacity(this));
            //designCapacity.setVisibility(View.GONE);
            actualCapacity.setVisibility(View.GONE);
            charging_level.setText(""+BatteryUtil.getCurrentBatteryLevel(this));
            temperature_value.setText(""+BatteryUtil.getBatteryTemperature(this));
            //charging_level.setVisibility(View.GONE);
            soh.setVisibility(View.GONE);
            soh_layout.setVisibility(View.GONE);

            //soh.setText(R.string.not_applicable);

            if("BATTERY HEALTH GOOD".equalsIgnoreCase(batteryHealthFromApi)) {
                batteryHealth.setText(R.string.vgood);
            } else if("BATTERY HEALTH UNKNOWN".equalsIgnoreCase(batteryHealthFromApi)) {
                batteryHealth.setText(R.string.unknown);
            } else {
                batteryHealth.setText(R.string.bad);
            }
            return;
        }

        DLog.d(TAG, "Battery Health% " +quickBatteryData.getBatteryHealth() + "  :  "
                + (int) quickBatteryData.getBatterySOH() + "%");

        if ("UNSUPPORTED".equalsIgnoreCase(quickBatteryData.getBatteryHealth()) ||
            getString(R.string.unsupported).equalsIgnoreCase(quickBatteryData.getBatteryHealth())) {
            layout_battery_info.setVisibility(View.VISIBLE);
            design_capacity_layout.setVisibility(View.VISIBLE);
            charging_level_layout.setVisibility(View.VISIBLE);
            soh_layout.setVisibility(View.VISIBLE);

            batteryHealth.setText(quickBatteryData.getBatteryHealth());
            //designCapacity.setVisibility(View.GONE);
            designCapacity.setText("" + BatteryUtil.getBatteryCapacity(this));
            actualCapacity.setVisibility(View.GONE);
            charging_level.setText("" + BatteryUtil.getCurrentBatteryLevel(this) + "%");
            temperature_value.setText(""+BatteryUtil.getBatteryTemperature(this));
            //charging_level.setVisibility(View.GONE);
            soh_layout.setVisibility(View.GONE);
            soh.setVisibility(View.GONE);
            //soh.setText(R.string.not_applicable);
            if("BATTERY HEALTH GOOD".equalsIgnoreCase(batteryHealthFromApi)) {
                batteryHealth.setText(R.string.vgood);
            } else if("BATTERY HEALTH UNKNOWN".equalsIgnoreCase(batteryHealthFromApi)) {
                batteryHealth.setText(R.string.unknown);
            } else {
                batteryHealth.setText(R.string.bad);
            }
        } else {
            layout_battery_info.setVisibility(View.VISIBLE);
            design_capacity_layout.setVisibility(View.VISIBLE);
            charging_level_layout.setVisibility(View.VISIBLE);
            soh_layout.setVisibility(View.VISIBLE);

            batteryHealth.setText(quickBatteryData.getBatteryHealth());
            designCapacity.setText("" + BatteryUtil.getBatteryCapacity(this));
            actualCapacity.setText("" + quickBatteryData.getBatteryFullChargeCapacity());
            if (quickBatteryData.isSOHFromCondition()){
                //soh.setText(R.string.not_applicable);
                soh_layout.setVisibility(View.GONE);
            }else {
                int qSoh = (int)(quickBatteryData.getBatterySOH()>100?100:quickBatteryData.getBatterySOH()); //100 CAP for SOH
                soh.setText(qSoh + "%");
            }
            charging_level.setText("" + BatteryUtil.getCurrentBatteryLevel(this) + "%");
            temperature_value.setText(""+BatteryUtil.getBatteryTemperature(this));
        }
    }

    private void updateDeviceInfo() {
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        DeviceInfo mDeviceInfo = DeviceInfo.getInstance(this);
        make_value.setText(mDeviceInfo.capitalizeFirstLetter(mDeviceInfo.get_make()));
        model_value.setText(globalConfig.getDeviceModelName());
        String imei = mDeviceInfo.get_imei();
        if(imei == null || imei.isEmpty())
            imei=getResources().getString(R.string.not_available);
        imei_value.setText(imei);
        firmware_value.setText(mDeviceInfo.getFirmwareVersion());
        os_version_value.setText("Android "+mDeviceInfo.get_version());

        String timestamp =   BaseUtils.DateUtil.format(GlobalConfig.getInstance().getSessionStartTime());
        start_time_value.setText(timestamp);

        if(isResultUploadSuccess) {
            updateSessionId();
        } else {
            sessin_id_value.setText("NA");
        }
    }

    private void updateSessionId() {
        Drawable image = context.getResources().getDrawable( R.drawable.ic_copytoclipboard );
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds( 0, 0, w, h );
        sessin_id_value.setCompoundDrawables( null, null, image, null );
        sessin_id_value.setText(String.valueOf(GlobalConfig.getInstance().getSessionId()));
        sessin_id_value.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (sessin_id_value.getRight() - sessin_id_value.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        ClipboardManager clipboard = (ClipboardManager) ResultsActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("copied text", String.valueOf(GlobalConfig.getInstance().getSessionId()));
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.session_id_copied_msg), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void updateFivePointCheckResults() {
        if (PervacioTest.getInstance().getFivePointCheckResultMap().size() > 0 && Util.isFivePointCheckRequired()) {
            if (!PervacioTest.getInstance().getFivePointCheckResultMap().containsValue(true)) {
                TextView textView = (TextView) findViewById(R.id.none_selected);
                textView.setVisibility(View.VISIBLE);
                return;
            }
            for (String fivePointCheckName : globalConfig.getFivePointCheckList().keySet()) {
//                list_item = inflater.inflate(R.layout.five_point_result_layout, null);
//                result_Observation = (TextView) list_item.findViewById(R.id.five_point_check_result);
//                result_Display_Name = (TextView) list_item.findViewById(R.id.five_point_check_name);
                if (PervacioTest.getInstance().getFivePointCheckResultMap().get(fivePointCheckName)) {
                    result_Display_Name.setText(fivePointCheckName);
                    result_Observation.setText(R.string.str_yes);
                    layout_five_point_check.addView(list_item);
                }
            }
        } else {
            layout_five_point_check.setVisibility(View.GONE);
            five_point_check_result_heading.setVisibility(View.GONE);
        }

    }

    private void updatePhysicalDamageCheckResults() {
        if (PervacioTest.getInstance().getPhysicalDamageResultMap().size() > 0) {
            five_point_check_result_heading.setText(getResources().getString(R.string.phy_dmg_result));
            for (String physicalDamageCheckName : PervacioTest.getInstance().getPhysicalDamageResultMap().keySet()) {
//                list_item = inflater.inflate(R.layout.five_point_result_layout, null);
//                result_Observation = (TextView) list_item.findViewById(R.id.five_point_check_result);
//                result_Display_Name = (TextView) list_item.findViewById(R.id.five_point_check_name);
                result_Display_Name.setText(physicalDamageCheckName);
                result_Observation.setText(PervacioTest.getInstance().getPhysicalDamageResultMap().get(physicalDamageCheckName) ? R.string.str_yes : R.string.str_no);
                layout_five_point_check.addView(list_item);
            }
        } else {
            five_point_check_result_heading.setVisibility(View.GONE);
            layout_five_point_check.setVisibility(View.GONE);
            five_point_check_result_heading.setVisibility(View.GONE);
        }

    }

    Dialog dialog;
    ProgressDialog progressDialog;

//    private void showSaveSummaryDialog() {
//        dialog = new Dialog(ResultsActivity.this);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        }
//        dialog.setContentView(R.layout.summary_image_download_layout);
//        final EditText emailEditText = (EditText) dialog.findViewById(R.id.enter_email_edit_Text);
//        final TextView submitButton = (TextView) dialog.findViewById(R.id.submit_button);
//        final TextView invalidEmailText = (TextView) dialog.findViewById(R.id.invalid_email);
//        final RadioButton emailRadioButton = (RadioButton) dialog.findViewById(R.id.email_Radio_button);
//        final RadioButton saveToDeviceRadioButton = (RadioButton) dialog.findViewById(R.id.save_to_device_radio_button);
//        saveToDeviceRadioButton.setChecked(true);
//        emailRadioButton.setEnabled(false);
//        emailRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DLog.i(TAG, "emailRadioButton clicked");
//            }
//        });
//        saveToDeviceRadioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                emailRadioButton.setChecked(false);
//                emailEditText.getText().clear();
//                invalidEmailText.setVisibility(View.GONE);
//                DLog.i(TAG, "saveToDeviceRadioButton clicked");
//            }
//        });
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (emailRadioButton.isChecked()) {
//                   /* String emailEntered = emailEditText.getText().toString();
//                    if (isEmailValid(emailEntered)) {
//
//                    } else {
//                        invalidEmailText.setVisibility(View.VISIBLE);
//                        emailEditText.getBackground().setColorFilter(getResources().getColor(R.color.manual_test_fail),
//                                PorterDuff.Mode.SRC_ATOP);
//                    }*/
//                } else if (saveToDeviceRadioButton.isChecked()) {
//                    dialog.dismiss();
//                    getSummaryImage();
//                }
//            }
//        });
//        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
//    }

    private void getSummaryImage() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(ResultsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setTitle(getResources().getString(R.string.saving_summary_image));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        SummaryType summaryType= new SummaryType();
        summaryType.setSessionId(GlobalConfig.getInstance().getSessionId());
        summaryType.setLocale(getResources().getString(R.string.locale));
        Call<TransactionResponse> summaryImageCall = ODDNetworkModule.getInstance().getDiagServerApiInterface().getSummaryImage(summaryType);
        summaryImageCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                TransactionResponse imageResponse = response.body();
                if (imageResponse != null && !TextUtils.isEmpty(imageResponse.getData())) {
                    boolean saveStatus = AppUtils.saveSummaryFileToStorageWireless(getApplicationContext(), imageResponse.getData());
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if(saveStatus) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_summary_image), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_save_denied), Toast.LENGTH_SHORT).show();
                    }

                    DLog.d(TAG, "data: " + imageResponse.getData());
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    PervacioTest.getInstance().setSummaryResult(TestResult.PASS);
                } else {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_save), Toast.LENGTH_SHORT).show();
                    PervacioTest.getInstance().setSummaryResult(TestResult.FAIL);
                }
                PervacioTest.getInstance().updateSession();
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                DLog.e(TAG, "Exception while getObjectFromData" + t.getMessage());
                if(saveImageRetryCount < 2) {
                    saveImageRetryCount = saveImageRetryCount + 1;
                    saveImageHandler.sendEmptyMessageDelayed(0,10000);
                } else {
                    saveImageRetryCount = 0;
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_save), Toast.LENGTH_SHORT).show();
                    PervacioTest.getInstance().setSummaryResult(TestResult.FAIL);
                    PervacioTest.getInstance().updateSession();
                }

            }
        });
    }

    Handler saveImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            saveImageHandler.removeMessages(0);
            getSummaryImage();
        }
    };

    private void updateBatteryCheckResults() {
        String batteryFinalResult = PervacioTest.getInstance().getBatteryFinalResult();
        switch (batteryFinalResult) {
            case "Replacement Not Needed":
                battery_result_status.setText(R.string.replace_not_needed);
                battery_result_status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_passed, 0, 0, 0);
                break;
            case "Consider Replacing Battery":
                battery_result_status.setText(R.string.consider_replace);
                battery_result_status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.results_warning, 0, 0, 0);
                break;

            case "Replace Battery":
                battery_result_status.setText(R.string.replace_battery);
                battery_result_status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_failed, 0, 0, 0);
                break;
            default:

        }

        for (String name : PervacioTest.getInstance().getBatteryCheckResultMap().keySet()) {
//            list_item = inflater.inflate(R.layout.five_point_result_layout, null);
//            result_Observation = (TextView) list_item.findViewById(R.id.five_point_check_result);
//            result_Display_Name = (TextView) list_item.findViewById(R.id.five_point_check_name);
            result_Display_Name.setText(name);
            result_Observation.setText(PervacioTest.getInstance().getBatteryCheckResultMap().get(name));
            layout_battery_health.addView(list_item);
        }
    }

    private BroadcastReceiver mTestResultsUploadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mResultUploadProgress.setVisibility(View.GONE);
            done_tv.setEnabled(true);
            LocalBroadcastManager.getInstance(ResultsActivity.this).unregisterReceiver(mTestResultsUploadedReceiver);
            int result = intent.getIntExtra("result", 0);
            DLog.d(TAG,"response : "+result);
            if (!Util.needToRemoveSendSummaryButton()) {
                if(result == 1) {
                    updateSessionId();
                    send_summary.setEnabled(true);
                    isResultUploadSuccess = true;
                    if(requestFrom == MailSummary){
                       // Intent emailSummary = new Intent(ResultsActivity.this, EmailSummaryChildUnusedActivity.class);
                   //     startActivity(emailSummary);
                    } else if(requestFrom == ImageSummary){
                        getSummaryImage();
                    } else if(requestFrom == ExitSummary){
                        Intent endingSession;
                        if(Util.showSessionIdEndScreen()){
                             endingSession = new Intent(ResultsActivity.this, SessionIdEndScreen.class);
                        } else {
                             endingSession = new Intent(ResultsActivity.this, EndingSessionActivity.class);
                        }
                        startActivity(endingSession);
                    }
                    requestFrom = -1;
                    isTransactionUpdateRequired = false;
                } else {
                    showErrorDialog(requestFrom, RESULT_UPLOAD_REQ, ERROR_TO_CONNECT_SEREVER);
                }
            }
        }
    };

}
