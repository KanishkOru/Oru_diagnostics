package com.oruphones.nativediagnostic.result;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.BaseUnusedActivity;
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
import com.oruphones.nativediagnostic.controller.summery.SummeryContentView;
import com.oruphones.nativediagnostic.controller.summery.newResultsVievHandler;
import com.oruphones.nativediagnostic.models.AdditionalInfoTest;
import com.oruphones.nativediagnostic.models.DeviceInfoDataSet;
import com.oruphones.nativediagnostic.models.SummaryDisplayElement;
import com.oruphones.nativediagnostic.models.SummeryDataSet;
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
import com.oruphones.nativediagnostic.util.CustomRatingsDialog;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.PreferenceHelper;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pervacio on 11-09-2017.
 */

public class ResultsSummeryActivity extends BaseActivity {

    private ScrollView summeryScreenScrollView;
    private LinearLayout summeryScreenContentContainer;
    private ProgressBar mResultUploadProgress;
    private static String TAG = ResultsSummeryActivity.class.getSimpleName();


    private Button done_tv, send_summary;
    private ImageButton ib_retry;
    private LinearLayout ll_internet_unavailable_retry;

    LayoutInflater inflater;


    private HashMap mTestResult;
    public boolean mShowRanScreen;
    private int RESULT_UPLOAD_REQ = 1;
    private int NET_UNAVAILABLE = 3;
    private int ERROR_TO_CONNECT_SEREVER = 4;
    private boolean isResultUploadSuccess = false;
    private int saveImageRetryCount = 0;
    ProgressDialog progressDialogServerConnect;
    private final int MailSummary = 0;
    private final int ImageSummary = 1;
    private final int ExitSummary = 2;
    private final int TRADE_IN_ACCEPTED = 3;
    private boolean isTransactionUpdateRequired = true;
    private PreferenceHelper prefsHelper;

    private GlobalConfig mGlobalConfig;
    private boolean resultHasBatteryPerformance;
    private ProgressDialog progressDialog;


    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ResultsSummeryActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected boolean setHomeButton() {
        /*if(ProductFlowUtil.isTradein()) {
            return false;
        } else {
            return true;
        }*/
        return false;
    }

    @Override
    protected boolean showExitButton() {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = LayoutInflater.from(getApplicationContext());
        summeryScreenContentContainer = findViewById(R.id.summeryScreenContentContainer);
        summeryScreenScrollView = findViewById(R.id.summeryScreenScrollView);
        mResultUploadProgress = findViewById(R.id.result_upload_progress);
        done_tv = (Button) findViewById(R.id.footerDoneBt);
        send_summary = findViewById(R.id.send_summary);
        findViewById(R.id.bottomLayout).setVisibility(BaseUnusedActivity.isIsAssistedApp() ? View.GONE : View.VISIBLE);

        prefsHelper =  PreferenceHelper.getInstance(getApplicationContext());

        String EarpieceInput1 =  convertListtoString(globalConfig.getTestIntegers("Earpiece User Input"));
        String EarphoneInput1 = convertListtoString(globalConfig.getTestIntegers("Earphone User Input"));
        String SpeakerInput1 = convertListtoString(globalConfig.getTestIntegers("Speaker User Input"));


        if (!Objects.equals(EarphoneInput1, "[]")){
            globalConfig.addItemToList("Earphone User Input: "+EarphoneInput1);
        }
        if (!Objects.equals(SpeakerInput1, "[]")){
            globalConfig.addItemToList("Speaker User Input: "+ SpeakerInput1);
        }
        if (!Objects.equals(EarpieceInput1, "[]")){
            globalConfig.addItemToList("Earpiece User Input: "+EarpieceInput1);
        }


        List<String> ResultTestList1 = globalConfig.getItemList();
    //    copyToClipboard(ResultTestList1);

        DLog.d(TAG, "ManualTestResultList11111111: "+ ResultTestList1);
        //Show RAN ON Screen
        mShowRanScreen = !PervacioTest.getInstance().getResults(TestResult.FAIL).isEmpty()
                && GlobalConfig.getInstance().isGenerateRAN();
        if (mShowRanScreen) {
            RANErrorActivity.openForResult(ResultsSummeryActivity.this, false);
        }

        ll_internet_unavailable_retry = findViewById(R.id.internet_unavailable_retry);
        ib_retry = findViewById(R.id.iv_retry);


        //  Commentd : This is handled using SummaryDisplayElement
        /*if (Util.showInfoAtMiddle()) {
            layout_fix_result_info.removeView(layout_detailedResults);
            layout_fix_result_info.removeView(layout_info);

            layout_fix_result_info.addView(layout_info, 1);
            layout_fix_result_info.addView(layout_detailedResults, 2);
        }*/

        if (Util.enableSendToRepair()) {
            done_tv.setText(R.string.send_to_repair);
        }

        done_tv.setEnabled(false);
        done_tv.setClickable(true);
        done_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.enableSendToRepair()) {
                    //RestrictionCheckUnusedActivity.startActivity(ResultsSummeryActivity.this, DeviceInfo.getInstance(ResultsSummeryActivity.this).get_imei());

                } else {
                    if (Util.showSessionIdEndScreen() && isResultUploadSuccess) {
                        updateHistory();
                        if (isTransactionUpdateRequired) {
                            uploadResultToSerever(ExitSummary);
                        } else {
                            Intent intent = new Intent(ResultsSummeryActivity.this, SessionIdEndScreen.class);
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

        if (Util.sendSummaryEmail()) {
            send_summary.setText(R.string.email_summary);
        }

        if (ProductFlowUtil.isTradein()) {
            send_summary.setText(R.string.str_next);
            done_tv.setVisibility(View.GONE);
        }
        done_tv.setVisibility(View.GONE);
        send_summary.setText(R.string.go_back_home);

        send_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
                /*if(ProductFlowUtil.isTradein()){
                    if(!isAnyTestFailed()) {
                        showAcceptAndRejectPopup();
                    } else {
                       goHome();
                    }

                } else if (Util.sendSummaryEmail()) {
                    if (isTransactionUpdateRequired) {
                        uploadResultToSerever(MailSummary);
                    } else {
                        EmailSummaryChildActivity.startActivity(ResultsSummeryActivity.this);

                    }
                } else {
                    if (isTransactionUpdateRequired) {
                        uploadResultToSerever(ImageSummary);
                    } else {
                        getSummaryImage();
                    }

                }*/
            }
        });


        if (BaseUnusedActivity.isIsAssistedApp()) {
            done_tv.setEnabled(false);
        }

        ll_internet_unavailable_retry.setVisibility(View.GONE);
        send_summary.setEnabled(isOnline());
        ManualTest.getInstance(this).manualTestDone();
        if (ProductFlowUtil.isTradein()) {
            PervacioTest.getInstance().setSessionStatus(isAnyTestFailed() ? "Trade_In_check_Failed" : "Trade_In_check_Passed");
        } else {
            PervacioTest.getInstance().setSessionStatus("Success");
        }

        uploadResultToSerever(-1);

        ib_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    ll_internet_unavailable_retry.setVisibility(View.GONE);
                    send_summary.setEnabled(true);
                } else {
                    progressDialogServerConnect = new ProgressDialog(ResultsSummeryActivity.this, AlertDialog.THEME_HOLO_LIGHT);
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
        if (!Resolution.getInstance().getAvailableResolutionsList().isEmpty() && !ProductFlowUtil.isTradein())
            showUserMessage(R.string.result_user_message);
    }

//    private void copyToClipboard(List<String> resultTestList) {
//        String textToCopy = TextUtils.join("\n", resultTestList);
//
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//
//        ClipData clip = ClipData.newPlainText("ResultTestList", textToCopy);
//
//        clipboard.setPrimaryClip(clip);
//
//       // Toast.makeText(this, "Result Data copied to clipboard", Toast.LENGTH_SHORT).show();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private String convertListtoString(List<Integer> itemList){
        String userInputString = new Gson().toJson(itemList);
        return userInputString;
    }
    private void disableButtons() {
        done_tv.setEnabled(false);
        send_summary.setEnabled(false);
    }

    private void enableButtons() {
        done_tv.setEnabled(true);
        send_summary.setEnabled(true);
    }

//    private void showAcceptAndRejectPopup() {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.tradein_accept_reject_dialog);
//        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//
//        Button acceptBtn = (Button) dialog.findViewById(R.id.accept_btn);
//        Button rejectBtn = (Button) dialog.findViewById(R.id.reject_btn);
//
//        acceptBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//                PervacioTest.getInstance().setSessionStatus("TradeIn_Accepted");
//                uploadResultToSerever(TRADE_IN_ACCEPTED);
//                //RestrictionCheckActivity.startActivity(ResultsSummeryActivity.this, DeviceInfo.getInstance(ResultsSummeryActivity.this).get_imei());
//            }
//        });
//
//        rejectBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//                PervacioTest.getInstance().setSessionStatus("TradeIn_Rejected");
//                uploadResultToSerever(ExitSummary);
//                /*Intent intent = new Intent(ResultsSummeryActivity.this, SessionIdEndScreen.class);
//                startActivity(intent);*/
//                //goHome();
//            }
//        });
//
//        dialog.show();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//
//    }

    AlertDialog mDialog = null;

    private void showUserMessage(final int message_id) {
        if (mDialog != null)
            mDialog.dismiss();

        CommonUtil.DialogUtil.twoButtonDialog(this, getString(R.string.alert), getString(message_id),
                new String[]{getString(R.string.str_ok)}, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Handler handler = new Handler(Looper.myLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new CustomRatingsDialog(ResultsSummeryActivity.this).getInstance().createRatingsDialog();
                            }
                        }, 1000);
                        //manualTestResultDialog(TestName.FINGERPRINTSENSORTEST,TestResult.SKIPPED);
                    }
                }, null
        );



        /*AlertDialog.Builder builder = new AlertDialog.Builder(ResultsSummeryActivity.this);
        builder.setTitle(R.string.alert)
                .setMessage(message_id)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mDialog != null)
                            mDialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();*/
    }

    private int requestFrom = -1;

    private void uploadResultToSerever(int request) {
        requestFrom = request;
        if (isOnline()) {
            disableButtons();
            mResultUploadProgress.setVisibility(View.VISIBLE);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mTestResultsUploadedReceiver, new IntentFilter("org.pervacio.wirelessapp.TEST_RESULTS_UPDATED"));
            pervacioTest.updateSession();
        } else {
            showErrorDialog(requestFrom, RESULT_UPLOAD_REQ, NET_UNAVAILABLE);
        }
    }

    private void showErrorDialog(final int requestFrom, final int reqCode, final int errorCode) {
        DLog.e(TAG, "showErrorDialog: reqCode : " + reqCode + " errorCode :: " + errorCode);

        int titleId;
        int messageId;
        int possitveBtnId;
        int negativeBtnId;

        DialogInterface.OnClickListener negativeListener = null;
        if (errorCode == NET_UNAVAILABLE) {
            titleId = R.string.internet_not_available;
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
            titleId = R.string.server_not_rechable;
            messageId = R.string.server_unavailable_msg;
            possitveBtnId = R.string.str_ok;
            negativeBtnId = R.string.action_cancel;
        }

        alertDialog = CommonUtil.DialogUtil.getAlert(this, getString(titleId), getString(messageId), getString(possitveBtnId), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (errorCode == NET_UNAVAILABLE) {
                    alertDialog.dismiss();
                    done_tv.setEnabled(true);
                    if (reqCode == RESULT_UPLOAD_REQ) {
                        uploadResultToSerever(requestFrom);
                    } else {
                        uploadResultToSerever(requestFrom);
                        //getSummaryImage();
                    }

                } else {
                    alertDialog.dismiss();
                }
            }
        }, getString(negativeBtnId), negativeListener);
        alertDialog.show();
    }

    private void setDetailView() {
        mGlobalConfig = GlobalConfig.getInstance();
        int childCount = summeryScreenContentContainer.getChildCount();

//        for (int i = 0; i <childCount ; i++) {
//            View childView = summeryScreenContentContainer.getChildAt(i);
//
//            int currView = childView.getId();
//            if (currView!=excludeViewid)
//            {
//              //  summeryScreenContentContainer.removeView(childView);
//            }
//        }

        View excludeViewid = findViewById(R.id.results_heading);
        summeryScreenContentContainer.removeAllViews();
        summeryScreenContentContainer.addView(excludeViewid);

        if (ProductFlowUtil.isTradein()) {
            addTradeInEligibleView();
        }
        for (SummaryDisplayElement summaryDisplayElement : mGlobalConfig.getSummaryDisplayElements()) {
            int title = -1;
            List<DeviceInfoDataSet> dataSetList = null;
            View.OnClickListener clickListener = null;
            switch (summaryDisplayElement) {
                case TestResults:
                    title = R.string.detailed_result;
                    dataSetList = results();
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        newResultsVievHandler summeryContentView = new newResultsVievHandler(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);
                    }
                    clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag() != null) {
                                String additionalData = (String) v.getTag();
                                String additiionalData[] = additionalData.split("&");
                                showAlertDialog(additiionalData[0], additiionalData[1]);
                            }
                        }
                    };
                    break;
                case DeviceInfo:
                    title = R.string.title_activity_device_info;
                    dataSetList = updateDeviceInfo();
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        newResultsVievHandler summeryContentView = new newResultsVievHandler(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);
                    }

                    break;

                case PhysicalDamage:
                    title = R.string.phy_dmg_result;
                    dataSetList = updatePhysicalDamageCheckResults();
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        SummeryContentView summeryContentView = new SummeryContentView(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);
                    }
                    break;
                case SuggestedFixes:
                    title = R.string.suggested_fixes;
                    if (!BaseUnusedActivity.isIsAssistedApp()) {
                        clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.getTag() != null) {
                                    startResolution((String) v.getTag());
                                }
                            }
                        };
                    }
                    dataSetList = resolutions(clickListener);
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        SummeryContentView summeryContentView = new SummeryContentView(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);

                    }
                    break;
                case FivePointCheck:
                    if (Util.isFivePointCheckRequired()) {
                        title = R.string.five_point_check_toolbar_name;
                        dataSetList = updateFivePointCheckResults();
                    }
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        SummeryContentView summeryContentView = new SummeryContentView(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);

                    }
                    break;
                case Notes:
                    title = R.string.notes_title;
                    if (!PervacioTest.getInstance().getNotesMessage().isEmpty()) {
                        dataSetList = new ArrayList<>();
                        dataSetList.add(new SummeryDataSet<>(PervacioTest.getInstance().getNotesMessage(), -1, null));
                    }

                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        SummeryContentView summeryContentView = new SummeryContentView(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);

                    }
                    break;
                case BatteryTest:
                    DLog.d(TAG, "Battery QuickBatteryRequired:" + ProductFlowUtil.isQuickBatteryRequired());
                    setBatteryResultLog(resultHasBatteryPerformance);
                    title = R.string.battery_results;
                    if (resultHasBatteryPerformance) {
                        if (BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_PASS ||
                                BatteryPerformanceResult.getInstance().getResultCode() == BatteryTestResult.RESULT_CODE_FAIL) {
                            dataSetList = batteryPerformanceResults();
                            title = R.string.battery_performance_results;
                        } else {
                            dataSetList = getBatteryResults();
                            title = R.string.quick_battery_results;
                        }
                    } else if (ProductFlowUtil.isQuickBatteryRequired()) {
                        dataSetList = getBatteryResults();
                        title = R.string.quick_battery_results;
                    } else if (Util.isBatteryQuestionaireRequired()) {
                        title = R.string.battery_health_check;
                        dataSetList = updateBatteryCheckResults();
                    }
                    //battery_result_status.setVisibility(showBatteryResultHeading?View.VISIBLE:View.GONE);
                    if (title > 0 && dataSetList != null && !dataSetList.isEmpty()) {
                        SummeryContentView summeryContentView = new SummeryContentView(this);
                        summeryContentView.setTitle(getString(title));
                        summeryContentView.setDisplayElement(summaryDisplayElement);
                        summeryContentView.updateView(dataSetList, clickListener);
                        summeryScreenContentContainer.addView(summeryContentView);

                    }
                    break;

            }


        }
    }

    private void addTradeInEligibleView() {
        List<DeviceInfoDataSet> eligibilityData = new ArrayList<>();
        String message = null;
        int titleColor = 0;
        if (isAnyTestFailed()) {
            message = getString(R.string.not_eligible_msg);
            titleColor = R.color.red;
        } else {
            message = getString(R.string.trdin_eligible_msg);
            titleColor = R.color.green;
        }
        SummeryDataSet<String> deviceInfoDataSet = new SummeryDataSet<>(message, -1, null);
        deviceInfoDataSet.setHideStatus(true);
        deviceInfoDataSet.setTitleColor(titleColor);
        eligibilityData.add(deviceInfoDataSet);

        SummeryContentView summeryContentView = new SummeryContentView(this);
        summeryContentView.setTitle(getString(R.string.device_aligibility));
        summeryContentView.setDisplayElement(SummaryDisplayElement.TradeInEligibility);
        summeryContentView.updateView(eligibilityData, null);
        summeryScreenContentContainer.addView(summeryContentView);
    }


    /*Resolutions */
    private List<DeviceInfoDataSet> resolutions(View.OnClickListener clickListener) {
        List<DeviceInfoDataSet> resolutions = null;
        if (!ProductFlowUtil.needToSkipResolutions()) {
            ArrayList<String> resolutionList = new ArrayList<String>();
            resolutionList.addAll(Resolution.getInstance().getAvailableResolutionsList());
            resolutions = new ArrayList<>();
            if (resolutionList.isEmpty()) {
                StringBuilder strEmptyMessage = new StringBuilder(getString(R.string.all_test_complete));
                strEmptyMessage.append("\n").append(getString(R.string.no_issues_found));
                SummeryDataSet<String> deviceInfoDataSet = new SummeryDataSet<>(strEmptyMessage.toString(), -1, null);
                deviceInfoDataSet.setHideStatus(true);
                resolutions.add(deviceInfoDataSet);
            } else {
                for (final String resolution : resolutionList) {
                    String resolutionDisplayName = GlobalConfig.getInstance().getTestDisplayName(resolution);
                    if (TextUtils.isEmpty(resolutionDisplayName)) {
                        Integer name = ODDUtils.resolutionNames.get(resolution);
                        if (name != null && name > 0)
                            resolutionDisplayName = getString(name);
                    }

                    // Drawable Icon
                    int drawableId = -1;
                    if (ODDUtils.resolutionImages.containsKey(resolution)) {
                        Integer drawable = ODDUtils.resolutionImages.get(resolution);
                        if (drawable != null)
                            drawableId = drawable;
                    }
                    SummeryDataSet<String> deviceInfoDataSet = new SummeryDataSet<>(resolutionDisplayName, drawableId, null);
                    //deviceInfoDataSet.setHideStatus(org.pervacio.wirelessapp.models.tests.ResolutionName.INTERNALSTORAGESUGGESTION.equalsIgnoreCase(resolution));
                    deviceInfoDataSet.setExtra(resolution);
                    resolutions.add(deviceInfoDataSet);
                }
            }
        }

        return resolutions;
    }

    private boolean isAnyTestFailed() {
        ArrayList<TestInfo> resultList = new ArrayList<TestInfo>();
        HashMap<String, TestInfo> testResult = PervacioTest.getInstance().getTestResult();
        testResult.putAll(PervacioTest.getInstance().getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());

        this.mTestResult = testResult;
        Iterator<Map.Entry<String, TestInfo>> it = testResult.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TestInfo> pair = (Map.Entry<String, TestInfo>) it.next();
            TestInfo testInfo = (TestInfo) pair.getValue();
            resultList.add(testInfo);
        }
        Collections.sort(resultList, new ResultComparator());

        for (TestInfo testInfo : resultList) {
            if (TestResult.FAIL.equals(testInfo.getTestResult())) {
                return true;
            }
        }

        return false;
    }

    private List<DeviceInfoDataSet> results() {
        ArrayList<TestInfo> resultList = new ArrayList<TestInfo>();
        HashMap<String, TestInfo> testResult = PervacioTest.getInstance().getTestResult();
        testResult.putAll(PervacioTest.getInstance().getAutoTestResult());
        testResult.putAll(PervacioTest.getInstance().getManualTestResult());

        this.mTestResult = testResult;
        Iterator<Map.Entry<String, TestInfo>> it = testResult.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TestInfo> pair = (Map.Entry<String, TestInfo>) it.next();
            TestInfo testInfo = (TestInfo) pair.getValue();
            resultList.add(testInfo);
        }
        Collections.sort(resultList, new ResultComparator());
        List<DeviceInfoDataSet> results = new ArrayList<>();
        for (TestInfo testInfo : resultList) {
            if (TestName.BATTERYPERFORMANCE.equalsIgnoreCase(testInfo.getName())) {
                resultHasBatteryPerformance = true;
                break;
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

            int drawable = -1;
            if (TestResult.CANBEIMPROVED.equals(testInfo.getTestResult())) {
                if (GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaO2UK")) {
                    drawable = R.drawable.results_optimizable_amber;
                } else {
                    drawable = R.drawable.ic_canbeimproved;
                }
            } else if (TestResult.PASS.equals(testInfo.getTestResult())) {
                drawable = R.drawable.success_test;
            } else if (TestResult.FAIL.equals(testInfo.getTestResult())) {
                drawable = R.drawable.fail_test;
            } else if (TestResult.OPTIMIZED.equals(testInfo.getTestResult())) {
                drawable = R.drawable.success_test;
            } else if (TestResult.NOTEQUIPPED.equals(testInfo.getTestResult())) {
                drawable = R.drawable.ic_not_equipped;
            } else if (TestResult.SKIPPED.equals(testInfo.getTestResult())) {
                drawable = R.drawable.ic_skipped;
            } else if (TestResult.ACCESSDENIED.equals(testInfo.getTestResult())) {
                drawable = R.drawable.ic_error;
            } else if (TestResult.NOTSUPPORTED.equals(testInfo.getTestResult())) {
                drawable = R.drawable.ic_notsupported;
            } else {
                drawable = R.drawable.ic_not_equipped;
            }

            SummeryDataSet<Object> deviceInfoDataSet = new SummeryDataSet<>(testInfo.getDisplayName(), drawable, CommonUtil.getMappedTestResult(testInfo.getTestResult()), testInfo.getTestAdditionalInfo());
            results.add(deviceInfoDataSet);
        }
        return results;
    }


    @Override
    protected void onResume() {
        super.onResume();

        mGlobalConfig = GlobalConfig.getInstance();
        setDetailView();
        //Remove the handler
        mInteractionMonitor.setIsEligibleForTimeOut(false, globalConfig.getUserInteractionSessionTimeOut());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case RANErrorActivity.RC_RAN:
                    Util.ranNumber = data.getStringExtra(RANErrorActivity.EXT_RAN_NUMBER);
                    uploadResultToSerever(-1);
                    break;
            }
        }
    }


    private void startResolution(String resolution) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        String result = TestResult.CANBEIMPROVED;
        if (mTestResult.containsKey(resolution)) {
            TestInfo testInfo = (TestInfo) mTestResult.get(resolution);
            result = testInfo.getTestResult();
        }
        if (TestResult.FAIL.equalsIgnoreCase(result)) {
            intent = new Intent(this, ResolutionsEducationalActivity.class);
            bundle.putString("TestResult", result);
            intent.putExtras(bundle);

        } else if (TestResult.ACCESSDENIED.equalsIgnoreCase(result)) {
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
//                    intent = new Intent(this, StorageResolutionsActivity.class);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Storage");
                    builder.setMessage("Please remove the duplicate files and Large Files to improve the performance of the device.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
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
                case TestName.INTERNALSTORAGE:

                 //   intent = new Intent(this, ResolutionsEducationalActivity.class);
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                    {
                        builder = new AlertDialog.Builder(this);
                        builder.setTitle("Storage");
                        builder.setMessage("Please remove the duplicate files and Large Files to improve the performance of the device.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                    else {
                       intent = new Intent(this, StorageResolutionsActivity.class);
                    }
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


    @Override
    public void onBackPressed() {
        if (!BaseUnusedActivity.isIsAssistedApp() && !Util.needToRemoveBackButton())
            goHome();
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.result_text);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_ssd_summery;
    }

    private void goHome() {
        //updateHistory();
        if (isTransactionUpdateRequired) {
            uploadResultToSerever(ExitSummary);
        } else {
            /*Intent intent = new Intent(ResultsSummeryActivity.this, EndingSessionActivity.class);
            startActivity(intent);*/
        finish();

            //MobiruFlutterActivity.mResult.success("Finished Health check");
        }
    }

    private List<DeviceInfoDataSet> batteryPerformanceResults() {
        List<DeviceInfoDataSet> batteryTestResults = new ArrayList<>();
        DeviceInfoDataSet batteryHealthSet = setBatteryHealthText();
        batteryTestResults.add(batteryHealthSet);
        SummeryDataSet<Object> sohSet = new SummeryDataSet<>(R.string.soh, -1, String.valueOf((int) BatteryPerformanceResult.getInstance().getBatterySOH() + "%"));


        SummeryDataSet<Object> designCapacitySet = new SummeryDataSet<>(R.string.design_capacity, -1, String.valueOf((int) BatteryPerformanceResult.getInstance().getBatteryDesignCapacity()));
        batteryTestResults.add(designCapacitySet);

        if (BatteryPerformanceResult.getInstance().getBatterySohByE() > BatteryPerformanceResult.getInstance().getBatterySohByT()) {
            SummeryDataSet<Object> actualCapacitySet = new SummeryDataSet<>(R.string.actual_capacity, -1, String.valueOf((int) BatteryPerformanceResult.getInstance().getBatteryCalculatedCapacity()));
            batteryTestResults.add(actualCapacitySet);
        }

        SummeryDataSet<Object> chargingLevelSet = new SummeryDataSet<>(R.string.charging_level, -1, String.valueOf(BatteryPerformanceResult.getInstance().getCurrentBatteryLevel(this) + "%"));
        DeviceInfoDataSet temperatureLevelSet = new SummeryDataSet<>(R.string.temperature, -1, String.valueOf(BatteryUtil.getBatteryTemperature(this)));


        batteryTestResults.add(sohSet);
        batteryTestResults.add(chargingLevelSet);
        batteryTestResults.add(temperatureLevelSet);
        return batteryTestResults;
    }

    private DeviceInfoDataSet setBatteryHealthText() {
        String batteryHealthValue = null;
        if (BatteryTestResult.healthVeryGood.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth())) {
            batteryHealthValue = getString(R.string.vgood);
        } else if (BatteryTestResult.healthGood.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth())) {
            batteryHealthValue = getString(R.string.good);
        } else if (BatteryTestResult.healthBad.equalsIgnoreCase(BatteryPerformanceResult.getInstance().getBatteryHealth())) {
            batteryHealthValue = getString(R.string.bad);
        } else
            batteryHealthValue = getString(R.string.unknown);
        return new DeviceInfoDataSet(R.string.battery_health_result, -1, batteryHealthValue);
    }

    private DeviceInfoDataSet setBatteryHealthText2(String batteryHealthFromApi) {
        String batteryHealthValue = null;
        if ("BATTERY HEALTH GOOD".equalsIgnoreCase(batteryHealthFromApi)) {
            batteryHealthValue = getString(R.string.vgood);
        } else if ("BATTERY HEALTH UNKNOWN".equalsIgnoreCase(batteryHealthFromApi)) {
            batteryHealthValue = getString(R.string.unknown);
        } else {
            batteryHealthValue = getString(R.string.bad);
        }
        return new DeviceInfoDataSet(R.string.battery_health_result, -1, batteryHealthValue);
    }

    private List<DeviceInfoDataSet> getBatteryResults() {
        DLog.d(TAG, "getBatteryResults...");
        String batteryHealthFromApi = BatteryUtil.getBatteryHealthByAndroidAPI(OruApplication.getAppContext());
        QuickBatteryTestInfo quickBatteryData = PervacioTest.getInstance().getQuickBatteryTestInfo();
        List<DeviceInfoDataSet> batteryTestResults = new ArrayList<>();


        DeviceInfoDataSet batterHealthSet;
        DeviceInfoDataSet sohSet = null;
        DeviceInfoDataSet actualCapacitySet = null;

        if (quickBatteryData == null) {
            batterHealthSet = setBatteryHealthText2(batteryHealthFromApi);
        } else {
            DLog.d(TAG, "Battery Health% " + quickBatteryData.getBatteryHealth() + "  :  "
                    + (int) quickBatteryData.getBatterySOH() + "%");
            if ("UNSUPPORTED".equalsIgnoreCase(quickBatteryData.getBatteryHealth()) ||
                    getString(R.string.unsupported).equalsIgnoreCase(quickBatteryData.getBatteryHealth())) {
                batterHealthSet = setBatteryHealthText2(batteryHealthFromApi);
            } else {
                batterHealthSet = new DeviceInfoDataSet(R.string.battery_health_result, -1, quickBatteryData.getBatteryHealth());
                actualCapacitySet = new DeviceInfoDataSet(R.string.actual_capacity, -1, String.valueOf((int) BatteryPerformanceResult.getInstance().getBatteryCalculatedCapacity()));
                if (!quickBatteryData.isSOHFromCondition()) {
                    int qSoh = (int) (quickBatteryData.getBatterySOH() > 100 ? 100 : quickBatteryData.getBatterySOH()); //100 CAP for SOH
                    sohSet = new DeviceInfoDataSet(R.string.soh, -1, String.valueOf(qSoh + "%"));
                }
            }
        }

        batteryTestResults.add(batterHealthSet);
        batteryTestResults.add(new DeviceInfoDataSet(R.string.design_capacity, -1, String.valueOf((int) BatteryUtil.getBatteryCapacity(this))));
        /*if (actualCapacitySet != null) {
            batteryTestResults.add(actualCapacitySet);
        }*/

        /*if (sohSet != null) {
            batteryTestResults.add(sohSet);
        }
        batteryTestResults.add(new DeviceInfoDataSet(R.string.charging_level, -1, String.valueOf(BatteryUtil.getCurrentBatteryLevel(this) + "%")));
        batteryTestResults.add(new DeviceInfoDataSet(R.string.temperature, -1, String.valueOf(BatteryUtil.getBatteryTemperature(this))));*/

        return batteryTestResults;
    }

    private void setBatteryResultLog(boolean hasBatteryPerformance) {
        DLog.d(TAG, "  ");
        DLog.d(TAG, "================= All Battery Test Results =================");
        DLog.d(TAG, "  ");

        DLog.d(TAG, "::::::::::::::Battery Api Test Result::::::::::::::");
        DLog.d(TAG, "Test Result: " + BatteryUtil.getBatteryHealthByAndroidAPI(this));
        DLog.d(TAG, "Result Info: \n" + BatteryUtil.getBatteryStatFromApi(this).toString());
        DLog.d(TAG, "::::::::::::::BatteryApiTest Result End::::::::::::::");
        DLog.d(TAG, "  ");

        DLog.d(TAG, "::::::::::::::Quick Battery Test Result::::::::::::::");
        BatteryDiagConfig batteryDiagConfig = new BatteryDiagConfig.BatteryDiagConfigBuilder(true).build();
        ActivityResultInfo activityResultInfo = new QuickTestComputeEngine(
                batteryDiagConfig, BatteryUtil.getBatteryCapacity(context)).computeQuickTestSoh();
        DLog.d(TAG, "Test Result: " + activityResultInfo.getTestResult().name());
        DLog.d(TAG, "Quick Test Possible: " + BatteryTest.getInstance().isQuickTestPossible(this));
        DLog.d(TAG, "Result Info: \n" + activityResultInfo.toString());
        DLog.d(TAG, "::::::::::::::QuickBatteryTest Result End::::::::::::::");
        DLog.d(TAG, "  ");

        if (hasBatteryPerformance) {
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
        DLog.d(TAG, "  ");

        DLog.d(TAG, "================= All Battery Test Results =================");
        DLog.d(TAG, "  ");
    }


    private List<DeviceInfoDataSet> updateDeviceInfo() {
        DeviceInfo mDeviceInfo = DeviceInfo.getInstance(this);
        List<DeviceInfoDataSet> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(new SummeryDataSet<>(R.string.device_info_make, -1, mDeviceInfo.capitalizeFirstLetter(mDeviceInfo.get_make())));
        deviceInfoList.add(new SummeryDataSet<>(R.string.device_info_model, -1, globalConfig.getDeviceModelName()));

        String imei = mDeviceInfo.get_imei();
        if (imei == null || imei.isEmpty())
            imei = getResources().getString(R.string.not_available);
//        deviceInfoList.add(new SummeryDataSet<>(R.string.device_info_imei_mac, -1, imei));
        deviceInfoList.add(new SummeryDataSet<>(R.string.firmware_version, -1, mDeviceInfo.getFirmwareVersion()));
        deviceInfoList.add(new SummeryDataSet<>(R.string.os_version_summary, -1, "Android " + mDeviceInfo.get_version()));
//        //TextUtils.isEmpty(imeiBlackListStatus)
//        String imeiStatus[] = globalConfig.getImeiBlackListStatus(this);
//        if (imeiStatus != null && imeiStatus.length > 0) {
//            if (imeiStatus.length == 1) {
//                deviceInfoList.add(new SummeryDataSet<>(getString(R.string.device_info_imei_block_status, ""),
//                        -1, getString(R.string.not_available)));
//            } else {
//                for (int i = 0; i < imeiStatus.length; i++) {
//                    deviceInfoList.add(new SummeryDataSet<>(getString(R.string.device_info_imei_block_status, String.valueOf(i + 1)),
//                            -1, getString(R.string.not_available)));
//                }
//            }
//        } else {
//            deviceInfoList.add(new SummeryDataSet<>(getString(R.string.device_info_imei_block_status, ""), -1, getString(R.string.not_available)));
//        }
        //deviceInfoList.add(new SummeryDataSet<>(R.string.device_info_imei_block_status, -1, globalConfig.getFormattedImeiBlackListStatus(this)));


        //Ran Values
        if (mShowRanScreen) {
            deviceInfoList.add(new SummeryDataSet<>(R.string.ran_number, -1, Util.ranNumber));
        }

        String format = BaseUtils.DateUtil.DateFormats.dd_MM_yyyy_hh_mm_Slash;
        if (DateFormat.is24HourFormat(this))
            format = BaseUtils.DateUtil.DateFormats.dd_MM_yyyy_HH_mm_Slash;

        String timestamp = BaseUtils.DateUtil.format(mGlobalConfig.getSessionStartTime(), format);
        deviceInfoList.add(new SummeryDataSet<>(R.string.start_time, -1, timestamp));

        String sessionId = String.valueOf(mGlobalConfig.getSessionId());
        if (TextUtils.isEmpty(sessionId)) {
            sessionId = "NA";
        }
        DeviceInfoDataSet sessionIdSet = new SummeryDataSet<>(R.string.session_id, -1, sessionId);
        deviceInfoList.add(new SummeryDataSet<>(R.string.session_id, -1, sessionId));
        return deviceInfoList;
    }


    private List<DeviceInfoDataSet> updateFivePointCheckResults() {
        if (PervacioTest.getInstance().getFivePointCheckResultMap().size() > 0 && Util.isFivePointCheckRequired()) {
            List<DeviceInfoDataSet> deviceInfoList = new ArrayList<>();
            if (!PervacioTest.getInstance().getFivePointCheckResultMap().containsValue(true)) {
                deviceInfoList.add(new SummeryDataSet<>(R.string.none_selected, -1, null));
            } else {
                for (String fivePointCheckName : globalConfig.getFivePointCheckList().keySet()) {
                    if (PervacioTest.getInstance().getFivePointCheckResultMap().get(fivePointCheckName)) {
                        deviceInfoList.add(new SummeryDataSet<>(fivePointCheckName, -1, getString(R.string.str_yes)));
                    }
                }
            }
            return deviceInfoList;
        }
        return null;
    }

    private List<DeviceInfoDataSet> updatePhysicalDamageCheckResults() {
        List<DeviceInfoDataSet> deviceInfoList = new ArrayList<>();
        if (PervacioTest.getInstance().getPhysicalDamageResultMap().size() > 0) {
            for (String physicalDamageCheckName : PervacioTest.getInstance().getPhysicalDamageResultMap().keySet()) {
                deviceInfoList.add(new SummeryDataSet<>(physicalDamageCheckName, -1, getString(PervacioTest.getInstance().getPhysicalDamageResultMap().get(physicalDamageCheckName) ? R.string.str_yes : R.string.str_no)));
            }
        }
        return deviceInfoList;
    }


    private void getSummaryImage() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ResultsSummeryActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setTitle(getResources().getString(R.string.saving_summary_image));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        disableButtons();
        SummaryType summaryType = new SummaryType();
        summaryType.setSessionId(GlobalConfig.getInstance().getSessionId());
        summaryType.setLocale(getResources().getString(R.string.locale));
        Call<TransactionResponse> summaryImageCall = ODDNetworkModule.getInstance().getDiagServerApiInterface().getSummaryImage(summaryType);
        summaryImageCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                enableButtons();
                TransactionResponse imageResponse = response.body();
                if (imageResponse != null && !TextUtils.isEmpty(imageResponse.getData())) {
                    boolean saveStatus = AppUtils.saveSummaryFileToStorageWireless(getApplicationContext(), imageResponse.getData());
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if (saveStatus) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.saved_summary_image), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_save_denied), Toast.LENGTH_SHORT).show();
                    }

                    DLog.d(TAG, "data: " + imageResponse.getData());
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
                enableButtons();
                DLog.e(TAG, "Exception while getObjectFromData" + t.getMessage());
                if (saveImageRetryCount < 2) {
                    saveImageRetryCount = saveImageRetryCount + 1;
                    saveImageHandler.sendEmptyMessageDelayed(0, 10000);
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


    private List<DeviceInfoDataSet> updateBatteryCheckResults() {
        String batteryFinalResult = PervacioTest.getInstance().getBatteryFinalResult();
        List<DeviceInfoDataSet> deviceInfoList = new ArrayList<>();
        int title = -1;
        int drawable = -1;
        switch (batteryFinalResult) {
            case "Replacement Not Needed":
                title = R.string.replace_not_needed;
                drawable = R.drawable.ic_passed;
                break;
            case "Consider Replacing Battery":
                title = R.string.consider_replace;
                drawable = R.drawable.results_warning;
                break;

            case "Replace Battery":
                title = R.string.replace_battery;
                drawable = R.drawable.ic_failed;
                break;
            default:

        }
        if (title > 0) {
            deviceInfoList.add(new DeviceInfoDataSet(title, drawable, null));
        }
        for (String name : PervacioTest.getInstance().getBatteryCheckResultMap().keySet()) {
            deviceInfoList.add(new DeviceInfoDataSet(name, -1, PervacioTest.getInstance().getBatteryCheckResultMap().get(name)));
        }
        return deviceInfoList;
    }


    private BroadcastReceiver mTestResultsUploadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mResultUploadProgress.setVisibility(View.GONE);
            //done_tv.setEnabled(true);
            enableButtons();
            LocalBroadcastManager.getInstance(ResultsSummeryActivity.this).unregisterReceiver(mTestResultsUploadedReceiver);
            int result = intent.getIntExtra("result", 0);
            DLog.d(TAG, "response : " + result);
            if (!Util.needToRemoveSendSummaryButton()) {
                if (result == 1) {
                    setDetailView();
                    send_summary.setEnabled(true);
                    isResultUploadSuccess = true;
                    if (requestFrom == MailSummary) {
                        DLog.d(TAG,"Email Summary");
//                        Intent emailSummary = new Intent(ResultsSummeryActivity.this, EmailSummaryChildUnusedActivity.class);
//                        startActivity(emailSummary);
                    } else if (requestFrom == ImageSummary) {
                        getSummaryImage();
                    } else if (requestFrom == TRADE_IN_ACCEPTED) {
                        DLog.d(TAG,"Trade in ");
                    //    RestrictionCheckUnusedActivity.startActivity(ResultsSummeryActivity.this, DeviceInfo.getInstance(ResultsSummeryActivity.this).get_imei());
                    } else if (requestFrom == ExitSummary) {
                        /*Intent endingSession;
                        if (Util.showSessionIdEndScreen()) {
                            endingSession = new Intent(ResultsSummeryActivity.this, SessionIdEndScreen.class);
                        } else {
                            endingSession = new Intent(ResultsSummeryActivity.this, EndingSessionActivity.class);
                        }*/
                        //startActivity(endingSession);
                        DLog.d("ExitSummary","1");
                        finish();
                    }
                    requestFrom = -1;
                    isTransactionUpdateRequired = false;
                } else {
                    showErrorDialog(requestFrom, RESULT_UPLOAD_REQ, ERROR_TO_CONNECT_SEREVER);
                }
            }
        }
    };

    public void showAlertDialog(String testName, String additionalInfo) {
        List<AdditionalInfoTest> additionalInfoTestList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(additionalInfo, ",");
        while (st.hasMoreTokens()) {
            String messagePair = st.nextToken();
            DLog.d(TAG, "hasMoreTokens messagePair " + messagePair + " TestName " + testName);
            if (messagePair.contains(":")) {
                int indexOfAssignment = messagePair.indexOf(":");
                String key = messagePair.substring(0, indexOfAssignment);
                String value = messagePair.substring(indexOfAssignment + 1);
                DLog.d(TAG, "hasMoreTokens key " + key + " value " + value);
                AdditionalInfoTest additionalInfoTest = new AdditionalInfoTest();
                additionalInfoTest.setAditionalInfoTestKey(key);
                additionalInfoTest.setAditionalInfoTestValue(value);
                additionalInfoTestList.add(additionalInfoTest);
            }
        }
        //TestAdditionalInfoDialogBaseUnusedActivity.startActivity(ResultsSummeryActivity.this, additionalInfoTestList, testName);
    }

}
