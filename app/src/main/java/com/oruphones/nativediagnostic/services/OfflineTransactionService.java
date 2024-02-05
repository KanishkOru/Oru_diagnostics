package com.oruphones.nativediagnostic.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;
import com.oruphones.nativediagnostic.history.History;
import com.oruphones.nativediagnostic.models.LogTransactionResp;
import com.oruphones.nativediagnostic.models.PDDiagLogging;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Pervacio on 20/12/17.
 */

public class OfflineTransactionService extends IntentService {

    private static String TAG = OfflineTransactionService.class.getSimpleName();
    private String serviceURL = "/sprintservice/offlineSprintTransaction";
    private CountDownLatch offlineDataLatch;
    private HashMap<String, String> totalOfflineRecords;
    /**
     * A constructor is required, and must call the super OfflineTransactionService(String)
     * constructor with a name for the worker thread.
     */
    public OfflineTransactionService() {
        super("TransactionLogService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            new Thread() {
                public void run() {
                    sendOfflineData();
                }
            }.start();


        } catch (Exception ex) {
             DLog.e(TAG, "Some Exception in For loop2:" + ex.getMessage());
            //History.getInstance().saveOfflineRecord(offlineRecords);
        }
    }

    private void sendOfflineData() {
         DLog.d(TAG, "sendOfflineData...");
        totalOfflineRecords = History.getInstance().getOfflineHistoryList();
        if(totalOfflineRecords == null || totalOfflineRecords.size()==0) return;
         DLog.d(TAG, "offlineRecords size..."+totalOfflineRecords.size());
        try {
            Set<String> keySet = totalOfflineRecords.keySet();
            ArrayList<String> keyList = new ArrayList<String>();
            keyList.addAll(keySet);
            Collections.sort(keyList);
             DLog.d(TAG, "keys size: " + keyList.size());
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                DLog.d(TAG, "key: " + key);
                try {
                    String logData = totalOfflineRecords.get(key);
                     DLog.d(TAG, "Submitting Record: " + logData);
                    startServerCall(logData, key);
                } catch (Exception e) {
                    e.printStackTrace();
                     DLog.e(TAG, "Some Exception in For loop1:" + e.getMessage());
                }
                 DLog.d(TAG, "For End........");
            }
        } catch (Exception ex) {
             DLog.e(TAG, "Some Exception in For loop2: " + ex.getMessage());
        }
    }

    private void startServerCall(String data, String key) {

        PDDiagLogging logging = getObjectFromData(data, PDDiagLogging.class);

        Call<LogTransactionResp> call = ODDNetworkModule.getInstance().getDiagServerApiInterface().logTransaction(logging);
        try {
            Response<LogTransactionResp> response = call.execute();
            LogTransactionResp result = response.body();
            if (result != null) {
                if (result.getStatus().equalsIgnoreCase("PASS")) {
                    if (totalOfflineRecords != null && totalOfflineRecords.size() > 0) {
                        if (totalOfflineRecords.containsKey(key)) {
                            totalOfflineRecords.remove(key);
                        }
                    }
                    History.getInstance().saveOfflineRecord(totalOfflineRecords);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T getObjectFromData(String data, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(data, type);
        } catch (Exception e) {
            DLog.e(TAG, "Exception while getObjectFromData" + e.getMessage());
        }
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DLog.d(TAG, "Service destroyed");
    }
}
