package com.oruphones.nativediagnostic.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.BuildConfig;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.PervacioApplication;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

public class CommonUtil implements TestResult {
    private static String TAG = CommonUtil.class.getSimpleName();

    public static GIFMovieView getNewGIFMovieView(Context context, String name) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GIFMovieView gifMovieView = new GIFMovieView(context, stream);
        gifMovieView.setBackgroundColor(Color.parseColor("#000000"));
        gifMovieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return gifMovieView;
    }


    /*---------MAC --*/
    public static String getMACAddress(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = manager.getConnectionInfo();

        if ("02:00:00:00:00:00".equals(wifiInf.getMacAddress())) {
            String ret = null;
            try {
                ret = getAdressMacByInterface();
                if (ret != null) {
                    return ret;
                } else {
                    ret = getAddressMacByFile(manager);
                    return ret;
                }
            } catch (IOException e) {
                DLog.e(TAG, "IOException in getMACAddress : " + e.getMessage());
            } catch (Exception e) {
                DLog.e(TAG, "Exception in getMACAddress : " + e.getMessage());
            }
        } else {
            return wifiInf.getMacAddress();
        }
        return "";
    }

    public static String getMACAddressInIMEIFormat(Context context) {
        String mac = getMACAddress(context);
        if (!TextUtils.isEmpty(mac)) {
            return mac.replaceAll(":", "");
        }
        return null;
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            DLog.e(TAG, "MAC Address : Exception in getAdressMacByInterface : ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File("/sys/class/net/wlan0/address");
        FileInputStream fin = new FileInputStream(fl);
        ret = convertStreamToString(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /* MAC - END --*/

    public static Context getAppContextFrom(Context context) {
        return context.getApplicationContext() == null ? context : context.getApplicationContext();
    }

    public static ComponentName getTopActivity(Context context) {
        context = getAppContextFrom(context);
        if (org.pervacio.onediaglib.utils.AppUtils.VersionUtils.hasMarshmallow()) {
            String packageName = _getTopActivityPackageNameM(context);
            ComponentName componentNameM = new ComponentName(packageName, Activity.class.getName());
            return componentNameM;
        } else if (org.pervacio.onediaglib.utils.AppUtils.VersionUtils.hasLollipop()) {
            String packageName = getTopActivityPackageName(context);
            ComponentName componentName = new ComponentName(packageName, Activity.class.getName());
            return componentName;
        } else {
            return getTopActivityCompat(context);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getTopActivityPackageName(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                currentTimeMillis - 60 * 1000, currentTimeMillis);
        if (usageStatsList == null || usageStatsList.isEmpty()) {
            return "";
        }
        TreeMap<Long, String> treeMap = new TreeMap<Long, String>();
        Iterator<UsageStats> iterator = usageStatsList.iterator();
        while (iterator.hasNext()) {
            UsageStats usageStats = iterator.next();
            treeMap.put(usageStats.getLastTimeStamp(), usageStats.getPackageName());
        }
        String packageName = treeMap.lastEntry().getValue();
        usageStatsList.clear();
        treeMap.clear();
        return packageName;
    }

    @SuppressWarnings("deprecation")
    private static ComponentName getTopActivityCompat(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName topActivity = runningTasks.get(0).topActivity;
            return topActivity;
        }
        return null;
    }

    public static String _getTopActivityPackageNameM(Context context) {
        UsageStatsManager _usageStatsManager = (UsageStatsManager) (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        UsageEvents usageEvents = _usageStatsManager.queryEvents(currentTimeMillis - 10 * 1000, currentTimeMillis);
        ArrayList<UsageEvents.Event> eventsList = new ArrayList<UsageEvents.Event>();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            eventsList.add(event);
        }
        if (eventsList.size() > 0) {
            for (int i = eventsList.size() - 1; i >= 0; i--) {
                UsageEvents.Event event = eventsList.get(i);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    return event.getPackageName();
                }
            }
        }
        return "";
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

    public static void showKeyboard(Activity activity, @NonNull View requestingView) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
       /* inputMethodManager.toggleSoftInputFromWindow(
                requestingView.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);*/
        requestingView.requestFocus();

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public static class DialogUtil {

        public static AlertDialog.Builder getAlert(Context context) {
            return new AlertDialog.Builder(context, ThemeUtil.getColorsFromAttrs(context, R.attr.c_alertDialogTheme));
        }

        public static AlertDialog showAlertWithList(Context context, View customTitle, String[] list, DialogInterface.OnClickListener singleChoice, String positiveButtonText, DialogInterface.OnClickListener positiveListener) {
            final AlertDialog.Builder builder2 = getAlert(context)
                    .setCustomTitle(customTitle)
                    .setCancelable(false)
                    .setSingleChoiceItems(list, -1, singleChoice);
            builder2.setPositiveButton(positiveButtonText, positiveListener);
            return builder2.create();
        }

        public static AlertDialog showAlert(Context context, String title, String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener) {
            AlertDialog dialog = getAlert(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, positiveListener).show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        public static AlertDialog getAlert(Context context, String title, String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener, String negativeButtonText, DialogInterface.OnClickListener negativeListener) {
            AlertDialog.Builder builder = getAlert(context)
                    .setTitle(title)
                    .setMessage(message);

            if (positiveListener != null)
                builder.setPositiveButton(positiveButtonText, positiveListener);

            if (negativeListener != null)
                builder.setNegativeButton(negativeButtonText, negativeListener);

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

        public static void twoButtonDialog(Activity activity, String title, String message, @NonNull String[] buttonText, final View.OnClickListener firstButton, final View.OnClickListener secondButton) {
            final Dialog dialog = new Dialog(activity);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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


    public static class PermissionUtil {

        public static boolean shouldAskPermission() {
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        }

        private static boolean shouldAskPermission(Context context, String permission) {
            if (shouldAskPermission()) {
                int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
                if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            return false;
        }

        public static void checkPermission(Context context, String permission, PermissionAskListener listener) {
            /*
             * If permission is not granted
             * */
            if (shouldAskPermission(context, permission)) {
                /*
                 * If permission denied previously
                 * */
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    listener.onPermissionPreviouslyDenied();
                } else {
                    /*
                     * Permission denied or first time requested
                     * */
                    if (PreferenceUtil.isFirstTimeAskingPermission(permission)) {
                        PreferenceUtil.firstTimeAskingPermission(permission, false);
                        listener.onPermissionAsk();
                    } else {
                        /*
                         * Handle the feature without permission or ask user to manually allow permission
                         * */
                        listener.onPermissionDisabled();
                    }
                }
            } else {
                listener.onPermissionGranted();
            }
        }


        /*
         * Callback on various cases on checking permission
         *
         * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
         *     If permission is already granted, onPermissionGranted() would be called.
         *
         * 2.  Above M, if the permission is being asked first time onPermissionAsk() would be called.
         *
         * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
         *     would be called.
         *
         * 4.  Above M, if the permission is disabled by device policy or the user checked "Never ask again"
         *     check box on previous request permission, onPermissionDisabled() would be called.
         * */
        public interface PermissionAskListener {
            void onPermissionAsk();

            void onPermissionPreviouslyDenied();

            void onPermissionDisabled();

            void onPermissionGranted();
        }


        public static String isPermissionGrantedForMarshmallow(Context activity, String[] listOfPermissions) {
            if (!shouldAskPermission())
                return null;

            for (String permission : listOfPermissions) {
                String allPermissionsGranted = getPermissionStatus((activity instanceof Activity) ? (Activity) activity : null, permission);
                if (!TextUtils.isEmpty(allPermissionsGranted))
                    return allPermissionsGranted;
            }
            return null;
        }


        private static final String SSD = "SSD";

        public static String isPermissionGrantedForMarshmallow(Context activity, boolean isGermany) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                return null;

            String[] listOfPermissions = null;
            switch (BuildConfig.FLAVOR_flav) {
                case "ssd":
                case "d2d":
                    listOfPermissions = permissionListForSSD(isGermany);
                    break;
                default:
                    listOfPermissions = getListOfPermissions();

            }
            for (String permission : listOfPermissions) {
                String allPermissionsGranted = getPermissionStatus((activity instanceof Activity) ? (Activity) activity : null, permission);
                if (!TextUtils.isEmpty(allPermissionsGranted))
                    return allPermissionsGranted;
            }
            return null;
        }

        private static List<String> commonPermissions() {
            List<String> permissionsList = new ArrayList<String>();
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            permissionsList.add(Manifest.permission.CAMERA);
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
//            Because its deprecated in API LEVEL 33
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S_V2)
//            {
            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
//            }

            permissionsList.add(Manifest.permission.CALL_PHONE);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                permissionsList.add(Manifest.permission.READ_PHONE_NUMBERS);
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                permissionsList.add(Manifest.permission.BLUETOOTH_SCAN);
                permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
//            permissionsList.add(Manifest.permission.BLUETOOTH_ADMIN);
//            permissionsList.add(Manifest.permission.BLUETOOTH);
//            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
//            permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);

            return permissionsList;

        }

        public static String[] permissionListForSSD(boolean isGermany) {
            List<String> permissionsList = commonPermissions();

            if (isGermany) {
                permissionsList.add(Manifest.permission.GET_ACCOUNTS);
            }

            String[] permissionLIst = new String[permissionsList.size()];
            permissionsList.toArray(permissionLIst);
            return permissionLIst;
        }

        public static String[] getListOfPermissions() {
            List<String> listOfPermissions = commonPermissions();

            listOfPermissions.add(Manifest.permission.GET_ACCOUNTS);
            listOfPermissions.add(Manifest.permission.READ_CONTACTS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                listOfPermissions.add(Manifest.permission.BODY_SENSORS);
            }

            String[] permissions = new String[listOfPermissions.size()];
            listOfPermissions.toArray(permissions);
            return permissions;
        }


        public static boolean hasRuntimePermission(String permission) {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || TextUtils.isEmpty(getPermissionStatus(null, permission));
        }

        private static String getPermissionStatus(Activity activity, String androidPermissionName) {
            if (ContextCompat.checkSelfPermission(OruApplication.getAppContext(), androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
                return androidPermissionName;
            }
            return null;
        }

        public static boolean shouldShowRequestPermissionRationale(Activity activity, String androidPermissionName) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName);
        }


        public static void openWirelessSetting(Context activity) {
            Intent intent1 = null;
            try {
                if ("SGH-M919".equalsIgnoreCase(Build.MODEL)) {
                    intent1 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent1 = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                }
                activity.startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void showWriteSettingRequest(final Activity activity, final int requestCode) {
            String title = activity.getString(R.string.permission);
            String message = "";
            if (requestCode == PreferenceUtil.USAGE_STATS) {
                message = activity.getString(R.string.enable_usage_stats_permission);
            } else {
                message = activity.getString(R.string.enable_permissions);
            }
            DialogUtil.showAlert(activity, title, message, activity.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (requestCode == PreferenceUtil.WRITE_SETTINGS) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivityForResult(intent, PreferenceUtil.WRITE_SETTINGS);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        activity.startActivityForResult(intent, PreferenceUtil.USAGE_STATS);
                    }
                }
            }).show();
        }


        public static boolean isForManualIMEI() {
            for (String item : PervacioApplication.manualDevice) {
                if (item.toLowerCase().equalsIgnoreCase(Build.MODEL.toLowerCase().trim())) {
                    return true;
                }
            }
            return isAndroidGoEdition(OruApplication.getAppContext());
        }

        public static void launchSettingActivityIntent(Activity activity) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, PreferenceUtil.PERMISSION_SETTINGS);
        }
    }


    /* Test Result Maps */
    public static String getMappedTestResult(int resultStatus) {
        switch (resultStatus) {
            case 2:
                return FAIL;
            case 22:
                return OPTIMIZED;
            case 4:
                return CANBEIMPROVED;
            case 1:
                return PASS;
            case 16:
                return SKIPPED;
            case 17:
                return NONE;
            case 23:
                return ACCESSDENIED;

            default:
                return "UNKNOWN";
        }
    }

    public static String getResultCodeToResultText(int resultCode) {
        String result;
        switch (resultCode) {
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PASS:
                result = TestResult.PASS;
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL:
                result = TestResult.FAIL;
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_FEATURE_NOT_EQUIPPED:
                result = TestResult.NOTEQUIPPED;
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_TIME_OUT:
                result = TestResult.TIMEOUT;
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_ERROR_UNKNOWN:
                result = "UNKNOWNERROR";
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_TRIPPLE_TOUCH_PERFORMED:
                result = "TRIPPLETOUCHPERFORMED";
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED:
                result = TestResult.ACCESSDENIED;
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_SETTING_VALUE_CHANGED:
                result = "VALUECHANGED";
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_TESTFINISHED:
                result = "USERINPUT";
                return result;
            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_CANBEIMPROVED:
                result = TestResult.CANBEIMPROVED;
                return result;
            default:
                result = TestResult.NOTEQUIPPED;
                return result;
        }

    }

    private static HashMap<String, String> testResultsMap = new HashMap<String, String>();

    static {
        Context context = OruApplication.getAppContext();
        testResultsMap.put(TestResult.PASS, context.getString(R.string.testresult_pass));
        testResultsMap.put(TestResult.FAIL, context.getString(R.string.testresult_fail));
        testResultsMap.put(TestResult.SKIPPED, context.getString(R.string.testresult_skip));
        testResultsMap.put(TestResult.CANBEIMPROVED, context.getString(R.string.testresult_canbeimproved));
        testResultsMap.put(TestResult.OPTIMIZED, context.getString(R.string.testresult_pass));
        testResultsMap.put(TestResult.NOTEQUIPPED, context.getString(R.string.testresult_notequipped));
        testResultsMap.put(TestResult.ACCESSDENIED, context.getString(R.string.testresult_accessdenied));
        testResultsMap.put(TestResult.TIMEOUT, context.getString(R.string.testresult_timeout));
        testResultsMap.put(TestResult.USERINPUT, context.getString(R.string.testresult_userinput));
    }

    public static String getMappedTestResult(String resultStatus) {
        return testResultsMap.get(resultStatus);
    }

    /* NETWORK STATE*/

    // check internet connection
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isAirplaneModeOn(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public static boolean isValidNetworkState(final Context context) {
        if (isAirplaneModeOn(context)) {
            DialogUtil.showAlert(context, context.getString(R.string.alert), context.getString(R.string.airplane_message), context.getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PermissionUtil.openWirelessSetting(context);
                }
            }).show();
            return false;
        } else if (!isOnline(context)) {
            DialogUtil.getAlert(context, context.getString(R.string.alert), context.getString(R.string.network_msz), context.getString(R.string.btn_retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isValidNetworkState(context);
                }
            }, context.getString(R.string.btn_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    exitFromApp(context);
                }
            }).show();
            return false;
        }
        return true;
    }

    public static void exitFromApp(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).finish();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Activity) context).finishAndRemoveTask();
            } else {
                ((Activity) context).finishAffinity();
            }
        }
        System.exit(0);
    }

    public static void bringAppTaskToForeground(Activity currentActivity) {
        try {
            int taskId = currentActivity.getTaskId();
            ActivityManager manager = (ActivityManager) currentActivity.getSystemService(Context.ACTIVITY_SERVICE);
            manager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
            DLog.i(TAG, "bringAppTaskToForeground :  Bringing app to foreground.");
        } catch (Exception e) {
            // There is no real threat of a crash here, but adding a catch just in case.
            DLog.i(TAG, "bringAppTaskToForeground : Exception" + e);
        }

    }

    public static boolean checkIfAppInForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isOurAppInFg = true;
        String ourPkgName = context.getPackageName();
        String fgPkgName;

        for (int i = 0; i < list.size(); i++) {
            String pkgName = list.get(i).topActivity.getPackageName();
            if (ourPkgName.equals(pkgName)) {
                if (i > 0) {
                    isOurAppInFg = false;
                    break;
                }
            } else {
                if (i == 0) {
                    DLog.d("AppInForeground", "Task#" + list.get(i).id + " topActivity:"
                            + list.get(i).topActivity + " pkg:" + pkgName);
                }
            }
        }
        ComponentName componentName = CommonUtil.getTopActivity(context);
        fgPkgName = componentName.getPackageName();
        if (fgPkgName.length() > 0 && !ourPkgName.equals(fgPkgName)) {
            isOurAppInFg = false;
        }
        return isOurAppInFg;
    }

    //CommonUtil.zipFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator+"WRD_LOGS.gz");

    /**
     * @param filePath This should be the absolute path of file
     * @return File
     */
    public static File gZipSingleFile(File filePath) {
        try {
            File file = new File(filePath.getAbsoluteFile() + ".gz");
            FileInputStream fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
            return file;
        } catch (FileNotFoundException ex) {
            System.err.format("The file %s does not exist", filePath);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
        return null;
    }

    public static boolean isAndroidGoEdition(Context context) {
        final String GMAIL_GO = "com.google.android.gm.lite";
        final String YOUTUBE_GO = "com.google.android.apps.youtube.mango";
        final String GOOGLE_GO = "com.google.android.apps.searchlite";
        final String ASSISTANT_GO = "com.google.android.apps.assistant";

        boolean isGmailGoPreInstalled = isPreInstalledApp(context, GMAIL_GO);
        boolean isYoutubeGoPreInstalled = isPreInstalledApp(context, YOUTUBE_GO);
        boolean isGoogleGoPreInstalled = isPreInstalledApp(context, GOOGLE_GO);
        boolean isAssistantGoPreInstalled = isPreInstalledApp(context, ASSISTANT_GO);

        if (isGoogleGoPreInstalled | isAssistantGoPreInstalled) {
            return true;
        }
        if (isGmailGoPreInstalled && isYoutubeGoPreInstalled) {
            return true;
        }

        return false;
    }

    private static boolean isPreInstalledApp(Context context, String packageName) {
        try {
            PackageManager pacMan = context.getPackageManager();
            PackageInfo packageInfo = pacMan.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                //Check if comes with the image OS
                int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
                return (packageInfo.applicationInfo.flags & mask) != 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            //The app isn't installed
        }
        return false;
    }

    public static boolean isNotificationPolicyAccessPermissionGranted(Context context) {
        boolean ifGranted = true;
        if (isDNDCheckRequired(context)) {
            if (Build.VERSION.SDK_INT >= 23) {
                @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService("notification");
                ifGranted = notificationManager.isNotificationPolicyAccessGranted();
            } else {
                ifGranted = true;
            }
        }
        return ifGranted;
    }

    private static boolean checkDNDTurnedOn(Context context) throws Settings.SettingNotFoundException {
        boolean dndOn = false;
        int zenModeValue = Settings.Global.getInt(context.getContentResolver(), "zen_mode");
        DLog.e(TAG, "checkDNDTurnedOn DnD : num " + zenModeValue);
        switch (zenModeValue) {

            case 0:
                DLog.e(TAG, "DnD : OFF");
                dndOn = false;
                break;
            case 1:
                DLog.e(TAG, "DnD : ON - Priority Only");
                dndOn = true;
                break;
            case 2:
                DLog.e(TAG, "DnD : ON - Total Silence");
                dndOn = true;
                break;
            case 3:
                DLog.e(TAG, "DnD : ON - Alarms Only");
                dndOn = true;
                break;
            default:
                DLog.e(TAG, "DND value is other than expected value");
        }
        return dndOn;
    }

    protected static boolean isDNDCheckRequired(Context context) {
        DLog.e(TAG, "isDNDCheckRequired :: ");
        if (Build.VERSION.SDK_INT < 23)
            return false;
        if ("LGE".equalsIgnoreCase(Build.MANUFACTURER) && Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
            return false;
        try {
            return checkDNDTurnedOn(context);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            DLog.e(TAG, "isDNDCheckRequired DND settings not found");
            return false;
        }
    }

}
