package com.oruphones.nativediagnostic.manualtests;

import static com.oruphones.nativediagnostic.models.tests.TestResult.PASS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.controller.accessory.AccessoryDialogBaseUnusedActivity;
import com.oruphones.nativediagnostic.home.HomeActivity;
import com.oruphones.nativediagnostic.models.AccessoryDataSet;
import com.oruphones.nativediagnostic.models.DiagConfiguration;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.result.ResultsSummeryActivity;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.ResultComparator;
import com.oruphones.nativediagnostic.util.Util;

import org.pervacio.onediaglib.diagtests.DiagTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pervacio on 29/08/2017.
 */

public class ManualTestsActivity extends BaseActivity {
    TextView mContinue, mSkip, select_text;
    RecyclerView manualTestRecyclerView;
    private static String TAG = ManualTestsActivity.class.getSimpleName();
    ManualTestNewAdapter manualTestAdapter;
    ArrayList<TestInfo> manualTestList = new ArrayList<>();
    public CheckBox checkAllTests;

    private void initView() {
        mContinue = (TextView) findViewById(R.id.start_manual_test);
        select_text = (TextView) findViewById(R.id.select_text);
        mSkip = (TextView) findViewById(R.id.cancel_tv);
        checkAllTests = (CheckBox) findViewById(R.id.checkall);
        manualTestRecyclerView = findViewById(R.id.manualTestRecyclerView);

        setFontToView(mContinue, ROBOTO_MEDIUM);
        setFontToView(mSkip, ROBOTO_MEDIUM);
        setFontToView(select_text, ROBOTO_LIGHT);


        mContinue.setText(getString(R.string.manual_tests_selection_btn_continue));
        mSkip.setText(getString(R.string.gotoresults));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        initView();

        checkAllTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preSelectedList(checkAllTests.isChecked(), null);
            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BaseActivity.selectedManualTests.clear();
                BaseActivity.selectedManualTests.addAll(manualTestAdapter.getSelectedTest());
                if (BaseActivity.selectedManualTests.size() == 0) {
                    Toast.makeText(ManualTestsActivity.this, R.string.manualtest_selection, Toast.LENGTH_SHORT).show();
                } else {
                    startNextManualTest();
                }
            }
        });

        // TODO: 25/3/21 Check this  
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualTestsActivity.this, ResultsSummeryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setUpAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentManualTest = null;
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.additional_tests);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_tests;
    }


    private void setUpAdapter() {
        manualTestAdapter = new ManualTestNewAdapter(this, manualTestList);
        manualTestRecyclerView.setAdapter(manualTestAdapter);
        manualTestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (BaseActivity.isAssistedApp) {
            (new DiagTimer(null)).setDisableTimer();
        }
        getList();
    }

    private void updateTestList(List<TestInfo> list) {
        manualTestList.clear();
        PervacioTest pervacioTest = PervacioTest.getInstance();
        for (TestInfo testInfo : list) {
            if (!(pervacioTest.getAutoTestResult().get(testInfo.getName()) != null &&
                    pervacioTest.getAutoTestResult().get(testInfo.getName()).getTestResult().equalsIgnoreCase(PASS))) {
                manualTestList.add(testInfo);
            }
        }
        //manualTestList.addAll(list);
        manualTestAdapter.notifyDataSetChanged();
    }

    private void fetchAndShowAccessoryPopup(List<TestInfo> testInfos) {
        List<AccessoryDataSet> dataSetList = ODDUtils.fetchAndShowAccessoryPopup(testInfos);
        if (!dataSetList.isEmpty()) {
            AccessoryDialogBaseUnusedActivity.startActivity(ManualTestsActivity.this, dataSetList);
        }
    }

    private void getList() {


        try {
            List<TestInfo> testList = globalConfig.cloneManualTestList(PervacioTest.getInstance().getSelectedCategory());
            if (testList == null) {
                handleOfflineMode();
            }


            Collections.sort(testList, new ResultComparator());
            if (!testList.isEmpty()) {
                updateTestList(testList);
                fetchAndShowAccessoryPopup(testList);
            }
        } catch (Exception e) {
            DLog.d(TAG, "Exception:" + e.getMessage());
        }
    }

    private void handleOfflineMode() {
        if (PervacioTest.getInstance().isOfflineDiagnostics()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String offlineData = sharedPreferences.getString("OfflineData", null);

            if (offlineData != null) {
                DiagConfiguration diagConfiguration = PervacioTest.getInstance().getObjectFromData(offlineData, new TypeToken<DiagConfiguration>() {
                }.getType());
                if (diagConfiguration != null) {
                    DLog.d(TAG, "Loading Offline Data........");
                    CommandServer.loadConfig(diagConfiguration, true, getApplicationContext());
                }
            }
        } else {
            Toast.makeText(ManualTestsActivity.this, "Tests list not fetched", Toast.LENGTH_SHORT).show();
        }
    }


    // Either preselected list or make all selected/deselected
    private void preSelectedList(Boolean checkAll, List<String> list) {
        if (manualTestList == null || manualTestRecyclerView == null)
            return;

        for (TestInfo testInfo : manualTestList) {
            if (list != null) { // If Pre-Selected list existed and wanted to show it
                testInfo.setChecked(list.contains(testInfo.getName()));
            } else if (checkAll != null) { // Check if Request for all selection or de select
                testInfo.setChecked(checkAll);
            }
        }
        manualTestAdapter.notifyDataSetChanged();
        updateSelectAllCheckBox();
    }

    public void updateSelectAllCheckBox() {
        checkAllTests.setChecked(manualTestAdapter.getSelectedTest().size() == manualTestList.size());
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            super.onBackPressed();
            pervacioTest.updateSession();
            updateHistory();
            Intent intent = new Intent(ManualTestsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


}
