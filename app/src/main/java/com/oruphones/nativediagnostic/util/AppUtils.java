package com.oruphones.nativediagnostic.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.widget.Toast;

import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.models.AbortReasons;
import com.oruphones.nativediagnostic.services.AudioRecordService;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Venkatesh Pendlikal on 19-11-2015.
 */
public class AppUtils extends BaseUtils{


    public static final boolean LOGGING_ENABLED = true;
    private static String TAG = AppUtils.class.getSimpleName();
    public static String TEMP_FOLDER_PATH = "/data/local/tmp/pva/";






    private AppUtils() {
    }

    public static void triggerUninstall(Activity context, boolean finish, String applicationId){
        triggerUninstall(context,  finish,  applicationId, AbortReasons.END_SESSION);
    }

    public static void triggerUninstall(Activity context, boolean finish, String applicationId, AbortReasons abortReasons){
        PervacioTest pervacioTest = PervacioTest.getInstance();
        pervacioTest.setAbortReasons(abortReasons);
        pervacioTest.updateSessionOnAppClose();
        BaseUtils.triggerUninstall(context,finish,applicationId);

    }

    public static void performCleanup(Context context) {
        try {
            AudioRecordService.deleteAudioRecordsDirectory(context);
            File file = new File(TEMP_FOLDER_PATH);
            if (file.exists()) {
                while (file.getPath().contains("pva")) {
                    File f = file.getParentFile();
                    deleteFile(context, file);
                    file = f;
                }
            }
        } catch (Exception e) {

            DLog.e(TAG, "Exception in performCleanup(): " + e.getMessage());
        }
    }



    public static int[] getSoundModeForFUJITSU(Context context) {
        int[] arr = new int[2];
        arr[0] = Settings.System.getInt(context.getContentResolver(), "manner_state", 0);
        arr[1] = Settings.System.getInt(context.getContentResolver(), "public_mode", 0);
        return arr;
    }

    public static void setSoundModeForFUJITSU(Context context, int mannerMode, int publicMode) {
        Settings.System.putInt(context.getContentResolver(), "manner_state", mannerMode);
        Settings.System.putInt(context.getContentResolver(), "public_mode", publicMode);
    }

    public static Context getAppContextFrom(Context context) {
        return context.getApplicationContext() == null ? context : context.getApplicationContext();
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = manager.isWifiEnabled();
        return wifiEnabled;
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static boolean isNFCEnabled(Context context) {
        boolean enabled = NfcAdapter.getDefaultAdapter(context).isEnabled();
        return enabled;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static boolean changeNFCState(Context context, boolean enable) {
        try {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            Method changeNFCStateMethod = nfcAdapter.getClass().getDeclaredMethod(enable ? "enable" : "disable");
            changeNFCStateMethod.setAccessible(true);
            changeNFCStateMethod.invoke(nfcAdapter);
            return true;
        } catch (Exception e) {
             DLog.e(TAG, "Exception in changeNFCState()." + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean changeGPSState(Context context, boolean enable) {
        try {
            Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER,
                    enable);
            return true;
        } catch (Exception e) {
             DLog.e(TAG, "Exception in changeGPSState()." + e.getMessage());
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothAdapter adapter = null;
        if (org.pervacio.onediaglib.utils.AppUtils.VersionUtils.hasJellybeanMR2()) {
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = manager.getAdapter();
        } else {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }
        return adapter;
    }

    public static Object getDeclaredFieldValue(String fieldName, Class<?> cls, Object receiver) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object object = field.get(receiver);
            return object;
        } catch (Exception e) {
        }
        return null;
    }



    public static String utf8(String in) {
        if (in == null)
            return "";
        in = in.replaceAll("&", "&#x26;");
        in = in.replaceAll("<", "&#x60;");
        in = in.replaceAll(">", "&#x62;");
        return (in);
    }

    public static void log(String message){
        if(LOGGING_ENABLED){
            DLog.d(TAG,message);
        }
    }

    /*TOAST*/
    public static void toast(String message){
        Toast.makeText(PervacioApplication.getAppContext(),message,Toast.LENGTH_LONG).show();
    }
    public static void toastDebug(String message){
        if(LOGGING_ENABLED){
            Toast.makeText(PervacioApplication.getAppContext(),message,Toast.LENGTH_LONG).show();
        }
    }

    public static boolean saveSummaryFileToStorageWireless(Context context, String fileData) {
        FileOutputStream fos = null;
        String statusString = "fail";
        boolean saveStatus = false;
        try {
            byte[] decodedData = Base64.decode(fileData, Base64.DEFAULT);
//            String outputPath = "" + Environment.getExternalStorageDirectory() + File.separator + BuildConfig.APPLICATION_ID;
            String outputPath = "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + BuildConfig.APPLICATION_ID;
            String outputFilePath = outputPath + "/" + "summaryImage"+System.currentTimeMillis()+".jpg";
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File externalFile = new File(outputFilePath);
            fos = new FileOutputStream(externalFile.getPath());
            fos.write(decodedData);
            refreshAndroidGallery(context, Uri.fromFile(externalFile));
            statusString = "success";
            saveStatus = true;
        } catch (Exception e) {
            DLog.e(TAG, "Exception in photoCallback", e);
            saveStatus = false;
        } finally {
            DLog.e("$$","&EventName=SaveSummaryFile="+ "&Status=" + statusString);
            if (fos != null) {
                try { fos.close(); } catch (IOException e) { DLog.e(TAG, "Exception in close", e); }
            }
            return saveStatus;
        }
    }

    /**
     * Refreshing the android gallery after the file is saved Or deleted.
     *
     * @param context
     * @param fileUri
     */
    public static void refreshAndroidGallery(Context context, Uri fileUri) {
        try {
            if (fileUri != null) {
                Context mContext = context.getApplicationContext();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(fileUri);
                    mContext.sendBroadcast(mediaScanIntent);
                } else {
                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                }
            }

        } catch (Exception e) {
            DLog.e(TAG, "Exception in Refreshing Android Gallery: " + e.getMessage());
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }


}
