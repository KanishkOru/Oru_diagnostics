package com.oruphones.nativediagnostic.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.Storage.StorageResolutionFileInfoPOJO;
import com.oruphones.nativediagnostic.api.BuildConfig;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.internalstorage.ApplicationData;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Satyanarayana Chidurala on 11/20/2015.
 */
public class Util {

    private static String TAG = Util.class.getSimpleName();

    public static String CALL_TEST = "callTest";
    public static String AUIDO_TEST = "audioTest";
    public static String BUTTON_TEST = "buttonTest";
    public static String CAMERA_TEST = "cameraTest";
    public static String SENSOR_TEST = "sensorTest";
    public static String DISPLAY_TEST = "displayTest";
    public static String TOUCH_SCREEN_TEST = "touchScreenTest";
    public static String VIBRATION_TEST = "vibrationTest";
    public static String HARDWARE_TEST = "hardwareTest";
    public static String ranNumber = null;
    //public static String CALL_NUMBER = "+919494998942";
    public static String CALL_NUMBER = "198";//"1234567890";
    //public static String CALL_NUMBER = "1234567890";
    public static final long MB_IN_BYTES = 1024 * 1024 * 1024;


    public static int StringToInteger(String data) {
        try {
            if (data != null)
                return Integer.parseInt(data);
            else
                return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static double StringToDouble(String data) {
        try {
            if (data != null)
                return Double.parseDouble(data);
            else
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
            DLog.e(TAG, "exception:" + e.toString());
            return 0;
        }
    }

    public static Double LongToDouble(Long data) {
        try {
            if (data != 0)
                return data.doubleValue();
            else
                return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }


    public static Double BtoMB(double value) {
        return (value / 1024) / 1024;
    }

    public static Double BtoKB(double value) {
        return (value / 1024);
    }

    public static Double KBtoMB(double value) {
        return (value / 1024);
    }

    public static Double KBtoGB(double value) {
        return (value / 1024) / 1024;
    }

    /*public static double getTotalStorageinMB(HashMap<String, StorageResolutionFileInfoPOJO> map){
        double  total_files_size =0;
        ArrayList<StorageResolutionFileInfoPOJO> storageFilesList=new ArrayList<>(map.values());
        for(StorageResolutionFileInfoPOJO storageFile : storageFilesList){
            total_files_size=total_files_size+StringToDouble(storageFile.getFileSize());
        }
        total_files_size=(BtoMB(total_files_size));
        return total_files_size;
    }*/

    /*public static double getTotalStorageinKB(HashMap<String, StorageResolutionFileInfoPOJO> map){
        double  total_files_size =0;
        ArrayList<StorageResolutionFileInfoPOJO> storageFilesList=new ArrayList<>(map.values());
        for(StorageResolutionFileInfoPOJO storageFile : storageFilesList){
            total_files_size=total_files_size+StringToDouble(storageFile.getFileSize());
        }
        total_files_size=(BtoKB(total_files_size));
        return total_files_size;
    }*/

    public static double getTotalStorage(HashMap<String, StorageResolutionFileInfoPOJO> map) {
        double total_files_size = 0;
        ArrayList<StorageResolutionFileInfoPOJO> storageFilesList = new ArrayList<>(map.values());
        for (StorageResolutionFileInfoPOJO storageFile : storageFilesList) {
            total_files_size = total_files_size + StringToDouble(storageFile.getFileSize());
        }
        return total_files_size;
    }


    public static double getTotalAppInMB(HashMap<String, ApplicationData> map) {
        double total_files_size = 0;
        ArrayList<ApplicationData> appsData = new ArrayList<>(map.values());
        for (ApplicationData apps : appsData) {
            long appData = apps.getAppSize().getCodeSize() + apps.getAppSize().getDataSize() + apps.getAppSize().getCacheSize();
            total_files_size += appData;
        }
        total_files_size = (BtoMB(total_files_size));
        return total_files_size;
    }

    public static double getTotalAppInKB(HashMap<String, ApplicationData> map) {
        double total_files_size = 0;
        ArrayList<ApplicationData> appsData = new ArrayList<>(map.values());
        for (ApplicationData apps : appsData) {
            if (apps != null && apps.getAppSize() != null) {
                long appData = apps.getAppSize().getCodeSize() + apps.getAppSize().getDataSize() + apps.getAppSize().getCacheSize();
                total_files_size += appData;
            }
        }
        total_files_size = (BtoKB(total_files_size));
        return total_files_size;
    }

    public static double getAppInKB(ApplicationData applicationData) {
        double total_files_size = 0;

        total_files_size = applicationData.getAppSize().getCodeSize() + applicationData.getAppSize().getDataSize() + applicationData.getAppSize().getCacheSize();
        DLog.i(TAG, applicationData.getAppSize().getCodeSize() + "  " + applicationData.getAppSize().getDataSize() + "  " + applicationData.getAppSize().getCacheSize());
        total_files_size = (BtoKB(total_files_size));
        return total_files_size;
    }

    public static String setDecimalPointToTwo(double data) {
        String conversion = "0";
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        if (data != 0) {
            conversion = new DecimalFormat("###.##", formatSymbols).format(data);
        }
        return conversion;
    }

    public static String setDecimalPointToThree(double data) {
        String conversion = "0";
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        if (data != 0) {
            conversion = new DecimalFormat("###.###", formatSymbols).format(data);
        }
        return conversion;
    }

    public static String setDecimalPointToOne(double data) {
        String conversion = "0";
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        if (data != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("###.#", formatSymbols);
            //decimalFormat.setRoundingMode(RoundingMode.DOWN);
            conversion = decimalFormat.format(data);
        }
        return conversion;
    }

    /**
     * This method to get the readable format of size.
     * Here user have to parse data size in Bytes.
     * size will be returned in KB, MB, GB, TB
     * <p>
     * size  - long - size in Bytes.
     *
     * @param size
     */
    public static String readableFileSize(long size) {
        String readableSize = "";
        if (size <= 0) return readableSize;
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        readableSize = new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        return readableSize;
    }

    public static void writeTofile(String fileName, String data) {
        try {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
            DLog.d(TAG, "writeTofile: " + filePath);
            FileWriter writer = new FileWriter(filePath);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImeiInPrefs(String imei) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(OruApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("IMEI", imei);
        editor.commit();
    }

    public static String getImeiFromPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(OruApplication.getAppContext());
        return sharedPreferences.getString("IMEI", "");
    }

    public static boolean retrivedIMEIForAndroid10() {
        String imei = getImeiFromPrefs();
        return imei != null && !imei.isEmpty();
    }

    public static boolean needToIncludeRunallInCategories() {
        return BuildConfig.FLAVOR.equalsIgnoreCase("enBplus") || "telephonica".equalsIgnoreCase(BuildConfig.FLAVOR_flav) ||
                BuildConfig.FLAVOR_flav.equalsIgnoreCase("bell") || "claro".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isFivePointCheckRequired() {
        return "bplus".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isBatteryQuestionaireRequired() {
        return "bplus".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean addStoreIdDynamically() {
        return "telephonica".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isDeviceInfoScreenRequired() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean needToSkipResolutions() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean needToSkipCategories() {
        return GlobalConfig.getInstance().getCategoryList().size() == 1;
    }

    public static boolean needToRemoveSendSummaryButton() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean needToRemoveBackButton() {
        return ProductFlowUtil.isTradein();
    }

    public static boolean isAdvancedTestFlow() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isTredInFlow() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean showTermsAndConditions() {
        return false;//"bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav) || "ssd".equalsIgnoreCase(BuildConfig.FLAVOR_flav);
    }

    public static boolean isOfflineDiagSupported() {
        return false;
    }

    public static boolean gpsLoginRequired() {
        return "telephonica".equalsIgnoreCase(BuildConfig.FLAVOR_flav)
                || Constants.TELEFONICA.equalsIgnoreCase(GlobalConfig.getInstance().getCompanyName());
    }


    public static boolean sendSummaryEmail() {
        return GlobalConfig.getInstance().isEmailSummary();
    }

    public static boolean showSessionIdEndScreen() {
        return ProductFlowUtil.isCountryGermany();
    }

    public static boolean showCSATScreen() {
        return ProductFlowUtil.showCSATScreen();
    }


    public static boolean isTelofonicaLatam() {
        String[] telefonicaLatam = {"TelefonicaArgentina", "TelefonicaChile", "TelefonicaColombia", "TelefonicaIndia", "TelefonicaMexico", "TelefonicaPeru", "TelefonicaEcuador", "TelefonicaUruguay"};
        return Arrays.asList(telefonicaLatam).contains(GlobalConfig.getInstance().getCompanyName());
       /*
        return (GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaArgentina") ||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaChile") ||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaColombia") ||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaIndia") ||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaMexico") ||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaPeru")||
               GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaEcuador"));*/
    }

    public static boolean isCustomerClaroPeru() {
        return ("ClaroPeru".equalsIgnoreCase(GlobalConfig.getInstance().getCompanyName()));
    }

    public static boolean isCustomerEntel() {
        return ("EntelPeru".equalsIgnoreCase(GlobalConfig.getInstance().getCompanyName()));
    }


    public static boolean retriveImeiManuallyInAndroid10() {
        return !ProductFlowUtil.isCountryGermany();
    }

    public static boolean showExitInMenu() {
        return ProductFlowUtil.isCustomerTelefonicaColombia() || ProductFlowUtil.isCustomerTelefonicaEcuador();
    }

    public static boolean enableSendToRepair() {
        return ProductFlowUtil.isCustomerTelefonicaColombia() || ProductFlowUtil.isCustomerTelefonicaEcuador();
    }

    public static boolean showInfoAtMiddle() {
        return ProductFlowUtil.isCountryGermany();
    }

    public static boolean isGetAccountsPermissionRequired() {
        return !ProductFlowUtil.isCountryGermany();
    }

    public static boolean showEpilepsyPopUp() {
        return ProductFlowUtil.isCustomerTelefonicaO2UK();
    }

    public static boolean disableSkip() {
        return "bell".equalsIgnoreCase(BuildConfig.FLAVOR_flav)
                || ProductFlowUtil.isCustomerTelefonicaO2UK()
                || GlobalConfig.getInstance().isTradeIn()
                || GlobalConfig.getInstance().isVerification()
                || GlobalConfig.getInstance().isBuyerVerification();
    }

    public static boolean showConfirmPin() {
        return ProductFlowUtil.isCustomerTelefonicaO2UK();
    }

    /**/
    public static SpannableStringBuilder requiredText(String value) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(value);
        int start = builder.length();
        builder.append("*");
        int end = builder.length();


        builder.setSpan(new SuperscriptSpan(), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static void hideKeyboard(Activity activity, @NonNull View requestingView, boolean show) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (show) {
            requestingView.requestFocus();
            imm.showSoftInputFromInputMethod(requestingView.getWindowToken(), InputMethodManager.SHOW_FORCED);
        } else {
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it

            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    public static class DialogUtil {

        public static void twoButtonDialog(Activity activity, String title, String message, @NonNull String[] buttonText, final View.OnClickListener firstButton, final View.OnClickListener secondButton) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.cutom_alert_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            TextView BL_alert_head = (TextView) dialog
                    .findViewById(R.id.BL_alert_head);
            TextView BL_alert_text = (TextView) dialog
                    .findViewById(R.id.BL_alert_text);
            BL_alert_head.setText(title);
            BL_alert_text.setText(message);
            Button BL_alert_ok = (Button) dialog.findViewById(R.id.BL_alert_yes);
            Button BL_alert_no = (Button) dialog.findViewById(R.id.BL_alert_no);

            /*Buttons as per requirements*/
            String firstButtonText = buttonText[0];
            String SecondButtonText = null;
            if (buttonText.length > 1) {
                SecondButtonText = buttonText[1];
            }

            BL_alert_ok.setVisibility(View.GONE);
            BL_alert_no.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(firstButtonText)) {
                BL_alert_ok.setText(firstButtonText);
                BL_alert_ok.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(SecondButtonText)) {
                BL_alert_no.setText(SecondButtonText);
                BL_alert_no.setVisibility(View.VISIBLE);
            }

            BL_alert_ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    if (firstButton != null) {
                        firstButton.onClick(v);
                    }
                }
            });
            BL_alert_no.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    if (secondButton != null) {
                        secondButton.onClick(v);
                    }
                }
            });


            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

        }
    }

}
