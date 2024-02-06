package com.oruphones.nativediagnostic.logging;


import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DLogInstance {

    private static String TAG = DLogInstance.class.getSimpleName();
    private static String LOG_FILE_NAME = BuildConfig.FLAVOR_flav+ "_log.txt";
    static boolean mLoggingOn = true;



    private OutputStream mDataLogStream;
    private static DLogInstance sDLogInstance;

    public static synchronized DLogInstance getInstance() {
        if (sDLogInstance == null) {
            sDLogInstance = new DLogInstance();
        }
        return sDLogInstance;
    }

    public static String getLogFilePath(){
        File externalStorage = OruApplication.getAppContext().getApplicationContext().getFilesDir();
        return externalStorage.toString()+File.separator + LOG_FILE_NAME;
    }

    private DLogInstance() {
        try {

            String logFilePath = getLogFilePath();

            if (mLoggingOn) {
                File logFile = new File(logFilePath);


                if (!logFile.exists()) {
                    DLog.d(TAG, "Created new file");
                    logFile.createNewFile();
                }
                try {
                    //Append to existing file
                    mDataLogStream = new FileOutputStream(logFile, true);
                    mDataLogStream.write("===========================================================================================================".getBytes());
                    mDataLogStream.write(BaseUtils.getCurrentDateAndTime().getBytes());
                    mDataLogStream.write("===========================================================================================================".getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, "Exception : ", ex);
        }
    }

    public synchronized void log(String aString) {
        try {
            byte[] logBytes = (aString + "\r\n").getBytes();
            if (mDataLogStream != null) {
                mDataLogStream.write(logBytes);
                mDataLogStream.flush();
            }
        } catch (Exception ex) {
            DLog.e(TAG, "Exception : ", ex);
        }

    }


}
///