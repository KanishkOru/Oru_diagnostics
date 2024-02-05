package com.oruphones.nativediagnostic.util;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.StringDef;

import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.monitor.MonitorServiceDocomo;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BaseUtils {

    private static String TAG = BaseUtils.class.getSimpleName();
    public static String LOG_FOLDER_PATH = "/data/local/tmp/pva/";

    static String folderLogPath = "pvc";
    private static String logFileName = "pvc_log.txt";
    public static final long MB_IN_BYTES = 1024 * 1024 * 1024;

    public static String getCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();
        String timeZone = tz.getDisplayName(false, TimeZone.SHORT).substring(0, 3);
        return dateFormat.format(date) + " " + timeZone;
    }


    private static void uninstallApp(Context context, String applicationId) throws ActivityNotFoundException {
        Uri packageUri;
        if (!TextUtils.isEmpty(applicationId)) {
            packageUri = Uri.parse("package:" + applicationId);
        } else {
            packageUri = Uri.parse("package:" + context.getPackageName());
        }

        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void triggerUninstall(Context context, boolean checkForKeyguard) {
        try {
            if (checkForKeyguard) {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager != null && !keyguardManager.isKeyguardLocked()) {
                    uninstallApp(context,null);
                }
            } else {
                uninstallApp(context,null);
            }
            MonitorServiceDocomo.stopService(context);
        } catch (Exception e) {
            DLog.e(TAG, "uninstallApp Exception : "+e);
        }
    }

    protected static void triggerKillApp(Activity context) {
        try {
                if (Build.VERSION.SDK_INT >= 21) {
                    context.finishAndRemoveTask();
                } else {
                    context.finishAffinity();
                }
            System.exit(0);
            DLog.d(TAG, "uninstallApp ");
        } catch (ActivityNotFoundException ex) {
            DLog.e(TAG, "Exception While removing app from " + ex.getMessage());

        }
    }
    /**
     * Uninstall app.
     *
     * @param context       the activity context of calling activity
     * @param finish        if true finish the calling activity
     * @param applicationId the application id of app which need to un-install
     */
    protected static void triggerUninstall(Activity context, boolean finish, String applicationId) {
        try {
            if (finish) {
                if (Build.VERSION.SDK_INT >= 21) {
                    context.finishAndRemoveTask();
                } else {
                    context.finish();
                }
            }
            uninstallApp(context,applicationId);
            DLog.d(TAG, "uninstallApp ");
        } catch (ActivityNotFoundException ex) {
            DLog.e(TAG, "Exception While removing app from " + ex.getMessage());

        }
    }


    public static boolean isNull(String str) {
        if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
            return true;
        } else {
            return false;
        }
    }

    /*FILES */
    public static boolean deleteFile(Context context, File file) {
        boolean isSuccess;
        if (file.isFile()) {
            isSuccess = file.delete();
            DLog.i(TAG, "Deleting file: " + file.getAbsolutePath() + "; is deleted = " + isSuccess);
            return isSuccess;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                deleteFile(context, f);
            }
        }
        isSuccess = file.delete();
        DLog.d(TAG, "Deleting file: " + file.getAbsolutePath() + "; is deleted = " + isSuccess);
        return isSuccess;
    }

    public static void copyFile(File from, File to) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(from);
            fileOutputStream = new FileOutputStream(to);
            FileChannel inChannel = fileInputStream.getChannel();
            FileChannel outChannel = fileOutputStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            DLog.e(TAG, "Exception while copying file from " + from.getPath() + " to " + to.getPath());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
    public static void createAndCopyFile(String from){
        File fileFrom = new File(from);
        File toFrom = new File(createLogFile());
        copyFile(fileFrom,toFrom);
    }
    private static String createLogFile() {
        File logStorageDir = new File(
                Environment.getExternalStorageDirectory(), folderLogPath);
        File alogFile = null;
        try {
            if (!logStorageDir.exists()) {
                logStorageDir.mkdirs();
            }
            alogFile = new File(logStorageDir.getPath() + File.separator
                    + logFileName);
            if (!alogFile.exists())
                alogFile.createNewFile();

            return alogFile.getAbsolutePath();
        } catch (Exception e) {
            DLog.e("Exception", "Exception while creating log file " + e);
        }
        return null;
    }

    public static class DateUtil{
        static Context sContext = PervacioApplication.getAppContext();

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({DateFormats.MM_dd_yyyy_HH_mm_Dot, DateFormats.MM_dd_yyyy_hh_mm_Dot, DateFormats.dd_MM_yyyy_HH_mm_Slash, DateFormats.dd_MM_yyyy_hh_mm_Slash,})
        public @interface DateFormats {
            String  MM_dd_yyyy_HH_mm_Dot = "MM.dd.yyyy HH:mm";
            String  MM_dd_yyyy_hh_mm_Dot = "MM.dd.yyyy hh:mm a";
            String  dd_MM_yyyy_HH_mm_Slash = "dd/MM/yyyy HH:mm";
            String  dd_MM_yyyy_hh_mm_Slash = "dd/MM/yyyy hh:mm a";
        }
        public static String getCurrent(@DateFormats String format) {
            return  format(Calendar.getInstance().getTime().getTime(),format);
        }
        public static String getCurrent() {
            return  format(Calendar.getInstance().getTime().getTime());
        }


        public static String format(long time,@DateFormats String format) {
            SimpleDateFormat simpleDateFormat =  new SimpleDateFormat(format, Locale.getDefault());
            return  simpleDateFormat.format(time);
        }

        public static String format(long time) {
            SimpleDateFormat simpleDateFormat =  new SimpleDateFormat();
            return  simpleDateFormat.format(time);
        }

    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ORUAPP_FIRST_RUN", Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("FIRST_RUN_KEY", true);

        if (isFirstRun) {
            // Update the flag to indicate that the app has been run before
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("FIRST_RUN_KEY", false);
            editor.apply();
        }

        return isFirstRun;
    }

    public static class PermissionsFlow{

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({Customers.TELEFONICA_O2UK, Customers.TELEFONICA_GERMANY})
        public @interface Customers {
            String  TELEFONICA_O2UK = "TelefonicaO2UK";
            String  TELEFONICA_GERMANY = "TelefonicaGermany";
        }
    }


}
