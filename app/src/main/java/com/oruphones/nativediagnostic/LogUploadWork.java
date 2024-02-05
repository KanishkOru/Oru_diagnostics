package com.oruphones.nativediagnostic.logging;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.webservices.BaseNetworkModule;

import java.io.File;

public class LogUploadWork extends Worker {
    private static String TAG = LogUploadWork.class.getSimpleName();
    private static String STR_SESSION_ID = "STR_SESSION_ID";
    private static String STR_SESSION_KEY = "STR_SESSION_KEY";
    private static String STR_LOGGING_URL = "STR_LOGGING_URL";
    private static final String TAG_WORKER = "simple_db_work";
    private String sessionId;
    private BaseNetworkModule mBaseNetworkModule;


    public static void scheduleTheFileUpload(String url, String key, String sessionId) {
        DLog.d(TAG, "Start Uploading the logs with ");
        WorkManager.getInstance().cancelAllWorkByTag(TAG_WORKER);
        Data data =  new Data.Builder()
                .putString(STR_SESSION_ID,sessionId)
                .putString(STR_SESSION_KEY,key)
                .putString(STR_LOGGING_URL,url).build();


        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(LogUploadWork.class)
                .setInputData(data)
                .setConstraints(constraints)
                .addTag(TAG_WORKER)
                .build();
        WorkManager.getInstance().enqueue(simpleRequest);
        DLog.d(TAG,"Start Uploading : Enqueue the request");
    }

    public LogUploadWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Data data = workerParams.getInputData();
        mBaseNetworkModule = new BaseNetworkModule();
        sessionId = data.getString(STR_SESSION_ID);
        mBaseNetworkModule.init(data.getString(STR_LOGGING_URL),data.getString(STR_SESSION_KEY));
    }

    @NonNull
    @Override
    public Result doWork() {

        sendDebugLogsToServer();
        return Result.success();
    }

    private void sendDebugLogsToServer() {
        DLog.d(TAG,  "Start uploading the Logs for sessionid"+ sessionId);
        DLogInstance.getInstance().log(TAG+ "Start uploading the Logs for sessionid"+ sessionId);
        File file = new File(DLogInstance.getLogFilePath());
        DLogInstance.getInstance().log(TAG+ " File status  " +file.exists());
        mBaseNetworkModule.uploadLogs(file,sessionId);
    }

}
