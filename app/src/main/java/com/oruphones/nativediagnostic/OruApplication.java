package com.oruphones.nativediagnostic;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.google.firebase.FirebaseApp;
import com.google.firebase.ktx.Firebase;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.APPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;


public class OruApplication extends APPI {


    private static String TAG = OruApplication.class.getSimpleName();
    public static final String CRASH_FILE_NAME= "oru_crash_logs.txt";
    @Override
    public void onCreate() {

        FirebaseApp.initializeApp(this);
        Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exception) {
                DLog.e(TAG,"APP CRASHED");
                handleUncaughtException( thread,  exception);
                PervacioTest pervacioTest =PervacioTest.getInstance();
                pervacioTest.updateSessionOnAppClose();
            }
        } );
        super.onCreate();

    }
    public void handleUncaughtException(Thread thread, Throwable throwable) {
        DLog.e(TAG, "Unhandled exception in thread " + thread.getName());
        saveLogsLocally((throwable));
    }

    private void saveLogsLocally(Throwable log) {
        try {
            // Create a file to save logs
            File logFile = new File(getExternalFilesDir(null), CRASH_FILE_NAME);
            FileWriter writer = new FileWriter(logFile, true);
            writer.append("\n\n");
            writer.append("Timestamp: ").append(Calendar.getInstance().getTime().toString()).append("\n");
            writer.append("Device Details:\n");
            writer.append("Brand: ").append(Build.BRAND).append("\n");
            writer.append("Model: ").append(Build.MODEL).append("\n");
            writer.append("Device: ").append(Build.DEVICE).append("\n");
            writer.append("Product: ").append(Build.PRODUCT).append("\n");
            writer.append("OS Version: ").append(Build.VERSION.RELEASE).append("\n");
            writer.append("\n");
            writer.append("-----------------------SYSTEM CRASH LOGS-----------------------");
            writer.append((Log.getStackTraceString(log)));
            DLog.e(TAG, "logs saved successfully"+CRASH_FILE_NAME);
            writer.close();
        } catch (IOException e) {
            DLog.e(TAG, "Error saving logs locally");
        }
    }

}
