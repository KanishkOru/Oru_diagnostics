package com.oruphones.nativediagnostic;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.oruphones.nativediagnostic.api.BuildConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ThemeUtil;
import com.oruphones.nativediagnostic.util.Util;


/**
 * Created by satya p on 22-Mar-18.
 */
public class WelcomeScreenActivity extends BaseActivity {

    private static String TAG = WelcomeScreenActivity.class.getSimpleName();
    private final int WRITE_SETTINGS = 1;
    private final int USAGE_STATS = 2;
    private TextView suggestion_tv, welcome_tv;
    private Button letsgetStartedBtn;
    private ServiceInterrupt serviceInt = null;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, WelcomeScreenActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setCustomer(getResources().getConfiguration().locale.getDisplayCountry());
        //PervacioTest.getInstance(true).initConfig();
        super.onCreate(savedInstanceState);
        letsgetStartedBtn = (Button) findViewById(R.id.letsgetstarted);
        suggestion_tv = (TextView) findViewById(R.id.suggestion_text);
        welcome_tv = (TextView) findViewById(R.id.welcome_tv);
        setFontToView(suggestion_tv, AILERON_LIGHT);
        setFontToView(welcome_tv, AILERON_LIGHT);
        TextView version = (TextView) findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);
        //airplaneMode = new BaseActivity.AirplaneOn();
        //registerReceiver(airplaneMode, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        serviceInt = new ServiceInterrupt();
        registerReceiver(serviceInt, new IntentFilter("co   m.sprint.network.interrupted"));
        initServiceStarted = false;
        permissionsStarted = false;
        selectedManualTestsResult.clear();
        manualStart = true;
//        if(!isTaskRoot()) {
//            finish();
//            return;
//        }


        letsgetStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHandler.removeMessages(0);
                checkNetworkState();
                /*List<String> returtnList = new ArrayList<String>();
                returtnList.add("testString");
                MobiruFlutterActivity.mResult.success(returtnList);
                finish();*/

            }
        });

        findViewById(R.id.copyLogs).setVisibility(View.GONE);
        findViewById(R.id.copyLogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RestrictionCheckActivity.startActivity(WelcomeScreenActivity.this,"");
            }
        });


        //calls autotests
        Intent intent = new Intent(WelcomeScreenActivity.this, ProdConfigActivity.class);

        startActivity(intent);
        finish();
    }

    AppUpdateManager appUpdateManager;

    private void inAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                Log.e("AVAILABLE_VERSION_CODE", appUpdateInfo.availableVersionCode() + "");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                WelcomeScreenActivity.this,
                                // Include a request code to later monitor this update request.
                                0);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                }
            }
        });

        appUpdateManager.registerListener(installStateUpdatedListener);

    }

    //lambda operation used for below listener
    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        } else
            Log.e("UPDATE", "Not downloaded yet");
    };


    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction("Restart", view ->
                appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    private void checkNetworkState() {

        if (isAirplaneModeOn(WelcomeScreenActivity.this)) {
            showNetworkDialogue(false, getResources().getString(R.string.alert), getResources().getString(R.string.airplane_message), 1, WelcomeScreenActivity.this);
        } else {
            if (!isOnline()) {
                showNetworkDialogue(hasOfflineData(), getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), 0, WelcomeScreenActivity.this);
            } else {
                Intent intent = new Intent(WelcomeScreenActivity.this, ProdConfigActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (PervacioTest.getInstance().isOfflineDiagnostics()) {
            DLog.d(TAG, "Application running offline..... ...");
            return;
        }
        if (Util.isAdvancedTestFlow()) {
            startHandler.sendEmptyMessageDelayed(0, 3000);
        }

    }

    public void showWriteSettingRequest(final int requestCode) {
        String title = getString(R.string.permission);
        String message = "";
        if (requestCode == USAGE_STATS) {
            message = getString(R.string.enable_usage_stats_permission);
        } else {
            message = getString(R.string.enable_permissions);
        }
        CommonUtil.DialogUtil.showAlert(this, title, message, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (requestCode == WRITE_SETTINGS) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, WRITE_SETTINGS);
                } else {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivityForResult(intent, USAGE_STATS);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                checkPermissions(permissions[i]);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("permissionsdenied", permissionDenied);
            editor.commit();

        }
    //    startApp();
        checkUsageStats();*/
    }

    private void grantSettingsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getApplicationContext())) {
            showWriteSettingRequest(WRITE_SETTINGS);
        } else {
            checkUsageStats();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USAGE_STATS) {
            startApp();
        } else if (requestCode == WRITE_SETTINGS)
            checkUsageStats();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (networkDialgoue != null) {
            networkDialgoue.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (airplaneMode != null)
            unregisterReceiver(airplaneMode);
        if (serviceInt != null)
            unregisterReceiver(serviceInt);
    }

    Handler startHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startHandler.removeMessages(0);
            checkNetworkState();
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash_screen;
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return true;
    }

}
