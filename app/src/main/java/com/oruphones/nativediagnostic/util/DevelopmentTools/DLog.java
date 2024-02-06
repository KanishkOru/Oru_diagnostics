package com.oruphones.nativediagnostic.util.DevelopmentTools;

import android.util.Log;

import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.OruApplication;


import org.pervacio.onediaglib.APPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DLog{

    // Define a tag for your logs
    private static final String TAG = "OruLog";
    private static final String LOG_FILE_NAME = "Oru_app_logs.txt"; // Change this file name as needed
    public static final String CRASH_FILE_NAME = OruApplication.CRASH_FILE_NAME;
    public static String getLogFileName()
    {
        return LOG_FILE_NAME;
    }

    public static String getCrashLogFileName()
    {
        return CRASH_FILE_NAME;
    }

    public static void d(String message) {
        logToFile(TAG,"DEBUG",message);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getCallingMethodName() + ": " + message);
        }
    }
    public static void e(String message) {
        logToFile(TAG,"ERROR",message);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getCallingMethodName() + ": " + message);
        }
    }


    // Method to log a debug message with calling method information and a specific tag
    public static void d(String tag, String message) {
     logToFile(tag,"DEBUG",message);
        if (BuildConfig.DEBUG) {
            Log.d(tag, getCallingMethodName() + ": " + message);
        }
    }

    // Method to log an info message with calling method information and a specific tag
    public static void i(String tag, String message) {
     logToFile(tag,"INFO",message);
        if (BuildConfig.DEBUG) {
            Log.i(tag, getCallingMethodName() + ": " + message);
        }
    }

    // Method to log a warning message with calling method information and a specific tag
    public static void w(String tag, String message) {
       logToFile(tag,"WARNING",message);
        if (BuildConfig.DEBUG) {
            Log.w(tag, getCallingMethodName() + ": " + message);
        }
    }

    // Method to log an error message with calling method information and a specific tag
    public static void e(String tag, String message) {
        logToFile(tag,"ERROR",message);
        if (BuildConfig.DEBUG) {
            Log.e(tag, getCallingMethodName() + ": " + message);
        }
    }
    public static void e(String tag, String message, Exception e) {
        logToFile(tag,"ERROR",message);
        if (BuildConfig.DEBUG) {
            Log.e(tag, getCallingMethodName() + ": " + message +" " +e.getMessage());
        }
    }

    public static void e(String tag, Exception e) {
        logToFile(tag,"ERROR",e.getMessage());
        if (BuildConfig.DEBUG) {
            Log.e(tag, getCallingMethodName()  +e.getMessage());
        }
    }

    // Method to log to file
    public static File logToFile(String TAG, String level, String message) {
        File  logFile = new File(APPI.getAppContext().getExternalFilesDir(null), LOG_FILE_NAME);
        try {
            String logMessage = String.format(Locale.US, "Timestamp:  %s Tag:   %s Level: %s   %s",
                    getCurrentTimeStamp(), TAG, level, message) + "\n";
            FileOutputStream outputStream = new FileOutputStream(logFile, true);
            outputStream.write(logMessage.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to log file OruLogs: " + e.getMessage());
        }
        return logFile;
    }


    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }

    // Helper method to get the calling method's information
    private static String getCallingMethodName() {
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        if (stackTraceElements.length >= 4) { // Ensure there's enough stack trace depth
//            StackTraceElement caller = stackTraceElements[3];
//            return caller.getClassName() + "." + caller.getMethodName() + "()";
//        }
        return "";
    }


    public static String getCrashLogs(boolean Delete)
    {
        String logs = retrieveLogsLocally(CRASH_FILE_NAME);
        if (Delete)deleteLogs(CRASH_FILE_NAME);
        return logs;
    }

    public static String retrieveLogsLocally(String filename) {
        StringBuilder logContent = new StringBuilder();
        try {
            // Get the path to the log file
            File logFile = new File(OruApplication.getAppContext().getExternalFilesDir(null), filename);
            // Check if the file exists
            if (logFile.exists()) {
                // Read the content of the file
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    logContent.append(line).append("\n");
                }
                reader.close();
            } else {
                DLog.e(TAG, "Log file not found LOGS...");
            }
        } catch (IOException e) {
            DLog.e(TAG, "Error retrieving logs locally");
        }
        DLog.d(TAG,"ORULOGS"+logContent.toString());
        return logContent.toString();
    }

    public static void deleteLogs(String filename)
    {
        try {
            File logFile = new File(OruApplication.getAppContext().getExternalFilesDir(null),filename);
          if (logFile.exists())
          {
              if (logFile.delete()) {
                  DLog.e(TAG+"DeleteLogsLocally", "Log file deleted successfully "+filename);
              } else {
                  DLog.e(TAG+"DeleteLogsLocally", "Error deleting log file "+ filename);
              }
          }else {
              DLog.e(TAG+"DeleteLogsLocally", "File not Exits");
          }
        }
        catch (Exception e)
        {
            DLog.e(TAG+"DeleteLogsLocally", "Error deleting log "+e.getMessage());
        }
    }
}
