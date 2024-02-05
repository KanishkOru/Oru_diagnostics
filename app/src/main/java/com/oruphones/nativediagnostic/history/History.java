package com.oruphones.nativediagnostic.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.models.history.HistoryInfo;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;



import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rohit kr. maurya on 03-10-2017.
 * put all the api in this class, which is related to history .
 */

public class History {
    private static String TAG = History.class.getSimpleName();
    private static History history;
    final Context context = OruApplication.getAppContext();
    private static final String HISTORY = "History";
    private static final String OFFLINE_HISTORY = "Offline_History";
    SharedPreferences sharedPreferences;

    private History() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static History getInstance() {
        if (history == null)
            history = new History();
        return history;
    }

    public static void clearInstance() {
        history = null;
    }

    public ArrayList<HistoryInfo> getHistoryList() {
        String json = sharedPreferences.getString(HISTORY, null);
        ArrayList<HistoryInfo> historyList = new ArrayList<HistoryInfo>();
        if (json != null)
            historyList = (ArrayList) PervacioTest.getInstance().getObjectFromData(json, new TypeToken<ArrayList<HistoryInfo>>() {
            }.getType()); //return the ArrayList, which will contains the List of HistoryInfo object
        return historyList;
    }

    public void saveHistory(HistoryInfo historyInfo) {
        String historyData = sharedPreferences.getString(HISTORY, ""); //if we haven't initialise this key then it will return empty, then create arrayList
        ArrayList historyList;
        if ("".equalsIgnoreCase(historyData)) {
            historyList = new ArrayList();
        } else {
            historyList = (ArrayList) PervacioTest.getInstance().getObjectFromData(historyData, new TypeToken<ArrayList<HistoryInfo>>() {
            }.getType());
        }
        historyList.add(0, historyInfo);

        String json = PDConstants.gson.toJson(historyList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HISTORY, json);
        editor.commit();
    }

    public HashMap<String, String> getOfflineHistoryList() {
        String json = sharedPreferences.getString(OFFLINE_HISTORY, null);
        HashMap<String, String> offlineHistoryList = new HashMap<String, String>();
        if (json != null)
            offlineHistoryList = (HashMap) PervacioTest.getInstance().getObjectFromData(json, new TypeToken<HashMap<String, String>>() {
            }.getType()); //return the ArrayList, which will contains the List of HistoryInfo object
        return offlineHistoryList;
    }

    public void saveOfflineHistory(String offlineSessionID, String offlineHistory) {
        String offlineHistoryData = sharedPreferences.getString(OFFLINE_HISTORY, ""); //if we haven't initialise this key then it will return empty, then create arrayList
        HashMap offlineHistoryMap;
        if ("".equalsIgnoreCase(offlineHistoryData)) {
            offlineHistoryMap = new HashMap();
        } else {
            offlineHistoryMap = (HashMap) PervacioTest.getInstance().getObjectFromData(offlineHistoryData, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
        if (offlineHistoryMap.size() < 99) {
            offlineHistoryMap.put(offlineSessionID, offlineHistory);
            saveOfflineRecord(offlineHistoryMap);
        }
    }

    public void saveOfflineRecord(HashMap offlineHistoryMap) {
        String json = PDConstants.gson.toJson(offlineHistoryMap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OFFLINE_HISTORY, json);
        editor.commit();
    }

    public void updateHistory(PervacioTest pervacioTest) {
        String timestamp =   BaseUtils.DateUtil.getCurrent();
        try {
            HistoryInfo historyInfo = new HistoryInfo(timestamp);
            historyInfo.setCatagoryName(PervacioTest.getInstance().getSelectedCategory());
            ArrayList<TestInfo> testresults = null;
            if (pervacioTest.getAutoTestResult() != null && pervacioTest.getAutoTestResult().size() > 0)
            {
                historyInfo.setAutoTestResult(pervacioTest.getAutoTestResult());
                testresults = new ArrayList<TestInfo> (pervacioTest.getAutoTestResult().values());
            }
            if (pervacioTest.getManualTestResult() != null && pervacioTest.getManualTestResult().size() > 0)
            {
                historyInfo.setManualTestResult(pervacioTest.getManualTestResult());
                if (testresults == null) {
                    testresults = new ArrayList<TestInfo>(pervacioTest.getManualTestResult().values());
                } else {
                    testresults.addAll(pervacioTest.getManualTestResult().values());
                }
            }
            if (pervacioTest.getBatteryCheckResultMap() != null && pervacioTest.getBatteryCheckResultMap().size() > 0) {
                historyInfo.setBatteryCheckResultHistory(pervacioTest.getBatteryCheckResultMap());
                historyInfo.setBatteryCheckResult(pervacioTest.getBatteryFinalResult());
            }
            historyInfo.updateTestPassFailCount(testresults);
            saveHistory(historyInfo);
        } catch (Exception e) {
            DLog.e(TAG, "exception on updating history " + e.toString());
        }
    }
}
