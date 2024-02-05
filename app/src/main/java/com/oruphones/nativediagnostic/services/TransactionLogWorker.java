package com.oruphones.nativediagnostic.services;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.google.gson.reflect.TypeToken;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.LogTransactionResp;
import com.oruphones.nativediagnostic.models.PDDiagLogging;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.webservices.APIClient;
import com.oruphones.nativediagnostic.webservices.ODDApiInterface;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Pervacio on 30/10/17.
 */

public class TransactionLogWorker extends Worker {
    private static String TAG = TransactionLogWorker.class.getSimpleName();
    private  static final String EXTRA_KEY = "ext_key";
    private  static final String EXTRA_SERVER_URL = "ext_server_url";
    private  static final String EXTRA_DATA = "ext_data";

    private String serverUrl, key;
    private PDDiagLogging pdDiaglogging;


    public static void scheduleTheFileUpload() {
        DLog.d(TAG, "Start Uploading transactions ");
        WorkManager.getInstance().cancelAllWorkByTag(TAG);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data  = prepareData();

      if(data!=null){
          final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(TransactionLogWorker.class)
                  .setConstraints(constraints)
                  .setInputData(data)
                  .addTag(TAG)
                  .build();
          WorkManager.getInstance().enqueue(simpleRequest);
          DLog.d(TAG,"Start Uploading : Enqueue the request");
       }

    }

    private static Data prepareData() {
        PervacioTest pervacioTest = PervacioTest.getInstance();
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        if (!TextUtils.isEmpty(globalConfig.getStoreID()) && globalConfig.getSessionId() > 0) {
            PDDiagLogging pdDiaglogging = TransactionLogService.prepareBaseSessionData(pervacioTest.getSessionStatus());
            Data data = new Data.Builder()
                    .putString(EXTRA_KEY, globalConfig.getServerKey())
                    .putString(EXTRA_SERVER_URL, globalConfig.getServerUrl())
                    .putString(EXTRA_DATA, APIClient.getGson().toJson(pdDiaglogging)).build();
            return data;
        } else {
            DLog.d(TAG, "prepareRequest : Fail" + globalConfig.getStoreID() + " SessionId " + globalConfig.getSessionId());
        }
        return null;
    }

    public TransactionLogWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Data data = workerParams.getInputData();

        serverUrl = data.getString(EXTRA_SERVER_URL);
        key = data.getString(EXTRA_KEY);
        String pdData =  data.getString(EXTRA_DATA);
        if(!TextUtils.isEmpty(pdData)){
           pdDiaglogging = APIClient.getGson().fromJson(pdData, new TypeToken<PDDiagLogging>(){}.getType());
        }

    }

    @NonNull
    @Override
    public Result doWork() {
        ODDApiInterface apiInterface = ODDNetworkModule.getNewApiInterface(serverUrl,key);
        if(apiInterface!=null && pdDiaglogging!=null){
            Call<LogTransactionResp> transaction = apiInterface.logTransaction(pdDiaglogging);
            try {
                Response<LogTransactionResp> serverConfigResponse =  transaction.execute();
                DLog.d(TAG, "doWork() "+serverConfigResponse.toString());
            } catch (IOException e) {
                e.printStackTrace();
                DLog.e(TAG, "doWork() "+e.getMessage());

            }
        }

        return Result.success();
    }

}
